package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonNamedCrs;

import java.util.ArrayList;
import java.util.List;

/**
 * A DTO to express a list of AdminUnits as a "FeatureCollection", with the extent class of a DiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeatureCollection
        extends GeoJsonFeatureCollection<GeoJsonDiseaseExtentFeature, GeoJsonNamedCrs> {
    public GeoJsonDiseaseExtentFeatureCollection() {
    }

    public GeoJsonDiseaseExtentFeatureCollection(List<AdminUnitDiseaseExtentClass> diseaseExtent) {
        super(extractExtentFeatures(diseaseExtent), GeoJsonNamedCrs.createEPSG4326(), null);
    }

    private static List<GeoJsonDiseaseExtentFeature> extractExtentFeatures(
            List<AdminUnitDiseaseExtentClass> diseaseExtent) {
        List<GeoJsonDiseaseExtentFeature> features = new ArrayList<>();

        for (AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass : diseaseExtent) {
            features.add(new GeoJsonDiseaseExtentFeature(adminUnitDiseaseExtentClass.getAdminUnitGlobalOrTropical(),
                    adminUnitDiseaseExtentClass.getDiseaseExtentClass()));
        }

        return features;
    }
}
