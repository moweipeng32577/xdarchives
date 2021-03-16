package com.xdtech.project.lot.mjj.message;

import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.struct.Struct;
import com.xdtech.project.lot.mjj.message.type.Int8;

@CmdKey(0x00)
public class MJJMessageData extends Struct {

    byte[] BYTES;

    @Override
    public void load(byte[] data) {
        this.BYTES = data;
    }

    @Override
    public byte[] toBytes() {
        return BYTES;
    }

    public Int8 getCmdKey() {
        return new Int8(getClass().getAnnotation(CmdKey.class).value());
    }

}
