package coverage.drivers.appium;

import coverage.drivers.SELENIUM;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static coverage.drivers.CapabilitiesHelper.getIphone12;

public class IPhone12 extends AbstractNativeBuilder implements NativeBuilder {
    public WebDriver build(String app, String orientation, boolean legacy) throws MalformedURLException {
        Capabilities caps = getIphone12(legacy);
        caps = prepareCaps(caps, app, orientation, legacy);
        return new IOSDriver<>(new URL(SELENIUM.SAUCE.url), caps);
    }
}