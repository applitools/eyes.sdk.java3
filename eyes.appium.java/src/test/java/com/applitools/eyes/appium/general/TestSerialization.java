package com.applitools.eyes.appium.general;

import com.applitools.ICheckSettings;
import com.applitools.eyes.ImageMatchSettings;
import com.applitools.eyes.MatchWindowTask;
import com.applitools.eyes.appium.Target;
import com.applitools.eyes.appium.TestEyes;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestSerialization extends ReportingTestSuite {

    /**
     * Used for serialization testing
     */
    private static ObjectMapper jsonMapper;

    @BeforeClass
    public static void InitOnce() {
        jsonMapper = new ObjectMapper();
    }

    @DataProvider(name = "four_booleans")
    public static Object[][] fourBooleansDP() {
        return new Object[][]{
                {true, true, true, true},
                {true, true, true, false},
                {true, true, false, true},
                {true, true, false, false},
                {true, false, true, true},
                {true, false, true, false},
                {true, false, false, true},
                {true, false, false, false},
                {false, true, true, true},
                {false, true, true, false},
                {false, true, false, true},
                {false, true, false, false},
                {false, false, true, true},
                {false, false, true, false},
                {false, false, false, true},
                {false, false, false, false}
        };
    }

    public TestSerialization() {
        super.setGroupName("appium");
    }

    @Test(dataProvider = "four_booleans")
    public void test_ImageMatchSettings_Serialization(boolean ignoreCaret, boolean useDom, boolean enablePatterns, boolean ignoreDisplacements) throws JsonProcessingException {
        super.addSuiteArg("ignoreCaret", ignoreCaret);
        super.addSuiteArg("useDom", useDom);
        super.addSuiteArg("enablePatterns", enablePatterns);
        super.addSuiteArg("ignoreDisplacements", ignoreDisplacements);
        ImageMatchSettings ims = new ImageMatchSettings();
        ims.setIgnoreCaret(ignoreCaret);
        ims.setUseDom(useDom);
        ims.setEnablePatterns(enablePatterns);
        ims.setIgnoreDisplacements(ignoreDisplacements);

        String actualSerialization = jsonMapper.writeValueAsString(ims);

        String expectedSerialization = String.format(
                "{\"matchLevel\":\"STRICT\",\"exact\":null,\"ignoreCaret\":%s,\"useDom\":%s,\"enablePatterns\":%s,\"ignoreDisplacements\":%s,\"accessibility\":[],\"accessibilitySettings\":null,\"Ignore\":null,\"Layout\":null,\"Strict\":null,\"Content\":null,\"Floating\":null}",
                ignoreCaret, useDom, enablePatterns, ignoreDisplacements);

        Assert.assertEquals(actualSerialization,
                expectedSerialization, "ImageMatchSettings serialization does not match!");
    }

    @Test(dataProvider = "four_booleans")
    public void test_ImageMatchSettings_Serialization_Global(boolean ignoreCaret, boolean useDom, boolean enablePatterns, boolean ignoreDisplacements) throws JsonProcessingException {
        super.addSuiteArg("ignoreCaret", ignoreCaret);
        super.addSuiteArg("useDom", useDom);
        super.addSuiteArg("enablePatterns", enablePatterns);
        super.addSuiteArg("ignoreDisplacements", ignoreDisplacements);
        ICheckSettings settings = Target.window().fully().useDom(useDom).enablePatterns(enablePatterns).ignoreCaret(ignoreCaret);
        TestEyes eyes = new TestEyes();
        Configuration configuration = eyes.getConfiguration();
        configuration.setIgnoreDisplacements(ignoreDisplacements);
        eyes.setConfiguration(configuration);
        ImageMatchSettings imageMatchSettings = MatchWindowTask.createImageMatchSettings((ICheckSettingsInternal)settings, eyes);

        String actualSerialization = jsonMapper.writeValueAsString(imageMatchSettings);

        String expectedSerialization = String.format(
                "{\"matchLevel\":\"STRICT\",\"exact\":null,\"ignoreCaret\":%s,\"useDom\":%s,\"enablePatterns\":%s,\"ignoreDisplacements\":%s,\"accessibility\":[],\"accessibilitySettings\":null,\"Ignore\":null,\"Layout\":null,\"Strict\":null,\"Content\":null,\"Floating\":null}",
                ignoreCaret, useDom, enablePatterns, ignoreDisplacements);

        Assert.assertEquals(actualSerialization,
                expectedSerialization, "ImageMatchSettings serialization does not match!");
    }
}
