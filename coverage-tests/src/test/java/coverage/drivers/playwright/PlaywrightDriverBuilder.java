package coverage.drivers.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;


import java.net.MalformedURLException;
import java.util.HashMap;

public class PlaywrightDriverBuilder {

    protected boolean headless = true;
    protected boolean legacy = false;
    protected boolean executionGrid = false;
    protected String browser = "chrome";
    protected Playwright playwright;

    static HashMap<String, PlaywrightBuilder> playwrightBuilders = new HashMap<String, PlaywrightBuilder>() {{
        put("chrome", new ChromeBuilder());
        put("firefox", new FirefoxBuilder());
    }};

    public PlaywrightDriverBuilder headless(boolean headless) {
        this.headless = headless;
        return this;
    }

    public PlaywrightDriverBuilder browser(String browser) {
        this.browser = browser;
        return this;
    }

    public PlaywrightDriverBuilder legacy(boolean legacy) {
        this.legacy = legacy;
        return this;
    }

    public PlaywrightDriverBuilder executionGrid(boolean executionGrid) {
        this.executionGrid = executionGrid;
        return this;
    }

    public Page build() throws MalformedURLException {
        playwright = Playwright.create();
        PlaywrightBuilder builder = playwrightBuilders.get(browser);
        return builder.build(playwright, headless, legacy, executionGrid);
    }

    public void quit() {
        if (playwright != null) {
            playwright.close();
        }
    }
}
