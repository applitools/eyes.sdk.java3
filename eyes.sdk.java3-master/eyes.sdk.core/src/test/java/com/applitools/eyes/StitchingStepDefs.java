package com.applitools.eyes;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

import java.util.List;

/**
 * Step definitions for the Stitching feature.
 */
public class StitchingStepDefs {

    private Region regionToDivide = null;
    private Iterable<Region> subRegions = null;
    private boolean illegalArgumentExceptionThrown = false;

    @Given("^I have a region with left (\\d+) and top (\\d+) and width (\\d+) and height (\\d+)$")
    public void I_have_a_region_with_width_and_height(int left, int top,
                                                      int width, int height)
            throws Throwable {
        regionToDivide = new Region(left, top, width, height);
    }

    private void divideIntoSubRegions(int width, int height, boolean isFixedSize) throws Throwable {
        try {
            subRegions = regionToDivide.getSubRegions(
                    new RectangleSize(width, height), isFixedSize);
        } catch (IllegalArgumentException e) {
            illegalArgumentExceptionThrown = true;
        }
    }

    @When("^I divide the region into fixed-size sub regions with width (\\d+) and height (\\d+)$")
    public void I_divide_the_region_into_fixed_size_sub_regions_with_width_and_height(int width, int height) throws Throwable {
        divideIntoSubRegions(width, height, true);
    }

    @When("^I divide the region into varying-size sub regions with width (\\d+) and height (\\d+)$")
    public void I_divide_the_region_into_varying_size_sub_regions_with_width_and_height(int width, int height) throws Throwable {
        divideIntoSubRegions(width, height, false);
    }

    @When("^I divide the region into sub regions with width (\\d+) and height (\\d+) without specifying sub-region type$")
    public void I_divide_the_region_into_sub_regions_with_width_and_height_without_specifying_sub_region_type(int width, int height) throws Throwable {
        subRegions = regionToDivide.getSubRegions(new RectangleSize(width, height));
    }

    @Then("^I get the following sub-regions:$")
    public void I_get_the_following_sub_regions(DataTable validSubRegionsDT)
            throws Throwable {
        List<Region> validSubRegions = validSubRegionsDT.asList(Region.class);

        int subRegionsSize = 0;
        for (Region currentSubRegion : subRegions) {
            Assert.assertTrue("Invalid Sub region: " + currentSubRegion,
                    validSubRegions.contains(currentSubRegion));
            ++subRegionsSize;
        }

        Assert.assertEquals("Number of sub-regions",
                subRegionsSize, validSubRegions.size());
    }

    @Then("^An exception should be thrown.$")
    public void An_exception_should_be_thrown() throws Throwable {
        Assert.assertTrue(illegalArgumentExceptionThrown);
    }
}
