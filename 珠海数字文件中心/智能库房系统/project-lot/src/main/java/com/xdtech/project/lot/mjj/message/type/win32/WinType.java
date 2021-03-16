package com.xdtech.project.lot.mjj.message.type.win32;

import com.xdtech.project.lot.mjj.message.type.Type;

public abstract class WinType extends Type {

    @Override
    public void load(byte[] data) {
        byte[] b = new byte[data.length];
        for (int i = b.length - 1; i >= 0; i--) {
            b[b.length - i - 1] = data[i];
        }
        super.load(b);
    }

    @Override
    public byte[] toBytes() {
        byte[] data = super.toBytes();
        byte[] b = new byte[data.length];

        for (int i = b.length - 1; i >= 0; i--) {
            b[b.length - i - 1] = data[i];
        }

        return b;
    }

    @Override
    public String toHex() {

        StringBuffer hex = new StringBuffer("0x");
        for (int i = 0; i < value.length; i++) {
            String s = Integer.toHexString(value[i] & 0x000000ff);
            if (s.length() == 1) {
                hex.append("0");
            }

            hex.append(s);
        }
        return hex.toString();
    }
}
