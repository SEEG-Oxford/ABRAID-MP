package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import ch.lambdaj.function.convert.Converter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.CovariateObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsIn.isIn;

/**
 * Service class for configuration data.
 * Copyright (c) 2014 University of Oxford
 */
public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationServiceImpl.class);
    private static final String LOG_LOADING_CONFIGURATION_FILE = "Loading configuration file %s";
    private static final String LOG_UPDATING_AUTH_CONFIGURATION = "Updating auth configuration: %s %s";
    private static final String LOG_UPDATING_REPOSITORY_URL_CONFIGURATION = "Updating repository url configuration: %s";
    private static final String LOG_UPDATING_VERSION_CONFIGURATION = "Updating repository version configuration: %s";
    private static final String LOG_UPDATING_R_PATH = "Updating R path configuration: %s";
    private static final String LOG_UPDATING_RUN_DURATION = "Updating max run duration configuration: %s";
    private static final String LOG_UPDATING_COVARIATE_DIR = "Updating covariate dir configuration: %s";

    private static final String DEFAULT_LINUX_CACHE_DIR = "/var/lib/abraid/modelwrapper";
    private static final String DEFAULT_WINDOWS_CACHE_DIR = System.getenv("LOCALAPPDATA") + "\\abraid\\modelwrapper";
    private static final String DEFAULT_COVARIATE_SUB_DIR = "covariates";
    private static final String DEFAULT_LINUX_R_PATH = "/usr/bin/R";
    private static final String DEFAULT_WINDOWS_R_PATH = System.getenv("R_HOME") + "\\bin\\R.exe";

    private static final String USERNAME_KEY = "auth.username";
    private static final String PASSWORD_KEY = "auth.password_hash";
    private static final String CACHE_DIR_KEY = "cache.data.dir";
    private static final String MODEL_REPOSITORY_KEY = "model.repo.url";
    private static final String MODEL_VERSION_KEY = "model.repo.version";
    private static final String R_EXECUTABLE_KEY = "r.executable.path";
    private static final String R_MAX_DURATION_KEY = "r.max.duration";
    private static final String COVARIATE_DIRECTORY_KEY = "covariate.dir";
    private static final String COVARIATE_JSON_FILE = "abraid.json";

    private final FileConfiguration basicProperties;
    private final File defaultCovariateConfig;
    private final OSChecker osChecker;

    public ConfigurationServiceImpl(File basicProperties, File defaultCovariateConfig, OSChecker osChecker)
            throws ConfigurationException {
        LOGGER.info(String.format(LOG_LOADING_CONFIGURATION_FILE, basicProperties.toString()));
        this.basicProperties = new PropertiesConfiguration(basicProperties);
        this.basicProperties.setAutoSave(true);
        this.defaultCovariateConfig = defaultCovariateConfig;
        this.osChecker = osChecker;
    }

    /**
     * Updates the current modelwrapper authentication details.
     * @param username The new username.
     * @param passwordHash The bcrypt hash of the new password.
     */
    @Override
    public void setAuthenticationDetails(String username, String passwordHash) {
        LOGGER.info(String.format(LOG_UPDATING_AUTH_CONFIGURATION, username, passwordHash));
        basicProperties.setProperty(USERNAME_KEY, username);
        basicProperties.setProperty(PASSWORD_KEY, passwordHash);
    }

    /**
     * Gets the current modelwrapper authentication username.
     * @return The username
     */
    @Override
    public String getAuthenticationUsername() {
        return basicProperties.getString(USERNAME_KEY);
    }

    /**
     * Gets the current modelwrapper authentication password hash.
     * @return The password hash.
     */
    @Override
    public String getAuthenticationPasswordHash() {
        return basicProperties.getString(PASSWORD_KEY);
    }

    /**
     * Get the current remote repository url to use as a source for the model.
     * @return The repository url.
     */
    @Override
    public String getModelRepositoryUrl() {
        return basicProperties.getString(MODEL_REPOSITORY_KEY);
    }

    /**
     * Set the current remote repository url to use as a source for the model.
     * @param repositoryUrl The repository url.
     */
    @Override
    public void setModelRepositoryUrl(String repositoryUrl) {
        LOGGER.info(String.format(LOG_UPDATING_REPOSITORY_URL_CONFIGURATION, repositoryUrl));
        basicProperties.setProperty(MODEL_REPOSITORY_KEY, repositoryUrl);
    }

    /**
     * Get the current model version to use to run the model.
     * @return The model version.
     */
    @Override
    public String getModelRepositoryVersion() {
        return basicProperties.getString(MODEL_VERSION_KEY);
    }

    /**
     * Set the current model version to use to run the model.
     * @param version The model version.
     */
    @Override
    public void setModelRepositoryVersion(String version) {
        LOGGER.info(String.format(LOG_UPDATING_VERSION_CONFIGURATION, version));
        basicProperties.setProperty(MODEL_VERSION_KEY, version);
    }

    /**
     * Gets the current directory to use for data caching.
     * @return The cache directory.
     */
    @Override
    public String getCacheDirectory() {
        String defaultDir = osChecker.isWindows() ? DEFAULT_WINDOWS_CACHE_DIR : DEFAULT_LINUX_CACHE_DIR;
        return basicProperties.getString(CACHE_DIR_KEY, defaultDir);
    }

    /**
     * Gets the current path to the R executable binary.
     * @return The R path.
     * @throws ConfigurationException When a value for the R path is not set and R is not present in default locations.
     */
    @Override
    public String getRExecutablePath() throws ConfigurationException {
        if (basicProperties.containsKey(R_EXECUTABLE_KEY)) {
            return basicProperties.getString(R_EXECUTABLE_KEY);
        } else {
            return findDefaultR();
        }
    }

    /**
     * Sets the current path to the R executable binary.
     * @param path The R path.
     */
    @Override
    public void setRExecutablePath(String path) {
        LOGGER.info(String.format(LOG_UPDATING_R_PATH, path));
        basicProperties.setProperty(R_EXECUTABLE_KEY, path);
    }

    /**
     * Gets the current maximum model run duration.
     * @return The max duration.
     */
    @Override
    public int getMaxModelRunDuration() {
        return basicProperties.getInt(R_MAX_DURATION_KEY, Integer.MAX_VALUE);
    }

    /**
     * Sets the current maximum model run duration.
     * @param value The max duration.
     */
    @Override
    public void setMaxModelRunDuration(int value) {
        LOGGER.info(String.format(LOG_UPDATING_RUN_DURATION, value));
        basicProperties.setProperty(R_MAX_DURATION_KEY, value);
    }

    /**
     * Gets the current directory for covariate files.
     * @return The directory for covariate files.
     */
    @Override
    public String getCovariateDirectory() {
        Path defaultDirPath = Paths.get(getCacheDirectory(), DEFAULT_COVARIATE_SUB_DIR);
        String defaultDir = defaultDirPath.toFile().getAbsolutePath();
        return basicProperties.getString(COVARIATE_DIRECTORY_KEY, defaultDir);
    }

    /**
     * Sets the current directory for covariate files.
     * @param path The directory for covariate files.
     */
    @Override
    public void setCovariateDirectory(String path) {
        LOGGER.info(String.format(LOG_UPDATING_COVARIATE_DIR, path));
        basicProperties.setProperty(COVARIATE_DIRECTORY_KEY, path);
    }

    /**
     * Gets the current covariate configuration.
     * @return The covariate configuration.
     * @throws java.io.IOException throw if the configuration json file can not be parsed correctly.
     */
    @Override
    public JsonCovariateConfiguration getCovariateConfiguration() throws IOException {
        String covariateDirectory = getCovariateDirectory();
        Path configPath = Paths.get(covariateDirectory, COVARIATE_JSON_FILE);
        File configFile = configPath.toFile();

        ObjectMapper jsonConverter = new CovariateObjectMapper();
        JsonCovariateConfiguration jsonCovariateConfiguration;
        if (configFile.exists()) {
            jsonCovariateConfiguration =
                    jsonConverter.readValue(configFile, JsonCovariateConfiguration.class);
        } else {
            jsonCovariateConfiguration =
                    jsonConverter.readValue(defaultCovariateConfig, JsonCovariateConfiguration.class);
        }

        appendNewCovariateFiles(jsonCovariateConfiguration, covariateDirectory);
        return jsonCovariateConfiguration;
    }

    private void appendNewCovariateFiles(
            JsonCovariateConfiguration jsonCovariateConfiguration, String covariateDirectoryLocation) {
        final Path covariateDirectoryPath = Paths.get(covariateDirectoryLocation);
        File covariateDirectory = covariateDirectoryPath.toFile();
        if (covariateDirectory.exists()) {
            Collection<File> files = FileUtils.listFiles(covariateDirectory, null, true);
            List<JsonCovariateFile> knownFiles = jsonCovariateConfiguration.getFiles();
            Collection<String> knownPaths = extract(knownFiles, on(JsonCovariateFile.class).getPath());

            files = filter(having(on(File.class).getPath(), not(isIn(knownPaths))), files);

            knownFiles.addAll(convert(files, new Converter<File, JsonCovariateFile>() {
                public JsonCovariateFile convert(File file) {
                    Path subPath = covariateDirectoryPath.relativize(file.toPath());
                    String normalizedSubPath = FilenameUtils.separatorsToUnix(subPath.toString());
                    return new JsonCovariateFile(normalizedSubPath, "", null, false, new ArrayList<Integer>());
                }
            }));
        }
    }

    private String findDefaultR() throws ConfigurationException {
        String rPath = osChecker.isWindows() ? DEFAULT_WINDOWS_R_PATH : DEFAULT_LINUX_R_PATH;
        File r = Paths.get(rPath).toFile();
        if (r.exists() && r.canExecute()) {
            return r.getAbsolutePath();
        } else {
            throw new ConfigurationException("Could not find R.");
        }
    }
}

