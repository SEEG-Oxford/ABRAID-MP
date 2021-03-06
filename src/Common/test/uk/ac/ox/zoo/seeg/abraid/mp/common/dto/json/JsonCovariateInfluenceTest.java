package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonCovariateInfluence.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateInfluenceTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        String name = "dir/upr_p.tif";
        String displayName = "GRUMP peri-urban surface";
        String info = "some info";
        double meanInfluence = 45.94;
        boolean discrete = true;

        CovariateInfluence covariateInfluence = mock(CovariateInfluence.class);
        CovariateFile covariateFile = mock(CovariateFile.class);
        CovariateSubFile subObj = mock(CovariateSubFile.class);
        when(subObj.getFile()).thenReturn(name);
        when(covariateFile.getFiles()).thenReturn(Arrays.asList(subObj));
        when(covariateFile.getName()).thenReturn(displayName);
        when(covariateFile.getInfo()).thenReturn(info);
        when(covariateFile.getDiscrete()).thenReturn(discrete);
        when(covariateInfluence.getMeanInfluence()).thenReturn(meanInfluence);
        when(covariateInfluence.getCovariateFile()).thenReturn(covariateFile);
        List<JsonEffectCurveCovariateInfluence> effectCurve = Arrays.asList(
                mock(JsonEffectCurveCovariateInfluence.class), mock(JsonEffectCurveCovariateInfluence.class));
        List<JsonCovariateValueBin> histogram = Arrays.asList(
                mock(JsonCovariateValueBin.class), mock(JsonCovariateValueBin.class));

        // Act
        JsonCovariateInfluence result = new JsonCovariateInfluence(covariateInfluence, histogram, effectCurve);

        // Assert
        assertThat(result.getName()).isEqualTo(displayName);
        assertThat(result.getInfo()).isEqualTo(info);
        assertThat(result.getMeanInfluence()).isEqualTo(meanInfluence);
        assertThat(result.getEffectCurve()).isEqualTo(effectCurve);
        assertThat(result.getValuesHistogram()).isEqualTo(histogram);
        assertThat(result.getDiscrete()).isEqualTo(discrete);
    }
}
