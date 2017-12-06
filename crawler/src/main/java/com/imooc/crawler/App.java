package com.imooc.crawler;

import java.io.IOException;
/**
 * 
 * @author yanrun
 *
 */
public class App {
	/**
	 *　　　　　　　　┏┓　　　┏┓+ +
	 *　　　　　　　┏┛┻━━━┛┻┓ + +
	 *　　　　　　　┃　　　　　　　┃ 　
	 *　　　　　　　┃　　　━　　　┃ ++ + + +
	 *　　　　       	██ ━██  ┃+
	 *　　　　　　　┃　　　　　　　┃ +
	 *　　　　　　　┃　　　┻　　　┃
	 *　　　　　　　┃　　　　　　　┃ + +
	 *　　　　　　　┗━┓　　　┏━┛
	 *　　　　　　　　　┃　　　┃　　　　　　　　　　　
	 *　　　　　　　　　┃　　　┃ + + + +
	 *　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting　　　　　　　
	 *　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug　　
	 *　　　　　　　　　┃　　　┃
	 *　　　　　　　　　┃　　　┃　　+　　　　　　　　　
	 *　　　　　　　　　┃　 　　┗━━━┓ + +
	 *　　　　　　　　　┃ 　　　　　　　┣┓
	 *　　　　　　　　　┃ 　　　　　　　┏┛
	 *　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
	 *　　　　　　　　　　┃┫┫　┃┫┫
	 *　　　　　　　　　　┗┻┛　┗┻┛+ + + +
	 */
	public static void main( String[] args ) throws IOException {
    	Crawler.Builder crawlerBuilder = new Crawler.Builder();
    	Crawler crawler = crawlerBuilder
    			.print(false)
    			.needToDownloadImg(true)
    			.imgPath("D://photos//.imooc")
    			.needToStoreDataToExcel(true)
    			.downloadImageThreadNum(5)
    			.excelStorePath("//D:\\111")
    			.excelFileName("课程")
    			.build();
    	crawler.crawImoocCourses("http://www.imooc.com/course/list?c=java&page=1");
    }
}
