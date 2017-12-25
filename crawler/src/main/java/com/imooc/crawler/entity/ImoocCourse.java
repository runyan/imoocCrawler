package com.imooc.crawler.entity;

import java.util.List;

import com.xuxueli.poi.excel.annotation.ExcelField;
import com.xuxueli.poi.excel.annotation.ExcelSheet;

import org.apache.poi.hssf.util.HSSFColor;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 慕课网课程实体类
 * @author yanrun
 *
 */
@Data
@AllArgsConstructor
@ExcelSheet(name = "慕课网Java课程信息", headColor = HSSFColor.HSSFColorPredefined.WHITE)
public class ImoocCourse {

	@ExcelField(name = "课程名称")
	private String courseName; //课程名称
	@ExcelField(name = "课程图片地址")
	private String imgSrc; //课程图片地址
	@ExcelField(name = "课程连接")
	private String courseURL; //课程连接
	@ExcelField(name = "课程等级")
	private String courseLevel; //课程等级
	@ExcelField(name = "课程标签")
	private List<String> courseLabels; //课程标签
	@ExcelField(name = "课程简介")
	private String courseDesc; //课程简介
	@ExcelField(name = "学习人数")
	private String studyNum; //学习人数
	
}
