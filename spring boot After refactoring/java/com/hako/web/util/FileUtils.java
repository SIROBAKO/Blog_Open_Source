package com.hako.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FileUtils extends org.apache.commons.io.FileUtils {

	private static final String FAIL_COPY_FOLDER = "폴더 복사 실패";
	private static final String FAIL_UPDATE_SITEMAP = "사이트맵 업데이트 실패";
	private static final String FAIL_DELETE_FILE = "파일 삭제 실패";

	private Logger LOG = LogManager.getLogger(FileUtils.class);

	public void copyFolder(String path_folder1, String path_folder2) {

		File folder1;
		File folder2;
		folder1 = new File(path_folder1);
		folder2 = new File(path_folder2);

		if (!folder1.exists())
			folder1.mkdirs();
		if (!folder2.exists())
			folder2.mkdirs();
		File[] target_file = folder1.listFiles();
		for (File file : target_file) {
			File temp = new File(folder2.getAbsolutePath() + File.separator + file.getName());
			if (file.isDirectory()) {
				temp.mkdir();
			} else {
				FileInputStream fis = null;
				FileOutputStream fos = null;
				try {
					fis = new FileInputStream(file);
					fos = new FileOutputStream(temp);
					byte[] b = new byte[4096];
					int cnt = 0;
					while ((cnt = fis.read(b)) != -1) {
						fos.write(b, 0, cnt);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						fis.close();
						fos.close();
					} catch (IOException e) {

						LOG.error(FAIL_COPY_FOLDER, e);
					}
				}

			}
		}
	}

	public void deleteFolder(String path) {
		File folder = new File(path);
		try {
			if (folder.exists()) {
				File folder_list[] = folder.listFiles();
				for (int i = 0; i < folder_list.length; i++) {
					if (folder_list[i].isFile())
						folder_list[i].delete();
					else
						deleteFolder(folder_list[i].getPath());
					folder_list[i].delete();
				}

				folder.delete();
			}
		} catch (Exception e) {
			LOG.error(FAIL_COPY_FOLDER, e);
		}
	}

	public List<String> getFileNamesFromFolder(String folderPath) {
		List<String> fileNames = new ArrayList<>();

		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					fileNames.add(file.getName());
				}
			}
		}

		return fileNames;
	}

	public void deleteFile(String filePath, String fileName) {
		Path path = Paths.get(filePath, fileName);
		try {
			Files.delete(path);
		} catch (Exception e) {
			LOG.error(FAIL_DELETE_FILE, e);
		}
	}

}
