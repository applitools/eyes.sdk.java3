package com.applitools.eyes.visualgrid.model;

import java.util.List;

public interface RenderGridJobListener {

    void getRenderStatus(List<String> urls);

    void onEyesOpenFailed();
}
