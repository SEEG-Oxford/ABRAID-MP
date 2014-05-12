package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static java.nio.charset.Charset.defaultCharset;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.util.Files.contentOf;

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
        RunConfiguration conf = createBasicRunConfiguration(null);

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
        RunConfiguration conf = createBasicRunConfiguration(null);

        // Act
        File result = target.generateScript(conf, testFolder.getRoot());

        // Assert
        assertThat(contentOf(result, defaultCharset())).startsWith("# A launch script for the ABRAID-MP disease risk model");
    }

    @Test
    public void generateScriptShouldReturnAddCorrectDataToTheScript() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        String expectedRunName = "foobar4321";
        RunConfiguration conf = createBasicRunConfiguration(expectedRunName);

        // Act
        File result = target.generateScript(conf, testFolder.getRoot());

        // Assert
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).contains("Run name = " + expectedRunName);
    }

    @Test
    public void generateScriptShouldThrowIfScriptCanNotBeWritten() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        RunConfiguration conf = createBasicRunConfiguration(null);

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
        RunConfiguration conf = createBasicRunConfiguration(null);

        // Act
        catchException(target).generateScript(conf, testFolder.newFile());
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }

    private RunConfiguration createBasicRunConfiguration(String runName) {
        return new RunConfiguration(
                StringUtils.isNotEmpty(runName) ? runName :"foo", testFolder.getRoot(),
                new CodeRunConfiguration("", ""),
                new ExecutionRunConfiguration(new File(""), 60000, 1, false),
                new CovariateRunConfiguration("", new ArrayList<String>()),
                new AdminUnitRunConfiguration(true, "", "", "", ""));
    }
}
