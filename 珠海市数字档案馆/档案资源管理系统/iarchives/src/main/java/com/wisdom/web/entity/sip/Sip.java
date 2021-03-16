package com.wisdom.web.entity.sip;

/**
 * Created by yl on 2017/11/7.
 */
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="unit_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="document_type">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="收文"/>
 *               &lt;enumeration value="发文"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="funds" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="catalog" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="retention">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="永久"/>
 *               &lt;enumeration value="长期"/>
 *               &lt;enumeration value="短期"/>
 *               &lt;enumeration value="30年"/>
 *               &lt;enumeration value="10年"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="file_date" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="filing_year" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://www.saac.gov.cn/standards/ERM}file" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "unitName",
        "documentType",
        "title",
        "funds",
        "catalog",
        "filenumber",
        "entryretention",
        "filedate",
        "filingyear",
        "file"
})
@XmlRootElement(name = "sip")
public class Sip {

    @XmlElement(name = "unit_name", required = true)
    protected String unitName;
    @XmlElement(name = "document_type", required = true)
    protected String documentType;
    @XmlElement(required = true)
    protected String title;
    @XmlElement(required = true)
    protected String funds;
    @XmlElement(required = true)
    protected String catalog;
    @XmlElement(name = "number",required = true)
    protected String filenumber;
    @XmlElement(name = "retention",required = true)
    protected String entryretention;
    @XmlElement(name = "file_date", required = true)
    protected String filedate;
    @XmlElement(name = "filing_year", required = true)
    protected String filingyear;
    @XmlElement(required = true)
    protected List<File> file;

    /**
     * 获取unitName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * 设置unitName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnitName(String value) {
        this.unitName = value;
    }

    /**
     * 获取documentType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * 设置documentType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDocumentType(String value) {
        this.documentType = value;
    }

    /**
     * 获取title属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置title属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * 获取funds属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFunds() {
        return funds;
    }

    /**
     * 设置funds属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFunds(String value) {
        this.funds = value;
    }

    /**
     * 获取catalog属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * 设置catalog属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCatalog(String value) {
        this.catalog = value;
    }

    /**
     * 获取number属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFilenumber() {
        return filenumber;
    }

    /**
     * 设置number属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFilenumber(String value) {
        this.filenumber = value;
    }

    /**
     * 获取retention属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEntryretention() {
        return entryretention;
    }

    /**
     * 设置retention属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRetention(String value) {
        this.entryretention = value;
    }

    /**
     * 获取fileDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFiledate() {
        return filedate;
    }

    /**
     * 设置fileDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */

    public void setFiledate(String value) {
        this.filedate = value;
    }

    /**
     * 获取filingYear属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFilingyear() {
        return filingyear;
    }

    /**
     * 设置filingYear属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFilingyear(String value) {
        this.filingyear = value;
    }

    /**
     * Gets the value of the file property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the file property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFile().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link File }
     *
     *
     */
    public List<File> getFile() {
        if (file == null) {
            file = new ArrayList<File>();
        }
        return this.file;
    }

}
