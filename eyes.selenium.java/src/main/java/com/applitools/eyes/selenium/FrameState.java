package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FrameState {
    private final EyesSeleniumDriver driver;
    private final WebElement scrolledElement;
    private final String cssTransform;
    private final Location scrollPosition;
    private final FrameChain frameChain;

    private final String overflow;
    private final String originalHtmlOverflow;

    public FrameState(EyesSeleniumDriver driver, WebElement scrolledElement, String cssTransform, Location scrollPosition,
                      FrameChain frameChain, String overflow, String originalHtmlOverflow) {
        this.driver = driver;
        this.scrolledElement = scrolledElement;
        this.cssTransform = cssTransform;
        this.scrollPosition = scrollPosition;
        this.frameChain = frameChain;
        this.overflow = overflow;
        this.originalHtmlOverflow = originalHtmlOverflow;
    }

    public void restore() {
        EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();
        switchTo.frames(frameChain);
        driver.executeScript(
                "var el=arguments[0]; " +
                        "el.style.transform='" + cssTransform + "';" +
                        "el.scrollLeft=" + scrollPosition.getX() + ";" +
                        "el.scrollTop=" + scrollPosition.getY() + ";" +
                        "el.style.overflow='" + overflow + "'",
                scrolledElement);

        if (originalHtmlOverflow == null) {
            return;
        }

        WebElement html = driver.findElement(By.tagName("html"));
        driver.executeScript(String.format("arguments[0].style.overflow='%s'", originalHtmlOverflow), html);
    }

    public static FrameState getCurrentFrameState(EyesSeleniumDriver driver, WebElement scrolledElement, boolean isDefaultSRE) {
        String script = "var el=arguments[0]; return el.style.transform+'#'+el.scrollLeft+';'+el.scrollTop+'#'+el.style.overflow";
        String result = (String) driver.executeScript(script, scrolledElement);
        String[] styleAttributes = result.split("#", -1);
        String cssTransform = styleAttributes[0];
        Location scrollPosition = EyesDriverUtils.parseLocationString(styleAttributes[1]);
        String overflow = styleAttributes[2];
        FrameChain frameChain = driver.getFrameChain().clone();

        String htmlOverflow = null;
        if (isDefaultSRE) {
            WebElement body = driver.findElement(By.tagName("body"));
            if (scrolledElement.equals(body)) {
                WebElement html = driver.findElement(By.tagName("html"));
                result = (String) driver.executeScript(script, html);
                styleAttributes = result.split("#", -1);
                htmlOverflow = styleAttributes[2];
            }
        }
        return new FrameState(driver, scrolledElement, cssTransform, scrollPosition, frameChain, overflow, htmlOverflow);
    }
}
