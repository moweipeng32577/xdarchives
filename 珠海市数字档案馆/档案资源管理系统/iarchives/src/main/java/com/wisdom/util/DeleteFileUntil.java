package com.wisdom.util;

import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yl on 2018/5/14. 删除文件工具类
 */
public class DeleteFileUntil {
	
	private final static Logger logger = LoggerFactory.getLogger(DeleteFileUntil.class);
	
	/**
	 * 彻底销毁文件(即使使用恢复软件，也只能恢复错误内容的文件)
	 *
	 * @param filepath
	 *            文件路径
	 * @return
	 */
	public static void completeDestroyDocument(String filepath) throws IOException {
		RandomAccessFile raf = null;
		File file = new File(filepath);
		if (file.exists()) {
			SecureRandom random = new SecureRandom();
			try {
				raf = new RandomAccessFile(file, "rw");
				FileChannel channel = raf.getChannel();
				MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, raf.length());
				// 插入0数据
				while (buffer.hasRemaining()) {
					buffer.put((byte) 0);
				}
				buffer.force();
				buffer.rewind();
				// 插入0xFF数据
				while (buffer.hasRemaining()) {
					buffer.put((byte) 0xFF);
				}
				buffer.force();
				buffer.rewind();
				// 用随机数据重写；一次一字节
				byte[] data = new byte[1];
				while (buffer.hasRemaining()) {
					random.nextBytes(data);
					buffer.put(data[0]);
				}
				buffer.force();
				channel.force(true);
				channel.close();
				// 释放内存
				unmap(buffer);
				// 经过三次数据重写，再执行删除文件
				if (!file.delete()) {
					file.delete();
				}
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage());
			} finally {
				if (raf != null) {
					raf.close();
				}
			}
		}
	}

	// 利用反射调用FileChannelImpl的unmap方法释放内存
	private static void unmap(MappedByteBuffer var0) {
		Cleaner var1 = ((DirectBuffer) var0).cleaner();
		if (var1 != null) {
			var1.clean();
		}
	}

	// public static void main(String[] args) {
	// try {
	// DeleteFileUntil.completeDestroyDocument("G:\\删除文件测试\\图片.png");
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
}