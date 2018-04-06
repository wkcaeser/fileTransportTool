package com.httptool;

import com.common.FileTool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class HttpTool {
	private static final String nextLine = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "wk_file_2519775";
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

		httpURLConnection.setRequestProperty("Charsert", "UTF-8");
		return httpURLConnection;
	}

	/**
	 * 下载文件
	 * @param fileUrl 文件名
	 */
	public static boolean downloadFile(String fileUrl, String savePath){
		AtomicReference<HttpURLConnection> connection = new AtomicReference<>(null);
		boolean isSuccess;
		try {
			 connection.set(createConnection(fileUrl, "GET"));
			if (connection.get() == null){
				throw new IOException("连接为空");
			}
			connection.get().connect();
			InputStream inputStream = connection.get().getInputStream();

			String[] urls = fileUrl.split("/");
			savePath += urls[urls.length - 1];

			isSuccess = FileTool.writeFile(savePath, inputStream);
			return isSuccess;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("连接请求失败！");
			return false;
		}
	}


	/**
	 * 多文件上传
	 * @param files 文件
	 */
	public static void uploadFiles(File[] files){
//		String url = "http://172.27.150.3:8095/file/42900400001/2018/04/05/";
		String url = "http://localhost:8080/file";
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
		HttpURLConnection connection = null;
		OutputStream outputStream = null;
		FileInputStream inputStream = null;
		try {
			connection = createConnection(url, "PUT");
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			connection.setRequestProperty("Accept", "application/json");
			connection.connect();

			outputStream = new DataOutputStream(connection.getOutputStream());

			String header = twoHyphens + boundary + nextLine;
			header += "Content-Disposition: form-data;name=\"file\";" + "filename=\"" + file.getName() + "\"" + nextLine + nextLine;
			outputStream.write(header.getBytes());

			inputStream = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = inputStream.read(bytes))!= -1){
				outputStream.write(bytes, 0, length);
			}
			outputStream.write(nextLine.getBytes());

			String footer = nextLine + twoHyphens + boundary + twoHyphens + nextLine;
			outputStream.write(footer.getBytes());
			outputStream.flush();

			InputStream response = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(response);
			while (reader.read() != -1){
				System.out.println(new String(bytes, "UTF-8"));
			}
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
				System.out.println(connection.getResponseMessage());
			}else {
				System.err.println("上传失败");
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
				if (connection != null){
					connection.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
