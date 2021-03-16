package com.wisdom.web.repository;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2020/10/12.
 */
@Entity
public class Tb_supervision_work {


    @Id
    @GenericGenerator(name="idGenerator", strategy="uuid") //生成32位UUID
    @GeneratedValue(generator="idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;
    @Column(columnDefinition = "varchar(10)")
    private String isunitmaterial;  //是否编制本单位的文件材料
    @Column(columnDefinition = "varchar(500)")
    private String fillingname;  //归档范围和档案保管期限表
    @Column(columnDefinition = "varchar(10)")
    private String isontime;  //各门类、载体的文件材料是否按规定及时归档且齐全完整
    @Column(columnDefinition = "varchar(30)")
    private String roomdocfilenum;  //（室藏）档案数[文书档案（件）]
    @Column(columnDefinition = "varchar(30)")
    private String roomdocfilesnum;  //（室藏）档案数[文书档案（卷）]
    @Column(columnDefinition = "varchar(30)")
    private String yeardocnum;  //（本年度）增加数[文书档案]
    @Column(columnDefinition = "varchar(30)")
    private String roombasefilesnum;  //（室藏）档案数[基建档案（卷）]
    @Column(columnDefinition = "varchar(30)")
    private String yearbasenum;  //（本年度）增加数[基建档案]
    @Column(columnDefinition = "varchar(30)")
    private String roomeqfilesnum;  //（室藏）档案数[设备档案（卷）]
    @Column(columnDefinition = "varchar(30)")
    private String yeareqnum;  //（本年度）增加数[设备档案]
    @Column(columnDefinition = "varchar(30)")
    private String roomaccountfilesnum;  //（室藏）档案数[会计档案（卷）]
    @Column(columnDefinition = "varchar(30)")
    private String roomaccountcopiesnum;  //（室藏）档案数[会计档案（册）]
    @Column(columnDefinition = "varchar(30)")
    private String yearaccountnum;  //（本年度）增加数[会计档案]
    @Column(columnDefinition = "varchar(30)")
    private String roomsxfilenum;  //（室藏）档案数[声像档案（件）]
    @Column(columnDefinition = "varchar(30)")
    private String yearsxnum;  //（本年度）增加数[声像档案]
    @Column(columnDefinition = "varchar(30)")
    private String roomphysicalnum;  //（室藏）档案数[实物档案（件）]
    @Column(columnDefinition = "varchar(30)")
    private String yearphysicalnum;  //（本年度）增加数[实物档案]
    @Column(columnDefinition = "varchar(30)")
    private String roomspecialfilenum;  //（室藏）档案数[专门档案（件）]
    @Column(columnDefinition = "varchar(30)")
    private String roomspecialfilesnum;  //（室藏）档案数[专门档案（卷）]
    @Column(columnDefinition = "varchar(30)")
    private String yearspecialnum;  //（本年度）增加数[专门档案]
    @Column(columnDefinition = "varchar(500)")
    private String classplanname;  //档案分类标识方案
    @Column(columnDefinition = "varchar(10)")
    private String isclearup;  //各类档案是否全部整理上架
    @Column(columnDefinition = "varchar(500)")
    private String fundsfiles;  //建立全宗卷
    @Column(columnDefinition = "varchar(500)")
    private String setindex;  //档案存放索引
    @Column(columnDefinition = "varchar(10)")
    private String isroomrecord;  //是否坚持测记库房温度
    @Column(columnDefinition = "varchar(10)")
    private String ischeckrecord;  //是否定期检查档案并记录
    @Column(columnDefinition = "varchar(10)")
    private String isrepair;  //是否对破损、霉变、褪变档案进行修复
    @Column(columnDefinition = "varchar(10)")
    private String isauditrecord;  //是否按期完成到期档案的鉴定工作并有鉴定记录
    @Column(columnDefinition = "varchar(10)")
    private String iscomplaterecord;  //档案销毁手续是否完备
    @Column(columnDefinition = "varchar(10)")
    private String isrecordbook;  //是否建有档案资料收进、移出登记簿
    @Column(columnDefinition = "varchar(10)")
    private String isparameter;  //是否各类档案建立统计台账
    @Column(columnDefinition = "varchar(10)")
    private String isontimereport;  //是否按要求及时准确报送档案统计报表
    @Column(columnDefinition = "varchar(100)")
    private String receivetype;  //本单位接收的档案类型
    @Column(columnDefinition = "varchar(10)")
    private String reciveograndata;  //是否按规定及时接收机关部门各类档案及数据
    @Column(columnDefinition = "varchar(10)")
    private String tranferorgandata;  //是否按规定向市档案馆移交各类档案及其数据
    @Column(columnDefinition = "varchar(10)")
    private String iseleandpage;  //是否按规定向市档案馆提供政府公开信息的纸质和电子文本
    @Column(columnDefinition = "varchar(10)")
    private String iscomplateprove;  //移交接收手续是否完备
    @Column(columnDefinition = "varchar(20)")
    private String yeartranfer;  //档案移交（年度）
    @Column(columnDefinition = "varchar(50)")
    private String tranferkind;  //档案移交（种类）
    @Column(columnDefinition = "varchar(30)")
    private String tranferfilenum;  //档案移交（数量/件）
    @Column(columnDefinition = "varchar(30)")
    private String tranferfilesnum;  //档案移交（数量/卷）
    @Column(columnDefinition = "varchar(30)")
    private String yeareletranfer;  //文件移交（年度）
    @Column(columnDefinition = "varchar(30)")
    private String eletranfernum;  //文件移交（数量/件）
    @Column(columnDefinition = "varchar(30)")
    private String eletranferopennum;  //文件移交（公开文件报送/件）
    @Column(columnDefinition = "varchar(10)")
    private String islearnactive;  //是否组织开展档案业务检查培训学习交流等活动
    @Column(columnDefinition = "varchar(10)")
    private String ismeeting;  //是否召开档案会议
    @Column(columnDefinition = "varchar(500)")
    private String normativefilename;  //指定本系统档案工作的规范性文件
    @Column(columnDefinition = "varchar(10)")
    private String isworktarget;  //是否实现档案工作目标
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String organid;  //机构id
    @Column(columnDefinition = "varchar(20)")
    private String selectyear;  //年度

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsunitmaterial() {
        return isunitmaterial;
    }

    public void setIsunitmaterial(String isunitmaterial) {
        this.isunitmaterial = isunitmaterial;
    }

    public String getFillingname() {
        return fillingname;
    }

    public void setFillingname(String fillingname) {
        this.fillingname = fillingname;
    }

    public String getIsontime() {
        return isontime;
    }

    public void setIsontime(String isontime) {
        this.isontime = isontime;
    }

    public String getRoomdocfilenum() {
        return roomdocfilenum;
    }

    public void setRoomdocfilenum(String roomdocfilenum) {
        this.roomdocfilenum = roomdocfilenum;
    }

    public String getRoomdocfilesnum() {
        return roomdocfilesnum;
    }

    public void setRoomdocfilesnum(String roomdocfilesnum) {
        this.roomdocfilesnum = roomdocfilesnum;
    }

    public String getYeardocnum() {
        return yeardocnum;
    }

    public void setYeardocnum(String yeardocnum) {
        this.yeardocnum = yeardocnum;
    }

    public String getRoombasefilesnum() {
        return roombasefilesnum;
    }

    public void setRoombasefilesnum(String roombasefilesnum) {
        this.roombasefilesnum = roombasefilesnum;
    }

    public String getYearbasenum() {
        return yearbasenum;
    }

    public void setYearbasenum(String yearbasenum) {
        this.yearbasenum = yearbasenum;
    }

    public String getRoomeqfilesnum() {
        return roomeqfilesnum;
    }

    public void setRoomeqfilesnum(String roomeqfilesnum) {
        this.roomeqfilesnum = roomeqfilesnum;
    }

    public String getYeareqnum() {
        return yeareqnum;
    }

    public void setYeareqnum(String yeareqnum) {
        this.yeareqnum = yeareqnum;
    }

    public String getRoomaccountfilesnum() {
        return roomaccountfilesnum;
    }

    public void setRoomaccountfilesnum(String roomaccountfilesnum) {
        this.roomaccountfilesnum = roomaccountfilesnum;
    }

    public String getRoomaccountcopiesnum() {
        return roomaccountcopiesnum;
    }

    public void setRoomaccountcopiesnum(String roomaccountcopiesnum) {
        this.roomaccountcopiesnum = roomaccountcopiesnum;
    }

    public String getYearaccountnum() {
        return yearaccountnum;
    }

    public void setYearaccountnum(String yearaccountnum) {
        this.yearaccountnum = yearaccountnum;
    }

    public String getRoomsxfilenum() {
        return roomsxfilenum;
    }

    public void setRoomsxfilenum(String roomsxfilenum) {
        this.roomsxfilenum = roomsxfilenum;
    }

    public String getYearsxnum() {
        return yearsxnum;
    }

    public void setYearsxnum(String yearsxnum) {
        this.yearsxnum = yearsxnum;
    }

    public String getRoomphysicalnum() {
        return roomphysicalnum;
    }

    public void setRoomphysicalnum(String roomphysicalnum) {
        this.roomphysicalnum = roomphysicalnum;
    }

    public String getYearphysicalnum() {
        return yearphysicalnum;
    }

    public void setYearphysicalnum(String yearphysicalnum) {
        this.yearphysicalnum = yearphysicalnum;
    }

    public String getRoomspecialfilenum() {
        return roomspecialfilenum;
    }

    public void setRoomspecialfilenum(String roomspecialfilenum) {
        this.roomspecialfilenum = roomspecialfilenum;
    }

    public String getRoomspecialfilesnum() {
        return roomspecialfilesnum;
    }

    public void setRoomspecialfilesnum(String roomspecialfilesnum) {
        this.roomspecialfilesnum = roomspecialfilesnum;
    }

    public String getYearspecialnum() {
        return yearspecialnum;
    }

    public void setYearspecialnum(String yearspecialnum) {
        this.yearspecialnum = yearspecialnum;
    }

    public String getClassplanname() {
        return classplanname;
    }

    public void setClassplanname(String classplanname) {
        this.classplanname = classplanname;
    }

    public String getIsclearup() {
        return isclearup;
    }

    public void setIsclearup(String isclearup) {
        this.isclearup = isclearup;
    }

    public String getFundsfiles() {
        return fundsfiles;
    }

    public void setFundsfiles(String fundsfiles) {
        this.fundsfiles = fundsfiles;
    }

    public String getSetindex() {
        return setindex;
    }

    public void setSetindex(String setindex) {
        this.setindex = setindex;
    }

    public String getIsroomrecord() {
        return isroomrecord;
    }

    public void setIsroomrecord(String isroomrecord) {
        this.isroomrecord = isroomrecord;
    }

    public String getIscheckrecord() {
        return ischeckrecord;
    }

    public void setIscheckrecord(String ischeckrecord) {
        this.ischeckrecord = ischeckrecord;
    }

    public String getIsrepair() {
        return isrepair;
    }

    public void setIsrepair(String isrepair) {
        this.isrepair = isrepair;
    }

    public String getIsauditrecord() {
        return isauditrecord;
    }

    public void setIsauditrecord(String isauditrecord) {
        this.isauditrecord = isauditrecord;
    }

    public String getIscomplaterecord() {
        return iscomplaterecord;
    }

    public void setIscomplaterecord(String iscomplaterecord) {
        this.iscomplaterecord = iscomplaterecord;
    }

    public String getIsrecordbook() {
        return isrecordbook;
    }

    public void setIsrecordbook(String isrecordbook) {
        this.isrecordbook = isrecordbook;
    }

    public String getIsparameter() {
        return isparameter;
    }

    public void setIsparameter(String isparameter) {
        this.isparameter = isparameter;
    }

    public String getIsontimereport() {
        return isontimereport;
    }

    public void setIsontimereport(String isontimereport) {
        this.isontimereport = isontimereport;
    }

    public String getReceivetype() {
        return receivetype;
    }

    public void setReceivetype(String receivetype) {
        this.receivetype = receivetype;
    }

    public String getReciveograndata() {
        return reciveograndata;
    }

    public void setReciveograndata(String reciveograndata) {
        this.reciveograndata = reciveograndata;
    }

    public String getTranferorgandata() {
        return tranferorgandata;
    }

    public void setTranferorgandata(String tranferorgandata) {
        this.tranferorgandata = tranferorgandata;
    }

    public String getIseleandpage() {
        return iseleandpage;
    }

    public void setIseleandpage(String iseleandpage) {
        this.iseleandpage = iseleandpage;
    }

    public String getIscomplateprove() {
        return iscomplateprove;
    }

    public void setIscomplateprove(String iscomplateprove) {
        this.iscomplateprove = iscomplateprove;
    }

    public String getYeartranfer() {
        return yeartranfer;
    }

    public void setYeartranfer(String yeartranfer) {
        this.yeartranfer = yeartranfer;
    }

    public String getTranferkind() {
        return tranferkind;
    }

    public void setTranferkind(String tranferkind) {
        this.tranferkind = tranferkind;
    }

    public String getTranferfilenum() {
        return tranferfilenum;
    }

    public void setTranferfilenum(String tranferfilenum) {
        this.tranferfilenum = tranferfilenum;
    }

    public String getTranferfilesnum() {
        return tranferfilesnum;
    }

    public void setTranferfilesnum(String tranferfilesnum) {
        this.tranferfilesnum = tranferfilesnum;
    }

    public String getYeareletranfer() {
        return yeareletranfer;
    }

    public void setYeareletranfer(String yeareletranfer) {
        this.yeareletranfer = yeareletranfer;
    }

    public String getEletranfernum() {
        return eletranfernum;
    }

    public void setEletranfernum(String eletranfernum) {
        this.eletranfernum = eletranfernum;
    }

    public String getEletranferopennum() {
        return eletranferopennum;
    }

    public void setEletranferopennum(String eletranferopennum) {
        this.eletranferopennum = eletranferopennum;
    }

    public String getIslearnactive() {
        return islearnactive;
    }

    public void setIslearnactive(String islearnactive) {
        this.islearnactive = islearnactive;
    }

    public String getIsmeeting() {
        return ismeeting;
    }

    public void setIsmeeting(String ismeeting) {
        this.ismeeting = ismeeting;
    }

    public String getNormativefilename() {
        return normativefilename;
    }

    public void setNormativefilename(String normativefilename) {
        this.normativefilename = normativefilename;
    }

    public String getIsworktarget() {
        return isworktarget;
    }

    public void setIsworktarget(String isworktarget) {
        this.isworktarget = isworktarget;
    }

    public String getOrganid() {
        return organid;
    }

    public void setOrganid(String organid) {
        this.organid = organid;
    }

    public String getSelectyear() {
        return selectyear;
    }

    public void setSelectyear(String selectyear) {
        this.selectyear = selectyear;
    }
}
