package com.wisdom.util;

import com.monitorjbl.xlsx.StreamingReader;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.wisdom.web.entity.Tb_entry_detail;
import com.wisdom.web.entity.Tb_entry_index;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.groovy.util.ListHashMap;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SunK on 2018/7/13 0013.
 */
public class ReadExcel {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 转换excel表格格式 获取值
    public static String getStringVal(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case Cell.CELL_TYPE_FORMULA:// 公式型
                return cell.getCellFormula();
            case Cell.CELL_TYPE_NUMERIC:// 数字型
                cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_STRING:// 字符型
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BLANK: // 空白格时
                return "";
            case Cell.CELL_TYPE_ERROR:
                return "";
            default:
                return "";
        }
    }

    // 转换excel表格格式 获取值
    public static String getStreamingCellStringVal(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case Cell.CELL_TYPE_FORMULA:// 公式型
                return cell.getCellFormula();
            case Cell.CELL_TYPE_NUMERIC:// 数字型
                //cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_STRING:// 字符型
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BLANK: // 空白格时
                return "";
            case Cell.CELL_TYPE_ERROR:
                return "";
            default:
                return "";
        }
    }


    /**
     * 读取excel文件内容 存入List数组 占适用2007之前版本的EXCEL
     *
     * @param file 文件
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static List<List<String>> readXls(File file, String[] fieldName) throws IOException {
        // 创建workbook对象
        List<List<String>> result = new ArrayList<List<String>>();// 存放表格数据
        FileInputStream inputStream = new FileInputStream(file);
        try {
            // 读取excel文件
            // 此处因判断excel的版本 然后创建对应的workbook对象(XSSFWorkbook使用2007之后的版本
            // HSSFWorkbook 适用2007之前的版本)
            Workbook workbook = new HSSFWorkbook(inputStream);
            for (Sheet sheet : workbook) {// 循环sheet数
                if (sheet == null) {
                    continue;
                }
                for (int rownum = 0; rownum < 1; rownum++) {// 判断列头于模板时候相同
                    Row row = sheet.getRow(rownum);
                    //int mincol = row.getFirstCellNum();
                    int maxcol = row.getLastCellNum();
                    for (int col = 0; col < maxcol; col++) {
                        Cell cell = row.getCell(col);
                        if (cell == null) {
                            continue;
                        }
                        //判断当前节点是否存在excel中的该字段 存在ture 否 false
                        boolean b = Arrays.asList(fieldName).contains(cell.getStringCellValue());
                        if (!b) {
                            throw new RuntimeException("excel格式异常");
                        }
                    }
                }
                // 从第2行开始读取（第一行是固定的列名行）
                for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {// 行(从第1行开始到最后一行)
                    Row row = sheet.getRow(rowNum);
                    //int minCol = row.getFirstCellNum();// 当前行最小列
                    int maxCol = row.getLastCellNum();// 当前行最大列号
                    List<String> rowList = new ArrayList<String>();
                    for (int col = 0; col < maxCol; col++) {// 循环列
                        Cell cell = row.getCell(col);// 获取到单元格
                        if (cell == null) {//--当单元格为空时是无法获取到cell的
                            //需要创建新的单元格
                            row.createCell(col).setCellValue("");
                            cell = row.getCell(col);// 获取到单元格
                        }
                        rowList.add(ReadExcel.getStringVal(cell));
                    }
                    result.add(rowList);// 依据循环顺序存入每行 row（cell）中的数据
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return result;
    }

    /**
     * 读取2007之后版本的excel .xlsx
     *
     * @param file 被读取的excle文件（.xlsx）
     * @return
     * @throws IOException
     */
    public static List<List<String>> readXlsx(File file, String[] fieldName) throws IOException {
        List<List<String>> lists = new ArrayList<List<String>>();
        FileInputStream inputStream = new FileInputStream(file);
        try {
            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
            // 读取excel文件
            for (Sheet sheet : wb) {// 循环sheet数
                if (sheet == null) {
                    continue;
                }
                // --第0行为列名行--（行数从0开始）
                for (int rownum = 0; rownum < 1; rownum++) {// 判断列头于模板时候相同
                    Row row = sheet.getRow(rownum);
                    //int mincol = row.getFirstCellNum();
                    int maxcol = row.getLastCellNum();
                    for (int col = 0; col < maxcol; col++) {
                        Cell cell = row.getCell(col);
                        if (cell == null) {
                            continue;
                        }
                        //判断当前节点是否存在excel中的该字段 存在ture 否 false
                       /* boolean b = Arrays.asList(fieldName).contains(cell.getStringCellValue());
                        if (!b) {
                            throw new RuntimeException("excel格式异常,不存在字段" + cell.getStringCellValue());
                        }*/
                    }
                }
                for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    //int minCol = row.getFirstCellNum();
                    int maxCol = row.getLastCellNum();
                    List<String> list = new ArrayList<String>();
                    for (int col = 0; col < maxCol; col++) {
                        Cell cell = row.getCell(col);
                        if (cell == null) {//--当单元格为空时是无法获取到cell的
                            //需要创建新的单元格
                            row.createCell(col).setCellValue("");
                            cell = row.getCell(col);// 获取到单元格
                        }
                        list.add(ReadExcel.getStringVal(cell));
                    }
                    lists.add(list);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return lists;
    }

    /**
     * EXCEL转实体
     * @param file
     * @param map
     * @param table
     * @return
     * @throws Exception
     */
    public static List<Object> readExcelToEntity(File file, Map<String,String> map, String table) throws Exception {
        Class aClass = Class.forName("com.wisdom.web.entity." + table);//根据表名取类
        List<Object> list = new ArrayList<>();
        FileInputStream inputStream = new FileInputStream(file);
        try {
            Workbook wb;
            String excVersion = FileUtil.getExcelVersion(file);
            if ("xls".equals(excVersion)) {
                wb = new HSSFWorkbook(inputStream);
            } else {
                wb = new XSSFWorkbook(inputStream);
            }
            for (Sheet sheet : wb) {// 循环sheet数
                if (sheet == null) {
                    continue;
                }
                //读列：该列列名为空时停止读列；读行：该行所有值为空时停止读行
                boolean readRow = false;//是否继续读行
                Row firstRow = sheet.getRow(0);
                for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {//读行
                    Row row = sheet.getRow(rowNum);
                    int maxCol = row.getLastCellNum();
                    Object object = aClass.newInstance();
                    for (int col = 0; col < maxCol; col++) {//读列
                        String colName = firstRow.getCell(col).getStringCellValue();
                        Cell cell = row.getCell(col);
                        if (colName == null || colName.trim().equals("")) {//列名为空：停止
                            break;
                        }
                        if(cell == null){
                            //需要创建新的单元格
                            row.createCell(col).setCellValue("");
                            cell = row.getCell(col);// 获取到单元格
                        }
                        String rowValue = ReadExcel.getStringVal(cell);
                        if (null!=map.get(colName)||"".equals(map.get(colName))) {
                            object = setValue(rowValue, map.get(colName), aClass, object);
                        }
                        if (!rowValue.trim().equals("")) {
                            readRow = true;
                        }
                    }
                    if (!readRow) {//本行所有列为空：停止
                        break;
                    } else {
                        readRow = false;
                    }
                    list.add(object);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return list;
    }

    //将值设置到Object
    public static Object setValue(String rowValue, String colName, Class aClass, Object object) throws Exception {
        Field field = aClass.getDeclaredField(colName);
        String setMethod = "set" + colName.substring(0, 1).toUpperCase() + colName.substring(1);
        Method fieldSetMet = aClass.getMethod(setMethod, field.getType());

        String fieldType = field.getType().getSimpleName();//获取属性类型
        if ("String".equals(fieldType)) {
            fieldSetMet.invoke(object, rowValue);
        } else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
            Integer value = null;
            if (!"".equals(rowValue)) {
                value = Integer.parseInt(rowValue);
            }
            fieldSetMet.invoke(object, value);
        } else if ("Long".equalsIgnoreCase(fieldType)) {
            Long value = null;
            if (!"".equals(rowValue)) {
                value = Long.parseLong(rowValue);
            }
            fieldSetMet.invoke(object, value);
        } else if ("Double".equalsIgnoreCase(fieldType) || "Float".equalsIgnoreCase(fieldType)) {
            Double value = null;
            if (!"".equals(rowValue)) {
                value = Double.parseDouble(rowValue);
            }
            fieldSetMet.invoke(object, value);
        } else if ("Boolean".equalsIgnoreCase(fieldType)) {
            Boolean value = null;
            if (!"".equals(rowValue)) {
                value = Boolean.parseBoolean(rowValue);
            }
            fieldSetMet.invoke(object, value);
        } else if ("Timestamp".equalsIgnoreCase(fieldType)) {
            Timestamp value = null;
            if (!"".equals(rowValue)) {
                value = Timestamp.valueOf(rowValue);
            }
            fieldSetMet.invoke(object, value);
        } else if ("Date".equalsIgnoreCase(fieldType)) {
            Date value = null;
            if (!"".equals(rowValue)) {
                value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rowValue);
            }
            fieldSetMet.invoke(object, value);
        } else if ("byte[]".equalsIgnoreCase(fieldType)) {
            byte[] value = null;
            if (!"".equals(rowValue)) {
                value = rowValue.getBytes();//TODO
            }
            fieldSetMet.invoke(object, value);
        }
        return object;
    }

    public List<List<String>> getExportInfo(File file) throws IOException {
        List<List<String>> excelDate = new ArrayList<List<String>>();
        // 1.创建输入流读取excel文件
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            // 2根据后缀名回去excle文件版本
            String suffix = FileUtil.getExcelVersion(file);
            // 3 判断excel文件版本 创建workbook对象 对应版本解析
            Workbook wb = null;
            if ("xls".equals(suffix)) {// 读取2007之前的版本
                wb = new HSSFWorkbook(input);
            } else if ("xlsx".equals(suffix)) {// 读取2007之后的版本
                wb = new XSSFWorkbook(input);
            }
            // 4 读取excel文件 创建数组 存储获取到的数据 sheet名，行 单元格--写成了实体类
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {// 读取sheet
                Sheet sheet = wb.getSheetAt(i);

                for (int j = 0; j < sheet.getFirstRowNum(); j++) {// 读取sheet的行
                    Row row = sheet.getRow(j);
                    List<String> cellDataList = new ArrayList<String>();// 存放单元格数据
                    for (int k = 0; k < row.getLastCellNum(); k++) {// 读取列
                        Cell cell = row.getCell(k);
                        // 循环获取cell数据 存入List集合
                        cellDataList.add(ReadExcel.getStringVal(cell));
                    }
                    excelDate.add(cellDataList);
                }
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return excelDate;
    }

    //获取excel列头fieldname-->对应的fieldcode---2007前版本的excel
    private static LinkedHashMap<String, String> getFieldCodeXls(File file, String[] fieldName, String[] fieldCode) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        LinkedHashMap<String, String> map = new LinkedHashMap();
        try {
            Workbook workbook = new HSSFWorkbook(inputStream);
            for (Sheet sheet : workbook) {// 循环sheet数
                if (sheet == null) {
                    continue;
                }
                for (int rownum = 0; rownum < 1; rownum++) {// 值读取excel的列头
                    Row row = sheet.getRow(rownum);
                    //int mincol = row.getFirstCellNum();
                    int maxcol = row.getLastCellNum();
                    for (int col = 0; col < maxcol; col++) {//循环列头单元格，获取值
                        Cell cell = row.getCell(col);
                        if (cell == null) {
                            continue;
                        }
                        for (int i = 0; i < fieldName.length; i++) {
                            if (ReadExcel.getStringVal(cell).equals(fieldName[i])) {
                                map.put(fieldCode[i], fieldName[i]);
                            }
                        }
                    }
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return map;
    }


    //获取excel列头fieldname-->对应的fieldcode---2007之后版本的excel
    private static LinkedHashMap getFieldCodeXlsx(File file, String[] fieldName, String[] fieldCode) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        LinkedHashMap<String, String> map = new LinkedHashMap();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            for (Sheet sheet : workbook) {// 循环sheet数
                if (sheet == null) {
                    continue;
                }
                for (int rownum = 0; rownum < 1; rownum++) {// 值读取excel的列头
                    Row row = sheet.getRow(rownum);
                    //int mincol = row.getFirstCellNum();
                    int maxcol = row.getLastCellNum();
                    for (int col = 0; col < maxcol; col++) {//循环列头单元格，获取值
                        Cell cell = row.getCell(col);
                        if (cell == null) {
                            continue;
                        }
                        for (int i = 0; i < fieldName.length; i++) {
                            if (ReadExcel.getStringVal(cell).equals(fieldName[i])) {
                                map.put(fieldCode[i], fieldName[i]);
                            }
                        }
                    }
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return map;
    }


    private static Map<String, Object> readDate(InputStream in) throws Exception {
        Map<String, Object> resMap = new ListHashMap<>();
        resMap.put("success", true);
        int rowCount = 0;
        List<String> lists = new ArrayList<>();
//        File tempFile = new File("E:/demo1.txt");
//        if (tempFile.exists()) {
//            tempFile.createNewFile();
//        }
        try {
            Workbook workbook = WorkbookFactory.create(in);
            int rowcount = 0;
            for (Sheet sheet : workbook) {// 循环sheet数
                if (sheet == null) {
                    continue;
                }
                rowcount += sheet.getLastRowNum();
                List list = new ArrayList();
                for(Row row:sheet){
                    if (rowCount > 10) {
                        break;
                    }
                    rowCount++;
                    //使用计数器 --第1行为列头行
                    if (rowCount == 1) {
                        int maxcol = row.getLastCellNum();
                        for (int col = 0; col < maxcol; col++) {//循环列头单元格，获取值
                            Cell cell = row.getCell(col);
                            if (cell == null) {
                                continue;
                            }
                            String cellValue = ReadExcel.getStringVal(cell);
                            if(null!=cellValue){
                                cellValue = cellValue.replace(" ","");
                            }
                            lists.add(cellValue);
//                            lists.add(ReadExcel.getStringVal(cell).replace(" ",""));
                        }
                        String[] strings = new String[lists.size()];
                        lists.toArray(strings);
                        resMap.put("header", lists);
                    }
                    if (rowCount > 1) {
                        // 从第2行开始读取（第一行是固定的列名行）
                        //String sub = "";
                        List list1 = new ArrayList();
                        int maxCol = row.getLastCellNum();// 当前行最大列号
                        for (int col = 0; col < maxCol; col++) {// 循环列
                            Cell cell = row.getCell(col);// 获取到单元格
                            if (cell == null) {//--当单元格为空时是无法获取到cell的
                                //空单元格直接存入空字符串
                                list1.add("");
                                continue;
                            }
                            list1.add(ReadExcel.getStringVal(cell));
                            //sub = sub + cell.getStringCellValue();
                        }
                        String[] stringss = new String[list1.size()];
                        list1.toArray(stringss);
                        list.add(stringss);// 依据循环顺序存入每行 row（cell）中的数据
                    }
                    resMap.put("data", list);
                }
                /*int forcount=0;
                List<String> lists = new ArrayList<>();
                if (forcount==0) {// 值读取excel的列头
                    Row row = sheet.getRow(forcount);
                    int maxcol = 0;
                    for(Cell cell:row){
                        maxcol++;
                    }
                    for (int col = 0; col < maxcol; col++) {//循环列头单元格，获取值
                        Cell cell = row.getCell(col);
                        if (cell == null) {
                            continue;
                        }
                        lists.add(cell.getStringCellValue());
                    }
                    forcount++;
                }
                String[] strings = new String[lists.size()];
                lists.toArray(strings);
                resMap.put("header", lists);
                // 从第2行开始读取（第一行是固定的列名行）
                List list = new ArrayList();
                int showRows = sheet.getLastRowNum();
                if (showRows > 10) {//防止上传文件少于10行预览数据
                    showRows = 10;
                }
                for (int rowNum = 1; rowNum <= showRows; rowNum++) {// 行(从第1行开始到最后一行)
                    String sub = "";
                    List list1 = new ArrayList();
                    Row row = sheet.getRow(rowNum);
                    //int minCol = row.getFirstCellNum();// 当前行最小列
                    int maxCol = row.getLastCellNum();// 当前行最大列号
                    for (int col = 0; col < maxCol; col++) {// 循环列
                        Cell cell = row.getCell(col);// 获取到单元格
                        if (cell == null) {//--当单元格为空时是无法获取到cell的
                            //需要创建新的单元格
                            row.createCell(col).setCellValue("");
                            cell = row.getCell(col);// 获取到单元格
                        }
                        list1.add(cell.getStringCellValue());
                        sub = sub + cell.getStringCellValue();
                    }
                    String[] stringss = new String[list1.size()];
                    list1.toArray(stringss);
                    list.add(stringss);// 依据循环顺序存入每行 row（cell）中的数据
                }
                resMap.put("data", list);*/
            }
            resMap.put("rowCount", rowcount+1);
        } finally {
            /*if (inputStream != null) {
                inputStream.close();
			}*/
        }

        return resMap;
    }
    private static Map<String, Object> readDateXlsx(InputStream inputStream) throws IOException {
        Map<String, Object> resMap = new ListHashMap<>();
        int rowCount = 0;
        resMap.put("success", true);
        try {
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(200)  //缓存到内存中的行数，默认是10
                    .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                    .open(inputStream);  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
            List<String> lists = new ArrayList<>();
            int rowcount = 0;
            for (Sheet sheet : workbook) {// 循环sheet数
                if (sheet == null) {
                    continue;
                }
                rowcount += sheet.getLastRowNum();
                List list = new ArrayList();
                for (Row row : sheet) {

                    if (rowCount > 10) {
                        break;
                    }
                    rowCount++;
                    //使用计数器 --第1行为列头行
                    if (rowCount == 1) {
                        int maxcol = row.getLastCellNum();
                        for (int col = 0; col < maxcol; col++) {//循环列头单元格，获取值
                            Cell cell = row.getCell(col);
                            if (cell == null) {
                                continue;
                            }
                            String cellValue = ReadExcel.getStreamingCellStringVal(cell);
                            if(null!=cellValue){
                                cellValue = cellValue.replace(" ","");
                            }
                            lists.add(cellValue);
                        }
                        String[] strings = new String[lists.size()];
                        lists.toArray(strings);
                        resMap.put("header", lists);
                    }
                    if (rowCount > 1) {
                        // 从第2行开始读取（第一行是固定的列名行）
                        //String sub = "";
                        List list1 = new ArrayList();
                        int maxCol = row.getLastCellNum();// 当前行最大列号
                        for (int col = 0; col < maxCol; col++) {// 循环列
                            Cell cell = row.getCell(col);// 获取到单元格
                            if (cell == null) {//--当单元格为空时是无法获取到cell的
                                //空单元格直接存入空字符串
                                list1.add("");
                                continue;
                            }
                            list1.add(ReadExcel.getStreamingCellStringVal(cell));
                            //sub = sub + cell.getStringCellValue();
                        }
                        String[] stringss = new String[list1.size()];
                        list1.toArray(stringss);
                        list.add(stringss);// 依据循环顺序存入每行 row（cell）中的数据
                    }
                    resMap.put("data", list);
                }
            }
            resMap.put("rowCount", rowcount+1);
        } finally {
            /*if (inputStream != null) {
                inputStream.close();
			}*/

        }
        return resMap;
    }


    /**
     * 读取所有版本的excle
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static List<List<String>> readAllVersionExcel(File file, String[] fieldName) throws IOException {
        String excVersion = FileUtil.getExcelVersion(file);
        List<List<String>> lists = new ArrayList<List<String>>();
        if ("xls".equals(excVersion)) {
            lists = readXls(file, fieldName);
        } else {
            lists = readXlsx(file, fieldName);
        }
        return lists;
    }

    public static LinkedHashMap<String, String> getAllFieldCode(File file, String[] fieldName, String[] fieldCode) throws IOException {
        String excVersion = FileUtil.getExcelVersion(file);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if ("xls".equals(excVersion)) {
            map = getFieldCodeXls(file, fieldName, fieldCode);
        } else {
            map = getFieldCodeXlsx(file, fieldName, fieldCode);
        }
        //String[] strArray = list.toArray(new String[list.size()]);
        return map;
    }

    /**
     * 解析excel 返回10行预览数据
     *
     * @param filePath 文件全路径
     * @return
     * @throws Exception
     */
    public static Map<String, Object> readAllexcelDate(String filePath) throws Exception {
        Map<String, Object> resMap = new ListHashMap<>();
        if (filePath.endsWith(".xls")) {
            resMap = readDate(new FileInputStream(new File(filePath)));
        } else if (filePath.endsWith(".xlsx")) {
            resMap = readDateXlsx(new FileInputStream(new File(filePath)));
        }
        return resMap;
    }
    
    //读取列头
    public static List<String> getHeadField(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        List<String> strings = new ArrayList<>();
        try {
            // 2根据后缀名回去excle文件版本
            String suffix = FileUtil.getExcelVersion(file);
            // 3 判断excel文件版本 创建workbook对象 对应版本解析
            Workbook workbook = null;
            if ("xls".equals(suffix)) {// 读取2007之前的版本
                workbook = new HSSFWorkbook(inputStream);
            } else if ("xlsx".equals(suffix)) {// 读取2007之后的版本
                workbook = new XSSFWorkbook(inputStream);
            }
            for (Sheet sheet : workbook) {// 循环sheet数
                if (sheet == null) {
                    continue;
                }
                for (int rownum = 0; rownum < 1; rownum++) {// 值读取excel的列头
                    Row row = sheet.getRow(rownum);
                    //int mincol = row.getFirstCellNum();
                    int maxcol = row.getLastCellNum();
                    for (int col = 0; col < maxcol; col++) {//循环列头单元格，获取值
                        Cell cell = row.getCell(col);
                        if (cell == null) {
                            continue;
                        }
                        strings.add(cell.getStringCellValue());
                    }
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return strings;
    }

    //---------------------------------处理100w以内数据------------------

    private static Map<String, Object> readdemoDateXlsx(InputStream inputStream, Iterator<Row> rows) throws IOException {

        int rowCount = 0;
        try {

            while (rows.hasNext()) {
                Row row = rows.next();
                //使用计数器 --第1行为列头行
                if (rowCount == 0) {
                    int maxcol = row.getLastCellNum();
                    for (int col = 0; col < maxcol; col++) {//循环列头单元格，获取值
                        Cell cell = row.getCell(col);
                        if (cell == null) {
                            continue;
                        }
                    }
                }
                if (rowCount > 0) {
                    int maxCol = row.getLastCellNum();// 当前行最大列号
                    for (int col = 0; col < maxCol; col++) {// 循环列
                        Cell cell = row.getCell(col);// 获取到单元格
                        if (cell == null) {//--当单元格为空时是无法获取到cell的
                            //需要创建新的单元格
                            row.createCell(col).setCellValue("");
                            cell = row.getCell(col);// 获取到单元格
                        }
                    }
                }

            }
        } finally {


        }
        return null;
    }

    //excel列头转换 map（key-value）
    public static List changeExcelHead(Map<String,String> map,String filepath){
        FileInputStream inputStream = null;
        List l = new ArrayList();
        try{
            inputStream = new FileInputStream(new File(filepath));
            // 2根据后缀名回去excle文件版本
            String suffix = FileUtil.getExcelVersion(new File(filepath));
            // 3 判断excel文件版本 创建workbook对象 对应版本解析
            Workbook workbook = null;
            if ("xls".equals(suffix)) {// 读取2007之前的版本
                workbook = new HSSFWorkbook(inputStream);
            } else if ("xlsx".equals(suffix)) {// 读取2007之后的版本
                workbook = new XSSFWorkbook(inputStream);
            }
            for (Sheet sheet : workbook) {// 循环sheet数
                if (sheet == null) {
                    continue;
                }
                for (int rownum = 0; rownum < 1; rownum++) {
                    Row row = sheet.getRow(rownum);
                    int maxcol = row.getLastCellNum();
                    for (int col = 0; col < maxcol; col++) {//循环列头单元格，获取值
                        Cell cell = row.getCell(col);
                        if (cell == null) {
                            continue;
                        }
                        if(map.get(cell.getStringCellValue())!=null){
                            String value = map.get(cell.getStringCellValue());
                            cell.setCellValue(value);
                            l.add(value);
                        }
                    }
                }
            }

            OutputStream os = new FileOutputStream(new File(filepath));
            workbook.write(os);
            os.flush();
            os.close();
            workbook.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return l;
    }

    public static List<List<List<String>>> readFieldModel(String filepath){
        FileInputStream inputStream = null;
        List<List<List<String>>> lists = new ArrayList<>();

        try {
            inputStream = new FileInputStream(new File(filepath));
            // 2根据后缀名回去excle文件版本
            String suffix = FileUtil.getExcelVersion(new File(filepath));
            // 3 判断excel文件版本 创建workbook对象 对应版本解析
            Workbook workbook = null;
            if ("xls".equals(suffix)) {// 读取2007之前的版本
                workbook = new HSSFWorkbook(inputStream);
            } else if ("xlsx".equals(suffix)) {// 读取2007之后的版本
                workbook = new XSSFWorkbook(inputStream);
            }
            for (Sheet sheet : workbook) {// 循环sheet数
                List<List<String>> list = new ArrayList<>();
                if (sheet == null) {
                    continue;
                }
                for (int rownum = 1; rownum <=sheet.getLastRowNum(); rownum++) {
                    List<String> strings = new ArrayList<>();
                    Row row = sheet.getRow(rownum);
                    //int mincol = row.getFirstCellNum();
                    int maxcol = row.getLastCellNum();
                    for (int col = 0; col < maxcol; col++) {//循环列头单元格，获取值
                        Cell cell = row.getCell(col);
                        if (cell == null) {
                            continue;
                        }
                        if("xls".equals(suffix)) {
                            strings.add(ReadExcel.getStreamingCellStringVal(cell));
                        }else {
                            strings.add(ReadExcel.getStringVal(cell));
                        }
                    }
                    list.add(strings);
                }
                lists.add(list);
            }
        }catch (IOException io){
            io.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
        }
        return lists;
    }

    // 转换excel表格格式 获取值
    public static String getStringValnew(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case Cell.CELL_TYPE_FORMULA:// 公式型
                return cell.getCellFormula();
            case Cell.CELL_TYPE_NUMERIC:// 数字型
                String cellValue = "";
                // poi解析数字会转成科学计数法，需要把它转回字符串
                double dc = cell.getNumericCellValue();
                BigDecimal bigDecimal = new BigDecimal(dc);
                cellValue = bigDecimal.toPlainString();
                return cellValue;
            case Cell.CELL_TYPE_STRING:// 字符型
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BLANK: // 空白格时
                return "";
            case Cell.CELL_TYPE_ERROR:
                return "";
            default:
                return "";
        }
    }

    public static void main(String[] age)throws Exception {
        //账号：^[a-zA-Z][a-zA-Z0-9]{5,17}$
        // 密码：^[a-zA-Z](?![0-9]+$)(?![a-zA-Z]+$)([a-zA-Z0-9]|[._#@]){7,17}$
//        String regex = "^[a-zA-Z](?![0-9]+$)(?![a-zA-Z]+$)([a-zA-Z0-9]|[._#@]){5,17}$";
//        String value = "as123123123.#@";
//        System.out.println(value.matches(regex));
//        String filepath="E:\\阳江市.xlsx";
//        Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(filepath)));
//        Workbook workbook = StreamingReader.builder()
//                .rowCacheSize(100)  //缓存到内存中的行数，默认是10
//                .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
//                .open(new FileInputStream(new File(filepath)));  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件

//        for(Sheet sheet:workbook){
//            for(Row row:sheet){
//                for(Cell cell:row){
//                    System.out.println(ReadExcel.getStringVal(cell));
//                }
//            }
//        }
    }
}