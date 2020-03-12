package com.applitools.eyes.utils;

public class CommUtils extends CommunicationUtils{

    public static void putTestResultJsonToSauceLabs(PassedResult passedResult, String sessionId) {
        String sauce_username = System.getenv("SAUCE_USERNAME");
        String sauce_access_key = System.getenv("SAUCE_ACCESS_KEY");
        HttpAuth creds = HttpAuth.basic(sauce_username, sauce_access_key);
        putJson("https://saucelabs.com/rest/v1/" + sauce_username + "/jobs/" + sessionId, passedResult, creds);
    }

}
