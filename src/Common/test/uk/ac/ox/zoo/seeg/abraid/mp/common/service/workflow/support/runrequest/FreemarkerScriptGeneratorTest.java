package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ModellingConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Files.contentOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        DiseaseGroup dg = createDiseaseGroup();
        Collection<CovariateFile> covariates = Arrays.asList(createCovariate(1, 1), createCovariate(2, 2));

        // Act
        File result = target.generateScript(conf, testFolder.getRoot(), dg, covariates);

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
        DiseaseGroup dg = createDiseaseGroup();
        Collection<CovariateFile> covariates = Arrays.asList(createCovariate(1, 1), createCovariate(2, 2));

        // Act
        File result = target.generateScript(conf, testFolder.getRoot(), dg, covariates);

        // Assert
        assertThat(contentOf(result, defaultCharset())).startsWith("# A launch script for the ABRAID-MP disease risk model");
    }

    @Test
    public void generateScriptShouldReturnAddCorrectDataToTheScript() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        int maxCPUs = 123;
        ModellingConfiguration conf =  new ModellingConfiguration(maxCPUs, false, false);
        DiseaseGroup dg = createDiseaseGroup();
        Collection<CovariateFile> covariates = Arrays.asList(createCovariate(1, 1), createCovariate(2, 2));

        // Act
        File result = target.generateScript(conf, testFolder.getRoot(), dg, covariates);

        // Assert
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).contains("max_cpus <- " + maxCPUs);
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).contains("verbose <- FALSE");
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).contains("max_cpus <- " + maxCPUs);
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).contains(
                "    covariate_paths <- list(" + System.lineSeparator() +
                "        \"id1\"=\"covariates/c/1_1.tif\"," + System.lineSeparator() +
                "        \"id2\"=list(" + System.lineSeparator() +
                "            \"2015-01\"=\"covariates/c/2_1.tif\"," + System.lineSeparator() +
                "            \"2015-02\"=\"covariates/c/2_2.tif\"" + System.lineSeparator() +
                "        )" + System.lineSeparator() +
                "    )" + System.lineSeparator());
        assertThat(contentOf(result, Charset.forName("US-ASCII"))).contains(
                "    covariate_factors <- list(" + System.lineSeparator() +
                "        \"id1\"=FALSE," + System.lineSeparator() +
                "        \"id2\"=FALSE" + System.lineSeparator() +
                "    )" + System.lineSeparator());
    }

    @Test
    public void generateScriptShouldThrowIfScriptCanNotBeWritten() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        ModellingConfiguration conf = createBasicConfiguration();
        DiseaseGroup dg = createDiseaseGroup();
        Collection<CovariateFile> covariates = Arrays.asList(createCovariate(1, 1), createCovariate(2, 2));

        // Act
        catchException(target).generateScript(conf, new File("non-existent"), dg, covariates);
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }

    @Test
    public void generateScriptShouldThrowIfWorkingDirectoryIsAFile() throws Exception {
        // Arrange
        ScriptGenerator target = new FreemarkerScriptGenerator();
        ModellingConfiguration conf = createBasicConfiguration();
        DiseaseGroup dg = createDiseaseGroup();
        Collection<CovariateFile> covariates = Arrays.asList(createCovariate(1, 1), createCovariate(2, 2));

        // Act
        catchException(target).generateScript(conf, testFolder.newFile(), dg, covariates);
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }

    private ModellingConfiguration createBasicConfiguration() throws IOException {
        return new ModellingConfiguration(1, false, false);
    }

    private DiseaseGroup createDiseaseGroup() {
        DiseaseGroup dg = mock(DiseaseGroup.class);
        when(dg.getId()).thenReturn(123);
        when(dg.getModelMode()).thenReturn("thismode");
        return dg;
    }

    private CovariateFile createCovariate(int id, int subCount) {
        CovariateFile cov = mock(CovariateFile.class);
        List<CovariateSubFile> files = new ArrayList<>();
        for (int i = 1; i <= subCount; i++) {
            CovariateSubFile subObj = mock(CovariateSubFile.class);
            when(subObj.getId()).thenReturn((id * 1000) + i);
            when(subObj.getQualifier()).thenReturn(subCount == 1 ? null : "2015-0" + i);
            when(subObj.getFile()).thenReturn("c/" + id + "_" + i + ".tif");
            files.add(subObj);
        }
        when(cov.getFiles()).thenReturn(files);
        when(cov.getId()).thenReturn(id);
        when(cov.getDiscrete()).thenReturn(false);
        return cov;
    }
}
