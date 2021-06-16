package coverage.drivers.browsers;

import coverage.GlobalSetup;
import coverage.drivers.SELENIUM;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class ChromeBuilder implements Builder {

    private String EG_URL = System.getenv("EXECUTION_GRID_URL");

    public ChromeBuilder() {}

    public WebDriver build(boolean headless, boolean legacy, boolean localDriver) throws MalformedURLException {
        ChromeOptions options = new ChromeOptions().setHeadless(headless);
        if (GlobalSetup.CI) {
            options.addArguments("--no-sandbox", "--disable-gpu");
        }
        if (GlobalSetup.useEG && !localDriver) return new RemoteWebDriver(new URL(EG_URL), options);
        if (GlobalSetup.useDocker) return new RemoteWebDriver(new URL(SELENIUM.CHROME_LOCAL.url), options);
        return new ChromeDriver(options);
    }
}
