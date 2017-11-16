package com.imooc.crawler.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * 下载工具类
 * @author yanrun
 *
 */
public class DownloadUtil {
	
	private final String INSERTED_IMG_STORE_PATH;
	private static volatile DownloadUtil instance;
	
	private DownloadUtil(String storeDir) {
		this.INSERTED_IMG_STORE_PATH = storeDir;
	}
	
	public static DownloadUtil getInstance(String storeDir) {
		if(null == instance) {
			synchronized (DownloadUtil.class) {
				if(null == instance) {
					instance = new DownloadUtil(storeDir);
				}
			}
		}
		return instance;
	}
	
	/**
	 * 创建文件夹
	 * @return 文件夹的路径
	 */
	private String createImgStorageDir() {
		return FileUtil.createDir(INSERTED_IMG_STORE_PATH);
	}
	
	/**
	 * 下载课程图片
	 * @param courseName 课程名称
	 * @param imgUrl 图片地址
	 */
	public void downloadCourseImg(String courseName, String imgUrl) {
		try {
			String storeDirPath = createImgStorageDir();
			String suffix = ".jpg"; //图片扩展名
			courseName = courseName.replaceAll("\\\\", "").replaceAll(":", "").replaceAll("/", "")
					.replaceAll("\\|", "").replaceAll("//*", "").replaceAll("//?", "")
					.replaceAll("\"", "").replaceAll("<", "").replaceAll(">", ""); //去除课程名中的特殊字符
			String imgPath = storeDirPath + courseName + suffix; //图片存储路径
			URL downloadUrl = new URL(imgUrl);
			HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection(); //打开连接
            connection.setRequestProperty("User-Agent", Constraints.USER_AGENT); 
            connection.setConnectTimeout(Constraints.TIME_OUT);
            InputStream inputStream = connection.getInputStream();
			byte[] tmp = new byte[1024];
			int length;
			OutputStream outputStream = new FileOutputStream(imgPath);
            while((length = inputStream.read(tmp)) != -1) {
            	outputStream.write(tmp, 0, length); //写文件
            }
            outputStream.close();
            inputStream.close();
			System.out.println("已下载:" + courseName + suffix);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
