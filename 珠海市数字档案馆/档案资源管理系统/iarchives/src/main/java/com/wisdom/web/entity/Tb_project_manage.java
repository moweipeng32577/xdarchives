package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Administrator on 2020/7/20.
 */
@Entity
public class Tb_project_manage {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Type(type = "com.wisdom.util.OracleCharIDType")
    @Column(columnDefinition = "char(36)")
    private String id;//项目管理id
    @Column(columnDefinition = "varchar(50)")
    private String title;//标题
    @Column(columnDefinition = "varchar(2000)")
    private String workproject;//工作项目
    @Column(columnDefinition = "varchar(2000)")
    private String workcontent;//工作内容
    @Column(columnDefinition = "varchar(50)")
    private String leaderrespon;//责任领导
    @Column(columnDefinition = "varchar(50)")
    private String undertakedepart;//承办科室
    @Column(columnDefinition = "varchar(50)")
    private String undertaker ;//承办人
    @Column(columnDefinition = "varchar(50)")
    private String cooperatedepart;//配合科室
    @Column(columnDefinition = "varchar(30)")
    private String finishtime;//完成时间
    @Column(columnDefinition = "varchar(50)")
    private String opinion;//督导意见
    @Column(columnDefinition = "varchar(20)")
    private String projectstatus;//状态  项目记录(1->8种)“状态”：
    // 1新增项目->2提交部门审核、提交副馆长审阅、提交馆长审阅->3部门审核通过、4部门审核不通过->5副领导审阅通过、6副领导审阅不通过>7领导审阅通过发布、8领导审阅不发布
    @Column(columnDefinition = "char(36)")
    private String operator;//操作人（新增项目记录人）
    @Column(columnDefinition = "varchar(2000)")
    private String desci;//备注,主要用于对项目详细描述
    @Column(columnDefinition = "varchar(30)")
    private String createtime;//创建时间
    @Column(columnDefinition = "varchar(30)")
    private String bmshtime;//提交部门审核时间
    @Column(columnDefinition = "varchar(30)")
    private String zhswbtime;//提交综合事务部整理时间
    @Column(columnDefinition = "varchar(30)")
    private String fgzsytime;//提交副馆长审阅时间
    @Column(columnDefinition = "varchar(30)")
    private String gzsytime;//提交馆长审阅时间
    @Column(columnDefinition = "char(36)")
    private String spnodeid;//审批环节
    public String getId() {
        return id;
    }
    @Column(columnDefinition = "varchar(1000)")
    private String approve;

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkproject() {
        return workproject;
    }

    public void setWorkproject(String workproject) {
        this.workproject = workproject;
    }

    public String getWorkcontent() {
        return workcontent;
    }

    public void setWorkcontent(String workcontent) {
        this.workcontent = workcontent;
    }

    public String getLeaderrespon() {
        return leaderrespon;
    }

    public void setLeaderrespon(String leaderrespon) {
        this.leaderrespon = leaderrespon;
    }

    public String getUndertakedepart() {
        return undertakedepart;
    }

    public void setUndertakedepart(String undertakedepart) {
        this.undertakedepart = undertakedepart;
    }

    public String getUndertaker() {
        return undertaker;
    }

    public void setUndertaker(String undertaker) {
        this.undertaker = undertaker;
    }

    public String getCooperatedepart() {
        return cooperatedepart;
    }

    public void setCooperatedepart(String cooperatedepart) {
        this.cooperatedepart = cooperatedepart;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getProjectstatus() {
        return projectstatus;
    }

    public void setProjectstatus(String projectstatus) {
        this.projectstatus = projectstatus;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDesci() {
        return desci;
    }

    public void setDesci(String desci) {
        this.desci = desci;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getBmshtime() {
        return bmshtime;
    }

    public void setBmshtime(String bmshtime) {
        this.bmshtime = bmshtime;
    }

    public String getZhswbtime() {
        return zhswbtime;
    }

    public void setZhswbtime(String zhswbtime) {
        this.zhswbtime = zhswbtime;
    }

    public String getFgzsytime() {
        return fgzsytime;
    }

    public void setFgzsytime(String fgzsytime) {
        this.fgzsytime = fgzsytime;
    }

    public String getGzsytime() {
        return gzsytime;
    }

    public void setGzsytime(String gzsytime) {
        this.gzsytime = gzsytime;
    }

    public String getSpnodeid() {
        return spnodeid;
    }

    public void setSpnodeid(String spnodeid) {
        this.spnodeid = spnodeid;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }
}
