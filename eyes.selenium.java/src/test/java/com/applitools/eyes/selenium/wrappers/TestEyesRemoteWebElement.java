package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEyesRemoteWebElement {

    @Test
    public void testCurrentCssLocation() {
        EyesSeleniumDriver driver = mock(EyesSeleniumDriver.class);
        RemoteWebElement element = mock(RemoteWebElement.class);
        EyesRemoteWebElement eyesElement = new EyesRemoteWebElement(new Logger(), driver, element);

        when(driver.executeScript(anyString(), eq(element))).thenReturn("translate( -10 ,  -15px)");
        Assert.assertEquals(eyesElement.getCurrentCssStitchingLocation(), new Location(10, 15));

        when(driver.executeScript(anyString(), eq(element))).thenReturn("translate( -10px ,  -15)");
        Assert.assertEquals(eyesElement.getCurrentCssStitchingLocation(), new Location(10, 15));

        when(driver.executeScript(anyString(), eq(element))).thenReturn("translate( -10.5 ,  -15 )");
        Assert.assertEquals(eyesElement.getCurrentCssStitchingLocation(), new Location(10, 15));

        when(driver.executeScript(anyString(), eq(element))).thenReturn(null);
        Assert.assertNull(eyesElement.getCurrentCssStitchingLocation());

        when(driver.executeScript(anyString(), eq(element))).thenReturn("");
        Assert.assertNull(eyesElement.getCurrentCssStitchingLocation());

        when(driver.executeScript(anyString(), eq(element))).thenReturn("hello");
        Assert.assertNull(eyesElement.getCurrentCssStitchingLocation());

        when(driver.executeScript(anyString(), eq(element))).thenThrow(new RuntimeException());
        Assert.assertNull(eyesElement.getCurrentCssStitchingLocation());
    }
}
