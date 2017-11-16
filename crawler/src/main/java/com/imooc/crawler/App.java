package com.imooc.crawler;

import java.io.IOException;

import com.imooc.crawler.util.Crawler;

public class App {
    public static void main( String[] args ) throws IOException {
    	Crawler.Builder crawlerBuilder = new Crawler.Builder();
    	Crawler crawler = crawlerBuilder
    			.needToDownloadImg(true)
    			.imgPath("D://photos//imooc//")
    			.needToStoreDataToExcel(true)
    			.excelStorePath("D://")
    			.build();
    	crawler.crawImoocCourses("http://www.imooc.com/course/list?c=java&page=1");
    }
}
