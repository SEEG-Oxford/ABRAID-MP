package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.geoserver;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * A facade for interacting with the GeoServer Configuration REST API.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoserverRestService {
    private static final String CREATE_COVERAGE_URL =
            "%s/rest/workspaces/abraid/coveragestores/%s/external.geotiff?configure=none&update=overwrite";
    private static final String CONFIGURE_COVERAGE_URL =
            "%s/rest/workspaces/abraid/coveragestores/%s/coverages.xml";
    private static final String CONFIGURE_LAYER_URL =
            "%s/rest/layers/abraid:%s";

    private static final String COVERAGE_TEMPLATE = "coverage.xml.ftl";
    private static final String LAYER_XML_FTL = "layer.xml.ftl";
    private static final String TEMPLATE_DATA_KEY = "basename";

    private final WebServiceClient http;
    private final String geoserverPath;
    private final Configuration templateConfig;

    public GeoserverRestService(WebServiceClient webServiceClient, String geoserverUrl, Configuration templateConfig) {
        this.http = webServiceClient;
        this.geoserverPath = geoserverUrl;
        this.templateConfig = templateConfig;
        this.templateConfig.setClassForTemplateLoading(this.getClass(), "");
    }

    /**
     * Creates a new coverage store and associated layer in GeoServer, linked to the specified GeoTIFF file on disk.
     * The name of the file will be used as the layer/store name.
     * @param file The GeoTIFF to publish.
     * @throws IOException Thrown if there is an issue accessing the GeoTIFF, or interacting with the GeoServer API.
     * @throws TemplateException Thrown if there is an issue generating the data to send to the GeoServer API.
     */
    public void publishGeoTIFF(File file) throws IOException, TemplateException {
        String basename = extractBasename(file);
        Map<String, String> data = new HashMap<>();
        data.put(TEMPLATE_DATA_KEY, basename);

        createCoverage(file, basename);

        configureCoverage(basename, data);

        createLayer(basename, data);
    }

    private void createCoverage(File file, String basename) {
        String coveragePath = "file://" + file.getAbsolutePath();
        http.makePutRequest(
                String.format(CREATE_COVERAGE_URL, geoserverPath, basename),
                coveragePath);
    }

    private void configureCoverage(String basename, Map<String, String> data) throws IOException, TemplateException {
        String coverageBody = renderTemplate(COVERAGE_TEMPLATE, data);
        http.makePostRequestWithXML(
                String.format(CONFIGURE_COVERAGE_URL, geoserverPath, basename),
                coverageBody);
    }

    private void createLayer(String basename, Map<String, String> data) throws IOException, TemplateException {
        String layerBody = renderTemplate(LAYER_XML_FTL, data);
        http.makePutRequestWithXML(
                String.format(CONFIGURE_LAYER_URL, geoserverPath, basename),
                layerBody);
    }

    private String renderTemplate(String templateName, Map<String, String> data) throws IOException, TemplateException {
        final Template template = templateConfig.getTemplate(templateName);
        Writer bodyWriter = new StringWriter();
        template.process(data, bodyWriter);
        return bodyWriter.toString();
    }

    private String extractBasename(File file) {
        String basename = file.getName();
        basename = basename.substring(0, basename.lastIndexOf('.'));
        return basename;
    }
}
