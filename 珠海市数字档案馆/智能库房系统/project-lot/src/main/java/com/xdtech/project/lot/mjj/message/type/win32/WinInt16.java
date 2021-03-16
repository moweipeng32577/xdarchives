package com.xdtech.project.lot.mjj.message.type.win32;

public class WinInt16 extends WinInt {

    public WinInt16() {
        this(0);
    }

    public WinInt16(int i) {
        super(i);
    }

    public WinInt16(byte[] value) {
        super(value);
    }

    public WinInt16(String value) {
        super(value);
    }

    @Override
    public int sizeof() {
        return 2;
    }

}
