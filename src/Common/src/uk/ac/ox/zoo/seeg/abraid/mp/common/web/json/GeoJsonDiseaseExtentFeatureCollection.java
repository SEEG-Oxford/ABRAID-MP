package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobalOrTropical;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonNamedCrs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A DTO to express a list of AdminUnits as a "FeatureCollection", with the extent class of a DiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeatureCollection
        extends GeoJsonFeatureCollection<GeoJsonDiseaseExtentFeature, GeoJsonNamedCrs> {
    public GeoJsonDiseaseExtentFeatureCollection() {
    }

    public GeoJsonDiseaseExtentFeatureCollection(Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> map) {
        super(extractExtentFeatures(map), GeoJsonNamedCrs.createEPSG4326(), null);
    }

    private static List<GeoJsonDiseaseExtentFeature> extractExtentFeatures(
            Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> map) {
        List<GeoJsonDiseaseExtentFeature> features = new ArrayList<>();
        for (Map.Entry<AdminUnitGlobalOrTropical, DiseaseExtentClass> entry : map.entrySet()) {
            features.add(new GeoJsonDiseaseExtentFeature(entry.getKey(), entry.getValue()));
        }
        return features;
    }
}
