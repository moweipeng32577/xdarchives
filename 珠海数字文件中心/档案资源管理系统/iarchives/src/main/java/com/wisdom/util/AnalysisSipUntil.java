package com.wisdom.util;

import com.wisdom.web.entity.sip.Sip;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yl on 2017/11/8. sip解析工具
 */
public class AnalysisSipUntil {
	
	private final Logger logger = LoggerFactory.getLogger(AnalysisSipUntil.class);

	private String fileXsdPath;
	private InputStream mInputStream;

	public AnalysisSipUntil() {
	}

	public AnalysisSipUntil(String xsdPath, InputStream inputStream) {
		fileXsdPath = xsdPath;
		mInputStream = inputStream;
	}

	public Sip getSipData() throws IOException, ParserConfigurationException, SAXException {
		Sip sip = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Sip.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setExpandEntityReferences(false);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(mInputStream);
			sip = (Sip) jaxbUnmarshaller.unmarshal(document);
		} catch (JAXBException e) {
			logger.info(e.getMessage());
		} finally {
			if (mInputStream != null) {
				mInputStream.close();
			}
		}
		return sip;
	}
}