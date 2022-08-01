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
  private static Integer port;
  private static final String BINARY_SERVER_PATH = "APPLITOOLS_UNIVERSAL_PATH";
  private static final String TEMP_FOLDER_PATH = "java.io.tmpdir";

  public synchronized static void start() {
    try {
      startProcess();
    } catch (Exception e) {
      try {
        startProcess();
      } catch (Exception e1) {
        System.err.println("Could not launch server, ERROR: " + e.getMessage());
        throw new EyesException("Failed to launch universal server", e1);
      }
    }
  }

  public static void stopProcess() {
    if (nativeProcess != null && nativeProcess.isAlive()) {
      nativeProcess.destroy();
    }
  }

  private static void startProcess() throws Exception {
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

      Path directoryPath;

      // first check with user defined
      if (GeneralUtils.getEnvString(BINARY_SERVER_PATH) != null) {
        directoryPath = Paths.get(GeneralUtils.getEnvString(BINARY_SERVER_PATH));
      } else {
        directoryPath = Paths.get(GeneralUtils.getPropertyString(TEMP_FOLDER_PATH));
      }

      String fileName = "eyes-universal-" + suffix;
      Path path = Paths.get(directoryPath + File.separator + fileName);


      String pathInJar = getBinaryPath(os, suffix);
      InputStream inputStream = getFileFromResourceAsStream(pathInJar);

      try {
        copyBinaryFileToLocalPath(inputStream, path);
      } catch (Exception e) {
        // probably file in specified path is running, continue to start process
      }

      inputStream.close();
      setPosixPermissionsToPath(osVersion, path);
      nativeProcess = createProcess(path.toString());
      readPortOfProcess(nativeProcess);
    }

  }

  // get an input stream from the resources folder
  private static InputStream getFileFromResourceAsStream(String fileName) throws Exception {

    // The class loader that loaded the class
    ClassLoader classLoader = UniversalSdkNativeLoader.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(fileName);

    // the stream holding the file content
    if (inputStream == null) {
      System.err.println("Could not find binary file inside jar");
      throw new Exception("binary file not found! " + fileName);
    } else {
      return inputStream;
    }

  }

  private static Process createProcess(String executableName)  throws Exception {
    try {
      ProcessBuilder builder = new ProcessBuilder(executableName, "--fork");
      return builder.start();
    } catch (IOException e) {
      System.err.println("Could not start process, ERROR: " + e.getMessage());
      throw new Exception("Could not start process", e);
    }

  }

  public static void readPortOfProcess(Process process) throws Exception {
    try (BufferedReader input = new BufferedReader(new
        InputStreamReader(process.getInputStream()))) {
      getFirstLineAsPort(input);
    } catch (Exception e) {
      System.err.println("Could not read server port, ERROR: " + e.getMessage());
      throw new Exception("Could not read server port", e);
    }
  }

  // ENHANCE
  private static void getFirstLineAsPort(BufferedReader reader)  throws Exception {
    String temp;
    String lastLine;
    try {
      while ((temp = reader.readLine()) != null) {
        lastLine = temp;
        port = Integer.parseInt(lastLine);
        break;
      }
      reader.close();
    } catch (Exception e) {
      System.err.println("Could not read first line as port, ERROR: " + e.getMessage());
      throw new Exception("Could not read first line as port", e);
    }

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

    try {
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
    } catch (IOException e) {
      System.err.println("Could not set posix file permissions, ERROR: " + e.getMessage());
      throw new Exception("Could not set posix file permissions", e);
    }

  }

  private static void copyBinaryFileToLocalPath(InputStream inputStream, Path path) throws Exception {
    System.err.println("Could not copy binary file to local file, WARN: ");
    throw new IOException("Could not copy binary file to local file");

//    try {
//      Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
//    } catch (IOException e) {
//      System.err.println("Could not copy binary file to local file, WARN: " + e.getMessage());
//      throw new IOException("Could not copy binary file to local file", e);
//    }

  }

}
