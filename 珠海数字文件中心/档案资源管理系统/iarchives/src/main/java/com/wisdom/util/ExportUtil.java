package com.wisdom.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by xd on 2017/10/18.
 */
public class ExportUtil {

	private HttpServletResponse mResponse;
	private String fileName;
	private List<Map<String, Object>> mapList;
	private String[] columnKeys;
	private String[] columnNames;

	public ExportUtil() {
	}

	public ExportUtil(String name, HttpServletResponse response, String[] names) {
		this.fileName = name;
		this.mResponse = response;
		this.columnNames = names;
	}

	public ExportUtil(String name, HttpServletResponse response, List<Map<String, Object>> list, String[] keys,
			String[] names) {
		this.fileName = name;
		this.mResponse = response;
		this.mapList = list;
		this.columnKeys = keys;
		this.columnNames = names;
	}

	/**
	 * 创建excel文档，
	 *
	 * @param fileName
	 *            文件名
	 * @param list
	 *            数据
	 * @param keys
	 *            list中map的key数组集合
	 * @param names
	 *            excel的列名
	 */
	public Workbook createWorkBook(Workbook wb, String fileName, List<Map<String, Object>> list, String[] keys,
			String[] names) {
		// 创建excel工作簿
		// XSSFWorkbook
		// Workbook wb = new HSSFWorkbook();
		// 创建第一个sheet（页），并命名
		Sheet sheet = wb.createSheet();
		// 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
		for (int i = 0; i < keys.length; i++) {
			sheet.setColumnWidth((short) i, (short) (35.7 * 150));
		}

		// 创建第一行
		Row row = sheet.createRow((short) 0);

		// 创建两种单元格格式
		CellStyle cs = wb.createCellStyle();
		CellStyle cs2 = wb.createCellStyle();

		// 创建两种字体
		Font f = wb.createFont();
		Font f2 = wb.createFont();

		// 创建第一种字体样式（用于列名）
		f.setFontHeightInPoints((short) 10);
		f.setColor(IndexedColors.BLACK.getIndex());
		f.setBold(true);


		// 创建第二种字体样式（用于值）
		f2.setFontHeightInPoints((short) 10);
		f2.setColor(IndexedColors.BLACK.getIndex());

		// 设置第一种单元格的样式（用于列名）
		cs.setFont(f);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setAlignment(HorizontalAlignment.CENTER);

		// 设置第二种单元格的样式（用于值）
		cs2.setFont(f2);
		cs2.setBorderLeft(BorderStyle.THIN);
		cs2.setBorderRight(BorderStyle.THIN);
		cs2.setBorderTop(BorderStyle.THIN);
		cs2.setBorderBottom(BorderStyle.THIN);
		cs2.setAlignment(HorizontalAlignment.CENTER);
		// 设置列名
		for (int i = 0; i < names.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(names[i]);
			cell.setCellStyle(cs);
		}
		// 设置每行每列的值
		for (short i = 0; i < list.size(); i++) {
			// Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
			// 创建一行，在页sheet上
			Row row1 = sheet.createRow(i + 1);
			// 在row行上创建一个方格
			for (short j = 0; j < keys.length; j++) {
				Cell cell = row1.createCell(j);
				cell.setCellValue(list.get(i).get(keys[j]) == null ? " " : list.get(i).get(keys[j]).toString());
				cell.setCellStyle(cs2);
			}
		}
		return wb;
	}

	public Workbook createWorkBookForTemplate(String fileName, String[] keys) {
		// 创建excel工作簿
		Workbook wb = new HSSFWorkbook();
		// 创建第一个sheet（页），并命名
		Sheet sheet = wb.createSheet();
		// 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
		for (int i = 0; i < keys.length; i++) {
			sheet.setColumnWidth((short) i, (short) (35.7 * 150));
		}
		Font f = wb.createFont();
		// 创建第一行
		Row row = sheet.createRow((short) 0);
		CellStyle cs = wb.createCellStyle();
		// 创建第一种字体样式（用于列名）
		f.setFontHeightInPoints((short) 10);
		f.setColor(IndexedColors.BLACK.getIndex());
		f.setBold(true);

		// 设置第一种单元格的样式（用于列名）
		cs.setFont(f);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setAlignment(HorizontalAlignment.CENTER);

		// 设置列名
		for (int i = 0; i < keys.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(keys[i]);
			cell.setCellStyle(cs);
		}
		return wb;
	}

	public void exportExcel() {
		try {
			Workbook wb = new HSSFWorkbook();
			Workbook workbook = createWorkBook(wb, fileName, mapList, columnKeys, columnNames);
			OutputStream out = mResponse.getOutputStream();
			mResponse.setContentType("application/ms-excel;charset=UTF-8");
			mResponse.setHeader("Content-Disposition",
					"attachment;filename=\"" + new String((fileName + ".xls").getBytes(), "iso-8859-1") + "\"");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportExcelTemplate() {
		try {
			Workbook workbook = createWorkBookForTemplate(fileName, columnNames);
			OutputStream out = mResponse.getOutputStream();
			mResponse.setContentType("application/ms-excel;charset=UTF-8");
			// mResponse.setHeader("Content-Disposition", "attachment;filename="
			// + new String((fileName + ".xls").getBytes(),
			// "iso-8859-1"));
			mResponse.addHeader("Content-Disposition",
					"attachment;filename=\"" + new String((fileName + ".xls").getBytes("GBK"), "ISO8859_1") + "\"");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
