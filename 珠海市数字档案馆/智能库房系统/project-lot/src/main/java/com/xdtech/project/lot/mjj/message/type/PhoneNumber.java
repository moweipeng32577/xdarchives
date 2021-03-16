package com.xdtech.project.lot.mjj.message.type;

import org.apache.commons.lang.StringUtils;

public class PhoneNumber extends Type {

    protected byte[] value = new byte[sizeof()];

    public PhoneNumber() {
        this(new byte[16]);
    }

    public PhoneNumber(String value) {
        if (StringUtils.isBlank(value)) {
            load(new byte[sizeof()]);
        } else {
            load(value.getBytes());
        }


    }

    public PhoneNumber(byte[] value) {
        load(value);
    }

    @Override
    public int sizeof() {
        return 16;
    }

    @Override
    public String toString() {
        return new String(toBytes());
    }

}
