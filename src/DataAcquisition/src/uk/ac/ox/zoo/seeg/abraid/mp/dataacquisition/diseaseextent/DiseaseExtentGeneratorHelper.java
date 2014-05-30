package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent;

import ch.lambdaj.group.Group;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;

/**
 * A helper for the DiseaseExtentGenerator class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGeneratorHelper {
    private DiseaseGroup diseaseGroup;
    private DiseaseExtentParameters parameters;
    private List<AdminUnitDiseaseExtentClass> currentDiseaseExtent;
    private List<? extends AdminUnitGlobalOrTropical> adminUnits;
    private List<DiseaseOccurrenceForDiseaseExtent> occurrences;
    private List<DiseaseExtentClass> diseaseExtentClasses;

    private Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrencesByAdminUnit;
    private Map<Integer, Integer> numberOfOccurrencesByCountry;
    private Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> classesByAdminUnit;

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
        // Create a mapping between country GAUL code and the number of occurrences in that country
        numberOfOccurrencesByCountry = new HashMap<>();
        Group<DiseaseOccurrenceForDiseaseExtent> group = group(occurrences,
                by(on(DiseaseOccurrenceForDiseaseExtent.class).getCountryGaulCode()));
        for (Group<DiseaseOccurrenceForDiseaseExtent> subgroup : group.subgroups()) {
            numberOfOccurrencesByCountry.put((Integer) subgroup.key(), subgroup.getSize());
        }
    }

    /**
     * Computes the disease extent classes for an initial disease extent.
     */
    public void computeInitialDiseaseExtentClasses() {
        // For each admin unit, convert its list of disease occurrences into a disease extent class
        classesByAdminUnit = new HashMap<>();
        for (Map.Entry<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrenceByAdminUnit :
                occurrencesByAdminUnit.entrySet()) {
            int occurrenceCount = occurrenceByAdminUnit.getValue().size();
            DiseaseExtentClass diseaseExtentClass;
            if (occurrenceCount == 0) {
                diseaseExtentClass = computeDiseaseExtentClassForCountry(
                        occurrenceByAdminUnit.getKey().getCountryGaulCode());
            } else {
                diseaseExtentClass = computeDiseaseExtentClassUsingOccurrenceCount(occurrenceCount, 1);
            }
            classesByAdminUnit.put(occurrenceByAdminUnit.getKey(), diseaseExtentClass);
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
            row.setDiseaseExtentClass(classesByAdminUnit.get(adminUnit));
            row.setOccurrenceCount(occurrenceByAdminUnit.getValue().size());
            adminUnitDiseaseExtentClasses.add(row);
        }

        return adminUnitDiseaseExtentClasses;
    }

    private DiseaseExtentClass computeDiseaseExtentClassForCountry(Integer countryGaulCode) {
        if (countryGaulCode != null) {
            Integer occurrenceCount = numberOfOccurrencesByCountry.get(countryGaulCode);
            if (occurrenceCount != null) {
                return computeDiseaseExtentClassUsingOccurrenceCount(occurrenceCount, 2);
            }
        }

        return findDiseaseExtentClass(DiseaseExtentClass.UNCERTAIN);
    }

    private DiseaseExtentClass computeDiseaseExtentClassUsingOccurrenceCount(int occurrenceCount, int factor) {
        // Convert an occurrence count into a disease extent class, using the disease extent parameters
        // Although the disease service is called multiple times to get the classes, Hibernate caching will save us
        if (occurrenceCount >= parameters.getMinimumOccurrencesForPresence() * factor) {
            return findDiseaseExtentClass(DiseaseExtentClass.PRESENCE);
        } else if (occurrenceCount >= parameters.getMinimumOccurrencesForPossiblePresence() * factor) {
            return findDiseaseExtentClass(DiseaseExtentClass.POSSIBLE_PRESENCE);
        } else {
            return findDiseaseExtentClass(DiseaseExtentClass.UNCERTAIN);
        }
    }

    private DiseaseExtentClass findDiseaseExtentClass(String diseaseExtentClass) {
        List<DiseaseExtentClass> matchingClasses = select(diseaseExtentClasses, having(
                on(DiseaseExtentClass.class).getName().equals(diseaseExtentClass)));
        return matchingClasses.get(0);
    }

    private AdminUnitDiseaseExtentClass findAdminUnitDiseaseExtentClass(AdminUnitGlobalOrTropical adminUnit) {
        int gaulCodeToFind = adminUnit.getGaulCode();
        List<AdminUnitDiseaseExtentClass> matchingClasses = select(currentDiseaseExtent, having(
                on(AdminUnitDiseaseExtentClass.class).getAdminUnitGlobalOrTropical().getGaulCode() == gaulCodeToFind));
        return matchingClasses.get(0);
    }

    private AdminUnitDiseaseExtentClass createAdminUnitDiseaseExtentClass(AdminUnitGlobalOrTropical adminUnit) {
        AdminUnitDiseaseExtentClass row = new AdminUnitDiseaseExtentClass();
        row.setDiseaseGroup(diseaseGroup);
        row.setAdminUnitGlobalOrTropical(adminUnit);
        return row;
    }
}
