package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="file_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="file_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="file_size" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="mime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="application" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="embedded" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="file_content" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="file_hash" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "fileId",
        "fileName",
        "fileSize",
        "mime",
        "application",
        "embedded",
        "fileContent",
        "fileHash"
})
@XmlRootElement(name = "document")
public class Document {

    @XmlElement(name = "file_id", required = true)
    protected String fileId;
    @XmlElement(name = "file_name", required = true)
    protected String fileName;
    @XmlElement(name = "file_size")
    protected long fileSize;
    @XmlElement(required = true)
    protected String mime;
    protected String application;
    @XmlElement(defaultValue = "true")
    protected Boolean embedded;
    @XmlElement(name = "file_content")
    protected String fileContent;
    @XmlElement(name = "file_hash", required = true)
    protected String fileHash;

    /**
     * Gets the value of the fileId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * Sets the value of the fileId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFileId(String value) {
        this.fileId = value;
    }

    /**
     * Gets the value of the fileName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the fileSize property.
     *
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the value of the fileSize property.
     *
     */
    public void setFileSize(long value) {
        this.fileSize = value;
    }

    /**
     * Gets the value of the mime property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMime() {
        return mime;
    }

    /**
     * Sets the value of the mime property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMime(String value) {
        this.mime = value;
    }

    /**
     * Gets the value of the application property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getApplication() {
        return application;
    }

    /**
     * Sets the value of the application property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setApplication(String value) {
        this.application = value;
    }

    /**
     * Gets the value of the embedded property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isEmbedded() {
        return embedded;
    }

    /**
     * Sets the value of the embedded property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setEmbedded(Boolean value) {
        this.embedded = value;
    }

    /**
     * Gets the value of the fileContent property.
     *
     * @return
     *     possible object is
     *     byte[]
     */
    public String getFileContent() {
        return fileContent;
    }

    /**
     * Sets the value of the fileContent property.
     *
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFileContent(String value) {
        this.fileContent = value;
    }

    /**
     * Gets the value of the fileHash property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFileHash() {
        return fileHash;
    }

    /**
     * Sets the value of the fileHash property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFileHash(String value) {
        this.fileHash = value;
    }

}

