package com.applitools.eyes.selenium.fluent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface IGetSeleniumRegion {
    @JsonIgnore
    List<WebElement> getElements();
}
