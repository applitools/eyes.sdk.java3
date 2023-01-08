package com.applitools.eyes.universal.server;

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
import com.applitools.eyes.Logger;
import com.applitools.eyes.logging.Stage;
import com.applitools.eyes.logging.TraceLevel;
import com.applitools.eyes.universal.utils.SystemInfo;
import com.applitools.utils.GeneralUtils;

/**
 * Universal Sdk Native Loader.
 */
public class UniversalSdkNativeLoader {
  private static Process nativeProcess = null;
  private static Integer port;
  private static final String BINARY_SERVER_PATH = "APPLITOOLS_UNIVERSAL_PATH";
  private static final String TEMP_FOLDER_PATH = "java.io.tmpdir";
  private static final long MAX_ACTION_WAIT_SECONDS = 90;
  private static final long SLEEP_BETWEEN_ACTION_CHECK_MS = 3000;
  private static Logger logger = new Logger();
  private static final TraceLevel INFO_LOG_LEVEL = TraceLevel.Info;
  private static final String UNIVERSAL_DEBUG = GeneralUtils.getEnvString("APPLITOOLS_UNIVERSAL_DEBUG");

  public synchronized static void start() {
    try {
      copyAndStartUniversalCore();
    } catch (Exception e) {
      String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e, "Could not launch server!");
      logger.log(TraceLevel.Error, Stage.GENERAL, errorMessage);
      throw new EyesException(errorMessage, e);
    }
  }

//  public static void stopProcess() {
//    if (nativeProcess != null && nativeProcess.isAlive()) {
//      nativeProcess.destroy();
//    }
//  }

  /**
   * for internal use only.
   */
  private static void destroyProcess() {
    if (nativeProcess.isAlive() && nativeProcess != null) {
      try {
        nativeProcess.destroy();
        nativeProcess.waitFor();
        nativeProcess = null;
      }
      catch (Exception e) {
        String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e, "Could not destroy server!");
        System.out.println(errorMessage);
        throw new EyesException(errorMessage, e);
      }
    }
  }

  private static void copyAndStartUniversalCore() throws Exception {
    if (nativeProcess == null) {

      // Get the OS we're running on.
      SystemInfo systemInfo = SystemInfo.getSystemInfo();

      logger.log(INFO_LOG_LEVEL, Stage.GENERAL, String.format("Identifies OS: %s", systemInfo.getOs()));
      logger.log(INFO_LOG_LEVEL, Stage.GENERAL, String.format("Identifies Architecture: %s", systemInfo.getArchitecture()));

      // Set the target path for the server
      String userSetPath = GeneralUtils.getEnvString(BINARY_SERVER_PATH); // might not exist
      Path directoryPath =
          userSetPath == null ? Paths.get(GeneralUtils.getPropertyString(TEMP_FOLDER_PATH)) : Paths.get(userSetPath);

      String serverFilename = "eyes-universal-" + systemInfo.getSuffix();

      Path serverTargetPath;
      // Read the server bytes from SDK resources, and write it to the target path
      String pathInJar = getBinaryPath(systemInfo.getOs(), systemInfo.getSuffix());
      try (InputStream serverBinaryAsStream = getFileFromResourceAsStream(pathInJar)) {
        serverTargetPath = copyBinaryFileToLocalPath(serverBinaryAsStream, directoryPath, serverFilename);
      }

      // Set the permissions on the binary
      setAndVerifyPosixPermissionsForUniversalCore(systemInfo.getOsVersion(), serverTargetPath);

      createProcessAndReadPort(serverTargetPath.toString());
    }
  }

  private static void createProcessAndReadPort(String executablePath) {

    if (Boolean.parseBoolean(UNIVERSAL_DEBUG)) {
      GeneralUtils.tryRunTaskWithRetry(new EyesRunnable() {
        @Override
        public void run() {
          try {
            nativeProcess = new ProcessBuilder(executablePath, "--port 0", "--no-singleton", "--shutdown-mode", "--debug", "stdin").start();
          } catch (Exception e) {
            String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e, "Failed to start universal core!");
            logger.log(INFO_LOG_LEVEL, Stage.GENERAL, errorMessage);
            throw new EyesException(errorMessage, e);
          }
          logger.log(INFO_LOG_LEVEL, Stage.GENERAL, "Universal Core (debug) start returned ok.");
        }

        @Override
        public String getName() {
          return "Start universal core";
        }
      }, MAX_ACTION_WAIT_SECONDS, SLEEP_BETWEEN_ACTION_CHECK_MS, "Timed out trying to start universal core!", logger);
    } else {
      GeneralUtils.tryRunTaskWithRetry(new EyesRunnable() {
        @Override
        public void run() {
          try {
            nativeProcess = new ProcessBuilder(executablePath, "--port 0", "--no-singleton", "--shutdown-mode", "stdin").start();
          } catch (Exception e) {
            String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e, "Failed to start universal core!");
            logger.log(INFO_LOG_LEVEL, Stage.GENERAL, errorMessage);
            throw new EyesException(errorMessage, e);
          }
          logger.log(INFO_LOG_LEVEL, Stage.GENERAL, "Universal Core start returned ok.");
        }

        @Override
        public String getName() {
          return "Start universal core";
        }
      }, MAX_ACTION_WAIT_SECONDS, SLEEP_BETWEEN_ACTION_CHECK_MS, "Timed out trying to start universal core!", logger);
    }

    GeneralUtils.tryRunTaskWithRetry(new EyesRunnable() {
      @Override
      public void run() throws EyesException {
        String inputLineFromServer="";

        try {
          // IMPORTANT: Do NOT close this stream, you might cause the child process to get stuck, and
          // you will NOT be able to re-open it for retry.
          InputStream childOutputStream = nativeProcess.getInputStream();

          // We need to check for this instead of just reading the bytes, as reading will BLOCK, and
          // if this is a retry, and the server will not output anything, we'll be stuck forever.
          if (childOutputStream.available() == 0) {
            String errorMessage = "server did not yet output the port number..";
            logger.log(TraceLevel.Error, Stage.GENERAL, errorMessage);
            throw new EyesException(errorMessage);
          }

          BufferedReader reader = new BufferedReader(new InputStreamReader(childOutputStream));
          inputLineFromServer = reader.readLine();
          port = Integer.parseInt(inputLineFromServer);
          logger.log(INFO_LOG_LEVEL, Stage.GENERAL, "Port read and parsed okay: " + port);
        } catch (IOException e) {
          String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e,
                  "Failed to get core's input stream!");
          logger.log(TraceLevel.Error, Stage.GENERAL, errorMessage);
          throw new EyesException(errorMessage, e);
        } catch (NumberFormatException nfe) {
          String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(nfe,
                  "Got a non-integer as port! Text: '" + inputLineFromServer + "'");
          logger.log(TraceLevel.Error, Stage.GENERAL, errorMessage);
          throw new EyesException(errorMessage, nfe);
        }
      }

      @Override
      public String getName() {
        return "Read universal core port";
      }
    }, MAX_ACTION_WAIT_SECONDS, SLEEP_BETWEEN_ACTION_CHECK_MS, "Timed out trying to read port from core!", logger);
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
          logger.log(INFO_LOG_LEVEL, Stage.GENERAL, "'Set core permissions' ended. Now verifying the permissions...");
        } catch (Exception e) {
          String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e,
                  "Could not set permissions on the universal core file!");
          logger.log(TraceLevel.Error, Stage.GENERAL, errorMessage);
          throw new EyesException(errorMessage, e);
        }
      }

      @Override
      public String getName() {
        return "Set core permissions";
      }
    }, MAX_ACTION_WAIT_SECONDS, SLEEP_BETWEEN_ACTION_CHECK_MS,
            "Timed out waiting for permissions to be set for universal core!", logger);

    // Verify that the permissions were set. If the OS is overloaded, this might take a while.
    GeneralUtils.tryRunTaskWithRetry(new EyesRunnable() {
      @Override
      public void run() throws EyesException {
        Set<PosixFilePermission> retrievedPermissions = null;
        try {
          retrievedPermissions = Files.getPosixFilePermissions(path, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
          String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e,
                  "Got IOException trying to read universal core permissions!");
          logger.log(TraceLevel.Error, Stage.GENERAL, errorMessage);
          throw new EyesException(errorMessage, e);
        }

        if (!(retrievedPermissions.containsAll(permissions))) {
          String errorMessage = "Permissions for universal core were not yet set correctly! Current permissions: " + Arrays.toString(retrievedPermissions.toArray());
          logger.log(TraceLevel.Error, Stage.GENERAL, errorMessage);
          throw new EyesException(errorMessage);
        }
        logger.log(INFO_LOG_LEVEL, Stage.GENERAL, "Core permissions verified.");
      }

     @Override
     public String getName() {
       return "Verify core permissions";
     }
    }, MAX_ACTION_WAIT_SECONDS, SLEEP_BETWEEN_ACTION_CHECK_MS,
            "Timed out waiting for permissions to be set for universal core!", logger);

  }

  private static Path copyBinaryFileToLocalPath(InputStream inputStream, Path directoryPath, String filename) {
    Path serverTargetPath = Paths.get(directoryPath + File.separator + filename);

      // If the required path does not exist, create it.
      if (!Files.exists(directoryPath)) {
        logger.log(INFO_LOG_LEVEL, Stage.GENERAL, "The path '" + directoryPath + "' for the universal core does not exist. Creating the directory structure...");
        try {
          Files.createDirectories(directoryPath);
        } catch (Exception e) {
          String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e,
                  "Could not create the directory structure '" + directoryPath +   "'!");
          logger.log(TraceLevel.Error, Stage.GENERAL, errorMessage);
          throw new EyesException(errorMessage, e);
        }
        System.out.println("Directory structure created.");
      }

    try {
      Files.copy(inputStream, serverTargetPath, StandardCopyOption.REPLACE_EXISTING);
      logger.log(INFO_LOG_LEVEL, Stage.GENERAL, "Successfully copied the universal core to path: '" + serverTargetPath + "'");
    } catch (Exception e) { // Might actually be a common, non-error, situation - the server might already be running.
      String errorMessage = GeneralUtils.createErrorMessageFromExceptionWithText(e,
              "Could not copy universal core to '" + serverTargetPath +   "' (can happen if server is already running)");
      logger.log(TraceLevel.Error, Stage.GENERAL, errorMessage);
    }

    return serverTargetPath;
  }

  public static void setLogger(Logger logger1) {
    logger = logger1;
  }
}
