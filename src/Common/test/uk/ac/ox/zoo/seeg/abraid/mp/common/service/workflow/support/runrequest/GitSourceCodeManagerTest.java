package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.GitSourceCodeManager;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.SourceCodeManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
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
        setupSourceCodeManager(setUpFakeRepo());

        // Assert
        File baseRepoCacheDir = Paths.get(cacheDir.getRoot().toString(), "repos").toFile();
        assertThat(baseRepoCacheDir.listFiles()).hasSize(1);
        File repoCacheDir = baseRepoCacheDir.listFiles()[0];
        assertThat(getGitLogMessages(repoCacheDir)).containsOnly("foobar");
        assertThat(FileUtils.readFileToString(Paths.get(repoCacheDir.getAbsolutePath(), ".git/config").toFile()))
                .contains("sharedRepository = group");
    }

    @Test
    public void updateRepositoryPullsRepositoryIfNeeded() throws Exception {
        // Arrange
        SourceCodeManager target = setupSourceCodeManager(setUpFakeRepo());
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
        SourceCodeManager target = setupSourceCodeManager(setUpFakeRepo());
        File repoCacheDir = getRepoCloneDir();

        // Act
        addTagToRepo(repoCacheDir, "expected_tag");
        List<String> result = target.getAvailableVersions();

        // Assert
        assertThat(result).containsOnly("expected_tag");
    }

    @Test
    public void provisionCopiesCode() throws Exception {
        // Arrange
        ConfigurationService configurationService = setUpFakeRepo();
        SourceCodeManager target = setupSourceCodeManager(configurationService);
        File repoCacheDir = getRepoCloneDir();
        File targetDir = workingDir.newFolder();
        when(configurationService.getModelRepositoryVersion()).thenReturn("expected_tag");

        // Act
        addTagToRepo(repoCacheDir, "expected_tag");
        target.provision(targetDir);

        // Assert
        assertThat(targetDir.listFiles()).hasSize(1);
        assertThat(Paths.get(targetDir.toString(), ".git").toFile()).doesNotExist();
    }

    @Test
    public void getSupportedModesForCurrentVersionReturnsCorrectModes() throws Exception {
        // Arrange
        ConfigurationService configurationService = setUpFakeRepo();
        SourceCodeManager target = setupSourceCodeManager(configurationService);
        File repoCacheDir = getRepoCloneDir();
        addFileToRepo(repoCacheDir, "data/abraid_modes.txt", "mode1\nmode2\nmode3\n");
        when(configurationService.getModelRepositoryVersion()).thenReturn("expected_tag");
        addTagToRepo(repoCacheDir, "expected_tag");

        // Act
        Set<String> result = target.getSupportedModesForCurrentVersion();

        // Assert
        assertThat(result).containsOnly("mode1", "mode2", "mode3");
    }

    @Test
    public void provisionRejectsInvalidVersions() throws Exception {
        // Arrange
        ConfigurationService configurationService = setUpFakeRepo();
        SourceCodeManager target = setupSourceCodeManager(configurationService);
        File repoCacheDir = getRepoCloneDir();
        File targetDir = workingDir.newFolder();
        when(configurationService.getModelRepositoryVersion()).thenReturn("expected_tag");

        // Act
        addTagToRepo(repoCacheDir, "bad_tag");
        catchException(target).provision(targetDir);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class).hasMessage("No such version");
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

    private void addFileToRepo(File repo, String file, String content) throws Exception {
        Git git = getGitFacade(repo);
        File newFile = Paths.get(repo.getAbsolutePath(), file).toFile();
        FileUtils.writeStringToFile(newFile, content);
        git.add().addFilepattern(file).call();
        git.commit().setMessage("new file").call();
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

    private ConfigurationService setUpFakeRepo() throws Exception {
        Repository repo = createGitRepository();
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getModelRepositoryUrl()).thenReturn(repo.getDirectory().getParent());
        return configurationService;
    }

    private SourceCodeManager setupSourceCodeManager(ConfigurationService configurationService) throws Exception {
        SourceCodeManager target = new GitSourceCodeManager(configurationService, Paths.get(cacheDir.getRoot().toString(), "repos").toString());
        target.updateRepository(); // initial clone
        return target;
    }
}
