package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ModellingConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.contentOf;

/**
* Tests the FreemarkerScriptGenerator class.
* Copyright (c) 2014 University of Oxford
*/
public class FreemarkerScriptGeneratorTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void generateScriptShouldReturnAFileThatItHasCreated() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        ModellingConfiguration conf = createBasicConfiguration();

        // Act
        File result = target.generateScript(conf, testFolder.getRoot());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).exists();
        assertThat(result).isFile();
        assertThat(result).canRead();
    }

    @Test
    public void generateScriptShouldReturnAFileThatIsBasedOnTheCorrectTemplate() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        ModellingConfiguration conf = createBasicConfiguration();

        // Act
        File result = target.generateScript(conf, testFolder.getRoot());

        // Assert
        assertThat(contentOf(result, defaultCharset())).startsWith("# A launch script for the ABRAID-MP disease risk model");
    }

    @Test
    public void generateScriptShouldReturnAddCorrectDataToTheScript() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        int maxCPUs = 123;
        ModellingConfiguration conf =  new ModellingConfiguration(maxCPUs, false, false);

        // Act
        File result = target.generateScript(conf, testFolder.getRoot());

        // Assert
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).contains("max_cpus <- " + maxCPUs);
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).contains("verbose <- FALSE");
    }

    @Test
    public void generateScriptShouldThrowIfScriptCanNotBeWritten() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        ModellingConfiguration conf = createBasicConfiguration();

        // Act
        catchException(target).generateScript(conf, new File("non-existent"));
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }

    @Test
    public void generateScriptShouldThrowIfWorkingDirectoryIsAFile() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        ModellingConfiguration conf = createBasicConfiguration();

        // Act
        catchException(target).generateScript(conf, testFolder.newFile());
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }

    private ModellingConfiguration createBasicConfiguration() throws IOException {
        return new ModellingConfiguration(1, false, false);
    }
}
