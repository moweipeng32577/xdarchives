package com.xdtech.project.lot.mjj.message;

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

public class MJJMessageContent extends Struct {
    private static final Map<Integer, Class<MJJMessageData>> datas = new HashMap<Integer, Class<MJJMessageData>>();
    static {
        try {
            List<Class> list = ClassUtils.getClassesFromPackage("com.xdtech.project.lot.mjj.message.data");
            for(Class clazz:list){
                CmdKey key = (CmdKey) clazz.getAnnotation(CmdKey.class);
                if(key != null){
                    datas.put(key.value(), clazz);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public Int8 CONTENT_LEN;//长度

    public Int8 MSG_TYPE;//帧类型

    public Int8 CODE;//库号

    public Int8 COL;//列号

    public Int8 CMD_KEY;//命令

    public MJJMessageData DATA;//密集架协议数据

    @Override
    public void load(byte[] data) {
        int offset = 0;

        CONTENT_LEN = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += CONTENT_LEN.sizeof();

        MSG_TYPE = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += MSG_TYPE.sizeof();

        CODE = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += CODE.sizeof();

        COL = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += COL.sizeof();

        CMD_KEY = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += CMD_KEY.sizeof();


        try {
            DATA = datas.get(CMD_KEY.toInt()).newInstance();
        } catch (Exception e) {
            DATA = new MJJMessageData();
        } finally {
            if(CONTENT_LEN.toInt() - 5 == 0){
                DATA.load(new byte[0]);
            } else {
                DATA.load(ArrayUtils.subarray(data, offset, offset + (CONTENT_LEN.toInt() - 5)));
            }
        }
    }

    @Override
    public byte[] toBytes() {
        try{
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            if(CONTENT_LEN == null){
                CONTENT_LEN = new Int8();
            }
            output.write(CONTENT_LEN.toBytes());

            if(MSG_TYPE == null){
                MSG_TYPE = new Int8();
            }
            output.write(MSG_TYPE.toBytes());

            if(CODE == null){
                CODE = new Int8();
            }
            output.write(CODE.toBytes());

            if(COL == null){
                COL = new Int8();
            }
            output.write(COL.toBytes());

            if(CMD_KEY == null){
                CMD_KEY = new Int8();
            }
            output.write(CMD_KEY.toBytes());

            if(DATA == null){
                DATA = new MJJMessageData();
            }
            byte[] data = DATA.toBytes();

            if(data != null && data.length > 0){
                output.write(data);
            }

            byte[] b = output.toByteArray();
            CONTENT_LEN = new Int8(b.length);

            byte len = CONTENT_LEN.toByte();
            output.close();

            b[0] = len;
            //b[4] = DATA.getCmdKey().toByte();

            return b;

        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

}
