package com.hako.web.blog.user.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.board.entity.BlogCategory;
import com.hako.web.blog.board.entity.BlogCount;
import com.hako.web.blog.board.service.BoardService;
import com.hako.web.blog.board.service.CategoryService;
import com.hako.web.blog.board.service.CountService;
import com.hako.web.blog.user.entity.BlogToken;
import com.hako.web.blog.user.entity.BlogUser;
import com.hako.web.blog.user.service.KakaoService;
import com.hako.web.blog.user.service.TokenService;
import com.hako.web.blog.user.service.UserService;
import com.hako.web.config.jwt.JwtProvider;

@Controller
@RequestMapping("/")
public class UserController {

	@Autowired
	private BoardService boardService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CountService countService;

	@Autowired
	private KakaoService kakaoService;

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	protected JwtProvider jwtProvider;

	@Autowired
	protected UserRestController userRestController;

	private Logger LOG = LogManager.getLogger(UserController.class);

	final String PREFERRED_URL_ATTRIBUTE = "url";
	final String INDEX_PAGE_ENDPOINT = "index";
	final String VIEW_COUNT_LIST_ATTRIBUTE = "countList";
	final String COMMEND_BOARD_LIST_ATTRIBUTE = "commend";
	final String CATEGORY_LIST_ATTRIBUTE = "categorys";

	private static final String BLOG_LOGIN_VIEW = "Blog.login";
	private static final String BLOG_JOIN_VIEW = "Blog.join";
	private static final String BLOG_FIND_VIEW = "Blog.find";
	private static final String BLOG_MYPAGE_VIEW = "Blog.mypage";
	private static final String BLOG_ALERT_VIEW = "alert";

	private static final String ALERT_MESSAGE_ATTRIBUTE = "msg";
	private static final String ALERT_REDIRECT_ATTRIBUTE = "url";

	private static final String KAKAO_LOGIN_START = "카카오 로그인 요청 시작";
	private static final String KAKAO_LOGIN_SUCCESS = "카카오 로그인 성공";
	private static final String KAKAO_LOGIN_FAILURE = "카카오 로그인 실패";
	private static final String KAKAO_SIGNUP_REDIRECT = "카카오 회원가입 페이지 이동";
	private static final String KAKAO_SIGNUP_REDIRECT_MESSAGE = "카카오 가입을 위해 추가정보를 입력해주세요.";

	private static final String WELCOME_MESSAGE = "환영합니다.";
	private static final String FAILURE_MESSAGE = "카카오 로그인 실패 관리자에게 문의해주세요.";
	private static final String REDIRECT_LOGIN_PAGE = "/blog-login";
	private static final String REFERRER = "referrer";
	private static final String KAKAO_JOIN_FORM = "/blog-join?form=kakao";

	// 메인페이지 반환
	@RequestMapping("blog-login")
	public String userLogin(Model model) {

		loadCommonData(model);

		return BLOG_LOGIN_VIEW;
	}

	// 회원 가입 페이지 반환
	@RequestMapping("blog-join")
	public String userJoin(Model model) {

		loadCommonData(model);

		return BLOG_JOIN_VIEW;
	}

	// 아이디 비밀번호 찾기 페이지
	@RequestMapping("blog-find")
	public String userFidn(Model model) {

		loadCommonData(model);

		return BLOG_FIND_VIEW;
	}

	// 아이디 비밀번호 찾기 페이지
	@RequestMapping("blog-mypage")
	public String userPage(Model model) {

		loadCommonData(model);

		return BLOG_MYPAGE_VIEW;
	}

	@RequestMapping("/kakao/callback")
	public String kakaoCallback(HttpServletRequest request, @RequestParam("code") String code,
			HttpServletResponse response) throws Exception {
		LOG.info(KAKAO_LOGIN_START);

		try {
			String kakaoId = kakaoService.getKakaoInfo(code);
			BlogUser user = userService.getUserFromId(kakaoId);

			if (isKakaoUser(user)) {

				BlogToken token = userRestController.createToken(user);

				if (tokenService.saveToken(token)) {
					LOG.info(KAKAO_LOGIN_SUCCESS);
					userRestController.setTokenCookie(token, response);
					return alertMessage(WELCOME_MESSAGE, REFERRER, request);
				} else {
					throw new Exception();
				}
			} else {
				LOG.info(KAKAO_SIGNUP_REDIRECT);
				return alertMessage(KAKAO_SIGNUP_REDIRECT_MESSAGE, KAKAO_JOIN_FORM + kakaoId, request);
			}
		} catch (Exception e) {
			LOG.error(KAKAO_LOGIN_FAILURE, e);
			return alertMessage(FAILURE_MESSAGE, REDIRECT_LOGIN_PAGE, request);
		}
	}

	private boolean isKakaoUser(BlogUser user) {

		return user != null;
	}

	private String alertMessage(String message, String pageUrl, HttpServletRequest request) {

		request.setAttribute(ALERT_MESSAGE_ATTRIBUTE, message);
		request.setAttribute(ALERT_REDIRECT_ATTRIBUTE, pageUrl);

		return BLOG_ALERT_VIEW;
	}

	private void loadCommonData(Model model) {

		List<BlogCategory> categoryList = categoryService.getCategoryList();
		List<BlogCount> viewCountList = countService.getCountInfoList();
		List<BlogBoard> commendBoardList = boardService.getCommendBoardList();
		model.addAttribute(COMMEND_BOARD_LIST_ATTRIBUTE, commendBoardList);
		model.addAttribute(CATEGORY_LIST_ATTRIBUTE, categoryList);
		model.addAttribute(VIEW_COUNT_LIST_ATTRIBUTE, viewCountList);
		model.addAttribute(PREFERRED_URL_ATTRIBUTE, INDEX_PAGE_ENDPOINT);

	}

}
