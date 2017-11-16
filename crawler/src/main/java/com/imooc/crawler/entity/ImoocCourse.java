package com.imooc.crawler.entity;

import java.util.List;

public class ImoocCourse {

	private String imgSrc;
	private String courseName;
	private String courseLevel;
	private List<String> courseLabels;
	private String courseDesc;
	private String studyNum;
	
	public ImoocCourse(String imgSrc, String courseName, String courseLevel,
			List<String> courseLabels, String courseDesc, String studyNum) {
		super();
		this.imgSrc = imgSrc;
		this.courseName = courseName;
		this.courseLevel = courseLevel;
		this.courseLabels = courseLabels;
		this.courseDesc = courseDesc;
		this.studyNum = studyNum;
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

	@Override
	public String toString() {
		return "ImoocCourse [imgSrc=" + imgSrc + ", courseName=" + courseName
				+ ", courseLevel=" + courseLevel + ", courseLabels="
				+ courseLabels + ", courseDesc=" + courseDesc + ", studyNum="
				+ studyNum + "]";
	}
	
	
}
