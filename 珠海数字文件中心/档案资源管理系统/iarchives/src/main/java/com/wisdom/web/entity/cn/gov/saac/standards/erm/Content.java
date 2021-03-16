package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"title", "parallelTitle", "alternativeTitle", "titleInformation", "descriptor", "keyword", "personalName", "_abstract", "classCode", "documentNumber", "author", "date", "documentType", "precedence", "principalRecever", "otherRecevers", "securityClass", "secrecyPeriod"})
@XmlRootElement(name="content")
public class Content {

    @XmlElement(required = true)
    protected String title;

    @XmlElement(name = "parallel_title")
    protected String parallelTitle;

    @XmlElement(name = "alternative_title")
    protected String alternativeTitle;

    @XmlElement(name = "title_information")
    protected String titleInformation;
    protected String descriptor;
    protected String keyword;

//    protected List<String> descriptor;
//    protected List<String> keyword;

    @XmlElement(name = "personal_name")
    protected String personalName;

    @XmlElement(name = "abstract")
    protected String _abstract;

    @XmlElement(name = "class_code")
    protected String classCode;

    @XmlElement(name = "document_number", required = true)
    protected String documentNumber;

    @XmlElement(required = true)
    protected String author;

    @XmlElement(required = true)
    protected String date;

    @XmlElement(name = "document_type", required = true)
    protected String documentType;
    protected String precedence;

    @XmlElement(name = "principal_recever")
    protected String principalRecever;

    @XmlElement(name = "other_recevers")
    protected String otherRecevers;

    @XmlElement(name = "security_class", required = true)
    protected String securityClass;

    @XmlElement(name = "secrecy_period")
    protected String secrecyPeriod;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getParallelTitle() {
        return this.parallelTitle;
    }

    public void setParallelTitle(String value) {
        this.parallelTitle = value;
    }

    public String getAlternativeTitle() {
        return this.alternativeTitle;
    }

    public void setAlternativeTitle(String value) {
        this.alternativeTitle = value;
    }

    public String getTitleInformation() {
        return this.titleInformation;
    }

    public void setTitleInformation(String value) {
        this.titleInformation = value;
    }

//    public List<String> getDescriptor() {
//        if (this.descriptor == null) {
//            this.descriptor = new ArrayList();
//        }
//        return this.descriptor;
//    }
//
//    public List<String> getKeyword() {
//        if (this.keyword == null) {
//            this.keyword = new ArrayList();
//        }
//        return this.keyword;
//    }

    public String getPersonalName() {
        return this.personalName;
    }

    public void setPersonalName(String value) {
        this.personalName = value;
    }

    public String getAbstract() {
        return this._abstract;
    }

    public void setAbstract(String value) {
        this._abstract = value;
    }

    public String getClassCode() {
        return this.classCode;
    }

    public void setClassCode(String value) {
        this.classCode = value;
    }

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(String value) {
        this.documentNumber = value;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String value) {
        this.author = value;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String value) {
        this.date = value;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public void setDocumentType(String value) {
        this.documentType = value;
    }

    public String getPrecedence() {
        return this.precedence;
    }

    public void setPrecedence(String value) {
        this.precedence = value;
    }

    public String getPrincipalRecever() {
        return this.principalRecever;
    }

    public void setPrincipalRecever(String value) {
        this.principalRecever = value;
    }

    public String getOtherRecevers() {
        return this.otherRecevers;
    }

    public void setOtherRecevers(String value) {
        this.otherRecevers = value;
    }

    public String getSecurityClass() {
        return this.securityClass;
    }

    public void setSecurityClass(String value) {
        this.securityClass = value;
    }

    public String getSecrecyPeriod() {
        return this.secrecyPeriod;
    }

    public void setSecrecyPeriod(String value) {
        this.secrecyPeriod = value;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String get_abstract() {
        return _abstract;
    }

    public void set_abstract(String _abstract) {
        this._abstract = _abstract;
    }
}