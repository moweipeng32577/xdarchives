package com.wisdom.util;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.SecureRandom;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QRCodeUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

	private static final String CHARSET = "utf-8";
	private static final String FORMAT_NAME = "JPG";
	// 二维码尺寸
	private static final int QRCODE_SIZE = 300;
	// 条形码尺寸
	private static final int BARCODE_WIDTH = 120;
	private static final int BARCODE_HEIGHT = 60;
	// LOGO宽度
	private static final int WIDTH = 60;
	// LOGO高度
	private static final int HEIGHT = 60;
	// 字体大小
	private static final int FONT_SIZE = 18;

	private static BufferedImage createImage(String content, String imgPath, boolean needCompress, boolean isQGCode)
			throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix;
		if (isQGCode) {
			bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE + 100, hints);
		} else {
			bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, BARCODE_WIDTH, BARCODE_HEIGHT,
					hints);
		}
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		if (imgPath == null || "".equals(imgPath)) {
			return image;
		}
		// 插入图片
		QRCodeUtil.insertImage(image, imgPath, needCompress);
		return image;
	}

	private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws Exception {
		File file = new File(imgPath);
		if (!file.exists()) {
			System.err.println("" + imgPath + " 二维logo不存在！");
			return;
		}
		Image src = ImageIO.read(new File(imgPath));
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		if (needCompress) { // 压缩LOGO
			if (width > WIDTH) {
				width = WIDTH;
			}
			if (height > HEIGHT) {
				height = HEIGHT;
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			src = image;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (QRCODE_SIZE - width) / 2;
		int y = (QRCODE_SIZE - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
		graph.setStroke(new BasicStroke(3f));
		graph.draw(shape);
		graph.dispose();
	}

	// public static ByteArrayOutputStream encode(String content, String
	// imgPath, String destPath, boolean needCompress,boolean isQGCode)
	// throws Exception {
	// BufferedImage image = QRCodeUtil.createImage(content, imgPath,
	// needCompress,isQGCode);
	// //mkdirs(destPath);
	// // 二维码图片
	// //String file = new Random().nextInt(99999999) + ".jpg";
	// //ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
	// ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
	// ImageIO.write(image,FORMAT_NAME,byteArrayOutputStream);
	// return byteArrayOutputStream;
	// }

	public static void encode(String content, String imgPath, String destPath, boolean needCompress, boolean isQGCode)
			throws Exception {
		BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress, isQGCode);
		mkdirs(destPath);
		SecureRandom generater = new SecureRandom();
		// 二维码图片
		String file = generater.nextInt(99999999) + ".jpg";
		ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
	}

	public static void mkdirs(String destPath) {
		File file = new File(destPath);
		// 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}

	public static void encode(String content, String imgPath, String destPath, boolean isQGCode) throws Exception {
		QRCodeUtil.encode(content, imgPath, destPath, true, isQGCode);
	}

	public static void encode(String content, String destPath, boolean needCompress, boolean isQGCode)
			throws Exception {
		QRCodeUtil.encode(content, null, destPath, needCompress, isQGCode);
	}

	public static void encode(String content, String destPath, boolean isQGCode) throws Exception {
		QRCodeUtil.encode(content, null, destPath, false, isQGCode);
	}

	public static void encode(String content, String imgPath, OutputStream output, boolean needCompress,
			boolean isQGCode) throws Exception {
		BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress, isQGCode);
		ImageIO.write(image, FORMAT_NAME, output);
	}

	public static void encode(String content, OutputStream output, boolean isQGCode) throws Exception {
		QRCodeUtil.encode(content, null, output, false, isQGCode);
	}

	public static String decode(File file) throws Exception {
		BufferedImage image;
		image = ImageIO.read(file);
		if (image == null) {
			return null;
		}
		BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
		result = new MultiFormatReader().decode(bitmap, hints);
		String resultStr = result.getText();
		return resultStr;
	}

	public static String decode(String path) throws Exception {
		return QRCodeUtil.decode(new File(path));
	}

	/**
	 * @param content      二维码内容
	 * @param imgPath      logo图路径 可为null
	 * @param needCompress 是否压缩logo图
	 * @param isQGCode
	 * @param pressText    二维码附加的文字
	 * @param newImg       二维码输出路径包括文件名和后缀
	 * @return 二维码全路径包括文件名和后缀
	 * @throws Exception
	 */
	public static String createQRcodeAndText(String content, String imgPath, boolean needCompress,
											 boolean isQGCode, String pressText, String newImg) throws Exception {
		BufferedImage image = QRCodeUtil.createImage(content, null, true, true);
		pressText(pressText, newImg, image, 1, Color.black, FONT_SIZE, QRCODE_SIZE, QRCODE_SIZE + 100);
		return newImg;
	}

	// public static void main(String[] args) throws Exception {
	// String text = "001-A2.1-001-0001";
	// // 生成带logo的二维码
	// QRCodeUtil.encode(text, "d:/180.png", "d:/MyWorkDoc", true);
	// // 生成不带二维码的logo图片
	// QRCodeUtil.encode(text, "d:/MyWorkDoc", true);
	//
	// String file = new Random().nextInt(99999999) + ".jpg";
	// QRCodeUtil.generateFile(text, "d:/MyWorkDoc/" + file);
	//
	// QRCodeUtil.encode(text, "d:/MyWorkDoc", false);
	// drawImage("D:/MyWorkDoc/11719558.jpg", 1);
	// }

	/**
	 * 生成文件
	 *
	 * @param msg
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static File generateFile(String msg, String path) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(path);
		File file = new File(path);
		try {
			generate(msg, outputStream);
		} finally {
			if (outputStream != null) {
				outputStream.close();
		    }
		}
		return file;
	}

	/**
	 * 生成字节
	 *
	 * @param msg
	 * @return
	 */
	public static byte[] generate(String msg) {
		ByteArrayOutputStream ous = new ByteArrayOutputStream();
		generate(msg, ous);
		return ous.toByteArray();
	}

	/**
	 * 生成到流
	 *
	 * @param msg
	 * @param ous
	 */
	public static void generate(String msg, OutputStream ous) {
		if (StringUtils.isEmpty(msg) || ous == null) {
			return;
		}

		Code128Bean bean = new Code128Bean();

		// 精细度
		final int dpi = 150;
		// module宽度
		final double moduleWidth = UnitConv.in2mm(1.0f / dpi);

		// 配置对象
		bean.setModuleWidth(moduleWidth);
		bean.doQuietZone(false);

		String format = "image/png";
		try {
			// 输出到流
			BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi, BufferedImage.TYPE_BYTE_BINARY,
					false, 0);
			// 生成条形码
			bean.generateBarcode(canvas, msg);
			// 结束绘制
			canvas.finish();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 给二维码下面附加文字
	 *
	 * @param pressText
	 * @param newImg
	 * @param image
	 * @param fontStyle
	 * @param color
	 * @param fontSize
	 * @param width
	 * @param height
	 */
	public static void pressText(String pressText, String newImg, BufferedImage image,
								 int fontStyle, Color color, int fontSize, int width, int height) {

		//计算文字开始的位置
		int startX = 0;
		//y开始的位置：图片高度-（图片高度-图片宽度）/2
		int startY = height - (height - width);
		try {
			File file = new File(newImg);
			String filepath = file.getParent();
			if (!new File(filepath + File.separator).exists()) {
				new File(filepath + File.separator).mkdirs();
			}
			int imageW = image.getWidth();
			int imageH = image.getHeight();
			Graphics2D g = image.createGraphics();
			//消除锯齿状
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			g.drawImage(image, 0, 0, imageW, imageH, null);
			//设置颜色
			g.setColor(color);
			//设置字体
			g.setFont(new Font("粗体", Font.BOLD, 21));
			int strWidth = g.getFontMetrics().stringWidth(pressText);//得到文字占大小
			//x开始的位置：（图片宽度-文字占大小）/2
			startX = (width - strWidth) / 2;//计算居中位置
			g.drawString(pressText, startX, startY + 60);
			g.dispose();
			FileOutputStream out = new FileOutputStream(newImg);
			ImageIO.write(image, "JPEG", out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	/**
	 * 打印二维码
	 *
	 * @param fileName
	 * @param count
	 */
	public static void drawImage(String fileName, int count) throws IOException {
		FileInputStream fin = new FileInputStream(fileName);
		try {
			DocFlavor dof = null;
			if (fileName.endsWith(".gif")) {
				dof = DocFlavor.INPUT_STREAM.GIF;
			} else if (fileName.endsWith(".jpg")) {
				dof = DocFlavor.INPUT_STREAM.JPEG;
			} else if (fileName.endsWith(".png")) {
				dof = DocFlavor.INPUT_STREAM.PNG;
			}
			// 获取默认打印机
			PrintService ps = PrintServiceLookup.lookupDefaultPrintService();

			PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			// pras.add(OrientationRequested.PORTRAIT);
			// pras.add(PrintQuality.HIGH);
			pras.add(new Copies(count));
			pras.add(MediaSizeName.ISO_A10); // 设置打印的纸张

			DocAttributeSet das = new HashDocAttributeSet();
			das.add(new MediaPrintableArea(0, 0, 1, 1, MediaPrintableArea.INCH));
			
			Doc doc = new SimpleDoc(fin, dof, das);
			DocPrintJob job = ps.createPrintJob();

			job.print(doc, pras);
		} catch (PrintException e) {
			logger.error(e.getMessage());
		} finally {
			if (fin != null) {
				fin.close();
			}
		}
	}
}