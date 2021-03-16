package com.xdtech.project.lot.mjj.message.type;

public class BitStream extends Type {

    private int size = 0;

    public BitStream(byte b) {
        this(new byte[]{b});
    }

    public BitStream(byte[] bytes) {
        load(bytes);
    }

    public void load(byte[] bytes) {
        this.size = bytes.length;
        this.value = bytes;
    }

    public boolean getBitAsBoolean(int bit) {
        return getBitAsInt(bit) == 1;
    }

    public int getBitAsInt(int bit) {
        byte b = 0x00;

        if (bit < 8) {
            b = this.value[0];
        } else {
            b = this.value[bit / 8];
        }

        int offset = 7 - (bit % 8);
        return (b >> offset) & 0x1;
    }

    @Override
    public int sizeof() {
        return this.size;
    }

}
