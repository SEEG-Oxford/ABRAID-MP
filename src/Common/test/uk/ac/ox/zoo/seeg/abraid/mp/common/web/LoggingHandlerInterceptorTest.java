package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.junit.Test;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;
import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the LoggingHandlerInterceptor class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LoggingHandlerInterceptorTest {
    private LoggingHandlerInterceptor interceptor = new LoggingHandlerInterceptor();

    @Test
    public void preHandleSucceedsIfHandlerIsNull() throws Exception {
        interceptor.preHandle(mockHttpServletRequest(), mockHttpServletResponse(), null);
    }

    @Test
    public void preHandleSucceedsIfHandlerIsNotAMethodHandler() throws Exception {
        interceptor.preHandle(mockHttpServletRequest(), mockHttpServletResponse(), 123);
    }

    @Test
    public void preHandleSucceedsIfHandlerIsAMethodHandler() throws Exception {
        interceptor.preHandle(mockHttpServletRequest(), mockHttpServletResponse(), mockHandlerMethod());
    }

    @Test
    public void getRequestDetailsReturnsNullIfHandlerIsNull() throws Exception {
        // Act
        String requestDetails = interceptor.getRequestDetails(mockHttpServletRequest(), null);

        // Assert
        assertThat(requestDetails).isNull();
    }

    @Test
    public void getRequestDetailsWithNoUsernameAndNoQueryString() throws Exception {
        // Arrange
        HttpServletRequest request = mockHttpServletRequest("http://www.google.com/", null, "POST");
        HandlerMethod handlerMethod = mockHandlerMethod();
        String expectedRequestDetails = "(anonymous) Calling java.lang.Integer.intValue using POST http://www.google.com/";

        // Act
        String actualRequestDetails = interceptor.getRequestDetails(request, handlerMethod);

        // Assert
        assertThat(actualRequestDetails).isEqualTo(expectedRequestDetails);
    }

    @Test
    public void getRequestDetailsWithUsernameAndQueryString() throws Exception {
        // Arrange
        HttpServletRequest request = mockHttpServletRequest("http://www.google.com", "q=hello&oq=hello", "GET");
        addMockPrincipalToRequest(request, "myname");
        HandlerMethod handlerMethod = mockHandlerMethod();
        String expectedRequestDetails = "(myname) Calling java.lang.Integer.intValue using GET http://www.google.com?q=hello&oq=hello";

        // Act
        String actualRequestDetails = interceptor.getRequestDetails(request, handlerMethod);

        // Assert
        assertThat(actualRequestDetails).isEqualTo(expectedRequestDetails);
    }

    @Test
    public void postHandleSucceedsIfStartTimeAttributeNotSet() throws Exception {
        interceptor.postHandle(mockHttpServletRequest(), mockHttpServletResponse(), mockHandlerMethod(), null);
    }

    @Test
    public void postHandle() throws Exception {
        HttpServletRequest request = mockHttpServletRequest();
        request.setAttribute("LoggingHandlerInterceptor_startTime", 100);
        interceptor.postHandle(request, mockHttpServletResponse(), mockHandlerMethod(), null);
    }

    private HttpServletRequest mockHttpServletRequest() {
        return mockHttpServletRequest("", "", "");
    }

    private HttpServletRequest mockHttpServletRequest(String url, String queryString, String method) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURL()).thenReturn(new StringBuffer(url));
        when(request.getQueryString()).thenReturn(queryString);
        return request;
    }

    private HttpServletResponse mockHttpServletResponse() {
        return mock(HttpServletResponse.class);
    }

    private HandlerMethod mockHandlerMethod() {
        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        Method method;
        try {
            method = Integer.class.getMethod("intValue");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        when(handlerMethod.getMethod()).thenReturn(method);
        return handlerMethod;
    }

    private void addMockPrincipalToRequest(HttpServletRequest request, String name) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(name);
        when(request.getUserPrincipal()).thenReturn(principal);
    }
}
