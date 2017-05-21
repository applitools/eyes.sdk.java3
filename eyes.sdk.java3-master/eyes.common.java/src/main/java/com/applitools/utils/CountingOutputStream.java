/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream with a counter of the number of bytes written.
 *
 * IMPORTANT: Of course, if the underlying stream adds bytes on its own, these
 *          bytes will not be counted.
 */
public class CountingOutputStream extends OutputStream{

    private OutputStream outputStream;
    private long bytesCount;

    public CountingOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        bytesCount = 0;
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
        bytesCount += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        bytesCount += len;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        ++bytesCount;
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    /**
     * @return The number of bytes written so far.
     */
    public long getBytesCount() {
        return bytesCount;
    }
}