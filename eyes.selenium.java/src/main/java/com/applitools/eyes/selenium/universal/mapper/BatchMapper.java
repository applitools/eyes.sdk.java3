package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.selenium.universal.dto.BatchDto;
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
    batchDto.setBatchSequenceName(batchInfo.getSequenceName());
    batchDto.setName(batchInfo.getName());
    batchDto.setStartedAt(batchInfo.getStartedAt() == null ? null : GeneralUtils.toISO8601DateTime(batchInfo.getStartedAt()));
    batchDto.setNotifyOnCompletion(batchInfo.isNotifyOnCompletion());
    batchDto.setCompleted(batchInfo.isCompleted());
    batchDto.setProperties(batchInfo.getProperties());
    return batchDto;

  }
}
