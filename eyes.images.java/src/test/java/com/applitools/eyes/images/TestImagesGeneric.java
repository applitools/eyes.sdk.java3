package com.applitools.eyes.images;

import com.applitools.eyes.*;
import com.applitools.eyes.images.utils.TestSetup;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.utils.ImageUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * these tests are implemented for the images SDK in each SDK individually.
 * (that's why I named it generic)
 */
public class TestImagesGeneric extends TestSetup {

    private static final String IMAGE_URL_JPG = "https://applitools.github.io/demo/images/image_1.jpg";
    private static final String IMAGE_URL_JPEG = "https://applitools.github.io/demo/images/minions_jpeg.jpeg";
    private static final String IMAGE_URL_PNG = "https://applitools.github.io/upload/appium.png";
    private static final String IMAGE_URL_BMP = "https://applitools.github.io/demo/images/minions_bitmap.bmp";

    private static final String IMAGE_PATH_JPEG = "src/main/resources/minions_jpeg.jpeg";
    private static final String IMAGE_PATH_JPG = "";
    private static final String IMAGE_PATH_PNG = "src/main/resources/minions_png.png";
    private static final String IMAGE_PATH_BMP = "src/main/resources/minions_bitmap.bmp";

    @Test
    public void testBitMapFromPath() {
        BufferedImage image = ImageUtils.imageFromFile(IMAGE_PATH_BMP);
        eyes.open(getApplicationName(), "TestBitMapFromPath", new RectangleSize(image.getWidth(), image.getHeight()));
        eyes.check(Target.image(IMAGE_PATH_BMP));
        eyes.close();
    }

    @Test
    public void testBitMapFromBuffer() {
        BufferedImage image = ImageUtils.imageFromFile(IMAGE_PATH_BMP);
        eyes.open(getApplicationName(), "TestBitMapFromBuffer", new RectangleSize(image.getWidth(), image.getHeight()));
        eyes.check(Target.image(image));
        eyes.close();
    }

    @Test
    public void testBitMapFromURL() throws MalformedURLException {
        BufferedImage image = ImageUtils.imageFromUrl(new URL(IMAGE_URL_BMP));
        eyes.open(getApplicationName(), "TestBitMapFromURL", new RectangleSize(image.getWidth(), image.getHeight()));
        eyes.check(Target.image(IMAGE_URL_BMP));
        eyes.close();
    }

    @Test
    public void testJPEGFromPath() {
        BufferedImage image = ImageUtils.imageFromFile(IMAGE_PATH_JPEG);
        eyes.open(getApplicationName(), "TestJPEGFromPath", new RectangleSize(image.getWidth(), image.getHeight()));
        eyes.check(Target.image(IMAGE_PATH_JPEG));
        eyes.close();
    }

    @Test
    public void testJPEGFromBuffer() {
        BufferedImage image = ImageUtils.imageFromFile(IMAGE_PATH_JPEG);
        eyes.open(getApplicationName(), "TestJPEGFromBuffer", new RectangleSize(image.getWidth(), image.getHeight()));
        eyes.check(Target.image(image));
        eyes.close();
    }

    @Test
    public void testJPEGFromURL() throws MalformedURLException {
        BufferedImage image = ImageUtils.imageFromUrl(new URL(IMAGE_URL_JPEG));
        eyes.open(getApplicationName(), "TestJPEGFromURL", new RectangleSize(image.getWidth(), image.getHeight()));
        eyes.check(Target.image(IMAGE_URL_JPEG));
        eyes.close();
    }

//    @Test
//    public void testJPGFromPath() {
//        BufferedImage image = ImageUtils.imageFromFile(IMAGE_PATH_JPG);
//        eyes.open(getApplicationName(), "TestJPGFromPath", new RectangleSize(image.getWidth(), image.getHeight()));
//        eyes.check(Target.image(IMAGE_PATH_JPG));
//        eyes.close();
//    }
//
//    @Test
//    public void testJPGFromBuffer() {
//        BufferedImage image = ImageUtils.imageFromFile(IMAGE_PATH_JPG);
//        eyes.open(getApplicationName(), "TestJPGFromBuffer", new RectangleSize(image.getWidth(), image.getHeight()));
//        eyes.check(Target.image(image));
//        eyes.close();
//    }
//
//    @Test
//    public void testJPGFromURL() throws MalformedURLException {
//        BufferedImage image = ImageUtils.imageFromUrl(new URL(IMAGE_URL_JPG));
//        eyes.open(getApplicationName(), "TestJPGFromURL", new RectangleSize(image.getWidth(), image.getHeight()));
//        eyes.check(Target.image(IMAGE_URL_JPG));
//        eyes.close();
//    }

    @Test
    public void testPNGFromPath() {
        BufferedImage image = ImageUtils.imageFromFile(IMAGE_PATH_PNG);
        eyes.open(getApplicationName(), "TestPNGFromPath", new RectangleSize(image.getWidth(), image.getHeight()));
        eyes.check(Target.image(IMAGE_PATH_PNG));
        eyes.close();
    }

    @Test
    public void testPNGFromBuffer() {
        BufferedImage image = ImageUtils.imageFromFile(IMAGE_PATH_PNG);
        eyes.open(getApplicationName(), "TestPNGFromBuffer", new RectangleSize(image.getWidth(), image.getHeight()));
        eyes.check(Target.image(image));
        eyes.close();
    }

    @Test
    public void testPNGFromURL() throws MalformedURLException {
        BufferedImage image = ImageUtils.imageFromUrl(new URL(IMAGE_URL_PNG));
        eyes.open(getApplicationName(), "TestPNGFromURL", new RectangleSize(image.getWidth(), image.getHeight()));
        eyes.check(Target.image(IMAGE_URL_PNG));
        eyes.close();
    }

    // ------------------------------------------------------------------------------------------------------------

    private final BufferedImage TEST_IMAGE = ImageUtils.imageFromFile(IMAGE_PATH_JPEG);
    private final BufferedImage EXTRACT_TEXT_IMAGE = ImageUtils.imageFromFile("src/main/resources/extractText.png");

    @Test
    public void testIgnoreRegionsFluent() {
        eyes.open(getApplicationName(), "TestIgnoreRegionFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE)
                .ignore(new Region(10, 20, 30, 40))
        );
        eyes.close();
    }

    @Test
    public void testFloatingRegionsFluent() {
        eyes.setConfiguration(
                eyes.getConfiguration()
        );

        eyes.open(getApplicationName(), "TestFloatingRegionFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE)
                .floating(5)
                .floating(new Region(10, 20, 30, 40), 5, 10, 20, 15)
        );
        eyes.close();
    }

    @Test
    public void testMatchLevelExactFluent() {
        eyes.open(getApplicationName(), "TestMatchLevelExactFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE).matchLevel(MatchLevel.EXACT));

        TestResults result = eyes.close(false);
        SessionResults info = getTestInfo(result);
        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();

        Assert.assertEquals(matchLevel, MatchLevel.EXACT);
    }

    @Test
    public void testMatchLevelIgnoreColorsFluent() {
        eyes.open(getApplicationName(), "TestMatchLevelIgnoreColorsFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE).matchLevel(MatchLevel.IGNORE_COLORS));

        TestResults result = eyes.close(false);
        SessionResults info = getTestInfo(result);
        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();

        Assert.assertEquals(matchLevel, MatchLevel.CONTENT);
    }

    @Test
    public void testMatchLevelStrictFluent() {
        eyes.open(getApplicationName(), "TestMatchLevelStrictFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE).matchLevel(MatchLevel.STRICT));

        TestResults result = eyes.close(false);
        SessionResults info = getTestInfo(result);
        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();

        Assert.assertEquals(matchLevel, MatchLevel.STRICT);
    }

    @Test
    public void testMatchLevelLayoutFluent() {
        eyes.open(getApplicationName(), "TestMatchLevelLayoutFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE).matchLevel(MatchLevel.LAYOUT));

        TestResults result = eyes.close(false);
        SessionResults info = getTestInfo(result);
        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();

        Assert.assertEquals(matchLevel, MatchLevel.LAYOUT2);
    }

    @Test
    public void testMatchLevelExactNonFluent() {
        eyes.setConfiguration(eyes.getConfiguration().setMatchLevel(MatchLevel.EXACT));

        eyes.open(getApplicationName(), "TestMatchLevelExactNonFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE));

        TestResults result = eyes.close(false);
        SessionResults info = getTestInfo(result);
        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();

        Assert.assertEquals(matchLevel, MatchLevel.EXACT);
    }

    @Test
    public void testMatchLevelIgnoreColorsNonFluent() {
        eyes.setConfiguration(eyes.getConfiguration().setMatchLevel(MatchLevel.IGNORE_COLORS));

        eyes.open(getApplicationName(), "TestMatchLevelIgnoreColorsNonFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE));

        TestResults result = eyes.close(false);
        SessionResults info = getTestInfo(result);
        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();

        Assert.assertEquals(matchLevel, MatchLevel.CONTENT);
    }

    @Test
    public void testMatchLevelStrictNonFluent() {
        eyes.setConfiguration(eyes.getConfiguration().setMatchLevel(MatchLevel.STRICT));

        eyes.open(getApplicationName(), "TestMatchLevelStrictNonFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE));

        TestResults result = eyes.close(false);
        SessionResults info = getTestInfo(result);
        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();

        Assert.assertEquals(matchLevel, MatchLevel.STRICT);
    }

    @Test
    public void testMatchLevelLayoutNonFluent() {
        eyes.setConfiguration(eyes.getConfiguration().setMatchLevel(MatchLevel.LAYOUT));

        eyes.open(getApplicationName(), "TestMatchLevelLayoutNonFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE));

        TestResults result = eyes.close(false);
        SessionResults info = getTestInfo(result);
        MatchLevel matchLevel = info.getActualAppOutput()[0].getImageMatchSettings().getMatchLevel();

        Assert.assertEquals(matchLevel, MatchLevel.LAYOUT2);
    }

    @Test
    public void testExtractText() {
        eyes.open(getApplicationName(), "TestExtractText", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));

        List<String> result = eyes.extractText(new OcrRegion(EXTRACT_TEXT_IMAGE));
        eyes.close();

        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0), "This is the navigation bar");
    }

    @Test
    public void testIgnoreDisplacementsFluent() {
        eyes.open(getApplicationName(), "TestIgnoreDisplacementsFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE).ignoreDisplacements());

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Assert.assertTrue(info.getActualAppOutput()[0].getImageMatchSettings().getIgnoreDisplacements());
    }

    @Test
    public void testIgnoreDisplacementsNonFluent() {
        eyes.setConfiguration(eyes.getConfiguration().setIgnoreDisplacements(true));
        eyes.open(getApplicationName(), "TestIgnoreDisplacementsNonFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Assert.assertTrue(info.getActualAppOutput()[0].getImageMatchSettings().getIgnoreDisplacements());
    }

    @Test
    public void testCodedRegionsFluent() {
        eyes.open(getApplicationName(), "TestCodedRegionsFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE)
                .ignore(new Region(10, 20, 30, 40))
                .content(new Region(10, 20, 30, 40))
                .strict(new Region(10, 20, 30, 40))
                .layout(new Region(10, 20, 30, 40))
        );

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Region ignoreRegion = info.getActualAppOutput()[0].getImageMatchSettings().getIgnore()[0];
        Region layoutRegion = info.getActualAppOutput()[0].getImageMatchSettings().getLayout()[0];
        Region contentRegion = info.getActualAppOutput()[0].getImageMatchSettings().getContent()[0];
        Region strictRegion = info.getActualAppOutput()[0].getImageMatchSettings().getStrict()[0];

        Assert.assertEquals(ignoreRegion, new Region(10, 20, 30, 40), "ignore");
        Assert.assertEquals(layoutRegion, new Region(10, 20, 30, 40), "layout");
        Assert.assertEquals(contentRegion, new Region(10, 20, 30, 40), "content");
        Assert.assertEquals(strictRegion, new Region(10, 20, 30, 40), "strict");
    }

    @Test
    public void testEnablePatternsFluent() {
        eyes.open(getApplicationName(), "TestEnablePatternsFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE).enablePatterns());

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Assert.assertTrue(info.getActualAppOutput()[0].getImageMatchSettings().getEnablePatterns());
    }

    @Test
    public void testEnablePatternsNonFluent() {
        eyes.setConfiguration(eyes.getConfiguration().setEnablePatterns(true));
        eyes.open(getApplicationName(), "TestEnablePatternsNonFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.image(TEST_IMAGE));

        final TestResults result = eyes.close(false);
        final SessionResults info = getTestInfo(result);

        Assert.assertTrue(info.getActualAppOutput()[0].getImageMatchSettings().getEnablePatterns());
    }

    @Test
    public void testCheckRegionFluent() {
        eyes.open(getApplicationName(), "TestCheckRegionFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.check(Target.region(TEST_IMAGE, new Region(50, 50, 50, 50)));
        eyes.close();
    }

    @Test
    public void testCheckRegionNonFluent() {
        eyes.open(getApplicationName(), "TestCheckRegionNonFluent", new RectangleSize(TEST_IMAGE.getWidth(), TEST_IMAGE.getHeight()));
        eyes.checkRegion(TEST_IMAGE, new Region(50, 50, 50, 50));
        eyes.close();
    }
}
