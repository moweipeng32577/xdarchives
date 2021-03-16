package com.wisdom.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
public class Tb_event_entry {

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "uuid") // 生成32位UUID
	@GeneratedValue(generator = "idGenerator")
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String evententryid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String eventid;
	@Type(type = "com.wisdom.util.OracleCharIDType")
	@Column(columnDefinition = "char(36)")
	private String entryid;

	public String getEvententryid() {
		return evententryid;
	}

	public void setEvententryid(String evententryid) {
		this.evententryid = evententryid;
	}

	public String getEventid() {
		return eventid;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public String getEntryid() {
		return entryid;
	}

	public void setEntryid(String entryid) {
		this.entryid = entryid;
	}
}