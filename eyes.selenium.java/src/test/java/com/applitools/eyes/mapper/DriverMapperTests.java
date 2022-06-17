package com.applitools.eyes.mapper;

import com.applitools.eyes.selenium.universal.dto.DriverDto;
import com.applitools.eyes.selenium.universal.mapper.DriverMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.HashMap;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class DriverMapperTests {

    @Test
    public void should_ReturnDto_WhenCalledWithRemoteWebDriver() throws Exception {
        // given
        String url = "https://applitools:zBo67o7BsoKhdkf8Va4u@hub-cloud.browserstack.com/wd/hub";
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "chrome");
        HashMap<String, Object> browserstackOptions = new HashMap<>();
        browserstackOptions.put("os", "Windows");
        browserstackOptions.put("osVersion", "10");
        browserstackOptions.put("buildName", "BStack-[Java] Selenium 4 Sample Test");
        browserstackOptions.put("sessionName", "Selenium 4 test");
        browserstackOptions.put("seleniumVersion", "4.0.0");
        capabilities.setCapability("bstack:options", browserstackOptions);

        WebDriver driver = new RemoteWebDriver(new URL(url), capabilities);

        // when
        DriverDto driverDto = DriverMapper.toDriverDto(driver, null);

        // then
        assertNotNull(driverDto);
        assertEquals("chrome", driverDto.getCapabilities().get("browserName"));
    }

}
