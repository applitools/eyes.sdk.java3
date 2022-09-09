package coverage.drivers.appium;

import coverage.drivers.SELENIUM;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static coverage.drivers.CapabilitiesHelper.getIphoneXS;

public class IPhoneXS extends AbstractNativeBuilder implements NativeBuilder {
    public WebDriver build(String app, String orientation, boolean legacy) throws MalformedURLException {
        Capabilities caps = getIphoneXS(legacy);
        caps = prepareCaps(caps, app, orientation, legacy);
        return new IOSDriver(new URL(SELENIUM.SAUCE.url), caps);
    }
}
