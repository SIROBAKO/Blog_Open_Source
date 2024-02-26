package com.hako.web.blog.board.controller;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.board.entity.BlogCategory;
import com.hako.web.blog.board.service.BoardService;
import com.hako.web.blog.board.service.CategoryService;
import com.hako.web.util.FileUtils;

@Controller
@RequestMapping("/admin/")
public class BoardAdminController {

	// 업로드 파일 저장 경로
	@Value("${blog.realPath}")
	private String realPath;

	// 추가 애드센스 코드
	@Value("${blog.adScript}")
	private String adScript;

	@Autowired
	private BoardService boardService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private FileUtils fileUtils;

	private static final String NOT_FOUND_CONTEXT = "존재하지 않는 게시물 입니다.";

	private static final String ADMIN_LIST = "/admin/list";

	private static final String BLOG_ADMiN_EDIT_VIEW = "Blog.admin.edit";
	private static final String BLOG_ADMiN_LIST_VIEW = "Blog.admin.list";
	private static final String BLOG_ADMiN_UPDATE_VIEW = "Blog.admin.update";
	private static final String BLOG_ALERT_VIEW = "alert";

	private static final String HIDDEN_CATEGORY_VALUE = "hidden";
	private static final String HIDDEN_CATEGORY_NAME = "임시저장";

	private static final String BOARD_ATTRIBUTE = "board";
	private static final String CATEGORY_LIST_ATTRIBUTE = "categorys";
	private static final String TOTAL_BOARD_COUNT_ATTRIBUTE = "count";
	private static final String CATEGORY_NAME_ATTRIBUTE = "category";
	private static final String BOARD_LIST_ATTRIBUTE = "list";
	private static final String ALERT_MESSAGE_ATTRIBUTE = "msg";
	private static final String ALERT_REDIRECT_ATTRIBUTE = "url";

	private static final String ADSCRIPT_PLACEHOLDER = "-----";

	private String IMAGE_TEMP_FOLDER_PATH;
	private String IMAGE_FOLDER_PATH;

	@PostConstruct
	public void init() {
		IMAGE_TEMP_FOLDER_PATH = realPath + "/upload/image/fileupload/temp/";
		IMAGE_FOLDER_PATH = realPath + "/upload/image/fileupload/";
	}

	// 게시글 작성 페이지
	@RequestMapping("edit")
	public String blogAdminEditPage(HttpServletRequest req, String pwd, Model model) {

		fileUtils.deleteFolder(IMAGE_TEMP_FOLDER_PATH);

		loadCommonData(model);
		getServerInfo(model);

		return BLOG_ADMiN_EDIT_VIEW;
	}

	// 게시글 목록 페이지
	@RequestMapping(value = { "list/{category}", "list" })
	public String blogAdminListPage(HttpServletRequest req, Model model,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@PathVariable(required = false, value = "category") String categoryName, String title) {

		categoryName = categoryName != null ? categoryName : "";
		title = title != null ? title : "";

		List<BlogBoard> boardList;
		int totalBoardCount;

		if (categoryName.equals(HIDDEN_CATEGORY_VALUE)) {
			categoryName = "";
			boardList = boardService.getHiddenBoardList(categoryName, title, page);
			totalBoardCount = boardService.getHiddenBoardCount(categoryName, title);
			model.addAttribute(CATEGORY_NAME_ATTRIBUTE, HIDDEN_CATEGORY_NAME);
		} else {
			boardList = boardService.getBoardList(categoryName, title, page);
			totalBoardCount = boardService.getBoardCount(categoryName, title);
			model.addAttribute(CATEGORY_NAME_ATTRIBUTE, categoryName);
		}

		loadCommonData(model);
		model.addAttribute(TOTAL_BOARD_COUNT_ATTRIBUTE, totalBoardCount);
		model.addAttribute(BOARD_LIST_ATTRIBUTE, boardList);
		getServerInfo(model);

		return BLOG_ADMiN_LIST_VIEW;

	}

	// 게시글 업데이트 페이지
	@RequestMapping("update")
	public String update(HttpServletRequest requset, Model model,
			@RequestParam(name = "num", defaultValue = "-1") int boardNum) {

		BlogBoard board = boardService.getBoard(boardNum);

		if (board == null) {
			return alertMessage(NOT_FOUND_CONTEXT, ADMIN_LIST, requset);
		}

		replaceBoardContents(board);

		loadCommonData(model);

		model.addAttribute(BOARD_ATTRIBUTE, board);
		getServerInfo(model);

		return BLOG_ADMiN_UPDATE_VIEW;

	}

	private void replaceBoardContents(BlogBoard board) {
		fileUtils.deleteFolder(IMAGE_TEMP_FOLDER_PATH);

		String imageFolderPath = IMAGE_FOLDER_PATH + board.getNum() + "/";
		fileUtils.copyFolder(imageFolderPath, IMAGE_TEMP_FOLDER_PATH);

		String BoardImagePath = "/" + board.getNum() + "/";
		String tempImagePath = "/temp/";

		String boardContents = board.getContents().replaceAll(BoardImagePath, tempImagePath);
		boardContents = board.getContents().replaceAll(Pattern.quote(adScript), ADSCRIPT_PLACEHOLDER);
		board.setContents(boardContents);
	}

	// 서버 정보 확인 메모리, cpu 사용량
	private Model getServerInfo(Model model) {

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

	private String alertMessage(String message, String pageUrl, HttpServletRequest request) {

		request.setAttribute(ALERT_MESSAGE_ATTRIBUTE, message);
		request.setAttribute(ALERT_REDIRECT_ATTRIBUTE, pageUrl);

		return BLOG_ALERT_VIEW;
	}

	private void loadCommonData(Model model) {

		List<BlogCategory> categoryList = categoryService.getCategoryList();
		model.addAttribute(CATEGORY_LIST_ATTRIBUTE, categoryList);

	}
}
