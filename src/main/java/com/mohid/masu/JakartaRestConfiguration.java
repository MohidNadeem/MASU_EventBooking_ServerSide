package com.mohid.masu;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class JakartaRestConfiguration extends ResourceConfig {

    public JakartaRestConfiguration() {
        packages("com.mohid.masu.resource");
        register(JacksonFeature.class);
    }
}