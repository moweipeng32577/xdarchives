package com.wisdom.util;

import com.wisdom.web.entity.Tb_log_msg;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xd on 2017/10/19.
 */
public class ImportUtil {
	// 列头名称
	private String[] columnNames;
	private static final DataFormatter FORMATTER = new DataFormatter();

	public ImportUtil(String[] names) {
		this.columnNames = names;
	}

	public List<Tb_log_msg> importFile(MultipartFile file) {
		List<Tb_log_msg> logDetails = new ArrayList<Tb_log_msg>();
		try {
			InputStream inputStream = file.getInputStream();// 获取excel数据
			Workbook hssfBook = new HSSFWorkbook(inputStream);
			// 获取第一页
			Sheet sheet = hssfBook.getSheetAt(0);
			int rowNum = sheet.getLastRowNum();
			if (rowNum == 0) {// 指表头
				return logDetails;
			}
			Row row = sheet.getRow(0);// 第一行数据
			int cellCount = row.getLastCellNum();
			// 验证表头格式是否正确
			for (int i = 0; i < columnNames.length; i++) {
				row.getCell(i).setCellType(Cell.CELL_TYPE_STRING);
				if (!columnNames[i].trim().equals(row.getCell(i).getStringCellValue().trim())) {
					return logDetails;
				}
			}
			for (int i = 1; i <= rowNum; i++) {
				row = sheet.getRow(i);
				Tb_log_msg logDetail = new Tb_log_msg();
				// logDetail.setLogID("001");
				for (int j = 0; j < cellCount; j++) {
					Cell cell = row.getCell(j);
					switch (j) {
					case 0:
						logDetail.setOperate_user(getCellContent(cell));
					case 1:
						logDetail.setRealname(getCellContent(cell));
					case 2:
						logDetail.setOrgan(getCellContent(cell));
					case 3:
						logDetail.setIp(getCellContent(cell));
					case 4:
						logDetail.setModule(getCellContent(cell));
					case 5:
						logDetail.setStartTime(getCellContent(cell));
					case 6:
						logDetail.setDesci(getCellContent(cell));
					}
				}
				logDetails.add(logDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logDetails;
	}

	/**
	 * 获取单元格内容
	 *
	 * @param cell
	 *            单元格对象
	 * @return 转化为字符串的单元格内容
	 */
	private static String getCellContent(Cell cell) {
		return FORMATTER.formatCellValue(cell);
	}
}
