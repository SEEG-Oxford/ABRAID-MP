package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the UriUtils class.
 * Copyright (c) 2014 University of Oxford
 */
public class UriUtilsTest {

    @Test
    public void extractBaseURLExtractsCorrectURLWhenProxyNotInUse() throws Exception {
        // Arrange
        HttpServletRequest request = createHttpServletRequest("foo", "abc", 123, "efg", false, false);

        // Act
        String url = UriUtils.extractBaseURL(request);

        // Assert
        assertThat(url).isEqualTo("foo://abc:123/efg");
    }

    @Test
    public void extractBaseURLExtractsCorrectURLWhenHTTPPortNotRequired() throws Exception {
        // Arrange
        HttpServletRequest request = createHttpServletRequest("http", "abc", 80, "efg", false, false);

        // Act
        String url = UriUtils.extractBaseURL(request);

        // Assert
        assertThat(url).isEqualTo("http://abc/efg");
    }

    @Test
    public void extractBaseURLExtractsCorrectURLWhenHTTPSPortNotRequired() throws Exception {
        // Arrange
        HttpServletRequest request = createHttpServletRequest("https", "abc", 443, "efg", false, false);

        // Act
        String url = UriUtils.extractBaseURL(request);

        // Assert
        assertThat(url).isEqualTo("https://abc/efg");
    }

    @Test
    public void extractBaseURLExtractsCorrectURLWhenForwardedHostUsed() throws Exception {
        // Arrange
        HttpServletRequest request = createHttpServletRequest("foo", "abc", 123, "efg", true, false);

        // Act
        String url = UriUtils.extractBaseURL(request);

        // Assert
        assertThat(url).isEqualTo("foo://abc:123/efg");
    }

    @Test
    public void extractBaseURLExtractsCorrectURLWhenForwardedSchemeUsed() throws Exception {
        // Arrange
        HttpServletRequest request = createHttpServletRequest("foo", "abc", 123, "efg", false, true);

        // Act
        String url = UriUtils.extractBaseURL(request);

        // Assert
        assertThat(url).isEqualTo("foo://abc:123/efg");
    }

    @Test
    public void extractBaseURLExtractsCorrectURLWhenProxyUsed() throws Exception {
        // Arrange
        HttpServletRequest request = createHttpServletRequest("https", "www.abraid.ox.ac.uk", 443, "/", true, true);

        // Act
        String url = UriUtils.extractBaseURL(request);

        // Assert
        assertThat(url).isEqualTo("https://www.abraid.ox.ac.uk/");
    }

    private HttpServletRequest createHttpServletRequest(String schema, String host, int port, String context, boolean useXFowardedHost, boolean useXFowardedProto) {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        if (useXFowardedProto) {
            when(httpServletRequest.getScheme()).thenReturn("http");
            when(httpServletRequest.getHeader("X-Forwarded-Proto")).thenReturn(schema);
        } else {
            when(httpServletRequest.getScheme()).thenReturn(schema);
        }

        if (useXFowardedHost) {
            when(httpServletRequest.getServerName()).thenReturn("localhost");
            when(httpServletRequest.getServerPort()).thenReturn(8080);
            when(httpServletRequest.getHeader("X-Forwarded-Host")).thenReturn(host + ":" + port);
        } else {

            when(httpServletRequest.getServerName()).thenReturn(host);
            when(httpServletRequest.getServerPort()).thenReturn(port);
        }
        when(httpServletRequest.getContextPath()).thenReturn(context);
        return httpServletRequest;
    }
}