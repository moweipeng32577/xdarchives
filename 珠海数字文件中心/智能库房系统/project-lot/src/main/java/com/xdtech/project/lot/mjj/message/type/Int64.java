package com.xdtech.project.lot.mjj.message.type;

public class Int64 extends Int {

    public Int64() {
        super();
    }

    public Int64(int value) {
        super(value);
    }

    public Int64(long value) {
        load(value);
    }

    public Int64(byte[] value) {
        super(value);
    }

    public Int64(String value) {
        super(value);
    }

    @Override
    public void load(String data) {
        if (data.startsWith("0x")) {
            super.load(data);
        } else if (data.matches("^[-\\+]?[\\d]*$")) {
            load(Long.parseLong(data));
        }
    }

    public void load(long data) {
        byte[] result = new byte[8];

        result[0] = (byte) (data >> 56);
        result[1] = (byte) (data >> 48);
        result[2] = (byte) (data >> 40);
        result[3] = (byte) (data >> 32);
        result[4] = (byte) (data >> 24);
        result[5] = (byte) (data >> 16);
        result[6] = (byte) (data >> 8);
        result[7] = (byte) (data);

        load(result);
    }

    public long toLong() {
        return ((((long) value[0] & 0xff) << 56) | (((long) value[1] & 0xff) << 48)
                | (((long) value[2] & 0xff) << 40) | (((long) value[3] & 0xff) << 32)
                | (((long) value[4] & 0xff) << 24) | (((long) value[5] & 0xff) << 16)
                | (((long) value[6] & 0xff) << 8) | (((long) value[7] & 0xff) << 0));
    }

    @Override
    public int toInt() {
        return (int) toLong();
    }

    @Override
    public int sizeof() {
        return 8;
    }

}
