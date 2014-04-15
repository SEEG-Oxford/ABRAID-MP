package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.SourceCodeManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for RepositoryController.
 * Copyright (c) 2014 University of Oxford
 */
public class RepositoryControllerTest {
    @Test
    public void setModelVersionRejectsInvalidVersion() throws Exception {
        // Arrange
        List<String> invalidUrls = Arrays.asList("", null);
        RepositoryController target = new RepositoryController(null, null);

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

        RepositoryController target = new RepositoryController(configurationService, sourceCodeManager);

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

        RepositoryController target = new RepositoryController(configurationService, sourceCodeManager);

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

        RepositoryController target = new RepositoryController(configurationService, sourceCodeManager);

        // Act
        ResponseEntity result = target.setModelVersion(expectedVersion);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(configurationService, times(1)).setModelRepositoryVersion(expectedVersion);
    }


    @Test
    public void syncRepositoryRejectsInvalidRepositoryUrl() throws Exception {
        // Arrange
        List<String> invalidUrls = Arrays.asList("", null);
        RepositoryController target = new RepositoryController(null, null);

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

        RepositoryController target = new RepositoryController(configurationService, sourceCodeManager);
        String url = "urlThatWontClone";

        // Act
        ResponseEntity result = target.syncRepository(url);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // should try the new url
        verify(configurationService, times(1)).setModelRepositoryUrl(url);
        // which will fail
        verify(sourceCodeManager, times(1)).updateRepository();
        // so the url should be reset
        verify(configurationService, times(1)).setModelRepositoryUrl("initialValue");
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

        RepositoryController target = new RepositoryController(configurationService, sourceCodeManager);
        String url = "urlThatWontRetrieveTags";

        // Act
        ResponseEntity result = target.syncRepository(url);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // should try the new url
        verify(configurationService, times(1)).setModelRepositoryUrl(url);
        // which will fail
        verify(sourceCodeManager, times(1)).updateRepository();
        // so the url should be reset
        verify(configurationService, times(1)).setModelRepositoryUrl("initialValue");
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

        RepositoryController target = new RepositoryController(configurationService, sourceCodeManager);
        String url = "urlThatWillWork";

        // Act
        ResponseEntity result = target.syncRepository(url);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(configurationService, times(1)).setModelRepositoryVersion("");
    }

    @Test
    public void syncRepositoryReturnsCorrectVersions() throws Exception {
        // Arrange
        List<String> expectedVersions = Arrays.asList("1", "2", "3");
        ConfigurationService configurationService = mock(ConfigurationService.class);
        setupGetAndSetForRepositoryURL(configurationService, "initialValue");
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        doReturn(expectedVersions).when(sourceCodeManager).getAvailableVersions();

        RepositoryController target = new RepositoryController(configurationService, sourceCodeManager);
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

        RepositoryController target = new RepositoryController(configurationService, sourceCodeManager);
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
