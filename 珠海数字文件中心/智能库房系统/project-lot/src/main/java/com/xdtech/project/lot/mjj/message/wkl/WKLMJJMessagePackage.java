package com.xdtech.project.lot.mjj.message.wkl;



import com.xdtech.project.lot.mjj.message.struct.Struct;
import com.xdtech.project.lot.mjj.message.type.InetAddress32;
import com.xdtech.project.lot.mjj.message.type.Int24;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.type.Type;
import com.xdtech.project.lot.mjj.message.util.CRC8;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WKLMJJMessagePackage extends Struct {

    public Int24 MSG_BEGIN = new Int24(0x685500);//起始码

    public InetAddress32 MJJ_ADDRESS;//固定列地址

    public Int8 COMMAND_BEGIN = new Int8(0x68);//命令起始码

    public WKLMJJMessageContent CONTENT;//内容（命令\长度\数据）

    public Int8 CHECK_VAL;//校验码

    public Int8 MSG_END = new Int8(0x16);//结束码

    @Override
    public void load(byte[] data) {
        int offset = 0;

        MSG_BEGIN = new Int24(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int24.class)));
        offset += MSG_BEGIN.sizeof();

        MJJ_ADDRESS = new InetAddress32(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(InetAddress32.class)));
        offset += MJJ_ADDRESS.sizeof();

        COMMAND_BEGIN = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += COMMAND_BEGIN.sizeof();

        int len = data.length - offset - Type.SIZEOF(Int8.class) - Type.SIZEOF(Int8.class);
        CONTENT = new WKLMJJMessageContent();
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
                MSG_BEGIN = new Int24(0x685500);
            }
            output.write(MSG_BEGIN.toBytes());

            if (MJJ_ADDRESS == null) {
                MJJ_ADDRESS = new InetAddress32();
            }
            output.write(MJJ_ADDRESS.toBytes());

            if (COMMAND_BEGIN == null) {
                COMMAND_BEGIN = new Int8();
            }
            output.write(COMMAND_BEGIN.toBytes());

            if (CONTENT == null) {
                CONTENT = new WKLMJJMessageContent();
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
            CHECK_VAL = new Int8(CRC8.calc(b, b.length - 2));
            b[b.length - 2] = CHECK_VAL.toByte();
            return b;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
