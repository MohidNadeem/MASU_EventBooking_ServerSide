package com.mohid.masu.util;

import java.io.InputStream;
import java.util.Properties;

// Creating this class to get the Connection String from app.properties file I have created to seperate conn string from code
public class PropertiesLoader {

    private static Properties properties = new Properties();

    static {
        try {
            InputStream input = PropertiesLoader.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties");

            properties.load(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}