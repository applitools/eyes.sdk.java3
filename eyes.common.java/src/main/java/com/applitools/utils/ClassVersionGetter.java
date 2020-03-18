package com.applitools.utils;

import java.io.IOException;
import java.util.Properties;

public class ClassVersionGetter {
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(ClassVersionGetter.class.getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String CURRENT_VERSION = properties.getProperty("version");
}
