package com.applitools.eyes.playwright.universal.driver;

import com.applitools.eyes.universal.driver.ICookie;
import com.microsoft.playwright.options.Cookie;

public class TCookie extends Cookie implements ICookie {

    private Double expiry;

    public TCookie(Cookie cookie) {
        this(cookie.name, cookie.value);
        this.expiry = cookie.expires;
        this.expires = null;
    }

    public TCookie(String name, String value) {
        super(name, value);
    }

    public Double getExpires() {
        return expires;
    }

    public void setExpires(Double expires) {
        this.expires = expires;
    }
}
