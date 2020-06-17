package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestPageObjects {

    private WebDriver driver;
    private final LogHandler logger = new StdoutLogHandler(false);

    public static class GoogleSearchPage {
        // The element is now looked up using the name attribute
        @FindBy(name = "q")
        private WebElement searchBox;

        public void searchFor(String text) {
            // We continue using the element just as before
            searchBox.sendKeys(text);
            searchBox.submit();
        }

        public WebElement getElement() {
            return searchBox;
        }
    }

    @DataProvider(name = "booleanDP")
    public Object[] dp() {
        return new Object[]{Boolean.FALSE};
    }

    @BeforeMethod
    public void beforeEach() {
        driver = SeleniumUtils.createChromeDriver();
    }

    @Test(dataProvider = "booleanDP")
    public void testPageObject(boolean useVisualGrid) {
        EyesRunner runner = useVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        String suffix = useVisualGrid ? "_VG" : "";
        Eyes eyes = new Eyes(runner);
        eyes.setLogHandler(logger);
        try {
            eyes.open(driver, "Java Eyes SDK", "testPageObject" + suffix, new RectangleSize(800, 800));
            driver.get("http://www.google.com/");
            GoogleSearchPage page = PageFactory.initElements(driver, GoogleSearchPage.class);
            //page.searchFor("Cheese");
            eyes.checkRegion(page.getElement());
            eyes.closeAsync();
        } finally {
            eyes.abortAsync();
            driver.quit();
            TestResultsSummary allTestResults = runner.getAllTestResults();
            System.out.println(allTestResults);
        }
    }
}
