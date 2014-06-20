package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import ch.lambdaj.function.convert.Converter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;

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
     * @param runConfiguration The model run configuration.
     * @param workingDirectory The directory in which the script should be created.
     * @return The script file.
     * @throws IOException Thrown in response to issues creating the script file.
     */
    @Override
    public File generateScript(RunConfiguration runConfiguration, File workingDirectory)
            throws IOException {
        LOGGER.info(String.format(LOG_ADDING_SCRIPT_FILE_TO_WORKSPACE, workingDirectory.toString()));

        //Load template from source folder
        Template template = loadTemplate();

        // Build the data-model
        Map<String, Object> data = buildDataModel(runConfiguration);

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

    private static Map<String, Object> buildDataModel(RunConfiguration runConfiguration) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("run", runConfiguration.getRunName());
        data.put("dry_run", runConfiguration.getExecutionConfig().getDryRunFlag());
        data.put("max_cpu", runConfiguration.getExecutionConfig().getMaxCPUs());
        data.put("verbose", runConfiguration.getExecutionConfig().getVerboseFlag());
        data.put("model_version", runConfiguration.getCodeConfig().getModelVersion());
        data.put("occurrence_file", "data/occurrences.csv");
        data.put("extent_file", "data/extent.tif");
        data.put("admin1_file", escapeFilePathForR(runConfiguration.getAdminUnitConfig().getAdmin1RasterFile()));
        data.put("admin2_file", escapeFilePathForR(runConfiguration.getAdminUnitConfig().getAdmin2RasterFile()));

        final String covariatePathPrefix = runConfiguration.getCovariateConfig().getCovariateDirectory();
        Map<String,String> covariateFiles = runConfiguration.getCovariateConfig().getCovariateFiles();

        Collection<String> covariatePaths =
                convert(covariateFiles.keySet(), new Converter<String,String>() {
                    public String convert(String subpath) {
                        return escapeFilePathForR(Paths.get(covariatePathPrefix, subpath).toString());
                    }
                });
        data.put("covariate_files", covariatePaths);

        Collection<String> covariateNames = covariateFiles.values();
        data.put("covariate_names", covariateNames);

        return data;
    }

    private static String escapeFilePathForR(String path) {
        return path.replace("\\", "/");
    }
}
