package com.applitools.eyes.selenium.universal.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import com.applitools.eyes.EyesException;
import com.applitools.utils.GeneralUtils;

/**
 * Universal Sdk Native Loader.
 */
public class UniversalSdkNativeLoader {
  private static Process nativeProcess = null;
  private static final Integer DEFAULT_SERVER_PORT = 21077;
  private static Integer port = DEFAULT_SERVER_PORT;
  private static final String BINARY_SERVER_PATH = "APPLITOOLS_UNIVERSAL_PATH";
  private static final String TEMP_FOLDER_PATH = "java.io.tmpdir";

  public synchronized static void start() {
    try {
      startProcess();
    } catch (Exception e) {
      e.printStackTrace();
      try {
        startProcess();
      } catch (Exception e1) {
        e1.printStackTrace();
        throw new EyesException("Failed to start universal server", e1);
      }
    }
  }

  public static void stopProcess() {
    if (nativeProcess != null && nativeProcess.isAlive()) {
      nativeProcess.destroy();
    }
  }

  private static void startProcess() {
    if (nativeProcess == null || !nativeProcess.isAlive()) {
      String osVersion = GeneralUtils.getPropertyString("os.name").toLowerCase();
      String os;
      String suffix;

      if (osVersion.contains("windows")) {
        os = "win-x64";
        suffix = "win.exe";
      } else if (osVersion.contains("mac")) {
        os = "mac-x64";
        suffix = "macos";
      } else {
        os = "linux-x64";
        suffix = "linux";
      }

      String pathInJar = getBinaryPath(os, suffix);
      InputStream inputStream = getFileFromResourceAsStream(pathInJar);
      String fileName = "eyes-universal-" + suffix;

      Path directoryPath;

      // first check with user defined
      if (GeneralUtils.getEnvString(BINARY_SERVER_PATH) != null) {
        directoryPath = Paths.get(GeneralUtils.getEnvString(BINARY_SERVER_PATH));
      } else {
        directoryPath = Paths.get(GeneralUtils.getPropertyString(TEMP_FOLDER_PATH));
      }

      Path path = Paths.get(directoryPath + File.separator + fileName);

      try {
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
        setPosixPermissionsToPath(osVersion, path);
        nativeProcess = createProcess(path.toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
      readPortOfProcess(nativeProcess);
    }

  }

  // get an input stream from the resources folder
  private static InputStream getFileFromResourceAsStream(String fileName) {

    // The class loader that loaded the class
    ClassLoader classLoader = UniversalSdkNativeLoader.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(fileName);

    // the stream holding the file content
    if (inputStream == null) {
      throw new IllegalArgumentException("file not found! " + fileName);
    } else {
      return inputStream;
    }

  }

  private static Process createProcess(String executableName) throws IOException {
    ProcessBuilder builder = new ProcessBuilder(executableName);
    return builder.start();
  }

  public static void readPortOfProcess(Process process) {
    try (BufferedReader input = new BufferedReader(new
        InputStreamReader(process.getInputStream()))) {
      getFirstLineAsPort(input);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Cannot read server port: " + e.getMessage());
      throw new EyesException("Could not read server port", e);
    }
  }

  // ENHANCE
  private static void getFirstLineAsPort(BufferedReader reader) throws Exception {
    String temp;
    String lastLine;
    while ((temp = reader.readLine()) != null) {
      lastLine = temp;
      port =  Integer.parseInt(lastLine);
      break;
    }

    reader.close();
  }

  public static Integer getPort() {
    return port;
  }

  private static void assignHookToStopProcess() {
    Runtime.getRuntime().addShutdownHook(
        new Thread(UniversalSdkNativeLoader::stopProcess)
    );
  }

  private static String getBinaryPath(String os, String suffix) {
    return "runtimes" +
        "/" +
        os +
        "/" +
        "native" +
        "/" +
        "eyes-universal-" +
        suffix;
  }

  private static void setPosixPermissionsToPath(String osVersion, Path path) throws Exception {
    if (!osVersion.contains("windows")) {
      Set<PosixFilePermission> permissions = new HashSet<>();

      /* -------------------------- OWNER Permissions ----------------------- */
      permissions.add(PosixFilePermission.OWNER_READ);
      permissions.add(PosixFilePermission.OWNER_WRITE);
      permissions.add(PosixFilePermission.OWNER_EXECUTE);

      /* -------------------------- GROUP Permissions ----------------------- */
      permissions.add(PosixFilePermission.GROUP_READ);
      permissions.add(PosixFilePermission.GROUP_WRITE);
      permissions.add(PosixFilePermission.GROUP_EXECUTE);

      /* -------------------------- OTHERS Permissions ----------------------- */
      permissions.add(PosixFilePermission.OTHERS_READ);
      permissions.add(PosixFilePermission.OTHERS_WRITE);
      permissions.add(PosixFilePermission.OTHERS_EXECUTE);

      Files.setPosixFilePermissions(path, permissions);
    }
  }

}
