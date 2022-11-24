package com.applitools.eyes.images.universal.mapper;

import com.applitools.eyes.fluent.FloatingRegionByRectangle;
import com.applitools.eyes.fluent.GetFloatingRegion;
import com.applitools.eyes.universal.dto.*;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ImageTFloatingRegionMapper {
    public static TFloatingRegion toTFloatingRegionDto(GetFloatingRegion getFloatingRegion) {
        if (getFloatingRegion == null) {
            return null;
        }

        if (getFloatingRegion instanceof FloatingRegionByRectangle) {
            FloatingRegionByRectangle floatingRegionByRectangle = (FloatingRegionByRectangle) getFloatingRegion;

            RectangleRegionDto rectangleRegionDto = RectangleRegionMapper
                    .toRectangleRegionDto(((FloatingRegionByRectangle) getFloatingRegion).getRegion());

            RectangleFloatingRegionDto response = new RectangleFloatingRegionDto();
            response.setRegion(rectangleRegionDto);
            response.setMaxUpOffset(floatingRegionByRectangle.getMaxUpOffset());
            response.setMaxDownOffset(floatingRegionByRectangle.getMaxDownOffset());
            response.setMaxLeftOffset(floatingRegionByRectangle.getMaxLeftOffset());
            response.setMaxRightOffset(floatingRegionByRectangle.getMaxRightOffset());
            response.setRegionId(floatingRegionByRectangle.getRegion().getRegionId());

            return response;
        }

        return null;

    }

    public static List<TFloatingRegion> toTFloatingRegionDtoList(List<GetFloatingRegion> getFloatingRegionList) {
        if (getFloatingRegionList == null || getFloatingRegionList.isEmpty()) {
            return null;
        }

        return getFloatingRegionList.stream().map(ImageTFloatingRegionMapper::toTFloatingRegionDto).collect(Collectors.toList());
    }
}
