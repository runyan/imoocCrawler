package com.imooc.crawler.util;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/**
 * 文件工具类
 * 
 * @author yanrun
 *
 */
public class FileUtil {

	private static final String SEPARATOR = File.separator;
	private static final String USER_DIR = System.getProperty("user.dir");

	/**
	 * 创建文件夹
	 * 
	 * @param insertedPath
	 *            用户输入的路径
	 * @return 路径
	 */
	public static String createDir(String insertedPath) {
		String path = (StringUtils.isEmpty(insertedPath)) ? generateDefaultDirPath()
				: parseInsertedPath(insertedPath); // 获取文件夹路径
		File dir = new File(path);
		if (!dir.exists()) {
			boolean createDirSuccessfully = dir.mkdirs(); // 获取创建结果
			if (!createDirSuccessfully) {
				throw new RuntimeException("创建文件夹" + path + "失败");
			}
		}
		return path;
	}

	/**
	 * 根据操作系统生成存储路径
	 * 
	 * @return 自动生成的路径
	 */
	private static String generateDefaultDirPath() {
		return USER_DIR.concat(SEPARATOR);
	}

	/**
	 * 对输入的文件路径进行处理
	 * 
	 * @param insertedPath
	 *            输入的文件路径
	 * @return 处理后的文件路径 
	 * 例：
	 * 	输入D:/123,输出D:/123/
	 *  输入123/folder, 输出123/folder/
	 */
	private static String parseInsertedPath(String insertedPath) {
		insertedPath = StringEscapeUtils.escapeJava(insertedPath); // 对转义字符进行反转义处理
		insertedPath = unifyPathSeparator(insertedPath); // 统一路径分隔符
		if(OSUtil.isWindows()) {
			//处理Windows系统下用户输入 盘符:\路径的情况
			if(pathStartsWithWindowsDisc(insertedPath)) {
				return handlePathContainsWindowsDisc(insertedPath);
			}
			//处理Windows系统下以分隔符开始的情况
			if(isStartsWithSeparator(insertedPath)) {
				//处理路径为/盘符:/路径的情况
				String subPath = StringUtils.substring(insertedPath, 1);
				if(pathStartsWithWindowsDisc(subPath)) {
					return handlePathContainsWindowsDisc(subPath);
				}
				return generateDefaultDirPath().concat(removeIllegalCharactersInFilePath(insertedPath));
			}
		}
		return removeIllegalCharactersInFilePath(insertedPath);
	}
	
	/**
	 * 处理Windows系统下用户输入 盘符:/路径的情况
	 * @param path
	 * @return
	 */
	private static String handlePathContainsWindowsDisc(String path) {
		String disc = StringUtils.substring(path, 0, 2);
		String remain = removeIllegalCharactersInFilePath(StringUtils.substring(path, 2));
		return disc.concat(SEPARATOR).concat(remain);
	}
	
	/**
	 * 判断路径是否为盘符:/路径的情况
	 * @param path
	 * @return
	 */
	private static boolean pathStartsWithWindowsDisc(String path) {
		return StringUtils.contains(path, ":") && ':' == path.charAt(1);
	}
	
	/**
	 * 判断文件名是否以分隔符开始
	 * @param fileName
	 * @return
	 */
	private static boolean isStartsWithSeparator(String fileName) {
		return StringUtils.startsWith(fileName, "\\\\") || StringUtils.startsWith(fileName, "\\") 
				|| StringUtils.startsWith(fileName, "/") || StringUtils.startsWith(fileName, "//");
	}
	

	/**
	 * 移除文件名中的非法字符
	 * 
	 * @param fileName
	 *            文件名
	 * @return 处理后的文件名
	 */
	public static String removeIlleagalCharactersInFileName(String fileName) {
		String parsedFileName = StringUtils.replaceAll(StringUtils.lowerCase(fileName).trim(), "[\\\\:/\\|//*//?\\<>\"@#$￥&-()]", "");
		parsedFileName = removeFirstDotsInFileName(parsedFileName); //去除文件名开始的.
		parsedFileName = StringUtils.replaceAll(parsedFileName, "\\s*", ""); //去除文件名中的空格
		return (OSUtil.isWindows()) ? removeWindowsReserveWordsInFileName(parsedFileName) : parsedFileName;
	}
	
	/**
	 * 移除文件名开始的.
	 * @param fileName
	 * @return
	 */
	private static String removeFirstDotsInFileName(String fileName) {
		while(StringUtils.startsWith(fileName, ".")) {
			fileName = StringUtils.replaceFirst(fileName, ".", "");
		}
		return fileName;
	}
	
	/**
	 * 将输入的路径的分隔符替换成系统分隔符
	 * @param path 文件路径
	 * @return 替换后的文件路径
	 */
	private static String unifyPathSeparator(String path) {
		String separator = unescapeSeparator(); //对分隔符进行转义
		return path.replaceAll("\\\\", separator).replaceAll("\\\\\\\\", separator).replaceAll("//", separator)
				.replaceAll("/", separator).replaceAll("////", separator);
	}
	
	/**
	 * 移除文件路径中的非法字符
	 * @param path 文件路径
	 * @return 处理后的文件路径
	 */
	private static String removeIllegalCharactersInFilePath(String path) {
		String[] pathParts = path.split(unescapeSeparator()); //以分隔符拆分路径
		StringBuilder pathBuilder = new StringBuilder();
		for(String pathPart : pathParts) {
			pathBuilder.append(removeIlleagalCharactersInFileName(pathPart)).append(SEPARATOR); //移除文件夹名称中的非法字符
		}
		return pathBuilder.toString();
	}
	
	/**
	 * 对文件分隔符进行转义
	 * @return 转义后的分隔符
	 */
	private static String unescapeSeparator() {
		return SEPARATOR.concat(SEPARATOR);
	}
	
	/**
	 * 移除文件名中的Windows系统保留字
	 * @param fileName 文件名
	 * @return 移除保留字后的文件名
	 */
	private static String removeWindowsReserveWordsInFileName(String fileName) {
		return fileName.replaceAll("aux", "").replaceAll("con", "").replaceAll("prn", "")
				.replaceAll("clock$", "").replaceAll("nul", "")
				.replaceAll("com1", "").replaceAll("com2", "")
				.replaceAll("com3", "").replaceAll("com4", "")
				.replaceAll("com5", "").replaceAll("com6", "")
				.replaceAll("com7", "").replaceAll("com8", "")
				.replaceAll("com9", "").replaceAll("lpt1", "");
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件的扩展名，带.
	 */
	public static String getFileExt(String fileName) {
		fileName = StringUtils.lowerCase(fileName);
		int lastDotIndex = StringUtils.lastIndexOf(fileName, ".");
		return (lastDotIndex >= 0) ? StringUtils.substring(fileName,
				lastDotIndex) : "";
	}

	/**
	 * 判断是否为合法的图片扩展名
	 * 
	 * @param ext
	 *            要判断的扩展名
	 * @return 判断结果
	 */
	public static boolean isLegalImageExt(String ext) {
		String[] legalExtArr = { ".bmp", ".jpg", ".png", ".tiff", ".gif",
				".exif", ".webp" };
		return Arrays.asList(legalExtArr).contains(StringUtils.lowerCase(ext));
	}

	/**
	 * 对传入的Excel文件名进行处理，将不是.xls扩展名的文件扩展名修改为.xls
	 * 
	 * @param excelFileName
	 *            传入的Excel文件名
	 * @return 处理后的Excel文件名
	 */
	public static String parseExcelExt(String excelFileName) {
		String fileExt = getFileExt(excelFileName);
		String xlsx = ".xlsx";
		String xls = ".xls";
		if (StringUtils.isEmpty(fileExt)) {
			return StringUtils.appendIfMissing(excelFileName, xls);
		} else if (StringUtils.equalsIgnoreCase(fileExt, xlsx)) {
			return StringUtils.replace(excelFileName, xlsx, xls);
		} else if (StringUtils.equalsIgnoreCase(fileExt, xls)) {
			return excelFileName;
		}
		return StringUtils.replace(excelFileName, fileExt, xls);
	}
}
