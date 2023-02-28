package com.applitools.eyes.integration;

import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.dto.Driver;
import com.applitools.eyes.playwright.universal.dto.Element;
import com.applitools.eyes.utils.PlaywrightTestSetup;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

public class TestRefer extends PlaywrightTestSetup {

    @BeforeClass
    public void setup() {
        page = initDriver(true);
    }

    @Test
    public void testRefer() {

        Refer refer = new Refer();
        Driver driver = new Driver(getPage());

        driver.setApplitoolsRefId(refer.ref(getPage(), driver.getRoot()));

        Element element = new Element();
        element.setApplitoolsRefId(refer.ref(element, driver.getRoot()));

        Assert.assertEquals(refer.deref(driver), page);
        Assert.assertEquals(refer.deref(element), element);

        HashMap relations;
        HashMap references;
        try {
            Field relationsField = com.applitools.eyes.universal.Refer.class.getDeclaredField("relations");
            relationsField.setAccessible(true);
            relations = (HashMap) relationsField.get(refer);

            Field referencesField = com.applitools.eyes.universal.Refer.class.getDeclaredField("references");
            referencesField.setAccessible(true);
            references = (HashMap) referencesField.get(refer);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Assert.assertNotNull(relations.get(driver.getRoot().getApplitoolsRefId()));

        String newObject = "new Object";
        refer.ref(newObject, element);

        Assert.assertNotNull(relations.get(element.getApplitoolsRefId()));

        refer.destroy(driver.getRoot());

        Assert.assertTrue(relations.isEmpty());
        Assert.assertTrue(references.isEmpty());
    }
}
