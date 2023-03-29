package coverage;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.fasterxml.jackson.databind.JsonNode;
import coverage.drivers.browsers.DriverBuilder;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import com.applitools.eyes.metadata.SessionResults;
import com.applitools.eyes.utils.TestUtils;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestSetup extends GlobalSetup {

    protected Eyes eyes;
    protected EyesRunner runner;

    // Eyes configuration

    public DriverBuilder buildDriver() {
        return new DriverBuilder();
    }

    public void initEyes(boolean isVisualGrid, String stitching, String branchName) {
        runner = isVisualGrid ? new VisualGridRunner(10) : new ClassicRunner();
        eyes = new Eyes(runner);
        eyes.setMatchTimeout(0);
        eyes.setApiKey(apiKey);
        eyes.setBranchName(branchName);
        eyes.setParentBranchName("master");
        eyes.setBatch(batch);
        eyes.setSaveNewTests(false);
        eyes.setHideScrollbars(true);
        eyes.setHideCaret(true);
        String showLogs = System.getenv("APPLITOOLS_SHOW_LOGS");
        String verbose = System.getenv("APPLITOOLS_SHOW_LOGS_VERBOSE");
        if (showLogs != null && showLogs.equals("true")) {
            eyes.setLogHandler(new StdoutLogHandler((verbose != null && verbose.equals("true"))));
        }
    }

    // Eyes configuration

    public void setEnablePatterns(Boolean shouldSet) { eyes.configure().setEnablePatterns(shouldSet); }
    public void setStitchOverlap(Integer overlap) { eyes.setStitchOverlap(overlap); }
    public void setAppName(String appName) { eyes.setAppName(appName);}
    public void setStitchMode(String stitchMode) { eyes.setStitchMode(stitchMode.equals("CSS") ? StitchMode.CSS : StitchMode.SCROLL);}
    public void setParentBranchName(String parentTestBranch) { eyes.setParentBranchName(parentTestBranch);}
    public void setHideScrollbars(Boolean hideScrollbars) { eyes.setHideScrollbars(hideScrollbars);}
    public void setIsDisabled(Boolean isDisabled){ eyes.setIsDisabled(isDisabled);}
    public void setBranchName(String name) {eyes.setBranchName(name);}
    public void setApiKey(String apiKey) {
        if (apiKey.startsWith("APPLITOOLS")) {
            eyes.setApiKey(System.getenv(apiKey));
        } else {
            eyes.setApiKey(apiKey);
        }
    }
    public void setServerUrl(String serverUrl) {
        if (serverUrl.startsWith("APPLITOOLS")) {
            eyes.setServerUrl(System.getenv(serverUrl));
        } else {
            eyes.setServerUrl(serverUrl);
        }
    }
    public void setBrowsersInfo(IRenderingBrowserInfo ...browsers){
        Configuration conf = eyes.getConfiguration();
        conf.addBrowsers(browsers);
        eyes.setConfiguration(conf);
    }
    public void setWaitBeforeCapture(int waitBeforeCapture) {
        eyes.configure().setWaitBeforeCapture(waitBeforeCapture);
    }
    public void setAccessibilitySettings(AccessibilitySettings settings) {
            ImageMatchSettings current = eyes.getDefaultMatchSettings();
            current.setAccessibilitySettings(settings);
            eyes.setDefaultMatchSettings(current);
    }
    public void setLayoutBreakpoints(int... breakpoints) {
            eyes.configure().setLayoutBreakpoints(breakpoints);
    }
    public void setLayoutBreakpoints(boolean shouldSet) {
        eyes.configure().setLayoutBreakpoints(shouldSet);
    }

    // Open

    public void open(WebDriver driver, String appName, String testName) { eyesDriver = eyes.open(driver, appName, testName); }
    public void open(WebDriver driver, String appName, String testName, RectangleSize rectangleSize){ eyesDriver = eyes.open(driver, appName, testName, rectangleSize); }

    // Test info

    public SessionResults getTestInfo(TestResults results) {
        SessionResults sessionResults = null;
            try {
                sessionResults = TestUtils.getSessionResults(eyes.getApiKey(), results);
            } catch (Throwable e) {
                e.printStackTrace();
                Assert.fail("Exception appeared while getting session results");
            }
        return sessionResults;
    }

    public com.applitools.eyes.metadata.BatchInfo getBatchInfo(TestResults results) {
        com.applitools.eyes.metadata.BatchInfo batchInfo = null;
        try {
            batchInfo = TestUtils.getBatchResults(eyes.getApiKey(), results);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Exception appeared while getting session results");
        }
        return batchInfo;
    }

    public void setBatch(String name, HashMap<String, String>[] properties) {
        BatchInfo batch = new BatchInfo(name);
        for (Map<String, String> property : properties) {
            batch.addProperty(property.get("name"), property.get("value"));
        }
        eyes.setBatch(batch);
    }

    public void setBatch(String name) {
        BatchInfo batch = new BatchInfo(name);
        eyes.setBatch(batch);
    }

    public void setBatch(String name, String id) {
        BatchInfo batch = new BatchInfo(name);
        batch.setId(id);
        eyes.setBatch(batch);
    }

    public JsonNode getDom(TestResults results, String domId) {
        JsonNode dom = null;
        try {
            String accountId = new QueryStringDecoder(results.getUrl()).parameters().get("accountId").get(0);
            dom = TestUtils.getStepDom(eyes.getLogger(), eyes.getServerUrl().toString(), GlobalSetup.readApiKey, domId, accountId);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail("Exception appeared while getting session results");
        }
        return dom;
    }

    public List<JsonNode> getNodesByAttributes(JsonNode dom, String attribute) {
        List<JsonNode> nodes = new ArrayList<>();
        if (dom.get("attributes") != null && dom.get("attributes").get(attribute) != null) {
            nodes.add(dom);
        }

        JsonNode children = dom.get("childNodes");
        if (children == null) {
            return nodes;
        }

        for (int i = 0; i < children.size(); i++) {
            nodes.addAll(getNodesByAttributes(children.get(i), attribute));
        }

        return nodes;
    }

    public EyesRunner getRunner(){
        return runner;
    }

    public void hover(WebElement element){
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();
    }
}
