package com.xdtech.project.lot.mjj.message.data.wkl;



import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.type.Type;
import com.xdtech.project.lot.mjj.message.wkl.WKLMJJMessageData;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@CmdKey(0x81)
public class WKLMJJCloseResultData extends WKLMJJMessageData {

    public Int8 RESULT;

    @Override
    public Int8 getCmdKey() {
        return new Int8(0x81);
    }

    @Override
    public void load(byte[] data) {
        int offset = 0;

        RESULT = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += RESULT.sizeof();
    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (RESULT == null) {
                RESULT = new Int8();
            }
            output.write(RESULT.toBytes());

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
