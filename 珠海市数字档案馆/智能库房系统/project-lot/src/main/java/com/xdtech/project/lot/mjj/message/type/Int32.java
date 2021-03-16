package com.xdtech.project.lot.mjj.message.type;

public class Int32 extends Int {

    public Int32() {
        super();
    }

    public Int32(int value) {
        super(value);
    }

    public Int32(byte[] value) {
        super(value);
    }

    public Int32(String value) {
        super(value);
    }

    @Override
    public int sizeof() {
        return 4;
    }

}
