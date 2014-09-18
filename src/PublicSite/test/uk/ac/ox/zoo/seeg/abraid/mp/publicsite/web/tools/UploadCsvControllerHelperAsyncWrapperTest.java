package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.junit.Test;

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
        UploadCsvControllerHelper helper = mock(UploadCsvControllerHelper.class);
        UploadCsvControllerHelperAsyncWrapper wrapper = new UploadCsvControllerHelperAsyncWrapper(helper);
        String csv = "Test csv";
        String emailAddress = "user@test.com";
        String filePath = "/path/to/filename.csv";

        // Act
        wrapper.acquireCsvData(csv, emailAddress, filePath).get();

        // Assert
        verify(helper).acquireCsvData(eq(csv), eq(emailAddress), eq(filePath));
    }

    @Test
    public void acquireCsvDataCatchesThrownException() throws Exception {
        // Arrange
        UploadCsvControllerHelper helper = mock(UploadCsvControllerHelper.class);
        UploadCsvControllerHelperAsyncWrapper wrapper = new UploadCsvControllerHelperAsyncWrapper(helper);
        String csv = "Test csv";
        String emailAddress = "user@test.com";
        String filePath = "/path/to/filename.csv";

        doThrow(new RuntimeException("Test")).when(helper).acquireCsvData(anyString(), anyString(), anyString());

        // Act
        wrapper.acquireCsvData(csv, emailAddress, filePath).get();

        // Assert
        verify(helper).acquireCsvData(eq(csv), eq(emailAddress), eq(filePath));
    }
}
