package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilities for Uri building and extracting.
 * Copyright (c) 2014 University of Oxford
 */
public final class UriUtils {
    private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    private static final String HTTPS_PROTO = "https";
    private static final String HTTP_PROTO = "http";
    private static final int HTTPS_PORT = 443;
    private static final int HTTP_PORT = 80;

    private UriUtils() {
    }

    /**
     * A proxy aware method to extract the base URL of a servlet from a HTTP request.
     * @param request The HTTP request.
     * @return The public URL of the servlet.
     */
    public static String extractBaseURL(HttpServletRequest request) {
        // Adapted from org.springframework.web.servlet.support.ServletUriComponentsBuilder#fromRequest (Apache v2)
        // Nginx config:
        //     proxy_pass <address>;
        //     proxy_set_header X-Forwarded-Host $host:$server_port;
        //     proxy_set_header X-Forwarded-Proto $scheme;

        String scheme = request.getScheme();
        int port = request.getServerPort();
        String host = request.getServerName();

        String header = request.getHeader(X_FORWARDED_HOST);
        if (StringUtils.hasText(header)) {
            String[] hosts = StringUtils.commaDelimitedListToStringArray(header);
            String hostToUse = hosts[0];
            if (hostToUse.contains(":")) {
                String[] hostAndPort = StringUtils.split(hostToUse, ":");
                host = hostAndPort[0];
                port = Integer.parseInt(hostAndPort[1]);
            } else {
                host = hostToUse;
            }
        }

        String forwardedScheme = request.getHeader(X_FORWARDED_PROTO);
        if (forwardedScheme != null) {
            scheme = forwardedScheme;
        }

        UriComponentsBuilder builder = ServletUriComponentsBuilder.newInstance();
        builder.scheme(scheme);
        builder.host(host);
        if (!((scheme.equals(HTTP_PROTO) && port == HTTP_PORT) || (scheme.equals(HTTPS_PROTO) && port == HTTPS_PORT))) {
            builder.port(port);
        }
        builder.path(request.getContextPath());

        return builder.build().toUriString();
    }
}
