package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for DiseaseExtentGenerationOutputData.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseExtentGenerationOutputDataTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        Map<Integer, DiseaseExtentClass> diseaseExtentClasses = new HashMap<>();
        Map<Integer, Integer> occurrenceCounts = new HashMap<>();
        Map<Integer, Collection<DiseaseOccurrence>> latestOccurrences = new HashMap<>();

        // Act
        DiseaseExtentGenerationOutputData result = new DiseaseExtentGenerationOutputData(
                diseaseExtentClasses,
                occurrenceCounts,
                latestOccurrences);

        // Assert
        assertThat(result.getDiseaseExtentClassByGaulCode()).isSameAs(diseaseExtentClasses);
        assertThat(result.getOccurrenceCounts()).isSameAs(occurrenceCounts);
        assertThat(result.getLatestOccurrencesByGaulCode()).isSameAs(latestOccurrences);
    }
}

