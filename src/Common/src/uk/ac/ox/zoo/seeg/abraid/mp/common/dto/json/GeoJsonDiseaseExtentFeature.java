package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobalOrTropical;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonFeature;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonMultiPolygonGeometry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonNamedCrs;

import java.util.List;

/**
 * A DTO to express an AdminUnit, with properties referring to the DiseaseGroup, as a "Feature".
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeature extends GeoJsonFeature
        <GeoJsonDiseaseExtentFeatureProperties, GeoJsonMultiPolygonGeometry<GeoJsonNamedCrs>, GeoJsonNamedCrs> {
    public GeoJsonDiseaseExtentFeature() {
    }

    public GeoJsonDiseaseExtentFeature(AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass,
                                       List<AdminUnitReview> reviews) {
        super(
                extractGaulCode(adminUnitDiseaseExtentClass.getAdminUnitGlobalOrTropical()),
                extractGeometry(adminUnitDiseaseExtentClass.getAdminUnitGlobalOrTropical()),
                extractProperties(adminUnitDiseaseExtentClass, reviews),
                null, null
        );
    }

    private static int extractGaulCode(AdminUnitGlobalOrTropical adminUnit) {
        return adminUnit.getGaulCode();
    }

    private static GeoJsonMultiPolygonGeometry<GeoJsonNamedCrs> extractGeometry(AdminUnitGlobalOrTropical adminUnit) {
        return new GeoJsonMultiPolygonGeometry<>(adminUnit.getSimplifiedGeom(), null, null);
    }

    private static GeoJsonDiseaseExtentFeatureProperties extractProperties(
            AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass, List<AdminUnitReview> reviews) {
        return new GeoJsonDiseaseExtentFeatureProperties(adminUnitDiseaseExtentClass, reviews);
    }
}
