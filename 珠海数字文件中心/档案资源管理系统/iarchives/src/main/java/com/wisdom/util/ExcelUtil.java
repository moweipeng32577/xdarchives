package com.wisdom.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;


/**
 * Created by yl on 2017/11/9.
 */
public class ExcelUtil<T> implements Serializable {

	private static final long serialVersionUID = 551970754610248636L;

	private Class<T> clazz;

	public ExcelUtil(Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * 将excel表单数据源的数据导入到list
	 *
	 * @param sheetName
	 *            工作表的名称
	 */
	public List<T> getExcelToList(String sheetName, InputStream input) {
		List<T> list = new ArrayList<T>();
		try {
			Workbook book = WorkbookFactory.create(input);
			Sheet sheet = null;
			// 如果指定sheet名,则取指定sheet中的内容.
			if (StringUtils.isNotBlank(sheetName)) {
				sheet = book.getSheet(sheetName);
			}
			// 如果传入的sheet名不存在则默认指向第1个sheet.
			if (sheet == null) {
				sheet = book.getSheetAt(0);
			}
			// 得到数据的行数
			int rows = sheet.getLastRowNum();
			// 没有数据直接返回集合
			if (rows == 0)
				return list;
			Map<String, Integer> cellNameMap = getCellNameMap(sheet);
			Field[] colFields = clazz.getDeclaredFields();
			// 获取带有注解的字段
			Field[] matchedColFields = matchDeclaredFields(colFields, ExcelAttribute.class);
			if (matchedColFields != null && matchedColFields.length > 0) {
				// 从第2行开始取数据,默认第一行是表头
				for (int i = 1; i <= rows; i++) {
					// 得到行对象
					Row row = sheet.getRow(i);
					T entity = clazz.newInstance();
					// 遍历字段，一个个往列名去找
					for (int j = 0; j < matchedColFields.length; j++) {
						Field colField = matchedColFields[j];
						ExcelAttribute excelColNameAnno = colField.getAnnotation(ExcelAttribute.class);
						String excelColName = excelColNameAnno.name().trim();
						if (cellNameMap.get(excelColName) != null) {
							Cell cell = row.getCell(cellNameMap.get(excelColName));
							if (cell != null) {
								String cellValue = getCellValue(cell);
								colField.set(entity, cellValue);
							}
						}
					}
					list.add(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取当前第一列的列名和第几列
	 * 
	 * @param sheet
	 * @return
	 */
	public Map<String, Integer> getCellNameMap(Sheet sheet) {
		Map<String, Integer> colNameMap = new HashMap<String, Integer>();
		// 获取第一行
		Row firstRow = sheet.getRow(0);
		// 获取列数
		int cellNum = firstRow.getLastCellNum();
		for (int i = 0; i < cellNum; i++) {
			colNameMap.put(getCellValue(firstRow.getCell(i)), i);
		}
		return colNameMap;
	}

	/**
	 * 获取列值
	 * 
	 * @param cell
	 * @return
	 */
	private String getCellValue(Cell cell) {
		String cellValue = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			cellValue = cell.getRichStringCellValue().getString().trim();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				cellValue = simpleDateFormat.format(date);
			} else {
				// poi解析数字会转成科学计数法，需要把它转回字符串
				double dc = cell.getNumericCellValue();
				BigDecimal bigDecimal = new BigDecimal(dc);
				cellValue = bigDecimal.toPlainString();
			}
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			cellValue = String.valueOf(cell.getNumericCellValue());
			break;

		default:
			cellValue = "";
		}
		return cellValue;
	}

	public <T extends AnnotationDocument.Annotation> Field[] matchDeclaredFields(Field[] declaredFields, Class T) {
		List<Field> matchedDeclaredFieldsList = new ArrayList<Field>();
		for (int i = 0; i < declaredFields.length; i++) {
			Field sheetFiled = declaredFields[i];
			sheetFiled.setAccessible(true);
			if (sheetFiled.getAnnotation(T) != null) {
				matchedDeclaredFieldsList.add(sheetFiled);
			}
		}
		Field[] matchedDeclaredFieldsArray = null;
		if (matchedDeclaredFieldsList.size() > 0) {
			matchedDeclaredFieldsArray = new Field[matchedDeclaredFieldsList.size()];
			for (int i = 0; i < matchedDeclaredFieldsArray.length; i++) {
				matchedDeclaredFieldsArray[i] = matchedDeclaredFieldsList.get(i);
			}
		}
		return matchedDeclaredFieldsArray;
	}

	/**
	 * 将list数据源的数据导入到excel表单
	 *
	 * @param list
	 *            数据源
	 * @param sheetName
	 *            工作表的名称
	 */
	public String  getListToExcel(List<T> list,String sheetName,String path) {
		try {
			// excel中每个sheet中最多有65536行
			int sheetSize = 65536;
			// 得到所有定义字段
			Field[] allFields = clazz.getDeclaredFields();
			List<Field> fields = new ArrayList<Field>();
			// 得到所有field并存放到一个list中
			for (Field field : allFields) {
				if (field.isAnnotationPresent(ExcelAttribute.class)) {
					fields.add(field);
				}
			}
			// 产生工作薄对象
			HSSFWorkbook workbook = new HSSFWorkbook();
			// 取出一共有多少个sheet
			int listSize = 0;
			if (list != null && list.size() >= 0) {
				listSize = list.size();
			}
			double sheetNo = Math.ceil(listSize / sheetSize);
			for (int index = 0; index <= sheetNo; index++) {
				// 产生工作表对象
				HSSFSheet sheet = workbook.createSheet();
				// 设置工作表的名称.
				workbook.setSheetName(index, sheetName + index);
				HSSFRow row;
				HSSFCell cell;// 产生单元格
				row = sheet.createRow(0);// 产生一行
				/* *********普通列样式********* */
				HSSFFont font = workbook.createFont();
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				font.setFontName("Arail narrow"); // 字体
				font.setBold(true); // 字体宽度
				/* *********标红列样式********* */
				HSSFFont newFont = workbook.createFont();
				HSSFCellStyle newCellStyle = workbook.createCellStyle();
				newFont.setFontName("Arail narrow"); // 字体
				newFont.setBold(true); // 字体宽度
				/* *************创建列头名称*************** */
				for (int i = 0; i < fields.size(); i++) {
					Field field = fields.get(i);
					ExcelAttribute attr = field.getAnnotation(ExcelAttribute.class);
					int col = i;
					// 根据指定的顺序获得列号
					if (StringUtils.isNotBlank(attr.column())) {
						col = getExcelCol(attr.column());
					}
					// 创建列
					cell = row.createCell(col);
					if (attr.isMark()) {
						newFont.setColor(HSSFFont.COLOR_RED); // 字体颜色
						newCellStyle.setFont(newFont);
						cell.setCellStyle(newCellStyle);
					} else {
						font.setColor(HSSFFont.COLOR_NORMAL); // 字体颜色
						cellStyle.setFont(font);
						cell.setCellStyle(cellStyle);
					}
					sheet.setColumnWidth(i,
							(int) ((attr.name().getBytes().length <= 4 ? 6 : attr.name().getBytes().length) * 1.5
									* 256));
					// 设置列中写入内容为String类型
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					// 写入列名
					cell.setCellValue(attr.name());
				}
				/* *************创建内容列*************** */
				font = workbook.createFont();
				cellStyle = workbook.createCellStyle();
				int startNo = index * sheetSize;
				int endNo = Math.min(startNo + sheetSize, listSize);
				// 写入各条记录,每条记录对应excel表中的一行
				for (int i = startNo; i < endNo; i++) {
					row = sheet.createRow(i + 1 - startNo);
					T vo = (T) list.get(i); // 得到导出对象.
					for (int j = 0; j < fields.size(); j++) {
						// 获得field
						Field field = fields.get(j);
						// 设置实体类私有属性可访问
						// field.setAccessible(true);
						ExcelAttribute attr = field.getAnnotation(ExcelAttribute.class);
						int col = j;
						// 根据指定的顺序获得列号
						if (StringUtils.isNotBlank(attr.column())) {
							col = getExcelCol(attr.column());
						}
						// 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
						if (attr.isExport()) {
							// 创建cell
							cell = row.createCell(col);
							if (attr.isMark()) {
								newFont.setColor(HSSFFont.COLOR_RED); // 字体颜色
								newCellStyle.setFont(newFont);
								cell.setCellStyle(newCellStyle);
							} else {
								font.setColor(HSSFFont.COLOR_NORMAL); // 字体颜色
								cellStyle.setFont(font);
								cell.setCellStyle(cellStyle);
							}
							// 如果数据存在就填入,不存在填入空格
							Class<?> classType = (Class<?>) field.getType();
							cell.setCellValue(field.get(vo) == null ? "" : String.valueOf(field.get(vo)));
						}
					}
				}
			}
			FileOutputStream fout = new FileOutputStream(path);
			workbook.write(fout);
			workbook.close();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * 将EXCEL中A,B,C,D,E列映射成0,1,2,3
	 *
	 * @param col
	 */
	public static int getExcelCol(String col) {
		col = col.toUpperCase();
		// 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。
		int count = -1;
		char[] cs = col.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);
		}
		return count;
	}
}