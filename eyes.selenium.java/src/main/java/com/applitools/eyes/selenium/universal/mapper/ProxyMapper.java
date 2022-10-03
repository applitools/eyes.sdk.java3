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
    proxyDto.setIsHttpOnly(true);
    return proxyDto;
  }

  public static AutProxyDto toAutProxyDto(AutProxySettings abstractAutProxySettings) {
    if (abstractAutProxySettings == null) {
      return null;
    }

    AutProxyDto autProxyDto = new AutProxyDto();
    autProxyDto.setProxy(toProxyDto(abstractAutProxySettings.getProxy()));
    autProxyDto.setDomains(abstractAutProxySettings.getDomains());
    autProxyDto.setAutProxyMode(abstractAutProxySettings.getAutProxyMode() == null ?
            null : abstractAutProxySettings.getAutProxyMode().getName());
    return autProxyDto;
  }
}
