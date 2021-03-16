package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fonds_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="catalogue_number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="year" type="{http://www.w3.org/2001/XMLSchema}gYear"/>
 *         &lt;element name="retention_period">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="����"/>
 *               &lt;enumeration value="����"/>
 *               &lt;enumeration value="����"/>
 *               &lt;enumeration value="10��"/>
 *               &lt;enumeration value="30��"/>
 *               &lt;enumeration value="����"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="osof" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="category_code" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="agency_file_number" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="archives_file_number" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="agency_item_number" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="archives_item_number" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name = "", propOrder = {
//        "content"
//})
//@XmlRootElement(name = "archival_code")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"fondsId", "catalogueNumber", "year", "retentionPeriod", "osof", "categoryCode",
        "agencyFileNumber", "archivesFileNumber", "agencyItemNumber", "archivesItemNumber"})
@XmlRootElement(name="archival_code")
public class ArchivalCode {

//    @XmlElementRefs({
//            @XmlElementRef(name = "archives_file_number", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class),
//            @XmlElementRef(name = "category_code", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class),
//            @XmlElementRef(name = "fonds_id", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class),
//            @XmlElementRef(name = "year", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class),
//            @XmlElementRef(name = "osof", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class),
//            @XmlElementRef(name = "catalogue_number", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class),
//            @XmlElementRef(name = "agency_file_number", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class),
//            @XmlElementRef(name = "agency_item_number", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class),
//            @XmlElementRef(name = "retention_period", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class),
//            @XmlElementRef(name = "archives_item_number", namespace = "http://www.saac.gov.cn/standards/ERM", type = JAXBElement.class)
//    })
//    @XmlMixed

//    protected Serializable content;
    @XmlElement(name = "fonds_id")
    protected String fondsId;

    @XmlElement(name = "catalogue_number")
    protected String catalogueNumber;

    @XmlElement(name = "year")
    protected String year;

    @XmlElement(name = "retention_period")
    protected String retentionPeriod;

    @XmlElement(name = "osof")
    protected String osof;

    @XmlElement(name = "category_code")
    protected String categoryCode;

    @XmlElement(name = "agency_file_number")
    protected String agencyFileNumber;

    @XmlElement(name = "archives_file_number")
    protected String archivesFileNumber;

    @XmlElement(name = "agency_item_number")
    protected String agencyItemNumber;

    @XmlElement(name = "archives_item_number")
    protected String archivesItemNumber;
    /**
     * Gets the value of the content property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link String }
     * {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     *
     *
     */
//    public List<Serializable> getContent() {
//        if (content == null) {
//            content = new ArrayList<Serializable>();
//        }
//        return this.content;
//    }

//    public Serializable getContent() {
//        return content;
//    }
//
//    public void setContent(Serializable content) {
//        this.content = content;
//    }


    public String getFondsId() {
        return fondsId;
    }

    public void setFondsId(String fondsId) {
        this.fondsId = fondsId;
    }

    public String getCatalogueNumber() {
        return catalogueNumber;
    }

    public void setCatalogueNumber(String catalogueNumber) {
        this.catalogueNumber = catalogueNumber;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRetentionPeriod() {
        return retentionPeriod;
    }

    public void setRetentionPeriod(String retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    public String getOsof() {
        return osof;
    }

    public void setOsof(String osof) {
        this.osof = osof;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getAgencyFileNumber() {
        return agencyFileNumber;
    }

    public void setAgencyFileNumber(String agencyFileNumber) {
        this.agencyFileNumber = agencyFileNumber;
    }

    public String getArchivesFileNumber() {
        return archivesFileNumber;
    }

    public void setArchivesFileNumber(String archivesFileNumber) {
        this.archivesFileNumber = archivesFileNumber;
    }

    public String getAgencyItemNumber() {
        return agencyItemNumber;
    }

    public void setAgencyItemNumber(String agencyItemNumber) {
        this.agencyItemNumber = agencyItemNumber;
    }

    public String getArchivesItemNumber() {
        return archivesItemNumber;
    }

    public void setArchivesItemNumber(String archivesItemNumber) {
        this.archivesItemNumber = archivesItemNumber;
    }
}

