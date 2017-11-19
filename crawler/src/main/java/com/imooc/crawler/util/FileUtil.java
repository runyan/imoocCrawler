package com.imooc.crawler.util;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.text.StringEscapeUtils;


/**
 * 文件工具类
 * @author yanrun
 *
 */
public class FileUtil {

	private static final String SEPARATOR = File.separator;

	/**
	 * 创建文件夹
	 * @param insertedPath 用户输入的路径
	 * @return 路径
	 */
	public static String createDir(String insertedPath) {
		String path = (null == insertedPath || insertedPath.isEmpty()) ? generateDefaultDirPath(insertedPath)
                : parseInsertedPath(insertedPath); //获取文件夹路径
		File dir = new File(path);
		if(!dir.exists()) {
			boolean createDirSuccessfully = dir.mkdirs(); //获取创建结果
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
	private static String generateDefaultDirPath(String insertedPath) {
		String rootPath;
		if(OSUtil.isWindows()) {
			rootPath = "D:".concat(SEPARATOR);
		} else if(OSUtil.isLinux() || OSUtil.isMacOS()) {
			rootPath = SEPARATOR.concat("usr").concat(SEPARATOR)
					.concat("home").concat(SEPARATOR);
		} else {
			throw new RuntimeException("暂不支持的操作系统");
		}
		String storeDirPath = "imoocCrawler".concat(SEPARATOR);
		return rootPath.concat(storeDirPath);
	}
	
	/**
	 * 对输入的文件路径进行处理
	 * @param insertedPath 输入的文件路径
	 * @return 处理后的文件路径 例：输入D:/123,输出d:/123/
	 */
	private static String parseInsertedPath(String insertedPath) {
		insertedPath = StringEscapeUtils.escapeJava(insertedPath); //对转义字符进行反转义处理
		//如果以/,//,\,\\结尾则不做处理
		if(insertedPath.endsWith("/") ||insertedPath.endsWith("\\\\")
				|| insertedPath.endsWith("\\")
				|| insertedPath.endsWith("//")) {
			return insertedPath;
		}
		if(insertedPath.contains("//")) {
			return insertedPath.concat("//");
		}
		if(insertedPath.contains("/")) {
			return insertedPath.concat("/");
		}
		if(insertedPath.contains("\\\\")) {
			return insertedPath.concat("\\\\");
		}
		if(insertedPath.contains("\\")) {
			return insertedPath.concat("\\");
		}
		throw new RuntimeException("非法的路径格式");
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
