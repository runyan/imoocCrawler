package com.imooc.crawler;

import java.io.IOException;

public class App {
    public static void main( String[] args ) throws IOException {
    	Crawler.Builder crawlerBuilder = new Crawler.Builder();
    	Crawler crawler = crawlerBuilder
    			.print(false)
    			.needToDownloadImg(true)
    			.imgPath("D://photos//imooc")
    			.needToStoreDataToExcel(true)
    			.build();
    	crawler.crawImoocCourses("http://www.imooc.com/course/list?c=java&page=1");
    }
}
