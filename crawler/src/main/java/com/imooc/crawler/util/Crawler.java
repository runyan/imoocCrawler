package com.imooc.crawler.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.imooc.crawler.entity.ImoocCourse;

public class Crawler {
	
	private boolean needToDownloadImg;
	private boolean needToStoreDataToExcel;
	private String imgPath;
	private String excelStorePath;
	
	private Crawler(Builder builder) {
		this.needToDownloadImg = builder.needToDownloadImg;
		this.needToStoreDataToExcel = builder.needToStoreDataToExcel;
		this.imgPath = builder.imgPath;
		this.excelStorePath = builder.excelStorePath;
	}
	
	@SuppressWarnings("unchecked")
	public void crawImoocCourses(String targetUrl) throws IOException {
		Map<String, Object> resultMap = HtmlParser.getInstance(targetUrl).parse();
		if(null == resultMap || resultMap.isEmpty()) {
			System.out.println("没有获取到数据");
			return ;
		}
		if(needToDownloadImg) {
			if(null == imgPath) {
				imgPath = "";
			}
			new Thread(new Runnable() {
				Map<String, String> imgUrlMap = (Map<String, String>) resultMap.get("imgUrlMap");
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
		if(needToStoreDataToExcel) {
			if(null == excelStorePath) {
				excelStorePath = "";
			}
			new Thread(new Runnable() {
				List<ImoocCourse> dataList = (List<ImoocCourse>) resultMap.get("data");
				@Override
				public void run() {
					if(null == dataList || dataList.isEmpty()) {
						System.out.println("没有可以保存的数据");
						return ;
					}
					System.out.println("开始保存");
					try {
						ExcelUtil.getInstance(excelStorePath).writeToExcel(dataList);
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println("保存失败");
					}
					System.out.println("保存完成");
				}
			}).start();
		}
	}
	
	public static class Builder {
		private boolean needToDownloadImg;
		private boolean needToStoreDataToExcel;
		private String imgPath;
		private String excelStorePath;
		
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
		
		public Crawler build() {
			return new Crawler(this);
		}
	}

}
