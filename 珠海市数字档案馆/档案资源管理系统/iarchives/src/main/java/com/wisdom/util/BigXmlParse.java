package com.wisdom.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanly on 2018/8/27 0027.
 */
public class BigXmlParse extends DefaultHandler {
    private String currentTag = "";
    private List<Map<String, Object>> dataList = new ArrayList<>();
    private Map<String, Object> dataMap = new HashMap<>();
    private StringBuilder sb = new StringBuilder();
    private String table = "";

    public List<Map<String, Object>> getDataList() {
        return dataList;
    }

    public String getTable() {
        return table;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("table".equals(qName)) {
            table = attributes.getValue(0);//只有一个：name
        } else if ("row".equals(qName)) {
            dataMap = new HashMap<>();
            return;
        }
        currentTag = qName;
        sb = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String value = new String(ch, start, length);
        sb.append(value);//分多次调用characters方法，差分了，因此合并回来
        if (!value.trim().equals("")) {//if(!StringUtils.isBlank(value)){
            dataMap.put(currentTag, sb.toString());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("row".equals(qName)) {
            dataList.add(dataMap);
        }
    }
}
