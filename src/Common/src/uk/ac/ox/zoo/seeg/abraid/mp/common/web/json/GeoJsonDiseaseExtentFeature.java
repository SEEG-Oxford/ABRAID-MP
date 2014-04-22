package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobal;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonFeature;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonMultiPolygonGeometry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonNamedCrs;

/**
 * A DTO to express an AdminUnit, with properties referring to the DIseaseGroup, as a "Feature".
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeature extends GeoJsonFeature
        <GeoJsonDiseaseExtentFeatureProperties, GeoJsonMultiPolygonGeometry<GeoJsonNamedCrs>, GeoJsonNamedCrs> {
    public GeoJsonDiseaseExtentFeature() {
    }

    public GeoJsonDiseaseExtentFeature(AdminUnitGlobal adminUnitGlobal, DiseaseExtentClass diseaseExtentClass) {
        super(
                extractGaulCode(adminUnitGlobal),
                extractGeometry(adminUnitGlobal),
                extractProperties(adminUnitGlobal, diseaseExtentClass),
                null, null
        );
    }

    private static int extractGaulCode(AdminUnitGlobal adminUnitGlobal) {
        return adminUnitGlobal.getGaulCode();
    }

    private static GeoJsonMultiPolygonGeometry<GeoJsonNamedCrs> extractGeometry(AdminUnitGlobal adminUnitGlobal) {
        return new GeoJsonMultiPolygonGeometry<>(adminUnitGlobal.getGeom(), null, null);
    }

    private static GeoJsonDiseaseExtentFeatureProperties extractProperties(AdminUnitGlobal adminUnitGlobal,
                                                                           DiseaseExtentClass diseaseExtentClass) {
        return new GeoJsonDiseaseExtentFeatureProperties(adminUnitGlobal, diseaseExtentClass);
    }
}
