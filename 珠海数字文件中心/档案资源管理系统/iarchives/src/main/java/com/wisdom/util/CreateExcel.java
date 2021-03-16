package com.wisdom.util;

import com.wisdom.secondaryDataSource.entity.Tb_codeset_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_template_sx;
import com.wisdom.web.entity.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by SunK on 2018/7/13 0013.
 */
public class CreateExcel {

    public static Workbook CreateExcle(String fileName, List<Entry> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // 1.创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // 3.创建第一行，列名行
        Row row = sheet.createRow(0);
        // 4.设置列名
        for (int i = 0; i < fieldname.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i);
            cell.setCellValue(fieldname[i]);
        }
        // 5 设置除第一行之外的行和列的参数
        Entry entry = new Entry();
        message.add(0, entry);// --给数据集合添加1个元素 应为第一行没有设置值
        for (int j = 1; j < message.size(); j++) {// 有多少行
            Row row1 = sheet.createRow(j);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)).toString());
            }
        }
        // 6
        // workbook.write(new FileOutputStream(path));
        return workbook;
    }

    //支持100W条以内的导出excel
    public static Sheet SXSSFWorkbookCreateExcle(Sheet sheet, List<Entry> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        //当前已写入的最大行数
        int rows = sheet.getLastRowNum();
        //获取workbook的sheet数
        if (rows == 0) {//第一行
            // 3.创建第一行，列名行
            Row row = sheet.createRow(0);
            // 4.设置列名
            for (int i = 0; i < fieldname.length; i++) {
                // 循环次数等于参数个数，创建对应行的单元格并设置列名
                Cell cell = row.createCell(i);
                cell.setCellValue(fieldname[i]);
            }
        }
        int c = 0;
        for (int j = rows; j < message.size() + rows; j++) {//循环行数 写入数据
            Row row1 = sheet.createRow(j + 1);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
               /* cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)).toString());*/
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)).toString());
            }
            c++;
        }
        if (c % 900 == 0) {
            ((SXSSFSheet) sheet).flushRows(10);//向磁盘写出890行，留10行下次循环才可以获取行号
            System.gc();
        }
        return sheet;
    }

    //支持100W条以内的导出预约管理的excel
    public static Sheet reservationSXSSFWorkbookCreateExcle(Sheet sheet, List<Tb_reserve> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        //当前已写入的最大行数
        int rows = sheet.getLastRowNum();
        //获取workbook的sheet数
        if (rows == 0) {//第一行
            // 3.创建第一行，列名行
            Row row = sheet.createRow(0);
            // 4.设置列名
            for (int i = 0; i < fieldname.length; i++) {
                // 循环次数等于参数个数，创建对应行的单元格并设置列名
                Cell cell = row.createCell(i);
                cell.setCellValue(fieldname[i]);
            }
        }
        int c = 0;
        for (int j = rows; j < message.size() + rows; j++) {//循环行数 写入数据
            Row row1 = sheet.createRow(j + 1);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
               /* cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)).toString());*/
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)).toString());
            }
            c++;
        }
        if (c % 900 == 0) {
            ((SXSSFSheet) sheet).flushRows(10);//向磁盘写出890行，留10行下次循环才可以获取行号
            System.gc();
        }
        return sheet;
    }

    //支持100W条以内的导出设备管理的excel
    public static Sheet equipmentSXSSFWorkbookCreateExcle(Sheet sheet, List<Tb_equipment> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        //当前已写入的最大行数
        int rows = sheet.getLastRowNum();
        //获取workbook的sheet数
        if (rows == 0) {//第一行
            // 3.创建第一行，列名行
            Row row = sheet.createRow(0);
            // 4.设置列名
            for (int i = 0; i < fieldname.length; i++) {
                // 循环次数等于参数个数，创建对应行的单元格并设置列名
                Cell cell = row.createCell(i);
                cell.setCellValue(fieldname[i]);
            }
        }
        int c = 0;
        for (int j = rows; j < message.size() + rows; j++) {//循环行数 写入数据
            Row row1 = sheet.createRow(j + 1);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
               /* cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)).toString());*/
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)).toString());
            }
            c++;
        }
        if (c % 900 == 0) {
            ((SXSSFSheet) sheet).flushRows(10);//向磁盘写出890行，留10行下次循环才可以获取行号
            System.gc();
        }
        return sheet;
    }

    //支持100W条以内的导出全文检索的excel
    public static Sheet originalSXSSFWorkbookCreateExcle(Sheet sheet, List<OriginalExcel> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        //当前已写入的最大行数
        int rows = sheet.getLastRowNum();
        //获取workbook的sheet数
        if (rows == 0) {//第一行
            // 3.创建第一行，列名行
            Row row = sheet.createRow(0);
            // 4.设置列名
            for (int i = 0; i < fieldname.length; i++) {
                // 循环次数等于参数个数，创建对应行的单元格并设置列名
                Cell cell = row.createCell(i);
                cell.setCellValue(fieldname[i]);
            }
        }
        int c = 0;
        for (int j = rows; j < message.size() + rows; j++) {//循环行数 写入数据
            Row row1 = sheet.createRow(j + 1);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)).toString());
            }
            c++;
        }
        if (c % 900 == 0) {
            ((SXSSFSheet) sheet).flushRows(10);//向磁盘写出890行，留10行下次循环才可以获取行号
            System.gc();
        }
        return sheet;
    }

    //支持100W条以内的导出excel
    public static Sheet captureSXSSFWorkbookCreateExcle(Sheet sheet, List<EntryCapture> message, String[] fieldcode,
                                                        String[] fieldname,List<AcceptEntryCapture> acceptList,
                                                        String exporttype,List<ManageEntry> manageEntries)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        //当前已写入的最大行数
        int rows = sheet.getLastRowNum();
        //获取workbook的sheet数
        if (rows == 0) {//第一行
            // 3.创建第一行，列名行
            Row row = sheet.createRow(0);
            // 4.设置列名
            for (int i = 0; i < fieldname.length; i++) {
                // 循环次数等于参数个数，创建对应行的单元格并设置列名
                Cell cell = row.createCell(i);
                cell.setCellValue(fieldname[i]);
            }
        }
        int c = 0;
        if(exporttype!=null&&"accept".equals(exporttype)){ //判断是否目录接收导出
            for (int j = rows; j < acceptList.size() + rows; j++) {//循环行数 写入数据
                Row row1 = sheet.createRow(j + 1);
                for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                    Cell cell = row1.createCell(k);
                    // --参数使用list 集合泛型是map map通过key获取到value
               /* cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)).toString());*/
                    cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], acceptList.get(c)) == null ? " "
                            : ValueUtil.getPoFieldValue(fieldcode[k], acceptList.get(c)).toString());
                }
                c++;
            }
        }else if(exporttype!=null&&"manage".equals(exporttype)){ //判断是否目录管理导出
            for (int j = rows; j < manageEntries.size() + rows; j++) {//循环行数 写入数据
                Row row1 = sheet.createRow(j + 1);
                for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                    Cell cell = row1.createCell(k);
                    // --参数使用list 集合泛型是map map通过key获取到value
               /* cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)).toString());*/
                    cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], manageEntries.get(c)) == null ? " "
                            : ValueUtil.getPoFieldValue(fieldcode[k], manageEntries.get(c)).toString());
                }
                c++;
            }
        }else{
            for (int j = rows; j < message.size() + rows; j++) {//循环行数 写入数据
                Row row1 = sheet.createRow(j + 1);
                for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                    Cell cell = row1.createCell(k);
                    // --参数使用list 集合泛型是map map通过key获取到value
               /* cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)).toString());*/
                    cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)) == null ? " "
                            : ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)).toString());
                }
                c++;
            }
        }
        if (c % 900 == 0) {
            ((SXSSFSheet) sheet).flushRows(10);//向磁盘写出890行，留10行下次循环才可以获取行号
            System.gc();
        }
        return sheet;
    }

    // 用于数据采集
    public static Workbook CaptureCreateExcle(String fileName, List<EntryCapture> message, String[] fieldcode,
                                              String[] fieldname) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // 1.创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // 3.创建第一行，列名行
        Row row = sheet.createRow(0);
        // 4.设置列名
        for (int i = 0; i < fieldname.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i);
            cell.setCellValue(fieldname[i]);
        }
        // 5 设置除第一行之外的行和列的参数
        EntryCapture entry = new EntryCapture();
        message.add(0, entry);// --给数据集合添加1个元素 应为第一行没有设置值
        for (int j = 1; j < message.size(); j++) {// 有多少行
            Row row1 = sheet.createRow(j);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)).toString());
            }
        }
        // 6 workbook.write(new FileOutputStream(path));
        return workbook;
    }

    public static Workbook createTemp(String[] fieldCode, String[] fieldName) {
        // 1.创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // fieldname
        Row row = sheet.createRow(0);
        for (int i = 0; i < fieldName.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i);
            cell.setCellValue(fieldName[i]);
        }
        // fieldcode
        Row row1 = sheet.createRow(1);
        for (int i = 0; i < fieldCode.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row1.createCell(i);
            cell.setCellValue(fieldCode[i]);
        }
        // 字段数据库属性
        Row row2 = sheet.createRow(2);
        for (int i = 0; i < fieldCode.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row2.createCell(i);
            cell.setCellValue(ConfigValue.getFieldProperty(fieldCode[i].toUpperCase()));
        }
        return workbook;
    }

    public static Workbook createFieldNameTemp(String[] fieldName) {
        // 1.创建工作簿
        Workbook workbook = new SXSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // fieldname
        Row row = sheet.createRow(0);
        for (int i = 0; i < fieldName.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i);
            cell.setCellValue(fieldName[i]);
        }
        return workbook;
    }

    public static void createErroExcel(String fileName, List<Entry> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // 1.创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // 3.创建第一行，列名行
        Row row = sheet.createRow(0);
        // 4.设置列名
        for (int i = 0; i < fieldname.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i);
            cell.setCellValue(fieldname[i]);
        }
        // 5 设置除第一行之外的行和列的参数
        Entry entry = new Entry();
        message.add(0, entry);// --给数据集合添加1个元素 应为第一行没有设置值
        for (int j = 1; j < message.size(); j++) {// 有多少行
            Row row1 = sheet.createRow(j);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)).toString());
            }
        }
        // 6
        // workbook.write(new FileOutputStream(path));
        // return workbook;
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/导入失败/" + fileName;
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        String newpath = path + "/" + fileName + ".xls";
        FileOutputStream fout = new FileOutputStream(newpath);

        // 6---在服务器本地生成excel文件
        workbook.write(fout);
        workbook.close();
        fout.flush();
        fout.close();
    }

    /**
     * 异常导出
     * 情形1 没找到相关条目
     * 情形2 匹配到多条条目
     * 情形3  存储位置不够详细
     * 情形4  放入密集架空间不足
     * 情形5  已入库
     * 情形6  存储位置没有匹配到
     * 情形7 存储位置信息为空
     * 情形8  可以进行入库
     * @return
     */
    public static void createTempErroExcel(String fileName, List<Tb_entry_index_temp> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // 1.创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // 3.创建第一行，列名行
        Row row = sheet.createRow(0);
        // 4.设置列名
        //先增加 9个列：入库异常描述、城市、单位、库房、架区、列、节、层、面
        String addCol="入库异常描述-城市-单位-库房-架区-列-节-层-面";
        String[] addColArr=addCol.split("-");
        for (int i = 0; i < addColArr.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i);
            cell.setCellValue(addColArr[i]);
        }
        for (int i = 0; i < fieldname.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i+9);
            cell.setCellValue(fieldname[i]);
        }
        // 5 设置除第一行之外的行和列的参数
        Tb_entry_index_temp entry = new Tb_entry_index_temp();
        message.add(0, entry);// --给数据集合添加1个元素 应为第一行没有设置值
        String entryStorage;
        String[] entryStorageArr=new String[]{};
        for (int j = 1; j < message.size(); j++) {// 有多少行
            Row row1 = sheet.createRow(j);
            entryStorage=ValueUtil.getPoFieldValue("entrystorage", message.get(j)) == null ? ""
                    : ValueUtil.getPoFieldValue("entrystorage", message.get(j)).toString();
            if(!"".equals(entryStorage)){
                //entryStorageArr=entryStorage.split("-");
                entryStorageArr=entryStorage.split("_");//分隔符改用下划线
            }
            //先设置新加的9个列的值
            for (int k = 0; k < addColArr.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                if(k==0){//入库异常描述
                    // --参数使用list 集合泛型是map map通过key获取到value
                    String resultType=ValueUtil.getPoFieldValue("sparefield5", message.get(j)) == null ? ""
                            : ValueUtil.getPoFieldValue("sparefield5", message.get(j)).toString();
                    if("1".equals(resultType)){
                        cell.setCellValue("没找到相关条目");
                    }else if("2".equals(resultType)){
                        cell.setCellValue("匹配到多条条目");
                    }else if("3".equals(resultType)){
                        cell.setCellValue("存储位置不够详细");
                    }else if("4".equals(resultType)){
                        cell.setCellValue("放入密集架空间不足");
                    }else if("5".equals(resultType)){
                        cell.setCellValue("已入库");
                    }else if("6".equals(resultType)){
                        cell.setCellValue("存储位置没有匹配到");
                    }else if("7".equals(resultType)){
                        cell.setCellValue("存储位置信息为空");
                    }else{
                        cell.setCellValue("");
                    }
                }else{//库存设置的详细位置
                    if(!"".equals(entryStorage)&&entryStorageArr.length==8){
                        cell.setCellValue(entryStorageArr[k-1]);
                    }else{
                        cell.setCellValue("");
                    }
                }
            }

            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k+9);
                // --参数使用list 集合泛型是map map通过key获取到value
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)).toString());
            }
        }
        // 6
        // workbook.write(new FileOutputStream(path));
        // return workbook;
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/导入失败/" + fileName;
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        String newpath = path + "/" + fileName + ".xls";
        FileOutputStream fout = new FileOutputStream(newpath);

        // 6---在服务器本地生成excel文件
        workbook.write(fout);
        workbook.close();
        fout.flush();
        fout.close();
    }

    public static void captureCreateErroExcel(String fileName, List<EntryCapture> message, String[] fieldcode,
             String[] fieldname,List<AcceptEntryCapture> acceptEntry, String importtype) throws IOException,
            ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchFieldException {
        // 1.创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // 3.创建第一行，列名行
        Row row = sheet.createRow(0);
        // 4.设置列名
        for (int i = 0; i < fieldname.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i);
            cell.setCellValue(fieldname[i]);
        }
        // 5 设置除第一行之外的行和列的参数
        if(importtype!=null&&"accept".equals(importtype)){ //判断是否目录导入
            AcceptEntryCapture entry = new AcceptEntryCapture();
            acceptEntry.add(0, entry);// --给数据集合添加1个元素 应为第一行没有设置值
            for (int j = 1; j < acceptEntry.size(); j++) {// 有多少行
                Row row1 = sheet.createRow(j);
                for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                    Cell cell = row1.createCell(k);
                    // --参数使用list 集合泛型是map map通过key获取到value
                    cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], acceptEntry.get(j)) == null ? " "
                            : ValueUtil.getPoFieldValue(fieldcode[k], acceptEntry.get(j)).toString());
                }
            }
        }else{
            EntryCapture entry = new EntryCapture();
            message.add(0, entry);// --给数据集合添加1个元素 应为第一行没有设置值
            for (int j = 1; j < message.size(); j++) {// 有多少行
                Row row1 = sheet.createRow(j);
                for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                    Cell cell = row1.createCell(k);
                    // --参数使用list 集合泛型是map map通过key获取到value
                    cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)) == null ? " "
                            : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)).toString());
                }
            }
        }
        // 6
        // workbook.write(new FileOutputStream(path));
        // return workbook;
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/导入失败/" + fileName;
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        String newpath = path + "/" + fileName + ".xls";
        FileOutputStream fout = new FileOutputStream(newpath);

        // 6---在服务器本地生成excel文件
        workbook.write(fout);
        workbook.close();
        fout.flush();
        fout.close();
    }

    public static void CreateErroExcel(String fileName, List<String> message, String fieldname) throws Exception {
        // 1.创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // 3.创建第一行，列名行
        Row row = sheet.createRow(0);
        // 4.设置列名
        // 循环次数等于参数个数，创建对应行的单元格并设置列名
        Cell cell = row.createCell(0);
        cell.setCellValue(fieldname);
        // 5 设置除第一行之外的行和列的参数
        message.add("NULL");
        for (int j = 1; j < message.size(); j++) {// 有多少行
            Row row1 = sheet.createRow(j);
            // 创建每行的单元格
            Cell cell1 = row1.createCell(0);
            // 下标减1是应为循环从1开始
            cell1.setCellValue(message.get(j - 1) == null ? " " : message.get(j - 1).toString());
        }
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/导入失败/" + fileName;
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        String newpath = path + "/" + fileName + ".xls";
        FileOutputStream fout = new FileOutputStream(newpath);

        // 6---在服务器本地生成excel文件
        workbook.write(fout);
        workbook.close();
        fout.flush();
        fout.close();
    }

    public static void ExportExcel(Workbook workbook, String fileName) {
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;
        // zip 完整路径
        String zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
        // 创建临时路径文件夹
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        try {
            String newpath = path + "/" + fileName + ".xlsx";
            FileOutputStream fout = new FileOutputStream(newpath);
            workbook.write(fout);
            workbook.close();
            fout.close();// ----------这里一定要关闭流，不然后面的删除文件夹会失败
            /*boolean bool = false;
			OutputStream fileOutputStream = new FileOutputStream(new File(zippath));
			bool = ZipUtils.toZip(path, fileOutputStream, true);

			if (bool) {
				InputStream inputStream = new FileInputStream(new File(zippath));
				OutputStream out = response.getOutputStream();
				response.setContentType("application/octet-stream;charset=UTF-8");
				response.setHeader("Content-Disposition",
						"attachment;filename=\"" + new String((fileName + ".zip").getBytes(), "iso-8859-1") + "\"");
				byte[] b = new byte[1024 * 1024 * 10];
				int leng = 0;
				while ((leng = inputStream.read(b)) != -1) {
					out.write(b, 0, leng);
				}
				out.flush();
				inputStream.close();
				out.close();
			}
			ZipUtils.delFolder(path);// 删除生成的excel文件
			new File(zippath).delete();// 删除生成的zip压缩包*/
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String createFieldModel(List<Tb_data_template> templates, String nodename, String[] cellsName, List<Tb_codeset> codesets, String[] codeFields) {
        //生成存放路径
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/Excel导出/节点字段模板/" + nodename;
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        //1.创建workbook对象
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        //循环对象
        templates.add(0,new Tb_data_template());
        for (int i = 0; i < templates.size(); i++) {
            Row row = sheet.createRow(i);
            if (i == 0) {
                //设置列头行
                for (int j = 0; j < cellsName.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(cellsName[j]);
                }
                continue;
            }
            for (int j = 0; j < cellsName.length; j++) {
                Cell cell = row.createCell(j);
                try {
                    cell.setCellValue(ValueUtil.getPoFieldValue(cellsName[j], templates.get(i)) == null ?
                            "" : ValueUtil.getPoFieldValue(cellsName[j], templates.get(i)).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Sheet sheet1 = workbook.createSheet("档号组成字段");
        //循环对象
        codesets.add(0,new Tb_codeset());
        for (int i = 0; i < codesets.size(); i++) {
            Row row = sheet1.createRow(i);
            if (i == 0) {
                //设置列头行
                for (int j = 0; j < codeFields.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(codeFields[j]);
                }
                continue;
            }
            for (int j = 0; j < codeFields.length; j++) {
                Cell cell = row.createCell(j);
                try {
                    cell.setCellValue(ValueUtil.getPoFieldValue(codeFields[j], codesets.get(i)) == null ?
                            "" : ValueUtil.getPoFieldValue(codeFields[j], codesets.get(i)).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // 6---在服务器本地生成excel文件
        String newpath = path + "/" + nodename + ".xlsx";
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(newpath);
            workbook.write(fout);
            workbook.close();
            fout.flush();

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return newpath;
    }

    public static String createSxFieldModel(List<Tb_data_template_sx> templates, String nodename, String[] cellsName, List<Tb_codeset_sx> codesets, String[] codeFields) {
        //生成存放路径
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/Excel导出/节点字段模板/" + nodename;
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        //1.创建workbook对象
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        //循环对象
        templates.add(0,new Tb_data_template_sx());
        for (int i = 0; i < templates.size(); i++) {
            Row row = sheet.createRow(i);
            if (i == 0) {
                //设置列头行
                for (int j = 0; j < cellsName.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(cellsName[j]);
                }
                continue;
            }
            for (int j = 0; j < cellsName.length; j++) {
                Cell cell = row.createCell(j);
                try {
                    cell.setCellValue(ValueUtil.getPoFieldValue(cellsName[j], templates.get(i)) == null ?
                            "" : ValueUtil.getPoFieldValue(cellsName[j], templates.get(i)).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Sheet sheet1 = workbook.createSheet("档号组成字段");
        //循环对象
        codesets.add(0,new Tb_codeset_sx());
        for (int i = 0; i < codesets.size(); i++) {
            Row row = sheet1.createRow(i);
            if (i == 0) {
                //设置列头行
                for (int j = 0; j < codeFields.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(codeFields[j]);
                }
                continue;
            }
            for (int j = 0; j < codeFields.length; j++) {
                Cell cell = row.createCell(j);
                try {
                    cell.setCellValue(ValueUtil.getPoFieldValue(codeFields[j], codesets.get(i)) == null ?
                            "" : ValueUtil.getPoFieldValue(codeFields[j], codesets.get(i)).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // 6---在服务器本地生成excel文件
        String newpath = path + "/" + nodename + ".xlsx";
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(newpath);
            workbook.write(fout);
            workbook.close();
            fout.flush();

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return newpath;
    }

    public static void main(String[] age) {
        Workbook workbook = new HSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // 3.创建第一行，列名行
        Row row = sheet.createRow(0);
        // 4.设置列名

        // 循环次数等于参数个数，创建对应行的单元格并设置列名
        Cell cell = row.createCell(0);
        cell.setCellValue("demo");
        Row row1 = sheet.createRow(1);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue("1");
        Sheet sheet2 = workbook.createSheet();
        int b = workbook.getNumberOfSheets();
        int a = sheet.getLastRowNum();
        System.out.println(b);
    }

    //支持100W条以内的导出excel,漏页检查导出
    public static Sheet SXSSFMissPageWorkbookCreateExcle(Sheet sheet, List<RebackMissPageCheck> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        //当前已写入的最大行数
        int rows = sheet.getLastRowNum();
        //获取workbook的sheet数
        if (rows == 0) {//第一行
            // 3.创建第一行，列名行
            Row row = sheet.createRow(0);
            // 4.设置列名
            for (int i = 0; i < fieldname.length; i++) {
                // 循环次数等于参数个数，创建对应行的单元格并设置列名
                Cell cell = row.createCell(i);
                cell.setCellValue(fieldname[i]);
            }
        }
        int c = 0;
        for (int j = rows; j < message.size() + rows; j++) {//循环行数 写入数据
            Row row1 = sheet.createRow(j + 1);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
               /* cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)).toString());*/
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)).toString());
            }
            c++;
        }
        if (c % 900 == 0) {
            ((SXSSFSheet) sheet).flushRows(10);//向磁盘写出890行，留10行下次循环才可以获取行号
            System.gc();
        }
        return sheet;
    }

    public static void createCalloutErroExcel(String fileName, List<Szh_RebackImport> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        // 1.创建工作簿
        Workbook workbook = new HSSFWorkbook();
        // 2.创建对应分表并设置表名
        Sheet sheet = workbook.createSheet();
        // 3.创建第一行，列名行
        Row row = sheet.createRow(0);
        // 4.设置列名
        for (int i = 0; i < fieldname.length; i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i);
            cell.setCellValue(fieldname[i]);
        }
        // 5 设置除第一行之外的行和列的参数
        Szh_RebackImport entry = new Szh_RebackImport();
        message.add(0, entry);// --给数据集合添加1个元素 应为第一行没有设置值
        for (int j = 1; j < message.size(); j++) {// 有多少行
            Row row1 = sheet.createRow(j);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j)).toString());
            }
        }
        // 6
        // workbook.write(new FileOutputStream(path));
        // return workbook;
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/导入失败/" + fileName;
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        String newpath = path + "/" + fileName + ".xls";
        FileOutputStream fout = new FileOutputStream(newpath);

        // 6---在服务器本地生成excel文件
        workbook.write(fout);
        workbook.close();
        fout.flush();
        fout.close();
    }


    public static Workbook createOrganExcel(List<Tb_right_organ> indexList, List<String> cellsName, List<String> codeFields)throws Exception {

        //1.创建workbook对象
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 3.创建第一行，列名行
        Row row = sheet.createRow(0);
        // 4.设置列名
        for (int i = 0; i < cellsName.size(); i++) {
            // 循环次数等于参数个数，创建对应行的单元格并设置列名
            Cell cell = row.createCell(i);
            cell.setCellValue(cellsName.get(i));
        }
        // 5 设置除第一行之外的行和列的参数
        Tb_right_organ entry_index = new Tb_right_organ();
        indexList.add(0,entry_index);// --给数据集合添加1个元素 应为第一行没有设置值
        for (int j = 1; j < indexList.size(); j++) {// 有多少行
            Row row1 = sheet.createRow(j);
            for (int k = 0; k < codeFields.size(); k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
                cell.setCellValue(ValueUtil.getPoFieldValue(codeFields.get(k), indexList.get(j)) == null ? " "
                        : ValueUtil.getPoFieldValue(codeFields.get(k), indexList.get(j)).toString());
            }
        }
        indexList.remove(0);
        return workbook;
    }

    //支持100W条以内的导出excel
    public static Sheet SXSSFWorkbookCreateUseExcle(Sheet sheet, List message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        //当前已写入的最大行数
        int rows = sheet.getLastRowNum();
        //获取workbook的sheet数
        if (rows == 0) {//第一行
            // 3.创建第一行，列名行
            Row row = sheet.createRow(0);
            // 4.设置列名
            for (int i = 0; i < fieldname.length; i++) {
                // 循环次数等于参数个数，创建对应行的单元格并设置列名
                Cell cell = row.createCell(i);
                cell.setCellValue(fieldname[i]);
            }
        }
        int c = 0;
        for (int j = rows; j < message.size() + rows; j++) {//循环行数 写入数据
            Row row1 = sheet.createRow(j + 1);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
               /* cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)).toString());*/
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)).toString());
            }
            c++;
        }
        if (c % 900 == 0) {
            ((SXSSFSheet) sheet).flushRows(10);//向磁盘写出890行，留10行下次循环才可以获取行号
            System.gc();
        }
        return sheet;
    }

    public static String createFieldModel(List<Tb_metadata_temp> templates, String nodename, String[] cellsName) {
        //生成存放路径
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/Excel导出/节点字段模板/" + nodename;
        File f = new File(path);
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        //1.创建workbook对象
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        //循环对象
        templates.add(0,new Tb_metadata_temp());
        for (int i = 0; i < templates.size(); i++) {
            Row row = sheet.createRow(i);
            if (i == 0) {
                //设置列头行
                for (int j = 0; j < cellsName.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(cellsName[j]);
                }
                continue;
            }
            for (int j = 0; j < cellsName.length; j++) {
                Cell cell = row.createCell(j);
                try {
                    cell.setCellValue(ValueUtil.getPoFieldValue(cellsName[j], templates.get(i)) == null ?
                            "" : ValueUtil.getPoFieldValue(cellsName[j], templates.get(i)).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // 6---在服务器本地生成excel文件
        String newpath = path + "/" + nodename + ".xlsx";
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(newpath);
            workbook.write(fout);
            workbook.close();
            fout.flush();

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return newpath;
    }

    //支持100W条以内的导出excel
    public static Sheet SXSSFCreateExcle(Sheet sheet, List<Object> message, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        //当前已写入的最大行数
        int rows = sheet.getLastRowNum();
        //获取workbook的sheet数
        if (rows == 0) {//第一行
            // 3.创建第一行，列名行
            Row row = sheet.createRow(0);
            // 4.设置列名
            for (int i = 0; i < fieldname.length; i++) {
                // 循环次数等于参数个数，创建对应行的单元格并设置列名
                Cell cell = row.createCell(i);
                cell.setCellValue(fieldname[i]);
            }
        }
        int c = 0;
        for (int j = rows; j < message.size() + rows; j++) {//循环行数 写入数据
            Row row1 = sheet.createRow(j + 1);
            for (int k = 0; k < fieldcode.length; k++) {// 创建每行的单元格
                Cell cell = row1.createCell(k);
                // --参数使用list 集合泛型是map map通过key获取到value
               /* cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(j-rows+j)).toString());*/
                cell.setCellValue(ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)) == null ? " "
                        : ValueUtil.getPoFieldValue(fieldcode[k], message.get(c)).toString());
            }
            c++;
        }
        if (c % 900 == 0) {
            ((SXSSFSheet) sheet).flushRows(10);//向磁盘写出890行，留10行下次循环才可以获取行号
            System.gc();
        }
        return sheet;
    }
}