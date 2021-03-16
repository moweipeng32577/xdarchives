package com.xdtech.project.lot.mjj.message.type;

public class Int16 extends Int {

    public Int16() {
        super();
    }

    public Int16(int value) {
        super(value);
    }

    public Int16(byte[] value) {
        super(value);
    }

    public Int16(String value) {
        super(value);
    }

    @Override
    public int sizeof() {
        return 2;
    }


}
