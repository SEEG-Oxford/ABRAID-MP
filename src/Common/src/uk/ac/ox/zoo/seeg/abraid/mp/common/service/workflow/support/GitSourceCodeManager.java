package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.util.DigestUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ConfigurationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;

import static ch.lambdaj.Lambda.convert;

/**
 * Provides interaction with a GIT VCS repository containing the model code.
 * Copyright (c) 2014 University of Oxford
 */
public class GitSourceCodeManager implements SourceCodeManager {
    private static final Logger LOGGER = Logger.getLogger(GitSourceCodeManager.class);
    private static final String LOG_ATTEMPTING_CLONE_REPOSITORY = "Attempting to clone repository to local cache: %s";
    private static final String LOG_RETRIEVING_VERSIONS = "Retrieving versions from local repository cache";
    private static final String LOG_PROVISIONING_MODEL_CODE = "Provisioning model code '%s' at %s";
    private static final String LOG_UPDATING_LOCAL_REPOSITORY_CACHE = "Updating local repository cache";
    private static final String LOG_RETURNING_TO_HEAD_OF_MASTER = "Returning to HEAD of master";
    private static final String LOG_COPYING_MODEL_SOURCE_FILES = "Copying model source files";
    private static final String LOG_CHECKING_OUT_REPO_VERSION = "Checking out repo version ";
    private static final String LOG_PROVISIONING_FAILED = "Provisioning model code failed as version not in repository";
    private static final String LOG_NOT_A_POSIX_FILE_SYSTEM = "Not a posix file system";

    private static final String GIT_CONFIG_DIRECTORY = ".git";
    private static final String MASTER_BRANCH_NAME = "master";
    private static final int FILE_NAME_MAX_URL_LENGTH = 200;
    private static final String FILE_NAME_INVALID_CHARS = "\\W+";
    private static final String TAG_PREFIX = "refs/tags/";

    private ConfigurationService configurationService;
    private String baseRepositoryCachePath;

    public GitSourceCodeManager(ConfigurationService configurationService, String repositoryDirectory) {
        this.configurationService = configurationService;
        baseRepositoryCachePath = repositoryDirectory;
        try {
            updateRepository();
        } catch (Exception e) {
            LOGGER.warn("Initial update of repo failed - it might not be configured yet.");
        }
    }

    /**
     * Copies a specific version of the source code into the workspace.
     * @param targetDirectory The directory into which the source code should be provisioned.
     * @throws IllegalArgumentException Thrown if the specified version is not found in the repository.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    @Override
    public void provision(File targetDirectory)
            throws IllegalArgumentException, IOException, UnsupportedOperationException {
        synchronized (GitSourceCodeManager.class) {
            String versionIdentifier = configurationService.getModelRepositoryVersion();
            LOGGER.info(String.format(LOG_PROVISIONING_MODEL_CODE, versionIdentifier, targetDirectory));
            try {
                List<String> tags = getAvailableVersions();
                if (!tags.contains(versionIdentifier)) {
                    LOGGER.warn(LOG_PROVISIONING_FAILED);
                    throw new IllegalArgumentException("No such version");
                }
                Git repository = openRepository();

                LOGGER.info(LOG_CHECKING_OUT_REPO_VERSION + versionIdentifier);
                repository.checkout().setName(versionIdentifier).call();

                LOGGER.info(LOG_COPYING_MODEL_SOURCE_FILES);
                FileUtils.copyDirectory(
                        getRepositoryDirectory().toFile(),
                        targetDirectory,
                        new NotFileFilter(new AndFileFilter(
                                new NameFileFilter(GIT_CONFIG_DIRECTORY),
                                DirectoryFileFilter.DIRECTORY)));

                LOGGER.info(LOG_RETURNING_TO_HEAD_OF_MASTER);
                repository.checkout().setName(MASTER_BRANCH_NAME).call();
            } catch (GitAPIException e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    /**
     * Synchronise the local repository cache with the remote repository url.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    @Override
    public void updateRepository() throws IOException, UnsupportedOperationException {
        synchronized (GitSourceCodeManager.class) {
            try {
                if (!Files.exists(getRepositoryDirectory())) {
                    cloneRepository();
                } else {
                    LOGGER.info(LOG_UPDATING_LOCAL_REPOSITORY_CACHE);
                    openRepository().pull().call();
                }
                checkRepositoryFilePermissions();
            } catch (GitAPIException e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    private void checkRepositoryFilePermissions() throws IOException {
        try {
            Set<PosixFilePermission> filePerms = PosixFilePermissions.fromString("rwxrwxr--"); // 664
            Set<PosixFilePermission> dirPerms = PosixFilePermissions.fromString("rwxrwxr-x"); // 665

            Path dir = getRepositoryDirectory();
            Collection<File> files = FileUtils.listFiles(dir.toFile(), null, true);
            for (File file : files) {
                Files.setPosixFilePermissions(file.toPath(), file.isDirectory() ? dirPerms : filePerms);
            }
        } catch (UnsupportedOperationException e) {
            LOGGER.debug(LOG_NOT_A_POSIX_FILE_SYSTEM);
        }
    }

    /**
     * Lists the package versions found in the source code repository.
     * @return The versions list.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    @Override
    public List<String> getAvailableVersions() throws IOException, UnsupportedOperationException {
        synchronized (GitSourceCodeManager.class) {
            try {
                LOGGER.info(LOG_RETRIEVING_VERSIONS);
                List<String> versions = convert(openRepository().tagList().call(), new Converter<Ref, String>() {
                    public String convert(Ref ref) {
                        return ref.getName().substring(TAG_PREFIX.length());
                    }
                });

                Collections.reverse(versions);

                return versions;
            } catch (GitAPIException e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    /**
     * Creates a unique, valid, folder name from the repository url, which will be used to store the cloned repository.
     * @return The folder path.
     */
    private Path getRepositoryDirectory() {
        String repositoryUrl = configurationService.getModelRepositoryUrl();

        String hash = DigestUtils.md5DigestAsHex(repositoryUrl.getBytes(Charsets.US_ASCII));

        if (repositoryUrl.length() > FILE_NAME_MAX_URL_LENGTH) {
            repositoryUrl = repositoryUrl.substring(0, FILE_NAME_MAX_URL_LENGTH);
        }

        String folderName = String.format("%s - %s", repositoryUrl, hash).replaceAll(FILE_NAME_INVALID_CHARS, "_");
        return Paths.get(baseRepositoryCachePath, folderName);
    }

    /**
     * Opens the current repository and ensure master is the working branch.
     * @return The Git facade to interact with the repository.
     * @throws IOException
     * @throws GitAPIException
     */
    private Git openRepository() throws IOException, GitAPIException {
        Path repositoryDirectory = getRepositoryDirectory();
        Repository repository = (new FileRepositoryBuilder())
                .setGitDir(Paths.get(repositoryDirectory.toString(), GIT_CONFIG_DIRECTORY).toFile())
                .readEnvironment()
                .findGitDir()
                .build();
        Git git = new Git(repository);
        git.checkout().setName(MASTER_BRANCH_NAME).call();
        return git;
    }

    /**
     * Clones a new copy of the git repository.
     * @throws GitAPIException
     */
    private void cloneRepository() throws GitAPIException {
        String repositoryUrl = configurationService.getModelRepositoryUrl();

        LOGGER.info(String.format(LOG_ATTEMPTING_CLONE_REPOSITORY, repositoryUrl));

        Git.cloneRepository()
                .setURI(repositoryUrl)
                .setDirectory(getRepositoryDirectory().toFile())
                .call();
    }
}
