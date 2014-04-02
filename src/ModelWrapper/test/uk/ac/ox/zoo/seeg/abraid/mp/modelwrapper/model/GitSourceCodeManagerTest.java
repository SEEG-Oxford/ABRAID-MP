package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for GitSourceCodeManager.
 * Copyright (c) 2014 University of Oxford
 */
public class GitSourceCodeManagerTest {
    @Rule
    public TemporaryFolder workingDir = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Rule
    public TemporaryFolder cacheDir = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier


    @Test
    public void updateRepositoryClonesRepositoryIfNeeded() throws Exception {
        // Arrange
        SourceCodeManager target = setupSourceCodeManager();

        // Assert
        File baseRepoCacheDir = Paths.get(cacheDir.getRoot().toString(), "repos").toFile();
        assertThat(baseRepoCacheDir.listFiles()).hasSize(1);
        File repoCacheDir = baseRepoCacheDir.listFiles()[0];
        assertThat(getGitLogMessages(repoCacheDir)).containsOnly("foobar");
    }

    @Test
    public void updateRepositoryPullsRepositoryIfNeeded() throws Exception {
        // Arrange
        SourceCodeManager target = setupSourceCodeManager();
        File repoCacheDir = getRepoCloneDir();

        // Act
        addCommitToRepo(repoCacheDir, "expected message");
        target.updateRepository();

        // Assert
        assertThat(getGitLogMessages(repoCacheDir)).containsOnly("foobar", "expected message");
    }

    @Test
    public void getAvailableVersionsReturnsTags() throws Exception {
        // Arrange
        SourceCodeManager target = setupSourceCodeManager();
        File repoCacheDir = getRepoCloneDir();

        // Act
        addTagToRepo(repoCacheDir, "expected_tag");
        List<String> result = target.getAvailableVersions();

        // Assert
        assertThat(result).containsOnly("expected_tag");
    }


    @Test
    public void provisionVersionCopiesCode() throws Exception {
        // Arrange
        SourceCodeManager target = setupSourceCodeManager();
        File repoCacheDir = getRepoCloneDir();
        File targetDir = workingDir.newFolder();

        // Act
        addTagToRepo(repoCacheDir, "expected_tag");
        target.provisionVersion("expected_tag", targetDir);

        // Assert
        assertThat(targetDir.listFiles()).hasSize(1);
        assertThat(Paths.get(targetDir.toString(), ".git").toFile()).doesNotExist();
    }

    @Test
    public void provisionVersionRejectsInvalidVersions() throws Exception {
        // Arrange
        SourceCodeManager target = setupSourceCodeManager();
        File repoCacheDir = getRepoCloneDir();
        File targetDir = workingDir.newFolder();

        // Act
        catchException(target).provisionVersion("wrong_tag", targetDir);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class).hasMessage("No such version");
    }

    private ConfigurationService setUpFakeRepo() throws Exception {
        Repository repo = createGitRepository();
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCacheDirectory()).thenReturn(cacheDir.getRoot().toString());
        when(configurationService.getModelRepositoryUrl()).thenReturn(repo.getDirectory().getParent());
        return configurationService;
    }

    private Repository createGitRepository() throws Exception {
        File dir = workingDir.newFolder();

        // Init repo
        File gitDir = Paths.get(dir.toString(), ".git").toFile();
        Repository repo = FileRepositoryBuilder.create(gitDir);
        repo.create();

        addCommitToRepo(repo.getDirectory().getParentFile(), "foobar");

        return repo;
    }

    private void addCommitToRepo(File repo, String message) throws Exception {
        Git git = getGitFacade(repo);

        File randomFile = Paths.get(repo.toString(), UUID.randomUUID().toString().substring(0, 8)).toFile();
        FileUtils.writeStringToFile(randomFile, UUID.randomUUID().toString());

        git.add().addFilepattern(randomFile.getName()).call();
        git.commit().setMessage(message).call();
    }

    private List<String> getGitLogMessages(File repo) throws Exception {
        Git git = getGitFacade(repo);

        List<String> log = new ArrayList<>();
        for (RevCommit commit : git.log().call()) {
            log.add(commit.getFullMessage());
        }
        return log;
    }

    private void addTagToRepo(File repo, String id) throws Exception {
        Git git = getGitFacade(repo);

        git.tag().setName(id).call();
    }

    private Git getGitFacade(File repo) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(Paths.get(repo.toString(), ".git").toFile())
                .readEnvironment()
                .findGitDir()
                .build();
        return new Git(repository);
    }

    private File getRepoCloneDir() {
        return Paths.get(cacheDir.getRoot().toString(), "repos").toFile().listFiles()[0];
    }

    private SourceCodeManager setupSourceCodeManager() throws Exception {
        ConfigurationService configurationService = setUpFakeRepo();
        SourceCodeManager target = new GitSourceCodeManager(configurationService);
        target.updateRepository(); // initial clone
        return target;
    }

}
