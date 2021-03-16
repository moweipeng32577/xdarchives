package com.xdtech.project.lot.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class expUtil {

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
                cell.setCellValue(getPoFieldValue(fieldcode[k], message.get(c)) == null ? " "
                        : getPoFieldValue(fieldcode[k], message.get(c)).toString());
            }
            c++;
        }
        if (c % 900 == 0) {
            ((SXSSFSheet) sheet).flushRows(10);//向磁盘写出890行，留10行下次循环才可以获取行号
            System.gc();
        }
        return sheet;
    }

    public static Object getPoFieldValue(String fieldName, Object po)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        Class poClass = Class.forName(po.getClass().getName());
        //fieldName = fieldName;
        String methodName = fieldName.trim().substring(0, 1).toUpperCase() + fieldName.substring(1);
        (new StringBuilder("set")).append(methodName).toString();
        String getMethod = "get" + methodName;
        Method meth = null;
        String fieldType = "string";
        Field field = null;
        Class[] partypes = new Class[1];
        Object fieldNameValue = null;
        try {
            field = poClass.getField(fieldName);//获取类的属性
        } catch (Exception var13) {
            field = null;
        }
        if (field != null) {
            fieldType = field.getType().getName().trim();
            if (fieldType.trim().toLowerCase().lastIndexOf("string") >= 0) {
                partypes[0] = field.getType().getName().getClass();
                meth = poClass.getMethod(getMethod, (Class[]) null);
                fieldNameValue = meth.invoke(po, (Object[]) null);
            } else if (fieldType.trim().toLowerCase().equalsIgnoreCase("int")) {
                partypes[0] = Integer.TYPE;
                meth = poClass.getMethod(getMethod, (Class[]) null);
                fieldNameValue = meth.invoke(po, (Object[]) null);
                fieldNameValue = String.valueOf(fieldNameValue);
            } else if (fieldType.trim().toLowerCase().lastIndexOf("integer") >= 0) {
                partypes[0] = Integer.class;
                meth = poClass.getMethod(getMethod, (Class[]) null);
                fieldNameValue = meth.invoke(po, (Object[]) null);
                fieldNameValue = String.valueOf(fieldNameValue);
            } else if (fieldType.trim().toLowerCase().lastIndexOf("double") >= 0) {
                partypes[0] = Double.TYPE;
                meth = poClass.getMethod(getMethod, (Class[]) null);
                fieldNameValue = meth.invoke(po, (Object[]) null);
                fieldNameValue = String.valueOf(fieldNameValue);
            } else if (fieldType.trim().toLowerCase().lastIndexOf("date") >= 0) {
                partypes[0] = Date.class;
                meth = poClass.getMethod(getMethod, (Class[]) null);
                fieldNameValue = meth.invoke(po, (Object[]) null);
                fieldNameValue = String.valueOf(fieldNameValue).substring(0, 10);
            } else if (fieldType.trim().toLowerCase().lastIndexOf("long") >= 0) {
                partypes[0] = Long.TYPE;
                meth = poClass.getMethod(getMethod, (Class[]) null);
                fieldNameValue = meth.invoke(po, (Object[]) null);
                fieldNameValue = String.valueOf(fieldNameValue);
            }
        } else {
            meth = poClass.getMethod(getMethod, (Class[]) null);
            fieldNameValue = meth.invoke(po, (Object[]) null);
            fieldNameValue = String.valueOf(fieldNameValue);
        }
        if ("null".equals(fieldNameValue)) {// 判断字符串为null时设置为空格
            fieldNameValue = "";
        }
        return fieldNameValue;
    }


    /**
     * 拆分数组-解决当 in查询参数超过1000问题
     * @param arr 数组
     * @param limit 每次拆分量
     * @return
     */
    public static List<String[]> splitAry(String[] arr, int limit) {
        int arrlen = arr.length;
        //拆分次数
        int count = arrlen % limit == 0 ? arrlen / limit : arrlen / limit + 1;

        List<List<String>> subAryList = new ArrayList<>();

        for (int i = 0; i < count; i++) {//分组
            int index = i * limit;
            List<String> list = new ArrayList<>();
            int j = 0;
            while (j < limit && index < arr.length) {
                list.add(arr[index++]);
                j++;
            }
            subAryList.add(list);
        }
        List<String[]> list1 = new ArrayList<>();
        for (int k = 0; k < subAryList.size(); k++) {
            String[] str = subAryList.get(k).toArray(new String[subAryList.get(k).size()]);
            list1.add(str);
        }
        return list1;

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
            e.printStackTrace();
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
}
