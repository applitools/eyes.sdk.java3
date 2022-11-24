package com.applitools.eyes.images;

import com.applitools.ICheckSettings;

import java.awt.image.BufferedImage;

public interface IImagesCheckTarget extends ICheckSettings {
    BufferedImage getImage();
}