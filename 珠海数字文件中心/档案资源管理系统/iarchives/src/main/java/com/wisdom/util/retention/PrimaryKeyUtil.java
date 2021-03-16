package com.wisdom.util.retention;

import java.util.UUID;

public class PrimaryKeyUtil {

    public static String getPrimaryKey(){
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString().toLowerCase();
        uuidStr = uuidStr.replaceAll("-", "");
        return uuidStr;
    }

    public static void main(String[] args) {
        for(int i = 0; i < 35; i++){

            System.out.println(PrimaryKeyUtil.getPrimaryKey());
        }
    }
}
