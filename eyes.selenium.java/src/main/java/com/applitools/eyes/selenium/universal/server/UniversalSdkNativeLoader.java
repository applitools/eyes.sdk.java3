package com.applitools.eyes.selenium.universal.server;


import com.applitools.utils.GeneralUtils;

public class UniversalSdkNativeLoader {

  public static void start() {

  }

  public static void stop() {

  }

  private static void startProcess() {
    String osVersion = GeneralUtils.getPropertyString("os.name").toLowerCase();
    String os;
    String suffix;

    if (osVersion.contains("windows")) {
      os = "win-x64";
      suffix = "win.exe";
    } else if (osVersion.contains("macos")) {
      os = "mac-x64";
      suffix = "macos";
    } else {
      os = "linux-x64";
      suffix = "linux";
    }

  }

}
