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
/**
 * HTML解析类
 * @author yanrun
 *
 */
public class HtmlParser {
	
	private volatile static HtmlParser instance = null;
	private final String TARGET_URL;
	private Document doc;

	private HtmlParser(String url){
		this.TARGET_URL = url;
		doc = Jsoup.parse(getHtmlString(TARGET_URL));
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
	
	/**
	 * 根据url获取HTML源码
	 * @param url 目标url
	 * @return HTML源码
	 */
	private String getHtmlString(String url) {
		return HttpUtil.getInstance().sendHttpGet(url);
	}
	
	/**
	 * 解析HTML
	 * @return
	 */
	public Map<String, Object> parse() {
		Map<String, Object> resultMap = new HashMap<>();
		Map<String, String> imgUrlMap = new HashMap<>();
		List<ImoocCourse> resultList = new LinkedList<>();
		int pageIndex = getLastEqualsIndex(TARGET_URL);
		String fixedCourseUrl = TARGET_URL.substring(0, pageIndex);
		int totalPages = getTotalPageNum();
		if(totalPages == 0) {
			return resultMap;
		}
		StringBuilder urlBuilder = new StringBuilder(fixedCourseUrl);
		Elements courseItems;
		Elements lableElements;
		Elements courseInfoElements;
		String imgSrc;
		String courseName;
		String courseURL;
		String courseLevel;
		List<String> courseLabels;
		String courseLabel;
		String courseDesc;
		String studyNum;
		String immocURL = "http://www.imooc.com";
		ImoocCourse c;
		String htmlStr;
		for(int pageNum = 1; pageNum <= totalPages; pageNum++) {
			System.out.println("正在处理第" + pageNum + "页,还有" + (totalPages - pageNum) + "页");
			urlBuilder = urlBuilder.append(pageNum);
			if(pageNum >= 2) {
				htmlStr = getHtmlString(urlBuilder.toString());
				if(htmlStr.isEmpty()) {
					continue;
				}
				doc = Jsoup.parse(htmlStr);
			}
			courseItems = doc.select(".container div .course-card-container");
			for(Element course : courseItems) {
				courseLabels = new ArrayList<>();
				imgSrc = "http:".concat(course.getElementsByTag("img").attr("src"));
				lableElements = course.select(".course-label label");
				for(Element label : lableElements) {
					courseLabel = label.text();
					courseLabels.add(courseLabel);
				}
				courseName = course.getElementsByClass("course-card-name").text();
				courseURL = immocURL.concat(course.select(".course-card").attr("href"));
				courseInfoElements = course.select(".course-card-info span");
				courseLevel = courseInfoElements.get(0).text();
				studyNum = courseInfoElements.get(1).text();
				courseDesc = course.getElementsByClass("course-card-desc").text();
				c = new ImoocCourse(imgSrc, courseName, courseLevel, courseLabels, courseDesc, studyNum, courseURL); 
				resultList.add(c);
				imgUrlMap.put(courseName, imgSrc);
			}
			urlBuilder.delete(pageIndex, urlBuilder.length());
		}
		resultMap.put("data", resultList);
		resultMap.put("imgUrlMap", imgUrlMap);
		System.out.println("获取数据完成");
		return resultMap;
	}
	
	private int getLastEqualsIndex(String str) {
		return str.lastIndexOf("=") + 1;
	}
	
	/**
	 * 解析HTML获取页数
	 * @return 页数
	 */
	private int getTotalPageNum() {
		try {
			String lastPageHref = doc.select(".page a").last().attr("href");
			return Integer.parseInt(lastPageHref.substring(getLastEqualsIndex(lastPageHref)));
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
