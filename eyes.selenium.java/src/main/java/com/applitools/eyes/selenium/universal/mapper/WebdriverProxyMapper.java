package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.WebdriverProxy;
import com.applitools.eyes.selenium.universal.dto.WebdriverProxyDto;

/**
 * web driver proxy mapper
 */
public class WebdriverProxyMapper {

    public static WebdriverProxyDto toWebdriverProxyDto(WebdriverProxy webdriverProxy) {
        if (webdriverProxy == null) {
            return null;
        }

        return new WebdriverProxyDto(webdriverProxy.getUrl(), webdriverProxy.getTunnel());
    }
}
