package com.wisdom.web.entity;

import java.sql.Date;

/**
 * Created by tanly on 2018/11/8 0012.
 */
public class OaEntry {
    private String archives_no;
    private String title;//题名
    private String sub_title;//并列题名（文头）
    private String archives_type;//文种
    private String department;//登记部门
    private String description_user;//登记人/拟稿人
    private Date description_date;//登记日期/拟稿日期
    private String lw_number;//来文编号
    private String lw_unit;//来文单位
    private String file_number;//收文序号/签报编号/文号
    private String archive_class;//分类
    private String child_class;//子类
    private String entry_retention;//保管期限
    private String flag_notice;//参加会议通知
    private String main_sender;//主送/主送领导
    private String copy_sender;//抄送
    private String emergency;//紧急程度
    private String is_open;//是否公开
    private String open_scope;//公开范围
    private String print_count;//印发份数
    private String proof;//校对
    private String archives_count;//件数
    private String page_count;//页数
    private String signreport_content;//签报正文

    public String getArchives_no() {
        return archives_no;
    }

    public void setArchives_no(String archives_no) {
        this.archives_no = archives_no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getArchives_type() {
        return archives_type;
    }

    public void setArchives_type(String archives_type) {
        this.archives_type = archives_type;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription_user() {
        return description_user;
    }

    public void setDescription_user(String description_user) {
        this.description_user = description_user;
    }

    public Date getDescription_date() {
        return description_date;
    }

    public void setDescription_date(Date description_date) {
        this.description_date = description_date;
    }

    public String getLw_number() {
        return lw_number;
    }

    public void setLw_number(String lw_number) {
        this.lw_number = lw_number;
    }

    public String getLw_unit() {
        return lw_unit;
    }

    public void setLw_unit(String lw_unit) {
        this.lw_unit = lw_unit;
    }

    public String getFile_number() {
        return file_number;
    }

    public void setFile_number(String file_number) {
        this.file_number = file_number;
    }

    public String getArchive_class() {
        return archive_class;
    }

    public void setArchive_class(String archive_class) {
        this.archive_class = archive_class;
    }

    public String getChild_class() {
        return child_class;
    }

    public void setChild_class(String child_class) {
        this.child_class = child_class;
    }

    public String getEntry_retention() {
        return entry_retention;
    }

    public void setEntry_retention(String entry_retention) {
        this.entry_retention = entry_retention;
    }

    public String getFlag_notice() {
        return flag_notice;
    }

    public void setFlag_notice(String flag_notice) {
        this.flag_notice = flag_notice;
    }

    public String getMain_sender() {
        return main_sender;
    }

    public void setMain_sender(String main_sender) {
        this.main_sender = main_sender;
    }

    public String getCopy_sender() {
        return copy_sender;
    }

    public void setCopy_sender(String copy_sender) {
        this.copy_sender = copy_sender;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public String getIs_open() {
        return is_open;
    }

    public void setIs_open(String is_open) {
        this.is_open = is_open;
    }

    public String getOpen_scope() {
        return open_scope;
    }

    public void setOpen_scope(String open_scope) {
        this.open_scope = open_scope;
    }

    public String getPrint_count() {
        return print_count;
    }

    public void setPrint_count(String print_count) {
        this.print_count = print_count;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public String getArchives_count() {
        return archives_count;
    }

    public void setArchives_count(String archives_count) {
        this.archives_count = archives_count;
    }

    public String getPage_count() {
        return page_count;
    }

    public void setPage_count(String page_count) {
        this.page_count = page_count;
    }

    public String getSignreport_content() {
        return signreport_content;
    }

    public void setSignreport_content(String signreport_content) {
        this.signreport_content = signreport_content;
    }

}
