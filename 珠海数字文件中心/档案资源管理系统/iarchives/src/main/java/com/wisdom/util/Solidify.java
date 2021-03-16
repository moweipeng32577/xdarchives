package com.wisdom.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.codec.TiffImage;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.wisdom.web.entity.Tb_electronic;
import com.wisdom.web.entity.Tb_electronic_capture;
import com.wisdom.web.entity.Tb_electronic_solid;
import com.wisdom.web.repository.ElectronicCaptureRepository;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.repository.ElectronicSolidRepository;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by tanly on 2018/4/18 0018.
 */
@Component
public class Solidify {

    public static Solidify solidify;

    public final static String MS = "MS";//Microsoft Office
    public final static String WPS = "WPS";//WPS Office
    public final static String NULL = "NULL";//没有安装Office
    public final static String UNCHECKED = "UNCHECKED";//未检测过

    public static String OFFICE = UNCHECKED;//初始为：未检测

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    ElectronicSolidRepository electronicSolidRepository;

    @Value("${system.document.rootpath}")
    private String rootpath;// 系统文件根目录
    @Value("${system.solidify.wkpath}")
    private String wkpath;// WK.EXE路径
    @Value("${chromuim.path}")
    private String chromiumPath;

    private final static Logger logger = LoggerFactory.getLogger(Solidify.class);

    @PostConstruct
    public void init() {
        solidify = this;
        solidify.electronicRepository = this.electronicRepository;
        solidify.electronicCaptureRepository = this.electronicCaptureRepository;
        solidify.electronicSolidRepository = this.electronicSolidRepository;
        solidify.rootpath = this.rootpath;
        solidify.wkpath = this.wkpath;
    }

    /**
     * 固化-数据管理 重名文件：在固化文件名后面加上文件格式
     *
     * @param entries
     */
    public static void convertToPdfOfManagement(String[] entries) {
        for (String entryId : entries) {
            solidify.electronicSolidRepository.deleteByEntryid(entryId);// 删除条目的所有固化文件
            List<Tb_electronic> electronicList = solidify.electronicRepository
                    .findByEntryidOrderBySortsequence(entryId);

            Set<String> dupSet = new HashSet<>();
            for (Tb_electronic electronic : electronicList) {
                String filename = electronic.getFilename().substring(0, electronic.getFilename().lastIndexOf("."));
                for (Tb_electronic ele : electronicList) {
                    if (ele.getFilename().startsWith(filename) && !ele.getEleid().equals(electronic.getEleid())) {
                        dupSet.add(electronic.getEleid());
                        break;
                    }
                }
            }

            for (Tb_electronic electronic : electronicList) {
                String name = electronic.getFilename();
                String srcpath = solidify.rootpath + electronic.getFilepath() + "/" + name;
                String filePath = getSolidFileBaseDir()+"/"+electronic.getEntryid().trim();//先获取文件路径(日期后面加多一层以entryid命名的文件夹)
                String fileName;
                String electronicid = electronic.getEleid();
                if (dupSet.contains(electronic.getEleid())) {
                    fileName = name.substring(0, name.lastIndexOf(".")) + "_"
                            + name.substring(name.lastIndexOf(".") + 1, name.length()) + ".pdf";
                } else {
                    fileName = name.substring(0, name.lastIndexOf(".")) + ".pdf";
                }
                String tarpath = getSolidFileDir(filePath) + "/" + fileName;
                String fileType = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
                execSolid(fileType, srcpath, tarpath, entryId, fileName, filePath,electronicid);
            }
        }
    }

    /**
     * 固化-数据采集 重名文件：在固化文件名后面加上文件格式
     *
     * @param entries
     */
    public static void convertToPdfOfCapture(String[] entries) {
        for (String entryId : entries) {
            solidify.electronicSolidRepository.deleteByEntryid(entryId);// 删除条目的所有固化文件
            List<Tb_electronic_capture> electronicList = solidify.electronicCaptureRepository
                    .findByEntryidOrderBySortsequence(entryId);

            Set<String> dupSet = new HashSet<>();
            for (Tb_electronic_capture electronic : electronicList) {
                String filename = electronic.getFilename().substring(0, electronic.getFilename().lastIndexOf("."));
                for (Tb_electronic_capture ele : electronicList) {
                    if (ele.getFilename().startsWith(filename) && !ele.getEleid().equals(electronic.getEleid())) {
                        dupSet.add(electronic.getEleid());
                        break;
                    }
                }
            }
            for (Tb_electronic_capture electronic : electronicList) {
                String name = electronic.getFilename();
                String srcpath = solidify.rootpath + electronic.getFilepath() + "/" + name;
                String filePath = getSolidFileBaseDir()+"/"+electronic.getEntryid().trim();//先获取文件路径(日期后面加多一层以entryid命名的文件夹)
                String fileName;
                String electronicid = electronic.getEleid();
                if (dupSet.contains(electronic.getEleid())) {
                    fileName = name.substring(0, name.lastIndexOf(".")) + "_"
                            + name.substring(name.lastIndexOf(".") + 1, name.length()) + ".pdf";
                } else {
                    fileName = name.substring(0, name.lastIndexOf(".")) + ".pdf";
                }
                String tarpath = getSolidFileDir(filePath) + "/" + fileName;
                String fileType = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
                execSolid(fileType, srcpath, tarpath, entryId, fileName, filePath,electronicid);
            }
        }
    }

    /**
     * 固化-数据管理-通过electronic集合 重名文件：在固化文件名后面加上文件格式
     *
     * @param electronicList
     */
    public static List<String> convertToPdfOfManagement(List<Tb_electronic> electronicList, List<String> failList) {
        for (Tb_electronic electronic : electronicList) {
            String filename = electronic.getFilename().substring(0, electronic.getFilename().lastIndexOf("."));
            List<Tb_electronic> allOfentryid = solidify.electronicRepository
                    .findByEntryidAndFilenameStartsWithAndEleidNot(electronic.getEntryid(), filename,
                            electronic.getEleid());

            String name = electronic.getFilename();
            String srcpath = solidify.rootpath + electronic.getFilepath() + "/" + name;
            String filePath = getSolidFileBaseDir()+"/"+electronic.getEntryid().trim();//先获取文件路径(日期后面加多一层以entryid命名的文件夹)
            String fileName;
            String electronicid = electronic.getEleid();
            if (allOfentryid.size() > 0) {
                fileName = name.substring(0, name.lastIndexOf(".")) + "_"
                        + name.substring(name.lastIndexOf(".") + 1, name.length()) + ".pdf";
            } else {
                fileName = name.substring(0, name.lastIndexOf(".")) + ".pdf";
            }
            String dir = getSolidFileDir(filePath);
            String tarpath = dir + "/" + fileName;
            String fileType = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
            boolean isSuccess = execSolid(fileType, srcpath, tarpath, electronic.getEntryid(), fileName, filePath,electronicid);
            if (!isSuccess) {
                failList.add(electronic.getEleid());
                File dirFile = new File(dir);
                File[] fileAry = dirFile.listFiles();
                if (fileAry != null && fileAry.length == 0) {
                    dirFile.delete();// 实际上delete()内部自带验证（目录内有文件则删不了）
                }
            }
        }
        return failList;
    }

    /**
     * 固化-数据采集--通过electronic集合 重名文件：在固化文件名后面加上文件格式
     *
     * @param electronicList
     */
    public static List<String> convertToPdfOfCapture(List<Tb_electronic_capture> electronicList,
                                                     List<String> failList) {
        for (Tb_electronic_capture electronic : electronicList) {
            String filename = electronic.getFilename().substring(0, electronic.getFilename().lastIndexOf("."));
            List<Tb_electronic_capture> allOfentryid = solidify.electronicCaptureRepository
                    .findByEntryidAndFilenameStartsWithAndEleidNot(electronic.getEntryid(), filename,
                            electronic.getEleid());

            String name = electronic.getFilename();
            String srcpath = solidify.rootpath + electronic.getFilepath() + "/" + name;
            String filePath = getSolidFileBaseDir()+"/"+electronic.getEntryid().trim();//先获取文件路径(日期后面加多一层以entryid命名的文件夹)
            String fileName;
            String electronicid = electronic.getEleid();
            if (allOfentryid.size() > 0) {
                fileName = name.substring(0, name.lastIndexOf(".")) + "_"
                        + name.substring(name.lastIndexOf(".") + 1, name.length()) + ".pdf";
            } else {
                fileName = name.substring(0, name.lastIndexOf(".")) + ".pdf";
            }
            String dir = getSolidFileDir(filePath);
            String tarpath = dir + "/" + fileName;
            String fileType = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
            boolean isSuccess = execSolid(fileType, srcpath, tarpath, electronic.getEntryid(), fileName, filePath,electronicid);
            if (!isSuccess) {
                failList.add(electronic.getEleid());
                File dirFile = new File(dir);
                File[] fileAry = dirFile.listFiles();
                if (fileAry != null && fileAry.length == 0) {
                    dirFile.delete();// 实际上delete()内部自带验证（目录内有文件则删不了）
                }
            }
        }
        return failList;
    }

    /**
     * 执行固化
     *
     * @param fileType
     * @param srcpath
     * @param tarpath
     * @param entryId
     * @param fileName
     * @param filePath
     * @param electronicid
     * @return
     */
    private static boolean execSolid(String fileType, String srcpath, String tarpath, String entryId, String fileName,
                                     String filePath,String electronicid) {
        srcpath = srcpath.replaceAll("/", "\\\\");
        tarpath = tarpath.replaceAll("/", "\\\\");
        boolean success = false;
        File file = new File(srcpath);
        if (!file.exists()) {
            failureLog("文件或路径不存在：" + srcpath);
            return false;
        } else if (!file.isFile()) {
            failureLog("该路径不是文件路径：" + srcpath);
            return false;
        }
        String uninstalledStr = "系统尚未安装Microsoft Office或WPS Office！";
        if ("docx".equals(fileType) || "doc".equals(fileType) || "rtf".equals(fileType) || "txt".equals(fileType)) {
            if (OFFICE.equals(MS)) {
                System.out.println("调用ms");//待删
                success = solidify.word2PDF(srcpath, tarpath);
            } else if (OFFICE.equals(WPS)) {
                System.out.println("调用wps");//待删
                success = solidify.word2PDFByWPS(srcpath, tarpath);
            } else {
                logger.info(">>>>>>>>>>>>>>>>>" + uninstalledStr + "<<<<<<<<<<<<<<<<<");
                logger.info("-----------------------------------------------");
                failureLog(uninstalledStr);
            }
        } else if ("xls".equals(fileType) || "xlsx".equals(fileType) || "xlsm".equals(fileType)) {
            if (OFFICE.equals(MS)) {
                System.out.println("调用ms");//待删
                success = solidify.Excel2PDFAll(srcpath, tarpath);
            } else if (OFFICE.equals(WPS)) {
                System.out.println("调用wps");//待删
                success = solidify.excel2PDFByWPS(srcpath, tarpath);
            } else {
                logger.info(">>>>>>>>>>>>>>>>>" + uninstalledStr + "<<<<<<<<<<<<<<<<<");
                logger.info("-----------------------------------------------");
                failureLog(uninstalledStr);
            }
        } else if ("ppt".equals(fileType) || "pptx".equals(fileType) || "pps".equals(fileType)) {
            if (OFFICE.equals(MS)) {
                System.out.println("调用ms");//待删
                success = solidify.Powerpoint2PDF(srcpath, tarpath);
            } else if (OFFICE.equals(WPS)) {
                System.out.println("调用wps");//待删
                success = solidify.ppt2PDFByWPS(srcpath, tarpath);
            } else {
                logger.info(">>>>>>>>>>>>>>>>>" + uninstalledStr + "<<<<<<<<<<<<<<<<<");
                logger.info("-----------------------------------------------");
                failureLog(uninstalledStr);
            }
        } else if ("jpg".equals(fileType) || "jpeg".equals(fileType) || "png".equals(fileType)
                || "bmp".equals(fileType)) {
            success = solidify.image2PDF(srcpath, tarpath);
        } else if ("tiff".equals(fileType) || "tif".equals(fileType)) {
            success = solidify.tiff2PDF(srcpath, tarpath);
        } else if ("html".equals(fileType)) {
            String exePath = solidify.wkpath.endsWith("exe") ? solidify.wkpath : solidify.wkpath + ".exe";
            File exeFile = new File(exePath);
            if (exeFile.exists()) {
                success = solidify.HTML2PDF(srcpath, tarpath);// wkhtmltopdf.exe
            } else {
                success = solidify.html2PDF(srcpath, tarpath);// itext
            }
        } else if ("pdf".equals(fileType)) {
            success = solidify.copyfile(srcpath, tarpath);
        } else if (("wps".equals(fileType) || "wpt".equals(fileType)) && OFFICE.equals(WPS)) {//仅在调用WPS时,支持固化这些格式
            success = solidify.word2PDFByWPS(srcpath, tarpath);
        } else if ("et".equals(fileType) && OFFICE.equals(WPS)) {
            success = solidify.excel2PDFByWPS(srcpath, tarpath);
        } else {
            logger.info(">>>>>>>>>>>>>>>>>" + fileType + "文件格式不支持转换!" + "<<<<<<<<<<<<<<<<<");
            logger.info("-----------------------------------------------");
            failureLog(fileType + "文件格式不支持转换：" + srcpath);
        }
        if (success) {
            File pdfFile = new File(tarpath);
            if (!pdfFile.exists()) {
                return false;
            }
            Tb_electronic_solid electronicSolid = new Tb_electronic_solid();
            electronicSolid.setEntryid(entryId);
            electronicSolid.setFilename(fileName);
            electronicSolid.setFilepath(filePath);
            electronicSolid.setFolder("");// 临时待改
            electronicSolid.setFilesize(String.valueOf(pdfFile.length()));
            electronicSolid.setFiletype("pdf");
            electronicSolid.setElectronicid(electronicid);
            solidify.electronicSolidRepository.save(electronicSolid);
        } else {
            return false;
        }
        return true;
    }

    /**
     * 获取日期路径
     *
     * @return
     */
    private static String getSolidFileBaseDir() {
        Calendar cal = Calendar.getInstance();
        return "/electronics/solidFile/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
                + cal.get(Calendar.DATE);
    }

    /**
     * 获取完整日期路径
     *
     * @param baseDir
     * @return
     */
    private static String getSolidFileDir(String baseDir) {
        File eleDir = new File(solidify.rootpath + baseDir);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        return solidify.rootpath + baseDir;
    }

    public boolean word2PDF(String srcFilePath, String pdfFilePath) {
        try {
            boolean success = false;
            ActiveXComponent app = null;
            Dispatch doc = null;
            try {
                long start = System.currentTimeMillis();
                ComThread.InitSTA();
                app = new ActiveXComponent("Word.Application");
                app.setProperty("Visible", false);
                app.setProperty("AutomationSecurity", new Variant(3));// 禁用宏
                Dispatch docs = app.getProperty("Documents").toDispatch();
                logger.info("打开文件:" + srcFilePath);
                doc = Dispatch.invoke(docs, "Open", Dispatch.Method, new Object[] { srcFilePath, new Variant(false),
                        new Variant(true), new Variant(false), new Variant("pwd") }, new int[1]).toDispatch();
                Dispatch.put(doc, "RemovePersonalInformation", false);
                // pdf格式宏：17
                File toFile = new File(pdfFilePath);
                if (toFile.exists()) {
                    toFile.delete();
                }
                Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { pdfFilePath, new Variant(17) }, new int[1]);// word保存为pdf格式宏，值为17
                long end = System.currentTimeMillis();
                logger.info("固化用时:" + (end - start) / 1000 + "s");
                success = true;
            } catch (ComFailException e) {
                failureLog("WORD固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } catch (Exception e) {
                failureLog("WORD固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } finally {
                if (doc != null) {
                    Dispatch.call(doc, "Close", false);
                }
                if (app != null) {
                    app.invoke("Quit", 0);
                    logger.info("------------------WORD固化结束------------------");
                }
                ComThread.Release();
            }
            return success;
        } catch (Exception e) {
            failureLog("WORD固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            logger.info("------------------WORD固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }

    public boolean Powerpoint2PDF(String srcFilePath, String pdfFilePath) {
        try {
            boolean success = false;
            ActiveXComponent app = null;
            Dispatch ppt = null;
            try {
                ComThread.InitSTA();
                long start = System.currentTimeMillis();
                app = new ActiveXComponent("PowerPoint.Application");
                Dispatch ppts = app.getProperty("Presentations").toDispatch();
                logger.info("打开文件:" + srcFilePath);
                ppt = Dispatch.call(ppts, "Open", srcFilePath, true, true, false).toDispatch();
                File toFile = new File(pdfFilePath);
                if (toFile.exists()) {
                    toFile.delete();
                }
                Dispatch.call(ppt, "SaveAs", pdfFilePath, 32);
                long end = System.currentTimeMillis();
                logger.info("固化用时:" + (end - start) / 1000 + "s");
                success = true;
            } catch (ComFailException e) {
                failureLog("PPT固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } catch (Exception e) {
                failureLog("PPT固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } finally {
                try {
                    if (ppt != null) {
                        Dispatch.call(ppt, "Close");
                    }
                    if (app != null) {
                        app.invoke("Quit");
                        logger.info("------------------PPT固化结束------------------");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ComThread.Release();
            }
            return success;
        } catch (Exception e) {
            failureLog("PPT固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            logger.info("------------------PPT固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }

    public boolean Excel2PDFAll(String srcFilePath, String pdfFilePath) {
        try {
            long start = System.currentTimeMillis();
            ActiveXComponent ax = null;
            Dispatch excel = null;
            try {
                ComThread.InitSTA();
                ax = new ActiveXComponent("Excel.Application");
                ax.setProperty("Visible", new Variant(false));
                ax.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
                Dispatch excels = ax.getProperty("Workbooks").toDispatch();

                Object[] obj = new Object[] { srcFilePath, new Variant(false), new Variant(false) };
                logger.info("打开文件:" + srcFilePath);
                File pdfFile = new File(pdfFilePath.substring(0, pdfFilePath.lastIndexOf("\\")));
                if (!pdfFile.exists()) {
                    pdfFile.mkdirs();// 目标路径不存在则创建
                }
                excel = Dispatch.invoke(excels, "Open", Dispatch.Method, obj, new int[9]).toDispatch();

                // 转换格式
                Object[] obj2 = new Object[]{new Variant(0), pdfFilePath, new Variant(0)};
                Dispatch.invoke(excel, "ExportAsFixedFormat", Dispatch.Method, obj2, new int[1]);
                long end = System.currentTimeMillis();
                logger.info("固化用时:" + (end - start) / 1000 + "s");
            } catch (Exception es) {
                es.printStackTrace();
                failureLog("EXCEL固化失败：" + srcFilePath + " ，异常信息：" + es.getMessage());
                return false;
            } finally {
                if (excel != null) {
                    Dispatch.call(excel, "Close", new Variant(false));
                }
                if (ax != null) {
//                    ax.invoke("Quit", new Variant[] {});
                    ax = null;
                    logger.info("------------------EXCEL固化结束------------------");
                }
                ComThread.Release();
            }
            return true;
        } catch (Exception e) {
            failureLog("EXCEL固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            logger.info("------------------EXCEL固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }

    public boolean tiff2PDF(String convertfilepath, String pdfFile) {
        try {
            logger.info("固化文件:" + convertfilepath);
            long start = System.currentTimeMillis();
            Document document = new Document(PageSize.LETTER, 0, 0, 0, 0);
            int comps = 0;
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                PdfContentByte cb = writer.getDirectContent();
                RandomAccessFileOrArray ra = null;
                try {
                    ra = new RandomAccessFileOrArray(convertfilepath);
                    comps = TiffImage.getNumberOfPages(ra);
                } catch (Throwable e) {
                    logger.error("Exception in " + convertfilepath + " " + e.getMessage());
                }
                for (int c = 0; c < comps; ++c) {
                    try {
                        Image img = TiffImage.getTiffImage(ra, c + 1);
                        if (img != null) {
                            logger.info("page:" + (c + 1));
                            img.scalePercent(7200f / img.getDpiX(), 7200f / img.getDpiY());
                            document.setPageSize(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
                            img.setAbsolutePosition(0, 0);
                            cb.addImage(img);
                            document.newPage();
                        }
                    } catch (Throwable e) {
                        logger.error("Exception " + convertfilepath + " page " + (c + 1) + " " + e.getMessage());
                    }
                }
                ra.close();
                document.close();
                long end = System.currentTimeMillis();
                logger.info("固化用时:" + (end - start) / 1000 + "s");
                logger.info("------------------TIF固化结束------------------");
                return true;
            } catch (Throwable e) {
                failureLog("TIF固化失败：" + convertfilepath + " ，异常信息：" + e.getMessage());
                return false;
            }
        } catch (Exception e) {
            failureLog("TIF固化失败：" + convertfilepath + " ，异常信息：" + e.getMessage());
            logger.info("------------------TIF固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }

    public boolean image2PDF(String inputFile, String pdfFile) {// 实际上也支持gif
        try {
            logger.info("固化文件:" + inputFile);
            long start = System.currentTimeMillis();
            boolean flag = true;
            try {
                File imgFile = new File(inputFile);
                if (!imgFile.exists()) {
                    throw new FileNotFoundException(inputFile + " (系统找不到指定的文件)");
                }
                File file = new File(pdfFile);
                // 1：创建一个document对象。
                Document document = new Document();
                document.setMargins(0, 0, 0, 0);
                // 2：创建一个PdfWriter实例，
                PdfWriter.getInstance(document, new FileOutputStream(file));
                // 3：打开文档。
                document.open();
                // 4：在文档中增加图片。
                Image img = Image.getInstance(inputFile);
                img.setAlignment(Image.ALIGN_CENTER);
                // 根据图片大小设置页面，一定要先设置页面，再newPage（），否则无效
                document.setPageSize(new Rectangle(img.getWidth(), img.getHeight()));
                document.newPage();
                document.add(img);
                // 5：关闭文档。
                document.close();
            } catch (FileNotFoundException e) {
                failureLog("IMAGE固化失败：" + inputFile + " ，异常信息：" + e.getMessage());
                flag = false;
            } catch (Exception e) {
                failureLog("IMAGE固化失败：" + inputFile + " ，异常信息：" + e.getMessage());
                flag = false;
            }

            long end = System.currentTimeMillis();
            logger.info("固化用时:" + (end - start) / 1000 + "s");
            if (flag) {
                logger.info("------------------Image固化结束------------------");
            }
            return flag;
        } catch (Exception e) {
            failureLog("IMAGE固化失败：" + inputFile + " ，异常信息：" + e.getMessage());
            logger.info("------------------Image固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 固化HTML:仅支持有完整标签的HTML
     *
     * @param src
     * @param target
     * @return
     */
    public boolean html2PDF(String src, String target) {
        try {
            logger.info("固化HTML文件:" + src);
            long start = System.currentTimeMillis();
            try {
                Document document = new Document(PageSize.A4, 10, 10, 10, 10);
                PdfWriter mPdfWriter = PdfWriter.getInstance(document, new FileOutputStream(target));
                document.open();

                File file = new File(src);
                FileInputStream bin = new FileInputStream(file);
                XMLWorkerHelper.getInstance().parseXHtml(mPdfWriter, document, bin, Charset.forName("utf-8"),
                        new ChinaFontProvide());
                document.close();
                mPdfWriter.close();
                long end = System.currentTimeMillis();
                logger.info("固化用时:" + (end - start) / 1000 + "s");
                logger.info("------------------HTML固化结束------------------");
                return true;
            } catch (FileNotFoundException e) {
                failureLog("HTML固化失败：" + src + " ，异常信息：" + e.getMessage());
                return false;
            } catch (DocumentException e) {
                failureLog("HTML固化失败：" + src + " ，异常信息：" + e.getMessage());
                return false;
            } catch (Exception e) {
                failureLog("HTML固化失败：" + src + " ，异常信息：" + e.getMessage());
                return false;
            }
        } catch (Exception e) {
            failureLog("HTML固化失败：" + src + " ，异常信息：" + e.getMessage());
            logger.info("------------------HTML固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 支持中文
     */
    public static final class ChinaFontProvide implements FontProvider {
        @Override
        public Font getFont(String arg0, String arg1, boolean arg2, float arg3, int arg4, BaseColor arg5) {
            BaseFont bfChinese = null;
            try {
                bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
            return FontChinese;
        }

        @Override
        public boolean isRegistered(String arg0) {
            return false;
        }
    }

    /**
     * 固化HTML:需要使用wkhtmltopdf.exe
     *
     * @param srcPath
     * @param destPath
     * @return
     */
    public boolean HTML2PDF(String srcPath, String destPath) {
        try {
            logger.info("固化HTML文件:" + srcPath);
            long start = System.currentTimeMillis();
            File file = new File(destPath);
            if (file.exists()) {
                file.delete();
            }
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();// 如果pdf保存路径不存在，则创建路径
            }

            boolean result = true;
            try {
                ArrayList<String> commandList = new ArrayList<>(1);
                String exePath = solidify.wkpath.endsWith("exe") ? "\"" + solidify.wkpath +"\"" :  "\"" + solidify.wkpath + ".exe" + "\"";
                commandList.add(exePath);
                commandList.add("\"" + srcPath + "\"");
                commandList.add("\"" + destPath + "\"");
                if(solidify.chromiumPath != null && !"".equals(solidify.chromiumPath)){
                    commandList.add("\"" + solidify.chromiumPath + "\"");
                }
                Process solidProcess = new ProcessBuilder(commandList).redirectErrorStream(true).start();
                printProcess(solidProcess.getInputStream());
//                File oldFile = new File(srcPath);// 原文件
//                String content = FileUtils.readFileToString(oldFile, "utf-8");
//                content = content.replaceAll("font-family:\\s*'黑体'", "font-family: '微软雅黑'");// 固化【黑体】存在问题
//
//                int lastDot = srcPath.lastIndexOf(".");
//                String tempPath = srcPath.substring(0, lastDot) + "_temp" + srcPath.substring(lastDot, srcPath.length());
//                File tempfile = new File(tempPath);
//                if (tempfile.exists()) {
//                    tempfile.delete();
//                }
//                tempfile.createNewFile();// 复制一份用于固化的临时文件
//                OutputStream fos = new FileOutputStream(tempfile);
//                fos.write(content.getBytes("UTF-8"));
//                fos.flush();
//                fos.close();
//
//                Process proc = Runtime.getRuntime().exec(solidify.wkpath + " \"" + tempPath + "\" \"" + destPath + "\"");// 路径有空格处理：加引号
//                HtmlToPdfInterceptor error = new HtmlToPdfInterceptor(proc.getErrorStream());
//                HtmlToPdfInterceptor output = new HtmlToPdfInterceptor(proc.getInputStream());
//                error.start();
//                output.start();
//                proc.waitFor();

//                if (tempfile.exists()) {
//                    tempfile.delete();// 删除临时文件
//                }
                long end = System.currentTimeMillis();
                logger.info("固化用时:" + (end - start) / 1000 + "s");
                logger.info("------------------HTML固化结束------------------");
            } catch (Exception e) {
                failureLog("HTML固化失败：" + srcPath + " ，异常信息：" + e.getMessage());
                result = false;
            }
            return result;
        } catch (Exception e) {
            failureLog("HTML固化失败：" + srcPath + " ，异常信息：" + e.getMessage());
            logger.info("------------------HTML固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }

    public static class HtmlToPdfInterceptor extends Thread {
        private InputStream is;

        public HtmlToPdfInterceptor(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                br.readLine();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public static int printProcess(InputStream __is) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(__is));
            String line=null;
            while((line=br.readLine())!=null){
                System.out.println(line);
                if(line.indexOf("taskkill") !=-1){
                    break;
                }
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制文件
     *
     * @param sourFile
     *            源文件
     * @param descFile
     *            目标文件
     * @return
     */
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

    /**
     * 打印失败日志信息
     *
     * @param logtext
     */
    public static void failureLog(String logtext) {
        BufferedWriter fw = null;
        try {
            String diretorypath = solidify.rootpath + "/electronics/solidFile" + "/固化日志/失败信息";
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
            fw.flush();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    public boolean word2PDFByWPS(String srcFilePath, String pdfFilePath) {
        try {
            boolean success = false;
            ActiveXComponent docActiveXComponent = null;
            ActiveXComponent workbook = null;
            try {
                long start = System.currentTimeMillis();
                ComThread.InitSTA();//初始化COM线程
                docActiveXComponent = new ActiveXComponent("KWPS.Application");//初始化exe程序
                Variant[] openParams = new Variant[]{
                        new Variant(srcFilePath),//filePath
                        new Variant(true),
                        new Variant(true)//readOnly
                };
                logger.info("打开文件:" + srcFilePath);
                workbook = docActiveXComponent.invokeGetComponent("Documents").invokeGetComponent("Open", openParams);
                File toFile = new File(pdfFilePath);
                if (toFile.exists()) {
                    toFile.delete();
                }
                workbook.invoke("SaveAs", new Variant(pdfFilePath), new Variant(17));
                long end = System.currentTimeMillis();
                logger.info("固化用时:" + (end - start) / 1000 + "s");
                success = true;
            } catch (ComFailException e) {
                failureLog("WORD固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } catch (Exception e) {
                failureLog("WORD固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } finally {
                if (workbook != null) {
                    workbook.invoke("Close");
                    workbook.safeRelease();
                }
                if (docActiveXComponent != null) {
                    docActiveXComponent.invoke("Quit");
                    docActiveXComponent.safeRelease();
                    logger.info("------------------WORD固化结束------------------");
                }
                ComThread.Release();
            }
            return success;
        } catch (Exception e) {
            failureLog("WORD固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            logger.info("------------------WORD固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }
    public boolean ppt2PDFByWPS(String srcFilePath, String pdfFilePath) {
        try {
            boolean success = false;
            ActiveXComponent pptActiveXComponent = null;
            ActiveXComponent workbook = null;
            boolean readonly = true;
            try {
                long start = System.currentTimeMillis();
                ComThread.InitSTA();//初始化COM线程
                pptActiveXComponent = new ActiveXComponent("KWPP.Application");//初始化exe程序
                logger.info("打开文件:" + srcFilePath);
                workbook = pptActiveXComponent.invokeGetComponent("Presentations").invokeGetComponent("Open", new Variant(srcFilePath), new Variant(readonly));
                File toFile = new File(pdfFilePath);
                if (toFile.exists()) {
                    toFile.delete();
                }
                workbook.invoke("SaveAs", new Variant(pdfFilePath), new Variant(32));
                long end = System.currentTimeMillis();
                logger.info("固化用时:" + (end - start) / 1000 + "s");
                success = true;
            } catch (ComFailException e) {
                failureLog("PPT固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } catch (Exception e) {
                failureLog("PPT固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            } finally {
                if (workbook != null) {
                    workbook.invoke("Close");
                    workbook.safeRelease();
                }
                if (pptActiveXComponent != null) {
                    pptActiveXComponent.invoke("Quit");
                    pptActiveXComponent.safeRelease();
                    logger.info("------------------PPT固化结束------------------");
                }
                ComThread.Release();
            }
            return success;
        } catch (Exception e) {
            failureLog("PPT固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            logger.info("------------------PPT固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }
    public boolean excel2PDFByWPS(String srcFilePath, String pdfFilePath) {
        try {
            long start = System.currentTimeMillis();
            ActiveXComponent et = null;
            Dispatch workbooks = null;
            Dispatch workbook = null;
            ComThread.InitSTA();//初始化COM线程
            try {
                et = new ActiveXComponent("KET.Application");//初始化et.exe程序
                et.setProperty("Visible", new Variant(false));
                workbooks = et.getProperty("Workbooks").toDispatch();
                logger.info("打开文件:" + srcFilePath);
                workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method, new Object[]{srcFilePath, 0, true}, new int[1]).toDispatch();
                Dispatch.call(workbook, "ExportAsFixedFormat", new Object[]{0, pdfFilePath});
                long end = System.currentTimeMillis();
                logger.info("固化用时:" + (end - start) / 1000 + "s");
            } catch (Exception es) {
                es.printStackTrace();
                failureLog("EXCEL固化失败：" + srcFilePath + " ，异常信息：" + es.getMessage());
                return false;
            } finally {
                if (workbook != null) {
                    Dispatch.call(workbook, "Close");
                    workbook.safeRelease();
                }
                if (et != null) {
                    et.invoke("Quit");
                    et.safeRelease();
                    logger.info("------------------EXCEL固化结束------------------");
                }
                ComThread.Release();
            }
            return true;
        } catch (Exception e) {
            failureLog("EXCEL固化失败：" + srcFilePath + " ，异常信息：" + e.getMessage());
            logger.info("------------------EXCEL固化结束------------------");
            e.printStackTrace();
            return false;
        }
    }

    //确定使用哪种office进行固化(优先使用：MS Office)
    public static void checkOffice() {
        if (OFFICE.equals(UNCHECKED)) {
            if (checkInstalled("Word.Application") || checkInstalled("PowerPoint.Application") || checkInstalled("Excel.Application")) {
                OFFICE = MS;
            } else if (checkInstalled("KWPS.Application") || checkInstalled("KWPP.Application") || checkInstalled("KET.Application")) {
                OFFICE = WPS;
            } else {
                OFFICE = NULL;
            }
            String printStr = OFFICE.equals(NULL) ? ">>>>>>>>当前环境未安装Microsoft Office或WPS Office！" : ">>>>>>>>经过检测，当前office类型为：" + OFFICE;
            logger.info(printStr);

//            boolean word = checkInstalled("KWPS.Application");//word
//            boolean ppt = checkInstalled("KWPP.Application");//ppt
//            boolean excel = checkInstalled("KET.Application");//excel
//			if (word || ppt || excel) {//只要有一个安装就视为安装了MS Office
//				OFFICE = WPS;
//			} else {
//                word = checkInstalled("Word.Application");//检测MS Word是否安装
//                ppt = checkInstalled("PowerPoint.Application");
//                excel = checkInstalled("Excel.Application");
//				if (word || ppt || excel) {
//                    OFFICE = MS;
//				} else {
//                    logger.info(">>>>>>>>当前环境未安装Microsoft Office或WPS Office！");
//					OFFICE = NULL;//赋个值以防止再次检测，若后来安装了office，需重启服务来重新检测
//				}
//			}
//            logger.info(">>>>>>>>经过检测，当前office类型为：" + OFFICE);
        } else {
            logger.info(">>>>>>>>未经检测，当前office类型为：" + OFFICE);
        }
    }

    //检测office各个软件是否安装：word、ppt、excel
    public static boolean checkInstalled(String type) {
        boolean isInstalled = true;
        ActiveXComponent app = null;
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent(type);
        } catch (Exception e) {
            e.printStackTrace();
            isInstalled = false;
        } finally {
            if (app != null) {
                app.invoke("Quit");
                app.safeRelease();
            }
            ComThread.Release();
        }
        logger.info("type:" + type + "    " + isInstalled);
        return isInstalled;
    }
}