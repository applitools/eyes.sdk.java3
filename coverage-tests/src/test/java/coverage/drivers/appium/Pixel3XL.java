package coverage.drivers.appium;

import coverage.drivers.SELENIUM;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static coverage.drivers.CapabilitiesHelper.getPixel3XL;

public class Pixel3XL extends AbstractNativeBuilder implements NativeBuilder {
    public WebDriver build(String app, String orientation, boolean legacy) throws MalformedURLException {
        Capabilities caps = getPixel3XL(legacy);
        caps = prepareCaps(caps, app, orientation, legacy);
        return new AndroidDriver(new URL(SELENIUM.SAUCE.url), caps);
    }
}
