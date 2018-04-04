package com.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileTool {
	public static boolean writeFile(String path, InputStream inputStream){
		File file = new File(path);
		if (file.exists()){
			boolean isDelete = file.delete();
			if (!isDelete){
				System.err.println("文件已存在！");
				return false;
			}
			try {
				boolean canCreateFile = file.createNewFile();
				if (!canCreateFile){
					System.err.println("文件创建失败！");
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		try(FileOutputStream fileOutputStream = new FileOutputStream(file)
		) {
			//http://www.wkcaeser.com/static/js/jquery-1.8.0.min.js
			byte[] bytes = new byte[1024];
			while (inputStream.read(bytes, 0, 1024) > 0){
					fileOutputStream.write(bytes);
				}
		} catch (java.io.IOException e) {
			e.printStackTrace();
			return false;
		}finally {
			if (inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
