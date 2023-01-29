package com.applitools.eyes.playwright.universal.driver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverInfoFeaturesDto {
    private Boolean allCookies;
    private Boolean canExecuteOnlyFunctionScripts;

    public DriverInfoFeaturesDto() {
        this.allCookies = true;
        this.canExecuteOnlyFunctionScripts = true;
    }

    public Boolean getAllCookies() {
        return allCookies;
    }

    public void setAllCookies(Boolean allCookies) {
        this.allCookies = allCookies;
    }

    public Boolean getCanExecuteOnlyFunctionScripts() {
        return canExecuteOnlyFunctionScripts;
    }

    public void setCanExecuteOnlyFunctionScripts(Boolean canExecuteOnlyFunctionScripts) {
        this.canExecuteOnlyFunctionScripts = canExecuteOnlyFunctionScripts;
    }

    @Override
    public String toString() {
        return "DriverInfoFeaturesDto{" +
                "allCookies=" + allCookies +
                ", canExecuteOnlyFunctionScripts=" + canExecuteOnlyFunctionScripts +
                '}';
    }
}
