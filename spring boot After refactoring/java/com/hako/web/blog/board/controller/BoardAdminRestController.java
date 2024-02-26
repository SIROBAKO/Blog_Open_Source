package com.hako.web.blog.board.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.board.service.BoardService;
import com.hako.web.util.FileUtils;

@Controller
@RequestMapping("/admin/")
public class BoardAdminRestController {

	// 이미지 등 파일 저장 경로
	@Value("${blog.realPath}")
	private String REAL_PATH;

	// 추가 애드센스 코드
	@Value("${blog.adScript}")
	private String adScript;

	@Autowired
	private BoardService boardService;

	@Autowired
	private FileUtils fileUtils;
	private static final String SUCCESS_UPDATE_BOARD = "게시글 업데이트 성공";
	private static final String SUCCESS_INSERT_BOARD = "게시글 작성 성공";
	private static final String SUCCESS_DELETE_BOARD = "게시글 삭제 성공";

	private static final String FAIL_UPDATE_BOARD = "게시글 업데이트 실패";
	private static final String FAIL_INSERT_BOARD = "게시글 작성 실패";
	private static final String FAIL_DELETE_BOARD = "게시글 삭제 실패";
	private static final String FAIL_UPDATE_SITEMAP = "사이트맵 업데이트 실패";
	private static final String FAIL_LOAD_SITEMAP = "사이트맵 로드 실패";
	private static final String FAIL_SAVE_THUMBNAIL = "썸네일 저장 실패";

	private static final String START_DELETE_BOARD = "게시글 삭제요청 시작";
	private static final String START_INSERT_BOARD = "게시글 작성요청 시작";
	private static final String START_UPDATE_BOARD = "게시글 수정요청 시작";

	final String ADSCRIPT_PLACEHOLDER = "-----";

	final String HIDDEN_BOARD_VALUE = "Y";
	final String VISIBLE_BOARD_VALUE = "N";

	final String EMPTY_BOARDNUM_VALUE = "-1";

	final String BOARD_NUM_ATTRIBUTE = "boardNum";

	final int SUCCESS_CODE = 1;

	private String IMAGE_TEMP_FOLDER_PATH;
	private String IMAGE_FOLDER_PATH;
	private String SITEMAP_FILE_PATH;

	private String THUMBNAIL_FOLDER_PATH = "/thumbnail/";
	private String END_OF_PATH = "/";

	private String EMPTY = "";
	private Logger LOG = LogManager.getLogger(BoardAdminRestController.class);

	@PostConstruct
	public void init() {
		IMAGE_TEMP_FOLDER_PATH = REAL_PATH + "/upload/image/fileupload/temp/";
		IMAGE_FOLDER_PATH = REAL_PATH + "/upload/image/fileupload/";
		SITEMAP_FILE_PATH = REAL_PATH + "/upload/sitemap/sitemap.xml";
	}

	@DeleteMapping(value = "board")
	@ResponseBody
	public ResponseEntity<String> deleteBoard(
			@RequestParam(name = "boardNum", defaultValue = EMPTY_BOARDNUM_VALUE) int boardNum) {
		LOG.info(START_DELETE_BOARD);
		try {
			if (isBoardDeletedSuccessfully(boardNum)) {
				LOG.info(SUCCESS_DELETE_BOARD);
				String path = IMAGE_FOLDER_PATH + boardNum + END_OF_PATH;
				fileUtils.deleteFolder(path);
				deleteSitemap(boardNum);

				return ResponseEntity.ok().build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.error(FAIL_DELETE_BOARD, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping(value = "board")
	@ResponseBody
	public ResponseEntity<String> insertBoard(
			@RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
			@RequestParam("jsonData") String jsonDataString) {
		LOG.info(START_INSERT_BOARD);
		try {
			// JSON 데이터를 객체로 매핑
			ObjectMapper objectMapper = new ObjectMapper();
			BlogBoard board = objectMapper.readValue(jsonDataString, BlogBoard.class);
			int newBoardNum = boardService.getNewBoardNum();
			board.setNum(newBoardNum);

			replaceBoardContents(board);
			if (isBoardInsertedSuccessfully(board)) {
				LOG.info(SUCCESS_INSERT_BOARD);
				moveBoardImageFileFromTempFolder(board);
				saveThumbnail(thumbnail, board);
				insertSitemap(board);

				HttpHeaders headers = new HttpHeaders();
				headers.add(BOARD_NUM_ATTRIBUTE, Integer.toString(newBoardNum));
				// 사이트맵 업데이트 로직
				return ResponseEntity.status(HttpStatus.OK).headers(headers).build();

			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.error(FAIL_INSERT_BOARD, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 게시글 업데이트 (비동기)
	@PutMapping(value = "board")
	@ResponseBody
	public ResponseEntity<String> updateBoard(
			@RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
			@RequestParam("jsonData") String jsonDataString) {
		LOG.info(START_UPDATE_BOARD);
		try {
			// JSON 데이터를 객체로 매핑
			ObjectMapper objectMapper = new ObjectMapper();
			BlogBoard board = objectMapper.readValue(jsonDataString, BlogBoard.class);

			replaceBoardContents(board);
			setBoardVisibility(board);

			if (isBoardUpdatedSuccessfully(board)) {
				LOG.info(SUCCESS_UPDATE_BOARD);
				moveBoardImageFileFromTempFolder(board);
				saveThumbnail(thumbnail, board);
				insertSitemap(board);

				return ResponseEntity.ok().build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.error(FAIL_UPDATE_BOARD, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@RequestMapping(value = "/uploadSummernoteImageFile")
	@ResponseBody
	public ResponseEntity<String> uploadSummernoteImageFile(@RequestParam("file") MultipartFile uploadImage,
			HttpServletRequest request) {

		String uploadImageName = uploadImage.getOriginalFilename();
		String extension = uploadImageName.substring(uploadImageName.lastIndexOf("."));
		String savedFileName = UUID.randomUUID() + extension;
		String filePath = IMAGE_TEMP_FOLDER_PATH + savedFileName;
		File targetFile = new File(filePath);

		try {
			InputStream fileStream = uploadImage.getInputStream();
			FileUtils.copyInputStreamToFile(fileStream, targetFile);

			return ResponseEntity.ok(filePath.replace(REAL_PATH, EMPTY));
		} catch (IOException e) {
			FileUtils.deleteQuietly(targetFile);
			LOG.error(FAIL_UPDATE_BOARD, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@RequestMapping(value = "/deleteSummernoteImageFile", produces = "application/json; charset=utf8")
	@ResponseBody
	public void deleteSummernoteImageFile(@RequestParam("file") String fileName) {

		// 해당 파일 삭제
		fileUtils.deleteFile(IMAGE_TEMP_FOLDER_PATH, fileName);

	}

	private void replaceBoardContents(BlogBoard board) {

		String tempImagePath = "/temp/";
		String BoardImagePath = END_OF_PATH + board.getNum() + END_OF_PATH;

		String content = board.getContents().replaceAll(tempImagePath, BoardImagePath);
		// 애드센스 광고 수동 입력
		content = content.replaceAll(ADSCRIPT_PLACEHOLDER, adScript);
		
		board.setContents(content);

	}

	// hidden값이 비어있을때만 해당 작업 수행
	private void setBoardVisibility(BlogBoard board) {
		if (board.getHidden() == null) {
			if (isHiddenBoard(board)) {
				board.setHidden(HIDDEN_BOARD_VALUE);
			} else {
				board.setHidden(VISIBLE_BOARD_VALUE);
			}
		}
	}

	private boolean isHiddenBoard(BlogBoard board) {

		return boardService.getBoard(board.getNum()).getHidden().equals(HIDDEN_BOARD_VALUE);
	}

	private boolean isBoardUpdatedSuccessfully(BlogBoard board) {
		return boardService.updateBoard(board) == SUCCESS_CODE;
	}

	private void saveThumbnail(MultipartFile thumbnail, BlogBoard board) {

		if (thumbnail != null && !thumbnail.isEmpty()) {
			String thumbnailPath = IMAGE_FOLDER_PATH + board.getNum() + THUMBNAIL_FOLDER_PATH;
			thumbnailUpload(thumbnailPath, thumbnail);
		}
	}

	private void moveBoardImageFileFromTempFolder(BlogBoard board) {
		List<String> imageFileNameList = fileUtils.getFileNamesFromFolder(IMAGE_TEMP_FOLDER_PATH);

		// 더미 파일 삭제함수 매개변수: 파일 목록, 파일 경로, 검사할 본문
		removeDummyFiles(imageFileNameList, board.getContents());

		String imageFolderPath = IMAGE_FOLDER_PATH + board.getNum() + END_OF_PATH;

		for (String fileName : fileUtils.getFileNamesFromFolder(imageFolderPath)) {
			fileUtils.deleteFile(imageFolderPath, fileName);
		}

		fileUtils.copyFolder(IMAGE_TEMP_FOLDER_PATH, imageFolderPath);
	}

	private boolean isBoardDeletedSuccessfully(int boardNum) {

		return boardService.deleteBoard(boardNum) == SUCCESS_CODE;
	}

	private boolean isBoardInsertedSuccessfully(BlogBoard board) {

		return boardService.insertBoard(board) == SUCCESS_CODE;
	}

	// 썸네일 저장
	private void thumbnailUpload(String path, MultipartFile thumbnail) {

		File targetFile = new File(path + "thumbnail.PNG");

		try {
			java.io.InputStream fileStream = thumbnail.getInputStream();
			FileUtils.copyInputStreamToFile(fileStream, targetFile);
		} catch (Exception e) {
			LOG.error(FAIL_SAVE_THUMBNAIL, e);
		}
	}

	private void removeDummyFiles(List<String> fileNames, String contents) {
		// 주어진 파일 이름 리스트를 기반으로 파일을 삭제
		for (String fileName : fileNames) {
			// contents 문자열에 파일 이름이 포함되어 있지 않은 경우 파일 삭제
			if (!contents.contains(fileName)) {
				fileUtils.deleteFile(IMAGE_TEMP_FOLDER_PATH, fileName);
			}
		}
	}

	private void insertSitemap(BlogBoard board) {

		String sitemapContent = getSitemapContent();

		String endPoint = "detail/" + board.getNum();
		if (board.getHidden().equals(VISIBLE_BOARD_VALUE) && !sitemapContent.contains(endPoint)) {
			String UrlElement = "<url><loc>https://sirobako.co.kr/" + endPoint
					+ "</loc><priority>0.60</priority></url>";

			sitemapContent = sitemapContent.replace("</urlset>", EMPTY) + UrlElement + "</urlset>";
			updateSitemapFile(sitemapContent);
		}
	}

	private void deleteSitemap(int boardNum) {

		String sitemapContent = getSitemapContent();

		String endPoint = "detail/" + boardNum;
		String UrlElement = "<url><loc>https://sirobako.co.kr/" + endPoint + "</loc><priority>0.60</priority></url>";
		sitemapContent = sitemapContent.replace(UrlElement, EMPTY);
		updateSitemapFile(sitemapContent);
	}

	private String getSitemapContent() {
		try {
			return new String(Files.readAllBytes(Paths.get(SITEMAP_FILE_PATH)));
		} catch (IOException e) {
			LOG.error(FAIL_LOAD_SITEMAP, e);
			return EMPTY;
		}
	}

	private void updateSitemapFile(String sitemapContent) {
		try {
			Files.write(Paths.get(SITEMAP_FILE_PATH), sitemapContent.getBytes(), StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			LOG.error(FAIL_UPDATE_SITEMAP, e);
			e.printStackTrace();
		}
	}

}
