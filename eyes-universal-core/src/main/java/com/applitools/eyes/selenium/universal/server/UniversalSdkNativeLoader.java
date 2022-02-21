package com.applitools.eyes.selenium.universal.server;

import java.io.BufferedReader;
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

import com.applitools.utils.GeneralUtils;

/**
 * @author Kanan
 */
public class UniversalSdkNativeLoader {
  private static Process nativeProcess = null;

  public static void start() {
    try {
      startProcess();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void stop() {
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

      String pathInJar = "runtimes" + "/" + os + "/" + "native" + "/" + "eyes-universal-" + suffix;
      InputStream inputStream = getFileFromResourceAsStream(pathInJar);
      Path tempFile = Files.createTempFile("eyes-universal-", suffix);
      Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
      inputStream.close();

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

        Files.setPosixFilePermissions(Paths.get(tempFile.toString()), permissions);
      }

      nativeProcess = createProcess(tempFile.toString());
      readPortOfProcess(nativeProcess);
      //Files.deleteIfExists(tempFile);
    }

  }


  // get a input stream from the resources folder
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
      getLastLine(input);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // ENHANCE
  private static void getLastLine(BufferedReader reader) throws IOException {
    String temp;
    String lastLine = null;
    while ((temp = reader.readLine()) != null) {
      lastLine = temp;
      break;
    }
    reader.close();
  }
}
