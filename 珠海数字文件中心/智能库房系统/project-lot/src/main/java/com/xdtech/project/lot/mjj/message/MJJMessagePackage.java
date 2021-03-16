package com.xdtech.project.lot.mjj.message;

import com.xdtech.project.lot.mjj.message.struct.Struct;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.type.Type;
import com.xdtech.project.lot.mjj.message.util.CRC8;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MJJMessagePackage extends Struct {

    public Int8 MSG_BEGIN = new Int8(0x55);

    public MJJMessageContent CONTENT;

    public Int8 CHECK_VAL;//校验和

    public Int8 MSG_END = new Int8(0x68);

    @Override
    public void load(byte[] data) {
        int offset = 0;

        MSG_BEGIN = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += MSG_BEGIN.sizeof();

        int len = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class))).toInt();
        CONTENT = new MJJMessageContent();
        CONTENT.load(ArrayUtils.subarray(data, offset, offset + len));
        offset += len;

        CHECK_VAL = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += CHECK_VAL.sizeof();

        MSG_END = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += MSG_END.sizeof();

    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            if (MSG_BEGIN == null) {
                MSG_BEGIN = new Int8(0x55);
            }
            output.write(MSG_BEGIN.toBytes());

            if (CONTENT == null) {
                CONTENT = new MJJMessageContent();
            }
            output.write(CONTENT.toBytes());

            if (CHECK_VAL == null) {
                CHECK_VAL = new Int8();
            }
            output.write(CHECK_VAL.toBytes());

            if (MSG_END == null) {
                MSG_END = new Int8(0x68);
            }
            output.write(MSG_END.toBytes());

            byte[] b = output.toByteArray();
            output.close();

            CHECK_VAL = new Int8(CRC8.calc(b, b.length - 2));
            b[b.length - 2] = CHECK_VAL.toByte();

            return b;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
