package com.wisdom.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by SunK on 2018/7/19 0019.
 */
public class ZipUtils {

	private final static Logger logger = LoggerFactory.getLogger(ZipUtils.class);

	private static final int BUFFER_SIZE = 2 * 1024;

	/**
	 * 递归压缩方法
	 *
	 * @param sourceFile
	 *            源文件
	 * @param zos
	 *            zip输出流
	 * @param name
	 *            压缩后的名字
	 * @param KeepDirStructure
	 *            是否保留原来的目录结构，false:所有文件在压缩包根目录（同名文件存在时报错）
	 */
	private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure)
			throws Exception {
		byte[] bytes = new byte[BUFFER_SIZE];
		if (sourceFile.isFile()) {
			// 向zip输出流中添加一个zip实体，参数列表中name为zip实体的名字
			zos.putNextEntry(new ZipEntry(name));
			// copy文件到zip输出流中
			int len;
			FileInputStream fis = new FileInputStream(sourceFile);
			while ((len = fis.read(bytes)) != -1) {
				zos.write(bytes, 0, len);
			}
			zos.closeEntry();
			fis.close();
		} else {
			// listFiles()方法是返回某个目录下所有文件和目录的绝对路径，返回的是File数组
			File[] files = sourceFile.listFiles();
			if (files == null || files.length == 0) {
				// 判断是否保留原目录结构
				if (KeepDirStructure) {
					// 空文件夹的处理
					zos.putNextEntry(new ZipEntry(name + "/"));
					// 没有文件，不需要文件的copy
					// 关闭当前的ZIP条目，并为编写下一个条目而定位流。
					zos.closeEntry();
				}
			} else {
				for (File file : files) {
					// 判断是否保留原目录结构
					if (KeepDirStructure) {
						// 注意：file.getName()前面需要带上父文件夹的文件加一个斜杠/
						// 不然最后压缩包中就不能保留原来的目录结构，即文件全在压缩包的根目录
						compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
					} else {
						compress(file, zos, file.getName(), KeepDirStructure);
					}
				}
			}
		}
	}

	/**
	 * 方法一
	 *
	 * @param srcDir
	 *            压缩文件夹路径
	 * @param out
	 *            压缩文件输出流(压缩文件路径及压缩包名)
	 * @param KeepDirStructure
	 *            是否保留原来的目录结构，false:所有文件在压缩包根目录（同名文件存在时报错）
	 * @throws RuntimeException
	 *             压缩失败会抛出运行时异常
	 */
	public static boolean toZip(String srcDir, OutputStream out, boolean KeepDirStructure) throws RuntimeException {
		// long start = System.currentTimeMillis();
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(out);
			File sourceFile = new File(srcDir);
			compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
			// long ent = System.currentTimeMillis();
			// System.out.println("压缩完成,耗时：" + (ent - start) + "ms");
		} catch (Exception e) {
			throw new RuntimeException("Zip erro from ZipUtils", e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
					out.close();
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * 方法二
	 *
	 * @param srcFiles
	 *            需要压缩的文件列表
	 * @param out
	 *            压缩文件输出流(压缩文件路径及压缩包名)
	 * @throws RuntimeException
	 *             压缩失败会抛出运行时异常
	 */
	public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
		// long start = System.currentTimeMillis();
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(out);
			for (File srcFile : srcFiles) {
				byte[] bytes = new byte[BUFFER_SIZE];
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int len;
				FileInputStream in = new FileInputStream(srcFile);
				while ((len = in.read(bytes)) != -1) {
					zos.write(bytes, 0, len);
				}
				// 关闭当前的ZIP条目，并为编写下一个条目而定位流。
				zos.closeEntry();
				in.close();
			}
			// long end = System.currentTimeMillis();
			// System.out.println("压缩完成,耗时：" + (end - start) + "ms");
		} catch (Exception e) {
			throw new RuntimeException("Zip erro from ZipUtils", e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		}
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

	// 删除文件夹
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	// 删除指定文件夹下的所有文件
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
	 * 导出压缩包
	 *
	 * @param file
	 * @param zipName
	 * @throws IOException
	 */
	public void ImportZip(File file, String zipName) throws IOException {
		if (file.isFile() || "".equals(zipName)) {
			throw new NullPointerException();
		}
		InputStream in = new FileInputStream(file);
		ZipOutputStream zput = null;
		zput = new ZipOutputStream(new FileOutputStream(zipName));
		ZipEntry zipEntry = new ZipEntry(file.getName());
		zput.putNextEntry(zipEntry);
		int temp = 0;
		while ((temp = in.read()) != -1) {
			zput.write(temp);
		}
		in.close();
		zput.close();
	}

	/**
	 * 解压zip格式压缩包 对应的是ant.jar
	 */
	public static void unzip(String sourceZip, String destDir) throws Exception {
		try {
			File f = new File(destDir);
			f.mkdirs();
			if (!f.exists()) {
				throw new RuntimeException("createXml()---创建文件夹失败");
			}
			Project p = new Project();
			Expand e = new Expand();
			e.setProject(p);
			e.setSrc(new File(sourceZip));
			e.setOverwrite(false);
			e.setDest(new File(destDir));
			/*
			 * ant下的zip工具默认压缩编码为UTF-8编码， 而winRAR软件压缩是用的windows默认的GBK或者GB2312编码
			 * 所以解压缩时要制定编码格式
			 */
			e.setEncoding("gbk");
			e.execute();
		} catch (Exception e) {
			throw e;
		}
	}

	public static void unZip4j(String sourceZip,String destDir) throws Exception{
		ZipFile zip = new ZipFile(sourceZip);
		zip.setFileNameCharset("UTF-8");
		if (!zip.isValidZipFile()) {
			throw new ZipException("文件不合法或不存在");
		}
		zip.extractAll(destDir);

	}
	public static void unZip4jx(String sourceZip,String destDir) throws Exception{
		// 保证文件夹路径最后是"/"或者"\"
		char lastChar = destDir.charAt(destDir.length() - 1);
		if (lastChar != '/' && lastChar != '\\') {
			destDir += File.separator;
		}
		ZipFile zip = new ZipFile(sourceZip);
		zip.setFileNameCharset("gbk");
		if (!zip.isValidZipFile()) {
			throw new ZipException("文件不合法或不存在");
		}
		zip.extractAll(destDir);

	}

	public static void unZip4jUTF8(String sourceZip,String destDir) throws Exception{
		ZipFile zip = new ZipFile(sourceZip);
		zip.setFileNameCharset("UTF-8");
		if (!zip.isValidZipFile()) {
			throw new ZipException("文件不合法或不存在");
		}
		zip.extractAll(destDir);

	}
	/**
	 *
	 * @param sourceFile
	 *            原文路径
	 * @param destDir
	 *            解压路径
	 * @throws Exception
	 */
	public static void deCompress(String sourceFile, String destDir) throws Exception {
		// 保证文件夹路径最后是"/"或者"\"
		char lastChar = destDir.charAt(destDir.length() - 1);
		if (lastChar != '/' && lastChar != '\\') {
			destDir += File.separator;
		}
		ZipUtils.unZip4jUTF8(sourceFile, destDir);
	}

	public static void deCompress(String sourceFile, String destDir,String encoding) throws Exception {
		// 保证文件夹路径最后是"/"或者"\"
		char lastChar = destDir.charAt(destDir.length() - 1);
		if (lastChar != '/' && lastChar != '\\') {
			destDir += File.separator;
		}
		ZipUtils.unZip4jx(sourceFile, destDir,encoding);
	}

	public static void unZip4jx(String sourceZip,String destDir,String encoding) throws Exception{
		// 保证文件夹路径最后是"/"或者"\"
		char lastChar = destDir.charAt(destDir.length() - 1);
		if (lastChar != '/' && lastChar != '\\') {
			destDir += File.separator;
		}
		ZipFile zip = new ZipFile(sourceZip);
		zip.setFileNameCharset(encoding);
		if (!zip.isValidZipFile()) {
			throw new ZipException("文件不合法或不存在");
		}
		zip.extractAll(destDir);

	}



	private static byte[] ZIP_HEADER_1 = new byte[] { 80, 75, 3, 4 };
	private static byte[] ZIP_HEADER_2 = new byte[] { 80, 75, 5, 6 };
	/**
	 * 判断文件是否为一个压缩文件
	 *
	 * @param file
	 * @return
	 */
	public static boolean isArchiveFile(File file) {

		if (file == null) {
			return false;
		}

		if (file.isDirectory()) {
			return false;
		}

		boolean isArchive = false;
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			byte[] buffer = new byte[4];
			int length = input.read(buffer, 0, 4);
			if (length == 4) {
				isArchive = (Arrays.equals(ZIP_HEADER_1, buffer)) || (Arrays.equals(ZIP_HEADER_2, buffer));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}

		return isArchive;
	}
}