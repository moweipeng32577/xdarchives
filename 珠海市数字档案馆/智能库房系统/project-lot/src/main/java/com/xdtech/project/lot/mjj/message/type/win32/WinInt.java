package com.xdtech.project.lot.mjj.message.type.win32;

import org.apache.commons.lang.StringUtils;

public abstract class WinInt extends WinType {

    public WinInt() {
        this(0);
    }

    public WinInt(int value) {
        load(value);
    }

    public WinInt(byte[] value) {
        load(value);
    }

    public WinInt(String value) {
        load(value);
    }

    public void load(String data) {
        if (StringUtils.isBlank(data)) {
            data = "0x0";
        }

        if (data.startsWith("0x")) {
            String s = data.substring(2);

            if (s.length() % 2 != 0) {
                s = "0" + s;
            }

            char[] chars = s.toCharArray();
            byte[] b = new byte[chars.length / 2];

            for (int i = 0; i < chars.length; i++) {
                if (i == 0 || i % 2 == 0) {
                    String text = new String(new char[]{chars[i]}) + new String(new char[]{chars[i + 1]});
                    int n = Integer.decode("0x" + text);
                    b[i / 2] = (byte) n;
                }

            }

            byte[] b2 = new byte[b.length];
            for (int i = b.length - 1; i >= 0; i--) {
                b2[b.length - i - 1] = b[i];
            }

            load(b2);
        } else if (data.matches("^[-\\+]?[\\d]*$")) {
            load(Integer.parseInt(data));
        }
    }

    public void load(int data) {
        byte[] result = new byte[4];
        result[3] = (byte) (data >> 24);
        result[2] = (byte) (data >> 16);
        result[1] = (byte) (data >> 8);
        result[0] = (byte) (data);

        load(result);
    }

    public int toInt() {
        byte[] b = new byte[4];

        if (value.length < b.length) {
            for (int i = 0; i < 4; i++) {
                if (i < (b.length - value.length)) {
                    b[i] = 0;
                } else {
                    b[i] = value[i - (b.length - value.length)];
                }
            }
        } else {
            for (int i = 0; i < b.length; i++) {
                b[i] = value[i];
            }
        }

        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < 4; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }

        return n;
    }

}
