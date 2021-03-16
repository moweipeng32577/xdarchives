package com.wisdom.web.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 缩略图
 */
@Entity
public class Tb_thumbnail {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    private String thumbid;//主键
    private String nodeid;//节点id
    private String eleid;
    private String entryid;//条目id
    private String name;//描述
    private String url;//地址
    private String type;//类型(采集or管理)
    private String entrysecurity;//密级
    
    public Tb_thumbnail(){}

    public Tb_thumbnail(String eleid, String entryid, String name, String url, String type){
       // this.thumbid = thumbid;
        this.eleid = eleid;
        this.entryid = entryid;
        this.name = name;
        this.url = url;
        this.type = type;
        this.entrysecurity = entrysecurity;
    }
    public Tb_thumbnail(String nodeid, String entryid, String name, String url, String type, String entrysecurity){
        // this.thumbid = thumbid;
        this.nodeid = nodeid;
        this.entryid = entryid;
        this.name = name;
        this.url = url;
        this.type = type;
        this.entrysecurity = entrysecurity;
    }
    
    public String getEleid() {
		return eleid;
	}

	public void setEleid(String eleid) {
		this.eleid = eleid;
	}

	public String getEntrysecurity() {
        return entrysecurity;
    }

    public void setEntrysecurity(String entrysecurity) {
        this.entrysecurity = entrysecurity;
    }

    public String getThumbid() {
        return thumbid;
    }

    public void setThumbid(String thumbid) {
        this.thumbid = thumbid;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}