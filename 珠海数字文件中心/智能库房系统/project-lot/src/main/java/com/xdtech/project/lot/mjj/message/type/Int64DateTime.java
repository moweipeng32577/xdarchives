package com.xdtech.project.lot.mjj.message.type;

import java.util.Date;

public class Int64DateTime extends Int64 {

    public Int64DateTime() {
        super();
    }

    public Int64DateTime(int value) {
        super(value);
    }

    public Int64DateTime(long value) {
        load(value);
    }

    public Int64DateTime(byte[] value) {
        super(value);
    }

    public Int64DateTime(String value) {
        super(value);
    }

    public Int64DateTime(Date date) {
        super(date.getTime());
    }

    public Date toDateTime() {
        Date date = new Date();
        date.setTime(toLong());

        return date;
    }

}
