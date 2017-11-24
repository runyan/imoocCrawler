package com.imooc.crawler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

import com.imooc.crawler.entity.ImoocCourse;
import com.imooc.crawler.util.DownloadUtil;
import com.imooc.crawler.util.ExcelUtil;
import com.imooc.crawler.util.HtmlParser;

/**
 * 爬虫类
 * @author yanrun
 *
 */
@Slf4j
public class Crawler {
	
	private boolean needToDownloadImg;
	private boolean needToStoreDataToExcel;
	private boolean print;
	private String imgPath;
	private String excelStorePath;
	private int downloadImageThreadNum;
	private String excelFileName;
	
	private Crawler(Builder builder) {
		this.needToDownloadImg = builder.needToDownloadImg;
		this.needToStoreDataToExcel = builder.needToStoreDataToExcel;
		this.print = builder.print;
		this.imgPath = builder.imgPath;
		this.excelStorePath = builder.excelStorePath;
		this.downloadImageThreadNum = builder.downloadImageThreadNum;
		this.excelFileName = builder.excelFileName;
	}
	/**
	 * 爬取url的信息
	 * @param targetUrl
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void crawImoocCourses(String targetUrl) {
		Map<String, Object> resultMap = HtmlParser.getInstance(targetUrl).parse(); //获取解析结果
		if(Objects.isNull(resultMap) || resultMap.isEmpty()) {
			log.info("没有获取到数据");
			return ;
		}
		Map<String, String> courseImgUrlMap = (Map<String, String>) resultMap.get("imgUrlMap");
		List<ImoocCourse> courseList = (List<ImoocCourse>) resultMap.get("courseList");
		//如果需要打印课程信息，则打印课程信息，默认打印
		if(print) {
			printCourseInfo(courseList);
		}
		//如果需要下载图片则启动新线程进行下载
		if(needToDownloadImg) {
			downloadImgs(courseImgUrlMap);
		}
		//如果需要保存数据，则启动新线程进行保存
		if(needToStoreDataToExcel) {
			saveDataToExcel(courseList);
		}
	}
	
	/**
	 * 打印课程信息
	 * @param print 是否需要打印
	 * @param courseList 课程列表
	 */
	private void printCourseInfo(List<ImoocCourse> courseList) {
		courseList.forEach((course) -> log.info(course.toString()));
	}
	
	/**
	 * 下载图片
	 * @param needToDownloadImg 是否需要下载图片
	 * @param imgUrlMap 图片url集合
	 */
	private void downloadImgs(Map<String, String> imgUrlMap) {
		if(null == imgPath) {
			imgPath = "";
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(Objects.isNull(imgUrlMap) || imgUrlMap.isEmpty()) {
					log.info("没有可以下载的数据");
					return ;
				}
				log.info("开始下载");
				DownloadUtil downloadUtil = DownloadUtil.getInstance(imgPath, downloadImageThreadNum);
				imgUrlMap.forEach((courseName, imgURL) -> {downloadUtil.downloadCourseImg(courseName, imgURL);});
				log.info("下载完成");
			}
		}).start();
	}
	
	/**
	 * 将数据保存到Excel
	 * @param courseList 课程列表
	 */
	private void saveDataToExcel(List<ImoocCourse> courseList) {
		if(null == excelStorePath) {
			excelStorePath = "";
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(Objects.isNull(courseList) || courseList.isEmpty()) {
					log.info("没有可以保存的数据");
					return ;
				}
				log.info("开始保存");
				boolean saveResult = ExcelUtil.getInstance(excelStorePath, excelFileName)
						.writeToExcel(courseList);
				log.info(saveResult ? "保存完成" : "保存失败");
			}
		}).start();
	}
	
	/**
	 * 爬虫类的建造者
	 * @author yanrun
	 *
	 */
	public static class Builder {
		private boolean needToDownloadImg; //是否需要下载图片
		private boolean needToStoreDataToExcel; //是否需要将数据保存到Excel
		private boolean print = true; //是否需要打印数据
		private String imgPath; //图片保存路径
		private int downloadImageThreadNum; //下载图片的线程数
		private String excelStorePath; //Excel保存路径
		private String excelFileName; //Excel文件名
		
		public Builder(){
			super();
		}
		
		public Builder needToDownloadImg(boolean needToDownloadImg) {
			this.needToDownloadImg = needToDownloadImg;
			return this;
		}
		
		public Builder needToStoreDataToExcel(boolean needToStoreDataToExcel) {
			this.needToStoreDataToExcel = needToStoreDataToExcel;
			return this;
		}
		
		public Builder imgPath(String imgPath) {
			this.imgPath = imgPath;
			return this;
		}
		
		public Builder excelStorePath(String excelStorePath) {
			this.excelStorePath = excelStorePath;
			return this;
		}
		
		public Builder print(boolean print) {
			this.print = print;
			return this;
		}
		
		public Builder downloadImageThreadNum(int downloadImageThreadNum) {
			this.downloadImageThreadNum = downloadImageThreadNum;
			return this;
		}
		
		public Builder excelFileName(String excelFileName) {
			this.excelFileName = excelFileName;
			return this;
		}
		
		public Crawler build() {
			return new Crawler(this);
		}
	}

}
