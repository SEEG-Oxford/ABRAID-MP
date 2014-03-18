package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Customise AuthenticationSuccessHandler to return a JSON instead of redirect.
 * This handler is used if the request was successfully handled, and login authentication was successful.
 * Copyright (c) 2014 University of Oxford
 */
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    /**
     * Prepares response JSON with success flag to pass to ajax success callback.
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @param auth Authentication
     * @throws java.io.IOException if the superclass throws this exception
     * @throws javax.servlet.ServletException if the superclass throws this exception
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.NO_CONTENT.value());

        // At some point we will probably return some json data here
        // response.setStatus(HttpStatus.OK.value());
        // response.getWriter().print(someJSON);
        // response.getWriter().flush();
    }
}
