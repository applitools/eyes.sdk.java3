package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.selenium.universal.dto.ProxyDto;

/**
 * Abstract Proxy Settings Mapper
 */
public class ProxyMapper {

  public static ProxyDto toProxyDto(AbstractProxySettings abstractProxySettings) {
    if (abstractProxySettings == null) {
      return null;
    }

    ProxyDto proxyDto = new ProxyDto();
    proxyDto.setUrl(abstractProxySettings.getUri());
    proxyDto.setUsername(abstractProxySettings.getUsername());
    proxyDto.setPassword(abstractProxySettings.getPassword());
    proxyDto.setHttpOnly(abstractProxySettings.isHttpOnly());
    return proxyDto;
  }
}
