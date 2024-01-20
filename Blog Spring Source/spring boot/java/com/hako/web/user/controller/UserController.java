package com.hako.web.user.controller;

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

import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;
import com.hako.web.blog.entity.Blog_Count;
import com.hako.web.blog.service.BlogService;
import com.hako.web.config.jwt.JwtProvider;
import com.hako.web.user.entity.BlogToken;
import com.hako.web.user.entity.BlogUser;
import com.hako.web.user.service.KakaoService;
import com.hako.web.user.service.UserService;

@Controller
@RequestMapping("/")
public class UserController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private KakaoService kakaoService;

	@Autowired
	private UserService userService;

	@Autowired
	protected JwtProvider jwtProvider;

	private Logger LOG = LogManager.getLogger(UserController.class);
	// 메인페이지 반환
	@RequestMapping("blog-login")
	public String userLogin(Model model) {

		List<Blog_Board> commend = blogService.getCommendBoard();
		List<Blog_Category> category = blogService.getCategory();
		List<Blog_Count> countList = blogService.getCount();

		model.addAttribute("categorys", category);
		model.addAttribute("commend", commend);
		model.addAttribute("countList", countList);
		model.addAttribute("url", "index");

		return "Blog.login";
	}

	// 회원 가입 페이지 반환
	@RequestMapping("blog-join")
	public String userJoin(Model model) {

		List<Blog_Board> commend = blogService.getCommendBoard();
		List<Blog_Category> category = blogService.getCategory();
		List<Blog_Count> countList = blogService.getCount();

		model.addAttribute("categorys", category);
		model.addAttribute("commend", commend);
		model.addAttribute("countList", countList);
		model.addAttribute("url", "index");

		return "Blog.join";
	}

	// 아이디 비밀번호 찾기 페이지
	@RequestMapping("blog-find")
	public String userFidn(Model model) {

		List<Blog_Board> commend = blogService.getCommendBoard();
		List<Blog_Category> category = blogService.getCategory();
		List<Blog_Count> countList = blogService.getCount();

		model.addAttribute("categorys", category);
		model.addAttribute("commend", commend);
		model.addAttribute("countList", countList);
		model.addAttribute("url", "index");

		return "Blog.find";
	}

	// 아이디 비밀번호 찾기 페이지
	@RequestMapping("blog-mypage")
	public String userPage(Model model) {

		List<Blog_Board> commend = blogService.getCommendBoard();
		List<Blog_Category> category = blogService.getCategory();
		List<Blog_Count> countList = blogService.getCount();

		model.addAttribute("categorys", category);
		model.addAttribute("commend", commend);
		model.addAttribute("countList", countList);
		model.addAttribute("url", "index");

		return "Blog.mypage";
	}

	// 카카오 로그인
	@RequestMapping("/kakao/callback")
	public String kakaoCallback(HttpServletRequest req, @RequestParam("code") String code, HttpServletResponse response)
			throws Exception {

		LOG.info("카카오 로그인 요청");

		String kakaoId = kakaoService.getKakaoInfo(code);
		BlogUser user = null;
		if ((user = userService.getUser(kakaoId)) != null) {
			// 성공시 토큰 생성
			BlogToken blogToken = new BlogToken();
			Map<String, String> Token = jwtProvider.generateTokenSet(kakaoId);
			blogToken.setAccess_token(Token.get("accessToken"));
			blogToken.setRefresh_token(Token.get("refreshToken"));
			blogToken.setUser_id(kakaoId);

			if (userService.saveToken(blogToken)) {
				LOG.info("카카오 로그인 성공");

				Cookie cookie = new Cookie("AccToken", Token.get("accessToken"));
				cookie.setPath("/");
				cookie.setHttpOnly(true);

				cookie = new Cookie("RefToken", Token.get("refreshToken"));
				cookie.setPath("/");
				cookie.setHttpOnly(true);
				response.addCookie(cookie);

			} else {
				LOG.info("카카오 로그인 실패");
				// 기존 가입 유저
				req.setAttribute("msg", "카카오 로그인 실패 관리자에게 문의해주세요.");
				req.setAttribute("url", "back -2");
				return "alert";

			}

			// 기존 가입 유저
			req.setAttribute("url", "back -2");
			return "alert";
		} else {
			// 추가 기입후 가입 유저
			req.setAttribute("msg", "카카오 가입을 위해 추가정보를 입력해주세요.");
			req.setAttribute("url", "/blog-join?form=kakao" + kakaoId);
			return "alert";
		}
	}

}
