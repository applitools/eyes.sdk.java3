package coverage.drivers.browsers;

import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

public interface Builder {
    WebDriver build(boolean headless, boolean legacy, boolean executionGrid) throws MalformedURLException;
}
