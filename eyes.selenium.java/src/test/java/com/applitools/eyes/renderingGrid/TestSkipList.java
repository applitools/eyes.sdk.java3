package com.applitools.eyes.renderingGrid;

import com.applitools.connectivity.ServerConnector;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TaskListener;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.TestDataProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.ReportingTestSuite;
import com.applitools.eyes.utils.SeleniumUtils;
import com.applitools.eyes.utils.TestUtils;
import com.applitools.eyes.visualgrid.model.RenderRequest;
import com.applitools.eyes.visualgrid.model.RunningRender;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

public class TestSkipList extends ReportingTestSuite {

    public TestSkipList() {
        super.setGroupName("selenium");
    }

    @Test
    public void testSkipList() throws InterruptedException {
        final List<Set<String>> resourceMaps = new ArrayList<>();

        ServerConnector serverConnector = spy(new ServerConnector());
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                RenderRequest renderRequest = invocation.getArgument(1);
                resourceMaps.add(renderRequest.getResources().keySet());
                invocation.callRealMethod();
                return null;
            }
        }).when(serverConnector).render(ArgumentMatchers.<TaskListener<List<RunningRender>>>any(), any(RenderRequest.class));

        VisualGridRunner runner = new VisualGridRunner(30);
        Eyes eyes = new Eyes(runner);

        Configuration conf = eyes.getConfiguration();
        conf.setTestName("Skip List");
        conf.setAppName("Visual Grid Render Test");
        conf.setViewportSize(new RectangleSize(800, 600));
        conf.setBatch(TestDataProvider.batchInfo);
        eyes.setConfiguration(conf);

        eyes.setSaveNewTests(false);
        eyes.setLogHandler(TestUtils.initLogger());
        eyes.setServerConnector(serverConnector);
        ChromeDriver driver = SeleniumUtils.createChromeDriver();

        try {
            eyes.open(driver);
            driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage/");
            eyes.check("Check1", Target.window());
            Thread.sleep(10000);
            eyes.check("Check2", Target.window());
            eyes.closeAsync();
        } finally {
            driver.quit();
            eyes.abortIfNotClosed();
            runner.getAllTestResults();
        }

        Set<String> expectedUrls = new HashSet<>();
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

        Assert.assertEquals(resourceMaps.size(), 2);
        for (Set<String> resourceMap : resourceMaps) {
            for (String url : resourceMap) {
                if (url.contains("https://applitools.github.io/demo/TestPages/VisualGridTestPage/frame.html")) {
                    resourceMap.remove(url);
                    break;
                }
            }
        }

        Assert.assertEquals(resourceMaps.get(0), expectedUrls);
        Assert.assertEquals(resourceMaps.get(1), expectedUrls);
    }
}
