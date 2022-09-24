package com.applitools.eyes.appium;

import com.applitools.eyes.LazyLoadOptions;
import org.junit.Test;
import org.testng.Assert;

public class LazyLoadOptionsTest {

    @Test
    public void should_ReturnDefault_When_Initialized() {
        AppiumCheckSettings sc = new AppiumCheckSettings();
        sc = sc.lazyLoad();
        Assert.assertEquals((int) sc.getLazyLoadOptions().getScrollLength(), 300);
        Assert.assertEquals((int) sc.getLazyLoadOptions().getWaitingTime(), 2000);
        Assert.assertEquals((int) sc.getLazyLoadOptions().getMaxAmountToScroll(), 15000);
    }

    @Test
    public void should_ReturnAssigned_When_Called() {
        AppiumCheckSettings sc = new AppiumCheckSettings();
        sc = sc.lazyLoad(new LazyLoadOptions().maxAmountToScroll(111).scrollLength(100).waitingTime(1000));
        Assert.assertEquals((int) sc.getLazyLoadOptions().getScrollLength(), 100);
        Assert.assertEquals((int) sc.getLazyLoadOptions().getWaitingTime(), 1000);
        Assert.assertEquals((int) sc.getLazyLoadOptions().getMaxAmountToScroll(), 111);

    }

}
