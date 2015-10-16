package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtent;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the DiseaseGroupForModelRunValidator class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseGroupForModelRunValidatorTest {
    @Test
    public void diseaseGroupIsValid() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isNull();
    }

    @Test
    public void publicNameIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setPublicName(null);
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("the public name is missing");
    }

    @Test
    public void shortNameIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setShortName("    ");
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("the short name is missing");
    }

    @Test
    public void abbreviationIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setAbbreviation("");
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("the abbreviation is missing");
    }

    @Test
    public void globalOrTropicalIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setGlobal(null);
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("global/tropical is missing");
    }

    @Test
    public void validatorDiseaseGroupIsMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setValidatorDiseaseGroup(null);
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("the validator disease group is missing");
    }

    @Test
    public void diseaseExtentParametersAreMissing() {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setDiseaseExtentParameters(null);
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("the disease extent parameters are missing");
    }

    @Test
    public void parameterMinValidationWeightingIsMissing() {
        parameterIsMissing(new DiseaseExtent(new DiseaseGroup(), null, 36, 1, 2), "minimum validation weighting");
    }

    @Test
    public void parameterMaximumMonthsAgoForHigherOccurrenceScoreIsMissing() {
        parameterIsMissing(new DiseaseExtent(new DiseaseGroup(), 0.6, null, 1, 2), "maximum months ago for higher occurrence score");
    }

    @Test
    public void parameterLowerOccurrenceScoreIsMissing() {
        parameterIsMissing(new DiseaseExtent(new DiseaseGroup(), 0.6, 36, null, 2), "lower occurrence score");
    }

    @Test
    public void parameterHigherOccurrenceScoreIsMissing() {
        parameterIsMissing(new DiseaseExtent(new DiseaseGroup(), 0.6, 36, 1, null), "higher occurrence score");
    }

    private void parameterIsMissing(DiseaseExtent parameters, String missingParameterName) {
        // Arrange
        DiseaseGroup diseaseGroup = createDiseaseGroup();
        diseaseGroup.setDiseaseExtentParameters(parameters);
        DiseaseGroupForModelRunValidator validator = new DiseaseGroupForModelRunValidator(diseaseGroup);

        // Act
        String errorMessage = validator.validate();

        // Assert
        assertThat(errorMessage).isEqualTo("a disease extent parameter (" + missingParameterName + ") is missing");
    }


    private DiseaseGroup createDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup(87);
        diseaseGroup.setName("Test name");
        diseaseGroup.setPublicName("Test public name");
        diseaseGroup.setShortName("Test short name");
        diseaseGroup.setAbbreviation("Test abbreviation");
        diseaseGroup.setGlobal(false);
        diseaseGroup.setValidatorDiseaseGroup(new ValidatorDiseaseGroup());
        diseaseGroup.setDiseaseExtentParameters(new DiseaseExtent(new DiseaseGroup(), 0.6, 36, 1, 2));
        return diseaseGroup;
    }
}
