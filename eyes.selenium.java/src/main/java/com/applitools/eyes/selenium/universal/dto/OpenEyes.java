package com.applitools.eyes.selenium.universal.dto;


import com.applitools.eyes.selenium.Reference;

/**
 * create an eyes object
 */
public class OpenEyes {

  /**
   * reference received from "Core.makeManager" command
   */
  private Reference manager;

  /**
   * reference to the driver that will be used by the server in order to perform automation
   */
  private DriverDto driver;

  /**
   * configuration object that will be associated with a new eyes object, it could be overridden later
   */
  private ConfigurationDto config;

  public OpenEyes(Reference manager, DriverDto driver, ConfigurationDto config) {
    this.manager = manager;
    this.driver = driver;
    this.config = config;
  }

  public Reference getManager() {
    return manager;
  }

  public void setManager(Reference manager) {
    this.manager = manager;
  }

  public DriverDto getDriver() {
    return driver;
  }

  public void setDriver(DriverDto driver) {
    this.driver = driver;
  }

  public ConfigurationDto getConfig() {
    return config;
  }

  public void setConfig(ConfigurationDto config) {
    this.config = config;
  }

  @Override
  public String toString() {
    return "OpenEyes{" +
        "manager=" + manager +
        ", driver=" + driver +
        ", config=" + config +
        '}';
  }
}
