package com.imooc.crawler.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class HtmlParser {
	
	private final String TARGET_URL;
	private final HttpUtil HTTP_UTIL;
	private Document doc;
	private ThreadLocal<Map<String, String>> imgUrlThreadLocal;
	private ThreadLocal<Map<String, Object>> parseResultThreadLocal;

	private HtmlParser(String url){
		this.TARGET_URL = url;
		this.HTTP_UTIL = HttpUtil.getInstance();
		String htmlString = getHtmlString(url);
		if(!StringUtils.isEmpty(htmlString)) {
			doc = Jsoup.parse(htmlString);
		}	
		imgUrlThreadLocal = new ThreadLocal<>();
		imgUrlThreadLocal.set(new HashMap<>());
		parseResultThreadLocal = new ThreadLocal<>();
		parseResultThreadLocal.set(new HashMap<>());
	}
	
	public static HtmlParser getInstance(String url) {
		if(!checkHost(url)) {
			throw new RuntimeException("目前只可以爬取慕课网的课程信息");
		}
		return new HtmlParser(url);
	}
	
	/**
	 * 检查传入的URL是否为慕课网的URL
	 * @param targetUrl 要检测的URL
	 * @return URL是否为慕课网的URL
	 */
	private static boolean checkHost(String targetUrl) {
		if(StringUtils.isEmpty(targetUrl)) {
			log.error("空的URL");
			return false;
		}
		targetUrl = StringUtils.lowerCase(targetUrl);
		String host;
		String path;
		if(!StringUtils.startsWithIgnoreCase(targetUrl, "http://") && !StringUtils.startsWithIgnoreCase(targetUrl, "https://")) {
			targetUrl = "http://".concat(targetUrl);
		}
		try {
			URL url = new URL(targetUrl);
			host = url.getHost();
			path = url.getPath();
		} catch(Exception e) {
			log.error("非法的URL");
			e.printStackTrace();
			return false;
		}
		return StringUtils.contains(host, "imooc.com") && StringUtils.contains(path, "course/list");
	}
	
	/**
	 * 根据url获取HTML源码
	 * @param url 目标url
	 * @return HTML源码
	 */
	private String getHtmlString(String url) {
		return HTTP_UTIL.sendHttpGet(url);
	}
	
	/**
	 * 解析HTML
	 * @return 解析获得的数据
	 */
	public Map<String, Object> parse() {
		Map<String, Object> resultMap = parseResultThreadLocal.get();
		Map<String, String> imgUrlMap = imgUrlThreadLocal.get();
		List<ImoocCourse> courseList = new LinkedList<>();
		courseList = Collections.synchronizedList(courseList);
		int pageIndex = getLastEqualsIndex(TARGET_URL);
		String fixedCourseUrl = StringUtils.substring(TARGET_URL, 0, pageIndex); //获取不变的URL
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
		ImoocCourse course;
		String htmlStr;
		for(int pageNum = 1; pageNum <= totalPages; pageNum++) {
			log.info("正在处理第" + pageNum + "页,还有" + (totalPages - pageNum) + "页");
			urlBuilder = urlBuilder.append(pageNum);
			if(pageNum >= 2) {
				htmlStr = getHtmlString(urlBuilder.toString());
				if(htmlStr.isEmpty()) {
					continue;
				}
				doc = Jsoup.parse(htmlStr);
			}
			courseItems = doc.select(".container div .course-card-container");
			for(Element courseItem : courseItems) {
				courseLabels = new ArrayList<>();
				imgSrc = "http:".concat(courseItem.getElementsByTag("img").attr("src"));
				lableElements = courseItem.select(".course-label label");
				for(Element label : lableElements) {
					courseLabel = label.text();
					courseLabels.add(courseLabel);
				}
				courseName = courseItem.getElementsByClass("course-card-name").text();
				courseURL = immocURL.concat(courseItem.select(".course-card").attr("href"));
				courseInfoElements = courseItem.select(".course-card-info span");
				courseLevel = courseInfoElements.get(0).text();
				studyNum = courseInfoElements.get(1).text();
				courseDesc = courseItem.getElementsByClass("course-card-desc").text();
				course = new ImoocCourse(courseName, imgSrc, courseURL, courseLevel, courseLabels, courseDesc, studyNum); 
				courseList.add(course);
				imgUrlMap.put(courseName, imgSrc);
			}
			urlBuilder.delete(pageIndex, urlBuilder.length());
		}
		resultMap.put("courseList", courseList);
		resultMap.put("imgUrlMap", imgUrlMap);
		log.info("获取数据完成");
		return resultMap;
	}
	
	/**
	 * 获取最后一个等号的位置
	 * @param str 源字符串
	 * @return 最后一个等号的位置
	 */
	private int getLastEqualsIndex(String str) {
		return StringUtils.lastIndexOf(str, "=") + 1;
	}
	
	/**
	 * 解析HTML获取页数
	 * @return 页数
	 */
	private int getTotalPageNum() {
		String errorMsg = "无法获取网页内容";
		try {
			if(Objects.isNull(doc)) {
				throw new RuntimeException(errorMsg);
			}
			Element lastPageElement = doc.select(".page a").last();
			String lastPageHref = (lastPageElement.getElementsByClass("active text-page-tag").isEmpty()) 
					? lastPageElement.attr("href") : lastPageElement.text();
			return Integer.parseInt(StringUtils.substring(lastPageHref, getLastEqualsIndex(lastPageHref)));
		} catch(Exception e) {
			String message = e.getMessage();
			if(StringUtils.equals(message, errorMsg)) {
				log.error(message);
			} else {
				e.printStackTrace();
			}
			return 0;
		}
	}
}
