package coverage.drivers.browsers;

import coverage.drivers.CapabilitiesBuilder;
import coverage.drivers.SELENIUM;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class InternetExplorer11Builder implements Builder{
    public WebDriver build(boolean headless, boolean legacy, boolean executionGrid) throws MalformedURLException {
        Capabilities caps = CapabilitiesBuilder.getIE11(legacy);
        return new RemoteWebDriver(new URL(SELENIUM.SAUCE.url), caps);
    }
}
