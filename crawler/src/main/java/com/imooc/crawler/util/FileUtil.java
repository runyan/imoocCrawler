package com.imooc.crawler.util;

import java.io.File;

/**
 * 文件工具类
 * @author yanrun
 *
 */
public class FileUtil {
	/**
	 * 创建文件夹
	 * @param insertedPath 用户输入的路径
	 * @return 路径
	 */
	public static String createDir(String insertedPath) {
		String path = (null == insertedPath || insertedPath.isEmpty()) ? generateDirPath(insertedPath) : insertedPath; //获取文件夹路径
		File dir = new File(path);
		if(!dir.exists()) {
			boolean createDirSuccessfully = dir.mkdir(); //获取创建结果
			if(!createDirSuccessfully) {
				throw new RuntimeException("创建文件夹" + path + "失败");
			}
		}
		return path;
	}

	/**
	 * 根据操作系统获得存储路径
	 * @param insertedPath 用户输入的路径
	 * @return 路径
	 */
	private static String generateDirPath(String insertedPath) {
		if(null != insertedPath && !insertedPath.isEmpty()) {
			return insertedPath;
		}
		String rootPath;
		String pathSeparator = File.separator;
		if(OSUtil.isWindows()) {
			rootPath = "D:" + pathSeparator;
		} else if(OSUtil.isLinux() || OSUtil.isMacOS()) {
			rootPath = pathSeparator + "usr" + pathSeparator + "home" + pathSeparator;
		} else {
			throw new RuntimeException("暂不支持的操作系统");
		}
		String storeDirPath = "imoocCrawler" + pathSeparator;
		return rootPath.concat(storeDirPath);
	}
}
