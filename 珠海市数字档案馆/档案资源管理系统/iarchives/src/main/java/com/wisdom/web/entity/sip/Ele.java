package com.wisdom.web.entity.sip;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * <p>
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ele_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://www.saac.gov.cn/standards/ERM}document" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "eleId",
        "document"
})
@XmlRootElement(name = "ele")
public class Ele {

    @XmlElement(name = "ele_id", required = true)
    protected String eleId;
    @XmlElement(required = true)
    protected List<Document> document;

    /**
     * 获取eleId属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getEleId() {
        return eleId;
    }

    /**
     * 设置eleId属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setEleId(String value) {
        this.eleId = value;
    }

    /**
     * Gets the value of the document property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the document property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocument().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Document }
     */
    public List<Document> getDocument() {
        if (document == null) {
            document = new ArrayList<Document>();
        }
        return this.document;
    }

}
