package com.xdtech.project.lot.mjj.message.wkl;



import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.struct.Struct;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.type.Type;
import com.xdtech.project.lot.mjj.message.util.ClassUtils;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class WKLMJJMessageContent extends Struct {

    private static final Map<Integer, Class<WKLMJJMessageData>> datas = new HashMap<Integer, Class<WKLMJJMessageData>>();

    static {
        try {
            List<Class> list = ClassUtils.getClassesFromPackage("com.titansoft.znkg.dcp.mjj.message.data.wkl");
            for (Class clazz : list) {
                CmdKey key = (CmdKey) clazz.getAnnotation(CmdKey.class);
                if (key != null) {
                    datas.put(key.value(), clazz);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public Int8 CMD_KEY;//命令

    public Int8 CONTENT_LEN;//数据长度

//	public Int8 CODE;
//	
//	public Int8 COLS;

    public WKLMJJMessageData DATA;//数据

    @Override
    public void load(byte[] data) {
        int offset = 0;

        CMD_KEY = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += CMD_KEY.sizeof();

        CONTENT_LEN = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += CONTENT_LEN.sizeof();

        int cmd_key = CMD_KEY.toInt();

        int data_len = CONTENT_LEN.toInt();

        if (cmd_key == 0x01 && data_len != 0) {

            if (CONTENT_LEN.toInt() == 4) {//控制命令
                CMD_KEY = new Int8(0x0101);

            } else if (CONTENT_LEN.toInt() == 2) {//复位命令

                CMD_KEY = new Int8(0x0102);
            }
        } else if (cmd_key == 0x81) {//控制回复或者复位回复0x8101 0x8102

        }
        try {
            DATA = datas.get(CMD_KEY.toInt()).newInstance();
        } catch (Exception e) {
            DATA = new WKLMJJMessageData();
        } finally {
            DATA.load(ArrayUtils.subarray(data, offset, offset + (CONTENT_LEN.toInt())));
        }
    }

    @Override
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            if (DATA == null) {
                DATA = new WKLMJJMessageData();
            }
            byte[] data = DATA.toBytes();

            if (CMD_KEY == null) {
                CMD_KEY = new Int8();
            }
            output.write(CMD_KEY.toBytes());

            byte len = 0;
            if (data != null && data.length > 0) {
                if (CONTENT_LEN == null) {
                    CONTENT_LEN = new Int8();
                }
                output.write(CONTENT_LEN.toBytes());
                output.write(data);
                CONTENT_LEN = new Int8(data.length);

                len = CONTENT_LEN.toByte();
            }

            byte[] b = output.toByteArray();

            output.close();

            if (data != null && data.length > 0) {
                b[1] = len;
            }

            b[0] = DATA.getCmdKey().toByte();
            return b;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
