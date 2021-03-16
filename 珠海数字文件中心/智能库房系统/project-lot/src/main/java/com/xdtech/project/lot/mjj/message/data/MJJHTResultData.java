package com.xdtech.project.lot.mjj.message.data;


import com.xdtech.project.lot.mjj.message.MJJMessageData;
import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.type.Int16;
import com.xdtech.project.lot.mjj.message.type.Type;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@CmdKey(0x0E)
public class MJJHTResultData extends MJJMessageData {

    public Int16 HUMI;

    public Int16 TEMP;

    @Override
    public void load(byte[] data) {
        int offset = 0;

        HUMI = new Int16(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int16.class)));
        offset += HUMI.sizeof();

        TEMP = new Int16(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int16.class)));
        offset += TEMP.sizeof();

    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (HUMI == null) {
                HUMI = new Int16();
            }
            output.write(HUMI.toBytes());

            if (TEMP == null) {
                TEMP = new Int16();
            }
            output.write(TEMP.toBytes());

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
