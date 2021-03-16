package com.wisdom.util;

/**
 * Created by SunK on 2020/7/8 0008.
 */
import java.io.*;
import java.util.*;

import org.apache.tools.zip.*;



public class ZipOption {

    /**
     * 取得指定目录下的所有文件列表，包括子目录.
     *
     * @param baseDir
     *            File 指定的目录
     * @return 包含java.io.File的List
     */
    public List<File> getSubFiles(File baseDir) {
        List<File> ret = new ArrayList<File>();
        // 判断文件是否存在
        if (baseDir.exists()) {
            File[] tmp = baseDir.listFiles();
            if (tmp != null) {
                for (int i = 0; i < tmp.length; i++) {
                    if (tmp[i].isFile()) {
                        ret.add(tmp[i]);
                    }
                    if (tmp[i].isDirectory()) {
                        ret.addAll(getSubFiles(tmp[i]));
                    }
                }
            }
            tmp = null;
            return ret;
        } else {
            return null;
        }
    }

    /**
     * 取得指定目录下的所有包含指定后缀的文件,不包含子目录，
     *
     * @param baseDir
     *            File 指定的目录
     * @param endsStr
     *            文件后缀名
     * @return 包含java.io.File的List
     */
    public List<File> getFiles(File baseDir, String endsStr) {
        List<File> ret = new ArrayList<File>();
        File[] tmp = baseDir.listFiles();
        if (tmp != null) {
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isFile()) {
                    String name = tmp[i].getName();
                    if (name.toLowerCase().endsWith("." + endsStr)) {
                        ret.add(tmp[i]);
                    }
                }
            }
        }
        tmp = null;
        return ret;
    }

    /**
     * 递归调用 取得指定目录下的所有包含指定后缀的文件,含子目录
     *
     * @param baseDir
     *            File 指定的目录
     * @param endsStr
     *            文件后缀名
     * @return 包含java.io.File的List
     */
    public List<File> getallFiles(File baseDir, String endsStr) {
        List<File> ret = new ArrayList<File>();
        File[] tmp = baseDir.listFiles();
        if (tmp != null) {
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isFile()) {
                    String name = tmp[i].getName();
                    if (name.toLowerCase().endsWith("." + endsStr)) {
                        ret.add(tmp[i]);
                    }
                } else if (tmp[i].isDirectory()) {
                    ret.addAll(getallFiles(tmp[i], endsStr));
                }
            }
        }
        tmp = null;
        return ret;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir
     *            指定根目录
     * @param absFileName
     *            相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    private File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
        }
        if (!ret.exists()) {
            ret.mkdirs();
        }
        ret = new File(ret, dirs[dirs.length - 1]);
        return ret;
    }

    /**
     * 复制单个文件 复制后文件夹目录必须已经存在
     *
     * @param oldPath
     *            String 原文件路径 如：c:/fqf.txt
     * @param newPath
     *            String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath) {
        boolean bool = false;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
            File newFile = new File(newPath);
            bool = newFile.exists();
            newFile = null;
            oldfile = null;
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
        return bool;
    }

    /**
     * 复制单个文件 复制后文件夹目录必须已经存在
     *
     * @param src
     *            String 文件
     * @param dst
     *            String 文件
     * @return boolean
     */
    public static boolean copyFile(File src, File dst) {
        InputStream in = null;
        OutputStream out = null;
        int BUFFER_SIZE = 100 * 1024;
        try {
            in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(dst),
                    BUFFER_SIZE);
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 删除路径下的所有文件,不删除根目录
     *
     * @param filepath
     *            文件或目录
     * @throws IOException
     */
    public static void del1(String filepath) throws IOException {

        File f = new File(filepath);// 定义文件路径
        if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
            if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
                f.delete();
            } else {// 若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        del1(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();// 删除文件
                }
                delFile = null;
            }
        } else if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        f = null;
    }

    /**
     * 删除路径下的所有文件,删除根目录
     *
     * @param filepath
     * @throws IOException
     */
    public static void del(String filepath) throws IOException {
        File f = new File(filepath);// 定义文件路径
        if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
            if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
                f.delete();
            } else {// 若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        del(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();// 删除文件
                }
                delFile = null;
            }
            del(filepath);// 递归调用
        } else if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        f = null;
    }

    /**
     * 解压ZIP文件
     *
     * @param zipFileName
     *            String 为需要解压的zip文件
     * @param extPlace
     *            String 为解压后文件的存放路径
     */
    public static void extZipFileList(String zipFileName, String extPlace)
            throws Exception {
        // 验证ZIP压缩文件是否有效
        File fileZip = new File(zipFileName);
        boolean exists = fileZip.exists();// 判断文件/文件夹是否存在
        if (exists) {
            // 创建extPlace目录
            File dirs = new File(extPlace);
            dirs.mkdirs();
            dirs = null;
            // 请空目录
            // ZipOption.del(dirs.getAbsolutePath());

            ZipFile zipFile = null ;

            try {
                zipFile = new ZipFile(zipFileName,"GBK");
                Enumeration e = zipFile.getEntries();
                ZipEntry zipEntry = null;
                while (e.hasMoreElements()) {
                    zipEntry = (ZipEntry) e.nextElement();
                    String entryName = zipEntry.getName().replace(
                            File.separatorChar, '/');
                    String names[] = entryName.split("/");
                    int length = names.length;
                    String path = extPlace;
                    for (int v = 0; v < length; v++) {
                        if (v < length - 1) {
                            path += names[v] + "/";
                            new File(path).mkdir();
                        } else { // 最后一个
                            if (entryName.endsWith("/")) { // 为目录,则创建文件夹
                                new File(extPlace + entryName).mkdir();
                            } else {
                                InputStream in = zipFile
                                        .getInputStream(zipEntry);
                                OutputStream os = new FileOutputStream(
                                        new File(extPlace + entryName));
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    os.write(buf, 0, len);
                                }
                                in.close();
                                os.close();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if(zipFile!=null)
                    zipFile.close();
            }
            if(zipFile!=null)
                zipFile.close();
        } else {

        }
        fileZip = null;
    }

    /**
     * 压缩电子文件
     *
     * @param WG11
     *            String 电子文件标识
     * @param mainTable
     *            String 源表
     * @param vType
     *            String 立档单位名称
     * @param dalx
     *            String 档案类型
     * @param orderNO
     *            Integer 压缩文件顺序号
     *
     *
     */
    public static boolean expZipFileList2(String WG11, String mainTable,
                                          String vType, String dalx, int orderNO) {
        boolean bool = false;

        ZipOption zipOp = new ZipOption();
        String basePath = "";
        String outFilename = ConfigValue
                .getValue("titans.lucene.directory.index")
                + "\\" + mainTable + "\\Media\\" + orderNO + ".zip";
        try {
            basePath = ConfigValue.getValue("titans.lucene.directory.zl")
                    + "\\" + vType + "\\" + dalx + "\\" + "200"
                    + WG11.substring(0, 3) + "\\" + WG11;
        } catch (Exception e) {
        }
        File bases = new File(basePath);
        if (bases.isDirectory()) {
            List list = zipOp.getSubFiles(bases);
            if (list.size() > 0) {
                try {
                    ZipOutputStream out = new ZipOutputStream(
                            new FileOutputStream(outFilename));
                    byte[] buf = new byte[1024];
                    for (int i = 0; i < list.size(); i++) {
                        String filePath = list.get(i).toString();
                        File absoluteFile = new File(filePath);
                        if (absoluteFile.exists()) {
                            FileInputStream in = new FileInputStream(filePath);
                            out.putNextEntry(new ZipEntry(absoluteFile
                                    .getName()));
                            int len;
                            while ((len = in.read(buf)) > 1) {
                                out.write(buf);
                            }
                            out.closeEntry();
                            in.close();
                        }
                    }
                    out.close();

                    File zipFile = new File(outFilename);
                    bool = zipFile.exists();
                    zipFile = null;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        bases = null;
        return bool;
    }

    /**
     *
     * 压缩电子文件为ZIP包
     *
     * @param directory
     *            需要压缩的目录
     * @param zipFileName
     *            ZIP压缩文件完整路径名
     * @return
     */
    public static boolean expZip(String directory, String zipFileName) {
        boolean bool = false;
        try {
            File file = new File(directory);
            boolean validate = zipFileName.toLowerCase().endsWith(".zip");
            if (!file.exists() || !validate || !file.isDirectory()) {
                return false;
            } else {

                directory = directory.replace(File.separatorChar, '/');
                zipFileName = zipFileName.replace(File.separatorChar, '/');
                int x = zipFileName.lastIndexOf("/");
                if (x <= -1) {
                    return false;
                } else {
                    String zipdir = zipFileName.substring(0, x);
                    File zipdirs = new File(zipdir);
                    zipdirs.mkdirs();
                    zipdirs = null;
                }

                OutputStream os = new FileOutputStream(zipFileName);
                BufferedOutputStream bs = new BufferedOutputStream(os);
                ZipOutputStream zo = new ZipOutputStream(bs);
                ZipOption.zip(directory, new File(directory), zo, true, true);
                zo.closeEntry();
                zo.close();
                bs.close();
                os.close();

                File exit = new File(zipFileName);
                bool = exit.exists();
                exit = null;
            }
            file = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bool;
    }

    /**
     *
     * 压缩电子文件为ZIP包
     *
     * @param files
     *            需要压缩的文件集合
     * @param zipFileName
     *            ZIP压缩文件完整路径名
     * @return
     */
    public static boolean expZip(File[] files, String directory,
                                 String zipFileName) {
        boolean bool = false;
        try {
            File file = new File(directory);
            ZipOption zipOp = new ZipOption();
            boolean validate = zipFileName.toLowerCase().endsWith(".zip");
            if (!file.exists() || !validate || !file.isDirectory()) {
                return false;
            } else {
                zipFileName = zipFileName.replace(File.separatorChar, '/');
                int x = zipFileName.lastIndexOf("/");
                if (x <= -1) {
                    return false;
                } else {
                    String zipdir = zipFileName.substring(0, x);
                    File zipdirs = new File(zipdir);
                    zipdirs.mkdirs();
                    zipdirs = null;
                }
                OutputStream os = new FileOutputStream(zipFileName);
                BufferedOutputStream bs = new BufferedOutputStream(os);
                ZipOutputStream zo = new ZipOutputStream(bs);
                ZipOption.zip(files, new File(directory), zo, true, true);
                zo.closeEntry();
                zo.close();
                bs.close();
                os.close();

                File exit = new File(zipFileName);
                bool = exit.exists();
                exit = null;
            }
            file = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * @param flist
     *            要压缩的文件集合
     * @param basePath
     *            如果path是目录,它一般为new File(path), 作用是:使输出的zip文件以此目录为根目录,
     *            如果为null它只压缩文件, 不解压目录.
     * @param zo
     *            压缩输出流
     * @param isRecursive
     *            是否递归
     * @param isOutBlankDir
     *            是否输出空目录, 要使输出空目录为true,同时baseFile不为null.
     * @throws IOException
     */
    public static void zip(File[] flist, File basePath, ZipOutputStream zo,
                           boolean isRecursive, boolean isOutBlankDir) throws IOException {
        if (flist.length > 0) {
            File[] files = new File[0];
            files = flist;
            byte[] buf = new byte[1024];
            int len;
            // System.out.println("baseFile: "+baseFile.getPath());
            for (int i = 0; i < files.length; i++) {
                String pathName = "";
                if (basePath != null) {
                    if (basePath.isDirectory()) {
                        pathName = files[i].getPath().substring(
                                basePath.getPath().length() + 1);
                    } else {// 文件
                        pathName = files[i].getPath().substring(
                                basePath.getParent().length() + 1);
                    }
                } else {
                    pathName = files[i].getName();
                }
                if (files[i].isDirectory()) {
                    if (isOutBlankDir && basePath != null) {
                        zo.putNextEntry(new ZipEntry(pathName + "/")); // 可以使空目录也放进去
                    }
                    if (isRecursive) { // 递归
                        zip(files[i].getPath(), basePath, zo, isRecursive,
                                isOutBlankDir);
                    }
                } else {
                    FileInputStream fin = new FileInputStream(files[i]);
                    zo.putNextEntry(new ZipEntry(pathName));
                    while ((len = fin.read(buf)) > 0) {
                        zo.write(buf, 0, len);
                    }
                    fin.close();
                }
            }
            files = null;
        }
    }

    /**
     * @param path
     *            要压缩的路径, 可以是目录, 也可以是文件.
     * @param basePath
     *            如果path是目录,它一般为new File(path), 作用是:使输出的zip文件以此目录为根目录,
     *            如果为null它只压缩文件, 不解压目录.
     * @param zo
     *            压缩输出流
     * @param isRecursive
     *            是否递归
     * @param isOutBlankDir
     *            是否输出空目录, 要使输出空目录为true,同时baseFile不为null.
     * @throws IOException
     */
    public static void zip(String path, File basePath, ZipOutputStream zo,
                           boolean isRecursive, boolean isOutBlankDir) throws IOException {

        File inFile = new File(path);

        File[] files = new File[0];
        if (inFile.isDirectory()) { // 是目录
            files = inFile.listFiles();
        } else if (inFile.isFile()) { // 是文件
            files = new File[1];
            files[0] = inFile;
        }
        byte[] buf = new byte[1024];
        int len;
        // System.out.println("baseFile: "+baseFile.getPath());
        for (int i = 0; i < files.length; i++) {
            String pathName = "";
            if (basePath != null) {
                if (basePath.isDirectory()) {
                    pathName = files[i].getPath().substring(
                            basePath.getPath().length() + 1);
                } else {// 文件
                    pathName = files[i].getPath().substring(
                            basePath.getParent().length() + 1);
                }
            } else {
                pathName = files[i].getName();
            }
            if (files[i].isDirectory()) {
                if (isOutBlankDir && basePath != null) {
                    zo.putNextEntry(new ZipEntry(pathName + "/")); // 可以使空目录也放进去
                }
                if (isRecursive) { // 递归
                    zip(files[i].getPath(), basePath, zo, isRecursive,
                            isOutBlankDir);
                }
            } else {
                FileInputStream fin = new FileInputStream(files[i]);
                zo.putNextEntry(new ZipEntry(pathName));
                while ((len = fin.read(buf)) > 0) {
                    zo.write(buf, 0, len);
                }
                fin.close();
            }
        }
        inFile = null;
        files = null;
    }

    public static String extractZipComment (String filename) {
        String retStr = null;
        try {
            File file = new File(filename);
            int fileLen = (int)file.length();

            FileInputStream in = new FileInputStream(file);

            /* The whole ZIP comment (including the magic byte sequence)
             * MUST fit in the buffer
             * otherwise, the comment will not be recognized correctly
             *
             * You can safely increase the buffer size if you like
             */
            byte[] buffer = new byte[Math.min(fileLen, 8192)];
            int len;

            in.skip(fileLen - buffer.length);

            if ((len = in.read(buffer)) > 0) {
                retStr = getZipCommentFromBuffer (buffer, len);
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retStr;
    }

    private static String getZipCommentFromBuffer (byte[] buffer, int len) {
        byte[] magicDirEnd = {0x50, 0x4b, 0x05, 0x06};
        int buffLen = Math.min(buffer.length, len);
        //Check the buffer from the end
        for (int i = buffLen-magicDirEnd.length-22; i >= 0; i--) {
            boolean isMagicStart = true;
            for (int k=0; k < magicDirEnd.length; k++) {
                if (buffer[i+k] != magicDirEnd[k]) {
                    isMagicStart = false;
                    break;
                }
            }
            if (isMagicStart) {
                //Magic Start found!
                int commentLen = buffer[i+20] + buffer[i+22]*256;
                int realLen = buffLen - i - 22;
                System.out.println ("ZIP comment found at buffer position " + (i+22) + " with len="+commentLen+", good!");
                if (commentLen != realLen) {
                    System.out.println ("WARNING! ZIP comment size mismatch: directory says len is "+
                            commentLen+", but file ends after " + realLen + " bytes!");
                }
                String comment = new String (buffer, i+22, Math.min(commentLen, realLen));
                return comment;
            }
        }
        System.out.println ("ERROR! ZIP comment NOT found!");
        return null;
    }


    public static void main(String[] args) {
        ZipOption opt = new ZipOption();
    }
}
