package com.imooc.crawler.util;

import java.io.File;

public class FileUtil {
	
	public static String createDir(String insertedPath) {
		String path = (null == insertedPath || insertedPath.isEmpty()) ? generateDirPath(insertedPath) : insertedPath;
		File dir = new File(path);
		if(!dir.exists()) {
			boolean createDirSuccessfully = dir.mkdir();
			if(!createDirSuccessfully) {
				throw new RuntimeException("创建文件夹" + path + "失败");
			}
		}
		return path;
	}

	private static String generateDirPath(String insertedPath) {
		if(null != insertedPath && !insertedPath.isEmpty()) {
			return insertedPath;
		}
		String path;
		String pathSeparator = File.separator;
		if(OSUtil.isWindows()) {
			path = "D:" + pathSeparator;
		} else if(OSUtil.isLinux() || OSUtil.isMacOS()) {
			path = pathSeparator + "usr" + pathSeparator + "home" + pathSeparator;
		} else {
			throw new RuntimeException("暂不支持的操作系统");
		}
		String storeDirPath = "imoocCrawler" + pathSeparator;
		return path.concat(storeDirPath);
	}
}
