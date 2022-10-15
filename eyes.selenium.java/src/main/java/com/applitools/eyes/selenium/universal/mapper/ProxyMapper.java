package com.applitools.eyes.selenium.universal.mapper;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.AutProxySettings;
import com.applitools.eyes.selenium.universal.dto.AutProxyDto;
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
    return proxyDto;
  }

  public static AutProxyDto toAutProxyDto(AutProxySettings autProxySettings) {
    if (autProxySettings == null) {
      return null;
    }

    AutProxyDto autProxyDto = new AutProxyDto();
    autProxyDto.setUrl(autProxySettings.getUri());
    autProxyDto.setUsername(autProxySettings.getUsername());
    autProxyDto.setPassword(autProxySettings.getPassword());
    autProxyDto.setDomains(autProxySettings.getDomains());
    autProxyDto.setAutProxyMode(autProxySettings.getAutProxyMode() == null ?
            null : autProxySettings.getAutProxyMode().getName());
    return autProxyDto;
  }
}
