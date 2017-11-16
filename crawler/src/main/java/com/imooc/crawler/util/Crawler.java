package com.imooc.crawler.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.imooc.crawler.entity.ImoocCourse;

/**
 * 爬虫类
 * @author yanrun
 *
 */
public class Crawler {
	
	private boolean needToDownloadImg;
	private boolean needToStoreDataToExcel;
	private boolean print;
	private String imgPath;
	private String excelStorePath;
	
	private Crawler(Builder builder) {
		this.needToDownloadImg = builder.needToDownloadImg;
		this.needToStoreDataToExcel = builder.needToStoreDataToExcel;
		this.print = builder.print;
		this.imgPath = builder.imgPath;
		this.excelStorePath = builder.excelStorePath;
	}
	/**
	 * 爬取url的信息
	 * @param targetUrl
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void crawImoocCourses(String targetUrl) throws IOException {
		Map<String, Object> resultMap = HtmlParser.getInstance(targetUrl).parse(); //获取解析结果
		if(null == resultMap || resultMap.isEmpty()) {
			System.out.println("没有获取到数据");
			return ;
		}
		Map<String, String> courseImgUrlMap = (Map<String, String>) resultMap.get("imgUrlMap");
		List<ImoocCourse> courseList = (List<ImoocCourse>) resultMap.get("data");
		printCourseInfo(courseList);
		//如果需要下载图片则启动新线程进行下载
		downloadImgs(courseImgUrlMap);
		//如果需要保存数据，则启动新线程进行保存
		saveDataToExcel(courseList);
	}
	
	/**
	 * 打印课程信息
	 * @param print 是否需要打印
	 * @param courseList 课程列表
	 */
	private void printCourseInfo(List<ImoocCourse> courseList) {
		if(print) {
			for(ImoocCourse course : courseList) {
				System.out.println(course);
			}
		}
	}
	
	/**
	 * 下载图片
	 * @param needToDownloadImg 是否需要下载图片
	 * @param imgUrlMap 图片url集合
	 */
	private void downloadImgs(Map<String, String> imgUrlMap) {
		if(needToDownloadImg) {
			if(null == imgPath) {
				imgPath = "";
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					if(null == imgUrlMap || imgUrlMap.isEmpty()) {
						System.out.println("没有可以下载的数据");
						return ;
					}
					System.out.println("开始下载");
					DownloadUtil downloadUtil = DownloadUtil.getInstance(imgPath);
					Set<Map.Entry<String, String>> imgUrlEntrySet = imgUrlMap.entrySet();
					for(Map.Entry<String, String> imgUrlEntry : imgUrlEntrySet) {
						downloadUtil.downloadCourseImg(imgUrlEntry.getKey(), imgUrlEntry.getValue());
					}
					System.out.println("下载完成");
				}
			}).start();
		}
	}
	
	/**
	 * 将数据保存到Excel
	 * @param courseList 课程列表
	 */
	private void saveDataToExcel(List<ImoocCourse> courseList) {
		if(needToStoreDataToExcel) {
			if(null == excelStorePath) {
				excelStorePath = "";
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					if(null == courseList || courseList.isEmpty()) {
						System.out.println("没有可以保存的数据");
						return ;
					}
					System.out.println("开始保存");
					try {
						boolean saveResult = ExcelUtil.getInstance(excelStorePath).writeToExcel(courseList);
						System.out.println(saveResult ? "保存完成" : "保存失败");
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("保存失败");
					}
				}
			}).start();
		}
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
		private String excelStorePath; //Excel保存路径
		
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
		
		public Crawler build() {
			return new Crawler(this);
		}
	}

}
