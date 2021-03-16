package com.xdtech.project.lot.mjj.message.type;

public class RFIDCode {
    private byte[] value;

    public RFIDCode(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException();
        }
        value = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            value[bytes.length - i - 1] = bytes[i];
        }
    }

    public String toRFIDCode() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.value.length; ++i) {
            String temp = Integer.toHexString(this.value[i] & 0xFF);
            if (temp.length() == 1)
                sb.append("0");

            sb.append(temp);
        }
        return sb.toString().toUpperCase();
    }

}
