package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by zool1112 on 13/02/14.
 */
public class ModelProcessHandlerTest {
    @Test
    public void getOutputStreamReturnsValidStream() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler();

        // Act
        OutputStream result = target.getOutputStream();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    public void getInputStreamReturnsValidStream() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler();

        // Act
        InputStream result = target.getInputStream();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    public void getErrorStreamReturnsValidStream() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler();

        // Act
        OutputStream result = target.getErrorStream();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    public void waitForCompletionShouldThrowIfWaiterHasNotBeenSet() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler();

        // Act
        catchException(target).waitForCompletion();
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IllegalStateException.class);
    }
}
