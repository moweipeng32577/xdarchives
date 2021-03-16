package com.wisdom.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.wisdom.web.entity.Tb_watermark;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * 为PDF文件添加文字或者图片水印
 */
@Component
public class WatermarkUtil {

	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	@Value("${system.document.watermarkpath}")
	private String watermarkpath;// 水印文件根路径

	private static Map<String,BaseColor> colorMap = new HashMap<>();

	static {
		colorMap.put("black",BaseColor.BLACK);
		colorMap.put("red",BaseColor.RED);
		colorMap.put("yellow",BaseColor.YELLOW);
		colorMap.put("green",BaseColor.GREEN);
		colorMap.put("blue",BaseColor.BLUE);
	}

	/**
	 * @param printing      权限代号(支持0和2052)
	 * @param input         文件地址
	 * @param waterMarkText 水印文本(为null时使用图片水印)
	 * @param outFilePath   文件输出路径
	 * @param imgPath       水印图片(当waterMarkText为null时启用)
	 * @param coordinate    水印坐标(1~9),可自行自定坐标格式为"x,y"
	 * @param opcity        不透明度
	 * @param rotation      水印旋转角度
	 * @param color         水印字体颜色(字体水印)
	 * @param isRepeat      是否满屏打印
	 * @param fontSize 		字体大小
	 * @param spacing 		字体间距大小
	 * @param lineWidth     字体加粗大小
	 * @return              生成水印pdf地址
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static String setWatermarkText(int printing, String input, String outFilePath, String waterMarkText,
			String imgPath, String coordinate, float opcity, float rotation,String color, boolean isRepeat,int fontSize,int spacing,int lineWidth)  throws DocumentException, IOException {
		if(fontSize<0) {
			fontSize = 30;
		}
		float width = 0;//水印元素宽度
		float height = 0;//水印元素高度
		boolean isImg = false;//是否图片水印
		File outFile = new File(outFilePath);
		if (!outFile.getParentFile().exists()) {
			outFile.getParentFile().mkdirs();
		}
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		PdfReader reader = null;
		PdfStamper stamper = null;
		try {
			reader = new PdfReader(input);
            Field f = PdfReader.class.getDeclaredField("ownerPasswordUsed");
            f.setAccessible(true);
            f.set(reader, Boolean.TRUE);
            fos = new FileOutputStream(outFile);
			bos = new BufferedOutputStream(fos);
			stamper = new PdfStamper(reader, bos);
			if (printing != -1) {
				stamper.setEncryption(null, null, printing, PdfWriter.STANDARD_ENCRYPTION_128);// 只读,第三个参数为0时失去所有权限
			}
			// stamper.setEncryption(null,null,PdfWriter.STANDARD_ENCRYPTION_128);
			int total = reader.getNumberOfPages() + 1;
			PdfContentByte content;
			BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
			PdfGState gs = new PdfGState();
			com.itextpdf.text.Rectangle pageRect = null;
			String[] xy = coordinate.split(",");
			float pdfX,x = 0;
			float pdfY,y = 0;
			int address = 0;

			if(waterMarkText!=null){
				width = base.getWidthPoint(waterMarkText,fontSize);//获取文字宽度
				height = fontSize;
			}else{
				isImg = true;
				Image image = Image.getInstance(imgPath);
				width = image.getPlainWidth();//获取图片宽度
				height = image.getPlainHeight();//获取图片高度
			}
            if (xy.length > 1) {
                x = Float.parseFloat(xy[0])+(isImg?0:(width / 2));//加上x轴偏移量
                y = Float.parseFloat(xy[1])+5;//加上y轴偏移量
            } else {
                address = Integer.parseInt(coordinate);
            }
			for (int i = 1; i < total; i++) {
				pageRect = stamper.getReader().getPageSizeWithRotation(i);
				pdfX =  pageRect.getWidth();
				pdfY =  pageRect.getHeight();
				if (xy.length==1) {
					if (!isRepeat) {
						float[] coordinates = getCoordinates(pageRect,address,width,height,isImg);
						x = coordinates[0];
						y = coordinates[1];
					}else{
					    x = pdfX;
					    y = pdfY;
                    }
				}else{
                    if(isRepeat){
                        x = pdfX;
                        y = pdfY;
                    }
                }
				content = stamper.getOverContent(i);// 在内容上方加水印
				gs.setFillOpacity(opcity);
				content.setGState(gs);
				float wx = isImg?0:(width / 2), wy = 0;
				boolean isWhile = true;
				do {
					content.beginText();
					if (waterMarkText != null) {
						content.setColorFill(colorMap.get(color));//设置颜色
						content.setLineWidth(lineWidth);//设置加粗大小
						content.setCharacterSpacing(spacing);//设置字符间距
						content.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
						content.setFontAndSize(base, fontSize);//设置字体,大小
//						content.setTextMatrix(70, 200);
						content.showTextAligned(Element.ALIGN_CENTER, waterMarkText, isRepeat ? wx : x,
								isRepeat ? wy : y, rotation);
						wx += 120;
						if (wx > x + 60) {
							wx = 10 + (new SecureRandom().nextInt(2));
							if (waterMarkText.length() > 5) {
								wy += width / 2 + 60;
							} else {
								wy += width;
							}
						}
					} else {
						Image image = Image.getInstance(imgPath);
						image.setAbsolutePosition(isRepeat?wx:x,isRepeat?wy:y);
						image.scaleToFit(width,height);
						image.setRotation(rotation);
						content.addImage(image);
						if((pdfX-wx)<width){
							wx = 0;
							wy += height;
						}else{
							wx += width;
						}
					}
					content.endText();
					isWhile = isRepeat&&(wy - y) < pdfY;
				} while (isWhile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stamper != null) {
				try {
					stamper.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return outFilePath;
	}

	/**
	 * 获取水印坐标(文字OR水印)
	 * @param pageRect pdf对象
	 * @param address  位置
	 * @param width    水印宽度
	 * @param height   水印高度
	 * @param isImg    是否图片水印
	 * @return         坐标数组
	 */
	public static float[] getCoordinates(Rectangle pageRect, int address, float width, float height, boolean isImg){
		float margin = 5;
		float x = pageRect.getWidth();
		float y = pageRect.getHeight();
			switch (address) {
				case 1:
					x = isImg?0:(width / 2);
					y = y - height;
					break;
				case 2:
					x = isImg?x / 2-width/2:x/2;
					y = y - height;
					break;
				case 3:
					x = isImg?x - width:x - (width / 2);
					y = y - height;
					break;
				case 4:
					x = isImg?0:(width / 2);
					y = isImg?y / 2-height/2:y / 2;
					break;
				case 5:
					x = isImg?x / 2-width/2:x/2;
					y = isImg?y / 2-height/2:y / 2;
					break;
				case 6:
					x = isImg?x - width:x - (width / 2);
					y = isImg?y / 2-height/2:y / 2;
					break;
				case 7:
					x = isImg?0:(width / 2);
					y = 0;
					break;
				case 8:
					x = isImg?x / 2-width/2:x/2;
					y = 0;
					break;
				case 9:
					x = isImg?x - width:x - (width / 2);
					y = 0;
					break;
			}
			if(!isImg){
				x = x!=(width/2)?(x-margin):(x+margin);//添加间隔
				y = y!=0?(y-margin):(y+margin);
			}
		return new float[]{x,y};
	}

	/**
	 * 生成水印pdf
	 * @param watermark 水印实例
	 * @param srcPath   原文地址
	 * @param username  用户名(用于根据用户名生成文件夹)
	 * @param printing  打印权限
	 * @return          水印文件地址
	 */
	public  String getWatermarkPdf(Tb_watermark watermark, String srcPath, String username, int printing,HttpServletRequest request){
		try {
			String cachePath = watermarkpath + "\\" + username + "\\cacheWatermark.pdf";
			if(watermark==null){//当配置信息为空时,默认文字水印
				return setWatermarkText(printing,srcPath,cachePath,"利用水印",null
						,"1",(float)0.3,45,"black",true,30,3,3);
			}
			LogAop.getIpAddress();
			boolean isRepeat = "1".equals(watermark.getIsrepeat())?true:false;
			String coordinate = watermark.getLocation()==null?watermark.getCoordinates():watermark.getLocation();
			if(!"1".equals(watermark.getIspicture())){//判断是否为文字水印
				if("1".equals(watermark.getNamedefault())){
					watermark.setWatermark_picture_text(watermark.getWatermark_picture_text()+" "+username);
				}
				if("1".equals(watermark.getUseip())){
					watermark.setWatermark_picture_text(watermark.getWatermark_picture_text()+" "+getIpAddress(request));
				}
				return setWatermarkText(printing,srcPath,cachePath,watermark.getWatermark_picture_text(),null
						,coordinate,Float.parseFloat(watermark.getTransparency()),Integer.parseInt(watermark.getDegree()),watermark.getColor(),isRepeat,watermark.getFontsize(),watermark.getSpacing(),watermark.getLinewidth());
			}else{
				return setWatermarkText(printing,srcPath,cachePath,null,rootpath+watermark.getWatermark_picture_path()
						,coordinate,Float.parseFloat(watermark.getTransparency()),Integer.parseInt(watermark.getDegree()),watermark.getColor(),isRepeat,watermark.getFontsize(),watermark.getSpacing(),watermark.getLinewidth());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

    /**
     * 生成水印pdf
     * @param watermark 水印实例
     * @param srcPath   原文地址
     * @param username  用户名(用于根据用户名生成文件夹)
     * @param printing  打印权限
     * @return          水印文件地址
     */
    public  String getWatermarkPdf(Tb_watermark watermark,String srcPath,String username, String realname,int printing,String filename){
        try {
            String cachePath = watermarkpath + "\\" + username + filename;
            if(watermark==null){//当配置信息为空时,默认文字水印
                return setWatermarkText(printing,srcPath,cachePath,"利用水印",null
                        ,"1",(float)0.3,45,"black",true,30,3,3);
            }
            boolean isRepeat = "1".equals(watermark.getIsrepeat())?true:false;
            String coordinate = watermark.getLocation()==null?watermark.getCoordinates():watermark.getLocation();
            if(!"1".equals(watermark.getIspicture())){//判断是否为文字水印
                String waterMarkText=watermark.getWatermark_picture_text();
                if("1".equals(watermark.getNamedefault())){//默认利用者名字
                    waterMarkText+="	"+realname;
                }
                return setWatermarkText(printing,srcPath,cachePath,waterMarkText,null
                        ,coordinate,Float.parseFloat(watermark.getTransparency()),Integer.parseInt(watermark.getDegree()),watermark.getColor(),isRepeat,watermark.getFontsize(),watermark.getSpacing(),watermark.getLinewidth());
            }else{
                return setWatermarkText(printing,srcPath,cachePath,null,rootpath+watermark.getWatermark_picture_path()
                        ,coordinate,Float.parseFloat(watermark.getTransparency()),Integer.parseInt(watermark.getDegree()),watermark.getColor(),isRepeat,watermark.getFontsize(),watermark.getSpacing(),watermark.getLinewidth());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	public static void main(String[] args) throws Exception {
		setWatermarkText(2052, "F:\\1.pdf", "F:\\2.pdf", null, "F:\\1.jpg", "9", (float) 0.4, 0,"red", false,30,3,3);
	}
}