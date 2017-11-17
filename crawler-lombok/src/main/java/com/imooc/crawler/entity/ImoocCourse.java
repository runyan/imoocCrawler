package com.imooc.crawler.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 慕课网课程实体类
 * @author yanrun
 *
 */
@Data
@AllArgsConstructor
public class ImoocCourse {

	private String imgSrc; //课程图片地址
	private String courseURL; //课程连接
	private String courseName; //课程名称
	private String courseLevel; //课程等级
	private List<String> courseLabels; //课程标签
	private String courseDesc; //课程简介
	private String studyNum; //学习人数
	
}
