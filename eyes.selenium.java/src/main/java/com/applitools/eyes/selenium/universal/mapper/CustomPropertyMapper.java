package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.applitools.eyes.PropertyData;
import com.applitools.eyes.selenium.universal.dto.CustomPropertyDto;

/**
 * @author Kanan
 */
public class CustomPropertyMapper {

  public static CustomPropertyDto toCustomPropertyDto(PropertyData propertyData) {
    if (propertyData == null) {
      return null;
    }

    CustomPropertyDto dto = new CustomPropertyDto();
    dto.setName(propertyData.getName());
    dto.setValue(propertyData.getValue());

    return dto;
  }

  public static List<CustomPropertyDto> toCustomPropertyDtoList(List<PropertyData> propertyDataList) {
    if (propertyDataList == null || propertyDataList.isEmpty()) {
      return null;
    }

    return propertyDataList.stream().map(CustomPropertyMapper::toCustomPropertyDto).collect(Collectors.toList());

  }
}
