package com.xdtech.project.lot.mjj.message.data;


import com.xdtech.project.lot.mjj.message.MJJMessageData;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.type.Type;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MJJOpenData extends MJJMessageData {

    public Int8 direction;//资料存放方向 0x01左,0x02右

    public Int8 layer; //层

    public Int8 section;//节

    @Override
    public void load(byte[] data) {
        int offset = 0;

        direction = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += direction.sizeof();

        layer = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += layer.sizeof();

        section = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += section.sizeof();
    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (direction == null) {
                direction = new Int8();
            }
            output.write(direction.toBytes());

            if (layer == null) {
                layer = new Int8();
            }
            output.write(layer.toBytes());

            if (section == null) {
                section = new Int8();
            }
            output.write(section.toBytes());

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
