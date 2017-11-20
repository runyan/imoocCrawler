package com.imooc.crawler;

import java.io.IOException;

public class App {
    public static void main( String[] args ) throws IOException {
    	Crawler.Builder crawlerBuilder = new Crawler.Builder();
    	Crawler crawler = crawlerBuilder
    			.print(false)
    			.needToDownloadImg(false)
    			.imgPath("D://photos//imooc")
    			.needToStoreDataToExcel(false)
    			.downloadImageThreadNum(5)
    			.excelFileName("courses")
    			.build();
    	crawler.crawImoocCourses("http://www.imooc.com/course/list?c=java&page=1");
    }
}
