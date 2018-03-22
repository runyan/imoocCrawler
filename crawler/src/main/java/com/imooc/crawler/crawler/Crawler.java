package com.imooc.crawler.crawler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.imooc.crawler.entity.ImoocCourse;
import com.imooc.crawler.factory.ThreadFactory;
import com.imooc.crawler.util.DownloadUtil;
import com.imooc.crawler.util.FileUtil;
import com.imooc.crawler.util.HtmlParser;
import com.xuxueli.poi.excel.ExcelExportUtil;

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
	private String excelFileName;
	private String targetUrl;
	private ThreadPoolExecutor threadPool;
	
	private String DEFAULT_EXCEL_FILE_NAME = "courses.xls";
	
	private Crawler(Builder builder) {
		this.needToDownloadImg = builder.needToDownloadImg;
		this.needToStoreDataToExcel = builder.needToStoreDataToExcel;
		this.print = builder.print;
		this.imgPath = builder.imgPath;
		this.excelStorePath = builder.excelStorePath;
		this.excelFileName = StringUtils.isEmpty(builder.excelFileName) ? DEFAULT_EXCEL_FILE_NAME : 
			FileUtil.parseExcelExt(builder.excelFileName); 
		if(StringUtils.isEmpty(builder.targetUrl)) {
			log.error("空的Url");
			throw new RuntimeException("empty target url");
		}
		this.targetUrl = builder.targetUrl;
		threadPool = ThreadFactory.getThreadPool();
	}
	/**
	 * 爬取url的信息
	 * @param targetUrl
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void crawImoocCourses() {
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
		threadPool.shutdown();
	}
	
	/**
	 * 打印课程信息
	 * @param print 是否需要打印
	 * @param courseList 课程列表
	 */
	private void printCourseInfo(List<ImoocCourse> courseList) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				courseList.forEach((course) -> log.info(course.toString()));
			}
		});
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
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				if(Objects.isNull(imgUrlMap) || imgUrlMap.isEmpty()) {
					log.info("没有可以下载的数据");
					return ;
				}
				log.info("开始下载");
				DownloadUtil downloadUtil = DownloadUtil.getInstance(imgPath);
				imgUrlMap.forEach((courseName, imgURL) -> {downloadUtil.downloadCourseImg(courseName, imgURL);});
				log.info("下载完成");
			}
		});
	}
	
	/**
	 * 将数据保存到Excel
	 * @param courseList 课程列表
	 */
	private void saveDataToExcel(List<ImoocCourse> courseList) {
		if(null == excelStorePath) {
			excelStorePath = "";
		}
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				if(Objects.isNull(courseList) || courseList.isEmpty()) {
					log.info("没有可以保存的数据");
					return ;
				}
				log.info("开始保存");
				String excelFilePath = FileUtil.createDir(excelStorePath);
				String excelFile = excelFilePath.concat(excelFileName);
				File excel = new File(excelFile);
				if(excel.exists()) {
					boolean deleteOldExcelFileResult = excel.delete();
					if(!deleteOldExcelFileResult) {
						log.error("出现异常");
					}
				}
				ExcelExportUtil.exportToFile(courseList, excelFile);
				log.info("保存完成");
			}
		});
	}
	
	/**
	 * 爬虫类的建造者
	 * @author yanrun
	 *
	 */
	public static final class Builder {
		private boolean needToDownloadImg; //是否需要下载图片
		private boolean needToStoreDataToExcel; //是否需要将数据保存到Excel
		private boolean print = true; //是否需要打印数据
		private String imgPath; //图片保存路径
		private String excelStorePath; //Excel保存路径
		private String excelFileName; //Excel文件名
		private String targetUrl; //目标URL
		
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
		
		public Builder excelFileName(String excelFileName) {
			this.excelFileName = excelFileName;
			return this;
		}
		
		public Builder targetUrl(String targetUrl) {
			this.targetUrl = targetUrl;
			return this;
		}
		
		public Crawler build() {
			return new Crawler(this);
		}
	}

}
