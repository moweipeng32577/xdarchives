package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


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
        "unitId",
        "unitName",
        "erCode",
        "isDescription",
        "sourceid",
        "archivalCode",
        "content",
        "recordBlock",
        "businessBlock",
        "agentBlock"
})
@XmlRootElement(name = "sip")
public class Sip {

    @XmlElement(name = "unit_id", required = true)
    protected String unitId;
    @XmlElement(name = "unit_name", required = true)
    protected String unitName;
    @XmlElement(name = "er_code", required = true)
    protected String erCode;
    @XmlElement(name = "is_description", required = true)
    protected String isDescription;
    @XmlElement(required = true)
    protected String sourceid;
    @XmlElement(name = "archival_code")
    protected ArchivalCode archivalCode;
    @XmlElement(required = true)
    protected Content content;
    @XmlElement(name = "record_block", required = true)
    protected RecordBlock recordBlock;
    @XmlElement(name = "business_block", required = true)
    protected BusinessBlock businessBlock;
    @XmlElement(name = "agent_block", required = true)
    protected AgentBlock agentBlock;
    @XmlAttribute
    @XmlSchemaType(name = "gYear")
    protected XMLGregorianCalendar version;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createtime;


    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    /**
     * Gets the value of the unitName property.
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
     * Sets the value of the unitName property.
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
     * Gets the value of the erCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getErCode() {
        return erCode;
    }

    /**
     * Sets the value of the erCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setErCode(String value) {
        this.erCode = value;
    }

    /**
     * Gets the value of the isDescription property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIsDescription() {
        return isDescription;
    }

    /**
     * Sets the value of the isDescription property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIsDescription(String value) {
        this.isDescription = value;
    }

    /**
     * Gets the value of the sourceid property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSourceid() {
        return sourceid;
    }

    /**
     * Sets the value of the sourceid property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSourceid(String value) {
        this.sourceid = value;
    }

    /**
     * Gets the value of the archivalCode property.
     *
     * @return
     *     possible object is
     *     {@link ArchivalCode }
     *
     */
    public ArchivalCode getArchivalCode() {
        return archivalCode;
    }

    /**
     * Sets the value of the archivalCode property.
     *
     * @param value
     *     allowed object is
     *     {@link ArchivalCode }
     *
     */
    public void setArchivalCode(ArchivalCode value) {
        this.archivalCode = value;
    }

    /**
     * Gets the value of the content property.
     *
     * @return
     *     possible object is
     *     {@link Content }
     *
     */
    public Content getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     *
     * @param value
     *     allowed object is
     *     {@link Content }
     *
     */
    public void setContent(Content value) {
        this.content = value;
    }

    /**
     * Gets the value of the recordBlock property.
     *
     * @return
     *     possible object is
     *     {@link RecordBlock }
     *
     */
    public RecordBlock getRecordBlock() {
        return recordBlock;
    }

    /**
     * Sets the value of the recordBlock property.
     *
     * @param value
     *     allowed object is
     *     {@link RecordBlock }
     *
     */
    public void setRecordBlock(RecordBlock value) {
        this.recordBlock = value;
    }

    /**
     * Gets the value of the businessBlock property.
     *
     * @return
     *     possible object is
     *     {@link BusinessBlock }
     *
     */
    public BusinessBlock getBusinessBlock() {
        return businessBlock;
    }

    /**
     * Sets the value of the businessBlock property.
     *
     * @param value
     *     allowed object is
     *     {@link BusinessBlock }
     *
     */
    public void setBusinessBlock(BusinessBlock value) {
        this.businessBlock = value;
    }

    /**
     * Gets the value of the agentBlock property.
     *
     * @return
     *     possible object is
     *     {@link AgentBlock }
     *
     */
    public AgentBlock getAgentBlock() {
        return agentBlock;
    }

    /**
     * Sets the value of the agentBlock property.
     *
     * @param value
     *     allowed object is
     *     {@link AgentBlock }
     *
     */
    public void setAgentBlock(AgentBlock value) {
        this.agentBlock = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setVersion(XMLGregorianCalendar value) {
        this.version = value;
    }

    /**
     * Gets the value of the createtime property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getCreatetime() {
        return createtime;
    }

    /**
     * Sets the value of the createtime property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setCreatetime(XMLGregorianCalendar value) {
        this.createtime = value;
    }
}
