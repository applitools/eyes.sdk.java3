package com.applitools.eyes.selenium.universal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.selenium.universal.dto.BatchDto;
import com.applitools.eyes.selenium.universal.dto.CustomPropertyDto;
import com.applitools.utils.GeneralUtils;

/**
 * batch mapper
 */
public class BatchMapper {

  public static BatchDto toBatchDto(BatchInfo batchInfo) {
    if (batchInfo == null) {
      return null;
    }

    BatchDto batchDto = new BatchDto();
    batchDto.setId(batchInfo.getId());
    batchDto.setSequenceName(batchInfo.getSequenceName());
    batchDto.setName(batchInfo.getName());
    batchDto.setStartedAt(batchInfo.getStartedAt() == null ? null : GeneralUtils.toISO8601DateTime(batchInfo.getStartedAt()));
    batchDto.setNotifyOnCompletion(batchInfo.isNotifyOnCompletion());

    List<CustomPropertyDto> customPropertyDtoList = new ArrayList<>();
     batchInfo
        .getProperties()
        .forEach(stringStringMap -> stringStringMap
            .forEach((s, s2) -> customPropertyDtoList.add(new CustomPropertyDto(s, s2))));

    batchDto.setProperties(customPropertyDtoList);
    return batchDto;
  }
}
