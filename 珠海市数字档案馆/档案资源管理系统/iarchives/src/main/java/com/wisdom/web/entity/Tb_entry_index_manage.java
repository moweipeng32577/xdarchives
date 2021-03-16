package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by Administrator on 2019/6/25.
 */
@Entity
public class Tb_entry_index_manage {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String entryid;// 条目主键ID(GUID)
    @Transient
    private String nodeid;// 节点ID(GUID)
    @ManyToOne
    @JoinColumn(name = "nodeid", columnDefinition = "char(36)")
    private Tb_data_node tdn;
    @Transient
    private String nodefullname;
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String eleid;
    @Column(columnDefinition = "varchar(1000)")
    private String title;// 题名
    @Column(columnDefinition = "varchar(200)")
    private String filenumber;// 文件编号
    @Column(columnDefinition = "varchar(100)")
    private String archivecode;// 档号
    @Column(columnDefinition = "varchar(20)")
    private String funds;// 全宗号
    @Column(columnDefinition = "varchar(30)")
    private String catalog;// 目录号、类别号
    @Column(columnDefinition = "varchar(10)")
    private String filecode;// 案卷号
    @Column(columnDefinition = "varchar(10)")
    private String innerfile;// 卷内顺序号
    @Column(columnDefinition = "varchar(8)")
    private String filingyear;// 归档年度
    @Column(columnDefinition = "varchar(200)")
    private String keyword;// 主题词
    @Column(columnDefinition = "varchar(15)")
    private String entryretention;// 保管期限
    @Column(columnDefinition = "varchar(100)")
    private String organ;// 机构/问题
    @Column(columnDefinition = "varchar(10)")
    private String recordcode;// 件号
    @Column(columnDefinition = "varchar(10)")
    private String entrysecurity;// 密级
    @Column(columnDefinition = "varchar(10)")
    private String pages;// 页数
    @Column(columnDefinition = "varchar(15)")
    private String pageno;// 页号
    @Column(columnDefinition = "varchar(20)")
    private String filedate;// 文件日期
    @Column(columnDefinition = "varchar(100)")
    private String responsible;// 责任者
    @Column(columnDefinition = "varchar(20)")
    private String serial;// 文件流水号
    @Column(columnDefinition = "varchar(20)")
    private String flagopen;// 开放状态
    @Column(columnDefinition = "varchar(100)")
    private String entrystorage;// 存储位置
    @Column(columnDefinition = "varchar(30)")
    private String descriptiondate;// 著录时间
    @Column(columnDefinition = "varchar(30)")
    private String descriptionuser;// 著录用户
    @Column(columnDefinition = "varchar(10)")
    private String fscount;// 份数
    @Column(columnDefinition = "varchar(10)")
    private String kccount;// 库存份数
    @Column(columnDefinition = "varchar(30)")
    private String opendate;// 开放时间
    @Column(columnDefinition = "varchar(10)")
    private String sparefield1;
    @Column(columnDefinition = "varchar(10)")
    private String sparefield2;
    @Column(columnDefinition = "varchar(10)")
    private String sparefield3;
    @Column(columnDefinition = "varchar(10)")
    private String sparefield4;
    @Column(columnDefinition = "varchar(10)")
    private String sparefield5;

    public String getNodefullname() {
        return nodefullname;
    }

    public void setNodefullname(String nodefullname) {
        this.nodefullname = nodefullname;
    }

    public String getFscount() {
        return fscount == null ? null : fscount.trim();
    }

    public String getKccount() {
        return kccount == null ? null : kccount.trim();
    }

    public void setKccount(String kccount) {
        this.kccount = kccount;
    }

    public void setFscount(String fscount) {
        this.fscount = fscount;
    }

    public String getEntryid() {
        return entryid == null ? null : entryid.trim();
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getEleid() {
        return eleid;
    }

    public void setEleid(String eleid) {
        this.eleid = eleid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilenumber() {
        return filenumber;
    }

    public void setFilenumber(String number) {
        this.filenumber = number;
    }

    public String getArchivecode() {
        return archivecode;
    }

    public void setArchivecode(String archivecode) {
        this.archivecode = archivecode;
    }

    public String getFunds() {
        return funds;
    }

    public void setFunds(String funds) {
        this.funds = funds;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getFilecode() {
        return filecode;
    }

    public void setFilecode(String filecode) {
        this.filecode = filecode;
    }

    public String getInnerfile() {
        return innerfile;
    }

    public void setInnerfile(String innerfile) {
        this.innerfile = innerfile;
    }

    public String getFilingyear() {
        return filingyear;
    }

    public void setFilingyear(String filingyear) {
        this.filingyear = filingyear;
    }

    public String getEntryretention() {
        return entryretention;
    }

    public void setEntryretention(String entryretention) {
        this.entryretention = entryretention;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getRecordcode() {
        return recordcode;
    }

    public void setRecordcode(String recordcode) {
        this.recordcode = recordcode;
    }

    public String getEntrysecurity() {
        return entrysecurity;
    }

    public void setEntrysecurity(String entrysecurity) {
        this.entrysecurity = entrysecurity;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPageno() {
        return pageno;
    }

    public void setPageno(String pageno) {
        this.pageno = pageno;
    }

    public String getFiledate() {
        return filedate;
    }

    public void setFiledate(String filedate) {
        this.filedate = filedate;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getFlagopen() {
        return flagopen;
    }

    public void setFlagopen(String flagopen) {
        this.flagopen = flagopen;
    }

    public String getEntrystorage() {
        return entrystorage;
    }

    public void setEntrystorage(String entrystorage) {
        this.entrystorage = entrystorage;
    }

    public String getDescriptiondate() {
        return descriptiondate;
    }

    public void setDescriptiondate(String descriptiondate) {
        this.descriptiondate = descriptiondate;
    }

    public String getDescriptionuser() {
        return descriptionuser;
    }

    public void setDescriptionuser(String descriptionuser) {
        this.descriptionuser = descriptionuser;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getOpendate() {
        return opendate;
    }

    public void setOpendate(String opendate) {
        this.opendate = opendate;
    }

    public String getSparefield1() {
        return sparefield1;
    }

    public void setSparefield1(String sparefield1) {
        this.sparefield1 = sparefield1;
    }

    public String getSparefield2() {
        return sparefield2;
    }

    public void setSparefield2(String sparefield2) {
        this.sparefield2 = sparefield2;
    }

    public String getSparefield3() {
        return sparefield3;
    }

    public void setSparefield3(String sparefield3) {
        this.sparefield3 = sparefield3;
    }

    public String getSparefield4() {
        return sparefield4;
    }

    public void setSparefield4(String sparefield4) {
        this.sparefield4 = sparefield4;
    }

    public String getSparefield5() {
        return sparefield5;
    }

    public void setSparefield5(String sparefield5) {
        this.sparefield5 = sparefield5;
    }

    public String getNodeid() {
        if (this.getTdn() != null) {
            return this.getTdn().getNodeid();
        }
        return this.nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
        Tb_data_node node = new Tb_data_node();
        node.setNodeid(nodeid);
        this.setTdn(node);
    }

    public Tb_data_node getTdn() {
        return tdn;
    }
    public void setTdn(Tb_data_node tdn) {
        this.tdn = tdn;
    }
}
