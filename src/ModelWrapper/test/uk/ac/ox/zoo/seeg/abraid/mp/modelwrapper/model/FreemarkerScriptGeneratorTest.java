package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.fest.util.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.Charset;

import static java.nio.charset.Charset.defaultCharset;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.util.Files.contentOf;

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

        // Act
        File result = target.generateScript(null, testFolder.getRoot());

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

        // Act
        File result = target.generateScript(null, testFolder.getRoot());

        // Assert
        assertThat(contentOf(result, defaultCharset())).startsWith("Templated message = ");
    }

    @Test
    public void generateScriptShouldReturnAddCorrectDataToTheScript() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();

        // Act
        File result = target.generateScript(null, testFolder.getRoot());

        // Assert
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).endsWith("Hello, World!");
    }
}
