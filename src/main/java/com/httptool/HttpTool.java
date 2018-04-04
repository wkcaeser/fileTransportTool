package com.httptool;

import com.common.FileTool;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicReference;

public class HttpTool {
	/**
	 * 生成http连接
	 * @param method http请求方式
	 * @return httpURLConnection
	 * @throws IOException 连接生成失败
	 */
	private static HttpURLConnection createConnection(String urlPath, String method) throws IOException {
		URL url = new URL(urlPath);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setRequestMethod(method);

		httpURLConnection.setRequestProperty("connection", "Keep-Alive");
		httpURLConnection.setRequestProperty("Charsert", "UTF-8");
		httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data");

		return httpURLConnection;
	}

	/**
	 * 获取文件流
	 * @param fileUrl 文件名
	 */
	public static void downloadFile(String fileUrl, String savePath){
		AtomicReference<HttpURLConnection> connection = new AtomicReference<>(null);
		try {
			 connection.set(createConnection(fileUrl, "GET"));
			if (connection.get() == null){
				throw new IOException("连接为空");
			}
			connection.get().connect();
			InputStream inputStream = connection.get().getInputStream();

			String[] urls = fileUrl.split("/");
			savePath += urls[urls.length - 1];

			FileTool.writeFile(savePath, inputStream);

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("连接请求失败！");
		}
	}


	public static void uploadFiles(File[] files){
		String url = "";
		for (File file : files){
			uploadFile(file, url);
		}
	}

	/**
	 * 上传文件
	 * @param file 文件
	 * @param url 请求路径
	 */
	private static void uploadFile(File file, String url){
		HttpURLConnection connection;
		OutputStream outputStream = null;
		FileInputStream inputStream = null;
		FileChannel fileChannel = null;
		try {
			connection = createConnection(url, "PUT");
			connection.connect();

			outputStream = connection.getOutputStream();

			inputStream = new FileInputStream(file);
			fileChannel = inputStream.getChannel();

			ByteBuffer buffer = ByteBuffer.allocate(1024<<4);

			while (fileChannel.read(buffer) != -1){
				byte[] bytes = buffer.array();
				buffer.clear();
				outputStream.write(bytes);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null){
					outputStream.close();
				}
				if (inputStream != null){
					inputStream.close();
				}
				if(fileChannel != null){
					fileChannel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
