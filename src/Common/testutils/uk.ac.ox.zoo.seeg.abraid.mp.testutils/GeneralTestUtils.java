package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

/**
 * Contains general test utilities.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeneralTestUtils {
    private GeneralTestUtils() {

    }
    
    /**
     * Mocks out log4j by injecting a mock logger into the specified object.
     * The object must have a field declared as follows (note the lowercase name and that it is not final):
     *       private static Logger logger = Logger.getLogger(...);
     * @param objectContainingLogger The object that contains the logger.
     * @return The mocked logger.
     */
    public static Logger createMockLogger(Object objectContainingLogger) {
        Logger mockLogger = mock(Logger.class);
        try {
            Field field = objectContainingLogger.getClass().getDeclaredField("logger");
            field.setAccessible(true);
            field.set(objectContainingLogger, mockLogger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mockLogger;
    }
}
