package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessRunnerFactory;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the CommonsExecProcessRunnerFactory class.
 * Copyright (c) 2014 University of Oxford
 */
public class CommonsExecProcessRunnerFactoryTest {
    @Test
    public void createProcessRunnerCreatesCorrectProcessRunnerImplementation() {
        // Arrange
        ProcessRunnerFactory target = new CommonsExecProcessRunnerFactory();

        // Act
        ProcessRunner result = target.createProcessRunner(null, new File("foo"), new String[0], null, 0);

        // Assert
        assertThat(result).isInstanceOf(CommonsExecProcessRunner.class);
    }

    @Test
    public void createProcessRunnerCreatesNewInstance() {
        // Arrange
        ProcessRunnerFactory target = new CommonsExecProcessRunnerFactory();

        // Act
        ProcessRunner first = target.createProcessRunner(null, new File("foo"), new String[0], null, 0);
        ProcessRunner second = target.createProcessRunner(null, new File("foo"), new String[0], null, 0);

        // Assert
        assertThat(first).isNotEqualTo(second);
    }

}
