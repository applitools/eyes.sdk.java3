package com.applitools.eyes.appium.unit;

import com.applitools.eyes.config.ContentInset;
import com.applitools.eyes.utils.ReportingTestSuite;
import org.junit.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

public class TestContentInset extends ReportingTestSuite {

    @BeforeClass
    public void setup() {
        super.setGroupName("appium");
    }

    @Test
    public void defualtInitialize() {
        ContentInset contentInset = new ContentInset();

        Assert.assertEquals(0, contentInset.getTop());
        Assert.assertEquals(0, contentInset.getBottom());
        Assert.assertEquals(0, contentInset.getLeft());
        Assert.assertEquals(0, contentInset.getRight());
    }

    @Test
    public void shouldReturnAssigned() {
        ContentInset contentInset = new ContentInset();
        contentInset.setTop(10);
        contentInset.setBottom(20);
        contentInset.setLeft(30);
        contentInset.setRight(40);

        Assert.assertEquals(10, contentInset.getTop());
        Assert.assertEquals(20, contentInset.getBottom());
        Assert.assertEquals(30, contentInset.getLeft());
        Assert.assertEquals(40, contentInset.getRight());
    }
}
