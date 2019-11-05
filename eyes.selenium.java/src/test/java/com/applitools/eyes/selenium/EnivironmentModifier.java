package com.applitools.eyes.selenium;

import java.lang.reflect.Field;
import java.util.Map;

class EnivironmentModifier {

    private Map<String, String> unmodEnvMap = null;

    public EnivironmentModifier() {
        try {
            Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");

            Field unmodifiableMapField = getAccessibleField(processEnvironment, "theUnmodifiableEnvironment");

            Object unmodifiableMap = unmodifiableMapField.get(null);

            Class unmodifiableMapClass = Class.forName("java.util.Collections$UnmodifiableMap");
            Field field = getAccessibleField(unmodifiableMapClass, "m");
            Object obj = field.get(unmodifiableMap);
            unmodEnvMap = (Map<String, String>) obj;

//            Field mapField = getAccessibleField(processEnvironment, "theEnvironment");
//            environmentMap = (Map<String, String>) mapField.get(null);

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Field getAccessibleField(Class<?> clazz, String fieldName)
            throws NoSuchFieldException {

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    void printEnv() {
        Map<String, String> getenv = System.getenv();
        for (Map.Entry entry : getenv.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    void setEnvironmentVariable(String key, String value) {
        if (value != null) {
            unmodEnvMap.put(key, value);
        } else {
            unmodEnvMap.remove(key);
        }

        //environmentMap.put(key, value);
    }

}
