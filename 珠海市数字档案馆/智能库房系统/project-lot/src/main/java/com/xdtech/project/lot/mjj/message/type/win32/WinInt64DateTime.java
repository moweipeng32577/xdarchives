package com.xdtech.project.lot.mjj.message.type.win32;

import java.util.Date;

public class WinInt64DateTime extends WinInt64 {

    public WinInt64DateTime() {
        super(new Date().getTime());
    }

    public WinInt64DateTime(int value) {
        super(value);
    }

    public WinInt64DateTime(long value) {
        load(value);
    }

    public WinInt64DateTime(byte[] value) {
        super(value);
    }

    public WinInt64DateTime(String value) {
        super(value);
    }

    public WinInt64DateTime(Date date) {
        super(date.getTime());
    }

    public Date toDateTime() {
        Date date = new Date();
        date.setTime(toLong());

        return date;
    }

}
