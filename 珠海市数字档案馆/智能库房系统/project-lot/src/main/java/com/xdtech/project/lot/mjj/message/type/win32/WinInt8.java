package com.xdtech.project.lot.mjj.message.type.win32;

public class WinInt8 extends WinInt {

    public WinInt8() {
        this(0);
    }

    public WinInt8(int i) {
        super(i);
    }

    public WinInt8(byte[] value) {
        super(value);
    }

    public WinInt8(String value) {
        super(value);
    }

    @Override
    public int sizeof() {
        return 1;
    }

}
