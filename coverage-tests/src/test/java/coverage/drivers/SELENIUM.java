package coverage.drivers;

public enum SELENIUM {
    SAUCE("https://" + System.getenv("SAUCE_USERNAME") +":" + System.getenv("SAUCE_ACCESS_KEY") +
            "@ondemand.saucelabs.com:443/wd/hub"),
    CHROME_LOCAL("http://localhost:4444/wd/hub"),
    FIREFOX_LOCAL("http://localhost:4445/wd/hub");

    public final String url;

    SELENIUM(String url) {
       this.url = url;
    }
}
