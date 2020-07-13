package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.positioning.PositionProvider;
import org.openqa.selenium.WebElement;

public interface ScrollPositionProvider extends PositionProvider {
    void setPosition(WebElement element);
}
