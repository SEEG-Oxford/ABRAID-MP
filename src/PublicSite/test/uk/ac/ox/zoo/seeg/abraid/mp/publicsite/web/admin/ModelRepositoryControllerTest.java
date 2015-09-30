package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.SourceCodeManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for ModelRepositoryController.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRepositoryControllerTest {
    @Test
    public void showPageReturnsCorrectFreemarkerTemplateName() {
        // Arrange
        ModelRepositoryController target = new ModelRepositoryController(mock(ConfigurationService.class), mock(SourceCodeManager.class));

        // Act
        String result = target.showPage(mock(Model.class));

        // Assert
        assertThat(result).isEqualTo("admin/model");
    }

    @Test
    public void showPageSetsCorrectModelData() throws Exception {
        // Arrange
        String expectedUrl = "foo1";
        String expectedVersion = "foo2";
        List<String> expectedVersions = Arrays.asList("1", "2", "3");

        ConfigurationService configurationService = mock(ConfigurationService.class);
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);

        when(configurationService.getModelRepositoryUrl()).thenReturn(expectedUrl);
        when(configurationService.getModelRepositoryVersion()).thenReturn(expectedVersion);
        when(sourceCodeManager.getAvailableVersions()).thenReturn(expectedVersions);

        Model model = mock(Model.class);
        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);

        // Act
        target.showPage(model);

        // Assert
        verify(model).addAttribute("repository_url", expectedUrl);
        verify(model).addAttribute("model_version", expectedVersion);
        verify(model).addAttribute("available_versions", expectedVersions);
    }

    @Test
    public void showPageSetsEmptyVersionListIfRepositoryCheckFails() throws Exception {
        // Arrange
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        when(sourceCodeManager.getAvailableVersions()).thenThrow(new IOException());
        Model model = mock(Model.class);
        ModelRepositoryController target = new ModelRepositoryController(mock(ConfigurationService.class), sourceCodeManager);

        // Act
        target.showPage(model);

        // Assert
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("available_versions"), captor.capture());
        assertThat(captor.getValue()).hasSize(0);
    }

    @Test
    public void setModelVersionRejectsInvalidVersion() throws Exception {
        // Arrange
        List<String> invalidUrls = Arrays.asList("", null);
        ModelRepositoryController target = new ModelRepositoryController(null, null);

        for (String invalidUrl : invalidUrls) {
            // Act
            ResponseEntity result = target.setModelVersion(invalidUrl);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void setModelVersionFailsIfVersionsCanNotBeRetrieved() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        doThrow(new IOException()).when(sourceCodeManager).getAvailableVersions();

        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);

        // Act
        ResponseEntity result = target.setModelVersion("version");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void setModelVersionRejectsVersionsNotInRepository() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        doReturn(Arrays.asList("1", "2", "3")).when(sourceCodeManager).getAvailableVersions();

        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);

        // Act
        ResponseEntity result = target.setModelVersion("4");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void setModelVersionAcceptsValidVersion() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        String expectedVersion = "3";
        doReturn(Arrays.asList("1", "2", expectedVersion)).when(sourceCodeManager).getAvailableVersions();

        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);

        // Act
        ResponseEntity result = target.setModelVersion(expectedVersion);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService).setModelRepositoryVersion(expectedVersion);
    }

    @Test
    public void setModelVersionAcceptsCurrentVersionButSkipsLogic() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        String expectedVersion = "3";
        when(configurationService.getModelRepositoryVersion()).thenReturn(expectedVersion);

        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);

        // Act
        ResponseEntity result = target.setModelVersion(expectedVersion);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(0)).setModelRepositoryVersion(expectedVersion);
    }


    @Test
    public void syncRepositoryRejectsInvalidRepositoryUrl() throws Exception {
        // Arrange
        List<String> invalidUrls = Arrays.asList("", null);
        ModelRepositoryController target = new ModelRepositoryController(null, null);

        for (String invalidUrl : invalidUrls) {
            // Act
            ResponseEntity result = target.syncRepository(invalidUrl);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void syncRepositoryTriesURLButRejectsIfCloneFails() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        setupGetAndSetForRepositoryURL(configurationService, "initialValue");
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        doThrow(new IOException()).when(sourceCodeManager).updateRepository();

        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);
        String url = "urlThatWontClone";

        // Act
        ResponseEntity result = target.syncRepository(url);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // should try the new url
        verify(configurationService).setModelRepositoryUrl(url);
        // which will fail
        verify(sourceCodeManager).updateRepository();
        // so the url should be reset
        verify(configurationService).setModelRepositoryUrl("initialValue");
        // leaving it at the initial value
        assertThat(configurationService.getModelRepositoryUrl()).isEqualTo("initialValue");
    }

    @Test
    public void syncRepositoryTriesURLButRejectsIfTagRetrievalFails() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        setupGetAndSetForRepositoryURL(configurationService, "initialValue");
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        doThrow(new IOException()).when(sourceCodeManager).getAvailableVersions();

        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);
        String url = "urlThatWontRetrieveTags";

        // Act
        ResponseEntity result = target.syncRepository(url);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // should try the new url
        verify(configurationService).setModelRepositoryUrl(url);
        // which will fail
        verify(sourceCodeManager).updateRepository();
        // so the url should be reset
        verify(configurationService).setModelRepositoryUrl("initialValue");
        // leaving it at the initial value
        assertThat(configurationService.getModelRepositoryUrl()).isEqualTo("initialValue");
    }

    @Test
    public void syncRepositoryClearsVersionIfUrlChanges() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        setupGetAndSetForRepositoryURL(configurationService, "initialValue");
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        doReturn(new ArrayList<String>()).when(sourceCodeManager).getAvailableVersions();

        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);
        String url = "urlThatWillWork";

        // Act
        ResponseEntity result = target.syncRepository(url);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(configurationService).setModelRepositoryVersion("");
    }

    @Test
    public void syncRepositoryReturnsCorrectVersions() throws Exception {
        // Arrange
        List<String> expectedVersions = Arrays.asList("1", "2", "3");
        ConfigurationService configurationService = mock(ConfigurationService.class);
        setupGetAndSetForRepositoryURL(configurationService, "initialValue");
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        doReturn(expectedVersions).when(sourceCodeManager).getAvailableVersions();

        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);
        String url = "urlThatWillWork";

        // Act
        ResponseEntity result = target.syncRepository(url);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedVersions);
    }

    @Test
    public void syncRepositoryDoesNotClearVersionIfUrlHasNotChanged() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        setupGetAndSetForRepositoryURL(configurationService, "initialValue");
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        doReturn(new ArrayList<String>()).when(sourceCodeManager).getAvailableVersions();

        ModelRepositoryController target = new ModelRepositoryController(configurationService, sourceCodeManager);
        String url = "initialValue";

        // Act
        ResponseEntity result = target.syncRepository(url);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(configurationService, never()).setModelRepositoryVersion(anyString());
    }

    private String valueCache;

    private void setupGetAndSetForRepositoryURL(ConfigurationService configurationService, String initialValue) {
        doAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                return valueCache;
            }
        }).when(configurationService).getModelRepositoryUrl();

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                valueCache = (String) invocation.getArguments()[0];
                return null;
            }
        }).when(configurationService).setModelRepositoryUrl(anyString());

        valueCache = initialValue;
    }
}
