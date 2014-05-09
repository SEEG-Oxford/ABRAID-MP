package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

/**
 * A Spring interceptor that logs controller method requests.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LoggingHandlerInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = Logger.getLogger(LoggingHandlerInterceptor.class);
    private static final String REQUEST_LOG_MESSAGE = "(%s) Calling %s.%s using %s %s";
    private static final String DURATION_LOG_MESSAGE = "Took %.3f seconds";
    private static final String START_TIME_ATTRIBUTE_NAME = "LoggingHandlerInterceptor_startTime";
    private static final double MINIMUM_REQUEST_DURATION_SECONDS = 0.3;
    private static final double MILLISECONDS_TO_SECONDS = 1000.0;

    /**
     * Logs an HTTP request, before it is received by a controller.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param handler The handler object.
     * @return Always returns true so that processing can continue.
     * @throws Exception if an error occurs
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestDetails = getRequestDetails(request, handler);
        if (requestDetails != null) {
            LOGGER.info(requestDetails);
        }

        // This is for the benefit of the postHandle() method below
        if (LOGGER.isDebugEnabled()) {
            request.setAttribute(START_TIME_ATTRIBUTE_NAME, System.currentTimeMillis());
        }
        return true;
    }

    /**
     * Logs an HTTP request, after it is processed by a controller.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param handler The handler object.
     * @param modelAndView The model and view.
     * @throws Exception if an error occurs
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // If DEBUG level logging is enabled, log all response times arising from a call to a controller method
        if (LOGGER.isDebugEnabled()) {
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE_NAME);
            if (startTime != null) {
                double durationInSeconds = getRequestDuration(startTime);
                if (durationInSeconds >= MINIMUM_REQUEST_DURATION_SECONDS) {
                    LOGGER.debug(String.format(DURATION_LOG_MESSAGE, durationInSeconds));
                }
            }
        }
    }

    /**
     * Gets the details of the HTTP request, for logging purposes.
     * This only includes requests that call controller methods.
     * @param request The HTTP request.
     * @param handler The handler object.
     * @return The details of the HTTP request, or null if the handler is not a HandlerMethod.
     */
    public String getRequestDetails(HttpServletRequest request, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String className = handlerMethod.getMethod().getDeclaringClass().getName();
            String methodName = handlerMethod.getMethod().getName();
            String requestMethodName = request.getMethod();
            String username = getUsername(request);
            String fullURL = getFullURL(request);

            return String.format(REQUEST_LOG_MESSAGE, username, className, methodName, requestMethodName, fullURL);
        }

        return null;
    }

    private String getUsername(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return (principal == null) ? "anonymous" : principal.getName();
    }

    private String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        // Returns the full request URL, including query parameters (if any)
        if (StringUtils.hasText(queryString)) {
            return requestURL.append('?').append(queryString).toString();
        } else {
            return requestURL.toString();
        }
    }

    private double getRequestDuration(long startTime) {
        long endTime = System.currentTimeMillis();
        return (endTime - startTime) / MILLISECONDS_TO_SECONDS;
    }
}
