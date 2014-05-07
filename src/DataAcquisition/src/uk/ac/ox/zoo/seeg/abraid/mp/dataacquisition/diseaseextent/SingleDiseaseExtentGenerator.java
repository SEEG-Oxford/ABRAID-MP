package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent;

import ch.lambdaj.function.convert.Converter;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;

/**
 * Generates a disease extent for a single disease group.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class SingleDiseaseExtentGenerator {
    private DiseaseService diseaseService;

    public SingleDiseaseExtentGenerator(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Generates a disease extent for a single disease group.
     * @param diseaseGroupId The disease group.
     * @param parameters Parameters used in generating the disease extent.
     */
    @Transactional
    public void generateDiseaseExtent(Integer diseaseGroupId, DiseaseExtentParameters parameters) {
        List<AdminUnitDiseaseExtentClass> currentDiseaseExtent =
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);

        // If there is currently no disease extent for this disease group, create an initial extent
        if (currentDiseaseExtent.size() == 0) {
            createInitialExtent(diseaseGroupId, parameters);
        }
    }

    private void createInitialExtent(Integer diseaseGroupId, DiseaseExtentParameters parameters) {
        // Retrieve relevant disease occurrences and group them by admin unit
        Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrencesByAdminUnit =
                findAndGroupOccurrences(diseaseGroupId, parameters);

        // Convert the groups of disease occurrences into disease extent classes
        Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> classesByAdminUnit =
                findAndGroupInitialDiseaseExtentClasses(occurrencesByAdminUnit, parameters);

        // Write out the disease extent using the two groups above
        writeDiseaseExtent(diseaseGroupId, occurrencesByAdminUnit, classesByAdminUnit);
    }

    private Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> findAndGroupOccurrences(
            Integer diseaseGroupId, DiseaseExtentParameters parameters) {
        // Find all occurrences of this disease group, subject to the disease extent parameters
        List<DiseaseOccurrenceForDiseaseExtent> occurrences =
                diseaseService.getDiseaseOccurrencesForDiseaseExtent(diseaseGroupId,
                        parameters.getMinimumValidationWeighting(),
                        DateTime.now().minusYears(parameters.getMaximumYearsAgo()),
                        parameters.getFeedIds());

        // Find all admin units, for either global or tropical diseases depending on the disease group
        // This query is necessary so that admin units with no occurrences appear in the disease extent
        List<? extends AdminUnitGlobalOrTropical> adminUnits =
                diseaseService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(diseaseGroupId);

        // Return all occurrences, grouped by admin unit
        return groupOccurrencesByAdminUnit(occurrences, adminUnits);
    }

    private Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> findAndGroupInitialDiseaseExtentClasses(
            Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrencesByAdminUnit,
            final DiseaseExtentParameters parameters) {
        // For each admin unit, convert its list of disease occurrences into a disease extent class
        return convertMap(occurrencesByAdminUnit,
                          new Converter<List<DiseaseOccurrenceForDiseaseExtent>, DiseaseExtentClass>() {
            @Override
            public DiseaseExtentClass convert(List<DiseaseOccurrenceForDiseaseExtent> occurrences) {
                // This is the conversion of one list of disease occurrences into a disease extent class
                return getInitialDiseaseExtentClass(occurrences.size(), parameters);
            }
        });
    }

    private Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> groupOccurrencesByAdminUnit(
            List<DiseaseOccurrenceForDiseaseExtent> occurrences,
            List<? extends AdminUnitGlobalOrTropical> adminUnits) {

        // Group admin units by GAUL code
        Map<Integer, AdminUnitGlobalOrTropical> adminUnitMapByGaulCode
                = index(adminUnits, on(AdminUnitGlobalOrTropical.class).getGaulCode());

        // Create empty groups of occurrences by admin unit
        Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> group = new HashMap<>();
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            group.put(adminUnit, new ArrayList<DiseaseOccurrenceForDiseaseExtent>());
        }

        // Add occurrences to the groups
        for (DiseaseOccurrenceForDiseaseExtent occurrence : occurrences) {
            AdminUnitGlobalOrTropical adminUnit = adminUnitMapByGaulCode.get(
                    occurrence.getAdminUnitGlobalOrTropicalGaulCode());
            // Should never be null, but just in case
            if (adminUnit != null) {
                group.get(adminUnit).add(occurrence);
            }
        }

        return group;
    }

    private DiseaseExtentClass getInitialDiseaseExtentClass(int occurrenceCount, DiseaseExtentParameters parameters) {
        // Convert an occurrence count into a disease extent class, using the disease extent parameters
        if (occurrenceCount >= parameters.getMinimumOccurrencesForPresence()) {
            return DiseaseExtentClass.PRESENCE;
        } else if (occurrenceCount >= parameters.getMinimumOccurrencesForPossiblePresence()) {
            return DiseaseExtentClass.POSSIBLE_PRESENCE;
        } else {
            return DiseaseExtentClass.UNCERTAIN;
        }
    }

    private void writeDiseaseExtent(Integer diseaseGroupId,
                        Map<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrencesByAdminUnit,
                        Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> classesByAdminUnit) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);

        // Insert a new disease extent row for each admin unit, for this disease group
        for (Map.Entry<AdminUnitGlobalOrTropical, List<DiseaseOccurrenceForDiseaseExtent>> occurrenceByAdminUnit :
                occurrencesByAdminUnit.entrySet()) {
            AdminUnitGlobalOrTropical adminUnit = occurrenceByAdminUnit.getKey();
            AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass();
            adminUnitDiseaseExtentClass.setDiseaseGroup(diseaseGroup);
            adminUnitDiseaseExtentClass.setAdminUnitGlobalOrTropical(adminUnit);
            adminUnitDiseaseExtentClass.setDiseaseExtentClass(classesByAdminUnit.get(adminUnit));
            adminUnitDiseaseExtentClass.setOccurrenceCount(occurrenceByAdminUnit.getValue().size());
            diseaseService.saveAdminUnitDiseaseExtentClass(adminUnitDiseaseExtentClass);
        }
    }
}
