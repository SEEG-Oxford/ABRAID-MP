package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

/**
 * Helper class for determining the distance between a location and the disease extent.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DistanceFromDiseaseExtentHelper {
    private NativeSQL nativeSQL;
    private LocationService locationService;

    public DistanceFromDiseaseExtentHelper(NativeSQL nativeSQL, LocationService locationService) {
        this.nativeSQL = nativeSQL;
        this.locationService = locationService;
    }

    /**
     * Finds the distance between the occurrence's location and the occurrence's disease group's current extent.
     * @param occurrence The occurrence.
     * @return The distance from the disease extent.
     */
    @Transactional
    public Double findDistanceFromDiseaseExtent(DiseaseOccurrence occurrence) {
        DiseaseGroup diseaseGroup = occurrence.getDiseaseGroup();
        Location location = occurrence.getLocation();

        int diseaseGroupId = diseaseGroup.getId();
        boolean isGlobal = diseaseGroup.isGlobal();

        List<AdminUnitDiseaseExtentClass> diseaseExtentClasses =
                locationService.getAdminUnitDiseaseExtentClassesForLocation(diseaseGroupId, isGlobal, location);
        boolean containsPresence = containsClass(diseaseExtentClasses, DiseaseExtentClass.PRESENCE);
        boolean containsPossiblePresence = containsClass(diseaseExtentClasses, DiseaseExtentClass.POSSIBLE_PRESENCE);
        boolean containsUncertain = containsClass(diseaseExtentClasses, DiseaseExtentClass.UNCERTAIN);
        boolean containsPossibleAbsence = containsClass(diseaseExtentClasses, DiseaseExtentClass.POSSIBLE_ABSENCE);
        boolean containsAbsence = containsClass(diseaseExtentClasses, DiseaseExtentClass.ABSENCE);

        boolean insideExtent = containsPresence || containsPossiblePresence;
        boolean outsideExtent = containsUncertain || containsPossibleAbsence || containsAbsence;

        if (insideExtent && outsideExtent) {
            // "Split" country straddling the edge
            return 0.0;
        } else if (outsideExtent) {
            // We find the distance using a PostGIS query instead of using routines in the GeometryUtils class, because
            // loading the entire disease extent geometry into memory is likely to be inefficient
            Double distance = nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, isGlobal, location.getId());
            return (distance != null) ? (+1.0 * distance) : null;
        } else if (insideExtent) {
            Double distance = nativeSQL.findDistanceInsideDiseaseExtent(diseaseGroupId, isGlobal, location.getId());
            return (distance != null) ? (-1.0 * distance) : null;
        } else {
            return null; // No extent defined
        }
    }

    private boolean containsClass(List<AdminUnitDiseaseExtentClass> diseaseExtentClasses, String extentClass) {
        return !filter(
                having(on(AdminUnitDiseaseExtentClass.class).getDiseaseExtentClass().getName(), equalTo(extentClass)),
                diseaseExtentClasses).isEmpty();
    }
}
