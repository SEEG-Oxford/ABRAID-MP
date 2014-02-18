package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Customise AuthenticationSuccessHandler to return a JSON instead of redirect.
 * This handler is used if the ajax request was successful, and login authentication was successful.
 * Copyright (c) 2014 University of Oxford
 */
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    /**
     * Prepares response JSON with success flag to pass to ajax success callback.
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @param auth Authentication
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException, ServletException {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.getWriter().print(
                    "{\"success\":true, \"targetUrl\":\"" + this.getTargetUrlParameter() + "\"}");
            response.getWriter().flush();
        } else {
            super.onAuthenticationSuccess(request, response, auth);
        }
    }
}
