package com;

import com.httptool.HttpTool;

import javax.swing.*;
import java.awt.*;
import java.io.File;


public class Main {
	private JFrame jFrame;
	private JPanel jPanel;

	private Main() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		jFrame = new JFrame("文件上传下载工具");
//		jFrame.setBounds(500, 200, 500, 300);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.setUndecorated(false);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		jFrame.setSize(dimension);

//		GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//		graphicsDevice.setFullScreenWindow(jFrame);

		jPanel = new JPanel();
		jPanel.setLayout(null);

		setDownloadUi();
		setUploadUi();

		jFrame.add(jPanel);
		jFrame.setVisible(true);
	}

	private void setDownloadUi(){
		JLabel jLabel = new JLabel("下载路径URL ：");
		JTextField inputText = new JTextField(50);
		JButton downloadButton = new JButton("下载");

		jLabel.setBounds(50, 50, 200, 30);
		inputText.setBounds(150, 50, 500, 35);
		downloadButton.setBounds(700,52, 80, 30);

		downloadButton.addActionListener(e -> {
			downloadButton.setEnabled(false);
			try {
				String downloadUrl = inputText.getText();
				if (downloadUrl==null || downloadUrl.trim().equals("")){
					JOptionPane.showMessageDialog(jFrame, "请输入下载URL", "",JOptionPane.WARNING_MESSAGE);
					return;
				}
				JFileChooser jf = new JFileChooser("D://");
				jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jf.showOpenDialog(jFrame);

				File file = jf.getSelectedFile();
				if (file != null && file.isDirectory()) {
					String directoryPath = file.getAbsolutePath();
					HttpTool.downloadFile(downloadUrl, directoryPath);
					JOptionPane.showMessageDialog(jFrame, "下载成功", "", JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(jFrame, "请输入本地下载目录", "", JOptionPane.WARNING_MESSAGE);
				}
			} finally {
				downloadButton.setEnabled(true);
			}
		});

		jPanel.add(jLabel);
		jPanel.add(inputText);
		jPanel.add(downloadButton);
	}

	private void setUploadUi(){
		JLabel jLabel = new JLabel("上传文件：");

		jLabel.setBounds(50, 200, 200, 30);

		JFileChooser jFileChooser = new JFileChooser("D://");
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jFileChooser.setMultiSelectionEnabled(true);
		jFileChooser.setBounds(50, 250, 1000, 400);

		JButton uploadButton = new JButton("上传");
		uploadButton.setBounds(900, 200, 150, 30);

		uploadButton.addActionListener(e->{
			uploadButton.setEnabled(false);
			try {
				File[] files = jFileChooser.getSelectedFiles();
				if (files.length > 0){
					HttpTool.uploadFiles(files);
				}
			} finally {
				uploadButton.setEnabled(true);
			}
		});

		jPanel.add(jLabel);
		jPanel.add(uploadButton);
		jPanel.add(jFileChooser);
	}

	public static void main(String[] args) {
		new Main();
	}
}
