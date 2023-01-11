package coverage.drivers.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.net.MalformedURLException;

public class FirefoxBuilder implements PlaywrightBuilder {
    @Override
    public Page build(Playwright playwright, boolean headless, boolean legacy, boolean executionGrid) throws MalformedURLException {
        Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(headless));
        return browser.newPage();
    }
}
