package coverage.drivers.browsers;

import coverage.drivers.CapabilitiesBuilder;
import coverage.drivers.SELENIUM;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class Safari12Builder implements Builder{
    public WebDriver build(boolean headless, boolean legacy, boolean localDriver) throws MalformedURLException {
        Capabilities caps = CapabilitiesBuilder.getSafari12(legacy);
        return new RemoteWebDriver(new URL(SELENIUM.SAUCE.url), caps);
    }
}
