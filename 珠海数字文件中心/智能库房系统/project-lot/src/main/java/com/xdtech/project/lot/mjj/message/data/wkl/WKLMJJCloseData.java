package com.xdtech.project.lot.mjj.message.data.wkl;



import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.type.Type;
import com.xdtech.project.lot.mjj.message.wkl.WKLMJJMessageData;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@CmdKey(0x0102)
public class WKLMJJCloseData extends WKLMJJMessageData {

    public Int8 COL;//移动列

    public Int8 IPOS;//左/右

    @Override
    public void load(byte[] data) {
        int offset = 0;

        COL = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += COL.sizeof();

        IPOS = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += IPOS.sizeof();
    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            if (COL == null) {
                COL = new Int8();
            }
            output.write(COL.toBytes());

            if (IPOS == null) {
                IPOS = new Int8();
            }
            output.write(IPOS.toBytes());

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Int8 getCmdKey() {
        return new Int8(0x01);
    }
}
