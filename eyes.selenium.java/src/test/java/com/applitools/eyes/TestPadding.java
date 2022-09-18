package com.applitools.eyes;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestPadding {

    @Test
    public void testDefaultPadding() {
        Padding padding = new Padding();

        Assert.assertEquals(padding.getTop(), 0);
        Assert.assertEquals(padding.getRight(), 0);
        Assert.assertEquals(padding.getBottom(), 0);
        Assert.assertEquals(padding.getLeft(), 0);
    }

    @Test
    public void testPaddingAllDirections() {
        int allDirections = 15;
        Padding padding = new Padding(allDirections);

        Assert.assertEquals(padding.getTop(), allDirections);
        Assert.assertEquals(padding.getRight(), allDirections);
        Assert.assertEquals(padding.getBottom(), allDirections);
        Assert.assertEquals(padding.getLeft(), allDirections);
    }

    @Test
    public void testPaddingWhenAssigned() {
        Padding padding = new Padding().setBottom(10).setRight(25).setTop(35).setLeft(0);

        Assert.assertEquals(padding.getTop(), 35);
        Assert.assertEquals(padding.getRight(), 25);
        Assert.assertEquals(padding.getBottom(), 10);
        Assert.assertEquals(padding.getLeft(), 0);
    }
}
