package com.applitools.eyes.selenium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.capture.SafariScreenshotImageProvider;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.ScreenOrientation;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestIOSCrop extends ReportingTestSuite {

    public TestIOSCrop() {
        super.setGroupName("selenium");
    }

    @DataProvider(name = "IOSDevices", parallel = true)
    public static Object[][] IOSDevices() {
        List<Object[]> devices = new ArrayList<>(Arrays.asList(new Object[][]{
                {"iPad Air 2 Simulator", "10.3", ScreenOrientation.LANDSCAPE, 2048, 1408},
                {"iPad Air 2 Simulator", "12.0", ScreenOrientation.LANDSCAPE, 2048, 1396},
                {"iPad Air 2 Simulator", "11.3", ScreenOrientation.LANDSCAPE, 2048, 1331},
                {"iPad Pro (12.9 inch) (2nd generation) Simulator", "11.0", ScreenOrientation.LANDSCAPE, 2732, 1908},
                {"iPad Pro (10.5 inch) Simulator", "11.0", ScreenOrientation.LANDSCAPE, 2224, 1528},

                {"iPad (5th generation) Simulator", "11.0", ScreenOrientation.PORTRAIT, 1536, 1843},
                {"iPad Air 2 Simulator", "10.3", ScreenOrientation.PORTRAIT, 1536, 1920},
                {"iPad Air 2 Simulator", "11.0", ScreenOrientation.PORTRAIT, 1536, 1908},
                {"iPad Pro (12.9 inch) (2nd generation) Simulator", "11.0", ScreenOrientation.PORTRAIT, 2048, 2592},
                {"iPad Pro (10.5 inch) Simulator", "11.0", ScreenOrientation.PORTRAIT, 1668, 2084},

                {"iPhone XS Simulator", "12.2", ScreenOrientation.LANDSCAPE, 2172, 912},
                {"iPhone 11 Pro Simulator", "13.0", ScreenOrientation.LANDSCAPE, 2172, 813},
                {"iPhone XS Max Simulator", "12.2", ScreenOrientation.LANDSCAPE, 2424, 1030},
                {"iPhone 11 Pro Max Simulator", "13.0", ScreenOrientation.LANDSCAPE, 2424, 922},
                {"iPhone XR Simulator", "12.2", ScreenOrientation.LANDSCAPE, 1616, 686},
                {"iPhone 11 Simulator", "13.0", ScreenOrientation.LANDSCAPE, 1616, 620},
                {"iPhone 6 Plus Simulator", "11.0", ScreenOrientation.LANDSCAPE, 2208, 1092},
                {"iPhone 7 Simulator", "10.3", ScreenOrientation.LANDSCAPE, 1334, 662},
                {"iPhone 7 Plus Simulator", "10.3", ScreenOrientation.LANDSCAPE, 2208, 1110},
                {"iPhone 5s Simulator", "10.3", ScreenOrientation.LANDSCAPE, 1136, 464},

                {"iPhone XS Simulator", "12.2", ScreenOrientation.PORTRAIT, 1125, 1905},
                {"iPhone XS Max Simulator", "12.2", ScreenOrientation.PORTRAIT, 1242, 2157},
                {"iPhone XR Simulator", "12.2", ScreenOrientation.PORTRAIT, 828, 1438},
                {"iPhone 6 Plus Simulator", "11.0", ScreenOrientation.PORTRAIT, 1242, 1866},
                {"iPhone 7 Simulator", "10.3", ScreenOrientation.PORTRAIT, 750, 1118},
                {"iPhone 5s Simulator", "10.3", ScreenOrientation.PORTRAIT, 640, 920}

        }));
        return devices.toArray(new Object[0][]);
    }

    @Test(dataProvider = "IOSDevices")
    public void TestIOSSafariCrop(String deviceName, String platformVersion, ScreenOrientation deviceOrientation, int vpWidth, int vpHeight) {
        super.addSuiteArg("deviceName", deviceName);
        super.addSuiteArg("platformVersion", platformVersion);
        super.addSuiteArg("deviceOrientation", deviceOrientation);
        super.addSuiteArg("vpWidth", vpWidth);
        super.addSuiteArg("vpHeight", vpHeight);
        String testName = deviceName + " " + platformVersion + " " + deviceOrientation;
        String sanitizedTestName = testName.replaceAll("[^\\.A-Za-z0-9]", "_");

        Logger logger = new Logger();
        logger.setLogHandler(TestUtils.initLogger(testName));
        InputStream inputStream = TestIOSCrop.class.getResourceAsStream("/IOSImages/Input/" + sanitizedTestName + ".png");
        BufferedImage input = ImageUtils.imageFromStream(inputStream);
        InputStream expectedStream = TestIOSCrop.class.getResourceAsStream("/IOSImages/Expected/" + sanitizedTestName + ".png");
        BufferedImage expected = ImageUtils.imageFromStream(expectedStream);

        if (input != null && expected != null) {
            BufferedImage output = SafariScreenshotImageProvider.cropIOSImage(input, new RectangleSize(vpWidth, vpHeight), logger);
            if (!TestUtils.runOnCI) {
                String inputPath = TestUtils.logsPath + File.separator + "java" + File.separator + "IOSCrop" + File.separator + sanitizedTestName + "_input.png";
                ImageUtils.saveImage(input, inputPath);
                String outputPath = TestUtils.logsPath + File.separator + "java" + File.separator + "IOSCrop" + File.separator + sanitizedTestName + "_output.png";
                ImageUtils.saveImage(output, outputPath);
            }
            Assert.assertEquals(output.getWidth(), expected.getWidth(), "Width");
            Assert.assertEquals(output.getHeight(), expected.getHeight(), "Height");
            Assert.assertTrue(ImageUtils.areImagesEqual(output, expected), "BufferedImage comparison");
        }
    }
}
