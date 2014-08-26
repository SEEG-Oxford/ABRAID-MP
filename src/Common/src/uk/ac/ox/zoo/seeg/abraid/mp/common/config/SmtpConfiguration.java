package uk.ac.ox.zoo.seeg.abraid.mp.common.config;

/**
 * Immutable configuration object to hold the configuration related to an smtp server.
 * Copyright (c) 2014 University of Oxford
 */
public class SmtpConfiguration {
    private final String address;
    private final int port;
    private final boolean useSSL;
    private final String username;
    private final String password;

    public SmtpConfiguration(String address, int port, boolean useSSL, String username, String password) {
        this.address = address;
        this.port = port;
        this.useSSL = useSSL;
        this.username = username;
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean useSSL() {
        return useSSL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
