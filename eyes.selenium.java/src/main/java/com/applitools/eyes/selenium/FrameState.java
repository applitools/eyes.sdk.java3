package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesSeleniumDriver;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import org.openqa.selenium.WebElement;

public class FrameState {
    public FrameState(EyesSeleniumDriver driver, WebElement scrolledElement, String cssTransform, Location scrollPosition, String overflow, FrameChain frameChain) {
        this.driver = driver;
        this.scrolledElement = scrolledElement;
        this.cssTransform = cssTransform;
        this.scrollPosition = scrollPosition;
        this.overflow = overflow;
        this.frameChain = frameChain;
    }

    private EyesSeleniumDriver driver;
    private WebElement scrolledElement;
    private String cssTransform;
    private String overflow;
    private Location scrollPosition;
    private FrameChain frameChain;

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
    }

    public static FrameState getCurrentFrameState(EyesSeleniumDriver driver, WebElement scrolledElement) {
        String data = (String) driver.executeScript(
                "var el=arguments[0]; return el.style.transform+'#'+el.scrollLeft+';'+el.scrollTop+'#'+el.style.overflow",
                scrolledElement);

        String[] datums = data.split("#", -1);
        String cssTransform = datums[0];
        Location scrollPosition = EyesDriverUtils.parseLocationString(datums[1]);
        String overflow = datums[2];
        FrameChain frameChain = driver.getFrameChain().clone();
        FrameState frameState = new FrameState(driver, scrolledElement, cssTransform, scrollPosition, overflow, frameChain);
        return frameState;
    }
}
