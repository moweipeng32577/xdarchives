
package com.wisdom.util.netca;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the netca.verifycert package. 
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

    private final static QName _ChkOneCert_QNAME = new QName("http://appintf.netcacertaa.netca/", "ChkOneCert");
    private final static QName _VerifyUserCert_QNAME = new QName("http://appintf.netcacertaa.netca/", "VerifyUserCert");
    private final static QName _VerifyUserCertResponse_QNAME = new QName("http://appintf.netcacertaa.netca/", "VerifyUserCertResponse");
    private final static QName _ChkOneCertResponse_QNAME = new QName("http://appintf.netcacertaa.netca/", "ChkOneCertResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: netca.verifycert
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ChkOneCert }
     * 
     */
    public ChkOneCert createChkOneCert() {
        return new ChkOneCert();
    }

    /**
     * Create an instance of {@link VerifyUserCert }
     * 
     */
    public VerifyUserCert createVerifyUserCert() {
        return new VerifyUserCert();
    }

    /**
     * Create an instance of {@link VerifyUserCertResponse }
     * 
     */
    public VerifyUserCertResponse createVerifyUserCertResponse() {
        return new VerifyUserCertResponse();
    }

    /**
     * Create an instance of {@link ChkOneCertResponse }
     * 
     */
    public ChkOneCertResponse createChkOneCertResponse() {
        return new ChkOneCertResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChkOneCert }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://appintf.netcacertaa.netca/", name = "ChkOneCert")
    public JAXBElement<ChkOneCert> createChkOneCert(ChkOneCert value) {
        return new JAXBElement<ChkOneCert>(_ChkOneCert_QNAME, ChkOneCert.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyUserCert }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://appintf.netcacertaa.netca/", name = "VerifyUserCert")
    public JAXBElement<VerifyUserCert> createVerifyUserCert(VerifyUserCert value) {
        return new JAXBElement<VerifyUserCert>(_VerifyUserCert_QNAME, VerifyUserCert.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyUserCertResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://appintf.netcacertaa.netca/", name = "VerifyUserCertResponse")
    public JAXBElement<VerifyUserCertResponse> createVerifyUserCertResponse(VerifyUserCertResponse value) {
        return new JAXBElement<VerifyUserCertResponse>(_VerifyUserCertResponse_QNAME, VerifyUserCertResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChkOneCertResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://appintf.netcacertaa.netca/", name = "ChkOneCertResponse")
    public JAXBElement<ChkOneCertResponse> createChkOneCertResponse(ChkOneCertResponse value) {
        return new JAXBElement<ChkOneCertResponse>(_ChkOneCertResponse_QNAME, ChkOneCertResponse.class, null, value);
    }

}
