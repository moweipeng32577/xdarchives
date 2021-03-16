package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/9 0009.
 */

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the cn.gov.saac.standards.erm package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ArchivalCodeYear_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "year");
    private final static QName _ArchivalCodeArchivesItemNumber_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "archives_item_number");
    private final static QName _ArchivalCodeCategoryCode_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "category_code");
    private final static QName _ArchivalCodeAgencyItemNumber_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "agency_item_number");
    private final static QName _ArchivalCodeCatalogueNumber_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "catalogue_number");
    private final static QName _ArchivalCodeAgencyFileNumber_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "agency_file_number");
    private final static QName _ArchivalCodeArchivesFileNumber_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "archives_file_number");
    private final static QName _ArchivalCodeRetentionPeriod_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "retention_period");
    private final static QName _ArchivalCodeFondsId_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "fonds_id");
    private final static QName _ArchivalCodeOsof_QNAME = new QName("http://www.saac.gov.cn/standards/ERM", "osof");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cn.gov.saac.standards.erm
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Agent }
     *
     */
    public Agent createAgent() {
        return new Agent();
    }

    /**
     * Create an instance of {@link RecordBlock }
     *
     */
    public RecordBlock createRecordBlock() {
        return new RecordBlock();
    }

    /**
     * Create an instance of {@link BusinessEntity }
     *
     */
    public BusinessEntity createBusinessEntity() {
        return new BusinessEntity();
    }

    /**
     * Create an instance of {@link Content }
     *
     */
    public Content createContent() {
        return new Content();
    }

    /**
     * Create an instance of {@link DocumentRelation }
     *
     */
    public DocumentRelation createDocumentRelation() {
        return new DocumentRelation();
    }

    /**
     * Create an instance of {@link Document }
     *
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link AgentBlock }
     *
     */
    public AgentBlock createAgentBlock() {
        return new AgentBlock();
    }

    /**
     * Create an instance of {@link AgentRelation }
     *
     */
    public AgentRelation createAgentRelation() {
        return new AgentRelation();
    }

    /**
     * Create an instance of {@link Sip }
     *
     */
    public Sip createSip() {
        return new Sip();
    }

    /**
     * Create an instance of {@link BusinessBlock }
     *
     */
    public BusinessBlock createBusinessBlock() {
        return new BusinessBlock();
    }

    /**
     * Create an instance of {@link ArchivalCode }
     *
     */
    public ArchivalCode createArchivalCode() {
        return new ArchivalCode();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "year", scope = ArchivalCode.class)
    public JAXBElement<XMLGregorianCalendar> createArchivalCodeYear(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ArchivalCodeYear_QNAME, XMLGregorianCalendar.class, ArchivalCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "archives_item_number", scope = ArchivalCode.class)
    public JAXBElement<Object> createArchivalCodeArchivesItemNumber(Object value) {
        return new JAXBElement<Object>(_ArchivalCodeArchivesItemNumber_QNAME, Object.class, ArchivalCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "category_code", scope = ArchivalCode.class)
    public JAXBElement<Object> createArchivalCodeCategoryCode(Object value) {
        return new JAXBElement<Object>(_ArchivalCodeCategoryCode_QNAME, Object.class, ArchivalCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "agency_item_number", scope = ArchivalCode.class)
    public JAXBElement<Object> createArchivalCodeAgencyItemNumber(Object value) {
        return new JAXBElement<Object>(_ArchivalCodeAgencyItemNumber_QNAME, Object.class, ArchivalCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "catalogue_number", scope = ArchivalCode.class)
    public JAXBElement<String> createArchivalCodeCatalogueNumber(String value) {
        return new JAXBElement<String>(_ArchivalCodeCatalogueNumber_QNAME, String.class, ArchivalCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "agency_file_number", scope = ArchivalCode.class)
    public JAXBElement<Object> createArchivalCodeAgencyFileNumber(Object value) {
        return new JAXBElement<Object>(_ArchivalCodeAgencyFileNumber_QNAME, Object.class, ArchivalCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "archives_file_number", scope = ArchivalCode.class)
    public JAXBElement<Object> createArchivalCodeArchivesFileNumber(Object value) {
        return new JAXBElement<Object>(_ArchivalCodeArchivesFileNumber_QNAME, Object.class, ArchivalCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "retention_period", scope = ArchivalCode.class)
    public JAXBElement<String> createArchivalCodeRetentionPeriod(String value) {
        return new JAXBElement<String>(_ArchivalCodeRetentionPeriod_QNAME, String.class, ArchivalCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "fonds_id", scope = ArchivalCode.class)
    public JAXBElement<String> createArchivalCodeFondsId(String value) {
        return new JAXBElement<String>(_ArchivalCodeFondsId_QNAME, String.class, ArchivalCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.saac.gov.cn/standards/ERM", name = "osof", scope = ArchivalCode.class)
    public JAXBElement<Object> createArchivalCodeOsof(Object value) {
        return new JAXBElement<Object>(_ArchivalCodeOsof_QNAME, Object.class, ArchivalCode.class, value);
    }

}
