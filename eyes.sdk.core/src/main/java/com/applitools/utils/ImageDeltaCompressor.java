/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * Provides image compression based on delta between consecutive images.
 */
public class ImageDeltaCompressor {

    private static final byte[] PREAMBLE;
    private static final byte COMPRESS_BY_RAW_BLOCKS_FORMAT = 3;

    // Init the preamble (needs to be in a static init block since we must
    // handle encoding exception).
    static {
        byte[] preambleBytes;
        try {
            preambleBytes = "applitools".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Use the default charset. Less desirable, but should work
            // in most cases.
            preambleBytes = "applitools".getBytes();
        }

        PREAMBLE = preambleBytes;
    }

    /**
     * Encapsulates a result for the CompareAndCopyBlockChannelData function.
     */
    private static class CompareAndCopyBlockChannelDataResult {
        private boolean isIdentical;
        private byte[] buffer;

        /**
         *
         * @param isIdentical Whether or not the target block was identical to
         *                    the source block.
         * @param buffer The target block's pixel values for a specific channel.
         */
        public CompareAndCopyBlockChannelDataResult(boolean isIdentical,
                byte[] buffer) {
            this.isIdentical = isIdentical;
            this.buffer = buffer;
        }

        /**
         * @return Whether or not the target block was identical to the
         * source block.
         */
        public boolean getIsIdentical() {
            return isIdentical;
        }

        /**
         * @return The target block's pixel values for a specific channel.
         */
        public byte[] getBuffer() {
            return buffer;
        }
    }

    /**
     * Computes the width and height of the image model contained in the block
     * at the input column and row.
     * @param imageSize The image size in pixels.
     * @param blockSize The block size for which we would like to compute the
     *                  image model width and height.
     * @param blockColumn The block column index
     * @param blockRow The block row index
     * @return The width and height of the image model contained in the block.
     */
    private static Dimension getActualBlockSize(Dimension imageSize,
            int blockSize, int blockColumn, int blockRow) {
        int actualWidth = Math.min(imageSize.width - (blockColumn * blockSize),
                blockSize);
        int actualHeight = Math.min(imageSize.height - (blockRow * blockSize),
                                blockSize);

        return new Dimension(actualWidth, actualHeight);
    }

    /**
     * Compares a block of pixels between the source and target images.
     * @param sourcePixels The pixels of the source block.
     * @param targetPixels The pixels of the target block
     * @param imageSize The image size in pixels.
     * @param pixelLength Bytes per pixel. Since pixel might include alpha.
     * @param blockSize The block size in pixels.
     * @param blockColumn The column index of the block to compare.
     * @param blockRow The row index of the block to compare.
     * @param channel The channel for which we compare the blocks
     * @return Whether the source and target blocks are identical,
     * and a copy of the target block's channel bytes.
     */
    @SuppressWarnings("SpellCheckingInspection")
    private static CompareAndCopyBlockChannelDataResult
            CompareAndCopyBlockChannelData(
                byte[] sourcePixels, byte[] targetPixels,
                Dimension imageSize, int pixelLength, int blockSize,
            int blockColumn, int blockRow, int channel) {

        boolean isIdentical = true; // initial default

        // Getting the actual amount of model in the block we wish to copy
        Dimension actualBlockSize =
                getActualBlockSize(imageSize, blockSize, blockColumn, blockRow);

        int actualBlockHeight = actualBlockSize.height;
        int actualBlockWidth = actualBlockSize.width;

        int stride = imageSize.width * pixelLength;

        // The number of bytes actually contained in the block for the
        // current channel (might be less than blockSize*blockSize)
        byte[] channelBytes = new byte[actualBlockHeight*actualBlockWidth];
        int channelBytesOffset = 0;

        // Actually comparing and copying the pixels
        byte sourceByte, targetByte;
        for (int h = 0; h < actualBlockHeight; ++h) {
            int offset = (((blockSize * blockRow) + h) * stride) +
                    (blockSize * blockColumn * pixelLength) + channel;
            for (int w = 0; w < actualBlockWidth; ++w) {
                sourceByte = sourcePixels[offset];
                targetByte = targetPixels[offset];
                if (sourceByte != targetByte) {
                    isIdentical = false;
                }

                channelBytes[channelBytesOffset++] = targetByte;
                offset += pixelLength;
            }
        }

        return new CompareAndCopyBlockChannelDataResult(isIdentical,
                channelBytes);
    }

    /**
     * Compresses a target image based on a difference from a source image.
     *
     * @param target The image we want to compress. (type is TYPE_4BYTE_ABGR)
     * @param targetEncoded The image we want to compress in its png bytes
     *                      representation.
     * @param source The baseline image by which a compression will be
     *               performed. (type is TYPE_4BYTE_ABGR)
     * @param blockSize How many pixels per block.
     * @return The compression result, or the {@code targetEncoded} if the
     * compressed bytes count is greater than the uncompressed bytes count.
     * @throws java.io.IOException If there was a problem reading/writing
     * from/to the streams which are created during the process.
     */
    public static byte[] compressByRawBlocks(BufferedImage target,
            byte[] targetEncoded, BufferedImage source, int blockSize)
                throws IOException {

        // If there's no image to compare to, or the images are in different
        // sizes, we simply return the encoded target.
        if (source == null
                || (source.getWidth() != target.getWidth())
                || (source.getHeight() != target.getHeight())) {
            return targetEncoded;
        }

        // IMPORTANT: Notice that the pixel bytes are (A)BGR!
        byte[] targetPixels =
                ((DataBufferByte) target.getRaster().getDataBuffer()).getData();
        byte[] sourcePixels =
                ((DataBufferByte) source.getRaster().getDataBuffer()).getData();

        // The number of bytes comprising a pixel (depends if there's an
        // Alpha channel).
        int pixelLength = (target.getAlphaRaster() != null) ? 4 : 3;
        Dimension imageSize = new Dimension(target.getWidth(),
                                            target.getHeight());

        // Calculating how many block columns and rows we've got.
        int blockColumnsCount = (target.getWidth() / blockSize)
                + ((target.getWidth() % blockSize) == 0 ? 0 : 1);
        int blockRowsCount = (target.getHeight() / blockSize)
                + ((target.getHeight() % blockSize) == 0 ? 0 : 1);

        // We'll use a stream for the compression.
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        CountingOutputStream resultCountingStream =
                new CountingOutputStream(resultStream);
        // Since we need to write "short" and other variations.
        DataOutputStream resultDataOutputStream =
                new DataOutputStream(resultCountingStream);
        // This will be used for doing actual model compression
        DeflaterOutputStream compressed =
                new DeflaterOutputStream(resultCountingStream,
                        new Deflater(Deflater.BEST_COMPRESSION,true));

        DataOutputStream compressedDos = new DataOutputStream(compressed);

        // Writing the header
        resultStream.write(PREAMBLE, 0, PREAMBLE.length);
        resultStream.write(COMPRESS_BY_RAW_BLOCKS_FORMAT);
        // since we don't have a source ID, we write 0 length (Big endian).
        resultDataOutputStream.writeShort(0);

        // Writing the block size (Big endian)
        resultDataOutputStream.writeShort(blockSize);

        CompareAndCopyBlockChannelDataResult compareResult;
        for (int channel = 0; channel < 3; ++channel) {

            // The image is RGB, so all that's left is to skip the Alpha
            // channel if there is one.
            int actualChannelIndex = (pixelLength == 4) ? channel + 1 : channel;

            int blockNumber = 0;
            for (int blockRow = 0; blockRow < blockRowsCount; ++blockRow) {
                for (int blockColumn = 0; blockColumn < blockColumnsCount;
                        ++blockColumn) {

                    compareResult = CompareAndCopyBlockChannelData
                            (sourcePixels, targetPixels, imageSize,
                                    pixelLength, blockSize, blockColumn,
                                    blockRow, actualChannelIndex);

                    if (!compareResult.getIsIdentical()) {
                        compressed.write(channel);
                        compressedDos.writeInt(blockNumber); // Big endian
                        byte[] channelBytes = compareResult.getBuffer();
                        compressed.write(channelBytes, 0, channelBytes.length);

                        // If the number of bytes already written is greater
                        // then the number of bytes for the uncompressed
                        // target, we just return the uncompressed target.
                        if (resultCountingStream.getBytesCount()
                            > targetEncoded.length) {
                            compressedDos.close();
                            return Arrays.copyOf(targetEncoded,
                                                    targetEncoded.length);
                        }
                    }

                    ++blockNumber;
                }
            }
        }
        compressedDos.close(); // flushing + closing the compression.

        if (resultCountingStream.getBytesCount() > targetEncoded.length) {
            return targetEncoded;
        }

        return resultStream.toByteArray();
    }

    /**
     * Compresses a target image based on a difference from a source image.
     * {@code blockSize} defaults to 10.
     * @param target The image we want to compress.
     * @param targetEncoded The image we want to compress in its png bytes
     *                      representation.
     * @param source The baseline image by which a compression will be
     *               performed.
     * @return The compression result.
     * @throws java.io.IOException If there was a problem reading/writing
     * from/to the streams which are created during the process.
     */
    public static byte[] compressByRawBlocks(BufferedImage target,
            byte[] targetEncoded, BufferedImage source) throws IOException {
        return compressByRawBlocks(target, targetEncoded, source, 10);
    }
}
