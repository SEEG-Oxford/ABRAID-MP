package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the AutomaticModelRunsEnabler class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AutomaticModelRunsEnablerTest {
    private DiseaseService diseaseService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;
    private AutomaticModelRunsEnabler automaticModelRunsEnabler;

    @Before
    public void setUp() {
        ModelRunService modelRunService = new ModelRunServiceImpl(mock(ModelRunDao.class), mock(NativeSQL.class));
        diseaseService = mock(DiseaseService.class);
        diseaseOccurrenceValidationService = mock(DiseaseOccurrenceValidationService.class);
        automaticModelRunsEnabler = new AutomaticModelRunsEnabler(diseaseService, diseaseOccurrenceValidationService,
                modelRunService);

        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void enableAutomaticModelRunsSavesAutomaticModelRunsStartDateOnDiseaseGroup() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        // Act
        automaticModelRunsEnabler.enable(diseaseGroupId);

        // Assert
        verify(diseaseService).saveDiseaseGroup(diseaseGroup);
        assertThat(diseaseGroup.getAutomaticModelRunsStartDate()).isEqualTo(now);
    }

    @Test
    public void enableAutomaticModelRunsSavesClassChangedDateOnAdminUnitDiseaseExtentClasses() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        AdminUnitDiseaseExtentClass extentClass = new AdminUnitDiseaseExtentClass();
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId)).thenReturn(Arrays.asList(extentClass));

        // Act
        automaticModelRunsEnabler.enable(diseaseGroupId);

        // Assert
        verify(diseaseService).saveAdminUnitDiseaseExtentClass(extentClass);
        assertThat(extentClass.getClassChangedDate()).isEqualTo(DateTime.now());
    }

    @Test
    public void enableAutomaticModelRunsAddsValidationParametersToDiseaseOccurrence() throws Exception {
        // Arrange
        int diseaseGroupId = 87;

        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        DiseaseOccurrence occurrence1 = new DiseaseOccurrence();
        DiseaseOccurrence occurrence2 = new DiseaseOccurrence();
        occurrence1.setOccurrenceDate(DateTime.now().minusDays(21));
        occurrence2.setOccurrenceDate(DateTime.now().minusDays(1));
        when(diseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(diseaseGroupId, false)).thenReturn(
                Arrays.asList(occurrence1, occurrence2));

        // Act
        automaticModelRunsEnabler.enable(diseaseGroupId);

        // Assert
        assertThat(occurrence1.getFinalWeighting()).isEqualTo(0);
        assertThat(occurrence1.getFinalWeightingExcludingSpatial()).isEqualTo(0);
        assertThat(occurrence2.getFinalWeighting()).isNull();
        assertThat(occurrence2.getFinalWeightingExcludingSpatial()).isNull();

        verify(diseaseOccurrenceValidationService).addValidationParameters(Arrays.asList(occurrence2));
        verify(diseaseService).saveDiseaseOccurrence(occurrence1);
        verify(diseaseService).saveDiseaseOccurrence(occurrence2);
    }
}
