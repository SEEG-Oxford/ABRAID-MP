package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectType;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseExtentGeoJsonTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the GeoJsonDiseaseExtentFeatureCollection class.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeatureCollectionTest extends AbstractDiseaseExtentGeoJsonTests {
    @Test
    public void constructorForGeoJsonDiseaseExtentFeatureCollectionExtractsFeaturesCorrectly() {
        // Arrange
        List<AdminUnitReview> reviews = new ArrayList<>();
        List<AdminUnitDiseaseExtentClass> diseaseExtent = Arrays.asList(defaultAdminUnitDiseaseExtentClass(),
                                                                        defaultAdminUnitDiseaseExtentClass());
        // Act
        GeoJsonDiseaseExtentFeatureCollection result = new GeoJsonDiseaseExtentFeatureCollection(diseaseExtent, reviews);

        // Assert
        assertThat(result.getType()).isEqualTo(GeoJsonObjectType.FEATURE_COLLECTION);
        assertThat(result.getCrs().getType()).isEqualTo("name");
        assertThat(result.getCrs().getProperties().getName()).isEqualTo("urn:ogc:def:crs:EPSG::4326");
        assertThat(result.getFeatures()).hasSameSizeAs(diseaseExtent);
        assertThat(result.getFeatures().get(0)).isInstanceOf(GeoJsonDiseaseExtentFeature.class);
    }
}
