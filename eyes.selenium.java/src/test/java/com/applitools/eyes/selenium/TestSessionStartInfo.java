package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class TestSessionStartInfo extends ReportingTestSuite {

    public TestSessionStartInfo() {
        super.setGroupName("selenium");
    }

    @Test
    public void TestSessionInfo() {
        Calendar instance = Calendar.getInstance();
        instance.set(2017, Calendar.JULY, 29, 11, 1, 0);
        instance.setTimeZone(TimeZone.getTimeZone("GMT"));
        BatchInfo batchInfo = new BatchInfo("some batch", instance);
        batchInfo.setId("someBatchId");
        batchInfo.addProperty("key", "val");
        ImageMatchSettings ims = new ImageMatchSettings();
        ims.setMatchLevel(MatchLevel.STRICT);
        AccessibilityRegionByRectangle[] accessibilityValidation = {new AccessibilityRegionByRectangle(10, 20, 30, 40, AccessibilityRegionType.GraphicalObject)};
        ims.setAccessibility(accessibilityValidation);
        AccessibilitySettings accessibilitySettings = new AccessibilitySettings(AccessibilityLevel.AA, AccessibilityGuidelinesVersion.WCAG_2_0);
        ims.setAccessibilitySettings(accessibilitySettings);
        ims.setFloatingRegions(new FloatingMatchSettings[]{new FloatingMatchSettings(22, 32, 42, 52, 5, 10, 15, 20)});

        SessionStartInfo sessionStartInfo = new SessionStartInfo(
                "id", "agent", SessionType.SEQUENTIAL,
                "some app",
                "1.0",
                "some test",
                batchInfo,
                "baseline", "some environment",
                new AppEnvironment("windows", "test suite", new RectangleSize(234, 456)),
                ims, "some branch",
                "parent branch",
                "baseline branch",
                null,
                null,
                null,
                null,
                null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            String targetJsonObj = mapper.writeValueAsString(sessionStartInfo);
            String sourceJsonAsString = GeneralUtils.readInputStreamAsString(TestDomCapture.class.getResourceAsStream("/sessionStartInfo.json"));
            Assert.assertEquals(targetJsonObj, sourceJsonAsString, "JSON strings are different.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DataProvider(name = "three_booleans")
    public static Object[][] threeBooleansDP() {
        return new Object[][]{
                {true, true, true},
                {true, true, false},
                {true, false, true},
                {true, false, false},
                {false, true, true},
                {false, true, false},
                {false, false, true},
                {false, false, false},
        };
    }

    @Test(dataProvider = "three_booleans")
    public void TestFluentApiSerialization(boolean useDom, boolean enablePatterns, boolean ignoreDisplacements) {
        super.addSuiteArg("useDom", useDom);
        super.addSuiteArg("enablePatterns", enablePatterns);
        super.addSuiteArg("ignoreDisplacements", ignoreDisplacements);
        ICheckSettings settings = Target.window().fully().useDom(useDom).enablePatterns(enablePatterns).ignoreDisplacements(ignoreDisplacements);
        EyesBase eyes = new TestEyes();
        EyesScreenshot screenshot = new TestEyesScreenshot();
        ImageMatchSettings imageMatchSettings = MatchWindowTask.createImageMatchSettings((ICheckSettingsInternal) settings, screenshot, eyes);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            String json = mapper.writeValueAsString(imageMatchSettings);
            String expectedJsonName = "/sessionStartInfo_FluentApiSerialization_" + useDom + "_" + enablePatterns + "_" + ignoreDisplacements + ".json";
            String expectedJson = GeneralUtils.readInputStreamAsString(TestDomCapture.class.getResourceAsStream(expectedJsonName));
            Assert.assertEquals(json, expectedJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(dataProvider = "three_booleans")
    public void TestImageMatchSettingsSerialization(boolean useDom, boolean enablePatterns, boolean ignoreDisplacements) {
        super.addSuiteArg("useDom", useDom);
        super.addSuiteArg("enablePatterns", enablePatterns);
        super.addSuiteArg("ignoreDisplacements", ignoreDisplacements);
        ICheckSettings settings = Target.window().fully().useDom(useDom).enablePatterns(enablePatterns).ignoreDisplacements(ignoreDisplacements);
        TestEyes eyes = new TestEyes();
        ExactMatchSettings exactMatchSettings = new ExactMatchSettings();
        exactMatchSettings.setMatchThreshold(0.5f);
        eyes.setDefaultMatchSettings(new ImageMatchSettings(MatchLevel.EXACT, exactMatchSettings, useDom));
        ImageMatchSettings imageMatchSettings = MatchWindowTask.createImageMatchSettings((ICheckSettingsInternal) settings, eyes);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            String json = mapper.writeValueAsString(imageMatchSettings);
            String expectedJsonName = "/sessionStartInfo_FluentApiSerialization_NonDefaultIMS_" + useDom + "_" + enablePatterns + "_" + ignoreDisplacements + ".json";
            String expectedJson = GeneralUtils.readInputStreamAsString(TestDomCapture.class.getResourceAsStream(expectedJsonName));
            Assert.assertEquals(json, expectedJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(dataProvider = "three_booleans")
    public void TestImageMatchSettingsSerialization_Global(boolean useDom, boolean enablePatterns, boolean ignoreDisplacements) {
        super.addSuiteArg("useDom", useDom);
        super.addSuiteArg("enablePatterns", enablePatterns);
        super.addSuiteArg("ignoreDisplacements", ignoreDisplacements);
        ICheckSettings settings = Target.window().fully().useDom(useDom).enablePatterns(enablePatterns);
        TestEyes eyes = new TestEyes();
        Configuration configuration = eyes.getConfiguration();
        ExactMatchSettings exactMatchSettings = new ExactMatchSettings();
        exactMatchSettings.setMatchThreshold(0.5f);
        configuration.setDefaultMatchSettings(new ImageMatchSettings(MatchLevel.EXACT, exactMatchSettings, useDom));
        configuration.setIgnoreDisplacements(ignoreDisplacements);
        eyes.setConfiguration(configuration);
        ImageMatchSettings imageMatchSettings = MatchWindowTask.createImageMatchSettings((ICheckSettingsInternal) settings, eyes);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            String json = mapper.writeValueAsString(imageMatchSettings);
            String expectedJsonName = "/sessionStartInfo_FluentApiSerialization_NonDefaultIMS_" + useDom + "_" + enablePatterns + "_" + ignoreDisplacements + ".json";
            String expectedJson = GeneralUtils.readInputStreamAsString(TestDomCapture.class.getResourceAsStream(expectedJsonName));
            Assert.assertEquals(json, expectedJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(dataProvider = "three_booleans")
    public void TestConfigurationSerialization(boolean useDom, boolean enablePatterns, boolean ignoreDisplacements) {
        super.addSuiteArg("useDom", useDom);
        super.addSuiteArg("enablePatterns", enablePatterns);
        super.addSuiteArg("ignoreDisplacements", ignoreDisplacements);
        ICheckSettings settings = Target.window().fully();
        TestEyes eyes = new TestEyes();
        Configuration configuration = eyes.getConfiguration();
        configuration.setUseDom(useDom);
        configuration.setEnablePatterns(enablePatterns);
        configuration.setIgnoreDisplacements(ignoreDisplacements);
        eyes.setConfiguration(configuration);

        EyesScreenshot screenshot = new TestEyesScreenshot();
        ImageMatchSettings imageMatchSettings = MatchWindowTask.createImageMatchSettings((ICheckSettingsInternal) settings, screenshot, eyes);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            String json = mapper.writeValueAsString(imageMatchSettings);
            String expectedJsonName = "/sessionStartInfo_FluentApiSerialization_" + useDom + "_" + enablePatterns + "_" + ignoreDisplacements + ".json";
            String expectedJson = GeneralUtils.readInputStreamAsString(TestDomCapture.class.getResourceAsStream(expectedJsonName));
            Assert.assertEquals(json, expectedJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
