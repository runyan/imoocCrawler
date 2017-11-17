package com.imooc.crawler.util;

/**
 * 操作系统工具类
 * @author yanrun
 *
 */
public class OSUtil {
	
	private static final String OS = System.getProperty("os.name").toLowerCase(); //操作系统信息
	
	/**
	 * 判断是否为Windows
	 * @return 是否为Windows
	 */
	public static boolean isWindows() {
		return OS.indexOf("windows") >= 0;
	}
	
	/**
	 * 判断是否为Linux
	 * @return 是否为Linux
	 */
	public static boolean isLinux() {
		return OS.indexOf("linux") >= 0;
	}
	
	/**
	 * 判断是否为Mac OS
	 * @return 是否为Max OS
	 */
	public static boolean isMacOS() {
		return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0;
	}

}
