package com.applitools.eyes.images.universal.mapper;

import com.applitools.eyes.images.OcrRegion;
import com.applitools.eyes.locators.BaseOcrRegion;
import com.applitools.eyes.universal.dto.OCRExtractSettingsDto;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ImageOCRExtractSettingsDtoMapper {

    public static OCRExtractSettingsDto toOCRExtractSettingsDto(BaseOcrRegion baseOcrRegion) {
        if (!(baseOcrRegion instanceof OcrRegion)) {
            return null;
        }

        OcrRegion ocrRegion = (OcrRegion) baseOcrRegion;


        OCRExtractSettingsDto ocrExtractSettingsDto = new OCRExtractSettingsDto();
        if (ocrRegion.getRegion() != null) {
            ocrExtractSettingsDto.setRegion(RectangleRegionMapper.toRectangleRegionDto(ocrRegion.getRegion()));
        }

        ocrExtractSettingsDto.setHint(ocrRegion.getHint());
        ocrExtractSettingsDto.setMinMatch(ocrRegion.getMinMatch());
        ocrExtractSettingsDto.setLanguage(ocrRegion.getLanguage());
        return ocrExtractSettingsDto;
    }


    public static List<OCRExtractSettingsDto> toOCRExtractSettingsDtoList(List<BaseOcrRegion> baseOcrRegionList) {
        if (baseOcrRegionList == null || baseOcrRegionList.isEmpty()) {
            return null;
        }

        return baseOcrRegionList.stream().map(ImageOCRExtractSettingsDtoMapper::toOCRExtractSettingsDto).collect(Collectors.toList());
    }
}
