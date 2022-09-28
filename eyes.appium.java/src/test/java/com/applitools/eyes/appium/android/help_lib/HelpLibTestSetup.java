package com.applitools.eyes.appium.android.help_lib;

import com.applitools.eyes.appium.android.AndroidTestSetup;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;
import java.time.Duration;

public abstract class HelpLibTestSetup extends AndroidTestSetup {

    private final String APP_VERSION = "1.8.7";

    private final String SOUSE_LABS_USERNAME = "applitools-dev";
    private final String SOUSE_LABS_ACCESS_KEY = "7f853c17-24c9-4d8f-a679-9cfde5b43951";
    private final String APPIUM_SOUSE_LABS_URL = "https://"+SOUSE_LABS_USERNAME+":" + SOUSE_LABS_ACCESS_KEY + "@ondemand.saucelabs.com:443/wd/hub";
    private final String APPIUM_LOCAL_URL = "http://localhost:4723/wd/hub";

    private final boolean IS_LOCAL_TEST = true;
    public enum MainMenuButton{
        btn_camera_view_activity,
        btn_list_view_activity,
        btn_recycler_view_activity,
        btn_text_fields_activity,
        btn_texture_view_activity,
        btn_view_pager_2_activity,
        btn_view_shadow_activity,
        btn_web_view_activity,
        btn_recycler_view_in_scroll_view_activity,
        btn_two_fragments_activity,
        btn_recycler_view_under_status_bar_activity,
        btn_web_view_dialog_activity,
        btn_recycler_view_with_margins_activity,
        btn_recycler_view_staggered_grid_activity,
        btn_recycler_view_nested_collapsing,
        btn_nested_scroll_view_activity,
        btn_nested_scroll_view_collapsing_toolbar_activity,
        btn_recycler_view_with_overlap_recycler_activity,
        btn_large_nested_scrollView_activity,
        btn_large_recyclerView_activity,
        btn_listView_stack_from_bottom_activity,
        btn_main_activity,
        btn_optionsMenu_activity,
        btn_recyclerView_with_appBar_activity,
        btn_view_pager_2_vertical_activity,

    }

    private MainMenuButton mMenuButton = null;
    @BeforeMethod
    public void setUp() {
        mMenuButton = getMainMenuButton();
        if(mMenuButton != null)
            clickMainMenuItem(mMenuButton);
    }

    @AfterMethod
    public void tearUp() {
        clearEDT();
        if(mMenuButton != null)
            driver.navigate().back();
    }

    @Override
    public void setCapabilities() {
        super.setCapabilities();
        capabilities.setCapability("deviceName","Google Pixel 5 GoogleAPI Emulator");
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("platformVersion","11.0");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("automationName", "UiAutomator2");

        capabilities.setCapability("newCommandTimeout", 2000);
//        capabilities.setCapability("noReset", true);
//        capabilities.setCapability("fullReset", false);

    }

    protected String getApplicationVersion(){
        return APP_VERSION;
    }
    @Override
    protected void setAppCapability() {
        capabilities.setCapability("app", "https://applitools.jfrog.io/artifactory/Examples/androidx/helper_lib/" +
                getApplicationVersion() + "/app-androidx-debug.apk");
    }


    @Override
    protected String getApplicationName() {
        return "Java Appium - Android Help Lib";
    }

    protected boolean isLocalTest(){
        return IS_LOCAL_TEST;
    }
    protected String getAppiumUrl(){
        return isLocalTest() ? APPIUM_LOCAL_URL : APPIUM_SOUSE_LABS_URL;
    }

    @Override
    protected void initDriver() throws MalformedURLException {
        appiumServerUrl = getAppiumUrl();
        super.initDriver();
    }


    public static int calculateMotionSpeed(int pixelPerSecond, int startPoint, int endPoint){
        return calculateMotionSpeed(pixelPerSecond,endPoint-startPoint);
    }
    public static int calculateMotionSpeed(int pixelPerSecond, int delta){
        return Math.abs(delta)/pixelPerSecond;
    }

    protected WebElement findEyesAppiumHelperEDT() {
        WebElement edt = null;
        for (int i = 0;;) {
            try {
                Thread.sleep(50);
                edt = driver.findElement(By.xpath("//*[@content-desc=\"EyesAppiumHelperEDT\"]"));
                break;
            } catch (Exception e) {
                if(++i >= 3)
                    throw new RuntimeException(e);
            }
        }
        return edt;
    }

    protected void sendEDTCommand(String command) {
        WebElement edt = findEyesAppiumHelperEDT();
        edt.clear();
        edt.sendKeys(command);
        edt.click();
    }

    protected String getEDTValue(){
        try {
            WebElement edt = findEyesAppiumHelperEDT();
            do {
                Thread.sleep(500);
            } while (edt.getText().equals("WAIT"));
            return edt.getText();
        }catch (InterruptedException e){
            throw new RuntimeException("getEDTValue failed: ", e);
        }
    }
    protected void clearEDT(){
        WebElement edt = findEyesAppiumHelperEDT();
        edt.clear();
    }
    protected void validateNoError(){
        String value = getEDTValue();
        if(value.startsWith("error;"))
            throw new RuntimeException("EDT error raw:" + value);
    }

    protected abstract MainMenuButton getMainMenuButton();

    protected void clickMainMenuItem(MainMenuButton menuBtn) {
        for(int page = 1; ; page++)
            try {
                WebElement webElement = driver.findElementById(menuBtn.name());
                webElement.click();
                return;

            }catch (Exception e){
                if(page > 2)
                    throw new RuntimeException("Unable to find element with id: " + menuBtn);
                TouchAction scrollAction = new TouchAction(driver);

                scrollAction.press(new PointOption().withCoordinates(5, 1500))
                        .waitAction(new WaitOptions().withDuration(Duration.ofSeconds(calculateMotionSpeed(1000,1400))))
                        .moveTo(new PointOption().withCoordinates(5, 100))
                        .release()
                        .perform();
            }
    }

}
