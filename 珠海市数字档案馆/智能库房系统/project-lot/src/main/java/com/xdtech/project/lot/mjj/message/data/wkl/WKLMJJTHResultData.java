package com.xdtech.project.lot.mjj.message.data.wkl;


import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.type.Int16;
import com.xdtech.project.lot.mjj.message.type.Type;
import com.xdtech.project.lot.mjj.message.wkl.WKLMJJMessageData;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@CmdKey(0x86)
public class WKLMJJTHResultData extends WKLMJJMessageData {

    public Int16 TEMPERATURE;  //温度

    public Int16 HUMIDITY;    //湿度

    @Override
    public void load(byte[] data) {
        int offset = 0;

        TEMPERATURE = new Int16(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int16.class)));
        offset += TEMPERATURE.sizeof();

        HUMIDITY = new Int16(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int16.class)));
        offset += HUMIDITY.sizeof();
    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            if (TEMPERATURE == null) {
                TEMPERATURE = new Int16();
            }
            output.write(TEMPERATURE.toBytes());

            if (HUMIDITY == null) {
                HUMIDITY = new Int16();
            }
            output.write(HUMIDITY.toBytes());

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
