package com.applitools.eyes.playwright.universal.driver;

import com.applitools.eyes.universal.driver.ICookie;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.playwright.options.Cookie;

public class TCookie extends Cookie implements ICookie {

    private Double expiry;

    public TCookie(Cookie cookie) {
        this(cookie.name, cookie.value);
        setExpires(cookie.expires);
        setDomain(cookie.domain);
        setPath(cookie.path);
        setUrl(cookie.url);
        setSecure(cookie.secure);
        setHttpOnly(cookie.httpOnly);
        setSameSite(cookie.sameSite);
    }

    private TCookie(String name, String value) {
        super(name, value);
    }

    public Double getExpires() {
        return expiry;
    }

    @JsonProperty("expiry")
    @Override
    public TCookie setExpires(double expires) {
        this.expiry = expires;
        return this;
    }
}
