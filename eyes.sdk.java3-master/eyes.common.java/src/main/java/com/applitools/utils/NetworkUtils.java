package com.applitools.utils;

import java.net.*;
import java.util.Enumeration;

/**
 * Network-related utilities.
 */
public class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * @return The non-loopback IP address of the local host or {@code null}
     * if none is available.
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces =
                    NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface current = interfaces.nextElement();

                if (!current.isUp() || current.isLoopback() || current.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = current.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isLoopbackAddress()
                            || !(address instanceof Inet4Address)) {
                        continue;
                    }

                    return address.getHostAddress();
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        return null;
    }

    /**
     * @param url The URL string to parse.
     * @return A {@link java.net.URI} object representing the input URL.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static URI getUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param url The URL string to parse.
     * @return A {@link java.net.URL} object representing the input URL.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static URL getUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
