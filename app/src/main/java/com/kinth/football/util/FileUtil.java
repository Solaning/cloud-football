package com.kinth.football.util;

import java.io.File;
import java.io.IOException;

/**
 * 文件工具类
 * @author Sola
 *
 */
public class FileUtil {
	/**
	 * 创建文件
	 * 
	 * @param file
	 * @param isFile
	 */
	public static void createFile(File file, boolean isFile) {
		if (!file.exists()) {// 如果文件不存在
			if (!file.getParentFile().exists()) {// 如果文件父目录不存在
				createFile(file.getParentFile(), false);
			} else {// 存在文件父目录
				if (isFile) {// 创建文件
					try {
						file.createNewFile();// 创建新文件
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					file.mkdir();// 创建目录
				}
			}
		}
	}
	
	/**
	 * 删除文件与文件夹
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			// file.delete();
		}
	}

	/**
	 * 删除文件与文件夹，包含自身
	 * 
	 * @param file
	 */
	public static void deleteAll(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}
}
