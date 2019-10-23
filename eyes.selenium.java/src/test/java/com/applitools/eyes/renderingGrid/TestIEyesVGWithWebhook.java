package com.applitools.eyes.renderingGrid;

import com.applitools.ICheckSettings;
import com.applitools.eyes.BatchInfo;

public class TestIEyesVGWithWebhook extends TestIEyesVG{
    protected final BatchInfo batchInfo = new BatchInfo("Top Sites - Visual Grid With Webhook");

    public TestIEyesVGWithWebhook() {
        super("visual_grid_with_webhook");
    }

    protected ICheckSettings getCheckSettings()
    {
        String jshook = "document.body.style='background-color: red'";
        return super.getCheckSettings().beforeRenderScreenshotHook(jshook);
    }
}
