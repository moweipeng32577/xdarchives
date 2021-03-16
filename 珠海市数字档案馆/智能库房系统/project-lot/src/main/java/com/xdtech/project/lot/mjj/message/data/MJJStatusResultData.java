package com.xdtech.project.lot.mjj.message.data;


import com.xdtech.project.lot.mjj.message.MJJMessageData;
import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.type.Int8;

@CmdKey(0x03)
public class MJJStatusResultData extends MJJMessageData {

    public Int8[] VALUES;

    @Override
    public void load(byte[] data) {
        if (data == null) {
            data = new byte[0];
        }

        VALUES = new Int8[data.length];

        for (int i = 0; i < data.length; i++) {
            VALUES[i] = new Int8(data[i]);
        }
    }

    @Override
    public byte[] toBytes() {
        if (VALUES == null) {
            VALUES = new Int8[0];
        }
        byte[] b = new byte[VALUES.length];

        for (int i = 0; i < VALUES.length; i++) {
            b[i] = VALUES[i].toByte();
        }

        return b;
    }
}
