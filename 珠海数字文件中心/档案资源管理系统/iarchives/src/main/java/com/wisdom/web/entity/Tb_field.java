package com.wisdom.web.entity;

public class Tb_field {

	private String fieldName;
	
	private String targetFieldName;
	
	private String fieldCode;
	
	private String targetFieldCode;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getTargetFieldName() {
		return targetFieldName;
	}

	public void setTargetFieldName(String targetFieldName) {
		this.targetFieldName = targetFieldName;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getTargetFieldCode() {
		return targetFieldCode;
	}

	public void setTargetFieldCode(String targetFieldCode) {
		this.targetFieldCode = targetFieldCode;
	}
}