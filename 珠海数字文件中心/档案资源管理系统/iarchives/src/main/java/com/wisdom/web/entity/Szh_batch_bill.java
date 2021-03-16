package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 数字化质检模块(批次单据表)
 */
@Entity
public class Szh_batch_bill {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String  id;         //主键
    private String  batchcode;  //批次号
    private String  batchname;  //批次名
    private String  archivetype;//档案类型
    private String  samplingtype;//抽检类型
    private Integer copies;     //份数
    private Integer pagenum;    //页数
    private String  checkcount; //抽检数量
    private String  inspector;  //抽检人
    private String  checktime;  //抽检时间
    private String  passrate;   //通过率
    private String  finishtime; //完成时间
    private String  status;     //质检状态
    private String type;        //类型(质检OR验收)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBatchcode() {
        return batchcode;
    }

    public void setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }

    public String getBatchname() {
        return batchname;
    }

    public void setBatchname(String batchname) {
        this.batchname = batchname;
    }

    public String getArchivetype() {
        return archivetype;
    }

    public void setArchivetype(String archivetype) {
        this.archivetype = archivetype;
    }

    public String getSamplingtype() {
        return samplingtype;
    }

    public void setSamplingtype(String samplingtype) {
        this.samplingtype = samplingtype;
    }

    public Integer getCopies() {
        return copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    public Integer getPagenum() {
        return pagenum;
    }

    public void setPagenum(Integer pagenum) {
        this.pagenum = pagenum;
    }

    public String getCheckcount() {
        return checkcount;
    }

    public void setCheckcount(String checkcount) {
        this.checkcount = checkcount;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public String getChecktime() {
        return checktime;
    }

    public void setChecktime(String checktime) {
        this.checktime = checktime;
    }

    public String getPassrate() {
        return passrate;
    }

    public void setPassrate(String passrate) {
        this.passrate = passrate;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
