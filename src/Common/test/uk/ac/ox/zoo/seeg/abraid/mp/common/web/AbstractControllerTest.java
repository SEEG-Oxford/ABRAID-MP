package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for AbstractController.
 * Copyright (c) 2014 University of Oxford
 */
public class AbstractControllerTest {
    @Test
    public void initBinderShouldRegisterStringTrimmer() {
        // Arrange
        AbstractController target = new AbstractController() { };
        WebDataBinder binder = mock(WebDataBinder.class);

        // Act
        target.initBinder(binder);

        // Assert
        verify(binder, times(1)).registerCustomEditor(eq(String.class), any(StringTrimmerEditor.class));
    }
}
