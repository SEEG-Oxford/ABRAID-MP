package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.geoserver;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public class GeoserverRestService {
    private static final String CREATE_COVERAGE_URL = "%s/rest/workspaces/abraid/coveragestores/%s/external.geotiff?configure=none&update=overwrite";
    private static final String CONFIGURE_COVERAGE_URL = "%s/rest/workspaces/abraid/coveragestores/%s/coverages.xml";
    private static final String CONFIGURE_LAYER_URL = "%s/rest/layers/abraid:%s";

    private final WebServiceClient http;
    private final String geoserverPath;
    private static final String ASCII = "US-ASCII";
    private final Charset CHARSET = Charset.forName(ASCII);

    public GeoserverRestService(WebServiceClient webServiceClient, String geoserverPath) {
        this.http = webServiceClient;
        this.geoserverPath = geoserverPath;
    }

    public void publishGeoTIFF(File file) throws IOException, TemplateException {
        String basename = extractBasename(file);
        Map<String, String> data = new HashMap<>();
        data.put("basename", basename);

        createCoverage(file, basename);

        configureCoverage(basename, data);

        createLayer(basename, data);
    }

    private void createLayer(String basename, Map<String, String> data) throws IOException, TemplateException {
        String layerBody = renderTemplate("layer.xml.ftl", data);
        http.makePutRequestWithXml(
                String.format(CONFIGURE_LAYER_URL, geoserverPath, basename),
                layerBody.getBytes(CHARSET));
    }

    private void configureCoverage(String basename, Map<String, String> data) throws IOException, TemplateException {
        String coverageBody = renderTemplate("coverage.xml.ftl", data);
        http.makePostRequestWithXml(
                String.format(CONFIGURE_COVERAGE_URL, geoserverPath, basename),
                coverageBody.getBytes(CHARSET));
    }

    private void createCoverage(File file, String basename) {
        String coveragePath = "file://" + file.getAbsolutePath();
        http.makePutRequest(
                String.format(CREATE_COVERAGE_URL, geoserverPath, basename),
                coveragePath.getBytes(CHARSET));
    }

    private String renderTemplate(String templateName, Map<String, String> data) throws IOException, TemplateException {
        Configuration config = new Configuration();
        config.setClassForTemplateLoading(this.getClass(), "");
        final Template template = config.getTemplate(templateName);
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
