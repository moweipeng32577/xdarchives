package com.wisdom.util;



import com.wisdom.web.entity.cn.gov.saac.standards.erm.Sip;
import com.wisdom.web.entity.cn.gov.saac.standards.erm.StandardHandler;
import sun.misc.BASE64Decoder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.wisdom.web.entity.sip.Sip;
//import com.wisdom.web.entity.sip.Sips;

/**
 * Created by SunK on 2020/7/8 0008.
 */
public class SipReceiveBS {

    private static String workpathStr = null;
    private static String xmlfilename = null;
    private static String doingfilepath = null;
    private static Map<String, Map> archivalcodeMap = null;
    private static Map<String, Map> contentMap = null;


    public static Sip zipManager(File xmlfiles)
             {
        long start = System.currentTimeMillis();
        Sip p2 = null;
        if (xmlfiles != null) {
            File xmlfile = xmlfiles;
            p2 = null;
            try {
                JAXBContext context = JAXBContext.newInstance(new Class[]{Sip.class});
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                Unmarshaller um = context.createUnmarshaller();
                p2 = (Sip) um.unmarshal(xmlfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("元数据文件缺失");
        }
        long end = System.currentTimeMillis();
        long use = end - start;
        double usetime = use / 1000.0D;
        return p2;
    }


    public static void main(String[] args) {
        File zipFile = new File("D://3333//20200709_1163784.zip");
        try {
            zipManager(zipFile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


/**
    private boolean buildSql(Sips p2, Map<String, String> xmlInfoMap, Map<String, String> mExceMap, Map<String, String> fileExceMap)
            throws Exception {
        boolean isexistUnmatch = false;

        Map txtmdVO = new HashMap();
        if ((p2.getErCode() != null) && (!"".equals(p2.getErCode()))) {
            txtmdVO.put("m7", p2.getErCode());
        }
        String unitname = p2.getUnitName();
        txtmdVO.put("m6", unitname);
        xmlInfoMap.put("organize", unitname);
        String sourceSystem = p2.getIsDescription();
        txtmdVO.put("m51", sourceSystem);
        if (p2.getArchivalCode() != null) {
            List list = p2.getArchivalCode().getContent();
            if ("".equals(list.get(0).toString().trim())) {
                txtmdVO.put("M8".toLowerCase(), list.get(0).toString().trim());
            }
            JAXBElement je = null;
            Map archivalcode = new HashMap();
            for (Iterator it = list.iterator(); it.hasNext(); ) {
                try {
                    je = (JAXBElement) it.next();
                } catch (Exception localException1) {

                }
                if (je != null) {
                    String scode = je.getName().toString().replace("{http://www.saac.gov.cn/standards/ERM}", "");
                    archivalcode.put(scode, je.getValue().toString());
                }
            }
            Set set = this.archivalcodeMap.entrySet();
            Iterator it = set.iterator();
            if (it.hasNext()) {
                while (it.hasNext()) {
                    Map.Entry me = (Map.Entry) it.next();
                    String scode = me.getKey().toString();
                    Map mMap = (Map) this.archivalcodeMap.get(scode);
                    String value = (String) archivalcode.get(scode);
                    boolean ischecked = checkStriction(mMap, value, mExceMap);
                    if (!ischecked) {
//                        addExceptionItems(mMap.get("name").toString(), mMap.get("title").toString(), "1000", mMap.get("title") + (String) errorinfo.get(mExceMap.get(mMap.get("sipcode"))), ((String) mExceMap.get(mMap.get("sipcode"))).toString(), xmlInfoMap);
                        isexistUnmatch = true;
                    }
                    if (!isexistUnmatch) {
                        if (value != null) {
                            txtmdVO.put(mMap.get("name").toString().toLowerCase(), value);
                        }
                    }
                }
            }
        }
        Content content = p2.getContent();
        Set set = this.contentMap.entrySet();
        Iterator it = set.iterator();
        if (it.hasNext()) {
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();

                String scode = me.getKey().toString();
                if ("descriptor".equals(scode)) {
                    if ((content.getDescriptor() != null) && (content.getDescriptor().size() > 0)) {
                        String descript = "";
                        List dlist = content.getDescriptor();
                        for (Iterator dit = dlist.iterator(); dit.hasNext(); ) {
                            descript = descript + "," + dit.next();
                        }
                        txtmdVO.put("m26".toLowerCase(), descript.substring(1));
                    }
                } else if ("keyword".equals(scode)) {
                    if ((content.getKeyword() != null) && (content.getKeyword().size() > 0)) {
                        String descript = "";
                        List dlist = content.getKeyword();
                        for (Iterator dit = dlist.iterator(); dit.hasNext(); ) {
                            descript = descript + "," + dit.next();
                        }
                        txtmdVO.put("m27".toLowerCase(), descript.substring(1));
                    }
                } else if ("content".equals(scode)) {
                    continue;
                } else {
                    Map mMap = (Map) this.contentMap.get(scode);
                    String value = (String) this.dataServiceDAO.getPoFieldValue(scode.toString().replace("_", ""), content);
                    boolean ischecked = checkStriction(mMap, value, mExceMap);
                    if (!ischecked) {
                        isexistUnmatch = true;
//                        addExceptionItems(mMap.get("name").toString(), mMap.get("title").toString(), "1000", mMap.get("title") + (String) errorinfo.get(mExceMap.get(mMap.get("sipcode"))), ((String) mExceMap.get(mMap.get("sipcode"))).toString(), xmlInfoMap);
                    }
                    if (!isexistUnmatch) {
                        if (value != null) {
                            txtmdVO.put(mMap.get("name").toString().toLowerCase(), value);
                        }
                    }
                }

            }

        }
        if (!isexistUnmatch) {
            this.dataServiceDAO.getDB().getConnection().setAutoCommit(false);
//            String etmid = this.dataServiceDAO.getMaxId();
            txtmdVO.put("id", etmid);
            txtmdVO.put("erid", etmid);
            txtmdVO.put("sortid", "1110211155530932002");
            txtmdVO.put("m14", "");// TODO: 2020/7/8 0008  m14 设置成null
            this.dataServiceDAO.addVO("T_ER_TXTMD", txtmdVO);

            List dlist = p2.getRecordBlock().getDocument();
            for (it = dlist.iterator(); it.hasNext(); ) {
                com.wisdom.web.entity.cn.gov.saac.standards.erm.Document document = (com.wisdom.web.entity.cn.gov.saac.standards.erm.Document) it.next();
                String filename = document.getFileName();

                String esfid = "false";

                String listid = this.dataServiceDAO.getMaxId();
                Map stdVO = new HashMap();
                stdVO.put("id", listid);
                stdVO.put("filename", filename);
                stdVO.put("fileformat", document.getMime() == null ? filename.substring(filename.indexOf(".")) : document.getMime());
                stdVO.put("filesize", Long.valueOf(document.getFileSize()));
                if ((document.getFileHash() != null) && (!"".equals(document.getFileHash()))) {
                    stdVO.put("hashcode", document.getFileHash());
                }
                stdVO.put("erid", etmid);

                this.dataServiceDAO.addVO("T_ER_SRCLIST", stdVO);
                if ((document.isEmbedded() == null) || (document.isEmbedded().booleanValue())) {
                    String base64Str = document.getFileContent();
                    if ((base64Str == null) || ("".equals(base64Str.trim()))) {
                        byte[] buffer = decoderBase64(base64Str);
                        try {
                            esfid = this.dataServiceDAO.base64MediaSave(buffer, listid, "T_ER_SRCLIST", "data");
                            String tranid = this.dataServiceDAO.getMaxId();
                            String sql = "insert into t_er_trans(id,etrid,transtype,relationtable,relationdata,createtime,createusercode,transstatus,transtitle)values('%1$s','%1$s','%2$s','T_ER_SRCLIST','%3$s','%4$s','erms','0','%5$s')";
                            this.dataServiceDAO.getDB().execute(String.format(sql, new Object[]{tranid, Integer.valueOf(0), listid, DateUtil.getTimeStandardStr(new Date()), filename + "数据标准化任务"}));
                        } catch (Exception e) {
                            fileExceMap.put(filename, filename + "文件存在问题");
//                            addExceptionItems("file", filename, "4000", filename + "文件缺失或者存在问题", null, xmlInfoMap);
                            isexistUnmatch = true;
                        }
                    } else {
                        fileExceMap.put(filename, filename + "文件缺失");
                        isexistUnmatch = true;
                    }
                } else {
                    File file = new File(this.doingfilepath + "/document/" + filename);
                    if (file.isFile()) {
                        FileInputStream in = new FileInputStream(file);
                        try {
                            esfid = this.dataServiceDAO.saveFileToDataColumn(in, "id", listid, "T_ER_SRCLIST", "data");

                            String tranid = this.dataServiceDAO.getMaxId();
                            String sql = "insert into t_er_trans(id,etrid,transtype,relationtable,relationdata,createtime,createusercode,transstatus,transtitle)values('%1$s','%1$s','%2$s','T_ER_SRCLIST','%3$s','%4$s','erms','0','%5$s')";

                            this.dataServiceDAO.getDB().execute(String.format(sql, new Object[]{tranid, Integer.valueOf(0), listid, DateUtil.getTimeStandardStr(new Date()), filename + "数据标准化任务"}));
                        } catch (Exception e) {
                            fileExceMap.put(filename, filename + "文件存在问题");
                            isexistUnmatch = true;
                        }
                    } else {
                        fileExceMap.put(filename, filename + "文件缺失");
                        isexistUnmatch = true;
                    }
                }

            }

            String updateLog = "update t_er_receivezips set m6='%s',m22='%s',erid='%s' where systemname='%s' and sourceid='%s'";
            this.dataServiceDAO.getDB().execute(String.format(updateLog, new Object[]{
                    p2.getUnitName(), p2.getContent().getTitle(), etmid,
                    xmlInfoMap.get("systemname"), xmlInfoMap.get("sourceid")}));

            if (isexistUnmatch) {
                this.dataServiceDAO.getDB().getConnection().rollback();
                set = fileExceMap.entrySet();
                it = set.iterator();
                if (it.hasNext()) {
                    while (it.hasNext()) {
                        Map.Entry me = (Map.Entry) it.next();
//                        addExceptionItems("file", me.getKey().toString(), "4000", me.getValue().toString(), null, xmlInfoMap);
                    }
                }
            }
//            this.dataServiceDAO.getDB().getConnection().commit();
//            this.dataServiceDAO.getDB().getConnection().setAutoCommit(true);
        }
        return isexistUnmatch;
    }
**/
    public static byte[] decoderBase64(String base64Code) throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        return buffer;
    }

    private boolean checkStriction(Map map, String value, Map<String, String> mExceMap) {
        boolean isChecked = true;
        StandardHandler ud = StandardHandler.getInstance();

        String fieldname = map.get("sipcode").toString();

        if (!"1".equals(map.get("cannull"))) {
            if ((value == null) || ("".equals(value))) {
                mExceMap.put(fieldname, "1");
                isChecked = false;
                return isChecked;
            }

        } else if ((value == null) || ("".equals(value))) {
            return isChecked;
        }

        if ((map.get("fielddomain") != null) && (!"".equals(map.get("fielddomain").toString().trim()))) {
            if ((value.indexOf(",") >= 0) || (map.get("fielddomain").toString().indexOf(value) < 0)) {
                mExceMap.put(fieldname, "2");
                isChecked = false;
                return isChecked;
            }
        }

        if ((map.get("dataformat") != null) && (!"".equals(map.get("dataformat").toString()))) {
            Pattern pattern = Pattern.compile(map.get("dataformat").toString());
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                mExceMap.put(fieldname, "3");
                isChecked = false;
                return isChecked;
            }
        }

        if ("number".equals(map.get("datatype"))) {
            Double d = null;
            try {
                d = Double.valueOf(Double.parseDouble(value));
            } catch (Exception localException) {
            }
            if (d == null) {
                mExceMap.put(fieldname, "4");
                isChecked = false;
                return isChecked;
            }
        }
        if ("date".equals(map.get("datatype"))) {
            String date = null;
            try {
                date = ud.toDate(value);
            } catch (Exception localException1) {
            }
            if (date == null) {
                mExceMap.put(fieldname, "4");
                isChecked = false;
                return isChecked;
            }
        }

        int len = Integer.parseInt(map.get("datalength").toString());
        if (value.length() > len) {
            mExceMap.put(fieldname, "5");
            isChecked = false;
            return isChecked;
        }

        return isChecked;
    }

}
