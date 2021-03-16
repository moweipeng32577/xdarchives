package com.xdtech.project.lot.mjj.message.data;

import com.xdtech.project.lot.mjj.message.MJJMessageData;
import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.type.Int16;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.type.Type;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@CmdKey(0x8B)
public class MJJMoveResultData extends MJJMessageData {

    public Int8 STATUS;

    @Override
    public void load(byte[] data) {
        if (data == null) {
            data = new byte[0];
        }
        int offset = 0;
        STATUS = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += STATUS.sizeof();
    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (STATUS == null) {
                STATUS = new Int8();
            }
            output.write(STATUS.toBytes());

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
