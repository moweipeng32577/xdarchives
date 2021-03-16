package com.xdtech.project.lot.mjj.message.util;

/**
 * @author wujy
 */
public class ToHexUtil {

    public static String  bytetoHexUtil(byte[] data){

        StringBuffer hex = new StringBuffer();
        byte[] b = data;

        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0x000000ff);
            if (s.length() == 1) {
                hex.append("0");
            }

            hex.append(s);
        }
        return hex.toString();
    }
}
