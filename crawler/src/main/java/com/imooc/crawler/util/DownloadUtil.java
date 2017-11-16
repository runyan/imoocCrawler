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
		String storeDirPath = createImgStorageDir();
		String suffix = FileUtil.getFileExtName(imgUrl); //图片扩展名
		if(null == suffix || suffix.isEmpty() || !FileUtil.isLegalImageExt(suffix)) {
			suffix = ".jpg";
		}
		courseName = FileUtil.removeIlleagalCharactersInFileName(courseName); //去除课程名中的非法字符
		String imageFileName = courseName.concat(suffix);
		String imgPath = storeDirPath.concat(courseName).concat(suffix); //图片存储路径
		doDownload(imgUrl, imageFileName, imgPath);
	}
	
	/**
	 * 执行下载
	 * @param imgUrl 下载路径
	 * @param imageFileName 文件名
	 * @param storePath 存储路径
	 */
	private void doDownload(String imgUrl, String imageFileName, String storePath) {
		InputStream inputStream = null;
		try(OutputStream outputStream = new FileOutputStream(storePath)) {
			URL downloadUrl = new URL(imgUrl);
			HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection(); //打开连接
	        connection.setRequestProperty("User-Agent", Constraints.USER_AGENT); 
	        connection.setConnectTimeout(Constraints.TIME_OUT);
            inputStream = connection.getInputStream();
			byte[] tmp = new byte[1024];
			int length;
            while((length = inputStream.read(tmp)) != -1) {
            	outputStream.write(tmp, 0, length); //写文件
            }
		} catch(Exception e) {
			e.printStackTrace();
			System.err.append(imageFileName + "下载失败").println();
		} finally {
			try {
				if(null != inputStream) {
					inputStream.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("已下载:" + imageFileName);
	}
}
