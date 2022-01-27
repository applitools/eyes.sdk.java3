package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.locators.BaseOcrRegion;
import com.applitools.eyes.locators.OcrRegion;
import com.applitools.eyes.selenium.universal.dto.OCRExtractSettingsDto;

/**
 * OCRExtractSettingsDtoMapper
 */
public class OCRExtractSettingsDtoMapper {

  public static OCRExtractSettingsDto toOCRExtractSettingsDto(BaseOcrRegion baseOcrRegion) {
    if (!(baseOcrRegion instanceof OcrRegion)) {
      return null;
    }

    OcrRegion ocrRegion = (OcrRegion) baseOcrRegion;


    OCRExtractSettingsDto ocrExtractSettingsDto = new OCRExtractSettingsDto();
    if (ocrRegion.getSelector() != null) {
      ocrExtractSettingsDto.setTarget(SelectorRegionMapper.toSelectorRegionDto(ocrRegion.getSelector()));
    } else if (ocrRegion.getElement() != null) {
      ocrExtractSettingsDto.setTarget(ElementRegionMapper.toElementRegionDto(ocrRegion.getElement()));
    } else if (ocrRegion.getRegion() != null) {
      ocrExtractSettingsDto.setTarget(RectangleRegionMapper.toRectangleRegionDto(ocrRegion.getRegion()));
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

    return baseOcrRegionList.stream().map(OCRExtractSettingsDtoMapper::toOCRExtractSettingsDto).collect(Collectors.toList());
  }
}
