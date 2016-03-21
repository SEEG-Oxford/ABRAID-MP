package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for the ErrorPageController.
 * Copyright (c) 2015 University of Oxford
 */
public class ErrorPageControllerTest {
    @Test
    public void getErrorPageReturnsCorrectTemplateAndData() throws Exception {
        // Arrange
        ErrorPageController target = new ErrorPageController();
        Model model = mock(Model.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>());
        when(request.getContextPath()).thenReturn("/publicsite");
        when(request.getRequestURI()).thenReturn("/publicsite/error");
        when(request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI)).thenReturn("/publicsite/page");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(123);

        // Act
        String result = target.getErrorPage(model, request, response);

        // Assert
        assertThat(result).isEqualTo("error");
        verify(model).addAttribute("status", 123);
        verify(model).addAttribute("uri", "/page");
    }

    @Test
    public void getErrorPageRejectsDirectRequests() throws Exception {
        // Arrange
        ErrorPageController target = new ErrorPageController();
        Model model = mock(Model.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>());
        when(request.getContextPath()).thenReturn("/publicsite");
        when(request.getRequestURI()).thenReturn("/publicsite/error");
        when(request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI)).thenReturn(null);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(0);

        // Act
        catchException(target).getErrorPage(model, request, response);

        // Assert
        assertThat(caughtException()).isInstanceOf(NoSuchRequestHandlingMethodException.class);
    }
}
