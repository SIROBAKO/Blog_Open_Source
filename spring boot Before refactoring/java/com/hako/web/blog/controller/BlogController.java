package com.hako.web.blog.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;
import com.hako.web.blog.entity.Blog_Comment;
import com.hako.web.blog.entity.Blog_Count;
import com.hako.web.blog.service.BlogService;
import com.hako.web.config.jwt.JwtProvider;
import com.hako.web.user.dto.ResponseDTO;
import com.hako.web.user.entity.BlogUser;
import com.hako.web.user.service.UserService;

@Controller
@RequestMapping("/")
public class BlogController {

	@Autowired
	private BlogService blogService;

	@Autowired
	private UserService userService;

	@Autowired
	protected JwtProvider jwtProvider;

	@Autowired
	protected MailSender sender; // 타입으로 받을 수 있음

	private Logger LOG = LogManager.getLogger(BlogController.class);


	@RequestMapping("alert")
	public String blogAlert() {

		return "alert";
	}
	
	// 메인페이지 반환
	@RequestMapping("index")
	public String blogIndex(Model model) {

		List<Blog_Board> board_list = blogService.getBoardList("Y");
		List<Blog_Board> commend = blogService.getCommendBoard();
		List<Blog_Category> category = blogService.getCategory();
		List<Blog_Count> countList = blogService.getCount();

		model.addAttribute("list", board_list);
		model.addAttribute("commend", commend);
		model.addAttribute("categorys", category);
		model.addAttribute("countList", countList);
		model.addAttribute("url", "index");

		return "Blog.index";
	}



	// 게시글 밴환
	@RequestMapping(value = { "detail", "detail/{board_num}" })
	public String blogDetail(HttpServletRequest req, HttpServletResponse resp, Model model,
			@PathVariable(required = false, value = "board_num") String num,
			@RequestHeader(value = "User-Agent", required = false) String userAgent) {

		if (num == null) {
			req.setAttribute("msg", "존재하지 않는 게시물 입니다.");
			req.setAttribute("url", "/index");
			return "alert";
		} else {
			int _num = Integer.parseInt(num);
			Blog_Board board = blogService.getBoard(_num);
			model.addAttribute("board", board);

			if (board == null || board.getHidden().equals("Y")) {
				req.setAttribute("msg", "존재하지 않는 게시물 입니다.");
				req.setAttribute("url", "/index");
				return "alert";
			}
			List<Blog_Comment> comment = blogService.getCommentList(_num);

			List<Blog_Comment> recomment = blogService.getCommentList(_num);
			Collections.reverse(recomment);

			// 다음 글 두개 가져오기
			List<Blog_Board> nextBoard = blogService.getCommendNextBoard(board.getCategory(), _num, 2);
			List<Blog_Board> prevBoard = blogService.getCommendPrevBoard(board.getCategory(), _num, 2);
			if (nextBoard.size() < 2 && prevBoard.size() == 2) {
				prevBoard = blogService.getCommendPrevBoard(board.getCategory(), _num, (4 - nextBoard.size()));
			} else if (nextBoard.size() == 2 && prevBoard.size() < 2) {
				nextBoard = blogService.getCommendNextBoard(board.getCategory(), _num, (4 - prevBoard.size()));
			}

			// 방문자 수 증가
			Cookie[] cookies = req.getCookies();
			countVisitor(cookies, resp, userAgent, _num);

			List<Blog_Category> category = blogService.getCategory();
			List<Blog_Count> countList = blogService.getCount();

			model.addAttribute("countList", countList);
			model.addAttribute("comment", comment);
			model.addAttribute("recomment", recomment);
			model.addAttribute("nextBoard", nextBoard);
			model.addAttribute("prevBoard", prevBoard);
			model.addAttribute("url", "index");
			model.addAttribute("categorys", category);
			model.addAttribute("url", "detail/" + num);

			return "Blog.nonaside.detail";
		}

	}

	// 게시글 목록 반환
	@RequestMapping(value = { "list", "list/{category}" })
	public String blogList(HttpServletRequest req, Model model,
			@PathVariable(required = false, value = "category") String category,
			@RequestParam(defaultValue = "1", name = "page") int page,
			@RequestParam(defaultValue = "", name = "title") String title) {

		category = (category == null) ? "" : category;

		List<Blog_Board> list = blogService.getBoardList(category, title, page, "Y");
		List<Blog_Board> commend = blogService.getCommendBoard();
		int count = blogService.getBoardCount(category, title, "Y");

		if (list.isEmpty()) {
			req.setAttribute("msg", "게시글이 없습니다..");
			req.setAttribute("url", "/index");
			return "alert";
		}

		List<Blog_Category> _category = blogService.getCategory();
		List<Blog_Count> countList = blogService.getCount();

		model.addAttribute("page", page);
		model.addAttribute("countList", countList);
		model.addAttribute("categorys", _category);
		model.addAttribute("category", category);
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("commend", commend);
		model.addAttribute("url", "list/" + category);

		return "Blog.list";
	}

	// 게시글 목록 추가
	@PostMapping("AddList")
	@ResponseBody
	public String AddList(@RequestBody Map<String, Object> form) {

		JSONObject add_list = new JSONObject();

		List<Blog_Board> list = blogService.getBoardList((int) form.get("page"), "Y");

		if (list.isEmpty()) {
			add_list.put("result", 0);
			add_list.put("list", list);
			return add_list.toString();
		} else {
			add_list.put("result", 1);
			add_list.put("list", list);
			return add_list.toString();
		}

	}

	// =============================== 댓글관련 함수 =============================

	@PostMapping("comment")
	@ResponseBody
	public ResponseEntity<ResponseDTO> addComment(@RequestBody Map<String, Object> form, HttpServletRequest request) {

		LOG.info("댓글 작성 요청 시작");

		// 댓글작성
		int ref = (int) form.get("ref");
		int ref_comment = (int) form.get("ref_comment");
		String pwd = ((String) form.get("pwd") == null) ? "" : (String) form.get("pwd");
		String username = (String) form.get("user_name");
		String user_email = (String) form.get("email");

		// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();
		String accToken = null;
		ResponseDTO responseDTO = new ResponseDTO();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
				if ("AccToken".equals(cookie.getName())) {
					accToken = cookie.getValue();
					break; // 원하는 쿠키를 찾았으므로 루프를 종료합니다.
				}
			}
		}

		if (accToken != null && !accToken.equals("")) {
			// accToken 검증
			if (!checkToken(accToken, null)) {
				responseDTO.setPurpose("Validation Failed"); // 실패 목적 설정
				responseDTO.setMessage("토큰이 유효하지 않습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
			}

			String user_id = jwtProvider.getUsernameFromToken(accToken);
			BlogUser user = userService.getUser(user_id);
			username = user.getName() + "HAKO_DEV_USER";
			pwd = user.getId();
			if (user_email.equals("HAKO_DEV_USER")) {
				user_email = user.getEmail();
			}
		}

		pwd = BCrypt.hashpw(pwd, BCrypt.gensalt());

		if (!pwd.equals("HAKO_DEV_USER") && (username.equals("HAKO") || username.equals("NULL"))) {
			responseDTO.setPurpose("Check Value");
			responseDTO.setMessage("사용불가 닉네임 입니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);

		}

		Blog_Comment comment = new Blog_Comment();
		comment.setRef(ref);
		comment.setRef_comment(ref_comment);
		comment.setUser_name(cleanXSS(username));
		comment.setPwd(pwd);
		comment.setComment(cleanXSS((String) form.get("comment")).replace("\n", "<br/>"));
		comment.setEmail(user_email);

		try {
			if (blogService.insertComment(comment) == 1) {
				LOG.info("댓글 작성 요청 성공");
				blogService.addComment(ref);
				responseDTO.setPurpose("Success");
				responseDTO.setMessage("댓글 작성에 성공했습니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);

			} else {
				LOG.info("댓글 작성 요청 실패");
				responseDTO.setPurpose("Error");
				responseDTO.setMessage("서버저장 오류발생");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);

			}
		} catch (Exception e) {
			LOG.error("댓글 작성 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버저장 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);

		}

	}

	@PostMapping("del-comment")
	@ResponseBody
	public ResponseEntity<ResponseDTO> delComment(@RequestBody Map<String, Object> form, HttpServletRequest request) {

		LOG.info("댓글 삭제 요청 시작");

		// 댓글삭제
		String pwd = ((String) form.get("pwd") == null) ? "" : (String) form.get("pwd");
		int num = (int) form.get("num");
		int ref = (int) form.get("ref");
		Blog_Comment comment = new Blog_Comment();

		// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();
		String accToken = null;
		ResponseDTO responseDTO = new ResponseDTO();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
				if ("AccToken".equals(cookie.getName())) {
					accToken = cookie.getValue();
					break; // 원하는 쿠키를 찾았으므로 루프를 종료합니다.
				}
			}
		}

		if (accToken != null && !accToken.equals("")) {
			// accToken 검증
			if (!checkToken(accToken, null)) {
				responseDTO.setPurpose("Validation Failed"); // 실패 목적 설정
				responseDTO.setMessage("토큰이 유효하지 않습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
			}

			String user_id = jwtProvider.getUsernameFromToken(accToken);
			BlogUser user = userService.getUser(user_id);
			pwd = user.getId();

		}

		comment.setNum(num);
		comment.setPwd(pwd);

		try {
			int result = blogService.delComment(comment);

			if (result == 1 || result == 3) {
				LOG.info("댓글 삭제 요청 성공");
				responseDTO.setPurpose("Success");
				responseDTO.setMessage("댓글 삭제에 성공했습니다." + result);
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			} else {
				LOG.info("댓글 삭제 요청 실패");
				responseDTO.setPurpose("Error");
				responseDTO.setMessage("댓글 삭제 오류발생");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
			}

		} catch (Exception e) {
			LOG.error("댓글 삭제 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("댓글 삭제 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);

		}

	}

	@PutMapping("comment")
	@ResponseBody
	public ResponseEntity<ResponseDTO> updateComment(@RequestBody Map<String, Object> form,
			HttpServletRequest request) {

		LOG.info("댓글 수정 요청 시작");

		// 댓글수정
		String pwd = ((String) form.get("pwd") == null) ? "" : (String) form.get("pwd");
		int num = (int) form.get("num");
		String username = (String) form.get("user_name");

		// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();
		String accToken = null;
		ResponseDTO responseDTO = new ResponseDTO();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
				if ("AccToken".equals(cookie.getName())) {
					accToken = cookie.getValue();
					break; // 원하는 쿠키를 찾았으므로 루프를 종료합니다.
				}
			}
		}

		if (accToken != null && !accToken.equals("")) {
			// accToken 검증
			if (!checkToken(accToken, null)) {
				responseDTO.setPurpose("Validation Failed"); // 실패 목적 설정
				responseDTO.setMessage("토큰이 유효하지 않습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
			}

			String user_id = jwtProvider.getUsernameFromToken(accToken);
			BlogUser user = userService.getUser(user_id);
			username = user.getName() + "HAKO_DEV_USER";
			pwd = user.getId();

		}

		if (!pwd.equals("HAKO_DEV_USER") && (username.equals("HAKO") || username.equals("NULL"))) {

			responseDTO.setPurpose("Check Value");
			responseDTO.setMessage("사용불가 닉네임 입니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);

		}

		Blog_Comment comment = new Blog_Comment();

		comment.setUser_name(cleanXSS(username));
		comment.setComment(cleanXSS((String) form.get("comment")).replace("\n", "<br/>"));
		comment.setNum(num);
		comment.setPwd(pwd);

		try {
			if (blogService.updateComment(comment) == 1) {
				LOG.info("댓글 수정 요청 성공");

				responseDTO.setPurpose("Success");
				responseDTO.setMessage("댓글 수정에 성공했습니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);

			} else {
				LOG.info("댓글 수정 요청 실패");
				responseDTO.setPurpose("Error");
				responseDTO.setMessage("댓글 수정 오류발생");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);

			}
		} catch (Exception e) {
			LOG.error("댓글 수정 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("댓글 수정 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);

		}

	}

	// 이메일 전송
	// form 구성
	// 유저명, 댓글 내용, 대댓글 여부, 게시글 번호
	@PostMapping("SendEmail")
	@ResponseBody
	public void sendEmail(@RequestBody Map<String, Object> form, HttpServletRequest request) {

		// 일반 유저일 경우 블로그 주인에게 메일 전송
		if (!form.get("user_name").equals("HAKO")) {

			String adress = "edwsqa987@gmail.com";
			String title = form.get("ref") + "번글에 댓글이 달렸습니다";
			String contents = "댓글 : " + cleanXSS((String) form.get("comment")) + "\nhttps://sirobako.co.kr/detail/"
					+ form.get("ref");

			sendEmail(adress, title, contents);
		}

		int ref_comment = (int) form.get("ref_comment");
		String user_email = (String) form.get("email");
		// 댓글에 이메일 반환 설정을 해놨을 경우 댓글 전송
		if (ref_comment != 0) {
			List<String> FeedBackEmail = blogService.feedBackComment(ref_comment);

			if (user_email.equals("HAKO_DEV_USER")) {
				// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
				Cookie[] cookies = request.getCookies();
				String accToken = null;

				if (cookies != null) {
					for (Cookie cookie : cookies) {
						// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
						if ("AccToken".equals(cookie.getName())) {
							accToken = cookie.getValue();
							break; // 원하는 쿠키를 찾았으므로 루프를 종료합니다.
						}
					}
				}

				// accToken 검증
				if (checkToken(accToken, null)) {
					String user_id = jwtProvider.getUsernameFromToken(accToken);
					BlogUser user = userService.getUser(user_id);
					user_email = user.getEmail();
				}

			}
			// 중복 이메일은 한번만
			Set<String> sentEmails = new HashSet<>();
			for (String email : FeedBackEmail) {
				if (!email.equals(user_email) && !email.equals("") && !sentEmails.contains(email)) {

					String title = "대댓글이 달렸습니다";
					String contents = "댓글 : " + cleanXSS((String) form.get("comment"))
							+ "\nhttps://sirobako.co.kr/detail/" + form.get("ref");

					sendEmail(email, title, contents);
					sentEmails.add(email);
				}
			}
		}
	}

	public void sendEmail(String adress, String title, String contents) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(adress); // 수신자 설정
		message.setSubject(title); // 메일 제목 설정
		message.setText(contents);
		sender.send(message);

	}

	// =============================== 기능 함수 =============================
	// XSS 방지 코드
	private String cleanXSS(String value) {
		// You'll need to remove the spaces from the html entities below
		value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
		value = value.replaceAll("'", "&#39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		return value;
	}

	// 크롤러 확인 후 방문자 수 증가
	public void countVisitor(Cookie[] cookies, HttpServletResponse resp, String userAgent, int board_num) {
		boolean isCrawling = shouldCrawl(userAgent);

		if (!isCrawling) {

			// 조회수 증가 여부 확인
			boolean increaseCount = true;
			boolean increaseVisitorCount = true;

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					String cookieName = cookie.getName();
					if (cookieName.equals("visited" + board_num)) {
						increaseCount = false;
						break;
					}
				}
				for (Cookie cookie : cookies) {
					String cookieName = cookie.getName();
					if (cookieName.equals("visited")) {
						increaseVisitorCount = false;
						break;
					}
				}

			}

			// 조회수 증가
			if (increaseCount) {
				blogService.addCount(board_num);
				blogService.addTotalCount("TODAY_COUNT", 1);
				Cookie cookie = new Cookie("visited" + board_num, "1");
				cookie.setMaxAge(60 * 60);
				cookie.setPath("/");
				resp.addCookie(cookie);
			}

			// 방문자 수 증가
			if (increaseVisitorCount) {
				blogService.addTotalCount("TODAY_VISITER", 1);
				Cookie cookie = new Cookie("visited", "1");
				cookie.setMaxAge(getSecondsUntilEndOfDay());
				cookie.setPath("/");
				resp.addCookie(cookie);
			}
		}
	}

	// 크롤러 제외
	private boolean shouldCrawl(String userAgent) {
		// User-Agent를 분석하여 크롤링 여부를 결정하는 로직 작성
		// 예를 들어, 특정 User-Agent 패턴을 가진 요청만 크롤링으로 허용할 수 있습니다.
		// 필요에 따라 쿠키 정보 등 다른 요소도 고려할 수 있습니다.
		// 크롤링을 허용하는 경우 true, 제한하는 경우 false를 반환합니다.
		// 이 예시에서는 User-Agent가 "CrawlerBot"으로 시작하는 경우에만 크롤링으로 허용하도록 설정하였습니다.
		String agent = userAgent.toLowerCase();
		return agent == null || agent.contains("bot") || agent.contains("spider") || agent.contains("crawler")
				|| agent.contains("kakao") || agent.contains("daum") || agent.contains("google")
				|| agent.contains("naver") || agent.contains("ias-") || agent.contains("iframely")
				|| agent.contains("notion") || agent.contains("facebook") || agent.contains("scalaj-http")
				|| agent.contains("ttd-content") || agent.contains("lighthouse") || agent.contains("censysinspect");
	}

	// 현재 시간 초로 반환
	private int getSecondsUntilEndOfDay() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime endOfDay = now.with(LocalTime.MAX);
		Duration duration = Duration.between(now, endOfDay);
		long seconds = duration.getSeconds();
		return (int) seconds;
	}

	// Token 인증
	private boolean checkToken(String accToken, String refToken) {

		String user_id = "";
		boolean valid = false;
		if (accToken != null) {
			valid = jwtProvider.validateToken(accToken).equals("valid");
			if (valid) {
				user_id = jwtProvider.getUsernameFromToken(accToken);
			}
		} else {
			valid = jwtProvider.validateToken(refToken).equals("valid");
			if (valid) {
				user_id = jwtProvider.getUsernameFromToken(refToken);
			}
		}

		// 토큰 유효성 검사
		if (!valid && !userService.checkToken(accToken, refToken, user_id)) {
			return false;
		} else {
			return true;
		}

	}

}
