package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for ModelWrapperWebServiceAsyncWrapper.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelWrapperWebServiceAsyncWrapperTest {
    @Test
    public void publishSingleDiseaseCallsPublishForAllKnownModelWrappers() throws Exception {
        // Arrange
        String[] mwList = new String[] { "mw1", "mw2" };
        ModelWrapperWebService modelWrapperWebService = mock(ModelWrapperWebService.class);
        ModelWrapperWebServiceAsyncWrapper target = new ModelWrapperWebServiceAsyncWrapper(modelWrapperWebService, mwList);
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);

        // Act
        Future<Boolean> future = target.publishSingleDisease(diseaseGroup);
        Boolean result = future.get();

        // Assert
        verify(modelWrapperWebService).publishSingleDisease(URI.create(mwList[0]), diseaseGroup);
        verify(modelWrapperWebService).publishSingleDisease(URI.create(mwList[1]), diseaseGroup);
        assertThat(result).isTrue();
    }

    @Test
    public void publishSingleDiseaseReturnsFalseIfAnyCallFails() throws Exception {
        // Arrange
        String[] mwList = new String[] { "mw1", "mw2" };
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        ModelWrapperWebService modelWrapperWebService = mock(ModelWrapperWebService.class);
        doThrow(new WebServiceClientException("fail")).when(modelWrapperWebService).publishSingleDisease(URI.create(mwList[0]), diseaseGroup);
        ModelWrapperWebServiceAsyncWrapper target = new ModelWrapperWebServiceAsyncWrapper(modelWrapperWebService, mwList);

        // Act
        Future<Boolean> future = target.publishSingleDisease(diseaseGroup);
        Boolean result = future.get();

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void publishAllDiseasesCallsPublishForAllKnownModelWrappers() throws Exception {
        // Arrange
        String[] mwList = new String[] { "mw1", "mw2" };
        ModelWrapperWebService modelWrapperWebService = mock(ModelWrapperWebService.class);
        ModelWrapperWebServiceAsyncWrapper target = new ModelWrapperWebServiceAsyncWrapper(modelWrapperWebService, mwList);
        Collection<DiseaseGroup> diseaseGroups = Arrays.asList(mock(DiseaseGroup.class), mock(DiseaseGroup.class), mock(DiseaseGroup.class));

        // Act
        Future<Boolean> future = target.publishAllDiseases(diseaseGroups);
        Boolean result = future.get();

        // Assert
        verify(modelWrapperWebService).publishAllDiseases(URI.create(mwList[0]), diseaseGroups);
        verify(modelWrapperWebService).publishAllDiseases(URI.create(mwList[1]), diseaseGroups);
        assertThat(result).isTrue();
    }

    @Test
    public void publishAllDiseasesReturnsFalseIfAnyCallFails() throws Exception {
        // Arrange
        String[] mwList = new String[] { "mw1", "mw2" };
        Collection<DiseaseGroup> diseaseGroups = Arrays.asList(mock(DiseaseGroup.class), mock(DiseaseGroup.class), mock(DiseaseGroup.class));
        ModelWrapperWebService modelWrapperWebService = mock(ModelWrapperWebService.class);
        doThrow(new WebServiceClientException("fail")).when(modelWrapperWebService).publishAllDiseases(URI.create(mwList[0]), diseaseGroups);
        ModelWrapperWebServiceAsyncWrapper target = new ModelWrapperWebServiceAsyncWrapper(modelWrapperWebService, mwList);

        // Act
        Future<Boolean> future = target.publishAllDiseases(diseaseGroups);
        Boolean result = future.get();

        // Assert
        assertThat(result).isFalse();
    }
}