package com.applitools.universal.mapper;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.universal.dto.AbstractProxySettingsDto;

/**
 * Abstract Proxy Settings Mapper
 */
public class AbstractProxySettingsMapper {

  public static AbstractProxySettingsDto toAbstractProxySettingsDto(AbstractProxySettings abstractProxySettings) {
    if (abstractProxySettings == null) {
      return null;
    }

    AbstractProxySettingsDto abstractProxySettingsDto = new AbstractProxySettingsDto();
    abstractProxySettingsDto.setUri(abstractProxySettings.getUri());
    abstractProxySettingsDto.setUsername(abstractProxySettings.getUsername());
    abstractProxySettingsDto.setPassword(abstractProxySettings.getPassword());
    abstractProxySettingsDto.setPort(abstractProxySettings.getPort());
    return abstractProxySettingsDto;
  }
}
