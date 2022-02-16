package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.applitools.eyes.selenium.universal.dto.VisualGridOptionDto;
import com.applitools.eyes.visualgrid.model.VisualGridOption;

/**
 * VisualGridOptionMapper
 */
public class VisualGridOptionMapper {

  public static VisualGridOptionDto toVisualGridOptionDto(VisualGridOption visualGridOption) {
    if (visualGridOption == null) {
      return null;
    }

    VisualGridOptionDto visualGridOptionDto = new VisualGridOptionDto();
    visualGridOptionDto.setKey(visualGridOption.getKey());
    visualGridOptionDto.setValue(visualGridOption.getValue());

    return visualGridOptionDto;

  }

  public static Map<String, Object> toVisualGridOptionDtoList(List<VisualGridOption> visualGridOptions) {
    if (visualGridOptions == null || visualGridOptions.isEmpty()) {
      return null;
    }

    return visualGridOptions.stream().collect(Collectors.toMap(VisualGridOption::getKey, VisualGridOption::getValue));
  }

}
