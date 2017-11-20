package com.imooc.crawler.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
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
		if(!checkHost(url)) {
			throw new RuntimeException("目前只可以爬取慕课网的课程信息");
		}
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
	 * 检查传入的URL是否为慕课网的URL
	 * @param url 要检测的URL
	 * @return URL是否为慕课网的URL
	 */
	private static boolean checkHost(String url) {
		String host;
		if(!StringUtils.startsWithIgnoreCase(url, "http://") && !StringUtils.startsWithIgnoreCase(url, "https://")) {
			url = "http://".concat(url);
		}
		try {
			URL uri = new URL(url);
			host = uri.getHost();
		} catch(Exception e) {
			System.err.append("非法的URL").println();
			return false;
		}
		return host.contains("imooc.com");
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
	 * @return 解析获得的数据
	 */
	public Map<String, Object> parse() {
		Map<String, Object> resultMap = new ConcurrentHashMap<>();
		Map<String, String> imgUrlMap = new ConcurrentHashMap<>();
		List<ImoocCourse> courseList = new LinkedList<>();
		courseList = Collections.synchronizedList(courseList);
		int pageIndex = getLastEqualsIndex(TARGET_URL);
		String fixedCourseUrl = TARGET_URL.substring(0, pageIndex); //获取不变的URL
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
				c = new ImoocCourse(imgSrc, courseURL, courseName, courseLevel, courseLabels, courseDesc, studyNum); 
				courseList.add(c);
				imgUrlMap.put(courseName, imgSrc);
			}
			urlBuilder.delete(pageIndex, urlBuilder.length());
		}
		resultMap.put("data", courseList);
		resultMap.put("imgUrlMap", imgUrlMap);
		System.out.println("获取数据完成");
		return resultMap;
	}
	
	/**
	 * 获取最后一个等号的位置
	 * @param str 源字符串
	 * @return 最后一个等号的位置
	 */
	private int getLastEqualsIndex(String str) {
		return str.lastIndexOf("=") + 1;
	}
	
	/**
	 * 解析HTML获取页数
	 * @return 页数
	 */
	private int getTotalPageNum() {
		try {
			Element lastPageElement = doc.select(".page a").last();
			lastPageElement.getElementsByClass("active text-page-tag");
			String lastPageHref = (lastPageElement.getElementsByClass("active text-page-tag").size() == 0) 
					? doc.select(".page a").last().attr("href") : lastPageElement.text();
			return Integer.parseInt(lastPageHref.substring(getLastEqualsIndex(lastPageHref)));
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
