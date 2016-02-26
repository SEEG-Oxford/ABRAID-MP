package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonFileUploadResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonParentDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils;

import java.util.Arrays;
import java.util.List;

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
    private DiseaseService diseaseService;
    private UploadCsvControllerHelperAsyncWrapper uploadCsvControllerHelperAsyncWrapper;
    private UploadCsvController controller;

    private static final String USER_EMAIL_ADDRESS = "user@test.com";
    private DiseaseGroup diseaseGroup;
    private AbraidJsonObjectMapper objectMapper;

    @Before
    public void setUp() {
        currentUserService = mock(CurrentUserService.class);
        expertService = mock(ExpertService.class);
        diseaseService = mock(DiseaseService.class);
        diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseService.getDiseaseGroupById(87)).thenReturn(diseaseGroup);
        objectMapper = mock(AbraidJsonObjectMapper.class);
        uploadCsvControllerHelperAsyncWrapper = mock(UploadCsvControllerHelperAsyncWrapper.class);
        controller = new UploadCsvController(
                currentUserService, expertService, diseaseService, objectMapper, uploadCsvControllerHelperAsyncWrapper);

        setUpCurrentUserService();
    }

    private void setUpCurrentUserService() {
        when(currentUserService.getCurrentUserId()).thenReturn(1);
        Expert expert = mock(Expert.class);
        when(expertService.getExpertById(1)).thenReturn(expert);
        when(expert.getEmail()).thenReturn(USER_EMAIL_ADDRESS);
    }

    @Test
    public void showCSVPageTemplatesCorrectly() throws Exception {
        // Arrange
        List<DiseaseGroup> diseaseGroups = Arrays.asList(createDisease(1, "a"), createDisease(2, "b"));
        when(diseaseService.getAllDiseaseGroups()).thenReturn(diseaseGroups);
        ArgumentCaptor<List<JsonParentDiseaseGroup>> captor = GeneralTestUtils.captorForListClass();
        when(objectMapper.writeValueAsString(captor.capture())).thenReturn("correctDiseaseGroups");

        // Act
        Model model = mock(Model.class);
        String template = controller.showCSVPage(model);

        // Assert
        assertThat(template).isEqualTo("tools/uploadcsv");
        verify(model).addAttribute("diseaseGroups", "correctDiseaseGroups");
        List<JsonParentDiseaseGroup> jsonDto = captor.getValue();
        assertThat(jsonDto.get(0).getId()).isEqualTo(1);
        assertThat(jsonDto.get(0).getName()).isEqualTo("a");
        assertThat(jsonDto.get(1).getId()).isEqualTo(2);
        assertThat(jsonDto.get(1).getName()).isEqualTo("b");
    }

    private DiseaseGroup createDisease(int id, String name) {
        DiseaseGroup mock = mock(DiseaseGroup.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        return mock;
    }

    @Test
    public void uploadCsvFileReturnsBadRequestIfFileIsNull() throws Exception {
        // Act
        ResponseEntity<JsonFileUploadResponse> responseEntity = controller.uploadCsvFile(null, false, false, 87);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(JsonFileUploadResponse.FAIL);
    }

    @Test
    public void uploadCsvFileReturnsBadRequestIfFileIsEmpty() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile("filename", new byte[] {});

        // Act
        ResponseEntity<JsonFileUploadResponse> responseEntity = controller.uploadCsvFile(file, false, false, 87);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(JsonFileUploadResponse.FAIL);
    }

    @Test
    public void uploadCsvFileReturnsBadRequestBiasDataSetWithInvalidDiseaseID() throws Exception {
        // Arrange
        byte[] csv = "Test CSV".getBytes();
        String filename = "filename.csv";
        String filePath = "/path/to/filename.csv";
        MultipartFile file = new MockMultipartFile(filename, filePath, MediaType.APPLICATION_OCTET_STREAM_VALUE, csv);

        // Act
        ResponseEntity<JsonFileUploadResponse> responseEntity = controller.uploadCsvFile(file, true, false, -87);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(JsonFileUploadResponse.FAIL);
    }

    @Test
    public void uploadCsvFileReturnsOKIfSuccessful() throws Exception {
        uploadCsvFileSuccessful(false, false, 87);
    }

    @Test
    public void uploadGoldStandardCsvFileReturnsOKIfSuccessful() throws Exception {
        uploadCsvFileSuccessful(false, true, 87);
    }

    @Test
    public void uploadBiasCsvFileReturnsOKIfSuccessful() throws Exception {
        uploadCsvFileSuccessful(true, false, 87);
    }

    private void uploadCsvFileSuccessful(boolean isBias, boolean isGoldStandard, int biasDisease) throws Exception {
        // Arrange
        byte[] csv = "Test CSV".getBytes();
        String filename = "filename.csv";
        String filePath = "/path/to/filename.csv";
        MultipartFile file = new MockMultipartFile(filename, filePath, MediaType.APPLICATION_OCTET_STREAM_VALUE, csv);
        DiseaseGroup expectedDisease = isBias ? diseaseGroup : null;

        // Act
        ResponseEntity<JsonFileUploadResponse> responseEntity = controller.uploadCsvFile(file, isBias, isGoldStandard, biasDisease);

        // Assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo(JsonFileUploadResponse.SUCCESS);
        verify(uploadCsvControllerHelperAsyncWrapper).acquireCsvData(eq(csv), eq(isBias), eq(isGoldStandard),
                eq(expectedDisease), eq(USER_EMAIL_ADDRESS), eq(filePath));
    }
}
