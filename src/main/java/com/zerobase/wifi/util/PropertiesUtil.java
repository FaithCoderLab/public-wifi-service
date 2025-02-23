package com.zerobase.wifi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static Properties properties;

    private PropertiesUtil() {
    }

    public static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();

            try (InputStream input = PropertiesUtil.class.getClassLoader()
                    .getResourceAsStream("properties/application.properties")) {
                if (input == null) {
                    throw new RuntimeException("Application properties file not found");
                }
                properties.load(input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }
}
