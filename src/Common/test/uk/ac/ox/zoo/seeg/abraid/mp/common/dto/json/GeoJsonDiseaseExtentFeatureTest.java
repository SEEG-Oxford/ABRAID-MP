package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectType;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseExtentGeoJsonTests;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the GeoJsonDiseaseExtentFeature class.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeatureTest extends AbstractDiseaseExtentGeoJsonTests {
    @Test
    public void constructorForGeoJsonDiseaseExtentFeatureBindsParametersCorrectly() throws Exception {
        // Arrange
        int gaulCode = 101;

        AdminUnitGlobal adminUnitGlobal = createAdminUnitGlobal(gaulCode, "Admin Unit");
        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                adminUnitGlobal,
                new DiseaseGroup(),
                new DiseaseExtentClass(DiseaseExtentClass.PRESENCE),
                0);
        List<AdminUnitReview> reviews = new ArrayList<>();

        // Act
        GeoJsonDiseaseExtentFeature result = new GeoJsonDiseaseExtentFeature(adminUnitDiseaseExtentClass, reviews);

        // Assert
        assertThat(result.getType()).isEqualTo(GeoJsonObjectType.FEATURE);
        assertThat(result.getId()).isEqualTo(gaulCode);
        assertThat(result.getCrs()).isNull();
        assertThat(result.getBBox()).isNull();
        assertThat(result.getGeometry().getType()).isEqualTo(GeoJsonObjectType.MULTI_POLYGON);
        assertThat(result.getGeometry().getCoordinates()).isNotNull();
        assertThat(result.getGeometry().getCrs()).isNull();
        assertThat(result.getGeometry().getBBox()).isNull();
        assertThat(result.getProperties()).isNotNull();
    }
}
