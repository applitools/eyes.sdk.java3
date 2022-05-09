package coverage.drivers;

public enum SELENIUM {
    SAUCE(" https://ondemand.us-west-1.saucelabs.com/wd/hub"),
    CHROME_LOCAL("http://localhost:4444/wd/hub"),
    FIREFOX_LOCAL("http://localhost:4445/wd/hub");

    public final String url;

    SELENIUM(String url) {
       this.url = url;
    }
}
