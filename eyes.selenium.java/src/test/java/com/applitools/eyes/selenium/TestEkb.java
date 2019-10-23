package com.applitools.eyes.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestEkb extends TestSetup {

    @Factory(dataProvider = "dp", dataProviderClass = TestDataProvider.class)
    public TestEkb(Capabilities caps, String mode) {
        super("Eyes Selenium SDK - Applitools EKB", caps, mode);
        testedPageUrl = "https://www.applitools.com/docs/topics/working-with-test-batches/working-with-test-batches.html";
    }

    @Test
    public void TestEKBPage(){
        //VisualGridEyes.checkRegion(By.cssSelector("div.horizontal-page"), "EKB - Working with test batches", true);
        getEyes().checkRegion(By.cssSelector(".docs.page-container"), "EKB - Working with test batches", true);
//        VisualGridEyes.check("EKB - Working with test batches",
//                Target.region(By.cssSelector(".docs.page-container"))
//                      .fully()
//                      //.scrollRootElement(By.cssSelector("div.horizontal-page"))
//        );
    }
}
