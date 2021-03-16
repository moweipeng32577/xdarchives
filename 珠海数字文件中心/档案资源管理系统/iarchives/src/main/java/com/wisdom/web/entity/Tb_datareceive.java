package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by yl on 2020/3/17.
 */
@Entity
public class Tb_datareceive {

    public static final String STATE_RECEIVE = "已接收";
    public static final String STATE_UNRECEIVE = "待接收";
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String receiveid;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String nodeid;
    @Column(columnDefinition = "varchar(30)")
    private String state;
    @Column(columnDefinition = "varchar(10)")
    private String type;
    @Column(columnDefinition = "varchar(100)")
    private String currentnode;
    @Column(columnDefinition = "varchar(400)")
    private String filename;
    @Column(columnDefinition = "varchar(800)")
    private String filepath;
    @Column(columnDefinition = "varchar(100)")
    private String transfertitle;    //交接工作名称
    @Column(columnDefinition = "varchar(500)")
    private String transdesc;//内容描述
    @Column(columnDefinition = "varchar(100)")
    private String transuser;//移交人
    @Column(columnDefinition = "varchar(100)")
    private String sequencecode;    //载体起止顺序号
    @Column(columnDefinition = "varchar(10)")
    private String transcount;//移交电子档案数
    @Column(columnDefinition = "varchar(10)")
    private String transfersize;    //移交数据量
    @Column(columnDefinition = "varchar(100)")
    private String transorgan;//移交部门
    @Column(columnDefinition = "varchar(30)")
    private String transdate;//实体移交时间
    @Column(columnDefinition = "varchar(10)")
    private String transferstcount;    //移交载体数量

    public String getReceiveid() {
        return receiveid;
    }

    public void setReceiveid(String receiveid) {
        this.receiveid = receiveid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getCurrentnode() {
        return currentnode;
    }

    public void setCurrentnode(String currentnode) {
        this.currentnode = currentnode;
    }

    public String getTransfertitle() {
        return transfertitle;
    }

    public void setTransfertitle(String transfertitle) {
        this.transfertitle = transfertitle;
    }

    public String getTransdesc() {
        return transdesc;
    }

    public void setTransdesc(String transdesc) {
        this.transdesc = transdesc;
    }

    public String getTransuser() {
        return transuser;
    }

    public void setTransuser(String transuser) {
        this.transuser = transuser;
    }

    public String getSequencecode() {
        return sequencecode;
    }

    public void setSequencecode(String sequencecode) {
        this.sequencecode = sequencecode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTranscount() {
        return transcount;
    }

    public void setTranscount(String transcount) {
        this.transcount = transcount;
    }

    public String getTransfersize() {
        return transfersize;
    }

    public void setTransfersize(String transfersize) {
        this.transfersize = transfersize;
    }

    public String getTransorgan() {
        return transorgan;
    }

    public void setTransorgan(String transorgan) {
        this.transorgan = transorgan;
    }

    public String getTransdate() {
        return transdate;
    }

    public void setTransdate(String transdate) {
        this.transdate = transdate;
    }

    public String getTransferstcount() {
        return transferstcount;
    }

    public void setTransferstcount(String transferstcount) {
        this.transferstcount = transferstcount;
    }
}
