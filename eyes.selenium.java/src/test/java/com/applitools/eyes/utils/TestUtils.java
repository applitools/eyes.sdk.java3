package com.applitools.eyes.utils;

import com.applitools.eyes.FileLogger;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.selenium.Eyes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TestUtils {
    public final static boolean runOnCI = System.getenv("CI") != null;
    public final static boolean runHeadless = runOnCI || "true".equalsIgnoreCase(System.getenv("APPLITOOLS_RUN_HEADLESS"));
    public final static String logsPath = System.getenv("APPLITOOLS_LOGS_PATH");
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    public final static boolean verboseLogs = !runOnCI || "true".equalsIgnoreCase(System.getenv("APPLITOOLS_VERBOSE_LOGS"));

    public static String initLogPath() {
        return initLogPath(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    public static String initLogPath(String methodName) {
        return initLogPath(methodName, logsPath);
    }

    public static String initLogPath(String methodName, String logsPath) {
        String dateTimeString = dateFormat.format(Calendar.getInstance().getTime());
        return logsPath + File.separator + "java" + File.separator + methodName + "_" + dateTimeString;
    }

    public static LogHandler initLogger() {
        return initLogger(Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    public static LogHandler initLogger(String testName, String logPath) {
        if (!TestUtils.runOnCI)
        {
            String path = logPath != null ? logPath : initLogPath(testName);
            return new FileLogger(path + File.separator + "log.log", false, true);
        }
        return new StdoutLogHandler(verboseLogs);
    }

    public static LogHandler initLogger(String methodName) {
        return initLogger(methodName, null);
    }

    public static void setupLogging(Eyes eyes) {
        setupLogging(eyes, Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    public static void setupLogging(Eyes eyes, String methodName) {
        LogHandler logHandler;
        if (!TestUtils.runOnCI && logsPath != null) {
            String path = initLogPath(methodName);
            logHandler = new FileLogger(path + File.separator + methodName + ".log", false, true);
            eyes.setDebugScreenshotsPath(path);
            eyes.setDebugScreenshotsPrefix(methodName + "_");
            eyes.setSaveDebugScreenshots(true);
        } else {
            logHandler = new StdoutLogHandler(verboseLogs);
        }
        eyes.setLogHandler(logHandler);
    }

    public static SessionResults getSessionResults(String apiKey, TestResults results) throws java.io.IOException {
        String apiSessionUrl = results.getApiUrls().getSession();
        URI apiSessionUri = UriBuilder.fromUri(apiSessionUrl)
                .queryParam("format", "json")
                .queryParam("AccessToken", results.getSecretToken())
                .queryParam("apiKey", apiKey)
                .build();

        Client client = ClientBuilder.newClient();
        String srStr = client.target(apiSessionUri)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return jsonMapper.readValue(srStr, SessionResults.class);
    }

    public static Object getFinalStatic(Class klass, String fieldName) throws Exception {
        return getFieldValue(klass, null, fieldName);
    }

    public static Object getFinalStatic(Field field) throws Exception {
        return getFieldValue(field, (Object)null);
    }

    public static Object getFieldValue(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        return getFieldValue(field, instance);
    }

    public static Object getFieldValue(Class klass, Object instance, String fieldName) throws Exception {
        Field field = klass.getDeclaredField(fieldName);
        return getFieldValue(field, instance);
    }

    public static Object getFieldValue(Field field, Object instance) throws Exception {
        makeFieldAccessible(field);
        return field.get(instance);
    }

    public static void setFinalStatic(Class klass, String fieldName, Object newValue) throws Exception {
        Field field = klass.getDeclaredField(fieldName);
        setFinalStatic(field, newValue);
    }

    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        makeFieldAccessible(field);
        field.set(null, newValue);
    }

    private static void makeFieldAccessible(Field field) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

    public static List<Object[]> generatePermutationsList(List<List<Object>> lists) {
        List<Object[]> result = new ArrayList<>();
        generatePermutations(lists, result, 0, null);
        return result;
    }

    public static Object[][] generatePermutations(List<List<Object>> lists) {
        List<Object[]> result = generatePermutationsList(lists);
        return result.toArray(new Object[0][0]);
    }

    @SafeVarargs
    public static Object[][] generatePermutations(List<Object>... lists) {
        return generatePermutations(Arrays.asList(lists));
    }

    private static void generatePermutations(List<List<Object>> lists, List<Object[]> result, int depth, List<Object> permutation) {
        if (depth == lists.size()) {
            if (permutation != null) {
                result.add(permutation.toArray());
            }
            return;
        }

        List<Object> listInCurrentDepth = lists.get(depth);
        for (Object newItem : listInCurrentDepth) {
            if (permutation == null || depth == 0) {
                permutation = new ArrayList<>();
            }

            permutation.add(newItem);
            generatePermutations(lists, result, depth + 1, permutation);
            permutation.remove(permutation.size() - 1);
        }
    }

}
