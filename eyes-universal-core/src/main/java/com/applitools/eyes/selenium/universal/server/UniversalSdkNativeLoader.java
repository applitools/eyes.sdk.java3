package com.applitools.eyes.selenium.universal.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.applitools.eyes.EyesException;
import com.applitools.eyes.EyesRunnable;
import com.applitools.utils.GeneralUtils;

/**
 * Universal Sdk Native Loader.
 */
public class UniversalSdkNativeLoader {
  private static Process nativeProcess = null;
  private static Integer port;
  private static final String BINARY_SERVER_PATH = "APPLITOOLS_UNIVERSAL_PATH";
  private static final String TEMP_FOLDER_PATH = "java.io.tmpdir";
  private static final long MAX_ACTION_WAIT_SECONDS = 10;
  private static final long SLEEP_BETWEEN_ACTION_CHECK_MS = 3000;

  public synchronized static void start() {
    try {
      copyAndStartUniversalCore();
    } catch (Exception e) {
      String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e, "Could not launch server!");
      System.out.println(errorMessage);
      throw new EyesException(errorMessage, e);
    }
  }

//  public static void stopProcess() {
//    if (nativeProcess != null && nativeProcess.isAlive()) {
//      nativeProcess.destroy();
//    }
//  }

  private static void copyAndStartUniversalCore() throws Exception {
    if (nativeProcess == null) {

      // Get the OS we're running on.
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

      System.out.println("Identified OS: " + os);

      // Set the target path for the server
      String userSetPath = GeneralUtils.getEnvString(BINARY_SERVER_PATH); // might not exist
      Path directoryPath =
          userSetPath == null ? Paths.get(GeneralUtils.getPropertyString(TEMP_FOLDER_PATH)) : Paths.get(userSetPath);

      String serverFilename = "eyes-universal-" + suffix;

      Path serverTargetPath;
      // Read the server bytes from SDK resources, and write it to the target path
      String pathInJar = getBinaryPath(os, suffix);
      try (InputStream serverBinaryAsStream = getFileFromResourceAsStream(pathInJar)) {
        serverTargetPath = copyBinaryFileToLocalPath(serverBinaryAsStream, directoryPath, serverFilename);
      }

      // Set the permissions on the binary
      setAndVerifyPosixPermissionsForUniversalCore(osVersion, serverTargetPath);

      createProcessAndReadPort(serverTargetPath.toString());
    }
  }

  private static void createProcessAndReadPort(String executablePath) {

    GeneralUtils.tryRunTaskWithRetry(new EyesRunnable() {
      @Override
      public void run() {
        try {
          nativeProcess = new ProcessBuilder(executablePath, "--port 0" ,"--no-singleton","--shutdown-mode", "stdin").start();
        } catch (Exception e) {
          String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e, "Failed to start universal core!");
          System.out.println(errorMessage);
          throw new EyesException(errorMessage, e);
        }
      }

      @Override
      public String getName() {
        return "Start universal core";
      }
    }, MAX_ACTION_WAIT_SECONDS, SLEEP_BETWEEN_ACTION_CHECK_MS, "Timed out trying to start universal core!");


    GeneralUtils.tryRunTaskWithRetry(new EyesRunnable() {
      @Override
      public void run() throws EyesException {
        String inputLineFromServer="";

        try (InputStream childOutputStream = nativeProcess.getInputStream()) {

          BufferedReader reader = new BufferedReader(new InputStreamReader(childOutputStream));
          inputLineFromServer = reader.readLine();
          port = Integer.parseInt(inputLineFromServer);
        } catch (IOException e) {
          String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e,
                  "Failed to get core's input stream!");
          System.out.println(errorMessage);
          throw new EyesException(errorMessage, e);
        } catch (NumberFormatException nfe) {
          String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(nfe,
                  "Got a non-integer as port! Text: '" + inputLineFromServer + "'");
          System.out.println(errorMessage);
          throw new EyesException(errorMessage, nfe);
        }
      }

      @Override
      public String getName() {
        return "Read universal core port";
      }
    }, MAX_ACTION_WAIT_SECONDS, SLEEP_BETWEEN_ACTION_CHECK_MS, "Timed out trying to read port from core!");
  }

  // get an input stream from the resources folder
  private static InputStream getFileFromResourceAsStream(String fileName) throws Exception {

    // The class loader that loaded the class
    ClassLoader classLoader = UniversalSdkNativeLoader.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(fileName);

    // the stream holding the file content
    if (inputStream == null) {
      String errorMessage = "Could not find the universal core inside the SDK jar! Filename searched: " + fileName;
      throw new EyesException(errorMessage);
    }

    return inputStream;
  }



  public static Integer getPort() {
    return port;
  }

//  private static void assignHookToStopProcess() {
//    Runtime.getRuntime().addShutdownHook(
//        new Thread(UniversalSdkNativeLoader::stopProcess)
//    );
//  }

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

  private static void setAndVerifyPosixPermissionsForUniversalCore(String osVersion, Path path) throws Exception {

    // We don't set POSIX on Windows
    if (osVersion.contains("windows")) {
      return;
    }

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

    GeneralUtils.tryRunTaskWithRetry(new EyesRunnable() {
      @Override
      public void run() throws EyesException {
        try {
          Files.setPosixFilePermissions(path, permissions);
          System.out.println("'Set core permissions' ended. Now verifying if the permissions are correct...");
        } catch (Exception e) {
          String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e,
                  "Could not set permissions on the universal core file!");
          System.out.println(errorMessage);
          throw new EyesException(errorMessage, e);
        }
      }

      @Override
      public String getName() {
        return "Set core permissions";
      }
    }, MAX_ACTION_WAIT_SECONDS, SLEEP_BETWEEN_ACTION_CHECK_MS,
            "Timed out waiting for permissions to be set for universal core!");

    // Verify that the permissions were set. If the OS is overloaded, this might take a while.
    GeneralUtils.tryRunTaskWithRetry(new EyesRunnable() {
      @Override
      public void run() throws EyesException {
        Set<PosixFilePermission> retrievedPermissions = null;
        try {
          retrievedPermissions = Files.getPosixFilePermissions(path, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
          String errMsg = GeneralUtils.createErrorMessageFromExceptionWithText(e, 
                  "Got IOException trying to read universal core permissions!");
          System.out.println(errMsg);
          throw new EyesException(errMsg, e);
        }

        if (!(retrievedPermissions.containsAll(permissions))) {
          String errMsg = "Permissions for universal core were not yet set correctly! Current permissions: " + Arrays.toString(retrievedPermissions.toArray());
          System.out.println(errMsg);
          throw new EyesException(errMsg);
        }
        System.out.println("Core permissions verified.");
      }

     @Override
     public String getName() {
       return "Verify core permissions";
     }
    }, MAX_ACTION_WAIT_SECONDS, SLEEP_BETWEEN_ACTION_CHECK_MS,
            "Timed out waiting for permissions to be set for universal core!");

  }

  private static Path copyBinaryFileToLocalPath(InputStream inputStream, Path directoryPath, String filename) {
    Path serverTargetPath = Paths.get(directoryPath + File.separator + filename);

      // If the required path does not exist, create it.
      if (!Files.exists(directoryPath)) {
        System.out.println("The path '" + directoryPath + "' for the universal core does not exist. Creating the directory structure...");
        try {
          Files.createDirectories(directoryPath);
        } catch (Exception e) {
          String errMsg = GeneralUtils.createErrorMessageFromExceptionWithText(e,
                  "Could not create the directory structure '" + directoryPath +   "'!");
          System.out.println(errMsg);
          throw new EyesException(errMsg, e);
        }
        System.out.println("Directory structure created.");
      }

    try {
      Files.copy(inputStream, serverTargetPath, StandardCopyOption.REPLACE_EXISTING);
      System.out.println("Successfully copied the universal core to path: '" + serverTargetPath + "'");
    } catch (Exception e) { // Might actually be a common, non-error, situation - the server might already be running.
      String errMsg = GeneralUtils.createErrorMessageFromExceptionWithText(e,
              "Could not copy universal core to '" + serverTargetPath +   "' (can happen if server is already running)");
      System.out.println(errMsg);
    }

    return serverTargetPath;
  }
}
