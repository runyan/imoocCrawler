package com.imooc.crawler.entity;

import java.util.List;

/**
 * 慕课网课程实体类
 * @author yanrun
 *
 */
public class ImoocCourse {

	private String imgSrc; //课程图片地址
	private String courseURL; //课程连接
	private String courseName; //课程名称
	private String courseLevel; //课程等级
	private List<String> courseLabels; //课程标签
	private String courseDesc; //课程简介
	private String studyNum; //学习人数
	
	public ImoocCourse(String imgSrc, String courseName, String courseLevel,
			List<String> courseLabels, String courseDesc, String studyNum, String courseURL) {
		super();
		this.imgSrc = imgSrc;
		this.courseName = courseName;
		this.courseLevel = courseLevel;
		this.courseLabels = courseLabels;
		this.courseDesc = courseDesc;
		this.studyNum = studyNum;
		this.courseURL = courseURL;
	}
	
	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseLevel() {
		return courseLevel;
	}

	public void setCourseLevel(String courseLevel) {
		this.courseLevel = courseLevel;
	}

	public List<String> getCourseLabels() {
		return courseLabels;
	}

	public void setCourseLabels(List<String> courseLabels) {
		this.courseLabels = courseLabels;
	}

	public String getCourseDesc() {
		return courseDesc;
	}

	public void setCourseDesc(String courseDesc) {
		this.courseDesc = courseDesc;
	}

	public String getStudyNum() {
		return studyNum;
	}

	public void setStudyNum(String studyNum) {
		this.studyNum = studyNum;
	}

	public String getCourseURL() {
		return courseURL;
	}

	public void setCourseURL(String courseURL) {
		this.courseURL = courseURL;
	}

	@Override
	public String toString() {
		return "ImoocCourse [imgSrc=" + imgSrc + ", courseURL=" + courseURL
				+ ", courseName=" + courseName + ", courseLevel=" + courseLevel
				+ ", courseLabels=" + courseLabels + ", courseDesc="
				+ courseDesc + ", studyNum=" + studyNum + "]";
	}

}
