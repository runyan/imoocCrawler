package com.imooc.crawler.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
/**
 * 下载工具类
 * @author yanrun
 *
 */
@Slf4j
public class DownloadUtil {
	
	private final String INSERTED_IMG_STORE_PATH;
	private final int DEFAULT_DOWNLOAD_THREAD_COUNT = 3; //默认下载线程数
	private final int MAX_DOWNLOAD_THREAD_COUNT = 5; //最大下载线程数
	private int downloadImageThreadNum;
	
	private DownloadUtil(String storeDir, int downloadImageThreadNum) {
		this.INSERTED_IMG_STORE_PATH = storeDir;
		this.downloadImageThreadNum = (downloadImageThreadNum <= 0) ? DEFAULT_DOWNLOAD_THREAD_COUNT : 
			(downloadImageThreadNum >= MAX_DOWNLOAD_THREAD_COUNT) ? MAX_DOWNLOAD_THREAD_COUNT : downloadImageThreadNum;
	}
	
	public static DownloadUtil getInstance(String storeDir, int downloadImageThreadNum) {
		return new DownloadUtil(storeDir, downloadImageThreadNum);
	}
	
	/**
	 * 创建文件夹
	 * @return 文件夹的路径
	 */
	private String createImgStorageDir() {
		return FileUtil.createDir(INSERTED_IMG_STORE_PATH);
	}
	/**
	 * 根据URL创建HttpURLConnection对象
	 * @param url
	 * @return HttpURLConnection对象
	 */
	private HttpURLConnection getConnectionByUrl(String url) {
		HttpURLConnection conn;
		try {
			URL targetUrl = new URL(url);
			conn = (HttpURLConnection) targetUrl.openConnection();
	        conn.setConnectTimeout(Constraints.TIME_OUT);
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty(
	                "Accept",
	                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
	                        + "application/x-shockwave-flash, application/xaml+xml, "
	                        + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
	                        + "application/x-ms-application, application/vnd.ms-excel, "
	                        + "application/vnd.ms-powerpoint, application/msword, */*");
	        conn.setRequestProperty("Charset", "UTF-8");
	        conn.setRequestProperty("Connection", "Keep-Alive");
	        conn.setRequestProperty("User-Agent", Constraints.USER_AGENT);
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("连接到" + url + "失败");
		}
		return conn;
	}
	
	/**
	 * 下载课程图片
	 * @param courseName 课程名称
	 * @param imgUrl 图片地址
	 */
	public void downloadCourseImg(String courseName, String imgUrl) {
		String storeDirPath = createImgStorageDir(); //课程图片存储路径
		String suffix = FileUtil.getFileExt(imgUrl); //图片扩展名
		if(StringUtils.isEmpty(suffix) || !FileUtil.isLegalImageExt(suffix)) {
			suffix = ".jpg";
		}
		courseName = FileUtil.removeIlleagalCharactersInFileName(courseName); //去除课程名中的非法字符
		if(StringUtils.isEmpty(courseName)) {
			courseName = "课程".concat(String.valueOf(new Random(100).nextInt()));
		}
		String imageFileName = courseName.concat(suffix);
		doDownload(imgUrl, imageFileName, storeDirPath);
	}
	
	/**
	 * 执行下载
	 * @param imgUrl 下载路径
	 * @param imageFileName 文件名
	 * @param storePath 存储路径
	 */
	private void doDownload(String imgUrl, String imageFileName, String storePath) {
		File targetFile = new File(storePath, imageFileName);
		ExecutorService threadPool = Executors.newFixedThreadPool(downloadImageThreadNum);
		try {
			HttpURLConnection conn = getConnectionByUrl(imgUrl); 
	        int fileSize = conn.getContentLength(); //得到文件大小
	        conn.disconnect();
	        int currentPartSize = fileSize / downloadImageThreadNum;
	        int startPos;
	        RandomAccessFile currentPart;
	        CountDownLatch countDownLatch = new CountDownLatch(downloadImageThreadNum);
	        DownloadTask downloadTask;
	        for(int i = 0; i < downloadImageThreadNum; i++) {
	        	startPos = i * currentPartSize;
	        	currentPart = new RandomAccessFile(targetFile, "rw");
	        	currentPart.seek(startPos);
	        	downloadTask = new DownloadTask(startPos, currentPartSize, currentPart, imgUrl, countDownLatch);
	        	threadPool.submit(downloadTask);
	        }
	        countDownLatch.await();
		} catch(Exception e) {
			e.printStackTrace();
			log.error(imageFileName + "下载失败");
		} finally {
			threadPool.shutdown();
		}
		log.info("已下载:" + imageFileName);
	}
	
	/**
	 * 下载线程类
	 * @author yanrun
	 *
	 */
	private class DownloadTask implements java.util.concurrent.Callable<Void> {
		
		private int startPos; //当前线程的下载位置
		private int currentPartSize; //当前线程负责下载的文件大小
		private String downloadUrl; //要下载的文件的URL
		private RandomAccessFile currentPart; //当前线程需要下载的文件块
		private int length; //已经该线程已下载的字节数
		private CountDownLatch countDownLatch;
		
		public DownloadTask(int startPos, int currentPartSize,
				RandomAccessFile currentPart, String downloadUrl, CountDownLatch countDownLatch) {
			super();
			this.startPos = startPos;
			this.currentPartSize = currentPartSize;
			this.currentPart = currentPart;
			this.downloadUrl = downloadUrl;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public Void call() throws Exception {
			HttpURLConnection conn = null;
			try {
				conn = getConnectionByUrl(downloadUrl);
				@Cleanup InputStream inputStream = conn.getInputStream();
		        inputStream.skip(startPos); //跳过startPos个字节，表明该线程只下载自己负责哪部分文件
		        byte[] buffer = new byte[1024];
		        int hasRead = 0;
		        //读取网络数据，并写入本地文件
		        while (length < currentPartSize
                        && (hasRead = inputStream.read(buffer)) != -1) {
		        	currentPart.write(buffer, 0, hasRead);
		        	length += hasRead; //累计该线程下载的总大小
		        }
			} catch (IOException e) {
				e.printStackTrace();
				log.error("流处理错误");
			} catch(Exception e) {
				e.printStackTrace();
				log.error("下载时出错");
			} finally {
				try {
					currentPart.close();
					countDownLatch.countDown();
				} catch (IOException e) {
					e.printStackTrace();
					log.error("关闭临时文件时出错");
				}
			}
			return null;
		}
		
	}
}
