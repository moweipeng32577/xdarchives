package com.wisdom.web.controller;

import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.codec.TiffImage;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_fulltext;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.UserDataNodeRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.FullSearchService;
import com.wisdom.web.service.UserService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全文检索控制器
 * Created by tanly on 2017/11/17 0017.
 */
@Controller
@RequestMapping(value = "/fullSearch")
public class FullSearchController {

    @Autowired
    private SolrClient client;

    @Autowired
    FullSearchService fullSearchService;

    @Autowired
    UserDataNodeRepository userDataNodeRepository;

    @Autowired
    UserService userService;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${system.openArchies.opened}")
    private String openArchies;   //是否开启仅显示本机构的开放档案

    @RequestMapping("/main")
    public String index(Model model) {
        model.addAttribute("iflag","gl");  //平台标志，gl为管理平台，ly为利用平台
        return "/inlet/fullSearch";
    }

    @RequestMapping("/mainly")
    public String indexly(Model model) {
        model.addAttribute("iflag","ly");  //平台标志，gl为管理平台，ly为利用平台
        return "/inlet/fullSearch";
    }

    @RequestMapping("/getFulltextByFilter")
    @ResponseBody
    public Page<Tb_fulltext> getFulltextByFilter(String filters, String oldparams, int page, int limit) {
        String[] filterchar = new String[]{")", "(", "\'", "〕", "〔", "[", "]", "\"", ",", "，"};//过滤的字符
        if (filters != null && filters.length() != 0) {
            filters = filterFormat(filterchar, filters);//格式化条件
            Page<Tb_fulltext> pagelist = fullSearchService.getFulltextByFilter(filters, page, limit);
            List<Tb_fulltext> list = pagelist.getContent();
            if (list.size() > 0) {
                if (oldparams != null && oldparams.length() != 0) {//结果中检索
                    oldparams = filterFormat(filterchar, oldparams);
                    List<Tb_fulltext> oldlist = fullSearchService.getFulltextByFilter(oldparams, page, limit).getContent();
                    list.retainAll(oldlist);
                }
                int sublen = 20;//截取条件前后的字符数
                int distance = 0;//区间相距distance以内视为有交集
                String[] filtersArr = filters.split(" ");//过滤条件数组
                for (int z = 0; z < list.size(); z++) {
                    Tb_fulltext fulltext = list.get(z);
                    String filetext = fulltext.getFiletext();//得到的全值
                    String columntext = "";//显示的列值
                    String allfilter = "";//全部条件
                    List sublist = new ArrayList();//截取的list
                    List marklist = new ArrayList();//标红的list
                    int[][] subarray;//截取的区间数组
                    int[][] markarray;//标红的区间数组

                    //==================截取=================start
                    for (int i = 0; i < filtersArr.length; i++) {//拼接起来截取
                        allfilter += filtersArr[i];
                    }
                    if (filetext.indexOf(allfilter) != -1) {
                        List templist = this.getSection(allfilter, filetext, sublen);//获得截取条件前后sublen长度的区间数组
                        if (templist.size() > 0)
                            sublist.addAll(templist);
                    } else {
                        for (int i = 0; i < filtersArr.length; i++) {
                            List templist = this.getSection(filtersArr[i], filetext, sublen);
                            if (templist.size() > 0)
                                sublist.addAll(templist);
                        }
                    }
                    subarray = this.sortArray(sublist);//根据开始index从小到大排序
//                    if (subarray.length == 0) {
//                        System.out.println("实际查到数量：" + list.size() + "条");
//                        list.remove(fulltext);
//                        z--;//remove后元素前移
//                        continue;
//                    }
                    sublist = this.arrangeArr(subarray, distance);
                    for (int i = 0; i < sublist.size(); i++) {
                        int[] arraytemp = (int[]) sublist.get(i);
                        if (arraytemp[0] != 0)
                            columntext += "...";
                        columntext += filetext.substring(arraytemp[0], arraytemp[1]) + "...";//截取字符串
                    }
                    columntext += "...";
                    //==================截取=================end

                    //==================标红=================start
                    for (int i = 0; i < filtersArr.length; i++) {
                        List templist = this.getSection(filtersArr[i], columntext, 0);
                        if (templist.size() > 0)
                            marklist.addAll(templist);
                    }
                    markarray = this.sortArray(marklist);//根据开始index从小到大排序
                    marklist = this.arrangeArr(markarray, 0);
                    for (int i = 0; i < marklist.size(); i++) {
                        int[] arraytemp = (int[]) marklist.get(i);
                        int num = 23 * i;//增加的标签长度
                        columntext = columntext.substring(0, arraytemp[0] + num)
                                + "<font color=red>"
                                + columntext.substring(arraytemp[0] + num, arraytemp[1] + num)
                                + "</font>"
                                + columntext.substring(arraytemp[1] + num);
//                        System.out.println("第" + (i + 1) + "次标红：" + columntext);
                    }
                    //==================标红=================end
                    fulltext.setFiletext(columntext);
                }
            }
            return pagelist;
        } else {
            return fullSearchService.getFulltextByFilter(filters, page, limit);
        }
    }

    private String filterFormat(String[] ch, String filters) {
        String[] filterArr = filters.split(" ");
        filters = "";
        for (int i = 0; i < filterArr.length; i++) {
            if ("".equals(filterArr[i].trim())) continue;//去掉""
            filters += filterArr[i] + " ";
        }
        for (int i = 0; i < ch.length; i++) {
            int indexchar = filters.indexOf(ch[i], 0);
            int indexch;
            while (indexchar != -1) {
                indexch = filters.indexOf(ch[i]);
                char prechar = indexch - 1 < 0 ? ' ' : filters.charAt(indexch - 1);//前
                char latchar = indexch + 1 > filters.length() - 1 ? ' ' : filters.charAt(indexch + 1);//后;
                if (prechar == ' ' || latchar == ' ') {
                    filters = filters.substring(0, indexch) + filters.substring(indexch + 1, filters.length());//去掉符号
                } else {
                    filters = filters.substring(0, indexch) + " " + filters.substring(indexch + 1, filters.length());//替换成空格
                }
                indexchar = filters.indexOf(ch[i], indexchar + 1);//从原位置后面开始找
            }
        }
        return filters.trim();//去掉空格
    }

    private List getSection(String filter, String filetext, int sublen) {
        List list = new ArrayList();
        int index = filetext.indexOf(filter, 0);//初始从0开始
        int min, max;
        while (index >= 0) {
            max = (index + filter.length() + sublen) < filetext.length() ?
                    index + sublen + filter.length() : filetext.length();
            min = (index - sublen) < 0 ? 0 : index - sublen;
            int[] temparr = {min, max};
            list.add(temparr);
            index = filetext.indexOf(filter, index + filter.length());
        }
        return list;
    }

    public int[][] sortArray(List newlist) {
        int[][] arr = new int[newlist.size()][2];
        for (int i = 0; i < arr.length; i++) {
            int[] array = (int[]) newlist.get(i);
            arr[i][0] = array[0];
            arr[i][1] = array[1];
        }
        for (int i = 0; i < arr.length - 1; i++) {   //冒泡
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j][0] > arr[j + 1][0]) {    //把大的值交换到后面
                    int temp = arr[j][0];
                    arr[j][0] = arr[j + 1][0];
                    arr[j + 1][0] = temp;

                    temp = arr[j][1];//尾巴跟着换
                    arr[j][1] = arr[j + 1][1];
                    arr[j + 1][1] = temp;
                }
            }
        }
//        System.out.println("排序后：\n"+Arrays.deepToString(arr));
        return arr;
    }

    public List arrangeArr(int[][] arr, int distance) {
        int endcompare = arr[0][1];//初始比较值
        int headcompare = arr[0][0];
        List newlist = new ArrayList();
        int[] initarr = {headcompare, endcompare};//初始化；放入第一个数组
        newlist.add(initarr);

        for (int i = 1; i < arr.length; i++) {//从第二个数组开始
            if (arr[i][0] - endcompare <= distance) {//两个区间相距不超过distance，都视为有交集
                newlist.remove(newlist.size() - 1);//删除最外层元素,准备更新最外层元素
                if (arr[i][1] > endcompare) {//后者的尾大于前者的尾，则新数组添加前者和后者的合并区间;
                    endcompare = arr[i][1];//更新前者的
                }
            } else {//没有交集，则新数组添加前者
                headcompare = arr[i][0];//全部用后者的
                endcompare = arr[i][1];
            }
            int[] temparr = {headcompare, endcompare};
            newlist.add(temparr);
        }
//        System.out.println("处理后的有效区间：");
//        for (int i = 0; i < newlist.size(); i++) {
//            int[] arraytemp=(int[]) newlist.get(i);
//            System.out.println(arraytemp[0]+","+arraytemp[1]);
//        }
//        System.out.println("===============================");
        return newlist;
    }

    private String getGson(int start, int limit, List<Tb_fulltext> list) {
        Map<String, Object> jsonObj = new HashMap<String, Object>();
        List<Tb_fulltext> listsub = new ArrayList<Tb_fulltext>();
        listsub = list.subList(start, list.size() < limit + start ? list.size() : limit + start);
        jsonObj.put("content", listsub);
        jsonObj.put("totalElements", list.size());
        Gson gson = new Gson();
        String jsonStr = gson.toJson(jsonObj);
        return jsonStr;
    }

    private static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
        if (!dir.getName().equals("testword")) {
            dir.delete();
        }
    }

    public static void main(String[] args) {
    }

    public static void convert2pdf() {
//        //删除
//        File file=new File("f:\\word2pdf\\testword");
//        deleteDir(file);

//        //复制100个文件
//        String srcFilePath = "f:\\word2pdf\\test.docx";//
//        for (int i = 1; i <= 100; i++) {
//            String copypath = "f:\\word2pdf\\testword\\test"+i+".docx";
//            System.out.println(copypath);
//            new FullSearchController().copyfile(srcFilePath,copypath);
//        }

        //转换
        File file = new File("f:\\word2pdf\\testword");
        String[] strlist = file.list();
        for (int i = 0; i < strlist.length; i++) {
            String srcpath = "f:\\word2pdf\\testword\\" + strlist[i].substring(0, strlist[i].lastIndexOf(".")) + ".jpg";
            String tarpath = "f:\\word2pdf\\testword\\" + strlist[i].substring(0, strlist[i].lastIndexOf(".")) + ".pdf";
            new FullSearchController().Word2PDF(srcpath, tarpath);
        }
    }

    public boolean Word2PDF(String srcFilePath, String pdfFilePath) {
        boolean iscreatepdf = false;
        ActiveXComponent app = null;
        Dispatch doc = null;
        String errormsg = "";
        try {
            System.out.println("Starting WORD...");
            long start = System.currentTimeMillis();
            ComThread.InitSTA();
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", false);
            app.setProperty("AutomationSecurity", new Variant(3));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            System.out.println("opening document:" + srcFilePath);
            doc = Dispatch.invoke(docs, "Open", Dispatch.Method, new Object[]{srcFilePath, new Variant(false), new Variant(true), new Variant(false), new Variant("pwd")}, new int[1]).toDispatch();
            Dispatch.put(doc, "RemovePersonalInformation", false);
            // word保存为pdf格式宏，值为17
            File tofile = new File(pdfFilePath);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[]{pdfFilePath, new Variant(17)}, new int[1]);
            long end = System.currentTimeMillis();
            System.out.println("completed..used:" + (end - start) / 1000 + "s");
            iscreatepdf = true;
        } catch (ComFailException e) {
            e.printStackTrace();
            errormsg = e.getMessage();
            System.out.println(errormsg);
            failureLog("WORD固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            errormsg = e.getMessage();
            failureLog("WORD固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
        } finally {
            if (doc != null) {
                Dispatch.call(doc, "Close", false);
            }
            if (app != null) {
                app.invoke("Quit", 0);
                System.out.println("==========WORD固化退出进程=================");
            }
            ComThread.Release();
            //如保存PDF过程出现错误，则换一个导出方式
//            if(errormsg.contains("Invoke of: SaveAs")){
//                iscreatepdf = this.wordToPDFByExport(srcFilePath, pdfFilePath,convertfilepath);
//            }
        }
        return iscreatepdf;
    }


    public boolean Powerpoint2PDF(String srcFilePath, String pdfFilePath) {
        boolean iscreatepdf = false;
        ActiveXComponent app = null;
        Dispatch ppt = null;
        System.out.println("Starting ppt...");
        try {
            ComThread.InitSTA();
            long start = System.currentTimeMillis();
            app = new ActiveXComponent("PowerPoint.Application");
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            ppt = Dispatch.call(ppts, "Open", srcFilePath, true, true, false).toDispatch();
            Dispatch.call(ppt, "SaveAs", pdfFilePath, 32); // ppSaveAsPDF为特定值32
            // 固化用时
            long end = System.currentTimeMillis();
            System.out.println("completed..used:" + (end - start) / 1000 + "s");
            iscreatepdf = true; // set flag true;
        } catch (ComFailException e) {
            e.printStackTrace();
            failureLog("PPT固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            failureLog("PPT固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
        } finally {
            try {
                if (ppt != null) {
                    Dispatch.call(ppt, "Close");
                }
                if (app != null) {
                    app.invoke("Quit");
                    System.out.println("==========ppt固化退出进程=================");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ComThread.Release();
        }
        return iscreatepdf;
    }

    public boolean Excel2PDFAll(String srcFilePath, String pdfFilePath) {

        boolean istopdfsucc = false;
        List<String> pdflistfile = new ArrayList<String>();
        System.out.println("Starting excel...");
        long start = System.currentTimeMillis();
        try {
            ComThread.InitSTA();
            String outFile = "";
            Dispatch sheet = null;
            Dispatch sheets = null;
            Dispatch excel = null;
            ActiveXComponent actcom = new ActiveXComponent("Excel.Application");
            try {
                // 创建存放PDF子文件的目录
                String pdfdirepath = "f:\\word2pdf\\excelChild";
                File objpdfdire = new File(pdfdirepath);
                if (objpdfdire.exists()) {
                    deleteDir(objpdfdire);
                }
                if (!objpdfdire.exists()) {
                    objpdfdire.mkdirs();
                }
                System.out.println("opening document:" + srcFilePath);
                actcom.setProperty("Visible", false);
                //禁用宏
                actcom.setProperty("AutomationSecurity", new Variant(3));
                Dispatch excels = actcom.getProperty("Workbooks").toDispatch();
                excel = Dispatch.invoke(excels, "Open", Dispatch.Method, new Object[]{srcFilePath, new Variant(false), new Variant(false), "1", new Variant("pwd")}, new int[9]).toDispatch();
                sheets = Dispatch.get(excel, "Sheets").toDispatch();// 得到所有sheet
                int count = Dispatch.get(sheets, "Count").getInt(); // 得到说有sheet的数量
                for (int j = 1; j <= count; j++) {
                    try {
                        sheet = Dispatch.invoke(sheets, "Item", Dispatch.Get, new Object[]{new Integer(j)}, new int[1]).toDispatch();
                        String sheetname = Dispatch.get(sheet, "name").toString();
                        if (sheetname.contains("Macro")) {
                            continue;
                        }
                        sheetname = sheetname.trim();
                        sheetname = ToDBC(sheetname);
                        sheetname = sheetname.replace(" ", "");
                        Dispatch.call(sheet, "Activate"); // 设置为当前活动的sheet
                        try {
                            Dispatch.call(sheet, "Select");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        outFile = "f:\\word2pdf\\excelChild" + "/" + sheetname + ".pdf";
                        Dispatch.invoke(excel, "SaveAs", Dispatch.Method, new Object[]{outFile, new Variant(57), new Variant(false), new Variant(57), new Variant(57), new Variant(false), new Variant(true), new Variant(57), new Variant(true), new Variant(true), new Variant(true)}, new int[1]);
                        pdflistfile.add(outFile);
                    } catch (ComFailException come) {
                        come.printStackTrace();
                        continue;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        continue;
                    }
                }
                // 合成PDF
                String[] childpdfArray = new String[pdflistfile.size()];
                for (int i = 0; i < pdflistfile.size(); i++) {
                    childpdfArray[i] = pdflistfile.get(i);
                }
                istopdfsucc = mergePdfFiles(childpdfArray, pdfFilePath);
                System.out.println("to pdf " + pdfFilePath);
                long end = System.currentTimeMillis();
                System.out.println("completed..used:" + (end - start) / 1000 + "s");
            } catch (ComFailException e) {
                e.printStackTrace();
                failureLog("EXCEL固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                failureLog("EXCEL固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } finally {
                if (excel != null) {
                    Dispatch.call(excel, "Close", new Variant(false));
                }
                if (actcom != null) {
                    actcom.invoke("Quit", new Variant[]{});
                    actcom = null;
                    System.out.println("==========EXCEL固化退出进程=================");
                }
                ComThread.Release();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return istopdfsucc;
    }

    public boolean tiff2PDF(String convertfilepath, String pdfFile) {
        String tiff = convertfilepath;
        String pdf = pdfFile;
        Document document = new Document(PageSize.LETTER, 0, 0, 0, 0);
        int pages = 0, comps = 0;
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            RandomAccessFileOrArray ra = null;
            try {
                ra = new RandomAccessFileOrArray(tiff);
                comps = TiffImage.getNumberOfPages(ra);
            } catch (Throwable e) {
                System.out.println("Exception in " + tiff + " " + e.getMessage());
            }
            System.out.println("Processing: " + tiff);
            for (int c = 0; c < comps; ++c) {
                try {
                    Image img = TiffImage.getTiffImage(ra, c + 1);
                    if (img != null) {
                        System.out.println("page " + (c + 1));
                        img.scalePercent(7200f / img.getDpiX(), 7200f / img.getDpiY());
                        document.setPageSize(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
                        img.setAbsolutePosition(0, 0);
                        cb.addImage(img);
                        document.newPage();
                        ++pages;
                    }
                } catch (Throwable e) {
                    System.out.println("Exception " + tiff + " page " + (c + 1) + " " + e.getMessage());
                }
            }
            ra.close();
            document.close();
            System.out.println("done...");
            return true;
        } catch (Throwable e) {
            failureLog("TIF固化失败：" + tiff + " ，异常信息：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static String ToDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);
        return returnString;
    }

    public static boolean mergePdfFiles(String[] files, String newfile) {
        boolean retValue = false;
        Document document = null;
        try {
            document = new Document(new PdfReader(files[0]).getPageSize(1));
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(newfile));
            document.open();
            for (int i = 0; i < files.length; i++) {
                PdfReader reader = new PdfReader(files[i]);
                int n = reader.getNumberOfPages();
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
            }
            retValue = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        return retValue;
    }

    public boolean copyfile(String sourFile, String descFile) {
        java.io.File vfileold = new File(sourFile);
        java.io.File vfilenew = new File(descFile);
        if (vfileold.exists()) {
            if (vfilenew.exists())
                vfilenew.delete();
            FileInputStream in1 = null;
            FileOutputStream out1 = null;
            try {
                in1 = new FileInputStream(vfileold);
                out1 = new FileOutputStream(vfilenew);

                byte[] bytes = new byte[1024];
                int c;
                while ((c = in1.read(bytes)) != -1) {
                    out1.write(bytes, 0, c);
                }
                in1.close();
                out1.close();
            } catch (Exception ex) {
                try {
                    in1.close();
                    out1.close();
                } catch (Exception aa) {
                    in1 = null;
                    out1 = null;
                    vfileold = null;
                    vfilenew = null;
                }
                vfileold = null;
                vfilenew = null;
                return false;
            }
            vfileold = null;
            vfilenew = null;
        }
        return true;
    }

    public static void failureLog(String logtext) {
        BufferedWriter fw = null;
        try {
            // 固化失败信息存放路径
            String rootpath = "F:\\word2pdf\\log";// SystemConfig.getValue("Titans.ftpjdpath");
            String errorinfopath = rootpath + "/固化信息/失败信息";
            String diretorypath = errorinfopath;
            if (!new File(diretorypath).exists()) {
                new File(diretorypath).mkdirs();
            }
            String filepath = diretorypath + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".txt";
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
            fw.append(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()) + "：" + logtext);
            fw.newLine();
            fw.flush(); // 全部写入缓存中的内容
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping("/search")
    @ResponseBody
    public Page<Tb_fulltext> search(String filters, String oldparams,String iflag, int page, int limit, int start, String sort) throws IOException, SolrServerException {
        Sort sortobj = WebSort.getSortByJson(sort);
        try {
            String regEx = "[`~!#%^&*()+=|{}':;,\\[\\]\\\\.<>/?！…（）—【】‘；：”“’。，、？]";//最后面是空格符
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(filters);
            filters = matcher.replaceAll("");
            filters = subQueryFilter(filters);//拼接关键词
            SolrQuery query = new SolrQuery();
            System.out.println("关键字:" + filters);
            query.set("q", "keyword:" + filters);
            if (oldparams != null && oldparams.length() != 0) {//结果中检索
                Matcher oldMatcher = pattern.matcher(oldparams);
                oldparams = oldMatcher.replaceAll("");
                oldparams = subQueryFilter(oldparams);
                query.addFilterQuery("keyword:" + oldparams);//添加
            }

            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<String> authList = userService.findDataAuths(userDetails.getUserid());
            if("gl".equals(iflag)){  //管理平台
                //用户拥有的权限节点
                StringBuffer authIds = new StringBuffer("(");
                for (int i = 0; i < authList.size(); i++) {//前面请求已判断size不为0
                    authIds.append("\"").append(authList.get(i).trim()).append("\"");
                    if (i < authList.size() - 1) {
                        authIds.append(",");
                    } else {
                        authIds.append(")");
                    }
                }
                query.addFilterQuery("nodeid:" + authIds.toString());
            }else{  //利用平台
                if("true".equals(openArchies)){  //只检索本机构的开放档案的电子文件
                    StringBuffer authIds = fullSearchService.getNodeidStringBuffer(userDetails.getUserid());
                    query.addFilterQuery("nodeid:" + authIds.toString());
                }
                query.addFilterQuery("flagopen:原文");//检测原文开放的电子文件
            }

            if (sortobj != null) {
                String[] sortStr = sortobj.toString().split(":");
                query.addSort(sortStr[0].trim(), "asc".equalsIgnoreCase(sortStr[1].trim()) ? ORDER.asc : ORDER.desc);
            }
            query.setStart(start);
            query.setRows(limit);
            query.setHighlight(false);
            query.setHighlightSnippets(20);//分片
            query.addHighlightField("filename");//高亮字段
            query.addHighlightField("filetext");
            query.setHighlightSimplePre("<span style='color:red'>");
            query.setHighlightSimplePost("</span>");
            query.setHighlightFragsize(50);//默认摘要长度是100
            QueryResponse qr = client.query(query, SolrRequest.METHOD.POST);
            List<Tb_fulltext> beanList = qr.getBeans(Tb_fulltext.class);
            Map<String, Map<String, List<String>>> highlightResult = qr.getHighlighting();
            for (int i = 0; i < beanList.size(); ++i) {
                String testid = beanList.get(i).getTextid();
                if (highlightResult.get(testid) != null && highlightResult.get(testid).get("filetext") != null) {
                    String appendStr = "......";
                    for (String snippet : highlightResult.get(testid).get("filetext")) {
                        appendStr += snippet + "......";//截取字符串
                    }
                    beanList.get(i).setFiletext(appendStr);
                } else {
                    int index;
                    String endChar = "";
                    if (beanList.get(i).getFiletext().length() > 100) {
                        index = 100;
                        endChar = "......";
                    } else {
                        index = beanList.get(i).getFiletext().length();
                    }
                    beanList.get(i).setFiletext(beanList.get(i).getFiletext().substring(0, index) + endChar);//截取
                }
                if (highlightResult.get(testid) != null && highlightResult.get(testid).get("filename") != null) {
                    beanList.get(i).setFilename(highlightResult.get(testid).get("filename").get(0));
                }
            }
            PageRequest pageRequest = new PageRequest(page - 1, limit);
            return new PageImpl(beanList, pageRequest, qr.getResults().getNumFound());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/searchValidation")
    @ResponseBody
    public ExtMsg searchValidation(String filters, String oldparams,String iflag) throws IOException, SolrServerException {
        try {
            if (filters != null && filters.length() != 0) {
                SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                List<String> authList = new ArrayList<>();
                if(!"ly".equals(iflag)){
                    //用户拥有的权限节点
                    authList = userService.findDataAuths(userDetails.getUserid());
                    if (authList==null||authList.size()<1) {
                        return new ExtMsg(false, "没有找到相关内容", null);
                    }
                }
                String regEx = "[`~!#%^&*()+=|{}':;,\\[\\]\\\\.<>/?！…（）—【】‘；：”“’。，、？]";//最后面是空格符
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(filters);
                filters = matcher.replaceAll("");
                filters = subQueryFilter(filters);//拼接关键词
                SolrQuery query = new SolrQuery();
                query.set("q", "keyword:" + filters);//例:("公安局"OR"时间"OR"你好")
                if (oldparams != null && oldparams.length() != 0) {//结果中检索
                    Matcher oldMatcher = pattern.matcher(oldparams);
                    oldparams = oldMatcher.replaceAll("");
                    oldparams = subQueryFilter(oldparams);
                    query.addFilterQuery("keyword:" + oldparams);//添加
                }

                if("gl".equals(iflag)){  //管理平台
                    //用户拥有的权限节点
                    StringBuffer authIds = new StringBuffer("(");
                    for (int i = 0; i < authList.size(); i++) {//前面请求已判断size不为0
                        authIds.append("\"").append(authList.get(i).trim()).append("\"");
                        if (i < authList.size() - 1) {
                            authIds.append(",");
                        } else {
                            authIds.append(")");
                        }
                    }
                    query.addFilterQuery("nodeid:" + authIds.toString());
                }else{  //利用平台
                    if("true".equals(openArchies)){  //只检索本机构的开放档案的电子文件
                        StringBuffer authIds = fullSearchService.getNodeidStringBuffer(userDetails.getUserid());
                        query.addFilterQuery("nodeid:" + authIds.toString());
                    }
                    query.addFilterQuery("flagopen:原文");//检测原文开放的电子文件
                }

                QueryResponse qr = client.query(query, SolrRequest.METHOD.POST);
                List<Tb_fulltext> beanList = qr.getBeans(Tb_fulltext.class);
                if (beanList.size() == 0) {
                    return new ExtMsg(false, "没有找到相关内容", null);
                }
            } else {
                return new ExtMsg(false, "请输入检索内容", null);
            }
        } catch (SolrServerException sse) {
            if (sse.getMessage().startsWith("Server refused connection")) {
                return new ExtMsg(false, "请开启全文检索服务", null);
            } else {
                sse.printStackTrace();
                return new ExtMsg(false, "检索服务出现异常！", null);
            }
        }catch (HttpSolrClient.RemoteSolrException e) {
            e.printStackTrace();
            return new ExtMsg(false, "请求出现异常:RemoteSolrException！", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ExtMsg(false, "预检请求出现异常！", null);
        }
        return new ExtMsg(true, "", null);
    }

    private String subQueryFilter(String filters) {//拼接查询语句
        String queryFilter = "(";
        String[] filterArr = filters.split(" ");
        for (int i = 0; i < filterArr.length; i++) {
            queryFilter += "\"" + filterArr[i] + "\"";
            if (i != filterArr.length - 1) {
                queryFilter += "OR";
            } else {
                queryFilter += ")";
            }
        }
        return queryFilter;
    }

    //删除全文索引记录
    public void delSolrRecord(String[] eleidArr,String idType){
        try{
            StringBuffer authIds = new StringBuffer("(");
            for (int i = 0; i < eleidArr.length; i++) {
                authIds.append("\"").append(eleidArr[i].trim()).append("\"");
                if (i < eleidArr.length - 1) {
                    authIds.append(",");
                } else {
                    authIds.append(")");
                }
            }
            //设置要删除的条件
            UpdateResponse product = client.deleteByQuery(idType+":"+authIds.toString());
            //提交请求
            client.commit();
            //获取状态
            int status = product.getStatus();
            //查看状态
            System.out.println("status = " + status);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
