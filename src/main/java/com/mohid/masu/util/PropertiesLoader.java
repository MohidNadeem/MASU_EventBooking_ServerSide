package com.mohid.masu.util;

import java.io.InputStream;
import java.util.Properties;

// Creating this class to get the Connection String from app.properties file I have created to seperate conn string from code
public class PropertiesLoader {

    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input = PropertiesLoader.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties");

            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        String envKey = key.toUpperCase().replace('.', '_');
        String envValue = System.getenv(envKey);

        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return properties.getProperty(key);
    }
}