package com.wisdom.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2020/1/10.
 */
public class PdfUtil {

    /**
     * @Author wmh
     * @Description 将图片转换为PDF
     * @return
     **/
    public static String Image2PDF(String imagePath, BufferedImage img, String descfolder) throws Exception{
        String pdfPath = "";
        try {
            //图片操作
            Image image = null;
            File file = new File(descfolder);

            if (!file.exists()){
                file.mkdirs();
            }

            pdfPath = descfolder +"/"+System.currentTimeMillis()+".pdf";
            String type = imagePath.substring(imagePath.lastIndexOf(".")+1);
            Document doc = new Document(null, 0, 0, 0, 0);

            //更换图片图层
            BufferedImage bufferedImage = new BufferedImage(img.getWidth(), img.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
            bufferedImage.getGraphics().drawImage(img, 0,0, img.getWidth(), img.getHeight(), null);
//            bufferedImage=new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null).filter (bufferedImage,null);

            //图片流处理
            doc.setPageSize(new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(bufferedImage, type, out);
            byte[] b = out.toByteArray();
            image = Image.getInstance(b);

            //写入PDF
            System.out.println("写入PDf:" + pdfPath);
            FileOutputStream fos = new FileOutputStream(pdfPath);
            PdfWriter.getInstance(doc, fos);
            doc.open();
            doc.add(image);
            doc.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return pdfPath;
    }



    /**
     * @Author wmh
     * @Description 合成PDF
     * @return
     **/
    public static void mergePDF(String[] files,String desfolder,String mergeFileName ) throws Exception{

        PDFMergerUtility mergePdf = new PDFMergerUtility();

        for (String file :files) {
            if (file.toLowerCase().endsWith("pdf"))
                mergePdf.addSource(file);
        }

        mergePdf.setDestinationFileName(desfolder+"/"+mergeFileName);
        mergePdf.mergeDocuments();
        System.out.println("merge over");

    }

    /**
     * @Author wmh
     * @Description 删除文件夹文件
     * @return
     **/
    public static void deleteFile(String folder) {
        File file = new File(folder);
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()) {
            file.mkdirs();
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f : files) {
            f.delete();
        }
    }
}
