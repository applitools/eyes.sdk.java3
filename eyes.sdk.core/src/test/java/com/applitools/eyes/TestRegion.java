package com.applitools.eyes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

@RunWith(JUnit4.class)
public class TestRegion {

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
        Assert.assertEquals("left", region.getLeft(), left);
        Assert.assertEquals("top", region.getTop(), top);
        Assert.assertEquals("width", region.getWidth(), width);
        Assert.assertEquals("height", region.getHeight(), height);

        region = new Region(new Location(left, top), new RectangleSize(width, height));
        Assert.assertEquals("left", region.getLeft(), left);
        Assert.assertEquals("top", region.getTop(), top);
        Assert.assertEquals("width", region.getWidth(), width);
        Assert.assertEquals("height", region.getHeight(), height);

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
            new Region(null, new RectangleSize(3, 4));
            Assert.fail("Location must not be null!");
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

        Assert.assertEquals("left", original.getLeft(), other.getLeft());
        Assert.assertEquals("top", original.getTop(), other.getTop());
        Assert.assertEquals("width", original.getWidth(), other.getWidth());
        Assert.assertEquals("height", original.getHeight(), other.getHeight());

        Assert.assertEquals("Region objects should be equal!", original, other);
        Assert.assertFalse("original and other should not be the same object", original == other);
    }

    @Test
    public void testLocation() {
        Region r = new Region(1, 2, 3, 4);

        Assert.assertEquals("invalid location", r.getLocation(), new Location(1, 2));

        r.setLocation(new Location(5, 6));
        Assert.assertEquals("invalid location", r.getLocation(), new Location(5, 6));
    }

    @Test
    public void testContains() {
        Region region = new Region(1, 1, 10, 10);
        Location containedLocation = new Location(2, 5);
        Location outsideLocation = new Location(20, 5);

        Assert.assertTrue("region contains containedLocation", region.contains(containedLocation));
        Assert.assertFalse("region doesn't contain location",
                region.contains(outsideLocation));
    }

    @Test
    public void testIntersect() {
        Region r1, r2;
        Region.initLogger(new Logger());
        Location l1 = new Location(10, 10);
        Location l2 = new Location(20, 30);
        RectangleSize s1 = new RectangleSize(50, 100);
        RectangleSize s2 = new RectangleSize(100, 50);

        r1 = new Region(l1, s1);
        r2 = new Region(l2, s2);

        r1.intersect(r2);
        Assert.assertEquals("intersected x", 20, r1.getLeft());
        Assert.assertEquals("intersected y", 30, r1.getTop());
        Assert.assertEquals("intersected width", 40, r1.getWidth());
        Assert.assertEquals("intersected height", 50, r1.getHeight());

        // Regions which don't intersect should return an empty region.
        r2.intersect(new Region(5, 5, 10, 10));
        Assert.assertEquals("no overlap", r2, Region.EMPTY);
    }

    @Test
    public void testEqualsAndHashCode() {
        Region r1, r2;
        r1 = new Region(1, 2, 3, 4);
        r2 = new Region(r1);
        Assert.assertEquals("Regions should be equal!", r1, r2);
        Assert.assertEquals("Hashes should be equal!", r1.hashCode(), r2.hashCode());

        r2.makeEmpty();
        Assert.assertNotEquals("Regions should differ!", r1, r2);
        Assert.assertNotEquals("Hashes should differ!", r1.hashCode(), r2.hashCode());
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
            Assert.assertEquals("Region deserialization does not match!",
                    expectedDeserialization, actualDeserialization);
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

        Assert.assertEquals("Region serialization does not match!",
                expectedSerialization, actualSerialization);

        r = new Region(new Location(left, top),
                new RectangleSize(width, height));
        actualSerialization = jsonMapper.writeValueAsString(r);
        Assert.assertEquals("Region serialization does not match for location/size constructor!",
                expectedSerialization, actualSerialization);
    }

    @Test
    public void test_ImageMatchSettings_Serialization() throws JsonProcessingException {
        ImageMatchSettings ims = new ImageMatchSettings();

        String actualSerialization = jsonMapper.writeValueAsString(ims);

        String expectedSerialization = "{\"matchLevel\":\"STRICT\",\"exact\":null,\"ignoreCaret\":null,\"Ignore\":null,\"Floating\":null}";

        Assert.assertEquals("ImageMatchSettings serialization does not match!",
                expectedSerialization, actualSerialization);

        ims.setIgnoreCaret(true);

        actualSerialization = jsonMapper.writeValueAsString(ims);

        expectedSerialization = "{\"matchLevel\":\"STRICT\",\"exact\":null,\"ignoreCaret\":true,\"Ignore\":null,\"Floating\":null}";

        Assert.assertEquals("ImageMatchSettings serialization does not match!",
                expectedSerialization, actualSerialization);

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

        SessionStartInfo ssi = new SessionStartInfo("some agent", SessionType.SEQUENTIAL,
                "my app", "1.0.0", "some scenario",
                bi,
                "some baseline name", "env name",
                new AppEnvironment(),
                new ImageMatchSettings(),
                "some branch name",
                "some parent branch name",
                properties);

        String actualSerialization = jsonMapper.writeValueAsString(ssi);

        String expectedSerialization = "{\"agentId\":\"some agent\",\"sessionType\":\"SEQUENTIAL\",\"appIdOrName\":\"my app\",\"verId\":\"1.0.0\",\"scenarioIdOrName\":\"some scenario\",\"batchInfo\":{\"id\":\"37a587aa-17d0-4e86-bf0e-566656a84dda\",\"name\":\"batch name\",\"startedAt\":\"2017-07-02T11:22:21Z\"},\"baselineEnvName\":\"some baseline name\",\"environmentName\":\"env name\",\"environment\":{\"inferred\":null,\"os\":null,\"hostingApp\":null,\"displaySize\":null},\"branchName\":\"some branch name\",\"parentBranchName\":\"some parent branch name\",\"defaultMatchSettings\":{\"matchLevel\":\"STRICT\",\"exact\":null,\"ignoreCaret\":null,\"Ignore\":null,\"Floating\":null},\"properties\":[{\"name\":\"property name\",\"value\":\"property value\"},{\"name\":null,\"value\":null}]}";

        Assert.assertEquals("SessionStartInfo serialization does not match!",
                expectedSerialization, actualSerialization);
    }

    @Test
    public void testMiddleOffset() {
        Region r = new Region(1, 1, 10, 20);

        Location middleOffset = r.getMiddleOffset();
        Assert.assertEquals("X middle is not correct!", 5,
                middleOffset.getX());
        Assert.assertEquals("Y middle is not correct!", 10,
                middleOffset.getY());
    }
}
