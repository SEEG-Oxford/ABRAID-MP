package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobal;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for DiseaseExtentGenerationInputData.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseExtentGenerationInputDataTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        List<DiseaseExtentClass> diseaseExtentClasses = new ArrayList<>();
        List<AdminUnitGlobal> adminUnits = new ArrayList<>();
        List<AdminUnitReview> reviews = new ArrayList<>();
        List<DiseaseOccurrence> occurrences = new ArrayList<>();

        // Act
        DiseaseExtentGenerationInputData result = new DiseaseExtentGenerationInputData(
                diseaseExtentClasses,
                adminUnits,
                reviews,
                occurrences);

        // Assert
        assertThat(result.getDiseaseExtentClasses()).isSameAs(diseaseExtentClasses);
        assertThat(result.getAdminUnits()).isSameAs(adminUnits);
        assertThat(result.getReviews()).isSameAs(reviews);
        assertThat(result.getOccurrences()).isSameAs(occurrences);
    }
}
