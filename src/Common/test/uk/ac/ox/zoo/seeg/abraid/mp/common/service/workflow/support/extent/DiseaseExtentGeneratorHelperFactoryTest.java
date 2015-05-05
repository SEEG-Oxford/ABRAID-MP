package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for DiseaseExtentGeneratorHelperFactory.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGeneratorHelperFactoryTest extends BaseDiseaseExtentGenerationTests {
    @Before
    public void setup() {
        baseSetup();
    }

    @Test
    public void createHelper() throws Exception {
        // Arrange
        DiseaseExtentGeneratorHelperFactory target = new DiseaseExtentGeneratorHelperFactory();

        // Act
        DiseaseExtentGeneratorHelper result = target.createHelper(diseaseGroup, createInputData(createReviews(), createOccurrences()));

        // Assert
        assertThat(result).isEqualToComparingFieldByField(new DiseaseExtentGeneratorHelper(createInputData(createReviews(), createOccurrences()), parameters));
    }
}
