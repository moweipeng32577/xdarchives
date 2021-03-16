package com.xdtech.project.lot.mjj.message.type;

public class Int8 extends Int {

    public Int8() {
        super();
    }

    public Int8(int value) {
        super(value);
    }

    public Int8(byte[] value) {
        super(value);
    }

    public Int8(String value) {
        super(value);
    }

    public byte toByte() {
        return toBytes()[0];
    }

    @Override
    public int sizeof() {
        return 1;
    }

}
