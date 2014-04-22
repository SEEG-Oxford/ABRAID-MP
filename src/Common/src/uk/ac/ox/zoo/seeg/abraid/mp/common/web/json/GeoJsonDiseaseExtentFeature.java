package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GlobalAdminUnit;
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

    public GeoJsonDiseaseExtentFeature(GlobalAdminUnit globalAdminUnit, DiseaseExtentClass diseaseExtentClass) {
        super(
                extractGaulCode(globalAdminUnit),
                extractGeometry(globalAdminUnit),
                extractProperties(globalAdminUnit, diseaseExtentClass),
                null, null
        );
    }

    private static int extractGaulCode(GlobalAdminUnit globalAdminUnit) {
        return globalAdminUnit.getGaulCode();
    }

    private static GeoJsonMultiPolygonGeometry<GeoJsonNamedCrs> extractGeometry(GlobalAdminUnit globalAdminUnit) {
        return new GeoJsonMultiPolygonGeometry<>(globalAdminUnit.getGeom(), null, null);
    }

    private static GeoJsonDiseaseExtentFeatureProperties extractProperties(GlobalAdminUnit globalAdminUnit,
                                                                           DiseaseExtentClass diseaseExtentClass) {
        return new GeoJsonDiseaseExtentFeatureProperties(globalAdminUnit, diseaseExtentClass);
    }
}
