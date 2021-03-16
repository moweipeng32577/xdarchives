package com.xdtech.project.lot.util;

public class BinaryToHex {

    public static String binaryToHex(byte[] data){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < data.length; i++){
            //把byte数据转成十六进制数据
            String hex = Integer.toHexString(data[i] & 0xFF);
            if(hex.length() < 2){
                result.append(0);
            }
            result.append(hex);
        }
        return result.toString();
    }
}
