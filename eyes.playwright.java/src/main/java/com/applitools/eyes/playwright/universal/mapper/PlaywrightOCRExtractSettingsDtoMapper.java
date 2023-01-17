package com.applitools.eyes.playwright.universal.mapper;

import com.applitools.eyes.locators.BaseOcrRegion;
import com.applitools.eyes.playwright.locators.OcrRegion;
import com.applitools.eyes.playwright.universal.Refer;
import com.applitools.eyes.playwright.universal.driver.Element;
import com.applitools.eyes.playwright.universal.driver.Selector;
import com.applitools.eyes.universal.dto.OCRExtractSettingsDto;
import com.applitools.eyes.universal.mapper.RectangleRegionMapper;

import java.util.List;
import java.util.stream.Collectors;

public class PlaywrightOCRExtractSettingsDtoMapper {

    public static OCRExtractSettingsDto toOCRExtractSettingsDto(BaseOcrRegion baseOcrRegion, Refer refer) {
        if (!(baseOcrRegion instanceof OcrRegion)) {
            return null;
        }

        OcrRegion ocrRegion = (OcrRegion) baseOcrRegion;


        OCRExtractSettingsDto ocrExtractSettingsDto = new OCRExtractSettingsDto();
        if (ocrRegion.getSelector() != null) {
            Selector selector = new Selector(ocrRegion.getSelector());
            selector.setApplitoolsRefId(refer.ref(selector));
            ocrExtractSettingsDto.setRegion(selector);
        } else if (ocrRegion.getElement() != null) {
            Element element = new Element(ocrRegion.getElement());
            element.setApplitoolsRefId(refer.ref(element));
            ocrExtractSettingsDto.setRegion(element);
        } else if (ocrRegion.getRegion() != null) {
            ocrExtractSettingsDto.setRegion(RectangleRegionMapper.toRectangleRegionDto(ocrRegion.getRegion()));
        }

        ocrExtractSettingsDto.setHint(ocrRegion.getHint());
        ocrExtractSettingsDto.setMinMatch(ocrRegion.getMinMatch());
        ocrExtractSettingsDto.setLanguage(ocrRegion.getLanguage());
        return ocrExtractSettingsDto;
    }


    public static List<OCRExtractSettingsDto> toOCRExtractSettingsDtoList(List<BaseOcrRegion> baseOcrRegionList, Refer refer) {
        if (baseOcrRegionList == null || baseOcrRegionList.isEmpty()) {
            return null;
        }

        return baseOcrRegionList.stream()
                .map(baseOcrRegion -> toOCRExtractSettingsDto(baseOcrRegion, refer))
                .collect(Collectors.toList());
    }
}