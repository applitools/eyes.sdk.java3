package com.applitools.eyes.selenium;


import com.applitools.eyes.LazyLoadOptions;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import org.junit.Test;
import org.testng.Assert;

public class TestLazyLoadOptions {

  @Test
  public void should_ReturnDefault_When_Initialized() {
    SeleniumCheckSettings sc = new SeleniumCheckSettings();
    sc = sc.lazyLoad();
    Assert.assertEquals((int) sc.getLazyLoadOptions().getScrollLength(), 300);
    Assert.assertEquals((int) sc.getLazyLoadOptions().getWaitingTime(), 2000);
    Assert.assertEquals((int) sc.getLazyLoadOptions().getMaxAmountToScroll(), 15000);
  }

  @Test
  public void should_ReturnAssigned_When_Called() {
    SeleniumCheckSettings sc = new SeleniumCheckSettings();
    sc = sc.lazyLoad(new LazyLoadOptions().maxAmountToScroll(111).scrollLength(100).waitingTime(1000));
    Assert.assertEquals((int) sc.getLazyLoadOptions().getScrollLength(), 100);
    Assert.assertEquals((int) sc.getLazyLoadOptions().getWaitingTime(), 1000);
    Assert.assertEquals((int) sc.getLazyLoadOptions().getMaxAmountToScroll(), 111);

  }
}
