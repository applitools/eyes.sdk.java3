/*
 * Applitools software.
 */
package com.applitools.utils;

import com.applitools.eyes.*;
import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;
import java.nio.charset.Charset;

public class ImageUtils {

    @SuppressWarnings("WeakerAccess")
    public static final int REQUIRED_IMAGE_TYPE = BufferedImage.TYPE_4BYTE_ABGR;

    public static BufferedImage normalizeImageType(BufferedImage image) {
        if (image.getType() == REQUIRED_IMAGE_TYPE) {
            return image;
        }

        return ImageUtils.copyImageWithType(image, REQUIRED_IMAGE_TYPE);
    }

    /**
     * Encodes a given image as PNG.
     * @param image The image to encode.
     * @return The PNG bytes representation of the image.
     */
    public static byte[] encodeAsPng(BufferedImage image) {

        ArgumentGuard.notNull(image, "image");

        byte[] encodedImage; // PNG representation.
        ByteArrayOutputStream pngBytesStream = new ByteArrayOutputStream();

        try {
            // Get the clipped image in PNG encoding.
            ImageIO.write(image, "png", pngBytesStream);
            pngBytesStream.flush();
            encodedImage = pngBytesStream.toByteArray();
        } catch (IOException e) {
            throw new EyesException("Failed to encode image", e);
        } finally {
            try {
                pngBytesStream.close();
            } catch (IOException e) {
                //noinspection ThrowFromFinallyBlock
                throw new EyesException("Failed to close png byte stream", e);
            }
        }
        return encodedImage;
    }

    /**
     * Creates a {@code BufferedImage} from an image file specified by {@code
     * path}.
     * @param path The path to the image file.
     * @return A {@code BufferedImage} instance.
     * @throws com.applitools.eyes.EyesException If there was a problem
     *                                           creating the {@code BufferedImage} instance.
     */
    public static BufferedImage imageFromFile(String path) throws
            EyesException {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(path));
            // Make sure the image is of the correct type
            image = normalizeImageType(image);
        } catch (IOException e) {
            throw new EyesException("Failed to to load the image bytes from "
                    + path, e);
        }
        return image;
    }

    /**
     * Creates a {@link BufferedImage} from an image file specified by {@code
     * resource}.
     * @param resource The resource path.
     * @return A {@code BufferedImage} instance.
     * @throws EyesException If there was a problem
     *                       creating the {@code BufferedImage} instance.
     */
    public static BufferedImage imageFromResource(String resource) throws
            EyesException {
        BufferedImage image;
        try {
            image = ImageIO.read(ImageUtils.class.getClassLoader()
                    .getResourceAsStream(resource));
            // Make sure the image is of the correct type
            image = normalizeImageType(image);
        } catch (IOException e) {
            throw new EyesException(
                    "Failed to to load the image from resource: " + resource,
                    e);
        }
        return image;
    }

    /**
     * Creates a {@link BufferedImage} from an input stream specified by {@code
     * stream}.
     * @param stream The input stream.
     * @return A {@code BufferedImage} instance.
     * @throws EyesException If there was a problem
     *                       creating the {@code BufferedImage} instance.
     */
    public static BufferedImage imageFromStream(InputStream stream) throws
            EyesException {
        BufferedImage image;
        try {
            image = ImageIO.read(stream);
            // Make sure the image is of the correct type
            image = normalizeImageType(image);
        } catch (IOException e) {
            throw new EyesException(
                    "Failed to to load the image from stream.", e);
        }
        return image;
    }

    /**
     * Creates a {@code BufferedImage} instance from a base64 encoding of an
     * image's bytes.
     * @param image64 The base64 encoding of an image's bytes.
     * @return A {@code BufferedImage} instance.
     * @throws com.applitools.eyes.EyesException If there was a problem
     *                                           creating the {@code BufferedImage} instance.
     */
    public static BufferedImage imageFromBase64(String image64) throws
            EyesException {
        ArgumentGuard.notNullOrEmpty(image64, "image64");

        // Get the image bytes
        byte[] imageBytes =
                Base64.decodeBase64(image64.getBytes(Charset.forName("UTF-8")));
        return imageFromBytes(imageBytes);
    }

    /**
     * @param image The image from which to get its base64 representation.
     * @return The base64 representation of the image (bytes encoded as PNG).
     */
    public static String base64FromImage(BufferedImage image) {
        ArgumentGuard.notNull(image, "image");

        byte[] imageBytes = encodeAsPng(image);
        return Base64.encodeBase64String(imageBytes);
    }

    /**
     * Creates a BufferedImage instance from raw image bytes.
     * @param imageBytes The raw bytes of the image.
     * @return A BufferedImage instance representing the image.
     * @throws EyesException If there was a problem
     *                       creating the {@code BufferedImage} instance.
     */
    public static BufferedImage imageFromBytes(byte[] imageBytes) throws
            EyesException {
        BufferedImage image;
        try {
            ByteArrayInputStream screenshotStream =
                    new ByteArrayInputStream(imageBytes);
            image = ImageIO.read(screenshotStream);
            screenshotStream.close();
            // Make sure the image is of the correct type
            image = normalizeImageType(image);
        } catch (IOException e) {
            throw new EyesException("Failed to create buffered image!", e);
        }
        return image;
    }

    /**
     * Get a copy of the part of the image given by region.
     * @param image  The image from which to get the part.
     * @param region The region which should be copied from the image.
     * @return The part of the image.
     */
    public static BufferedImage getImagePart(BufferedImage image,
                                             Region region) {
        ArgumentGuard.notNull(image, "image");

        // Get the clipped region as a BufferedImage.
        BufferedImage imagePart = image.getSubimage(
                region.getLeft(), region.getTop(), region.getWidth(),
                region.getHeight());
        // IMPORTANT We copy the image this way because just using getSubImage
        // created a later problem (maybe an actual Java bug): the pixels
        // weren't what they were supposed to be.
        byte[] imagePartBytes = encodeAsPng(imagePart);
        return imageFromBytes(imagePartBytes);
    }

    /**
     * Rotates an image by the given degrees.
     * @param image The image to rotate.
     * @param deg   The degrees by which to rotate the image.
     * @return A rotated image.
     */
    public static BufferedImage rotateImage(BufferedImage image, double deg) {
        ArgumentGuard.notNull(image, "image");

        if (deg % 360 == 0) return image;

        double radians = Math.toRadians(deg);

        // We need this to calculate the width/height of the rotated image.
        double angleSin = Math.abs(Math.sin(radians));
        double angleCos = Math.abs(Math.cos(radians));

        int originalWidth = image.getWidth();
        double originalHeight = image.getHeight();

        int rotatedWidth = (int) Math.floor(
                (originalWidth * angleCos) + (originalHeight * angleSin)
        );

        int rotatedHeight = (int) Math.floor(
                (originalHeight * angleCos) + (originalWidth * angleSin)
        );

        BufferedImage rotatedImage =
                new BufferedImage(rotatedWidth, rotatedHeight, image.getType());

        Graphics2D g = rotatedImage.createGraphics();

        // Notice we must first perform translation so the rotated result
        // will be properly positioned.
        g.translate((rotatedWidth - originalWidth) / 2,
                (rotatedHeight - originalHeight) / 2);

        g.rotate(radians, originalWidth / 2, originalHeight / 2);

        g.drawRenderedImage(image, null);
        g.dispose();

        return normalizeImageType(rotatedImage);
    }

    public static boolean areImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Creates a copy of an image with an updated image type.
     * @param src         The image to copy.
     * @param updatedType The type of the copied image.
     *                    See {@link BufferedImage#getType()}.
     * @return A copy of the {@code src} of the requested type.
     */
    public static BufferedImage copyImageWithType(BufferedImage src,
                                                  int updatedType) {
        ArgumentGuard.notNull(src, "src");
        BufferedImage result = new BufferedImage(src.getWidth(),
                src.getHeight(), updatedType);
        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage(src, null);
        g2.dispose();
        return result;
    }

    public static BufferedImage scaleImage(BufferedImage image, double scaleRatio) {
        return scaleImage(image, scaleRatio, false);
    }

    /**
     * Scales an image by the given ratio
     * @param image      The image to scale.
     * @param scaleRatio Factor to multiply the image dimensions by
     * @return If the scale ratio != 1, returns a new scaled image,
     * otherwise, returns the original image.
     */
    public static BufferedImage scaleImage(BufferedImage image, double scaleRatio, boolean isMobile) {
        ArgumentGuard.notNull(image, "image");

        image = normalizeImageType(image);

        if (scaleRatio == 1) {
            return image;
        }

        int targetWidth = (int) Math.ceil(image.getWidth() * scaleRatio);
        int targetHeight;

        // The difference in the scaling between mobile and web is because a legacy difference between
        // the java appium sdk and the java selenium sdk. Changing it may fail a lot of clients' tests.
        if (isMobile) {
            double imageRatio = (double) image.getHeight() / (double) image.getWidth();
            targetHeight = (int) Math.ceil(targetWidth * imageRatio);;
        } else {
            targetHeight = (int) Math.ceil(image.getHeight() * scaleRatio);
        }

        BufferedImage scaledImage = resizeImage(image, targetWidth, targetHeight);

        return normalizeImageType(scaledImage);
    }

    /**
     * Scales an image by the given ratio
     * @param image        The image to scale.
     * @param targetWidth  The width to resize the image to
     * @param targetHeight The height to resize the image to
     * @return If the size of image equal to target size, returns the original image,
     * otherwise, returns a new resized image.
     */
    public static BufferedImage resizeImage(BufferedImage image, int targetWidth, int targetHeight) {
        ArgumentGuard.notNull(image, "image");
        ArgumentGuard.notNull(targetWidth, "targetWidth");
        ArgumentGuard.notNull(targetHeight, "targetHeight");

        image = normalizeImageType(image);

        if (image.getWidth() == targetWidth && image.getHeight() == targetHeight) {
            return image;
        }

        BufferedImage resizedImage;
        if (targetWidth > image.getWidth() || targetHeight > image.getHeight()) {
            resizedImage = scaleImageBicubic(image, targetWidth, targetHeight);
        } else {
            resizedImage = scaleImageIncrementally(image, targetWidth, targetHeight);
        }

        return normalizeImageType(resizedImage);
    }

    private static int interpolateCubic(int x0, int x1, int x2, int x3, double t) {
        int a0 = x3 - x2 - x0 + x1;
        int a1 = x0 - x1 - a0;
        int a2 = x2 - x0;
        return (int) Math.max(0, Math.min(255, (a0 * (t * t * t)) + (a1 * (t * t)) + (a2 * t) + (x1)));
    }

    private static BufferedImage scaleImageBicubic(BufferedImage srcImage, int targetWidth, int targetHeight) {

        normalizeImageType(srcImage);

        DataBuffer bufSrc = srcImage.getRaster().getDataBuffer();
        DataBuffer bufDst = new DataBufferByte(targetWidth * targetHeight * 4);

        int wSrc = srcImage.getWidth();
        int hSrc = srcImage.getHeight();

        // when dst smaller than src/2, interpolate first to a multiple between 0.5 and 1.0 src, then sum squares
        int wM = (int) Math.max(1, Math.floor(wSrc / targetWidth));
        int wDst2 = targetWidth * wM;
        int hM = (int) Math.max(1, Math.floor(hSrc / targetHeight));
        int hDst2 = targetHeight * hM;

        int i, j, k, xPos, yPos, kPos, buf1Pos, buf2Pos;
        double x, y, t;

        // Pass 1 - interpolate rows
        // buf1 has width of dst2 and height of src
        DataBuffer buf1 = new DataBufferByte(wDst2 * hSrc * 4);
        for (i = 0; i < hSrc; i++) {
            for (j = 0; j < wDst2; j++) {
                x = (double) j * (wSrc - 1) / wDst2;
                xPos = (int) Math.floor(x);
                t = x - xPos;
                int srcPos = (i * wSrc + xPos) * 4;

                buf1Pos = (i * wDst2 + j) * 4;
                for (k = 0; k < 4; k++) {
                    kPos = srcPos + k;
                    int x0 = (xPos > 0) ? bufSrc.getElem(kPos - 4) : 2 * bufSrc.getElem(kPos) - bufSrc.getElem(kPos + 4);
                    int x1 = bufSrc.getElem(kPos);
                    int x2 = bufSrc.getElem(kPos + 4);
                    int x3 = (xPos < wSrc - 2) ? bufSrc.getElem(kPos + 8) : 2 * bufSrc.getElem(kPos + 4) - bufSrc.getElem(kPos);
                    buf1.setElem(buf1Pos + k, interpolateCubic(x0, x1, x2, x3, t));
                }
            }
        }

        // Pass 2 - interpolate columns
        // buf2 has width and height of dst2
        DataBuffer buf2 = new DataBufferByte(wDst2 * hDst2 * 4);
        for (i = 0; i < hDst2; i++) {
            y = (double) i * (hSrc - 1) / hDst2;
            yPos = (int) Math.floor(y);
            t = y - yPos;
            for (j = 0; j < wDst2; j++) {
                buf1Pos = (yPos * wDst2 + j) * 4;
                buf2Pos = (i * wDst2 + j) * 4;
                for (k = 0; k < 4; k++) {
                    kPos = buf1Pos + k;
                    int y0 = (yPos > 0) ? buf1.getElem(kPos - wDst2 * 4) : 2 * buf1.getElem(kPos) - buf1.getElem(kPos + wDst2 * 4);
                    int y1 = buf1.getElem(kPos);
                    int y2 = buf1.getElem(kPos + wDst2 * 4);
                    int y3 = (yPos < hSrc - 2) ? buf1.getElem(kPos + wDst2 * 8) : 2 * buf1.getElem(kPos + wDst2 * 4) - buf1.getElem(kPos);
                    //noinspection SuspiciousNameCombination
                    buf2.setElem(buf2Pos + k, interpolateCubic(y0, y1, y2, y3, t));
                }
            }
        }

        // Pass 3 - scale to dst
        int m = wM * hM;
        if (m > 1) {
            for (i = 0; i < targetHeight; i++) {
                for (j = 0; j < targetWidth; j++) {
                    int r = 0;
                    int g = 0;
                    int b = 0;
                    int a = 0;
                    for (y = 0; y < hM; y++) {
                        yPos = (int) (i * hM + y);
                        for (x = 0; x < wM; x++) {
                            xPos = (int) (j * wM + x);
                            int xyPos = (yPos * wDst2 + xPos) * 4;
                            r += buf2.getElem(xyPos);
                            g += buf2.getElem(xyPos + 1);
                            b += buf2.getElem(xyPos + 2);
                            a += buf2.getElem(xyPos + 3);
                        }
                    }

                    int pos = (i * targetWidth + j) * 4;
                    bufDst.setElem(pos, Math.round(r / m));
                    bufDst.setElem(pos + 1, Math.round(g / m));
                    bufDst.setElem(pos + 2, Math.round(b / m));
                    bufDst.setElem(pos + 3, Math.round(a / m));
                }
            }
        } else {
            bufDst = buf2;
        }

        BufferedImage dstImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_4BYTE_ABGR);
        dstImage.setData(Raster.createRaster(dstImage.getSampleModel(), bufDst, null));
        return dstImage;
    }

    private static BufferedImage scaleImageIncrementally(BufferedImage src, int targetWidth, int targetHeight) {
        boolean hasReassignedSrc = false;

        src = normalizeImageType(src);

        int currentWidth = src.getWidth();
        int currentHeight = src.getHeight();

        // For ultra quality should use 7
        int fraction = 2;

        do {
            int prevCurrentWidth = currentWidth;
            int prevCurrentHeight = currentHeight;

            // If the current width is bigger than our target, cut it in half and sample again.
            if (currentWidth > targetWidth) {
                currentWidth -= (currentWidth / fraction);

                // If we cut the width too far it means we are on our last iteration. Just set it to the target width and finish up.
                if (currentWidth < targetWidth)
                    currentWidth = targetWidth;
            }

            // If the current height is bigger than our target, cut it in half and sample again.
            if (currentHeight > targetHeight) {
                currentHeight -= (currentHeight / fraction);

                // If we cut the height too far it means we are on our last iteration. Just set it to the target height and finish up.
                if (currentHeight < targetHeight)
                    currentHeight = targetHeight;
            }

            // Stop when we cannot incrementally step down anymore.
            if (prevCurrentWidth == currentWidth && prevCurrentHeight == currentHeight)
                break;

            // Render the incremental scaled image.
            BufferedImage incrementalImage = scaleImageBicubic(src, currentWidth, currentHeight);

            // Before re-assigning our interim (partially scaled) incrementalImage to be the new src image before we iterate around
            // again to process it down further, we want to flush() the previous src image IF (and only IF) it was one of our own temporary
            // BufferedImages created during this incremental down-sampling cycle. If it wasn't one of ours, then it was the original
            // caller-supplied BufferedImage in which case we don't want to flush() it and just leave it alone.
            if (hasReassignedSrc)
                src.flush();

            // Now treat our incremental partially scaled image as the src image
            // and cycle through our loop again to do another incremental scaling of it (if necessary).
            src = incrementalImage;

            // Keep track of us re-assigning the original caller-supplied source image with one of our interim BufferedImages
            // so we know when to explicitly flush the interim "src" on the next cycle through.
            hasReassignedSrc = true;
        } while (currentWidth != targetWidth || currentHeight != targetHeight);

        return src;
    }

    public static BufferedImage cropImage(Logger logger, BufferedImage image,
                                          Rectangle regionToCrop) {
        return cropImage(image, new Region(regionToCrop.x, regionToCrop.y, regionToCrop.width, regionToCrop.height));
    }

    /**
     * Removes a given region from the image.
     * @param image        The image to crop.
     * @param regionToCrop The region to crop from the image.
     * @return A new image without the cropped region.
     */
    public static BufferedImage cropImage(BufferedImage image, Region regionToCrop) {
        Region imageRegion = new Region(0, 0, image.getWidth(), image.getHeight());
        imageRegion.intersect(regionToCrop);
        if (imageRegion.isSizeEmpty()) {
            return image;
        }

        BufferedImage croppedImage = Scalr.crop(image, imageRegion.getLeft(),
                imageRegion.getTop(), imageRegion.getWidth(),
                imageRegion.getHeight());

        return normalizeImageType(croppedImage);
    }

    /**
     * Save image to local file system
     * @param image    The image to save.
     * @param filename The path to save image
     */
    public static void saveImage(BufferedImage image, String filename) {
        try {
            File file = new File(filename);
            File path = file.getParentFile();
            if (path != null && !path.exists()) {
                path.mkdirs();
            }
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            throw new EyesException("Failed to save image", e);
        }
    }

}
