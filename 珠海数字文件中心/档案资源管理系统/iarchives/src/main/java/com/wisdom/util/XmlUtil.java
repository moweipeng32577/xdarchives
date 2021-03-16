package com.wisdom.util;

import com.wisdom.web.entity.*;
import org.codehaus.groovy.util.ListHashMap;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPMessage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by SunK on 2018/7/23 0023.
 */
public class XmlUtil {


    /**
     * 导入、导出 xml 1.需要的参数：文件名，文件存放路径，数据集
     */

    // --生成日志xml
    public static void createXml(String xmlname, int maxcount, int rows, List<Object> list) {
        // 1.数据模板
        String fieldcode = "username,realname,ipaddress,dotime,dodesc,functionname"; // 字段名
        // xml节点id
        String fieldname = "用户名,姓名,IP地址,操作时间,功能描述,功能"; // 字段描述名
        String[] fieldcodearray = fieldcode.split(",");
        String[] fieldnamearray = fieldname.split(",");
        String dir = ConfigValue.getValue("XD.ftpjdpath");
        String tableName = "需要的数据-表名";// ------需要的数据-表名

        Document document = DocumentHelper.createDocument();// 创建Doucument对象
        Element root = document.addElement("table"); // 根节点
        root.addAttribute("tablename", tableName);
        // 创建临时目录
        String path = dir + "/xml导出/临时目录/" + xmlname;
        // zip 完整路径
        String zippath = dir + "/xml导出/" + xmlname + ".zip";
        // 创建临时路径文件夹
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        // 2.数据集
        // List<Object> list = new
        // ArrayList<Object>();//集合的key为fieldcodearry数组中的一个
        // 3.需要的参数用来循环（数据集的总条数，以及页数）

        // 4.循环
        if (maxcount > 0) {
            for (int i = 0; i < rows; i++) {// 数据集的条数
                if (!list.isEmpty() && list != null) {
                    // 循环写入一条数据
                    for (int j = 0; j < list.size(); j++) {
                        Element record = root.addElement("record");// 创建record节点
                        record.addAttribute("tablename", tableName);// 给record添加属性名字和属性值
                        // 循环读取每一个字段
                        for (int k = 0; k < fieldcodearray.length; k++) {// 读取字段名
                            String value = "节点的参数";
                            Element field = record.addElement(fieldnamearray[k]);
                            field.addAttribute("fieldcode", fieldcodearray[k]);
                            field.addText(value);// 给节点添加文本内容
                        }
                    }
                }

                // 将document 数据写入xml文件
                File xmlFile = null;
                XMLWriter writer = null;
                xmlFile = new File(path + "/" + xmlname + "_" + (i + 1) + ".xml");// 生成xml文件
                try {
                    writer = new XMLWriter(new FileOutputStream(xmlFile));
                    writer.write(document);
                } catch (IOException e) {
                    throw new RuntimeException("创建xml失败");
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (Exception e) {
                            throw new RuntimeException("createXml()关闭流失败");
                        }
                    }
                }
                // 重新创建xml文件
                document = DocumentHelper.createDocument();
                root = document.addElement("table"); // 根节点
                root.addAttribute("tablename", tableName);
            }
        }
    }

    /**
     * 导出条目信息 生成xml文件
     *
     * @param fieldcodearry 模板集合
     * @param fieldnamearry 模板集合
     * @param objectList    数据集
     * @param filename      表名、文件名
     * @throws Exception
     */
    public static void exportXml(String[] fieldnamearry, String[] fieldcodearry, List<Entry> objectList,
                                 String filename) throws Exception {

        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/xml导出/临时目录/" + filename;
        // zip 完整路径
        String zippath = dir + "/OAFile" + "/xml导出/" + filename + ".zip";
        // 创建临时路径文件夹
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        // 创建xml
        Document document = DocumentHelper.createDocument();// 创建Doucument对象
        Element root = document.addElement("table"); // 根节点
        root.addAttribute("tablename", filename);

        for (int i = 0; i < objectList.size(); i++) {// 遍历数据集
            Entry entry = (Entry) objectList.get(i);
            Element record = root.addElement("record");// 创建record节点
            for (int j = 0; j < fieldcodearry.length; j++) {
                String value = String.valueOf(ValueUtil.getPoFieldValue(fieldcodearry[j], entry));
                if (fieldnamearry[j].contains("/")) {
                    fieldnamearry[j] = fieldnamearry[j].replace("/", "或"); // 里面有斜杠
                    // 需要改一下
                }
                /*
                 * if(fieldnamearry[j].contains("(")){
                 *
                 * fieldnamearry[j] =fieldnamearry[j].replace("(",""); //
                 * 里面有括号需要改一下 fieldnamearry[j]
                 * =fieldnamearry[j].replace(")",""); }
                 */
                Element field = record.addElement(fieldcodearry[j]);
                field.addAttribute("property", ConfigValue.getFieldProperty(fieldcodearry[j].toUpperCase()));
                field.addAttribute("fieldname", fieldnamearry[j]);
                field.addText(value);// 给节点添加文本内容
            }
        }
        // 生成xml文件
        File xmlFile = null;

        String newPath = path + "/" + filename + ".xml";
        // 得到一个新xml文件
        xmlFile = new File(newPath);
        // 能输出流写入新xml文件
        XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile));
        writer.write(document);
        writer.close();
    }

    public static void captureExportXml(String[] fieldnamearry, String[] fieldcodearry, List<EntryCapture> objectList,
                                        String filename, HttpServletResponse response, List<AcceptEntryCapture> acceptList,
                                        String exporttype, List<ManageEntry> manageEntries) throws Exception {

        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/xml导出/临时目录/" + filename;
        // zip 完整路径
        String zippath = dir + "/OAFile" + "/xml导出/" + filename + ".zip";
        // 创建临时路径文件夹
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        // 创建xml
        Document document = DocumentHelper.createDocument();// 创建Doucument对象
        Element root = document.addElement("table"); // 根节点
        root.addAttribute("tablename", filename);

        if (exporttype != null && "accept".equals(exporttype)) { //判断是否目录接收导出
            for (int i = 0; i < acceptList.size(); i++) {// 遍历数据集
                AcceptEntryCapture entry = (AcceptEntryCapture) acceptList.get(i);
                Element record = root.addElement("record");// 创建record节点
                for (int j = 0; j < fieldcodearry.length; j++) {
                    String value = String.valueOf(ValueUtil.getPoFieldValue(fieldcodearry[j], entry));
                    if (fieldnamearry[j].contains("/")) {
                        fieldnamearry[j] = fieldnamearry[j].replace("/", "或"); // 里面有斜杠
                        // 需要改一下
                    }
                    /*
                     * if(fieldnamearry[j].contains("(")){
                     *
                     * fieldnamearry[j] =fieldnamearry[j].replace("(",""); //
                     * 里面有括号需要改一下 fieldnamearry[j]
                     * =fieldnamearry[j].replace(")",""); }
                     */
                    Element field = record.addElement(fieldcodearry[j]);
                    field.addAttribute("property", ConfigValue.getFieldProperty(fieldcodearry[j].toUpperCase()));
                    field.addAttribute("fieldname", fieldnamearry[j]);
                    field.addText(value);// 给节点添加文本内容
                }
            }
        } else if (exporttype != null && "manage".equals(exporttype)) { //判断是否目录管理导出
            for (int i = 0; i < manageEntries.size(); i++) {// 遍历数据集
                ManageEntry entry = (ManageEntry) manageEntries.get(i);
                Element record = root.addElement("record");// 创建record节点
                for (int j = 0; j < fieldcodearry.length; j++) {
                    String value = String.valueOf(ValueUtil.getPoFieldValue(fieldcodearry[j], entry));
                    if (fieldnamearry[j].contains("/")) {
                        fieldnamearry[j] = fieldnamearry[j].replace("/", "或"); // 里面有斜杠
                        // 需要改一下
                    }
                    /*
                     * if(fieldnamearry[j].contains("(")){
                     *
                     * fieldnamearry[j] =fieldnamearry[j].replace("(",""); //
                     * 里面有括号需要改一下 fieldnamearry[j]
                     * =fieldnamearry[j].replace(")",""); }
                     */
                    Element field = record.addElement(fieldcodearry[j]);
                    field.addAttribute("property", ConfigValue.getFieldProperty(fieldcodearry[j].toUpperCase()));
                    field.addAttribute("fieldname", fieldnamearry[j]);
                    field.addText(value);// 给节点添加文本内容
                }
            }
        } else {
            for (int i = 0; i < objectList.size(); i++) {// 遍历数据集
                EntryCapture entry = (EntryCapture) objectList.get(i);
                Element record = root.addElement("record");// 创建record节点
                for (int j = 0; j < fieldcodearry.length; j++) {
                    String value = String.valueOf(ValueUtil.getPoFieldValue(fieldcodearry[j], entry));
                    if (fieldnamearry[j].contains("/")) {
                        fieldnamearry[j] = fieldnamearry[j].replace("/", "或"); // 里面有斜杠
                        // 需要改一下
                    }
                    /*
                     * if(fieldnamearry[j].contains("(")){
                     *
                     * fieldnamearry[j] =fieldnamearry[j].replace("(",""); //
                     * 里面有括号需要改一下 fieldnamearry[j]
                     * =fieldnamearry[j].replace(")",""); }
                     */
                    Element field = record.addElement(fieldcodearry[j]);
                    field.addAttribute("property", ConfigValue.getFieldProperty(fieldcodearry[j].toUpperCase()));
                    field.addAttribute("fieldname", fieldnamearry[j]);
                    field.addText(value);// 给节点添加文本内容
                }
            }
        }

        // 生成xml文件
        File xmlFile = null;

        String newPath = path + "/" + filename + ".xml";
        // 得到一个新xml文件
        xmlFile = new File(newPath);
        // 能输出流写入新xml文件
        XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile));
        writer.write(document);
        writer.close();
		/*boolean bool = false;
		OutputStream fileOutputStream = new FileOutputStream(new File(zippath));
		bool = ZipUtils.toZip(path, fileOutputStream, true);
		if (bool) {
			InputStream inputStream = new FileInputStream(new File(zippath));
			OutputStream out = response.getOutputStream();
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=\"" + new String((filename + ".zip").getBytes(), "iso-8859-1") + "\"");
			byte[] b = new byte[1024 * 1024 * 10];
			int leng = 0;
			while ((leng = inputStream.read(b)) != -1) {
				out.write(b, 0, leng);
			}
			out.flush();
			inputStream.close();
			out.close();
		}
		ZipUtils.del(path);
		new File(zippath).delete();*/
    }

    // 创建导入失败的xml
    public static void CreateFailureXml(String[] fieldnamearry, String[] fieldcodearry, List<Entry> objectList,
                                        String filename) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile/" + "导入失败/" + filename;
        // 创建临时路径文件夹
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        // 创建xml
        Document document = DocumentHelper.createDocument();// 创建Doucument对象
        Element root = document.addElement("table"); // 根节点
        root.addAttribute("tablename", filename);

        for (int i = 0; i < objectList.size(); i++) {// 遍历数据集
            Entry entry = (Entry) objectList.get(i);
            Element record = root.addElement("record");// 创建record节点
            for (int j = 0; j < fieldcodearry.length; j++) {
                String value = String.valueOf(ValueUtil.getPoFieldValue(fieldcodearry[j], entry));
                if (fieldnamearry[j].contains("/")) {
                    fieldnamearry[j] = fieldnamearry[j].replace("/", "或"); // 里面有斜杠
                    // 需要改一下
                }
                /*
                 * if(fieldnamearry[j].contains("(")){
                 *
                 * fieldnamearry[j] =fieldnamearry[j].replace("(",""); //
                 * 里面有括号需要改一下 fieldnamearry[j]
                 * =fieldnamearry[j].replace(")",""); }
                 */
                Element field = record.addElement(fieldcodearry[j]);
                field.addAttribute("property", ConfigValue.getFieldProperty(fieldcodearry[j].toUpperCase()));
                field.addAttribute("fieldname", fieldnamearry[j]);
                field.addText(value);// 给节点添加文本内容
            }
        }
        // 生成xml文件
        File xmlFile = null;

        String newPath = path + "/" + filename + ".xml";
        // 得到一个新xml文件
        xmlFile = new File(newPath);
        // 能输出流写入新xml文件
        XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile));
        writer.write(document);
        writer.flush();
        writer.close();
    }

    // 数据采集--创建导入失败的xml
    public static void captureCreateFailureXml(String[] fieldnamearry, String[] fieldcodearry,
                                               List<EntryCapture> objectList, String filename, List<AcceptEntryCapture> acceptEntry, String importtype)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "导入失败/" + filename;
        // 创建临时路径文件夹
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        // 创建xml
        Document document = DocumentHelper.createDocument();// 创建Doucument对象
        Element root = document.addElement("table"); // 根节点
        root.addAttribute("tablename", filename);

        if (importtype != null && "accept".equals(importtype)) { //判断是否目录导入
            for (int i = 0; i < acceptEntry.size(); i++) {// 遍历数据集
                AcceptEntryCapture entry = (AcceptEntryCapture) acceptEntry.get(i);
                Element record = root.addElement("record");// 创建record节点
                for (int j = 0; j < fieldcodearry.length; j++) {
                    String value = String.valueOf(ValueUtil.getPoFieldValue(fieldcodearry[j], entry));
                    if (fieldnamearry[j].contains("/")) {
                        fieldnamearry[j] = fieldnamearry[j].replace("/", "或"); // 里面有斜杠
                        // 需要改一下
                    }
                    /*
                     * if(fieldnamearry[j].contains("(")){
                     *
                     * fieldnamearry[j] =fieldnamearry[j].replace("(",""); //
                     * 里面有括号需要改一下 fieldnamearry[j]
                     * =fieldnamearry[j].replace(")",""); }
                     */
                    Element field = record.addElement(fieldcodearry[j]);
                    field.addAttribute("property", ConfigValue.getFieldProperty(fieldcodearry[j].toUpperCase()));
                    field.addAttribute("fieldname", fieldnamearry[j]);
                    field.addText(value);// 给节点添加文本内容
                }
            }
        } else {
            for (int i = 0; i < objectList.size(); i++) {// 遍历数据集
                EntryCapture entry = (EntryCapture) objectList.get(i);
                Element record = root.addElement("record");// 创建record节点
                for (int j = 0; j < fieldcodearry.length; j++) {
                    String value = String.valueOf(ValueUtil.getPoFieldValue(fieldcodearry[j], entry));
                    if (fieldnamearry[j].contains("/")) {
                        fieldnamearry[j] = fieldnamearry[j].replace("/", "或"); // 里面有斜杠
                        // 需要改一下
                    }
                    /*
                     * if(fieldnamearry[j].contains("(")){
                     *
                     * fieldnamearry[j] =fieldnamearry[j].replace("(",""); //
                     * 里面有括号需要改一下 fieldnamearry[j]
                     * =fieldnamearry[j].replace(")",""); }
                     */
                    Element field = record.addElement(fieldcodearry[j]);
                    field.addAttribute("property", ConfigValue.getFieldProperty(fieldcodearry[j].toUpperCase()));
                    field.addAttribute("fieldname", fieldnamearry[j]);
                    field.addText(value);// 给节点添加文本内容
                }
            }
        }

        // 生成xml文件
        File xmlFile = null;

        String newPath = path + "/" + filename + ".xml";
        // 得到一个新xml文件
        xmlFile = new File(newPath);
        // 能输出流写入新xml文件
        XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile));
        writer.write(document);
        writer.flush();
        writer.close();
    }

    // 创建导入失败后生成的xml
    public static void createFailureXml(List<String> archivecodes, String fileName) throws IOException {
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "导入失败/" + fileName;
        // 创建临时路径文件夹
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        // 创建xml
        Document document = DocumentHelper.createDocument();// 创建Doucument对象
        Element root = document.addElement("table"); // 根节点
        root.addAttribute("tablename", fileName);
        for (int i = 0; i < archivecodes.size(); i++) {
            Element record = root.addElement("record");// 创建record节点
            Element field = record.addElement("archivecode");
            field.addAttribute("fieldname", "档号");
            field.addText(archivecodes.get(i));

        }
        // 生成xml文件
        File xmlFile = null;

        String newPath = path + "/" + fileName + ".xml";
        // 得到一个新xml文件
        xmlFile = new File(newPath);
        // 能输出流写入新xml文件
        XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile));
        writer.write(document);
        writer.flush();
        writer.close();
    }

    // 解析xml
    public static List<List<String>> readXml(String xmlFilePath) {
        // 1.创建对象reader
        SAXReader reader = new SAXReader();
        List<List<String>> lists = new ArrayList<>();
        try {
            // 2.通过reader对象加载xml文件，生成document对象
            Document document = reader.read(new File(xmlFilePath));
            // 3.通过document对象获取跟节点
            Element element = document.getRootElement();
            // 4.创建集合存放解析后的数据
            lists = new ArrayList<>();
            // 5.获取table下面的子节点
            List<Element> elements = element.elements();
            for (int i = 0; i < elements.size(); i++) {// record
                List<String> list = new ArrayList<>();
                List<Element> list1 = elements.get(i).elements();
                for (int j = 0; j < list1.size(); j++) {
                    list.add(list1.get(j).getStringValue());
                }
                lists.add(list);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return lists;
    }


    public static LinkedHashMap<String, String> getXmlFieldCodeAndFieldName(String xmlFilePath) {
        //1.创建sax对象
        SAXReader reader = new SAXReader();
        //List<LinkedHashMap<String,String>> linkedHashMapList = new ArrayList<>();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            Document document = reader.read(new File(xmlFilePath));
            Element element = document.getRootElement();
            List<Element> elements = element.elements();
            //for(int i=0;i<elements.size();){// record 条目数

            List<Element> list = elements.get(0).elements();
            for (int j = 0; j < list.size(); j++) {
                map.put(list.get(j).getName(), list.get(j).getStringValue());
            }
            //linkedHashMapList.add(map);
            //}
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //2.从跟节点开始读取
        //3.
        return map;
    }

    public static Map<String, Object> readDateXlsx(String xmlFilePath) throws IOException {
        Map<String, Object> resMap = new ListHashMap<>();
        int rowCount = 0;
        resMap.put("success", true);
        // 1.创建对象reader
        SAXReader reader = new SAXReader();
        try {
            // 2.通过reader对象加载xml文件，生成document对象
            Document document = reader.read(new File(xmlFilePath));
            // 3.通过document对象获取跟节点
            Element element = document.getRootElement();
            // 4.创建集合存放解析后的数据
            List lists = new ArrayList<>();
            List codeLists = new ArrayList<>();
            // 5.获取table下面的子节点
            List<Element> elements = element.elements();
            rowCount = elements.size();
            int forCount = 10;
            if (elements.size() <= 10) {
                forCount = elements.size();
            }
            for (int i = 0; i < forCount; i++) {// record
                List<String> list = new ArrayList<>();
                List<Element> list1 = elements.get(i).elements();//fieldcod/fieldname集合
                for (int j = 0; j < list1.size(); j++) {
                    list.add(list1.get(j).getStringValue());
                    if (codeLists.size() < list1.size()) {
//                        codeLists.add(list1.get(j).attributeValue("fieldname"));
                        String newstr = null == list1.get(j).attributeValue("fieldname") ? list1.get(j).getName() : list1.get(j).attributeValue("fieldname");
                        codeLists.add(newstr);
                        if ("wg11".equalsIgnoreCase(newstr)) {
                            resMap.put("wg11Index", j);
                        }
                    }
                }
                String[] stringss = new String[list.size()];
                list.toArray(stringss);
                lists.add(stringss);
            }
            String[] codeString = new String[codeLists.size()];
            codeLists.toArray(codeString);
            resMap.put("header", codeLists);
            resMap.put("data", lists);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        resMap.put("rowCount", rowCount);
        return resMap;
    }

    //----------------------------解析AO数据包中的xml文件------------------------------
    public static Map<String, String> parseSoapMessage(File xmlFile) {
        // 1.创建对象reader
        SAXReader reader = new SAXReader();
        Map map = new HashMap();
        try {
            // 2.通过reader对象加载xml文件，生成document对象
            Document document = reader.read(xmlFile);
            // 3.通过document对象获取跟节点
            Element element = document.getRootElement();    //Envelope主节点
            getNodes(element, map);//迭代遍历
            System.out.println(map.size());
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 从指定节点开始,递归遍历所有子节点
     */
    public static void getNodes(Element node, Map map) {
        //System.out.println("--------------------");

        //当前节点的名称、文本内容和属性
        //System.out.println("当前节点名称：" + node.getName());//当前节点名称
        //System.out.println("当前节点的内容：" + node.getTextTrim());//当前节点名称
        map.put(node.getName(), node.getTextTrim());
        List<Attribute> listAttr = node.attributes();//当前节点的所有属性的list
        for (Attribute attr : listAttr) {//遍历当前节点的所有属性
            String name = attr.getName();//属性名称
            String value = attr.getValue();//属性的值
            //System.out.println("属性名称：" + name + "属性值：" + value);
            map.put(name, value);
        }
        //递归遍历当前节点所有的子节点
        List<Element> listElement = node.elements();//所有一级子节点的list
        for (Element e : listElement) {//遍历所有一级子节点
            getNodes(e, map);//递归
        }
    }


    // 创建导入失败的xml
    public static void CreateCalloutFailureXml(String[] fieldnamearry, String[] fieldcodearry, List<Szh_RebackImport> objectList,
                                               String filename) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile/" + "导入失败/" + filename;
        // 创建临时路径文件夹
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        // 创建xml
        Document document = DocumentHelper.createDocument();// 创建Doucument对象
        Element root = document.addElement("table"); // 根节点
        root.addAttribute("tablename", filename);

        for (int i = 0; i < objectList.size(); i++) {// 遍历数据集
            Szh_RebackImport entry = (Szh_RebackImport) objectList.get(i);
            Element record = root.addElement("record");// 创建record节点
            for (int j = 0; j < fieldcodearry.length; j++) {
                String value = String.valueOf(ValueUtil.getPoFieldValue(fieldcodearry[j], entry));
                if (fieldnamearry[j].contains("/")) {
                    fieldnamearry[j] = fieldnamearry[j].replace("/", "或"); // 里面有斜杠
                    // 需要改一下
                }
                /*
                 * if(fieldnamearry[j].contains("(")){
                 *
                 * fieldnamearry[j] =fieldnamearry[j].replace("(",""); //
                 * 里面有括号需要改一下 fieldnamearry[j]
                 * =fieldnamearry[j].replace(")",""); }
                 */
                Element field = record.addElement(fieldcodearry[j]);
                field.addAttribute("property", ConfigValue.getFieldProperty(fieldcodearry[j].toUpperCase()));
                field.addAttribute("fieldname", fieldnamearry[j]);
                field.addText(value);// 给节点添加文本内容
            }
        }
        // 生成xml文件
        File xmlFile = null;

        String newPath = path + "/" + filename + ".xml";
        // 得到一个新xml文件
        xmlFile = new File(newPath);
        // 能输出流写入新xml文件
        XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile));
        writer.write(document);
        writer.flush();
        writer.close();
    }

    public static Map<String, Object> readsocialSecurityXml(String xmlFilePath) throws IOException {
        Map<String, Object> resMap = new ListHashMap<>();

        resMap.put("success", true);
        // 1.创建对象reader
        SAXReader reader = new SAXReader();
        try {
            // 2.通过reader对象加载xml文件，生成document对象
            Document document = reader.read(new File(xmlFilePath));
            // 3.通过document对象获取跟节点
            Element element = document.getRootElement();
            // 4.创建集合存放解析后的数据
            List lists = new ArrayList<>();
            List codeLists = new ArrayList<>();
            // 5.获取table下面的子节点
            List<Element> elements = element.elements();
            for (int i = 0; i < elements.size(); i++) {// record
                List<String> list = new ArrayList<>();
                List<Element> list1 = elements.get(i).elements();//fieldcod/fieldname集合
                for (int j = 0; j < list1.size(); j++) {
                    if ("RECORD".equalsIgnoreCase(list1.get(j).getName())) {
                        List<Element> list2 = list1.get(j).elements();
                        for (int k = 0; k < list2.size(); k++) {
                            list.add(list2.get(k).getStringValue());
                            if (codeLists.size() < list2.size()) {
                                String newstr = list2.get(k).getName();
                                codeLists.add(newstr);
                                if ("wg11".equalsIgnoreCase(newstr)) {
                                    resMap.put("wg11Index", k);
                                }
                            }
                        }
                    }
                }
                String[] stringss = new String[list.size()];
                list.toArray(stringss);
                lists.add(stringss);
            }
            String[] codeString = new String[codeLists.size()];
            codeLists.toArray(codeString);
            resMap.put("header", codeLists);
            resMap.put("data", lists);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return resMap;
    }

    public static void main(String[] age) {
        String filepath = "F:\\新.xml";
        parseSoapMessage(new File(filepath));
    }
}
