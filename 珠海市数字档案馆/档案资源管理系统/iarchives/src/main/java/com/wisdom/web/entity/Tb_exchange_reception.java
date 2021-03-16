package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by yl on 2017/11/3.
 * 数据接收
 */
@Entity
public class Tb_exchange_reception {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String exchangeid;
    @Column(columnDefinition = "varchar(50)")
    private String filename;
    @Column(columnDefinition = "char(32)")
    private String filemd5;
    //@Column(columnDefinition = "longblob")//mysql
    //@Column(columnDefinition = "blob")//oracle
    private byte[] filedata;
    @Column(columnDefinition = "integer")
    private Long filesize;
    @Column(columnDefinition = "varchar(20)")
    private String filetime;

    @Transient
    private String kbSize;

    public Tb_exchange_reception() {
    }

    public Tb_exchange_reception(String exchangeid, String filename, String filemd5, Long filesize) {
        this.exchangeid = exchangeid;
        this.filename = filename;
        this.filemd5 = filemd5;
        this.filesize = filesize;
    }

    public Tb_exchange_reception(String filename, String filemd5, byte[] filedata, Long filesize, String fileTime) {
        this.filename = filename;
        this.filemd5 = filemd5;
        this.filedata = filedata;
        this.filesize = filesize;
        this.filetime = fileTime;
    }

    public String getExchangeid() {
        return exchangeid;
    }

    public void setExchangeid(String exchangeid) {
        this.exchangeid = exchangeid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilemd5() {
        return filemd5;
    }

    public void setFilemd5(String filemd5) {
        this.filemd5 = filemd5;
    }

    public byte[] getFiledata() {
        return filedata;
    }

    public void setFiledata(byte[] filedata) {
        this.filedata = filedata;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public String getKbSize() {
        return bytes2kb(filesize);
    }

    public void setKbSize(String kbSize) {
        this.kbSize = kbSize;
    }

    public String getFiletime() {
        return filetime;
    }

    public void setFiletime(String filetime) {
        this.filetime = filetime;
    }

    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal kilobyte = new BigDecimal(1024);
        float returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return String.valueOf(returnValue);
    }

    @Override
    public String toString() {
        return "Tb_exchange_reception{" +
                "exchangeid='" + exchangeid + '\'' +
                ", filename='" + filename + '\'' +
                ", filemd5='" + filemd5 + '\'' +
                ", filedata=" + Arrays.toString(filedata) +
                ", filesize=" + filesize +
                ", filetime='" + filetime + '\'' +
                ", kbSize='" + kbSize + '\'' +
                '}';
    }
}

