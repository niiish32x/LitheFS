package com.niiish32x.lithefs.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 用于获取本机当前的IP
 */
public class IPAddressUtil {
    public static String getIPAddress() {
        String ipAddress = null;
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            ipAddress = localhost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }
}