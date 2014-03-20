package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.fest.util.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec.CommonsExecProcessRunnerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static java.nio.charset.Charset.defaultCharset;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.util.Files.contentOf;
import static org.mockito.Mockito.mock;

/**
 * Tests the FreemarkerScriptGenerator class.
 * Copyright (c) 2014 University of Oxford
 */
public class FreemarkerScriptGeneratorTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void generateScriptShouldReturnAFileThatItHasCreated() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        RunConfiguration conf = new RunConfiguration(null, null, "", 0, "");

        // Act
        File result = target.generateScript(conf, testFolder.getRoot(), false);

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
        RunConfiguration conf = new RunConfiguration(null, null, "", 0, "");

        // Act
        File result = target.generateScript(conf, testFolder.getRoot(), false);

        // Assert
        assertThat(contentOf(result, defaultCharset())).startsWith("# A launch script for the ABRAID-MP disease risk model");
    }

    @Test
    public void generateScriptShouldReturnAddCorrectDataToTheScript() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        String expectedRunName = "foobar4321";
        RunConfiguration conf = new RunConfiguration(null, null, expectedRunName, 0, "");

        // Act
        File result = target.generateScript(conf, testFolder.getRoot(), false);

        // Assert
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).contains("Run name = " + expectedRunName);
    }

    @Test
    public void generateScriptShouldThrowIfScriptCanNotBeWritten() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        RunConfiguration conf = new RunConfiguration(null, null, "", 0, "");

        // Act
        catchException(target).generateScript(conf, new File("non-existent"), false);
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }
}
