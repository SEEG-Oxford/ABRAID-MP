package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ModellingConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A freemarker based ScriptGenerator to generate model run scripts based on a template file.
 * Copyright (c) 2014 University of Oxford
 */
public class FreemarkerScriptGenerator implements ScriptGenerator {
    private static final Logger LOGGER = Logger.getLogger(FreemarkerScriptGenerator.class);
    private static final String LOG_APPLYING_FREEMARKER_SCRIPT_TEMPLATE = "Applying freemarker script template";
    private static final String LOG_APPLYING_FREEMARKER_TEMPLATE_FAILED = "Applying freemarker script template failed!";
    private static final String LOG_ADDING_SCRIPT_FILE_TO_WORKSPACE = "Adding script file to workspace at %s";
    private static final String LOG_SCRIPT_FILE_ADDED_TO_WORKSPACE = "Script file added to workspace at %s";

    private static final String SCRIPT_FILE_NAME = "modelRun.R";
    private static final String TEMPLATE_FILE_NAME = "ModelRunTemplate.ftl";
    private static final String ASCII = "US-ASCII";

    /**
     * Creates a model run script file in the working directory for the given configuration.
     * @param modellingConfiguration The model run configuration.
     * @param workingDirectory The directory in which the script should be created.
     * @param diseaseGroup The disease group being modelled.
     * @param covariates The covariate file to use in the model.
     * @return The script file.
     * @throws IOException Thrown in response to issues creating the script file.
     */
    @Override
    public File generateScript(ModellingConfiguration modellingConfiguration, File workingDirectory,
                               DiseaseGroup diseaseGroup, Collection<CovariateFile> covariates)
            throws IOException {
        LOGGER.info(String.format(LOG_ADDING_SCRIPT_FILE_TO_WORKSPACE, workingDirectory.toString()));

        //Load template from source folder
        Template template = loadTemplate();

        // Build the data-model
        Map<String, Object> data = buildDataModel(
                modellingConfiguration, diseaseGroup.getId(), diseaseGroup.getModelMode(), covariates);

        // File output
        File scriptFile = applyTemplate(workingDirectory, template, data);

        LOGGER.info(String.format(LOG_SCRIPT_FILE_ADDED_TO_WORKSPACE, workingDirectory.toString()));
        return scriptFile;
    }

    private static File applyTemplate(File workingDirectory, Template template, Map<String, Object> data)
            throws IOException {
        File scriptFile = Paths.get(workingDirectory.getAbsolutePath(), SCRIPT_FILE_NAME).toFile();
        Writer fileWriter = null;
        try {
            LOGGER.info(LOG_APPLYING_FREEMARKER_SCRIPT_TEMPLATE);
            fileWriter = new OutputStreamWriter(new FileOutputStream(scriptFile), Charset.forName(ASCII).newEncoder());
            template.process(data, fileWriter);
            fileWriter.flush();
        } catch (TemplateException e) {
            LOGGER.warn(LOG_APPLYING_FREEMARKER_TEMPLATE_FAILED);
            throw new IOException("Either could not read the template file or the file was invalid.", e);
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
        return scriptFile;
    }

    private Template loadTemplate() throws IOException {
        //Freemarker configuration object
        Configuration config = new Configuration();

        config.setClassForTemplateLoading(this.getClass(), "");
        return config.getTemplate(TEMPLATE_FILE_NAME);
    }

    private static Map<String, Object> buildDataModel(ModellingConfiguration modellingConfiguration, int diseaseGroupId,
                                                      String mode, Collection<CovariateFile> covariates) {
        Map<String, Object> data = new HashMap<>();
        data.put("dry_run", modellingConfiguration.getDryRunFlag());
        data.put("max_cpu", modellingConfiguration.getMaxCPUs());
        data.put("verbose", modellingConfiguration.getVerboseFlag());
        data.put("disease", diseaseGroupId);
        data.put("mode", mode);
        data.put("covariates", covariates);
        return data;
    }
}
