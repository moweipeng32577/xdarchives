package com.xdtech.project.lot.mjj.message.type;

public class Tag128 extends Type {

    public Tag128() {
        this(new byte[128]);
    }

    public Tag128(byte[] bytes) {
        load(bytes);
    }

    public String toTag() {
        return toHex().substring(2).toLowerCase();
    }

    @Override
    public int sizeof() {
        return 16;
    }

}
