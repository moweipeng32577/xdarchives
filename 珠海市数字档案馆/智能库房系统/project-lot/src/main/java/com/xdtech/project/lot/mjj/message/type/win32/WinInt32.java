package com.xdtech.project.lot.mjj.message.type.win32;

public class WinInt32 extends WinInt {

    public WinInt32() {
        this(0);
    }

    public WinInt32(int i) {
        super(i);
    }

    public WinInt32(byte[] value) {
        super(value);
    }

    public WinInt32(String value) {
        super(value);
    }

    @Override
    public int sizeof() {
        return 4;
    }

}
