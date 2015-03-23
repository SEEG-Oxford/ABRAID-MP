package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonFileUploadResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the UploadCsvController class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class UploadCsvControllerTest {
    private CurrentUserService currentUserService;
    private ExpertService expertService;
    private UploadCsvControllerHelperAsyncWrapper uploadCsvControllerHelperAsyncWrapper;
    private UploadCsvController controller;

    private static final String USER_EMAIL_ADDRESS = "user@test.com";

    @Before
    public void setUp() {
        currentUserService = mock(CurrentUserService.class);
        expertService = mock(ExpertService.class);
        uploadCsvControllerHelperAsyncWrapper = mock(UploadCsvControllerHelperAsyncWrapper.class);
        controller = new UploadCsvController(currentUserService, expertService, uploadCsvControllerHelperAsyncWrapper);

        setUpCurrentUserService();
    }

    private void setUpCurrentUserService() {
        when(currentUserService.getCurrentUserId()).thenReturn(1);
        Expert expert = mock(Expert.class);
        when(expertService.getExpertById(1)).thenReturn(expert);
        when(expert.getEmail()).thenReturn(USER_EMAIL_ADDRESS);
    }

    @Test
    public void uploadCsvFileReturnsBadRequestIfFileIsNull() throws Exception {
        // Act
        ResponseEntity<JsonFileUploadResponse> responseEntity = controller.uploadCsvFile(null, false);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(JsonFileUploadResponse.FAIL);
    }

    @Test
    public void uploadCsvFileReturnsBadRequestIfFileIsEmpty() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile("filename", new byte[] {});

        // Act
        ResponseEntity<JsonFileUploadResponse> responseEntity = controller.uploadCsvFile(file, false);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(JsonFileUploadResponse.FAIL);
    }

    @Test
    public void uploadCsvFileReturnsOKIfSuccessful() throws Exception {
        uploadCsvFileSuccessful(false);
    }

    @Test
    public void uploadGoldStandardCsvFileReturnsOKIfSuccessful() throws Exception {
        uploadCsvFileSuccessful(true);
    }

    private void uploadCsvFileSuccessful(boolean isGoldStandard) throws Exception {
        // Arrange
        byte[] csv = "Test CSV".getBytes();
        String filename = "filename.csv";
        String filePath = "/path/to/filename.csv";
        MultipartFile file = new MockMultipartFile(filename, filePath, MediaType.APPLICATION_OCTET_STREAM_VALUE, csv);

        // Act
        ResponseEntity<JsonFileUploadResponse> responseEntity = controller.uploadCsvFile(file, isGoldStandard);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(JsonFileUploadResponse.SUCCESS);
        verify(uploadCsvControllerHelperAsyncWrapper).acquireCsvData(eq(csv), eq(isGoldStandard),
                eq(USER_EMAIL_ADDRESS), eq(filePath));
    }
}
