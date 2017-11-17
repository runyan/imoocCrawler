package com.imooc.crawler.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import lombok.Cleanup;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.imooc.crawler.entity.ImoocCourse;

public class ExcelUtil {
	
	private volatile static ExcelUtil instance;
	
	private final String STORE_PATH;
	private HSSFWorkbook workBook; //Excel工作簿
	private HSSFSheet workSheet; //Excel工作表
	
	private ExcelUtil(String storeDir){
		this.STORE_PATH = storeDir;
		this.workBook = createWorkBook();
		this.workSheet = createWorkSheetAndAddHeader();
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
	
	/**
	 * 创建存储的文件夹
	 * @return 文件夹的路径
	 */
	private String getStorePath() {
		return FileUtil.createDir(STORE_PATH);
	}

	/**
	 * 保存到Excel
	 * @param courses 要保存的课程列表
	 * @return 保存结果 true 成功, false 失败
	 */
	public boolean writeToExcel(List<ImoocCourse> courses) {
		return addContentToWorkSheet(courses);
	}
	
	/**
	 * 创建Excel工作簿
	 * @return
	 */
	private HSSFWorkbook createWorkBook() {
		return new HSSFWorkbook();
	}
	
	/**
	 * 创建Excel工作表并添加表头
	 * @return
	 */
	private HSSFSheet createWorkSheetAndAddHeader() {
		workSheet = workBook.createSheet(); //创建工作表
		HSSFRow headerRow = workSheet.createRow(0); //第一列，表头
		HSSFCell cell = headerRow.createCell(0); //创表头建单元格，并填充内容
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
		cell = headerRow.createCell(6);
		cell.setCellValue("课程连接");
		return workSheet;
	}
	
	/**
	 * 向Excel工作表中添加数据
	 * @param courses
	 * @return
	 */
	private boolean addContentToWorkSheet(List<ImoocCourse> courses) {
		int rowsInserted = 0;
		int courseLength = courses.size();
		HSSFRow contentRow; //内容行
		ImoocCourse course;
		//循环添加行
		for(; rowsInserted < courseLength; rowsInserted++) {
			contentRow = workSheet.createRow(rowsInserted + 1);
			course = courses.get(rowsInserted);
			contentRow.createCell(0).setCellValue(course.getCourseName());
			contentRow.createCell(1).setCellValue(course.getImgSrc());
			contentRow.createCell(2).setCellValue(course.getCourseLevel());
			contentRow.createCell(3).setCellValue(course.getCourseLabels().toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			contentRow.createCell(4).setCellValue(course.getCourseDesc());
			contentRow.createCell(5).setCellValue(course.getStudyNum());
			contentRow.createCell(6).setCellValue(course.getCourseURL());
		}
		String fileName = "courses.xls"; //Excel文件名
		String storePath = getStorePath().concat(fileName); //Excel文件存储路径
		try {
			@Cleanup FileOutputStream fos = new FileOutputStream(storePath);
			workBook.write(fos); //将数据写入Excel文件
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
		return rowsInserted == courseLength;
	}
	
}
