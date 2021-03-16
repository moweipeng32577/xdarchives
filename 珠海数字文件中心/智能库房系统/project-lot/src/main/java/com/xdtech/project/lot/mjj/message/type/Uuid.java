package com.xdtech.project.lot.mjj.message.type;

import org.apache.commons.lang.StringUtils;

public class Uuid extends Type {

    protected byte[] value = new byte[sizeof()];

    public Uuid() {
        this(new byte[32]);
    }

    public Uuid(String value) {
        if (StringUtils.isBlank(value)) {
            load(new byte[sizeof()]);
        } else {
            load(value.getBytes());
        }


    }

    public Uuid(byte[] value) {
        load(value);
    }

    @Override
    public int sizeof() {
        return 32;
    }

    @Override
    public String toString() {
        return new String(toBytes());
    }

}
