package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.TreeSet;

public class TestSkipList {

    @Test
    public void Test() throws InterruptedException {
        VisualGridRunner runner = new VisualGridRunner(30);
        Eyes eyes = new Eyes(runner);

        eyes.setLogHandler(TestUtils.initLogger());

        Configuration conf = new Configuration();
        conf.setTestName("Skip List");
        conf.setAppName("Visual Grid Render Test");
        conf.setBatch(TestDataProvider.batchInfo);
        conf.setUseDom(true);
        conf.setSendDom(true);

        eyes.setConfiguration(conf);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();

        try {
            eyes.open(driver);
            driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage/");
            eyes.check("Check1", Target.window());

            Set<String> expectedUrls = new TreeSet<>();
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/AbrilFatface-Regular.woff2");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/applitools_logo_combined.svg");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/company_name.png");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/innerstyle0.css");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/innerstyle1.css");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/innerstyle2.css");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/logo.svg");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/minions-800x500_green_sideways.png");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/minions-800x500.jpg");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/slogan.svg");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/style0.css");
            expectedUrls.add("https://applitools.github.io/demo/TestPages/VisualGridTestPage/style1.css");
            expectedUrls.add("https://fonts.googleapis.com/css?family=Raleway");
            expectedUrls.add("https://fonts.googleapis.com/css?family=Unlock");
            expectedUrls.add("https://fonts.gstatic.com/s/raleway/v18/1Ptxg8zYS_SKggPN4iEgvnHyvveLxVvaorCFPrEHJA.woff2");
            expectedUrls.add("https://fonts.gstatic.com/s/raleway/v18/1Ptxg8zYS_SKggPN4iEgvnHyvveLxVvaorCGPrEHJA.woff2");
            expectedUrls.add("https://fonts.gstatic.com/s/raleway/v18/1Ptxg8zYS_SKggPN4iEgvnHyvveLxVvaorCHPrEHJA.woff2");
            expectedUrls.add("https://fonts.gstatic.com/s/raleway/v18/1Ptxg8zYS_SKggPN4iEgvnHyvveLxVvaorCIPrE.woff2");
            expectedUrls.add("https://fonts.gstatic.com/s/raleway/v18/1Ptxg8zYS_SKggPN4iEgvnHyvveLxVvaorCMPrEHJA.woff2");
            expectedUrls.add("https://fonts.gstatic.com/s/unlock/v10/7Au-p_8ykD-cDl72LwLT.woff2");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/css/all.css");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-brands-400.eot");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-brands-400.svg");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-brands-400.ttf");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-brands-400.woff");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-brands-400.woff2");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-regular-400.eot");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-regular-400.svg");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-regular-400.ttf");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-regular-400.woff");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-regular-400.woff2");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-solid-900.eot");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-solid-900.svg");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-solid-900.ttf");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-solid-900.woff");
            expectedUrls.add("https://use.fontawesome.com/releases/v5.8.2/webfonts/fa-solid-900.woff2");

            Thread.sleep(5_000);

            eyes.check("Check2", Target.window());

            eyes.closeAsync();

            Assert.assertEquals(new TreeSet<>(runner.getCachedResources().keySet()), expectedUrls);

            runner.getAllTestResults();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
        }
    }
}
