package com.applitools.eyes.selenium.universal.mapper;

import java.util.List;
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

  public static List<VisualGridOptionDto> toVisualGridOptionDtoList(List<VisualGridOption> visualGridOptions) {
    return visualGridOptions.stream().map(VisualGridOptionMapper::toVisualGridOptionDto).collect(Collectors.toList());
  }
}
