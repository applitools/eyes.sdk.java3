package com.applitools.eyes.unit;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSpecDriverExecuteScriptHandler {

    @Test
    public void testHandlerTypeMatcher() {

        final List<String> samples = new ArrayList<String>() {{
            add("ThisIsJustAText");
            add("jshandle@node");
            add("object");
            add("array(4)");
            add("array(15)");
        }};

        String type = "";
        Pattern pattern = Pattern.compile("(?:.+@)?(\\w*)(?:\\(\\d+\\))?", Pattern.CASE_INSENSITIVE);
        for(String sample : samples) {
            Matcher matcher = pattern.matcher(sample);
            if (matcher.find()) {
                type = matcher.group(0);
            }
            Assert.assertEquals(type, sample);
        }

        String jsonObject = "{\"someKey\": \"some_value\", \"other\": \"array(4)\"}";
        Matcher matcher = pattern.matcher(jsonObject);
        if (matcher.find())
            type = matcher.group(0);
        Assert.assertEquals(type, "");

    }
}
