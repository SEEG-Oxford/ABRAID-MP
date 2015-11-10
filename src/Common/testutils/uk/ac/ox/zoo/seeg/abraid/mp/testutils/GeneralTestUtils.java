package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import org.apache.log4j.Logger;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Contains general test utilities.
 *
 * Copyright (c) 2014 University of Oxford
 */
public final class GeneralTestUtils {
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

    /**
     * Build a new <code>ArgumentCaptor</code>.
     * @param clazz Type matching the parameter to be captured.
     * @param <T> Type of clazz
     * @return A new ArgumentCaptor
     */
    public static <T> org.mockito.ArgumentCaptor<T> captorForClass(java.lang.Class<T> clazz) {
        return ArgumentCaptor.forClass(clazz);
    }

    /**
     * Build a new <code>ArgumentCaptor</code> for a typed list.
     * @param <T> Type of clazz for list elements
     * @return A new ArgumentCaptor
     */
    public static <T> org.mockito.ArgumentCaptor<List<T>> captorForListClass() {
        Class<List<T>> clazz = getListClass();
        return GeneralTestUtils.captorForClass(clazz);
    }

    /**
     * Build a new <code>ArgumentCaptor</code> for a typed map.
     * @param <K> Type of clazz for map keys
     * @param <V> Type of clazz for map values
     * @return A new ArgumentCaptor
     */
    public static <K, V> org.mockito.ArgumentCaptor<Map<K, V>> captorForMapClass() {
        Class<Map<K, V>> clazz = getMapClass();
        return GeneralTestUtils.captorForClass(clazz);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<List<T>> getListClass() {
        return (Class<List<T>>) (Class) List.class;
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Class<Map<K, V>> getMapClass() {
        return (Class<Map<K, V>>) (Class) Map.class;
    }
}
