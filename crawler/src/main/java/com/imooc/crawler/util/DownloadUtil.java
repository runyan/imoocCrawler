package com.imooc.crawler.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
	
	private String createImgStorageDir() {
		return FileUtil.createDir(INSERTED_IMG_STORE_PATH);
	}
	
	public void downloadCourseImg(String courseName, String imgUrl) {
		try {
			String storeDirPath = createImgStorageDir();
			String suffix = ".jpg";
			courseName = courseName.replaceAll("\\\\", "").replaceAll(":", "").replaceAll("/", "")
					.replaceAll("\\|", "").replaceAll("//*", "").replaceAll("//?", "")
					.replaceAll("\"", "").replaceAll("<", "").replaceAll(">", "");
			String imgPath = storeDirPath + courseName + suffix;
			URL downloadUrl = new URL(imgUrl);
			HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
            connection.setRequestProperty("User-Agent", Constraints.USER_AGENT);
            connection.setConnectTimeout(Constraints.TIME_OUT);
            InputStream inputStream = connection.getInputStream();
			byte[] tmp = new byte[1024];
			int length;
			OutputStream outputStream = new FileOutputStream(imgPath);
            while((length = inputStream.read(tmp)) != -1) {
            	outputStream.write(tmp, 0, length);
            }
            outputStream.close();
            inputStream.close();
			System.out.println("已下载:" + courseName + suffix);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
