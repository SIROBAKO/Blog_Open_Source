package com.hako.web.blog.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.board.entity.BlogCategory;
import com.hako.web.blog.board.entity.BlogCount;
import com.hako.web.blog.board.service.BoardService;
import com.hako.web.blog.board.service.CategoryService;
import com.hako.web.blog.board.service.CountService;
import com.hako.web.blog.commet.entity.BlogComment;
import com.hako.web.blog.commet.service.CommentService;

@Controller
@RequestMapping("/")
public class BoardController {

	@Autowired
	private BoardService boardService;

	@Autowired
	private CountService countService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CommentService commentService;

	private enum UserAgentType {
		BOT, SPIDER, CRAWLER, KAKAO, DAUM, GOOGLE, NAVER, IAS, IFRAMELY, NOTION, FACEBOOK, SCALAJ_HTTP, TTD_CONTENT,
		LIGHTHOUSE, CENSYSINSPECT
	}

	private static final String NOT_FOUND_CONTEXT = "존재하지 않는 게시물 입니다.";
	private static final String NOT_FOUND_LIST = "게시글이 존재하지 않습니다.";

	private static final String INDEX = "/index";

	private static final String LIST_PAGE_ENDPOINT = "list/";
	private static final String DETAIL_PAGE_ENDPOINT = "detail/";
	private static final String INDEX_PAGE_ENDPOINT = "index";

	private static final String HIDDEN_BOARD_VALUE = "N";
	private static final String VISITED_VALUE = "VISITED";

	private static final String VISITED_COOKIE_NAEM = "visited";

	private static final String BOARD_LIST_ATTRIBUTE = "list";
	private static final String VIEW_COUNT_LIST_ATTRIBUTE = "countList";
	private static final String COMMEND_BOARD_LIST_ATTRIBUTE = "commend";
	private static final String CATEGORY_LIST_ATTRIBUTE = "categorys";
	private static final String PREFERRED_URL_ATTRIBUTE = "url";
	private static final String BOARD_CONTEXT_ATTRIBUTE = "board";
	private static final String COMMENT_LIST_ATTRIBUTE = "comment";
	private static final String REVERSE_COMMENT_LIST_ATTRIBUTE = "recomment";
	private static final String NEXT_BOARD_LIST_ATTRIBUTE = "nextBoard";
	private static final String PREVIOUS_BOARD_LIST_ATTRIBUTE = "prevBoard";
	private static final String CURRENT_PAGE_ATTRIBUTE = "page";
	private static final String TOTAL_BOARD_COUNT_ATTRIBUTE = "count";
	private static final String CATEGORY_NAME_ATTRIBUTE = "category";
	private static final String ALERT_MESSAGE_ATTRIBUTE = "msg";
	private static final String ALERT_REDIRECT_ATTRIBUTE = "url";

	private static final String BLOG_INDEX_VIEW = "Blog.index";
	private static final String BLOG_ALERT_VIEW = "alert";
	private static final String BLOG_DETAIL_VIEW = "Blog.nonaside.detail";
	private static final String BLOG_List_VIEW = "Blog.list";

	private static final int MAX_SIZE_BOARD_LIST = 4;
	private static final int FIT_SIZE_BOARD_LIST = 2;

	@RequestMapping("alert")
	public String blogAlert() {

		return BLOG_ALERT_VIEW;
	}

	@RequestMapping("index")
	public String blogIndexPage(Model model) {

		loadCommonData(model);

		List<BlogBoard> board_list = boardService.getBoardList();

		model.addAttribute(BOARD_LIST_ATTRIBUTE, board_list);
		model.addAttribute(PREFERRED_URL_ATTRIBUTE, INDEX_PAGE_ENDPOINT);

		return BLOG_INDEX_VIEW;
	}

	@RequestMapping("detail/{board_num}")
	public String blogDetailPage(HttpServletRequest request, HttpServletResponse response, Model model,
			@PathVariable(value = "board_num") String boardNum,
			@RequestHeader(value = "User-Agent", required = false) String userAgent) {

		int _num = Integer.parseInt(boardNum);

		BlogBoard boardContext = boardService.getBoard(_num);

		if (!isValidBoard(boardContext)) {
			return alertMessage(NOT_FOUND_CONTEXT, INDEX, request);
		}
		countVisitor(request, response, userAgent, _num);
		setCommentList(model, _num);
		setBoardListNextAndPrev(model, boardContext);
		loadCommonData(model);
		model.addAttribute(BOARD_CONTEXT_ATTRIBUTE, boardContext);
		model.addAttribute(PREFERRED_URL_ATTRIBUTE, DETAIL_PAGE_ENDPOINT + boardNum);

		return BLOG_DETAIL_VIEW;

	}

	@RequestMapping(value = { "list", "list/{category}" })
	public String blogListPage(HttpServletRequest request, Model model,
			@PathVariable(required = false, value = "category") String categoryName,
			@RequestParam(defaultValue = "1", name = "page") int page,
			@RequestParam(defaultValue = "", name = "title") String title) {

		categoryName = (categoryName == null) ? "" : categoryName;

		List<BlogBoard> boardList = boardService.getBoardList(categoryName, title, page);

		int totalBoardCount = boardService.getBoardCount(categoryName, title);

		if (boardList.isEmpty()) {
			return alertMessage(NOT_FOUND_LIST, INDEX, request);
		}

		loadCommonData(model);

		model.addAttribute(CURRENT_PAGE_ATTRIBUTE, page);
		model.addAttribute(CATEGORY_NAME_ATTRIBUTE, categoryName);
		model.addAttribute(TOTAL_BOARD_COUNT_ATTRIBUTE, totalBoardCount);
		model.addAttribute(BOARD_LIST_ATTRIBUTE, boardList);

		model.addAttribute(PREFERRED_URL_ATTRIBUTE, LIST_PAGE_ENDPOINT + categoryName);

		return BLOG_List_VIEW;
	}

	private void countVisitor(HttpServletRequest request, HttpServletResponse response, String userAgent,
			int board_num) {

		Cookie[] cookies = request.getCookies();

		if (!isCrawl(userAgent)) {

			// 조회수 증가 여부 확인
			boolean increaseCount = true;
			boolean increaseVisitorCount = true;

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					String cookieName = cookie.getName();
					if (cookieName.equals(VISITED_COOKIE_NAEM + board_num)) {
						increaseCount = false;
						break;
					}
				}
				for (Cookie cookie : cookies) {
					String cookieName = cookie.getName();
					if (cookieName.equals(VISITED_COOKIE_NAEM)) {
						increaseVisitorCount = false;
						break;
					}
				}

			}

			// 조회수 증가
			if (increaseCount) {
				boardService.addViewCount(board_num);
				countService.addTotalCount("TODAY_COUNT");
				Cookie cookie = new Cookie(VISITED_COOKIE_NAEM + board_num, VISITED_VALUE);
				cookie.setMaxAge(60 * 60);
				cookie.setPath("/");
				response.addCookie(cookie);
			}

			// 방문자 수 증가
			if (increaseVisitorCount) {
				countService.addTotalCount("TODAY_VISITER");
				Cookie cookie = new Cookie(VISITED_COOKIE_NAEM, VISITED_VALUE);
				cookie.setMaxAge(getSecondsUntilEndOfDay());
				cookie.setPath("/");
				response.addCookie(cookie);
			}
		}
	}

	private boolean isCrawl(String userAgent) {
		if (userAgent == null) {
			return true; // null인 경우 크롤러로 간주합니다.
		}

		userAgent = userAgent.toUpperCase();

		for (UserAgentType type : UserAgentType.values()) {
			if (userAgent.contains(type.name())) {
				return true; // 크롤러로 간주되는 문자열을 포함하고 있으면 true 반환
			}
		}
		return false; // 크롤러로 간주되지 않으면 false 반환
	}

	private int getSecondsUntilEndOfDay() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime endOfDay = now.with(LocalTime.MAX);
		Duration duration = Duration.between(now, endOfDay);
		long seconds = duration.getSeconds();
		return (int) seconds;
	}

	private String alertMessage(String message, String pageUrl, HttpServletRequest request) {

		request.setAttribute(ALERT_MESSAGE_ATTRIBUTE, message);
		request.setAttribute(ALERT_REDIRECT_ATTRIBUTE, pageUrl);

		return BLOG_ALERT_VIEW;
	}

	private boolean isValidBoard(BlogBoard boardContext) {
		return boardContext != null && boardContext.getHidden().equals(HIDDEN_BOARD_VALUE);
	}

	private void loadCommonData(Model model) {

		List<BlogCategory> categoryList = categoryService.getCategoryList();
		List<BlogCount> viewCountList = countService.getCountInfoList();
		List<BlogBoard> commendBoardList = boardService.getCommendBoardList();
		model.addAttribute(COMMEND_BOARD_LIST_ATTRIBUTE, commendBoardList);
		model.addAttribute(CATEGORY_LIST_ATTRIBUTE, categoryList);
		model.addAttribute(VIEW_COUNT_LIST_ATTRIBUTE, viewCountList);

	}

	private void setBoardListNextAndPrev(Model model, BlogBoard boardContext) {
		// 다음 글 두개 가져오기
		int boardNum = boardContext.getNum();
		String category = boardContext.getCategory();
		List<BlogBoard> nextBoardList = boardService.getCommendNextBoardList(category, boardNum, FIT_SIZE_BOARD_LIST);
		List<BlogBoard> prevBoardList = boardService.getCommendPrevBoardList(category, boardNum, FIT_SIZE_BOARD_LIST);
		if (nextBoardList.size() < FIT_SIZE_BOARD_LIST && prevBoardList.size() == FIT_SIZE_BOARD_LIST) {
			prevBoardList = boardService.getCommendPrevBoardList(category, boardNum,
					(MAX_SIZE_BOARD_LIST - nextBoardList.size()));
		} else if (nextBoardList.size() == FIT_SIZE_BOARD_LIST && prevBoardList.size() < FIT_SIZE_BOARD_LIST) {
			nextBoardList = boardService.getCommendNextBoardList(category, boardNum,
					(MAX_SIZE_BOARD_LIST - prevBoardList.size()));
		}

		model.addAttribute(NEXT_BOARD_LIST_ATTRIBUTE, nextBoardList);
		model.addAttribute(PREVIOUS_BOARD_LIST_ATTRIBUTE, prevBoardList);

	}

	private void setCommentList(Model model, int boardNum) {

		List<BlogComment> commentList = commentService.getCommentList(boardNum);
		List<BlogComment> reverseCommentList = new ArrayList<>(commentList);
		Collections.reverse(reverseCommentList);

		model.addAttribute(COMMENT_LIST_ATTRIBUTE, commentList);
		model.addAttribute(REVERSE_COMMENT_LIST_ATTRIBUTE, reverseCommentList);
	}

}
