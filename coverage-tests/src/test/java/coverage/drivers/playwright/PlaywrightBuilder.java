package coverage.drivers.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.net.MalformedURLException;

public interface PlaywrightBuilder {
    Page build(Playwright playwright, boolean headless, boolean legacy, boolean executionGrid) throws MalformedURLException;
}
