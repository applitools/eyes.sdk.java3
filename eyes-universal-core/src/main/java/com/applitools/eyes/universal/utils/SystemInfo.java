package com.applitools.eyes.universal.utils;

import com.applitools.eyes.EyesException;
import com.applitools.utils.GeneralUtils;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SystemInfo {

    private String os;
    private String osVersion;
    private String suffix;
    private String architecture;

    private static final String ALPINE_PATH = "/etc/alpine-release";

    private SystemInfo(String os, String osVersion, String suffix, String osArchitecture) {
        this.os = os;
        this.osVersion = osVersion;
        this.suffix = suffix;
        this.architecture = osArchitecture;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public static SystemInfo getSystemInfo() {
        String osVersion = GeneralUtils.getPropertyString("os.name").toLowerCase();
        String osArchitecture = GeneralUtils.getPropertyString("os.arch").toLowerCase();

        String os;
        String suffix;

        if (osVersion.contains("windows")) {
            os = "win-x64";
            suffix = "win.exe";
        } else if (osVersion.contains("mac")) {
            os = "mac-x64";
            suffix = "macos";
        } else if (osVersion.contains("linux")) {
            os = "linux-x64";
            suffix = "linux";
            if (Files.exists(Paths.get(ALPINE_PATH))) {
                suffix = "alpine";
            } else if (osArchitecture.contains("arm64") || osArchitecture.contains("aarch64")) {
                suffix = "linux-arm64";
            }
        } else {
            throw new EyesException(String.format("Operating system is not supported. Version: %s, Architecture: %s",
                    osVersion,
                    osArchitecture));
        }

        return new SystemInfo(os, osVersion, suffix, osArchitecture);
    }
}
