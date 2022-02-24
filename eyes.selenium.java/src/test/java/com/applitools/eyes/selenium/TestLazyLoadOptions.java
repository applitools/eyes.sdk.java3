package com.applitools.eyes.selenium;


import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.fluent.Target;
import org.junit.Test;
import org.testng.Assert;

public class TestLazyLoadOptions {

  @Test
  public void should_ReturnDefault_When_Initialized() {
    SeleniumCheckSettings sc = new SeleniumCheckSettings();
    sc = sc.lazyLoad();
    Assert.assertEquals(300,(int) sc.getLazyLoadOptions().getScrollLength());
    Assert.assertEquals(2000,(int) sc.getLazyLoadOptions().getWaitingTime());
    Assert.assertEquals(15000,(int) sc.getLazyLoadOptions().getPageHeight());
  }

  @Test
  public void should_ReturnAssigned_When_Called() {
    SeleniumCheckSettings sc = new SeleniumCheckSettings();
    sc = sc.lazyLoad(new LazyLoadOptions().pageHeight(111).scrollLength(100).waitingTime(1000));
    Assert.assertEquals(100,(int) sc.getLazyLoadOptions().getScrollLength());
    Assert.assertEquals(1000,(int) sc.getLazyLoadOptions().getWaitingTime());
    Assert.assertEquals(111,(int) sc.getLazyLoadOptions().getPageHeight());

  }
}
