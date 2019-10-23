package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class Charter2 {
    public static void main(String[] args) throws Exception {

        WebDriver driver = SeleniumUtils.createChromeDriver();

        Eyes eyes = new Eyes();

        eyes.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));

        try {
            WebDriver eyesDriver = eyes.open(driver, "Hello World!", "My first Selenium C# test!", new RectangleSize(800, 680));

            // Navigate the browser to the "hello world!" web-site.
            eyesDriver.get("file:///C:/Users/USER/Downloads/Business%20TV%20Service%20-%20Office%20&%20Waiting%20Room%20TV%20_%20Spectrum%20Business.html");

            Thread.sleep(5000);

            StitchMode originalStitchMode = eyes.getStitchMode();
            eyes.setStitchMode(StitchMode.CSS);


            WebElement frame = eyesDriver.findElement(By.id("clu_contents"));
            eyesDriver.switchTo().frame(frame);
            ((JavascriptExecutor)driver).executeScript("var s = document.querySelectorAll('span.small.sortable');for (var i=0;i<s.length;++i) s[i].innerText=(i+1);");

            WebElement element = eyesDriver.findElement(By.cssSelector("body"));
            Dimension size = element.getSize();
            Point location = element.getLocation();

            Rectangle elementRect = new Rectangle(location, size);
            Rectangle rect;
            List<ICheckSettings> targets = new ArrayList<>();
            for (int i = location.y, c = 1; i < location.y + size.height; i += 10000, c++)
            {
                if ((location.y + size.height) > i + 10000)
                {
                    rect = new Rectangle(location.x, i, 10000, size.width);
                }
                else
                {
                    rect = new Rectangle(location.x, i, (location.y + size.height) - i, size.width);
                }
                targets.add(Target.region(new Region(rect.x, rect.y, rect.width, rect.height)));
            }

            eyes.check(targets.toArray(new ICheckSettings[0])); //Require check(Array)

            eyes.setStitchMode(originalStitchMode);

            // End the test.
            eyes.close();

        }
        finally {
            driver.quit();

            eyes.abortIfNotClosed();
        }
    }
}