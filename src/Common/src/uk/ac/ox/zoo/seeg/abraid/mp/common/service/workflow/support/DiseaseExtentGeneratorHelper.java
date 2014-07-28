package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.group.Group;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * A helper for the DiseaseExtentGenerator class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGeneratorHelper {
    private static final int SCALING_FACTOR = 50;

    // Input fields
    private DiseaseGroup diseaseGroup;
    private DiseaseExtent parameters;
    private List<AdminUnitDiseaseExtentClass> currentDiseaseExtent;
    private List<? extends AdminUnitGlobalOrTropical> adminUnits;
    private List<DiseaseExtentClass> diseaseExtentClasses;
    private List<DiseaseOccurrenceForDiseaseExtent> occurrences;
    private List<AdminUnitReview> reviews;

    // Working fields
    private Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrencesByAdminUnit;
    private Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> classesByAdminUnit;
    private Map<Integer, Integer> numberOfOccurrencesByCountry;
    private Map<Integer, List<AdminUnitReview>> reviewsByAdminUnit;

    public DiseaseExtentGeneratorHelper(DiseaseGroup diseaseGroup,
                                        List<AdminUnitDiseaseExtentClass> currentDiseaseExtent,
                                        List<? extends AdminUnitGlobalOrTropical> adminUnits,
                                        List<DiseaseExtentClass> diseaseExtentClasses) {
        this.diseaseGroup = diseaseGroup;
        this.parameters = diseaseGroup.getDiseaseExtentParameters();
        this.currentDiseaseExtent = currentDiseaseExtent;
        this.adminUnits = adminUnits;
        this.diseaseExtentClasses = diseaseExtentClasses;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public DiseaseExtent getParameters() {
        return parameters;
    }

    public List<AdminUnitDiseaseExtentClass> getCurrentDiseaseExtent() {
        return currentDiseaseExtent;
    }

    public List<DiseaseOccurrenceForDiseaseExtent> getOccurrences() {
        return occurrences;
    }

    public Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> getOccurrencesByAdminUnit() {
        return occurrencesByAdminUnit;
    }

    public Map<Integer, Integer> getNumberOfOccurrencesByCountry() {
        return numberOfOccurrencesByCountry;
    }

    public Map<Integer, List<AdminUnitReview>> getReviewsByAdminUnit() {
        return reviewsByAdminUnit;
    }

    public void setOccurrences(List<DiseaseOccurrenceForDiseaseExtent> occurrences) {
        this.occurrences = occurrences;
    }

    public void setReviews(List<AdminUnitReview> reviews) {
        this.reviews = reviews;
    }

    /**
     * Groups the disease occurrences by admin unit (global or tropical).
     */
    public void groupOccurrencesByAdminUnit() {
        // Create empty groups of occurrences by admin unit
        occurrencesByAdminUnit = new HashMap<>();
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            occurrencesByAdminUnit.put(adminUnit, new ArrayList<DiseaseOccurrenceForDiseaseExtent>());
        }

        // Add occurrences to the groups
        for (DiseaseOccurrenceForDiseaseExtent occurrence : occurrences) {
            AdminUnitGlobalOrTropical adminUnit = getAdminUnitByGaulCode(occurrence.getAdminUnitGaulCode());
            occurrencesByAdminUnit.get(adminUnit).add(occurrence);
        }
    }

    /**
     * Groups the occurrences by country (strictly, it groups the number of occurrences by country GAUL code).
     * The country GAUL code is taken from the admin unit global/tropical entity.
     */
    public void groupOccurrencesByCountry() {
        numberOfOccurrencesByCountry = new HashMap<>();
        for (DiseaseOccurrenceForDiseaseExtent occurrence : occurrences) {
            AdminUnitGlobalOrTropical adminUnit = getAdminUnitByGaulCode(occurrence.getAdminUnitGaulCode());
            Integer countryGaulCode = adminUnit.getCountryGaulCode();
            if (countryGaulCode != null) {
                // Country GAUL code found, so add 1 to the number of occurrences for this country
                Integer numberOfOccurrences = numberOfOccurrencesByCountry.get(countryGaulCode);
                numberOfOccurrencesByCountry.put(countryGaulCode, nullSafeAdd(numberOfOccurrences, 1));
            }
        }
    }

    /**
     * Groups the expert reviews by admin unit (strictly, by admin unit GAUL code).
     */
    public void groupReviewsByAdminUnit() {
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
     * For each admin unit, convert its list of disease occurrences into a disease extent class.
     * @param computer A method for converting the disease occurrences for 1 admin unit into a disease extent class.
     */
    public void computeDiseaseExtentClasses(DiseaseExtentClassComputer computer) {
        classesByAdminUnit = new HashMap<>();
        for (Map.Entry<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrenceByAdminUnit :
                occurrencesByAdminUnit.entrySet()) {
            AdminUnitGlobalOrTropical adminUnit = occurrenceByAdminUnit.getKey();
            List<DiseaseOccurrenceForDiseaseExtent> occurrencesForAdminUnit = occurrenceByAdminUnit.getValue();
            DiseaseExtentClass extentClass = computer.compute(adminUnit, occurrencesForAdminUnit);
            classesByAdminUnit.put(occurrenceByAdminUnit.getKey(), extentClass);
        }
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
            if (!newClass.equals(row.getDiseaseExtentClass())) {
                row.setDiseaseExtentClass(newClass);
                row.setClassChangedDate(DateTime.now());
            }
            row.setOccurrenceCount(occurrenceByAdminUnit.getValue().size());
            adminUnitDiseaseExtentClasses.add(row);
        }
        return adminUnitDiseaseExtentClasses;
    }

    /**
     * Computes a disease extent class, based on the number of occurrences and a scaling factor.
     * @param occurrenceCount The number of occurrences.
     * @param factor A scaling factor that is multiplied by the number of occurrences when doing the comparison.
     * @return The computed disease extent class.
     */
    public DiseaseExtentClass computeDiseaseExtentClassUsingOccurrenceCount(int occurrenceCount, int factor) {
        // Convert an occurrence count into a disease extent class, using the disease extent parameters
        if (occurrenceCount >= parameters.getMinimumOccurrencesForPresence() * factor) {
            return findDiseaseExtentClass(DiseaseExtentClass.PRESENCE);
        } else if (occurrenceCount >= parameters.getMinimumOccurrencesForPossiblePresence() * factor) {
            return findDiseaseExtentClass(DiseaseExtentClass.POSSIBLE_PRESENCE);
        } else {
            return findDiseaseExtentClass(DiseaseExtentClass.UNCERTAIN);
        }
    }

    /**
     * Computes a disease extent class, based on a list of occurrences and a list of reviews.
     * @param occurrencesList The list of occurrences.
     * @param reviewsList The list of reviews.
     * @return The computed disease extent class.
     */
    public DiseaseExtentClass computeDiseaseExtentClassUsingOccurrencesAndReviews(
            List<DiseaseOccurrenceForDiseaseExtent> occurrencesList, List<AdminUnitReview> reviewsList) {
        double overallScore = computeScoreForOccurrencesAndReviews(occurrencesList, reviewsList);

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

    /**
     * Computes a disease extent class, based on a list of occurrences and a list of reviews.
     * @param occurrencesList The list of occurrences.
     * @param reviewsList The list of reviews.
     * @return The computed disease extent class.
     */
    public double computeScoreForOccurrencesAndReviews(List<DiseaseOccurrenceForDiseaseExtent> occurrencesList,
                                                       List<AdminUnitReview> reviewsList) {
        // Compute the score for each occurrence and each review, and take the average
        // Be extra careful with int -> double conversions...
        double occurrencesScore = computeOccurrencesScore(occurrencesList);
        double reviewsScore = computeReviewsScore(reviewsList);
        double totalScore = occurrencesScore + reviewsScore;
        double totalCount = occurrencesList.size() + reviewsList.size();
        return (totalCount == 0) ? 0 : (totalScore / totalCount);
    }

    /**
     * Computes a disease extent class for a country.
     * @param countryGaulCode The country's GAUL code.
     * @return The computed disease extent class.
     */
    public DiseaseExtentClass computeDiseaseExtentClassForCountry(Integer countryGaulCode) {
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
        DateTime oldestDateForHigherScore =
                DateTime.now().minusMonths(parameters.getMaximumMonthsAgoForHigherOccurrenceScore());

        // Unlike computeReviewsScore(), the total is an integer so that we can maintain full accuracy over multiple
        // additions
        int total = 0;
        for (DiseaseOccurrenceForDiseaseExtent occurrence : occurrenceList) {
            // The score for each occurrence depends on the occurrence date. It scores the "higher score" unless it
            // is older than the oldest date allowed for the higher score, in which case it scores the "lower score".
            // These values are all defined by the disease extent parameters.
            boolean useLowerScore = occurrence.getOccurrenceDate().isBefore(oldestDateForHigherScore);
            total += useLowerScore ? parameters.getLowerOccurrenceScore() : parameters.getHigherOccurrenceScore();
        }
        return total;
    }

    private double computeReviewsScore(List<AdminUnitReview> reviewsList) {
        double total = 0;
        for (AdminUnitReview review : reviewsList) {
            // The response weighting is currently divided by 50 so that the weightings in the database (which
            // were chosen for use with the model) can be used for our purposes. Eventually this should be removed.
            int scaledResponseWeighting = review.getResponse().getWeighting() / SCALING_FACTOR;
            total += scaledResponseWeighting * review.getExpert().getWeighting();
        }
        return total;
    }

    private AdminUnitGlobalOrTropical getAdminUnitByGaulCode(int gaulCode) {
        return selectUnique(adminUnits, having(
                on(AdminUnitGlobalOrTropical.class).getGaulCode(), equalTo(gaulCode)));
    }

    private List<AdminUnitReview> getReviewsByGaulCode(int adminUnitGaulCode) {
        List<AdminUnitReview> reviewsList = reviewsByAdminUnit.get(adminUnitGaulCode);
        return (reviewsList == null) ? new ArrayList<AdminUnitReview>() : reviewsList;
    }

    private DiseaseExtentClass findDiseaseExtentClass(String diseaseExtentClass) {
        // Returns the disease extent class with the specified name
        return selectUnique(diseaseExtentClasses, having(
                on(DiseaseExtentClass.class).getName(), equalTo(diseaseExtentClass)));
    }

    private AdminUnitDiseaseExtentClass findAdminUnitDiseaseExtentClass(AdminUnitGlobalOrTropical adminUnit) {
        // Searches the current disease extent for the specified admin unit. Returns it if found, or null if not found.
        int gaulCodeToFind = adminUnit.getGaulCode();
        return selectUnique(currentDiseaseExtent, having(
                on(AdminUnitDiseaseExtentClass.class).getAdminUnitGlobalOrTropical().getGaulCode(),
                equalTo(gaulCodeToFind)));
    }

    private AdminUnitDiseaseExtentClass createAdminUnitDiseaseExtentClass(AdminUnitGlobalOrTropical adminUnit) {
        AdminUnitDiseaseExtentClass row = new AdminUnitDiseaseExtentClass();
        row.setDiseaseGroup(diseaseGroup);
        row.setAdminUnitGlobalOrTropical(adminUnit);
        return row;
    }

    private int nullSafeAdd(Integer a, Integer b) {
        return ((a != null) ? a : 0) + ((b != null) ? b : 0);
    }

    /**
     * Computes the disease extent class for one admin unit.
     */
    private interface DiseaseExtentClassComputer {
        DiseaseExtentClass compute(AdminUnitGlobalOrTropical adminUnit,
                                   List<DiseaseOccurrenceForDiseaseExtent> occurrencesForAdminUnit);
    }
}
