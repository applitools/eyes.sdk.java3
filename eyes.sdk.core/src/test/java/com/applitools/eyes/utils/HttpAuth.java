package com.applitools.eyes.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.nio.charset.StandardCharsets;

public class HttpAuth {
    private String username;
    private String password;
    private String mode;

    public static HttpAuth basic(String username, String password) {
        HttpAuth auth = new HttpAuth();
        auth.username = username;
        auth.password = password;
        auth.mode = "Basic";
        return auth;
    }

    public Header getHeader() {
        String token = this.username + ":" + this.password;
        token = Base64.encodeBase64String(token.getBytes(StandardCharsets.UTF_8));
        Header header = new BasicHeader("Authorization", this.mode + " " + token);
        return header;
    }
}
