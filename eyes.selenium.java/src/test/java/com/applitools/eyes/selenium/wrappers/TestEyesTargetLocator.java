package com.applitools.eyes.selenium.wrappers;

import com.applitools.eyes.Logger;
import com.applitools.eyes.config.Feature;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.SeleniumEyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEyesTargetLocator {

    @Test
    public void testDefaultContentFeature() {
        EyesSeleniumDriver driver = mock(EyesSeleniumDriver.class);
        when(driver.getFrameChain()).thenReturn(new FrameChain(new Logger()));
        SeleniumEyes eyes = mock(SeleniumEyes.class);
        when(driver.getEyes()).thenReturn(eyes);
        Configuration configuration = new Configuration();
        when(eyes.getConfiguration()).thenReturn(configuration);

        final AtomicInteger count = new AtomicInteger(0);
        WebDriver.TargetLocator locator = mock(WebDriver.TargetLocator.class);
        when(locator.defaultContent()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                count.set(count.get() + 1);
                return null;
            }
        });

        EyesTargetLocator eyesTargetLocator = null;
                //new EyesTargetLocator(driver, new Logger(), locator);
        //eyesTargetLocator.defaultContent();
        Assert.assertEquals(count.get(), 1);

        configuration.setFeatures(Feature.NO_SWITCH_WITHOUT_FRAME_CHAIN);
        count.set(0);
        eyesTargetLocator.defaultContent();
        Assert.assertEquals(count.get(), 0);
    }
}
