package com.hako.web.blog.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;
import com.hako.web.blog.service.BlogService;

@Controller
@RequestMapping("/admin/")
public class BlogAdminController {

	// 사이트 맵 경로
	@Value("${blog.sitemapFilePath}")
	private String SITEMAP_FILE_PATH;

	// 이미지 등 파일 저장 경로
	@Value("${blog.realPath}")
	private String realPath;

	// 추가 애드센스 코드
	@Value("${blog.adScript}")
	private String adScript;

	@Autowired
	private BlogService blogService;

	// 게시글 작성 페이지
	@RequestMapping("edit")
	public String edit(HttpServletRequest req, String pwd, Model model) {

		String path = realPath + "/upload_image/image/fileupload/temp/";
		deleteFolder(path);

		List<Blog_Category> category = blogService.getCategory();
		model.addAttribute("categorys", category);

		model = setServerInfo(model);

		return "Blog.admin.edit";

	}

	// 게시글 목록 페이지
	@RequestMapping(value = { "list/{category}", "list" })
	public String list(HttpServletRequest req, Model model, @RequestParam(name = "page", defaultValue = "1") int page,
			@PathVariable(required = false) String category, String title) {

		category = category != null ? category : "";
		title = title != null ? title : "";

		List<Blog_Board> list = null;

		int count = 0;
		if (category.equals("hidden")) {
			category = "";
			list = blogService.getBoardList(category, title, page, "N");
			count = blogService.getBoardCount(category, title, "N");
			model.addAttribute("category", "임시저장");
		} else {
			list = blogService.getBoardList(category, title, page, "Y");
			count = blogService.getBoardCount(category, title, "Y");
			model.addAttribute("category", category);

		}

		List<Blog_Category> _category = blogService.getCategory();
		model.addAttribute("categorys", _category);
		model.addAttribute("count", Integer.valueOf(count));
		model.addAttribute("list", list);

		model = setServerInfo(model);

		return "Blog.admin.list";

	}

	// 게시글 작성 (기능)
	@RequestMapping("reg")
	public String reg(HttpServletRequest req, HttpServletResponse resp, String title, String content, String category,
			String description, MultipartFile thumbnail, Model model, String doit) {

		Blog_Board board = blogService.getLastBoard();
		String board_num = Integer.toString((board.getNum() + 1));
		content = content.replaceAll("/temp/", "/" + board_num + "/");

		// 애드센스 광고 수동 입력
		content = content.replaceAll("-----", adScript);
		// ----------------------------------
		board.setTitle(title);
		board.setCategory(category);
		board.setContents(content);
		board.setDescription(description);
		if (doit.equals("임시저장")) {
			board.setHidden("Y");
		} else {
			board.setHidden("N");
		}
		if (blogService.insertBoard(board) == 1)
			req.setAttribute("msg", "게시글 등록 성공");
		else
			req.setAttribute("msg", "게시글 등록 실패");

		String path_folder1 = realPath + "/upload_image/image/fileupload/temp/";
		String path_folder2 = realPath + "/upload_image/image/fileupload/" + board_num + "/";

		fileUpload(path_folder1, path_folder2);

		if (!thumbnail.isEmpty()) {
			String path = realPath + "/upload_image/image/fileupload/" + board_num + "/thumbnail/";
			thumbnailUpload(path, thumbnail);
		}

		req.setAttribute("url", "/admin/list");
		return "alert";

	}

	// 게시글 업데이트 페이지
	@RequestMapping("update")
	public String update(HttpServletRequest req, Model model, String num) {

		if (num == null) {
			req.setAttribute("msg", "존재하지 않는 게시물 입니다.");
			req.setAttribute("url", "/admin/list");
			return "alert";
		}
		String path = realPath + "/upload_image/image/fileupload/temp/";
		deleteFolder(path);
		int _num = Integer.parseInt(num);
		Blog_Board board = blogService.getBoard(_num);

		String path_folder1 = realPath + "/upload_image/image/fileupload/" + board.getNum() + "/";
		String path_folder2 = realPath + "/upload_image/image/fileupload/temp/";

		// temp로 임시저장
		fileUpload(path_folder1, path_folder2);

		board.setContents(board.getContents().replaceAll("/" + board.getNum() + "/", "/temp/"));

		// 애드센스 광고 다시 ----- 로 변경

		board.setContents(board.getContents().replaceAll(Pattern.quote(adScript), "-----"));

		// ----------------------------------

		List<Blog_Category> category = blogService.getCategory();
		model.addAttribute("categorys", category);

		if (board.getTitle() == null) {
			req.setAttribute("msg", "존재하지 않는 게시물 입니다.");
			req.setAttribute("url", "/admin/list");
			return "alert";
		} else {

			model.addAttribute("board", board);
			model = setServerInfo(model);

			return "Blog.admin.update";
		}

	}

	// 게시글 업데이트 (비동기)
	@PostMapping(value = "updatePost", produces = "application/json; charset=utf8")
	@ResponseBody
	public String updatePost(@RequestBody HashMap<String, Object> form) {

		try {
			String content = ((String) form.get("content")).replaceAll("/temp/", "/" + form.get("num") + "/");

			// 애드센스 광고 수동 입력
			content = content.replaceAll("-----", adScript);
			// ----------------------------------

			Blog_Board board = new Blog_Board();
			board.setNum((Integer) form.get("num")); // Integer로 그대로 사용
			board.setTitle((String) form.get("title"));
			board.setDescription((String) form.get("description")); // Fix typo in the key "descripyion" to
																	// "description"
			board.setCategory((String) form.get("category"));
			board.setContents(content);

			if (blogService.getBoard((int) form.get("num")).getHidden().equals("Y")) {
				board.setHidden("Y");
			} else {
				board.setHidden("N");
			}

			if (blogService.updateBoard(board) == 1) {

				// 본문에 안들어간 파일들 삭제(temp 폴더)
				String filePath = realPath + "/upload_image/image/fileupload/temp/";

				// 더미 파일 삭제함수 매개변수: 파일 목록, 파일 경로, 검사할 본문
				removeDummyFiles(getFileNamesFromFolder(filePath), filePath, content);

				// 본글의 폴더 비우기
				filePath = realPath + "/upload_image/image/fileupload/" + form.get("num") + "/";
				for (String fileName : getFileNamesFromFolder(filePath)) {
					deleteFile(filePath, fileName);
				}

				// temp 에저 저장된 데이터들 업로드
				String path_folder1 = realPath + "/upload_image/image/fileupload/temp/";
				String path_folder2 = realPath + "/upload_image/image/fileupload/" + form.get("num") + "/";

				fileUpload(path_folder1, path_folder2);

				return "업데이트 성공";
			} else {
				return "업데이트 실패";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "에러 발생: " + e.getMessage();
		}
	}

	// 게시글 삭제, 업데이트 (기능)
	@RequestMapping("del_update")
	public String del_update(HttpServletRequest req, String num, String doit, String title, String content,
			String description, String category, MultipartFile thumbnail) {
		if (num == null) {
			req.setAttribute("msg", "존재하지 않는 게시물 입니다.");
			req.setAttribute("url", "/admin/list");
			return "alert";
		}
		int _num = Integer.parseInt(num);

		if (doit.equals("업데이트") || doit.equals("비공개")) {

			content = content.replaceAll("/temp/", "/" + Integer.toString(_num) + "/");

			// 애드센스 광고 수동 입력
			content = content.replaceAll("-----", adScript);
			// ----------------------------------

			Blog_Board board = new Blog_Board();
			board.setNum(_num);
			board.setTitle(title);
			board.setDescription(description);
			board.setCategory(category);
			board.setContents(content);
			if (doit.equals("비공개")) {
				board.setHidden("Y");
			} else {
				board.setHidden("N");
			}

			if (blogService.updateBoard(board) == 1) {
				req.setAttribute("msg", "게시글 업데이트 성공");

				// 본문에 안들어간 파일들 삭제(temp 폴더)
				String filePath = realPath + "/upload_image/image/fileupload/temp/";

				// 더미 파일 삭제함수 매개변수 : 파일 목록, 파일 경로, 검사할 본문
				removeDummyFiles(getFileNamesFromFolder(filePath), filePath, content);

				// 본글의 폴더 비우기
				filePath = realPath + "/upload_image/image/fileupload/" + num + "/";
				for (String fileName : getFileNamesFromFolder(filePath)) {
					deleteFile(filePath, fileName);
				}

				// temp 에저 저장된 데이터들 업로드
				String path_folder1 = realPath + "/upload_image/image/fileupload/temp/";
				String path_folder2 = realPath + "/upload_image/image/fileupload/" + Integer.toString(_num) + "/";

				fileUpload(path_folder1, path_folder2);

				// 썸네일 변경 있으면 저장
				if (!thumbnail.isEmpty()) {
					String path = realPath + "/upload_image/image/fileupload/" + Integer.toString(_num) + "/thumbnail/";
					deleteFolder(path);
					thumbnailUpload(path, thumbnail);

				}
			} else {
				req.setAttribute("msg", "게시글 업데이트 실패");
			}

			req.setAttribute("url", "/admin/list");
			return "alert";
		}
		if (doit.equals("삭제")) {

			if (blogService.delBoard(_num) == 1) {
				req.setAttribute("msg", "게시글 삭제 성공");
				String path = realPath + "/upload_image/image/fileupload/" + Integer.toString(_num) + "/";

				deleteFolder(path);
			} else {
				req.setAttribute("msg", "게시글 삭제 실패");
			}
		}
		req.setAttribute("url", "/admin/list");
		return "alert";
	}

	// 서머노트 이미지 업로드 temp 저장
	@RequestMapping(value = "/uploadSummernoteImageFile", produces = "application/json; charset=utf8")
	@ResponseBody
	public String uploadSummernoteImageFile(@RequestParam("file") MultipartFile multipartFile,
			HttpServletRequest request) {
		JsonObject jsonObject = new JsonObject();
		String contextRoot = realPath + "/upload_image/image/fileupload/temp/";
		String fileRoot = contextRoot;
		String originalFileName = multipartFile.getOriginalFilename();
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
		String savedFileName = UUID.randomUUID() + extension;
		File targetFile = new File(fileRoot + savedFileName);
		try {
			java.io.InputStream fileStream = multipartFile.getInputStream();
			FileUtils.copyInputStreamToFile(fileStream, targetFile);
			jsonObject.addProperty("url", "/upload_image/image/fileupload/temp/" + savedFileName);
			jsonObject.addProperty("responseCode", "success");
		} catch (IOException e) {
			FileUtils.deleteQuietly(targetFile);
			jsonObject.addProperty("responseCode", "error");
			e.printStackTrace();
		}
		String a = jsonObject.toString();
		return a;
	}

	// 서머노트 이미지 삭제 temp 에서
	@RequestMapping(value = "/deleteSummernoteImageFile", produces = "application/json; charset=utf8")
	@ResponseBody
	public void deleteSummernoteImageFile(@RequestParam("file") String fileName) {

		// 폴더 위치
		String filePath = realPath + "/upload_image/image/fileupload/temp/";

		// 해당 파일 삭제
		deleteFile(filePath, fileName);

	}

	// 사이트맵 업데이트
	@PostMapping(value = "/appendSitemap")
	@ResponseBody
    public String appendSitemap(@RequestParam("board_num") int boardNum) {
		JsonObject jsonObject = new JsonObject();
		if(updateSitemap(boardNum)) {
			jsonObject.addProperty("message", "업데이트 성공");
            return jsonObject.toString();
        } else {
        	jsonObject.addProperty("message", "중복된 사이트맵");
        	  return jsonObject.toString();
        }
    }

	// 하위 폴더 복사
	private void fileUpload(String path_folder1, String path_folder2) {

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

						e.printStackTrace();
					}

				}

			}
		}
	}

	// 썸네일 저장
	private void thumbnailUpload(String path, MultipartFile thumbnail) {

		File targetFile = new File(path + "thumbnail.PNG");

		try {
			java.io.InputStream fileStream = thumbnail.getInputStream();
			FileUtils.copyInputStreamToFile(fileStream, targetFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	// 하이 폴더 삭제
	private void deleteFolder(String path) {
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
			e.getStackTrace();
		}
	}

	// 위치값으로 내부 파일이름가져오기
	private List<String> getFileNamesFromFolder(String folderName) {
		List<String> fileNames = new ArrayList<>();

		File folder = new File(folderName);
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

	// 파일 하나삭제
	private void deleteFile(String filePath, String fileName) {
		Path path = Paths.get(filePath, fileName);
		try {
			Files.delete(path);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	// 더미 파일 삭제
	private void removeDummyFiles(List<String> fileNames, String filePath, String contents) {
		// 주어진 파일 이름 리스트를 기반으로 파일을 삭제
		for (String fileName : fileNames) {
			// contents 문자열에 파일 이름이 포함되어 있지 않은 경우 파일 삭제
			if (!contents.contains(fileName)) {
				deleteFile(filePath, fileName);
			}
		}
	}

	// 서버 정보 확인 메모리, cpu 사용량
	private Model setServerInfo(Model model) {

		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

		// Check if the platform supports com.sun.management.OperatingSystemMXBean
		if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
			@SuppressWarnings("deprecation")
			double cpuUsage = ((com.sun.management.OperatingSystemMXBean) osBean).getSystemCpuLoad() * 100;
			DecimalFormat df = new DecimalFormat("0.00");
			String cpuUsageFormatted = df.format(cpuUsage);
			model.addAttribute("Used_Cpu", cpuUsageFormatted + " %");

			MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
			MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

			model.addAttribute("Used_Memory", heapMemoryUsage.getUsed() / (1024 * 1024) + " MB");
		}
		return model;
	}

	// 사이트맵 업데이트
	private boolean updateSitemap(int num) {
		String sitemapContent = getSitemapContent();

		if (!sitemapContent.contains("detail/"+num)) {
			String newUrlElement = "<url>\n" + "    <loc>https://sirobako.co.kr/detail/" + num + "</loc>\n"
					+ "    <priority>0.60</priority>\n" + "</url>";

			sitemapContent = sitemapContent.replace("</urlset>", "") + newUrlElement + "</urlset>";
			updateSitemapFile(sitemapContent);
			return true;
		} else {
			return false;
		}
	}

	// 사이트맵 불러오기
	private String getSitemapContent() {
		try {
			return new String(Files.readAllBytes(Paths.get(SITEMAP_FILE_PATH)));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	// 사이트맵 파일 업데이트
	private void updateSitemapFile(String sitemapContent) {
		try {
			Files.write(Paths.get(SITEMAP_FILE_PATH), sitemapContent.getBytes(), StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
