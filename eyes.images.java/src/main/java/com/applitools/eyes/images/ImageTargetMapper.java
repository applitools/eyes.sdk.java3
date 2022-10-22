package com.applitools.eyes.images;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Location;
import com.applitools.eyes.locators.VisualLocatorSettings;
import com.applitools.eyes.selenium.universal.dto.CheckSettingsDto;
import com.applitools.eyes.selenium.universal.dto.ImageTargetDto;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImageTargetMapper {

    public static ImageTargetDto toImageTargetDto(BufferedImage image, String tag) {
        if (image == null) {
            return null;
        }

        ImageTargetDto imageTargetDto = new ImageTargetDto();
        imageTargetDto.setImage(ImageUtils.base64FromImage(image));
        imageTargetDto.setName(tag);
        imageTargetDto.setSource(image.getSource().toString());
        imageTargetDto.setDom(null); //TODO
        imageTargetDto.setLocationInViewport(new Location());
        imageTargetDto.setLocationInView(new Location());

        return imageTargetDto;
    }

    public static ImageTargetDto toImageTargetDto(ICheckSettings checkSettings) {
        if (!(checkSettings instanceof ImagesCheckSettings)) {
            return null;
        }

        ImagesCheckSettings imagesCheckSettings = (ImagesCheckSettings) checkSettings;

        ImageTargetDto imageTargetDto = new ImageTargetDto();
        imageTargetDto.setImage(ImageUtils.base64FromImage(imagesCheckSettings.getImage()));
        imageTargetDto.setName(imagesCheckSettings.getName());
        imageTargetDto.setSource(imagesCheckSettings.getImage().getSource().toString());
        imageTargetDto.setDom(null); //TODO
        imageTargetDto.setLocationInViewport(new Location());
        imageTargetDto.setLocationInView(new Location());

        return imageTargetDto;
    }

    public static ImageTargetDto toImageTargetDto(VisualLocatorSettings visualLocatorSettings) {
        if (visualLocatorSettings == null)
            return null;

        ImageTargetDto imageTargetDto = new ImageTargetDto();

        imageTargetDto.setImage(ImageUtils.base64FromImage(visualLocatorSettings.getImage()));
        imageTargetDto.setName(null); //TODO
        imageTargetDto.setSource(visualLocatorSettings.getImage().getSource().toString());
        imageTargetDto.setDom(null); //TODO
        imageTargetDto.setLocationInViewport(new Location());
        imageTargetDto.setLocationInView(new Location());

        return imageTargetDto;
    }
}
