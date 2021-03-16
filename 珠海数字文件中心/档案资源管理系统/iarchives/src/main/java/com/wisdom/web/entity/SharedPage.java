package com.wisdom.web.entity;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * 共享数据分页实体类
 * Created by wjh
 */
@XmlRootElement(name="IPage")
@XmlAccessorType(XmlAccessType.FIELD)
public class SharedPage {
    /**
     * 每页的展现条数
     */
    private int pageSize = 20;
    /**
     * 总记录数
     */
    private int totalCount;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 当前页数
     */
    private int currentPage = 0;
    /**
     * 当前页起始记录
     */
    private int startIndex = 0;

    /**
     *返回状态信息
     */
    private String msg;

    private List data;

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

//    /**
//     * 查询用户结果集
//     */
//    @XmlElementWrapper(name="users")
//    @XmlElement(name="user")
//    private List<Tb_user> users;
//
//    /**
//     * 查询机构结果集
//     */
//    @XmlElementWrapper(name="organs")
//    @XmlElement(name="organ")
//    private List<Tb_right_organ> organs;
//
//    /**
//     * 查询业务数据结果集
//     */
//    @XmlElementWrapper(name="entrys")
//    @XmlElement(name="entry")
//    private List<Tb_entry_index> entrys;


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}