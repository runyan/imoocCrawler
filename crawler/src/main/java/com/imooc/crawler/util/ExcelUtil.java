package com.imooc.crawler.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.imooc.crawler.entity.ImoocCourse;

public class ExcelUtil {
	
	private volatile static ExcelUtil instance;
	
	private final String STORE_PATH;
	
	private ExcelUtil(String storeDir){
		this.STORE_PATH = storeDir;
	}
	
	public static ExcelUtil getInstance(String storeDir) {
		if(null == instance) {
			synchronized (ExcelUtil.class) {
				if(null == instance) {
					instance = new ExcelUtil(storeDir);
				}
			}
		}
		return instance;
	}
	
	private String getStorePath() {
		return FileUtil.createDir(STORE_PATH);
	}

	public boolean writeToExcel(List<ImoocCourse> courses) throws IOException {
		int rowsInserted = 0;
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow headerRow = sheet.createRow(0);
		HSSFCell cell = headerRow.createCell(0);
		cell.setCellValue("课程名称");
		cell = headerRow.createCell(1);
		cell.setCellValue("课程图片URL");
		cell = headerRow.createCell(2);
		cell.setCellValue("课程等级");
		cell = headerRow.createCell(3);
		cell.setCellValue("课程标签");
		cell = headerRow.createCell(4);
		cell.setCellValue("课程简介");
		cell = headerRow.createCell(5);
		cell.setCellValue("学习人数");
		HSSFRow contentRow;
		ImoocCourse course;
		for(int i = 0; i < courses.size(); i++) {
			contentRow = sheet.createRow(i + 1);
			course = courses.get(i);
			contentRow.createCell(0).setCellValue(course.getCourseName());
			contentRow.createCell(1).setCellValue(course.getImgSrc());
			contentRow.createCell(2).setCellValue(course.getCourseLevel());
			contentRow.createCell(3).setCellValue(course.getCourseLabels().toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			contentRow.createCell(4).setCellValue(course.getCourseDesc());
			contentRow.createCell(5).setCellValue(course.getStudyNum());
			rowsInserted++;
		}
		String fileName = "courses.xls";
		String storePath = getStorePath() + File.separator + fileName;
		FileOutputStream fos = new FileOutputStream(storePath);
		workbook.write(fos);
		fos.close();
		return rowsInserted == courses.size();
	}
}
