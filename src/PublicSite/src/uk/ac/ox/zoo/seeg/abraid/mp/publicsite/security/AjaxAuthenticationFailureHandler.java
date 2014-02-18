package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Customise AuthenticationFailureHandler to return a JSON instead of redirect.
 * Strategy used to handle if the ajax request was successful, but the user login authentication failed.
 * Copyright (c) 2014 University of Oxford
 */
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    /**
     * Prepares response JSON so AuthenticationException may be passed to and handled by the ajax success callback.
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @param auth The authentication exception, eg "Bad credentials"
     * @throws IOException
     * @throws ServletException
     */
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException auth)
            throws IOException, ServletException {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.getWriter().print("{\"success\":false, \"message\":\"" + auth.getMessage() + "\"}");
            response.getWriter().flush();
        } else {
            super.onAuthenticationFailure(request, response, auth);
        }
    }
}
