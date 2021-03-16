package com.wisdom.util;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SunK on 2018/7/13 0013.
 */
public class FileUtil {

	private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	public final static String ExcelVersion2017 = "xlsx";
	public final static String ExcelVersion2003 = "xls";

	// 删除文件夹
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 删除指定文件夹下所有文件
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 获取文件后缀名
	 *
	 * @param file
	 * @return
	 */
	public static String getExcelVersion(File file) {
		String fileName = file.getName();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

		return suffix;
	}

	/**
	 * @param targetSystem
	 *            目标系统
	 * @param tempName
	 *            模板名称
	 * @return
	 */
	public static File FindExportTemplateFile(String targetSystem, String tempName) throws IOException {
		File TemplateFile = ResourceUtils.getFile("classpath:templates" + targetSystem + "/" + tempName + ".xml");
		return TemplateFile;
	}

	public static String[] getXmlTemp(List<Object> tempList) {
		String fieldcode = "";// 字段编号
		String fieldname = "";// 字段名称
		if (!tempList.isEmpty() && tempList.size() > 0) {
			HashMap map = null;
			for (int i = 0; i < tempList.size(); i++) {
				map = (HashMap) tempList.get(i);
				String code = map.get("fieldcode").toString();
				String name = map.get("fieldname").toString();
				fieldcode += "," + code;
				fieldname += "," + name;
			}
			fieldcode = fieldcode.substring(1);
			fieldname = fieldname.substring(1);
			if (!fieldcode.toLowerCase().contains("atype")) {
				fieldcode += ",atype";
				fieldname += ",档案类型";
			}
			if (!fieldcode.toLowerCase().contains("sortanme")) {
				fieldcode += ",sortname";
				fieldname += ",分类名称";
			}
		}
		String[] fieldcodearry = fieldcode.toLowerCase().split(",");// 字段编号数组
		String[] fieldnamearry = fieldname.split(",");// 字段名称数据
		return null;
	}

	/**
	 * 拷贝文件
	 *
	 * @param src
	 *            原文件路径
	 * @param usefilepath
	 *            用户输入的文件名
	 * @param filename
	 *            原文件名带后缀
	 * @throws IOException
	 */
	public static void CopyFile(File src, String usefilepath, String filename) throws IOException {
		if (usefilepath == null || filename == null) {
			throw new RuntimeException("CopyFile()---拷贝文件失败");
		}
		String dir = ConfigValue.getPath("system.document.rootpath");
		String path = dir +"/OAFile"+"/Excel导出/临时目录/" + usefilepath;//
		InputStream is = null;
		OutputStream os = null;
		// 创建临时路径文件夹
		// 创建临时路径文件夹
		if (!(new File(path)).exists()) {
			(new File(path)).mkdirs();
		}
		try {
			String newpath = path + "/" + filename;
			is = new FileInputStream(src);
			os = new FileOutputStream(newpath);
			byte[] by = new byte[1024];// 准备车子
			int len = 0; // 假设车上没有人
			try {
				while (-1 != (len = is.read(by))) { // 开始慢慢下车和上车
					os.write(by, 0, len); // 下车
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			/*} finally {
				os.flush();
				is.close();
				os.close();*/
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			logger.info("文件未找到");
		}finally {
			os.flush();
			is.close();
			os.close();
		}
	}

	public static void CopyFileXml(File src, String usefilepath, String filename) throws IOException {
		if (usefilepath == null || filename == null) {
			throw new RuntimeException("CopyFile()---拷贝文件失败");
		}
		String dir = ConfigValue.getPath("system.document.rootpath");
		String path = dir +"/OAFile"+ "/Xml导出/临时目录/" + usefilepath;//
		InputStream is = null;
		OutputStream os = null;
		// 创建临时路径文件夹
		if (!(new File(path)).exists()) {
			(new File(path)).mkdirs();
		}
		try {
			String newpath = path + "/" + filename;
			is = new FileInputStream(src);
			os = new FileOutputStream(newpath);
			byte[] by = new byte[1024];// 准备车子
			int len = 0; // 假设车上没有人
			try {
				while (-1 != (len = is.read(by))) { // 开始慢慢下车和上车
					os.write(by, 0, len); // 下车
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			/*} finally {
				os.flush();
				is.close();
				os.close();*/
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		}finally {
			os.flush();
			is.close();
			os.close();
		}
	}

	public static void CopyDataopenFile(File src, String usefilepath, String filename) throws IOException {
		if (usefilepath == null || filename == null) {
			throw new RuntimeException("CopyFile()---拷贝文件失败");
		}
		String dir = ConfigValue.getPath("system.document.rootpath");
		String path =
				dir + File.separator + "datarelease" + File.separator + "dataopen" +File.separator + "临时目录" + File.separator + usefilepath;
		InputStream is = null;
		OutputStream os = null;
		// 创建临时路径文件夹
		if (!(new File(path)).exists()) {
			(new File(path)).mkdirs();
		}
		try {
			String newpath = path + "/" + filename;
			is = new FileInputStream(src);
			os = new FileOutputStream(newpath);
			byte[] by = new byte[1024];// 准备车子
			int len = 0; // 假设车上没有人
			try {
				while (-1 != (len = is.read(by))) { // 开始慢慢下车和上车
					os.write(by, 0, len); // 下车
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			} finally {
				os.flush();
				is.close();
				os.close();
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 *
	 * @param sourcePath
	 *            源目录
	 * @param newPath
	 *            新目录
	 * @throws IOException
	 */
	public static void copyDir(String sourcePath, String newPath) throws IOException {
		File file = new File(sourcePath);
		String[] filePath = file.list();

		if (!(new File(newPath)).exists()) {
			(new File(newPath)).mkdirs();
		}

		for (int i = 0; i < filePath.length; i++) {
			if ((new File(sourcePath + file.separator + filePath[i])).isDirectory()) {
				copyDir(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
			}

			if (new File(sourcePath + file.separator + filePath[i]).isFile()) {
				copyFile(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
			}

		}
	}

	public static void copyFile(String oldPath, String newPath) throws IOException {
		/*
		 * File f = new File(newPath); f.mkdirs(); if (!f.exists()) { throw new
		 * RuntimeException("createXml()---创建文件夹失败"); }
		 */
		File oldFile = new File(oldPath);
		File file = new File(newPath);
		FileInputStream in = new FileInputStream(oldFile);
		FileOutputStream out = new FileOutputStream(file);
		byte[] buffer = new byte[2097152];
		int readByte = 0;
		while ((readByte = in.read(buffer)) != -1) {
			out.write(buffer, 0, readByte);
		}
		in.close();
		out.close();
	}

	// 获取目录下的所有文件
	public static List<String> getFile(String path) {
		List<String> fileList = new ArrayList<>();
		File file = new File(path);
		File[] tempList = file.listFiles();
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				fileList.add(tempList[i].toString());
			}
		}
		return fileList;
	}

	// 获取目录下的所有文件夹
	public static List<String> getFolder(String path) {
		List<String> fileList = new ArrayList<>();
		File file = new File(path);
		File[] tempList = file.listFiles();
		if(tempList!=null) {
			for (int i = 0; i < tempList.length; i++) {
				if (tempList[i].isDirectory()) {
					fileList.add(tempList[i].toString());
				}
			}
		}
		return fileList;
	}

	public static void CopyFile(String srcPath, String file, String filename) throws IOException {
		CopyFile(new File(srcPath), file, filename);
	}

	public static  boolean isexists(String srcPath){
		File file = new File(srcPath);
		return file.exists();
	}

	public static void main(String[] ages){
		String path ="E:\\document\\OAFile\\导入失败\\document\\a\\";
		File file = new File(path);
		if(!file .exists()  && !file .isDirectory()){
			file.mkdirs();
		}
		/*String filePath ="E:\\document\\OAFile\\导入失败\\document\\a";
		String paths[] = filePath.split("\\\\");
		String dir = paths[0];
		for (int i = 0; i < paths.length - 1; i++) {//注意此处循环的长度
			try {
				dir = dir + "/" + paths[i + 1];
				File dirFile = new File(dir);
				if (!dirFile.exists()) {
					dirFile.mkdir();
					System.out.println("创建目录为：" + dir);
				}
			} catch (Exception err) {
				System.err.println("文件夹创建发生异常");
			}
		}
		File fp = new File(filePath);
		if(!fp.exists()){
			 // 文件不存在，执行下载功能
		}else{
			// 文件存在不做处理
		}*/



	}
	//判断目录中的文件名是否有乱码
	public static boolean isMessyCode(File[] files) {
		boolean b = false;
		for (int i = 0;i < files[0].listFiles().length; i++){
			if (files[0].listFiles().length > 1){
				//判断是乱码 (GBK包含全部中文字符；UTF-8则包含全世界所有国家需要用到的字符。)
				if (!(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(files[0].getName()))) {
					return true;
				}
				for (int y =0;y < files[0].listFiles()[1].listFiles().length; y++){
					String filepath = files[0].listFiles()[1].listFiles()[y].getPath();
					if (!"".equals(filepath)) {
						//判断是乱码 (GBK包含全部中文字符；UTF-8则包含全世界所有国家需要用到的字符。)
						if (!(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(filepath))) {
							b = true;
						}
					}
				}
			}
		}
		return b;
	}

	/**
	 * 传入带后缀名的文件名，用于识别是文件类型
	 * @param fileName
	 * @return
	 */
	public static String identifyFileType(final String fileName)
	{
		final Tika defaultTika = new Tika();
		return defaultTika.detect(fileName);
	}

	public static int getMediaNumByFilename(String filename) {
		String types = FileUtil.identifyFileType(filename);
		if(null != types && types.startsWith("image")){//图片
			return 3;
		}
		else if(null != types && types.startsWith("video")){//视频
			return 1;
		}
		else if(null != types && types.startsWith("audio")){//音频
			return 2;
		}
		return 0;
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
	 * 取得指定目录下的所有包含指定后缀的文件,不包含子目录，
	 *
	 * @param baseDir
	 *            File 指定的目录
	 * @param endsStr
	 *            文件后缀名
	 * @return 包含java.io.File的List
	 */
	public static List<File> getFiles(File baseDir, String endsStr) {
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
}