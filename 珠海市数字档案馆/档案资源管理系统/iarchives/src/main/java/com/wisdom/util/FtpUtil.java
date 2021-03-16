package com.wisdom.util;

import com.wisdom.web.entity.Tb_oa_record;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import sun.net.ftp.FtpClient;

import java.io.*;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 链接ftp服务器
 */
public class FtpUtil {



    /**
     * 获取FTPClient对象
     *
     * @param ftpHost     FTP主机服务器
     * @param ftpPassword FTP 登录密码
     * @param ftpUserName FTP登录用户名
     * @param ftpPort     FTP端口 默认为21
     * @return
     */
    public static FTPClient getFTPClient(String ftpHost, String ftpUserName,
                                         String ftpPassword, int ftpPort) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
            ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                System.out.println("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
            } else {
                System.out.println("FTP连接成功。");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("FTP的IP地址可能错误，请正确配置。");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("FTP的端口错误,请正确配置。");
        }
        return ftpClient;
    }

    /**
     * 下载文件
     *
     * @param ftpHost     ftp服务器地址
     * @param ftpUserName anonymous匿名用户登录，不需要密码。administrator指定用户登录
     * @param ftpPassword 指定用户密码
     * @param ftpPort     ftp服务员器端口号
     * @param ftpPath     ftp文件存放物理路径
     * @param localPath   文件路径
     * @param fileName    文件输入流，即从本地服务器读取文件的IO输入流
     */
    public static void downloadFile(String ftpHost, String ftpUserName,
                                    String ftpPassword, int ftpPort, String ftpPath, String localPath,
                                    String fileName) {
        FTPClient ftpClient = null;

        try {
            ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(ftpPath);

            File localFile = new File(localPath + File.separatorChar + fileName);
            OutputStream os = new FileOutputStream(localFile);
            ftpClient.retrieveFile(fileName, os);
            os.close();
            ftpClient.logout();

        } catch (FileNotFoundException e) {
            System.out.print("没有找到" + ftpPath + "文件");
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.print("连接FTP失败.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("文件读取错误。");
            e.printStackTrace();
        }
    }


    /**
     * 上传文件
     *
     * @param ftpHost     ftp服务器地址
     * @param ftpUserName anonymous匿名用户登录，不需要密码。administrator指定用户登录
     * @param ftpPassword 指定用户密码
     * @param ftpPort     ftp服务员器端口号
     * @param ftpPath     ftp文件存放物理路径
     * @param fileName    文件路径
     * @param input       文件输入流，即从本地服务器读取文件的IO输入流
     */
    public static void uploadFile(String ftpHost, String ftpUserName,
                                  String ftpPassword, int ftpPort, String ftpPath,
                                  String fileName, InputStream input) {
        FTPClient ftp = null;
        try {
            ftp = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
            ftp.changeWorkingDirectory(ftpPath);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            fileName = new String(fileName.getBytes("GBK"), "iso-8859-1");
            ftp.storeFile(fileName, input);
            input.close();
            ftp.logout();
            System.out.println("upload succes!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取文件夹所有文件
    public static FTPFile[] getFtpFiles(String ftpPathFile) {

        return null;
    }


    /**
     * method  以GBK编码传递过来字符转换成ISO-8859-1
     *
     * @param s 要转换的字符串
     * @return 以编码ISO-8859-1返回字符符串
     */
    private String unchangechar(String s) {
        if (s == null || s.length() == 0) return "";
        boolean isISO = false;
        isISO = isChina(s);
        if (!isISO) return s;
        try {
            byte[] bytes = s.getBytes("GBK");
            return new String(bytes, "ISO-8859-1");
        } catch (Exception e) {

            return s;
        }
    }

    /**
     * method  判断字符串是否是汉字
     *
     * @param str 要转换的字符串
     * @return 是汉字返回true 否则返回false
     */
    private boolean isChina(String str) {
        char[] chars = str.toCharArray();
        boolean isGB2312 = false;
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes = ("" + chars[i]).getBytes();
            if (bytes.length == 2) {
                int[] ints = new int[2];
                ints[0] = bytes[0] & 0xff;
                ints[1] = bytes[1] & 0xff;
                if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40 && ints[1] <= 0xFE) {
                    isGB2312 = true;
                    break;
                }
            }
        }
        return isGB2312;
    }



    //检查文件是否重复
    public static boolean createsaveFilenameXml(String filenames) {
        boolean b = false;
        FileInputStream out = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            String dir = ConfigValue.getPath("system.document.rootpath");//E:/document
            String filenmae = dir + File.separator +"OAFile"+File.separator+ "OA接收" + File.separator + "保存文件名.txt";
            File file = new File(filenmae);
            if (!file.exists()) {//不存在就创建新文件
                file.createNewFile();
            }
            // 读取文件内容 (输入流)
            out = new FileInputStream(file);
            isr = new InputStreamReader(out, "UTF-8");
            reader = new BufferedReader(isr, 5 * 1024);
            String lineTxt = null;
            while ((lineTxt = reader.readLine()) != null) {
                String str = lineTxt.replaceAll("\uFEFF","");
                if (str.equals(filenames)) {
                    b = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return b;
    }


    public static void addFileName(String addfilename) {
        String dir = ConfigValue.getPath("system.document.rootpath");//E:/document
        String filenmae = dir + File.separator +"OAFile"+File.separator+ "OA接收" + File.separator + "保存文件名.txt";
        File file = new File(filenmae);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);

            fileWriter.write(addfilename);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void addErrorFileName(String addfilename) {
        String dir = ConfigValue.getPath("system.document.rootpath");//E:/document
        String filenmae = dir + File.separator +"OAFile"+File.separator+ "OA接收" + File.separator + "ErrorFile.txt";
        File file = new File(filenmae);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);

            fileWriter.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+addfilename+ "\r\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addErrorLog(String addfilename) {
        String dir = ConfigValue.getPath("system.document.rootpath");//E:/document
        String filenmae = dir + File.separator +"OAFile"+File.separator+ "OA接收" + File.separator + "ErrorLog.txt";
        File file = new File(filenmae);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);

            fileWriter.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+addfilename+ "\r\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] age) throws Exception {
        boolean b = createsaveFilenameXml("20090523164907.zip");
        if (b) {
            System.out.println("xxx");
        }
        String aa="\uFEFF20090523164907.zip";
        String bb="20090523164907.zip";
//        String a = "0123456.xml";
//        StringBuffer b = new StringBuffer(a).insert(a.lastIndexOf("."), "已接收");
//        System.out.println(b);
        /*String ftpHost = "127.0.0.1"; //ftp服务器地址
        int ftpPort = 21;//ftp服务员器端口号
        String ftpUserName = "sun";//anonymous匿名用户登录，不需要密码。administrator指定用户登录
        String ftpPassword = "774229444";//指定用户密码
        String ftpPath = "/测试ftp/oa文档/泰坦档案软件接口资料/20090523164907";

        FTPClient ftpClient = FtpUtil.getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
        ftpClient.setControlEncoding("utf-8");
        boolean b=ftpClient.changeWorkingDirectory(new String(ftpPath.getBytes("GBK"),"ISO-8859-1"));
        FTPFile[] files=ftpClient.listFiles();
        for(FTPFile ftpFile:files){
            if(ftpFile.isFile()){//是xml文件
                File file= new File("F:\\ftp\\demo.xml");
                OutputStream ios = new FileOutputStream(file);
                ftpClient.retrieveFile(ftpFile.getName(), ios);
                System.out.println("文件："+new String(ftpPath.getBytes("GBK"),"ISO-8859-1")+File.separator+ftpFile.getName());
            }
            if(ftpFile.isDirectory()){
                System.out.println("目录："+ftpFile.getName());
            }
        }*/
    }

    public static void appdFileName(String addfilename) {
        String dir = ConfigValue.getPath("system.document.rootpath");//E:/document
        String filenmae = dir + File.separator + "OAFile" + File.separator + "OA接收" + File.separator + "保存文件名.txt";
        File file = new File(filenmae);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);

            fileWriter.write(addfilename);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
