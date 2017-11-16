package com.imooc.crawler.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.imooc.crawler.entity.ImoocCourse;

public class HtmlParser {
	
	private volatile static HtmlParser instance = null;
	private final String BASE_URL;
	private Document doc;

	private HtmlParser(String url){
		this.BASE_URL = url;
		doc = Jsoup.parse(getHtmlString(BASE_URL));
	}
	
	public static HtmlParser getInstance(String url) {
		if(null == instance) {
			synchronized (HtmlParser.class) {
				if(null == instance) {
					instance = new HtmlParser(url);
				}
			}
		}
		return instance;
	}
	
	private String getHtmlString(String url) {
		return HttpUtil.getInstance().sendHttpGet(url);
	}
	
	public Map<String, Object> parse() {
		Map<String, Object> resultMap = new HashMap<>();
		Map<String, String> imgUrlMap = new HashMap<>();
		List<ImoocCourse> resultList = new LinkedList<>();
		String baseUrl = BASE_URL.substring(0, BASE_URL.lastIndexOf("=") + 1);
		int pageNum = 1;
		int totalPages = getTotalPageNum();
		StringBuilder urlBuilder;
		Elements courseItems;
		Elements lableElements;
		Elements courseInfoElements;
		String imgSrc;
		String courseName;
		String courseLevel;
		List<String> courseLabels;
		String courseLabel;
		String courseDesc;
		String studyNum;
		ImoocCourse c;
		while(pageNum <= totalPages) {
			System.out.println("正在处理第" + pageNum + "页,还有" + (totalPages - pageNum) + "页");
			urlBuilder = new StringBuilder(baseUrl).append(pageNum);
			if(pageNum >= 2) {
				doc = Jsoup.parse(getHtmlString(urlBuilder.toString()));
			}
			courseItems = doc.getElementsByClass("container").select("div .course-card-container");
			for(Element course : courseItems) {
				courseLabels = new ArrayList<>();
				imgSrc = "http:" + course.getElementsByTag("img").attr("src");
				lableElements = course.getElementsByClass("course-label").select("label");
				for(Element label : lableElements) {
					courseLabel = label.text();
					courseLabels.add(courseLabel);
				}
				courseName = course.getElementsByClass("course-card-name").text();
				courseInfoElements = course.getElementsByClass("course-card-info").select("span");
				courseLevel = courseInfoElements.get(0).text();
				studyNum = courseInfoElements.get(1).text();
				courseDesc = course.getElementsByClass("course-card-desc").text();
				c = new ImoocCourse(imgSrc, courseName, courseLevel, courseLabels, courseDesc, studyNum); 
				resultList.add(c);
				imgUrlMap.put(courseName, imgSrc);
			}
			pageNum++;
			urlBuilder.delete(0, urlBuilder.length());
		}
		resultMap.put("data", resultList);
		resultMap.put("imgUrlMap", imgUrlMap);
		System.out.println("获取数据完成");
		return resultMap;
	}
	
	private int getTotalPageNum() {
		try {
			String lastPageHref = doc.select(".page a").last().attr("href");
			return Integer.parseInt(lastPageHref.substring(lastPageHref.lastIndexOf("=") + 1));
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
