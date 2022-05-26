package coverage.drivers.browsers;

import coverage.GlobalSetup;
import coverage.drivers.SELENIUM;
import coverage.drivers.CapabilitiesHelper;
import coverage.exceptions.MissingEnvVarException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class ChromeEmulatorBuilder implements DeviceBuilder {

    public void browser(String browser) {}

    public WebDriver build(boolean headless, boolean legacy, boolean executionGrid) throws MalformedURLException {
        ChromeOptions options = CapabilitiesHelper.getAndroid8ChromeEmulator(false);
        if (GlobalSetup.CI) {
            options.addArguments("--no-sandbox", "--disable-gpu");
        }
        if (executionGrid) {
            if (GlobalSetup.EG_URL == null) throw new MissingEnvVarException("EXECUTION_GRID_URL");

            return new RemoteWebDriver(new URL(GlobalSetup.EG_URL), options);
        }
        if (GlobalSetup.useDocker) return new RemoteWebDriver(new URL(SELENIUM.CHROME_LOCAL.url), options);
        return new ChromeDriver(options);
    }
}
