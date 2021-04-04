package com.applitools.eyes;

import com.applitools.ICheckSettings;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class TestSerialization extends ReportingTestSuite {

    public TestSerialization() {
        super.setGroupName("core");
    }

    /**
     * Used for serialization testing
     */
    private static ObjectMapper jsonMapper;

    @BeforeClass
    public static void InitOnce() {
        jsonMapper = new ObjectMapper();
    }

    @SuppressWarnings("EmptyCatchBlock")
    @Test
    public void testConstructor() {
        int left = 1;
        int top = 2;
        int width = 3;
        int height = 4;
        Region region = new Region(left, top, width, height);
        Assert.assertEquals(left, region.getLeft(), "left");
        Assert.assertEquals(top, region.getTop(), "top");
        Assert.assertEquals(width, region.getWidth(), "width");
        Assert.assertEquals(height, region.getHeight(), "height");

        region = new Region(new Location(left, top), new RectangleSize(width, height));
        Assert.assertEquals(left, region.getLeft(), "left");
        Assert.assertEquals(top, region.getTop(), "top");
        Assert.assertEquals(width, region.getWidth(), "width");
        Assert.assertEquals(height, region.getHeight(), "height");

        // This should still be ok (another way to say "empty region")
        new Region(1, 2, 0, 0);

        // Making sure negative positions are valid.
        try {
            new Region(-1, 2, 3, 4);
        } catch (IllegalArgumentException e) {
            Assert.fail("Left can be <= 0");
        }

        try {
            new Region(1, -2, 3, 4);
        } catch (IllegalArgumentException e) {
            Assert.fail("Top can be <= 0");
        }


        try {
            new Region(1, 2, -1, 0);
            Assert.fail("Width must be >=0");
        } catch (IllegalArgumentException e) {
        }

        try {
            new Region(1, 2, 3, -1);
            Assert.fail("Height must be >=0");
        } catch (IllegalArgumentException e) {
        }

        try {
            new Region(new Location(1, 2), null);
            Assert.fail("Size must not be null!");
        } catch (IllegalArgumentException e) {
        }
        try {
            new Region(null, null);
            Assert.fail("Location and size must not be null!");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testCopyConstructor() {
        int top = 1;
        int left = 2;
        int width = 3;
        int height = 4;

        Region original, other;
        original = new Region(left, top, width, height);
        other = new Region(original);

        Assert.assertEquals(other.getLeft(), original.getLeft(), "left");
        Assert.assertEquals(other.getTop(), original.getTop(), "top");
        Assert.assertEquals(other.getWidth(), original.getWidth(), "width");
        Assert.assertEquals(other.getHeight(), original.getHeight(), "height");

        Assert.assertEquals(other, original, "Region objects should be equal!");
        Assert.assertNotSame(original, other, "original and other should not be the same object");
    }

    @Test
    public void testLocation() {
        Region r = new Region(1, 2, 3, 4);

        Assert.assertEquals(r.getLocation(), new Location(1, 2), "invalid location");

        r.setLocation(new Location(5, 6));
        Assert.assertEquals(r.getLocation(), new Location(5, 6), "invalid location");
    }

    @Test
    public void testContains() {
        Region region = new Region(1, 1, 10, 10);
        Location containedLocation = new Location(2, 5);
        Location outsideLocation = new Location(20, 5);

        Assert.assertTrue(region.contains(containedLocation), "region contains containedLocation");
        Assert.assertFalse(region.contains(outsideLocation), "region doesn't contain location");
    }

    @Test
    public void testIntersect() {
        Region r1, r2;
        Location l1 = new Location(10, 10);
        Location l2 = new Location(20, 30);
        RectangleSize s1 = new RectangleSize(50, 100);
        RectangleSize s2 = new RectangleSize(100, 50);

        r1 = new Region(l1, s1);
        r2 = new Region(l2, s2);

        r1.intersect(r2);
        Assert.assertEquals(r1.getLeft(), 20, "intersected x");
        Assert.assertEquals(r1.getTop(), 30, "intersected y");
        Assert.assertEquals(r1.getWidth(), 40, "intersected width");
        Assert.assertEquals(r1.getHeight(), 50, "intersected height");

        // Regions which don't intersect should return an empty region.
        r2.intersect(new Region(5, 5, 10, 10));
        Assert.assertEquals(r2, Region.EMPTY, "no overlap");
    }

    @Test
    public void testEqualsAndHashCode() {
        Region r1, r2;
        r1 = new Region(1, 2, 3, 4);
        r2 = new Region(r1);
        Assert.assertEquals(r1, r2, "Regions should be equal!");
        Assert.assertEquals(r1.hashCode(), r2.hashCode(), "Hashes should be equal!");

        r2.makeEmpty();
        Assert.assertNotEquals(r1, r2, "Regions should differ!");
        Assert.assertNotEquals(r1.hashCode(), r2.hashCode(), "Hashes should differ!");
    }


    @Test
    public void test_Region_Deserialization() throws JsonProcessingException {
        int left = 1;
        int top = 2;
        int width = 3;
        int height = 4;

        try {
            String jsonData =
                    "{"
                            + "\"left\":" + String.valueOf(left) + ","
                            + "\"top\":" + String.valueOf(top) + ","
                            + "\"width\":" + String.valueOf(width) + ","
                            + "\"height\":" + String.valueOf(height)
                            + "}";

            Region actualDeserialization = jsonMapper.readValue(jsonData, Region.class);
            Region expectedDeserialization = new Region(left, top, width, height);
            Assert.assertEquals(actualDeserialization, expectedDeserialization, "Region deserialization does not match!");
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void test_Region_Serialization() throws JsonProcessingException {
        int left = 1;
        int top = 2;
        int width = 3;
        int height = 4;
        String expectedSerialization =
                "{"
                        + "\"left\":" + String.valueOf(left) + ","
                        + "\"top\":" + String.valueOf(top) + ","
                        + "\"width\":" + String.valueOf(width) + ","
                        + "\"height\":" + String.valueOf(height) + ","
                        + "\"coordinatesType\":\"SCREENSHOT_AS_IS\""
                        + "}";

        Region r = new Region(left, top, width, height);
        String actualSerialization = jsonMapper.writeValueAsString(r);

        Assert.assertEquals(actualSerialization,
                expectedSerialization, "Region serialization does not match!");

        r = new Region(new Location(left, top),
                new RectangleSize(width, height));
        actualSerialization = jsonMapper.writeValueAsString(r);
        Assert.assertEquals(actualSerialization,
                expectedSerialization, "Region serialization does not match for location/size constructor!");
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
        ICheckSettings settings = new CheckSettings(0).fully().useDom(useDom).enablePatterns(enablePatterns).ignoreCaret(ignoreCaret);
        TestEyes eyes = new TestEyes();
        Configuration configuration = eyes.getConfigurationInstance();
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

    @Test
    public void test_SessionStartInfo_Serialization() throws JsonProcessingException {
        ArrayList<PropertyData> properties = new ArrayList<>();
        properties.add(new PropertyData("property name", "property value"));
        properties.add(new PropertyData(null, null));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"));
        calendar.set(2017, Calendar.JULY, 2, 8, 22, 21);
        BatchInfo bi = new BatchInfo("batch name", calendar);
        bi.setId("37a587aa-17d0-4e86-bf0e-566656a84dda");
        String agentSessionId = UUID.randomUUID().toString();
        String agentRunId = UUID.randomUUID().toString();

        SessionStartInfo ssi = new SessionStartInfo("id", "some agent", SessionType.SEQUENTIAL,
                "my app", "1.0.0", "some scenario",
                bi,
                "some baseline name", "env name",
                new AppEnvironment(),
                new ImageMatchSettings(),
                "some branch name",
                "some parent branch name",
                "some baseline branch name",
                false,
                properties,
                agentSessionId,
                agentRunId,
                1800);

        String actualSerialization = jsonMapper.writeValueAsString(ssi);

        String expectedSerialization = String.format("{\"agentId\":\"some agent\",\"sessionType\":\"SEQUENTIAL\",\"appIdOrName\":\"my app\",\"verId\":\"1.0.0\",\"scenarioIdOrName\":\"some scenario\",\"batchInfo\":{\"id\":\"37a587aa-17d0-4e86-bf0e-566656a84dda\",\"batchSequenceName\":null,\"name\":\"batch name\",\"startedAt\":\"2017-07-02T05:22:21Z\",\"notifyOnCompletion\":false,\"isCompleted\":false,\"properties\":[]},\"baselineEnvName\":\"some baseline name\",\"environmentName\":\"env name\",\"environment\":{\"inferred\":null,\"os\":null,\"hostingApp\":null,\"displaySize\":null,\"deviceInfo\":null,\"osInfo\":null,\"hostingAppInfo\":null},\"branchName\":\"some branch name\",\"parentBranchName\":\"some parent branch name\",\"baselineBranchName\":\"some baseline branch name\",\"saveDiffs\":false,\"defaultMatchSettings\":{\"matchLevel\":\"STRICT\",\"exact\":null,\"ignoreCaret\":false,\"useDom\":false,\"enablePatterns\":false,\"ignoreDisplacements\":false,\"accessibility\":[],\"accessibilitySettings\":null,\"Ignore\":null,\"Layout\":null,\"Strict\":null,\"Content\":null,\"Floating\":null},\"properties\":[{\"name\":\"property name\",\"value\":\"property value\"},{\"name\":null,\"value\":null}],\"agentSessionId\":\"%s\",\"agentRunId\":\"%s\",\"timeout\":1800,\"concurrencyVersion\":2}", agentSessionId, agentRunId);

        Assert.assertEquals(actualSerialization,
                expectedSerialization, "SessionStartInfo serialization does not match!");
    }

    @Test
    public void testMiddleOffset() {
        Region r = new Region(1, 1, 10, 20);

        Location middleOffset = r.getMiddleOffset();
        Assert.assertEquals(middleOffset.getX(), 5, "X middle is not correct!");
        Assert.assertEquals(middleOffset.getY(), 10, "Y middle is not correct!");
    }
}
