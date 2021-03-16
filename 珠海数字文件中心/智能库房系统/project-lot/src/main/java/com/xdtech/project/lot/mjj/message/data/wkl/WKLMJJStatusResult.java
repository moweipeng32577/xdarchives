package com.xdtech.project.lot.mjj.message.data.wkl;


import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.wkl.WKLMJJMessageData;

@CmdKey(0x84)
public class WKLMJJStatusResult extends WKLMJJMessageData {
    public Int8[] COLS;//列

    public Int8[] STATUSES;//状态

    @Override
    public void load(byte[] data) {
        if (data == null) {
            data = new byte[0];
        }

        int len = data.length / 2;

        COLS = new Int8[len];

        STATUSES = new Int8[len];

        for (int i = 0; i < len; i++) {
            COLS[i] = new Int8(data[i * 2]);
            STATUSES[i] = new Int8(data[i * 2 + 1]);
        }
    }

    @Override
    public byte[] toBytes() {
        if (COLS == null) {
            COLS = new Int8[0];
        }
        if (STATUSES == null) {
            STATUSES = new Int8[0];
        }
        byte[] b = new byte[COLS.length];

        for (int i = 0, j = 0; i < COLS.length; i++, j++) {
            b[j] = COLS[i].toByte();
            b[++j] = STATUSES[i].toByte();
        }
        return b;
    }

}
