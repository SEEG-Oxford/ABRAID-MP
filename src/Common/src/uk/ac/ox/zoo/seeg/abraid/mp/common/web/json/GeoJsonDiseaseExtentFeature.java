package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobalOrTropical;
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

    public GeoJsonDiseaseExtentFeature(AdminUnitGlobalOrTropical adminUnit, DiseaseExtentClass diseaseExtentClass) {
        super(
                extractGaulCode(adminUnit),
                extractGeometry(adminUnit),
                extractProperties(adminUnit, diseaseExtentClass),
                null, null
        );
    }

    private static int extractGaulCode(AdminUnitGlobalOrTropical adminUnit) {
        return adminUnit.getGaulCode();
    }

    private static GeoJsonMultiPolygonGeometry<GeoJsonNamedCrs> extractGeometry(AdminUnitGlobalOrTropical adminUnit) {
        return new GeoJsonMultiPolygonGeometry<>(adminUnit.getGeom(), null, null);
    }

    private static GeoJsonDiseaseExtentFeatureProperties extractProperties(AdminUnitGlobalOrTropical adminUnit,
                                                                           DiseaseExtentClass diseaseExtentClass) {
        return new GeoJsonDiseaseExtentFeatureProperties(adminUnit, diseaseExtentClass);
    }
}
