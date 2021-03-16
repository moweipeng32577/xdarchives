package com.xdtech.project.lot.mjj.message.data;

import com.xdtech.project.lot.mjj.message.MJJMessageData;
import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.type.Type;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author wujy
 */
@CmdKey(0x8F)
public class MJJVentilateResultData extends MJJMessageData {
    public Int8 IPOS;

    @Override
    public void load(byte[] data) {
        int offset = 0;

        IPOS = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += IPOS.sizeof();
    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (IPOS == null) {
                IPOS = new Int8();
            }
            output.write(IPOS.toBytes());

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
