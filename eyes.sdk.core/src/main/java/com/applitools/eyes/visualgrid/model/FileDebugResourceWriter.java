package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.Logger;
import com.applitools.utils.GeneralUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

public class FileDebugResourceWriter implements IDebugResourceWriter {

    private static final String DEFAULT_PREFIX = "resource_";
    private static final String DEFAULT_PATH = "";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private Logger logger;
    private String path;
    private String prefix;
    private String filter;

    public FileDebugResourceWriter(Logger logger, String path, String prefix, String filter) {
        this.logger = logger;
        this.setPath(path);
        this.setPrefix(prefix);
        this.filter = filter;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    public void setPath(String path) {
        if (path != null) {
            path = path.endsWith("/") ? path : path + '/';
        } else {
            path = DEFAULT_PATH;
        }

        this.path = path;
    }

    @Override
    public void write(RGridResource resource) {
        if(resource == null ) return;
        String url = resource.getUrl();
        if (filter == null || filter.isEmpty() || url.toUpperCase().contains(filter.toUpperCase())) {
            try {
                String urlHash = GeneralUtils.getSha256hash(url.getBytes(StandardCharsets.UTF_8));
                String ext = resource.getContentType();
                int slash = ext.indexOf("/");
                ext = ext.substring(slash + 1);
                int semicolon = ext.indexOf(";");
                if (semicolon > -1) {
                    ext = ext.substring(0, semicolon);
                }
                String pathname = path + prefix + urlHash + "_" + resource.getSha256() + "." + ext;
                pathname = pathname.replaceAll("\\?", "_");
                File file = new File(pathname);
                ensureFilePath(file);
                logger.verbose("writing resource to file: " + file);
                byte[] data = resource.getContent();
                try (FileOutputStream stream = new FileOutputStream(file)) {
                    stream.write(data);
                    stream.flush();
                    stream.close();
                }
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    private void ensureFilePath(File file) {
        File path = file.getParentFile();
        if (path != null && !path.exists()) {
            System.out.println("No Folder");
            boolean success = path.mkdirs();
            System.out.println("Folder created");
        }
    }
}
