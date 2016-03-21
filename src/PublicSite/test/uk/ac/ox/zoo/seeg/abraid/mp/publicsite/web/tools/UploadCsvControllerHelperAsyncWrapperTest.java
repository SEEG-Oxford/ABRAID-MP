package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the UploadCsvControllerHelperAsyncWrapper class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class UploadCsvControllerHelperAsyncWrapperTest {
    @Test
    public void acquireCsvDataRunsSuccessfully() throws Exception {
        // Arrange
        boolean isGoldStandard = false;
        boolean isBias = true;
        DiseaseGroup biasDisease = mock(DiseaseGroup.class);
        UploadCsvControllerHelper helper = mock(UploadCsvControllerHelper.class);
        UploadCsvControllerHelperAsyncWrapper wrapper = new UploadCsvControllerHelperAsyncWrapper(helper);
        byte[] csv = "Test csv".getBytes();
        String emailAddress = "user@test.com";
        String filePath = "/path/to/filename.csv";

        // Act
        wrapper.acquireCsvData(csv, isBias, isGoldStandard, biasDisease, emailAddress, filePath).get();

        // Assert
        verify(helper).acquireCsvData(eq(csv), eq(isBias), eq(isGoldStandard), eq(biasDisease), eq(emailAddress), eq(filePath));
    }

    @Test
    public void acquireCsvDataCatchesThrownException() throws Exception {
        // Arrange
        boolean isGoldStandard = false;
        boolean isBias = true;
        DiseaseGroup biasDisease = mock(DiseaseGroup.class);
        UploadCsvControllerHelper helper = mock(UploadCsvControllerHelper.class);
        UploadCsvControllerHelperAsyncWrapper wrapper = new UploadCsvControllerHelperAsyncWrapper(helper);
        byte[] csv = "Test csv".getBytes();
        String emailAddress = "user@test.com";
        String filePath = "/path/to/filename.csv";

        doThrow(new RuntimeException("Test")).when(helper).acquireCsvData(any(byte[].class), anyBoolean(), anyBoolean(), any(DiseaseGroup.class), anyString(),
                anyString());

        // Act
        wrapper.acquireCsvData(csv, isBias, isGoldStandard, biasDisease, emailAddress, filePath).get();

        // Assert
        verify(helper).acquireCsvData(eq(csv), eq(isBias), eq(isGoldStandard), eq(biasDisease), eq(emailAddress), eq(filePath));
        // Implicit assertion that an exception hasn't bubble out of wrapper.acquireCsvData
    }
}
