package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for the ABRAID error page.
 * Copyright (c) 2015 University of Oxford
 *
 * Note: The approach here, to use a RequestMapping with "<error-page>" in web.xml instead of using Spring's
 * @ControllerAdvice/ExceptionResolver mechanism, is intentional. Due to the structure of
 * DefaultHandlerExceptionResolver there is no way to add an ExceptionResolver while keeping the behavior of
 * DefaultHandlerExceptionResolver (setting the correct HTTP status and header - i.e. Accept advice) while also styling
 * the html output, for all error types. Instead we let DefaultHandlerExceptionResolver process the errors then
 * internally redirect to our error page, which adds the styling.
 */
@Controller
public class ErrorPageController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(ErrorPageController.class);

    /**
     * Show the error page.
     * @param model The page template model.
     * @param request The HTTP request (this is a second request caused by the internal redirection of error pages).
     * @param response The HTTP response.
     * @return The page template name.
     * @throws NoSuchRequestHandlingMethodException 404 if the hit on this RequestMapping is caused by somebody viewing
     *                                              "/error" instead of by the internal redirect.
     */
    @RequestMapping(value = "/error")
    public String getErrorPage(Model model, HttpServletRequest request, HttpServletResponse response)
            throws NoSuchRequestHandlingMethodException {
        int status = response.getStatus();
        preventExternalRequests(request, status);
        String originalUri = getOriginalServletPath(request);

        LOGGER.warn(String.format("Showing error page due to %s accessing '%s'.", status, originalUri));

        model.addAttribute("status", status);
        model.addAttribute("uri", originalUri);
        return "error";
    }

    private void preventExternalRequests(HttpServletRequest request, int status) throws NoSuchRequestHandlingMethodException {
        if (status == 0) {
            // This request mapping is for internal error access (via web.xml <errorpage>) only. If we get a normal hit
            // on it, throw a 404 (which will end up back here via DefaultHandlerExceptionResolver).
            throw new NoSuchRequestHandlingMethodException(request);
        }
    }

    private String getOriginalServletPath(HttpServletRequest request) {
        // Get the URI from the first request (not the internally forwarded one)
        String originalUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        String contextPath = request.getContextPath();
        if (originalUri.startsWith(contextPath)) {
            originalUri = StringUtils.replaceOnce(originalUri, contextPath, "");
        }
        return originalUri;
    }
}
