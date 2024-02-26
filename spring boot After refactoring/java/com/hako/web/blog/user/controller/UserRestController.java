package com.hako.web.blog.user.controller;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hako.web.blog.commet.service.CommentService;
import com.hako.web.blog.user.dto.UserDTO;
import com.hako.web.blog.user.entity.BlogToken;
import com.hako.web.blog.user.entity.BlogUser;
import com.hako.web.blog.user.service.KakaoService;
import com.hako.web.blog.user.service.TokenService;
import com.hako.web.blog.user.service.UserService;
import com.hako.web.config.jwt.JwtProvider;
import com.hako.web.util.StringUtils;

@Controller
@ResponseBody
public class UserRestController {

	@Autowired
	private UserService userService;

	@Autowired
	protected JwtProvider jwtProvider;

	@Autowired
	private KakaoService kakaoService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private StringUtils stringUtils;

	private Logger LOG = LogManager.getLogger(UserRestController.class);

	private static String invalidValue;
	private static String FIND_ID;

	public final static String BLOG_USER_TAG = "_BLOG_USER";
	private static final String VALID_TOKEN = "valid";
	private static final String ACC_TOKEN_COOKIE_NAME = "AccToken";
	private static final String REF_TOKEN_COOKIE_NAME = "RefToken";
	private static final int ACC_TOKEN_MAX_AGE = 3600 * 2;
	private static final int REF_TOKEN_MAX_AGE = 3600 * 2 * 24 * 15;
	private static final String INVALID_VALUE_ATTRIBUTE = "invalidValue";

	private static final String INVALID_ID = "ID";
	private static final String INVALID_NAME = "NAME";
	private static final String INVALID_EMAIL = "EMAIL";
	private static final String INVALID_PASSWORD = "PWD";

	private static final String ID_PATTERN = "^[a-zA-Z0-9]{8,15}$";
	private static final String NAME_PATTERN = "^[a-zA-Z0-9가-힣]{2,10}$";
	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
	private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z0-9!@#$%^&*\\(\\)-_=+]).{8,15}$";

	public enum LogMessage {
		LOGIN_REQUEST("로그인 요청 : "), LOGOUT_REQUEST("로그아웃 요청 : "), GET_USER_REQUEST("회원정보 반환요청 : "),
		FIND_USER_REQUEST("아이디 찾기 요청 : "), RESET_USER_PWD_REQUEST("비밀번호 재설정 요청 : "), DELETE_USER_REQUEST("회원탈퇴 요청 : "),
		UPDATE_USER_REQUEST("회원정보 수정요청 : "), SIGNUP_REQUEST("회원가입 요청 : "), ID_DUPLICATION("아이디 중복 확인 요청 : "),
		NAME_DUPLICATION("닉네임 중복 확인 요청 : "), EMAIL_DUPLICATION("이메일 중복 확인 요청 : ");

		private final String description;
		private final String START = "시작";
		private final String SUCCESS = "성공";
		private final String ERROR = "오류";
		private final String FAIL_INVALID_FORM = "form 데이터 부적합 ";
		private final String FAIL_INVALID_ACC_TOKEN = "accToken 부적합";
		private final String FAIL_AUTH_MISMATCH = "비밀번호 부적합";
		private final String NOT_FOUND = "반환정보 없음";

		LogMessage(String description) {
			this.description = description;
		}

		public String start() {
			return description + START;
		}

		public String success() {
			return description + SUCCESS;
		}

		public String failInvalidForm(String invalidValut) {
			return description + FAIL_INVALID_FORM + invalidValut;
		}

		public String failInvalidAccToken() {
			return description + FAIL_INVALID_ACC_TOKEN;
		}

		public String failAuthMisMatch() {
			return description + FAIL_AUTH_MISMATCH;
		}

		public String notFound() {
			return description + NOT_FOUND;
		}

		public String error() {
			return description + ERROR;
		}

	}

	@PostMapping("/user")
	public ResponseEntity<?> insertUser(@RequestBody BlogUser user) {
		LOG.info(LogMessage.SIGNUP_REQUEST.start());
		try {
			if (!isValidUserForm(user)) {
				LOG.info(LogMessage.SIGNUP_REQUEST.failInvalidForm(invalidValue));
				return ResponseEntity.badRequest().header(INVALID_VALUE_ATTRIBUTE, invalidValue).build();
			}
			if (userService.insertUser(user)) {
				LOG.info(LogMessage.SIGNUP_REQUEST.success());
				return ResponseEntity.ok().build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.error(LogMessage.SIGNUP_REQUEST.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/user")
	public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response) {
		LOG.info(LogMessage.DELETE_USER_REQUEST.start());
		try {
			String accToken = getAccTokenFormCookie(request);

			if (!checkAndRenewAccessToken(accToken, request, response)) {
				LOG.info(LogMessage.DELETE_USER_REQUEST.failInvalidAccToken());
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			String userNum = jwtProvider.getUsernameFromToken(accToken);

			BlogUser user = userService.getUser(userNum);
			if (userService.deleteUser(userNum)) {
				LOG.info(LogMessage.DELETE_USER_REQUEST.success());
				if (BCrypt.checkpw("kakaoUser", user.getPwd())) {
					kakaoService.delKakaoUser(user.getId());
				}

				return ResponseEntity.ok().build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.info(LogMessage.DELETE_USER_REQUEST.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PutMapping("/user")
	public ResponseEntity<?> updateUser(@RequestBody BlogUser user, HttpServletRequest request,
			HttpServletResponse response) {
		LOG.info(LogMessage.UPDATE_USER_REQUEST.start());
		try {
			String accToken = getAccTokenFormCookie(request);

			if (!checkAndRenewAccessToken(accToken, request, response)) {
				LOG.info(LogMessage.UPDATE_USER_REQUEST.failInvalidAccToken());
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			String userNum = jwtProvider.getUsernameFromToken(accToken);
			user.setNum(userNum);

			if (!isValidUserUpdateForm(user)) {
				LOG.info(LogMessage.UPDATE_USER_REQUEST.FAIL_INVALID_FORM + invalidValue);
				return ResponseEntity.badRequest().header(INVALID_VALUE_ATTRIBUTE, invalidValue).build();
			}

			BlogUser oldUser = userService.getUser(userNum).deepCopy();

			if (userService.updateUser(user)) {
				LOG.info(LogMessage.UPDATE_USER_REQUEST.success());

				updateCommentInfo(oldUser, user);

				return ResponseEntity.status(HttpStatus.OK).build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.error(LogMessage.UPDATE_USER_REQUEST.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/user")
	public ResponseEntity<?> getUser(HttpServletRequest request, HttpServletResponse response) {
		LOG.info(LogMessage.GET_USER_REQUEST.start());
		try {
			String accToken = getAccTokenFormCookie(request);

			if (!checkAndRenewAccessToken(accToken, request, response)) {
				LOG.info(LogMessage.GET_USER_REQUEST.failInvalidAccToken());
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			String userNum = jwtProvider.getUsernameFromToken(accToken);
			BlogUser blogUser = new BlogUser();
			if ((blogUser = userService.getUser(userNum)) != null) {
				LOG.info(LogMessage.GET_USER_REQUEST.success());

				UserDTO userDTO = new UserDTO();
				userDTO.setUser_name(blogUser.getName());
				userDTO.setUser_email(blogUser.getEmail());

				return ResponseEntity.status(HttpStatus.OK).body(userDTO);
			} else {
				LOG.info(LogMessage.GET_USER_REQUEST.notFound());
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
		} catch (Exception e) {
			LOG.error(LogMessage.GET_USER_REQUEST.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/user/find")
	public ResponseEntity<?> findUser(@RequestBody BlogUser user) {
		LOG.info(LogMessage.FIND_USER_REQUEST.start());
		try {
			if (findEmailSuccessify(user.getEmail())) {
				LOG.info(LogMessage.FIND_USER_REQUEST.success());
				return ResponseEntity.status(HttpStatus.OK).body(FIND_ID);
			} else {
				LOG.info(LogMessage.FIND_USER_REQUEST.notFound());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		} catch (Exception e) {
			LOG.error(LogMessage.FIND_USER_REQUEST.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/user/reset")
	public ResponseEntity<?> resetUserPwd(@RequestBody BlogUser user) {
		LOG.info(LogMessage.RESET_USER_PWD_REQUEST.start());
		try {

			BlogUser resetUser = userService.getUserFromId(user.getId()).deepCopy();

			if (resetUser != null && resetUser.getEmail().equals(user.getEmail())) {
				user.setNum(resetUser.getNum());

				if (userService.updateUser(user)) {
					LOG.info(LogMessage.RESET_USER_PWD_REQUEST.success());
					return ResponseEntity.status(HttpStatus.OK).build();
				} else {
					throw new Exception();
				}
			} else {
				LOG.info(LogMessage.RESET_USER_PWD_REQUEST.notFound());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		} catch (Exception e) {
			LOG.error(LogMessage.RESET_USER_PWD_REQUEST.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/token")
	public ResponseEntity<?> createToken(@RequestBody BlogUser user, HttpServletResponse response) {
		LOG.info(LogMessage.LOGIN_REQUEST.start());

		if (!user.isValidLoginForm()) {
			LOG.info(LogMessage.LOGIN_REQUEST.failInvalidForm(INVALID_ID));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		try {
			if (userService.loginUser(user)) {
				BlogToken token = createToken(user);

				if (tokenService.saveToken(token)) {
					LOG.info(LogMessage.LOGIN_REQUEST.success());
					setTokenCookie(token, response);
					return ResponseEntity.status(HttpStatus.OK).build();
				} else {
					throw new Exception();
				}
			} else {
				LOG.info(LogMessage.LOGIN_REQUEST.failAuthMisMatch());
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} catch (Exception e) {
			LOG.error(LogMessage.LOGIN_REQUEST.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/token")
	public ResponseEntity<?> deleteToken(HttpServletRequest request, HttpServletResponse response) {
		LOG.info(LogMessage.LOGOUT_REQUEST.start());

		try {
			deleteTokenFromCookie(response);

			String accToken = getAccTokenFormCookie(request);

			if (!checkAndRenewAccessToken(accToken, request, response)) {
				LOG.info(LogMessage.LOGOUT_REQUEST.failInvalidAccToken());
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			String userNum = jwtProvider.getUsernameFromToken(accToken);
			if (tokenService.deleteToken(userNum)) {
				LOG.info(LogMessage.LOGOUT_REQUEST.success());
				return ResponseEntity.status(HttpStatus.OK).build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.error(LogMessage.LOGOUT_REQUEST.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/check-id")
	public ResponseEntity<?> cheackDuplicatedId(@RequestBody BlogUser user) {
		try {

			String userId = user.getId();
			if (!stringUtils.isValidPattern(userId, ID_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			} else if (!isDuplicatedId(userId)) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			} else {
				return ResponseEntity.status(HttpStatus.OK).build();
			}
		} catch (Exception e) {
			LOG.error(LogMessage.ID_DUPLICATION.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@PostMapping("/check-name")
	public ResponseEntity<?> cheackDuplicatedName(@RequestBody BlogUser user, HttpServletRequest request) {

		// 서버통신 부분
		try {

			String userName = user.getName();
			String accToken = getAccTokenFormCookie(request);
			String userNum = (accToken != null) ? jwtProvider.getUsernameFromToken(accToken) : null;

			if (!stringUtils.isValidPattern(userName, NAME_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			} else if (!isDuplicatedName(userName, userNum)) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			} else {
				return ResponseEntity.status(HttpStatus.OK).build();
			}

		} catch (Exception e) {
			LOG.error(LogMessage.NAME_DUPLICATION.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/check-email")
	public ResponseEntity<?> cheackDuplicatedEmail(@RequestBody BlogUser user, HttpServletRequest request) {

		// 서버통신 부분
		try {
			String accToken = getAccTokenFormCookie(request);
			String userEmail = user.getEmail();
			String userNum = (accToken != null) ? jwtProvider.getUsernameFromToken(accToken) : null;
			if (!stringUtils.isValidPattern(userEmail, EMAIL_PATTERN)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			} else if (!isDuplicatedEmail(userEmail, userNum)) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			} else {
				return ResponseEntity.status(HttpStatus.OK).build();
			}

		} catch (Exception e) {
			LOG.error(LogMessage.EMAIL_DUPLICATION.error(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	private boolean isValidUserForm(BlogUser user) {
		if (!isValidId(user.getId())) {
			return false;
		}

		if (!isValidName(user.getName())) {
			return false;
		}

		if (!isValidEmail(user.getEmail())) {
			return false;
		}

		if (!isValidPassword(user.getPwd())) {
			return false;
		}

		return true;
	}

	private boolean isValidUserUpdateForm(BlogUser user) {

		if (!isValidName(user.getName(), user.getNumToString())) {
			return false;
		}

		if (!isValidEmail(user.getEmail(), user.getNumToString())) {
			return false;
		}

		return true;
	}

	private boolean isValidId(String id) {
		if (!stringUtils.isValidPattern(id, ID_PATTERN)) {
			invalidValue = INVALID_ID;
			return false;
		}

		if (!isDuplicatedId(id)) {
			invalidValue = INVALID_ID;
			return false;
		}

		return true;
	}

	private boolean isValidName(String name) {
		return isValidName(name, null);
	}

	private boolean isValidName(String name, String userNum) {
		if (!stringUtils.isValidPattern(name, NAME_PATTERN)) {
			invalidValue = INVALID_NAME;
			return false;
		}

		if (!isDuplicatedName(name, userNum)) {
			invalidValue = INVALID_NAME;
			return false;
		}

		return true;
	}

	private boolean isValidEmail(String email) {
		return isValidEmail(email, null);
	}

	private boolean isValidEmail(String email, String userNum) {
		if (!stringUtils.isValidPattern(email, EMAIL_PATTERN)) {
			invalidValue = INVALID_EMAIL;
			return false;
		}

		if (!isDuplicatedEmail(email, userNum)) {
			invalidValue = INVALID_EMAIL;
			return false;
		}

		return true;
	}

	private boolean isValidPassword(String password) {
		if (!stringUtils.isValidPattern(password, PASSWORD_PATTERN)) {
			invalidValue = INVALID_PASSWORD;
			return false;
		}

		return true;
	}

	private boolean isDuplicatedId(String user_id) {
		return userService.isDuplicatedId(user_id);
	}

	private boolean isDuplicatedName(String name, String userNum) {
		return userService.isDuplicatedName(name, userNum);
	}

	private boolean isDuplicatedEmail(String email, String userNum) {
		return userService.isDuplicatedEmail(email, userNum);
	}

	private boolean isValidAccToken(String accToken) {

		String userNum = getUserNumFromJWT(accToken);

		// 토큰 유효성 검사
		if (userNum != null && tokenService.isValidAccToken(accToken, userNum)) {
			return true;
		} else {
			return false;
		}

	}

	private boolean isValidRefToken(String refToken) {

		String userNum = getUserNumFromJWT(refToken);

		// 토큰 유효성 검사
		if (userNum != null && tokenService.isValidRefToken(refToken, userNum)) {
			return true;
		} else {
			return false;
		}

	}

	private String getUserNumFromJWT(String accToken) {
		boolean valid = jwtProvider.validateToken(accToken).equals(VALID_TOKEN);
		if (valid) {
			return jwtProvider.getUsernameFromToken(accToken);
		} else {
			return null;
		}
	}

	public BlogToken createToken(BlogUser user) {
		String userNum = userService.getUserNumFromId(user.getId());
		Map<String, String> token = jwtProvider.generateTokenSet(userNum);
		return new BlogToken(userNum, token.get("accessToken"), token.get("refreshToken"));
	}

	
	public static String getAccTokenFormCookie(HttpServletRequest request) {
		// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
				if (ACC_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static String getRefTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (REF_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public void deleteTokenFromCookie(HttpServletResponse response) {
		deleteCookie(response, ACC_TOKEN_COOKIE_NAME);
		deleteCookie(response, REF_TOKEN_COOKIE_NAME);
	}

	public void setTokenCookie(BlogToken token, HttpServletResponse response) {
		setCookie(response, ACC_TOKEN_COOKIE_NAME, token.getAccess_token(), ACC_TOKEN_MAX_AGE);
		setCookie(response, REF_TOKEN_COOKIE_NAME, token.getRefresh_token(), REF_TOKEN_MAX_AGE);
	}

	private void deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	private boolean checkAndRenewAccessToken(String accToken, HttpServletRequest request,
			HttpServletResponse response) {
		if (!isValidAccToken(accToken)) {
			if (accToken == null) {
				return false;
			}

			String refToken = getRefTokenFromCookie(request);
			if (!isValidRefToken(refToken)) {
				deleteTokenFromCookie(response);
				return false;
			}
			accToken = refreshAccessToken(refToken, response);
		}
		return true;
	}

	private String refreshAccessToken(String refToken, HttpServletResponse response) {
		try {
			BlogToken blogToken = new BlogToken();

			String userNum = jwtProvider.getUsernameFromToken(refToken);
			Map<String, String> token = jwtProvider.generateTokenSet(userNum);
			blogToken.setAccess_token(token.get("accessToken"));
			blogToken.setRefresh_token(token.get("refreshToken"));
			blogToken.setUser_num(userNum);

			if (tokenService.saveToken(blogToken)) {
				return blogToken.getAccess_token();
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		return null;
	}


	private void updateCommentInfo(BlogUser oldUser, BlogUser newUser) {
		String oldName = oldUser.getName() + BLOG_USER_TAG;
		String oldEmail = oldUser.getEmail();
		String newName = newUser.getName() + BLOG_USER_TAG;
		String newEmail = newUser.getEmail();

		commentService.updateCommentName(oldName, newName);
		commentService.updateCommentEmail(oldEmail, newEmail);
	}

	private boolean findEmailSuccessify(String email) {
		BlogUser blogUser = new BlogUser();
		if ((blogUser = userService.findUser(email)) != null) {
			FIND_ID = blogUser.getId();
			return true;
		} else {
			return false;
		}
	}
}
