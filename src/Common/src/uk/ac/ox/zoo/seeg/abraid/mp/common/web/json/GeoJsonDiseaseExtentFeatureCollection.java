package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GlobalAdminUnit;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonNamedCrs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeatureCollection
        extends GeoJsonFeatureCollection<GeoJsonDiseaseExtentFeature, GeoJsonNamedCrs> {
    public GeoJsonDiseaseExtentFeatureCollection() {
    }

    public GeoJsonDiseaseExtentFeatureCollection(Map<GlobalAdminUnit, DiseaseExtentClass> map) {
        super(extractExtentFeatures(map), GeoJsonNamedCrs.createEPSG4326(), null);
    }

    private static List<GeoJsonDiseaseExtentFeature> extractExtentFeatures(Map<GlobalAdminUnit, DiseaseExtentClass> map) {
        List<GeoJsonDiseaseExtentFeature> features = new ArrayList<>();
        for (GlobalAdminUnit globalAdminUnit : map.keySet()) {
            DiseaseExtentClass diseaseExtentClass = map.get(globalAdminUnit);
            features.add(new GeoJsonDiseaseExtentFeature(globalAdminUnit, diseaseExtentClass));
        }
        return features;
    }
}
