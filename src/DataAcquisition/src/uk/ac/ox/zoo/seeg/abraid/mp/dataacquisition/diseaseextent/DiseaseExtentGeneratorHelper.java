package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent;

import ch.lambdaj.group.Group;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.*;

import static ch.lambdaj.Lambda.*;

/**
 * A helper for the DiseaseExtentGenerator class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGeneratorHelper {
    private static final int SCALING_FACTOR = 50;

    // Input fields
    private DiseaseGroup diseaseGroup;
    private DiseaseExtentParameters parameters;
    private List<AdminUnitDiseaseExtentClass> currentDiseaseExtent;
    private List<? extends AdminUnitGlobalOrTropical> adminUnits;
    private List<DiseaseOccurrenceForDiseaseExtent> occurrences;
    private List<DiseaseExtentClass> diseaseExtentClasses;

    // Working fields
    private Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrencesByAdminUnit;
    private Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> classesByAdminUnit;
    private Map<Integer, Integer> numberOfOccurrencesByCountry;
    private Map<Integer, List<AdminUnitReview>> reviewsByAdminUnit;

    public DiseaseExtentGeneratorHelper(DiseaseGroup diseaseGroup, DiseaseExtentParameters parameters,
                                        List<AdminUnitDiseaseExtentClass> currentDiseaseExtent,
                                        List<? extends AdminUnitGlobalOrTropical> adminUnits,
                                        List<DiseaseOccurrenceForDiseaseExtent> occurrences,
                                        List<DiseaseExtentClass> diseaseExtentClasses) {
        this.diseaseGroup = diseaseGroup;
        this.parameters = parameters;
        this.currentDiseaseExtent = currentDiseaseExtent;
        this.adminUnits = adminUnits;
        this.occurrences = occurrences;
        this.diseaseExtentClasses = diseaseExtentClasses;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public List<AdminUnitDiseaseExtentClass> getCurrentDiseaseExtent() {
        return currentDiseaseExtent;
    }

    /**
     * Groups the disease occurrences by admin unit (global or tropical).
     */
    public void groupOccurrencesByAdminUnit() {
        // Group admin units by GAUL code
        Map<Integer, AdminUnitGlobalOrTropical> adminUnitMapByGaulCode
                = index(adminUnits, on(AdminUnitGlobalOrTropical.class).getGaulCode());

        // Create empty groups of occurrences by admin unit
        occurrencesByAdminUnit = new HashMap<>();
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            occurrencesByAdminUnit.put(adminUnit, new ArrayList<DiseaseOccurrenceForDiseaseExtent>());
        }

        // Add occurrences to the groups
        for (DiseaseOccurrenceForDiseaseExtent occurrence : occurrences) {
            AdminUnitGlobalOrTropical adminUnit = adminUnitMapByGaulCode.get(
                    occurrence.getAdminUnitGlobalOrTropicalGaulCode());
            // Should never be null, but just in case
            if (adminUnit != null) {
                occurrencesByAdminUnit.get(adminUnit).add(occurrence);
            }
        }
    }

    /**
     * Groups the occurrences by country (strictly, it groups the number of occurrences by country GAUL code).
     */
    public void groupOccurrencesByCountry() {
        // Group the occurrences by country GAUL code
        Group<DiseaseOccurrenceForDiseaseExtent> group = group(occurrences,
                by(on(DiseaseOccurrenceForDiseaseExtent.class).getCountryGaulCode()));

        // Convert the grouping to a map from GAUL code to number of occurrences
        numberOfOccurrencesByCountry = new HashMap<>();
        for (Group<DiseaseOccurrenceForDiseaseExtent> subgroup : group.subgroups()) {
            numberOfOccurrencesByCountry.put((Integer) subgroup.key(), subgroup.getSize());
        }
    }

    /**
     * Groups the expert reviews by admin unit (strictly, by admin unit GAUL code).
     * @param reviews The expert reviews for this disease group.
     */
    public void groupReviewsByAdminUnit(List<AdminUnitReview> reviews) {
        // Reviews with null expert weightings are not used when scoring the reviews for disease extent generation
        removeReviewsWithNullExpertWeightings(reviews);

        // Group the reviews by admin unit GAUL code
        Group<AdminUnitReview> group = group(reviews,
                by(on(AdminUnitReview.class).getAdminUnitGlobalOrTropicalGaulCode()));

        // Convert the grouping to a map from GAUL code to reviews
        reviewsByAdminUnit = new HashMap<>();
        for (Group<AdminUnitReview> subgroup : group.subgroups()) {
            reviewsByAdminUnit.put((Integer) subgroup.key(), subgroup.findAll());
        }
    }

    /**
     * Computes the disease extent classes for an initial disease extent.
     */
    public void computeInitialDiseaseExtentClasses() {
        computeDiseaseExtentClasses(new DiseaseExtentClassComputer() {
            @Override
            public DiseaseExtentClass compute(AdminUnitGlobalOrTropical adminUnit,
                                              List<DiseaseOccurrenceForDiseaseExtent> occurrencesForAdminUnit) {
                // Computes the initial disease extent class for one admin unit
                int occurrenceCount = occurrencesForAdminUnit.size();
                if (occurrenceCount == 0) {
                    return computeDiseaseExtentClassForCountry(adminUnit.getCountryGaulCode());
                } else {
                    return computeDiseaseExtentClassUsingOccurrenceCount(occurrenceCount, 1);
                }
            }
        });
    }

    /**
     * Computes the disease extent classes for updating an existing disease extent.
     */
    public void computeUpdatedDiseaseExtentClasses() {
        computeDiseaseExtentClasses(new DiseaseExtentClassComputer() {
            @Override
            public DiseaseExtentClass compute(AdminUnitGlobalOrTropical adminUnit,
                                              List<DiseaseOccurrenceForDiseaseExtent> occurrencesForAdminUnit) {
                // Computes the updated disease extent class for one admin unit
                List<AdminUnitReview> reviewsForAdminUnit = getReviewsByGaulCode(adminUnit.getGaulCode());
                if (occurrencesForAdminUnit.size() == 0 && reviewsForAdminUnit.size() == 0) {
                    return computeDiseaseExtentClassForCountry(adminUnit.getCountryGaulCode());
                } else {
                    return computeDiseaseExtentClassUsingOccurrencesAndReviews(occurrencesForAdminUnit,
                            reviewsForAdminUnit);
                }
            }
        });
    }

    /**
     * Forms the disease extent for saving to the database.
     * Updates existing rows or creates new rows as appropriate.
     * @return A list of AdminUnitDiseaseExtentClass rows for saving.
     */
    public List<AdminUnitDiseaseExtentClass> getDiseaseExtentToSave() {
        List<AdminUnitDiseaseExtentClass> adminUnitDiseaseExtentClasses = new ArrayList<>();

        for (Map.Entry<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrenceByAdminUnit :
                occurrencesByAdminUnit.entrySet()) {
            AdminUnitGlobalOrTropical adminUnit = occurrenceByAdminUnit.getKey();
            AdminUnitDiseaseExtentClass row = findAdminUnitDiseaseExtentClass(adminUnit);
            if (row == null) {
                row = createAdminUnitDiseaseExtentClass(adminUnit);
            }
            DiseaseExtentClass newClass = classesByAdminUnit.get(adminUnit);
            row.setHasChanged(!newClass.equals(row.getDiseaseExtentClass()));
            row.setDiseaseExtentClass(newClass);
            row.setOccurrenceCount(occurrenceByAdminUnit.getValue().size());
            adminUnitDiseaseExtentClasses.add(row);
        }

        return adminUnitDiseaseExtentClasses;
    }

    private List<AdminUnitReview> getReviewsByGaulCode(int adminUnitGaulCode) {
        List<AdminUnitReview> reviews = reviewsByAdminUnit.get(adminUnitGaulCode);
        return (reviews == null) ? new ArrayList<AdminUnitReview>() : reviews;
    }

    private void computeDiseaseExtentClasses(DiseaseExtentClassComputer computer) {
        // For each admin unit, convert its list of disease occurrences into a disease extent class
        classesByAdminUnit = new HashMap<>();
        for (Map.Entry<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrenceByAdminUnit :
                occurrencesByAdminUnit.entrySet()) {
            AdminUnitGlobalOrTropical adminUnit = occurrenceByAdminUnit.getKey();
            List<DiseaseOccurrenceForDiseaseExtent> occurrencesForAdminUnit = occurrenceByAdminUnit.getValue();
            DiseaseExtentClass extentClass = computer.compute(adminUnit, occurrencesForAdminUnit);
            classesByAdminUnit.put(occurrenceByAdminUnit.getKey(), extentClass);
        }
    }

    private DiseaseExtentClass computeDiseaseExtentClassUsingOccurrenceCount(int occurrenceCount, int factor) {
        // Convert an occurrence count into a disease extent class, using the disease extent parameters
        if (occurrenceCount >= parameters.getMinimumOccurrencesForPresence() * factor) {
            return findDiseaseExtentClass(DiseaseExtentClass.PRESENCE);
        } else if (occurrenceCount >= parameters.getMinimumOccurrencesForPossiblePresence() * factor) {
            return findDiseaseExtentClass(DiseaseExtentClass.POSSIBLE_PRESENCE);
        } else {
            return findDiseaseExtentClass(DiseaseExtentClass.UNCERTAIN);
        }
    }

    private DiseaseExtentClass computeDiseaseExtentClassUsingOccurrencesAndReviews(
            List<DiseaseOccurrenceForDiseaseExtent> occurrencesList, List<AdminUnitReview> reviewsList) {
        // Compute the score for each occurrence and each review, and take the average
        // Be extra careful with int -> double conversions...
        double occurrencesScore = computeOccurrencesScore(occurrencesList);
        double reviewsScore = computeReviewsScore(reviewsList);
        double totalScore = occurrencesScore + reviewsScore;
        double totalCount = occurrencesList.size() + reviewsList.size();
        double overallScore = totalScore / totalCount;

        if (overallScore > 1) {
            return findDiseaseExtentClass(DiseaseExtentClass.PRESENCE);
        } else if (overallScore > 0) {
            return findDiseaseExtentClass(DiseaseExtentClass.POSSIBLE_PRESENCE);
        } else if (overallScore == 0) {
            return findDiseaseExtentClass(DiseaseExtentClass.UNCERTAIN);
        } else if (overallScore >= -1) {
            return findDiseaseExtentClass(DiseaseExtentClass.POSSIBLE_ABSENCE);
        } else {
            return findDiseaseExtentClass(DiseaseExtentClass.ABSENCE);
        }
    }

    private DiseaseExtentClass computeDiseaseExtentClassForCountry(Integer countryGaulCode) {
        // The disease extent class for a country uses the "occurrence count" method, but with the parameters
        // multiplied by a factor of 2
        if (countryGaulCode != null) {
            Integer occurrenceCount = numberOfOccurrencesByCountry.get(countryGaulCode);
            if (occurrenceCount != null) {
                return computeDiseaseExtentClassUsingOccurrenceCount(occurrenceCount, 2);
            }
        }

        return findDiseaseExtentClass(DiseaseExtentClass.UNCERTAIN);
    }

    private int computeOccurrencesScore(List<DiseaseOccurrenceForDiseaseExtent> occurrenceList) {
        DateTime minimumDateForHigherScore =
                DateTime.now().minusYears(parameters.getMinimumYearsAgoForHigherOccurrenceScore());

        // Unlike computeReviewsScore(), the total is an integer so that we can maintain full accuracy over multiple
        // additions
        int total = 0;
        for (DiseaseOccurrenceForDiseaseExtent occurrence : occurrenceList) {
            // The score for each occurrence depends on the occurrence date. If it is before the "minimum date",
            // it scores the "lower score" otherwise it scores the "higher score". These values are all defined by
            // the disease extent parameters.
            boolean useLowerScore = occurrence.getOccurrenceDate().isBefore(minimumDateForHigherScore);
            total += useLowerScore ? parameters.getLowerOccurrenceScore() : parameters.getHigherOccurrenceScore();
        }
        return total;
    }

    private double computeReviewsScore(List<AdminUnitReview> reviews) {
        double total = 0;
        for (AdminUnitReview review : reviews) {
            // The response weighting is currently divided by 50 so that the weightings in the database (which
            // were chosen for use with the model) can be used for our purposes. Eventually this should be removed.
            int scaledResponseWeighting = review.getResponse().getWeighting() / SCALING_FACTOR;
            total += scaledResponseWeighting * review.getExpert().getWeighting();
        }
        return total;
    }

    private DiseaseExtentClass findDiseaseExtentClass(String diseaseExtentClass) {
        // Returns the disease extent class with the specified name
        return selectUnique(diseaseExtentClasses, having(
                on(DiseaseExtentClass.class).getName(), IsEqual.equalTo(diseaseExtentClass)));
    }

    private AdminUnitDiseaseExtentClass findAdminUnitDiseaseExtentClass(AdminUnitGlobalOrTropical adminUnit) {
        // Searches the current disease extent for the specified admin unit. Returns it if found, or null if not found.
        int gaulCodeToFind = adminUnit.getGaulCode();
        return selectUnique(currentDiseaseExtent, having(
                on(AdminUnitDiseaseExtentClass.class).getAdminUnitGlobalOrTropical().getGaulCode(),
                IsEqual.equalTo(gaulCodeToFind)));
    }

    private AdminUnitDiseaseExtentClass createAdminUnitDiseaseExtentClass(AdminUnitGlobalOrTropical adminUnit) {
        AdminUnitDiseaseExtentClass row = new AdminUnitDiseaseExtentClass();
        row.setDiseaseGroup(diseaseGroup);
        row.setAdminUnitGlobalOrTropical(adminUnit);
        return row;
    }

    private void removeReviewsWithNullExpertWeightings(List<AdminUnitReview> reviews) {
        Iterator<AdminUnitReview> iterator = reviews.iterator();
        while (iterator.hasNext()) {
            AdminUnitReview review = iterator.next();
            if (review.getExpert().getWeighting() == null) {
                iterator.remove();
            }
        }
    }

    /**
     * Computes the disease extent class for one admin unit.
     */
    private interface DiseaseExtentClassComputer {
        DiseaseExtentClass compute(AdminUnitGlobalOrTropical adminUnit,
                                   List<DiseaseOccurrenceForDiseaseExtent> occurrencesForAdminUnit);
    }
}