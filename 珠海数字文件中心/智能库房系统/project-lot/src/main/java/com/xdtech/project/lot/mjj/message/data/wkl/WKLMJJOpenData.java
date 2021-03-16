package com.xdtech.project.lot.mjj.message.data.wkl;




import com.xdtech.project.lot.mjj.message.annotation.CmdKey;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.type.Type;
import com.xdtech.project.lot.mjj.message.wkl.WKLMJJMessageData;
import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@CmdKey(0x0101)
public class WKLMJJOpenData extends WKLMJJMessageData {

    public Int8 COL;//密集架移动列号

    public Int8 IPOS;//左\右

    public Int8 CELL_ROW;//单元格行

    public Int8 CELL_COL;//单元格列

    @Override
    public void load(byte[] data) {
        int offset = 0;

        COL = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += COL.sizeof();

        IPOS = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += IPOS.sizeof();

        CELL_ROW = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += CELL_ROW.sizeof();

        CELL_COL = new Int8(ArrayUtils.subarray(data, offset, offset + Type.SIZEOF(Int8.class)));
        offset += CELL_COL.sizeof();
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

            if (CELL_ROW == null) {
                CELL_ROW = new Int8();
            }
            output.write(CELL_ROW.toBytes());

            if (CELL_COL == null) {
                CELL_COL = new Int8();
            }
            output.write(CELL_COL.toBytes());

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
