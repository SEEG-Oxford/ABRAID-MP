package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnit;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.List;

import static ch.lambdaj.Lambda.*;

/**
 * Contains lookup data that is used when performing quality control checks.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCLookupData {
    private List<AdminUnit> adminUnits;
    private MultiPolygon landSeaBorders;

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

    /**
     * Gets a multipolygon representing the concatenation of the land-sea borders.
     * @return A multipolygon representing the concatenation of the land-sea borders.
     */
    public MultiPolygon getLandSeaBorders() {
        if (landSeaBorders == null) {
            List<LandSeaBorder> landSeaBorderList = locationService.getAllLandSeaBorders();
            List<MultiPolygon> multiPolygons = extract(landSeaBorderList, on(LandSeaBorder.class).getGeom());
            landSeaBorders = GeometryUtils.concatenate(multiPolygons);
        }
        return landSeaBorders;
    }
}
