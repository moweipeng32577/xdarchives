package com.wisdom.secondaryDataSource.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "tb_entry_index")
public class Tb_entry_index_sx {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String entryid;// 条目主键ID(GUID)
    @Transient
    private String nodeid;// 节点ID(GUID)
    @ManyToOne
    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name = "nodeid", columnDefinition = "char(36)")
    private Tb_data_node_sx tdn;
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
    @Column(columnDefinition = "varchar(100)")
    private String author;// 拍录作者
    @Column(columnDefinition = "varchar(500)")
    private String address;// 地点
    @Column(columnDefinition = "varchar(30)")
    private String theme;// 主题
//    @Column(columnDefinition = "varchar(30)")
//    private String source;// 来源
//    @Column(columnDefinition = "varchar(20)")
//    private String entrytype;// 类别
//    @Type(type = "com.wisdom.util.OracleCharIDType")
//    @Column(columnDefinition = "char(36)")
//    private String docgroupid;
//    @Column(columnDefinition = "varchar(4)")
//    private String isprint;// 是否冲印
//    private Integer sorting;//排序(与件号值保持一致的数值类型)
//    @Column(columnDefinition = "varchar(100)")
//    private String classificationcode;
//    @Column(columnDefinition = "varchar(20)")
//    private String appraisaltype;  //鉴定类型

//    public String getAppraisaltype() {
//        return appraisaltype;
//    }
//
//    public void setAppraisaltype(String appraisaltype) {
//        this.appraisaltype = appraisaltype;
//    }

//    public String getClassificationcode() {
//        return classificationcode;
//    }
//
//    public void setClassificationcode(String classificationcode) {
//        this.classificationcode = classificationcode;
//    }

    public Tb_data_node_sx getTdn() {
        return tdn;
    }

    public void setTdn(Tb_data_node_sx tdn) {
        this.tdn = tdn;
    }

    public String getNodefullname() {
        return nodefullname;
    }

    public void setNodefullname(String nodefullname) {
        this.nodefullname = nodefullname;
    }

    public String getFscount() {
        return fscount;
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

    public String getNodeid() {
        if (this.getTdn() != null) {
            return this.getTdn().getNodeid();
        }
        return this.nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
        // if(this.getTdn() == null){
        Tb_data_node_sx node = new Tb_data_node_sx();
        node.setNodeid(nodeid);
        this.setTdn(node);
        // }
        // this.tdn.setNodeid(nodeid);
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

//    public String getDocgroupid() {
//        return docgroupid;
//    }
//
//    public void setDocgroupid(String docgroupid) {
//        this.docgroupid = docgroupid;
//    }
//
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
//
//    public String getSource() {
//        return source;
//    }
//
//    public void setSource(String source) {
//        this.source = source;
//    }
//
//    public String getEntrytype() {
//        return entrytype;
//    }
//
//    public void setEntrytype(String entrytype) {
//        this.entrytype = entrytype;
//    }

    @JoinColumn(name = "entryid")
    @OneToOne
    private Tb_entry_detail_sx tb_entry_detail;

    public Tb_entry_detail_sx getTb_entry_detail() {
        return tb_entry_detail;
    }

    public void setTb_entry_detail(Tb_entry_detail_sx tb_entry_detail) {
        this.tb_entry_detail = tb_entry_detail;
    }

//    public Integer getSorting() {
//        return sorting;
//    }
//
//    public void setSorting(Integer sorting) {
//        this.sorting = sorting;
//    }

//    public String getIsprint() {
//        return isprint;
//    }
//
//    public void setIsprint(String isprint) {
//        this.isprint = isprint;
//    }

    @Override
    public String toString() {
        return "Tb_entry_index [entryid=" + entryid + ", nodeid=" + nodeid + ", tdn=" + tdn + ", nodefullname="
                + nodefullname + ", eleid=" + eleid + ", title=" + title + ", filenumber=" + filenumber
                + ", archivecode=" + archivecode + ", funds=" + funds + ", catalog=" + catalog + ", filecode="
                + filecode + ", innerfile=" + innerfile + ", filingyear=" + filingyear + ", keyword=" + keyword
                + ", entryretention=" + entryretention + ", organ=" + organ + ", recordcode=" + recordcode
                + ", entrysecurity=" + entrysecurity + ", pages=" + pages + ", pageno=" + pageno + ", filedate="
                + filedate + ", responsible=" + responsible + ", serial=" + serial + ", flagopen=" + flagopen
                + ", entrystorage=" + entrystorage + ", descriptiondate=" + descriptiondate + ", descriptionuser="
                + descriptionuser + ", fscount=" + fscount + ", kccount=" + kccount + ", opendate=" + opendate + "]";
    }

    public String toFieldnameString() {
        return "entryid,nodeid,eleid,title,keyword,filenumber,archivecode,funds,catalog,filecode,innerfile,filingyear,entryretention,organ,recordcode,"
                + "entrysecurity,pages,pageno,filedate,responsible,serial,flagopen,entrystorage,descriptiondate,descriptionuser,fscount,kccount,opendate,"
                + "author,address,theme,source,entrytype,docgroupid,isprint";
    }
}

