package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.geoserver;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for GeoserverRestService.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoserverRestServiceTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void publishGeoTIFFMakesCorrectSequenceOfHTTPCalls() throws Exception {
        // Arrange
        Configuration templateConfig = mock(Configuration.class);
        Template coverageTemplate = createMockTemplate("coverage.xml.ftl Template Rendered");
        when(templateConfig.getTemplate("coverage.xml.ftl")).thenReturn(coverageTemplate);
        Template layerTemplate = createMockTemplate("layer.xml.ftl Template Rendered");
        when(templateConfig.getTemplate("layer.xml.ftl")).thenReturn(layerTemplate);

        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        final String expectedBaseUrl = "url";
        GeoserverRestService target = new GeoserverRestService(webServiceClient, expectedBaseUrl, templateConfig);
        String fileName = "qwertyuiop";
        File file = testFolder.newFile(fileName + ".tif");

        // Act
        target.publishGeoTIFF(file);

        // Assert
        InOrder inOrder = inOrder(webServiceClient);
        inOrder.verify(webServiceClient).makePutRequest(
                expectedBaseUrl + "/rest/workspaces/abraid/coveragestores/" + fileName + "/external.geotiff?configure=none&update=overwrite",
                "file://" + file.getAbsolutePath());
        inOrder.verify(webServiceClient).makePostRequestWithXML(
                expectedBaseUrl + "/rest/workspaces/abraid/coveragestores/" + fileName + "/coverages.xml",
                "coverage.xml.ftl Template Rendered");
        inOrder.verify(webServiceClient).makePutRequestWithXML(
                expectedBaseUrl + "/rest/layers/abraid:" + fileName,
                "layer.xml.ftl Template Rendered");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void publishGeoTIFFTemplatesCoverageConfigurationCorrectly() throws Exception {
        // Arrange
        Configuration templateConfig = new Configuration();
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        GeoserverRestService target = new GeoserverRestService(webServiceClient, "url", templateConfig);
        String fileName = "qwertyuiop";
        File file = testFolder.newFile(fileName + ".tif");
        InputStream stream = target.getClass().getResourceAsStream("coverage.xml.ftl");
        String expectation = IOUtils.toString(stream, "UTF-8");

        // Act
        target.publishGeoTIFF(file);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(webServiceClient).makePostRequestWithXML(
                eq("url/rest/workspaces/abraid/coveragestores/" + fileName + "/coverages.xml"),
                argumentCaptor.capture());

        // Assert
        String templatedValue = argumentCaptor.getValue();
        assertThat(templatedValue).contains("<name>" + fileName + "</name>");
        assertThat(templatedValue).contains("<nativeName>" + fileName + "</nativeName>");
        assertThat(templatedValue).contains("<nativeCoverageName>" + fileName + "</nativeCoverageName>");
        assertThat(templatedValue).contains("<title>" + fileName + "</title>");
        assertThat(templatedValue).contains("<description>Generated from " + fileName + "</description>");

        assertThat(templatedValue).isEqualTo(expectation.replace("${basename}", fileName));
    }

    @Test
    public void publishGeoTIFFTemplatesLayerConfigurationCorrectly() throws Exception {
        // Arrange
        Configuration templateConfig = new Configuration();
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        GeoserverRestService target = new GeoserverRestService(webServiceClient, "url", templateConfig);
        String fileName = "qwertyuiop";
        File file = testFolder.newFile(fileName + ".tif");
        InputStream stream = target.getClass().getResourceAsStream("layer.xml.ftl");
        String expectation = IOUtils.toString(stream, "UTF-8");

        // Act
        target.publishGeoTIFF(file);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(webServiceClient).makePutRequestWithXML(
                eq("url/rest/layers/abraid:" + fileName),
                argumentCaptor.capture());

        // Assert
        String templatedValue = argumentCaptor.getValue();
        assertThat(templatedValue).contains("<name>" + fileName + "</name>");

        assertThat(templatedValue).isEqualTo(expectation.replace("${basename}", fileName));
    }

    private Template createMockTemplate(final String string) throws IOException, TemplateException {
        Template template = mock(Template.class);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((Writer) invocationOnMock.getArguments()[1]).append(string);
                return null;
            }
        }).when(template).process(any(Object.class), any(Writer.class));
        return template;
    }
}
