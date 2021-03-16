package com.wisdom.web.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by Administrator on 2019/6/26.
 */
@Entity
@Table(name = "v_index_detail_manage")
public class Tb_index_detail_manage {

    @Id
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String entryid;//条目主键ID(GUID)
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String nodeid;//节点ID(GUID)
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String eleid;
    @Column(columnDefinition = "varchar(1000)")
    private String title;//题名
    @Column(columnDefinition = "varchar(200)")
    private String filenumber;//文件编号
    @Column(columnDefinition = "varchar(100)")
    private String archivecode;//档号
    @Column(columnDefinition = "varchar(20)")
    private String funds;//全宗号
    @Column(columnDefinition = "varchar(30)")
    private String catalog;//目录号、类别号
    @Column(columnDefinition = "varchar(10)")
    private String filecode;//案卷号
    @Column(columnDefinition = "varchar(10)")
    private String innerfile;//卷内顺序号
    @Column(columnDefinition = "varchar(8)")
    private String filingyear;//归档年度
    @Column(columnDefinition = "varchar(200)")
    private String keyword;//主题词
    @Column(columnDefinition = "varchar(15)")
    private String entryretention;//保管期限
    @Column(columnDefinition = "varchar(100)")
    private String organ;//机构/问题
    @Column(columnDefinition = "varchar(10)")
    private String recordcode;//件号
    @Column(columnDefinition = "varchar(10)")
    private String entrysecurity;//密级
    @Column(columnDefinition = "varchar(10)")
    private String pages;//页数
    @Column(columnDefinition = "varchar(15)")
    private String pageno;//页号
    @Column(columnDefinition = "varchar(20)")
    private String filedate;//文件日期
    @Column(columnDefinition = "varchar(100)")
    private String responsible;//责任者
    @Column(columnDefinition = "varchar(20)")
    private String serial;//文件流水号
    @Column(columnDefinition = "varchar(20)")
    private String flagopen;//开放状态
    @Column(columnDefinition = "varchar(100)")
    private String entrystorage;//存储位置
    @Column(columnDefinition = "varchar(30)")
    private String descriptiondate;//著录时间
    @Column(columnDefinition = "varchar(30)")
    private String descriptionuser;//著录用户
    @Column(columnDefinition = "varchar(10)")
    private String fscount;//份数
    @Column(columnDefinition = "varchar(10)")
    private String kccount;//库存份数
    @Column(columnDefinition = "varchar(30)")
    private String opendate;//开放时间
    @Transient
    private String uniquetag;
    @Transient
    private Tb_data_node tdn;
    @Transient
    private String nodefullname;
    @Column(columnDefinition = "varchar(250)")
    private String f01;
    @Column(columnDefinition = "varchar(400)")
    private String f02;
    @Column(columnDefinition = "varchar(20)")
    private String f03;
    @Column(columnDefinition = "varchar(100)")
    private String f04;
    @Column(columnDefinition = "varchar(250)")
    private String f05;
    @Column(columnDefinition = "varchar(250)")
    private String f06;
    @Column(columnDefinition = "varchar(100)")
    private String f07;
    @Column(columnDefinition = "varchar(100)")
    private String f08;
    @Column(columnDefinition = "varchar(20)")
    private String f09;
    @Column(columnDefinition = "varchar(20)")
    private String f10;
    @Column(columnDefinition = "varchar(20)")
    private String f11;
    @Column(columnDefinition = "varchar(20)")
    private String f12;
    @Column(columnDefinition = "varchar(20)")
    private String f13;
    @Column(columnDefinition = "varchar(20)")
    private String f14;
    @Column(columnDefinition = "varchar(20)")
    private String f15;
    @Column(columnDefinition = "varchar(40)")
    private String f16;
    @Column(columnDefinition = "varchar(40)")
    private String f17;
    @Column(columnDefinition = "varchar(40)")
    private String f18;
    @Column(columnDefinition = "varchar(40)")
    private String f19;
    @Column(columnDefinition = "varchar(40)")
    private String f20;
    @Column(columnDefinition = "varchar(40)")
    private String f21;
    @Column(columnDefinition = "varchar(40)")
    private String f22;
    @Column(columnDefinition = "varchar(40)")
    private String f23;
    @Column(columnDefinition = "varchar(40)")
    private String f24;
    @Column(columnDefinition = "varchar(40)")
    private String f25;
    @Column(columnDefinition = "varchar(40)")
    private String f26;
    @Column(columnDefinition = "varchar(40)")
    private String f27;
    @Column(columnDefinition = "varchar(40)")
    private String f28;
    @Column(columnDefinition = "varchar(40)")
    private String f29;
    @Column(columnDefinition = "varchar(40)")
    private String f30;
    @Column(columnDefinition = "varchar(40)")
    private String f31;
    @Column(columnDefinition = "varchar(40)")
    private String f32;
    @Column(columnDefinition = "varchar(40)")
    private String f33;
    @Column(columnDefinition = "varchar(40)")
    private String f34;
    @Column(columnDefinition = "varchar(40)")
    private String f35;
    @Column(columnDefinition = "varchar(100)")
    private String f36;
    @Column(columnDefinition = "varchar(100)")
    private String f37;
    @Column(columnDefinition = "varchar(100)")
    private String f38;
    @Column(columnDefinition = "varchar(100)")
    private String f39;
    @Column(columnDefinition = "varchar(100)")
    private String f40;
    @Column(columnDefinition = "varchar(100)")
    private String f41;
    @Column(columnDefinition = "varchar(100)")
    private String f42;
    @Column(columnDefinition = "varchar(100)")
    private String f43;
    @Column(columnDefinition = "varchar(100)")
    private String f44;
    @Column(columnDefinition = "varchar(100)")
    private String f45;
    @Column(columnDefinition = "varchar(100)")
    private String f46;
    @Column(columnDefinition = "varchar(100)")
    private String f47;
    @Column(columnDefinition = "varchar(100)")
    private String f48;
    @Column(columnDefinition = "varchar(100)")
    private String f49;
    @Column(columnDefinition = "varchar(100)")
    private String f50;
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


    public Tb_data_node getTdn() {
        return tdn;
    }

    public void setTdn(Tb_data_node tdn) {
        this.tdn = tdn;
    }
    public String getEntryid() {
        return entryid == null ? null : entryid.trim();
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getNodeid() {
        return nodeid.trim();
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
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

    public void setFilenumber(String filenumber) {
        this.filenumber = filenumber;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public String getFscount() {
        return fscount;
    }

    public void setFscount(String fscount) {
        this.fscount = fscount;
    }

    public String getKccount() {
        return kccount;
    }

    public void setKccount(String kccount) {
        this.kccount = kccount;
    }

    public String getOpendate() {
        return opendate;
    }

    public void setOpendate(String opendate) {
        this.opendate = opendate;
    }

    public String getF01() {
        return f01;
    }

    public void setF01(String f01) {
        this.f01 = f01;
    }

    public String getF02() {
        return f02;
    }

    public void setF02(String f02) {
        this.f02 = f02;
    }

    public String getF03() {
        return f03;
    }

    public void setF03(String f03) {
        this.f03 = f03;
    }

    public String getF04() {
        return f04;
    }

    public void setF04(String f04) {
        this.f04 = f04;
    }

    public String getF05() {
        return f05;
    }

    public void setF05(String f05) {
        this.f05 = f05;
    }

    public String getF06() {
        return f06;
    }

    public void setF06(String f06) {
        this.f06 = f06;
    }

    public String getF07() {
        return f07;
    }

    public void setF07(String f07) {
        this.f07 = f07;
    }

    public String getF08() {
        return f08;
    }

    public void setF08(String f08) {
        this.f08 = f08;
    }

    public String getF09() {
        return f09;
    }

    public void setF09(String f09) {
        this.f09 = f09;
    }

    public String getF10() {
        return f10;
    }

    public void setF10(String f10) {
        this.f10 = f10;
    }

    public String getF11() {
        return f11;
    }

    public void setF11(String f11) {
        this.f11 = f11;
    }

    public String getF12() {
        return f12;
    }

    public void setF12(String f12) {
        this.f12 = f12;
    }

    public String getF13() {
        return f13;
    }

    public void setF13(String f13) {
        this.f13 = f13;
    }

    public String getF14() {
        return f14;
    }

    public void setF14(String f14) {
        this.f14 = f14;
    }

    public String getF15() {
        return f15;
    }

    public void setF15(String f15) {
        this.f15 = f15;
    }

    public String getF16() {
        return f16;
    }

    public void setF16(String f16) {
        this.f16 = f16;
    }

    public String getF17() {
        return f17;
    }

    public void setF17(String f17) {
        this.f17 = f17;
    }

    public String getF18() {
        return f18;
    }

    public void setF18(String f18) {
        this.f18 = f18;
    }

    public String getF19() {
        return f19;
    }

    public void setF19(String f19) {
        this.f19 = f19;
    }

    public String getF20() {
        return f20;
    }

    public void setF20(String f20) {
        this.f20 = f20;
    }

    public String getF21() {
        return f21;
    }

    public void setF21(String f21) {
        this.f21 = f21;
    }

    public String getF22() {
        return f22;
    }

    public void setF22(String f22) {
        this.f22 = f22;
    }

    public String getF23() {
        return f23;
    }

    public void setF23(String f23) {
        this.f23 = f23;
    }

    public String getF24() {
        return f24;
    }

    public void setF24(String f24) {
        this.f24 = f24;
    }

    public String getF25() {
        return f25;
    }

    public void setF25(String f25) {
        this.f25 = f25;
    }

    public String getF26() {
        return f26;
    }

    public void setF26(String f26) {
        this.f26 = f26;
    }

    public String getF27() {
        return f27;
    }

    public void setF27(String f27) {
        this.f27 = f27;
    }

    public String getF28() {
        return f28;
    }

    public void setF28(String f28) {
        this.f28 = f28;
    }

    public String getF29() {
        return f29;
    }

    public void setF29(String f29) {
        this.f29 = f29;
    }

    public String getF30() {
        return f30;
    }

    public void setF30(String f30) {
        this.f30 = f30;
    }

    public String getF31() {
        return f31;
    }

    public void setF31(String f31) {
        this.f31 = f31;
    }

    public String getF32() {
        return f32;
    }

    public void setF32(String f32) {
        this.f32 = f32;
    }

    public String getF33() {
        return f33;
    }

    public void setF33(String f33) {
        this.f33 = f33;
    }

    public String getF34() {
        return f34;
    }

    public void setF34(String f34) {
        this.f34 = f34;
    }

    public String getF35() {
        return f35;
    }

    public void setF35(String f35) {
        this.f35 = f35;
    }

    public String getF36() {
        return f36;
    }

    public void setF36(String f36) {
        this.f36 = f36;
    }

    public String getF37() {
        return f37;
    }

    public void setF37(String f37) {
        this.f37 = f37;
    }

    public String getF38() {
        return f38;
    }

    public void setF38(String f38) {
        this.f38 = f38;
    }

    public String getF39() {
        return f39;
    }

    public void setF39(String f39) {
        this.f39 = f39;
    }

    public String getF40() {
        return f40;
    }

    public void setF40(String f40) {
        this.f40 = f40;
    }

    public String getF41() {
        return f41;
    }

    public void setF41(String f41) {
        this.f41 = f41;
    }

    public String getF42() {
        return f42;
    }

    public void setF42(String f42) {
        this.f42 = f42;
    }

    public String getF43() {
        return f43;
    }

    public void setF43(String f43) {
        this.f43 = f43;
    }

    public String getF44() {
        return f44;
    }

    public void setF44(String f44) {
        this.f44 = f44;
    }

    public String getF45() {
        return f45;
    }

    public void setF45(String f45) {
        this.f45 = f45;
    }

    public String getF46() {
        return f46;
    }

    public void setF46(String f46) {
        this.f46 = f46;
    }

    public String getF47() {
        return f47;
    }

    public void setF47(String f47) {
        this.f47 = f47;
    }

    public String getF48() {
        return f48;
    }

    public void setF48(String f48) {
        this.f48 = f48;
    }

    public String getF49() {
        return f49;
    }

    public void setF49(String f49) {
        this.f49 = f49;
    }

    public String getF50() {
        return f50;
    }

    public void setF50(String f50) {
        this.f50 = f50;
    }

    public String getUniquetag() {
        return uniquetag;
    }

    public void setUniquetag(String uniquetag) {
        this.uniquetag = uniquetag;
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
}
