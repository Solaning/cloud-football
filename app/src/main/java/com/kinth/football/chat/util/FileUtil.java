package com.kinth.football.chat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
	
	public static void renameFile(String file, String toFile) {

        File toBeRenamed = new File(file);
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            System.out.println("File does not exist: " + file);
            return;
        }

        File newFile = new File(toFile);

        //修改文件名
        if (toBeRenamed.renameTo(newFile)) {
            System.out.println("File has been renamed.");
        } else {
            System.out.println("Error renmaing file");
        }
    }
	
	
	/** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public static void copyFile(String oldFile, String newPath, String newFile) { 
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           
           File newFilePath  = new File(newPath);  
           if (!newFilePath.exists()) {
				newFilePath.mkdir();
           } 
           
           File newfile = new File(newPath); 
           if (!newfile.exists())
        	   newfile.createNewFile();
        	   
           File oldfile = new File(oldFile); 
           if (oldfile.exists()) { //文件存在时 
               InputStream inStream = new FileInputStream(oldFile); //读入原文件 
               FileOutputStream fs = new FileOutputStream(newFile); 
               byte[] buffer = new byte[1444]; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                   fs.write(buffer, 0, byteread); 
               } 
               inStream.close(); 
               fs.close();
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制单个文件操作出错"); 
           e.printStackTrace(); 

       } 

   } 
   
	/**
	 * 在文件夹里创建一个名为.nomedia的文件，即可对系统隐藏多媒体文件
	 * 
	 * @param dirPath
	 *            目录路径
	 */
   public static void isExitNoMedia(String dirPath) {
		// TODO 自动生成的方法存根
		File file = new File(dirPath + ".nomedia");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
   
}
