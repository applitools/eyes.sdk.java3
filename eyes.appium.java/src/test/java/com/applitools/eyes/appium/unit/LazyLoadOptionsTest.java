package com.applitools.eyes.appium.unit;

import com.applitools.eyes.LazyLoadOptions;
import com.applitools.eyes.appium.AppiumCheckSettings;
import com.applitools.eyes.appium.Target;
import org.junit.Test;
import org.testng.Assert;

public class LazyLoadOptionsTest {

    @Test
    public void should_ReturnDefault_When_Initialized() {
        AppiumCheckSettings sc = Target.window().lazyLoad();
        Assert.assertEquals((int) sc.getLazyLoadOptions().getScrollLength(), 300);
        Assert.assertEquals((int) sc.getLazyLoadOptions().getWaitingTime(), 2000);
        Assert.assertEquals((int) sc.getLazyLoadOptions().getMaxAmountToScroll(), 15000);
    }

    @Test
    public void should_ReturnAssigned_When_Called() {
        AppiumCheckSettings sc = Target.window().lazyLoad(new LazyLoadOptions().maxAmountToScroll(111).scrollLength(100).waitingTime(1000));
        Assert.assertEquals((int) sc.getLazyLoadOptions().getScrollLength(), 100);
        Assert.assertEquals((int) sc.getLazyLoadOptions().getWaitingTime(), 1000);
        Assert.assertEquals((int) sc.getLazyLoadOptions().getMaxAmountToScroll(), 111);

    }

}
