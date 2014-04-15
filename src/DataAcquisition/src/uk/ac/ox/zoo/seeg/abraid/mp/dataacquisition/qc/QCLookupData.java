package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnit;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;

import java.util.List;

/**
 * Contains lookup data that is used when performing quality control checks.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCLookupData {
    private List<AdminUnit> adminUnits;

    private LocationService locationService;

    public QCLookupData(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Gets a list of administrative units.
     * @return A list of administrative units.
     */
    public List<AdminUnit> getAdminUnits() {
        if (adminUnits == null) {
            adminUnits = locationService.getAllAdminUnits();
        }
        return adminUnits;
    }
}
