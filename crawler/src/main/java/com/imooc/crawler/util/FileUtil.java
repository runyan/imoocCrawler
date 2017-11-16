package com.imooc.crawler.util;

import java.io.File;
import java.util.Arrays;

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
			rootPath = "D:".concat(pathSeparator);
		} else if(OSUtil.isLinux() || OSUtil.isMacOS()) {
			rootPath = pathSeparator.concat("usr").concat(pathSeparator).concat("home").concat(pathSeparator);
		} else {
			throw new RuntimeException("暂不支持的操作系统");
		}
		String storeDirPath = "imoocCrawler".concat(pathSeparator);
		return rootPath.concat(storeDirPath);
	}
	
	/**
	 * 移除文件名中的非法字符
	 * @param fileName 文件名
	 * @return 处理后的文件名
	 */
	public static String removeIlleagalCharactersInFileName(String fileName) {
		return fileName.replaceAll("\\\\", "").replaceAll(":", "").replaceAll("/", "")
				.replaceAll("\\|", "").replaceAll("//*", "").replaceAll("//?", "")
				.replaceAll("\"", "").replaceAll("<", "").replaceAll(">", "");
	}
	
	/**
	 * 获取文件扩展名
	 * @param fileName 文件名
	 * @return 文件的扩展名，带.
	 */
	public static String getFileExtName(String fileName) {
		int lastDotIndex = fileName.lastIndexOf(".");
		return (lastDotIndex >= 0) ? fileName.substring(lastDotIndex) : "";
	}
	
	/**
	 * 判断是否为合法的图片扩展名
	 * @param ext 要判断的扩展名
	 * @return 判断结果
	 */
	public static boolean isLegalImageExt(String ext) {
		String[] legalExtArr = {".bmp", ".jpg", ".png", ".tiff", ".gif", ".exif", ".webp"};
		return Arrays.asList(legalExtArr).contains(ext);
	}
}
