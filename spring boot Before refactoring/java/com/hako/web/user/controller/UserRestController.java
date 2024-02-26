package com.hako.web.user.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

import com.hako.web.blog.service.BlogService;
import com.hako.web.config.jwt.JwtProvider;
import com.hako.web.user.dto.ResponseDTO;
import com.hako.web.user.dto.UserDTO;
import com.hako.web.user.entity.BlogToken;
import com.hako.web.user.entity.BlogUser;
import com.hako.web.user.service.KakaoService;
import com.hako.web.user.service.UserService;

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
	private BlogService blogService;

	private Logger LOG = LogManager.getLogger(UserRestController.class);

	// 회원가입 *
	@PostMapping("/user")
	public ResponseEntity<ResponseDTO> createUser(@RequestBody HashMap<String, Object> form) {

		LOG.info("회원가입 요청 시작");

		ResponseDTO responseDTO = new ResponseDTO();
		int result = 0;

		// form 데이터 검증 부분
		String[] valueKey = { "아이디", "닉네임", "비밀번호", "이메일" };
		String[] value = { "user_id", "user_name", "user_pwd", "user_email" };
		if ((result = checkValue(value, form)) != -1) {
			responseDTO.setPurpose("Empty Value");
			responseDTO.setMessage(valueKey[result] + "값이 없습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
		}

		String user_id = (String) form.get("user_id");
		String user_name = (String) form.get("user_name");
		String user_pwd = (String) form.get("user_pwd");
		String user_email = (String) form.get("user_email");

		// 서버통신 부분
		try {

			// 각종 형식및 중복 체크
			if ((result = checkId(user_id)) != 1) {
				responseDTO.setPurpose("Check Value");
				if (result == -1) {
					responseDTO.setPurpose("Conflict");
					responseDTO.setMessage("중복된 아이디 입니다.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
				} else {
					responseDTO.setPurpose("Check Value");
					responseDTO.setMessage("아이디값의 형식을 확인해주세요");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
				}
			}
			if ((result = checkName(user_name, null)) != 1) {
				if (result == -1) {
					responseDTO.setPurpose("Conflict");
					responseDTO.setMessage("중복된 닉네임 입니다.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
				} else {
					responseDTO.setPurpose("Check Value");
					responseDTO.setMessage("닉네임값의 형식을 확인해주세요");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
				}
			}
			if ((result = checkEmail(user_email, null)) != 1) {
				if (result == -1) {
					responseDTO.setPurpose("Conflict");
					responseDTO.setMessage("중복된 이메일 입니다.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
				} else {
					responseDTO.setPurpose("Check Value");
					responseDTO.setMessage("이메일값의 형식을 확인해주세요");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
				}
			}

			if ((result = checkPassword(user_pwd)) != 1) {
				responseDTO.setPurpose("Check Value");
				responseDTO.setMessage("비밀번호의 형식을 확인해주세요");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
			}
			user_pwd = BCrypt.hashpw(user_pwd, BCrypt.gensalt());

			BlogUser user = new BlogUser(user_id, user_name, user_pwd, user_email, new Date());

			// 회원가입
			if (userService.insertUser(user)) {
				LOG.info("회원가입 요청 성공");
				responseDTO.setPurpose("Success");
				responseDTO.setMessage("회원가입에 성공했습니다.");

				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			} else {
				LOG.info("회원가입 실패");
				responseDTO.setPurpose("Error");
				responseDTO.setMessage("서버저장 오류발생");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
			}

		} catch (Exception e) {
			LOG.error("회원가입 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버저장 심각한 오류발생" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}

	}

	// 회원탈퇴*
	@DeleteMapping("/user")
	public ResponseEntity<ResponseDTO> deleteUser(HttpServletRequest request) {

		LOG.info("회원탈퇴 요청 시작");

		ResponseDTO responseDTO = new ResponseDTO();

		String accToken = null;

		// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
				if ("AccToken".equals(cookie.getName())) {
					accToken = cookie.getValue();
					break; // 원하는 쿠키를 찾았으므로 루프를 종료합니다.
				}
			}
		}
		// 회원탈퇴
		try {

			// accToken 검증
			if (!checkToken(accToken, null)) {
				responseDTO.setPurpose("Validation Failed"); // 실패 목적 설정
				responseDTO.setMessage("토큰이 유효하지 않습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
			}

			String user_id = jwtProvider.getUsernameFromToken(accToken);
			BlogUser user = userService.getUser(user_id);

			if (userService.deleteUser(user_id)) {
				LOG.info("회원탈퇴 성공");
				if (BCrypt.checkpw("kakaoUser", user.getPwd())) {
					kakaoService.delKakaoUser(user_id);
				}
				responseDTO.setPurpose("Success");
				responseDTO.setMessage("회원탈퇴에 성공했습니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);

			} else {
				LOG.info("회원탈퇴 실패");
				responseDTO.setPurpose("Error");
				responseDTO.setMessage("서버저장 오류발생");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
			}

		} catch (Exception e) {
			LOG.info("회원탈퇴 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버저장 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}

	}

	// 회원 정보 수정*
	@PutMapping("/user")
	public ResponseEntity<ResponseDTO> updateUser(@RequestBody HashMap<String, Object> form,
			HttpServletRequest request) {
		LOG.info("회원정보 수정요청 시작");

		ResponseDTO responseDTO = new ResponseDTO();
		int result = 0;
		String accToken = null;

		// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
				if ("AccToken".equals(cookie.getName())) {
					accToken = cookie.getValue();
					break; // 원하는 쿠키를 찾았으므로 루프를 종료합니다.
				}
			}
		}
		// 서버통신 부분
		try {
			// accToken 검증
			if (!checkToken(accToken, null)) {
				responseDTO.setPurpose("Validation Failed"); // 실패 목적 설정
				responseDTO.setMessage("토큰이 유효하지 않습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
			}

			String user_id = jwtProvider.getUsernameFromToken(accToken);
			String user_name = (String) form.get("user_name");
			String user_email = (String) form.get("user_email");

			// 각종 형식및 중복 체크
			if ((result = checkName(user_name, user_id)) != 1) {
				if (result == -1) {
					responseDTO.setPurpose("Conflict");
					responseDTO.setMessage("중복된 닉네임 입니다.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
				} else {
					responseDTO.setPurpose("Check Value");
					responseDTO.setMessage("닉네임값의 형식을 확인해주세요");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
				}
			}
			if ((result = checkEmail(user_email, user_id)) != 1) {
				if (result == -1) {
					responseDTO.setPurpose("Conflict");
					responseDTO.setMessage("중복된 이메일 입니다.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
				} else {
					responseDTO.setPurpose("Check Value");
					responseDTO.setMessage("이메일값의 형식을 확인해주세요");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
				}
			}

			BlogUser user = new BlogUser(user_id, user_name, null, user_email, null);
			BlogUser oldUser = userService.getUser(user_id);
			String uT = "HAKO_DEV_USER";
			String oldName = oldUser.getName() + uT;
			String oldEmail = oldUser.getEmail();

			// 회원정보 수정
			if (userService.updateUser(user)) {

				blogService.updateCommentName(oldName, user_name + uT);
				blogService.updateCommentEmail(oldEmail, user_email);

				LOG.info("회원정보 수정 성공");
				responseDTO.setPurpose("Success");
				responseDTO.setMessage("회원정보 수정에 성공했습니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			} else {
				LOG.info("회원정보 수정 실패");
				responseDTO.setPurpose("Error");
				responseDTO.setMessage("서버저장 오류발생");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
			}

		} catch (Exception e) {
			LOG.error("회원정보 수정 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버저장 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	// 회원 정보 반환 *
	@GetMapping("/user")
	public ResponseEntity<UserDTO> getUser(HttpServletRequest request) {
		LOG.info("회원정보 반환요청 시작");

		String accToken = null;
		UserDTO userDTO = new UserDTO();

		// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
				if ("AccToken".equals(cookie.getName())) {
					accToken = cookie.getValue();
					break; // 원하는 쿠키를 찾았으므로 루프를 종료합니다.
				}
			}
		}

		// 회원 정보 반환
		try {

			// accToken 검증
			if (!accToken.equals("") &&!checkToken(accToken, null)) {
				userDTO.setPurpose("Validation Failed"); // 실패 목적 설정
				userDTO.setMessage("토큰이 유효하지 않습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userDTO);
			}else if(accToken.equals("")) {
				userDTO.setPurpose("Empty Value"); // 실패 목적 설정
				userDTO.setMessage("토큰이 없습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userDTO);
			}

			String user_id = jwtProvider.getUsernameFromToken(accToken);
			BlogUser blogUser = new BlogUser();
			if ((blogUser = userService.getUser(user_id)) != null) {
				LOG.info("회원정보 반환 성공");
				userDTO.setUser_name(blogUser.getName());
				userDTO.setUser_email(blogUser.getEmail());
				userDTO.setPurpose("Success");
				userDTO.setMessage("회원정보 조회에 성공했습니다.");
				return ResponseEntity.status(HttpStatus.OK).body(userDTO);
			} else {
				LOG.info("회원정보 반환 실패");
				userDTO.setPurpose("Error");
				userDTO.setMessage("서버조회 오류발생");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(userDTO);
			}

		} catch (Exception e) {
			LOG.error("회원정보 반환 오류", e);
			userDTO.setPurpose("Error");
			userDTO.setMessage("서버조회 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(userDTO);
		}
	}

	// ID 찾기 email
	@PostMapping("/user/find")
	public ResponseEntity<ResponseDTO> findUser(@RequestBody HashMap<String, Object> form) {
		LOG.info("아이디 찾기 요청 시작");

		ResponseDTO responseDTO = new ResponseDTO();
		String user_email = (String) form.get("user_email");

		// 회원 정보 반환
		try {
			BlogUser blogUser = new BlogUser();
			if ((blogUser = userService.findUser(user_email)) != null) {
				LOG.info("아이디 찾기 성공");

				responseDTO.setPurpose("Success");
				responseDTO.setMessage("회원님의 아이디는 " + blogUser.getId() + "입니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			} else {
				LOG.info("회원정보 반환 실패");
				responseDTO.setPurpose("Not Found");
				responseDTO.setMessage("해당하는 아이디가 없습니다.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
			}

		} catch (

		Exception e) {
			LOG.error("회원정보 반환 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버조회 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	// 비밀번호 재설정 id, email 사용
	@PostMapping("/user/reset")
	public ResponseEntity<ResponseDTO> resetUserPwd(@RequestBody HashMap<String, Object> form) {

		LOG.info("비밀번호 재설정 요청 시작");

		ResponseDTO responseDTO = new ResponseDTO();
		int result = 0;

		// form 데이터 검증 부분
		String[] valueKey = { "아이디", "비밀번호", "이메일" };
		String[] value = { "user_id", "user_pwd", "user_email" };
		if ((result = checkValue(value, form)) != -1) {
			responseDTO.setPurpose("Empty Value");
			responseDTO.setMessage(valueKey[result] + "값이 없습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
		}

		String user_id = (String) form.get("user_id");
		String user_pwd = (String) form.get("user_pwd");
		String user_email = (String) form.get("user_email");

		// 서버통신 부분
		try {

			user_pwd = BCrypt.hashpw(user_pwd, BCrypt.gensalt());
			BlogUser user = userService.getUser(user_id);
			if (user.getEmail().equals(user_email)) {

				user = new BlogUser(user_id, null, user_pwd, user_email, null);

				// 회원가입
				if (userService.updateUser(user)) {
					LOG.info("비밀번호 재설정 성공");
					responseDTO.setPurpose("Success");
					responseDTO.setMessage("비밀번호 재설정에 성공했습니다.");

					return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
				} else {
					LOG.info("비밀번호 재설정 실패");
					responseDTO.setPurpose("Error");
					responseDTO.setMessage("서버저장 오류발생");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
				}
			} else {
				responseDTO.setPurpose("NOT_FOUND");
				responseDTO.setMessage("일치하는 계정이 존재하지 않습니다.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
			}

		} catch (Exception e) {
			LOG.error("회원가입 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버저장 심각한 오류발생" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}

	}

	// 토큰 발급(로그인) *
	@PostMapping("/token")
	public ResponseEntity<ResponseDTO> createToken(@RequestBody HashMap<String, Object> form,
			HttpServletResponse response) {

		LOG.info("로그인 요청 시작");
		ResponseDTO ResponseDTO = new ResponseDTO();
		int result = 0;

		// form 데이터 검증 부분
		String[] valueKey = { "아이디", "비밀번호" };
		String[] value = { "user_id", "user_pwd" };
		if ((result = checkValue(value, form)) != -1) {
			ResponseDTO.setPurpose("Empty Value");
			ResponseDTO.setMessage(valueKey[result] + "값이 없습니다.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO);
		}

		try {

			String user_id = (String) form.get("user_id");
			String user_pwd = (String) form.get("user_pwd");

			// 로그인확인
			if ((result = userService.loginUser(user_id, user_pwd)) == 1) {
				// 성공시 토큰 생성
				BlogToken blogToken = new BlogToken();
				Map<String, String> Token = jwtProvider.generateTokenSet(user_id);
				blogToken.setAccess_token(Token.get("accessToken"));
				blogToken.setRefresh_token(Token.get("refreshToken"));
				blogToken.setUser_id(user_id);

				if (userService.saveToken(blogToken)) {
					LOG.info("로그인 성공");

					Cookie cookie = new Cookie("AccToken", Token.get("accessToken"));
					cookie.setPath("/");
					cookie.setHttpOnly(true);
					cookie.setMaxAge(3600);
					response.addCookie(cookie);

					cookie = new Cookie("RefToken", Token.get("refreshToken"));
					cookie.setPath("/");
					cookie.setHttpOnly(true);
					response.addCookie(cookie);

					ResponseDTO.setPurpose("Success");
					ResponseDTO.setMessage("로그인에 성공했습니다.");
					return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO);
				} else {
					LOG.info("로그인 실패");
					ResponseDTO.setPurpose("Error");
					ResponseDTO.setMessage("서버 오류발생");
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO);
				}
			} else {
				LOG.info("로그인 실패");
				// 실패시
				ResponseDTO.setPurpose("Fail Login");
				if (result == 0) {
					ResponseDTO.setMessage("아이디가 존재하지 않습니다.");

				} else if (result == -1) {
					ResponseDTO.setMessage("아이디 또는 비밀번호가 틀렸습니다.");
				}
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO);

			}

		} catch (Exception e) {
			LOG.error("로그인 오류", e);
			// TODO: handle exception
			ResponseDTO.setPurpose("Error");
			ResponseDTO.setMessage("서버 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO);
		}

	}

	// 토큰 삭제(로그아웃) *
	@DeleteMapping("/token")
	public ResponseEntity<ResponseDTO> deleteToken(HttpServletRequest request, HttpServletResponse response) {
		LOG.info("로그아웃 요청 시작");
		ResponseDTO responseDTO = new ResponseDTO();

		String accToken = null;

		// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("AccToken".equals(cookie.getName())) {
					accToken = cookie.getValue();
					break;
				}
			}
		}

		// 쿠키 여부상관없이 쿠키는 삭제
		Cookie cookie = new Cookie("AccToken", "");
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		response.addCookie(cookie);

		cookie = new Cookie("RefToken", "");
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		response.addCookie(cookie);

		// 로그아웃
		try {
			// accToken 검증
			if (!checkToken(accToken, null)) {

				responseDTO.setPurpose("Validation Failed"); // 실패 목적 설정
				responseDTO.setMessage("토큰이 유효하지 않습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
			}

			String user_id = jwtProvider.getUsernameFromToken(accToken);
			if (userService.deleteToken(user_id)) {

				LOG.info("로그아웃 성공");
				responseDTO.setPurpose("Success");
				responseDTO.setMessage("로그아웃에 성공했습니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			} else {
				LOG.info("로그아웃 실패");
				responseDTO.setPurpose("Error");
				responseDTO.setMessage("서버저장 오류발생");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
			}

		} catch (Exception e) {
			LOG.error("로그아웃 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버저장 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	// 토큰 재발급(주기적으로 보안)*
	@PutMapping("/token")
	public ResponseEntity<ResponseDTO> updateToken(HttpServletRequest request, HttpServletResponse response) {
		LOG.info("토큰 재발급 요청 시작");
		ResponseDTO responseDTO = new ResponseDTO();

		String refToken = null;

		// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("RefToken".equals(cookie.getName())) {
					refToken = cookie.getValue();
					break;
				}
			}
		}

		// 서버통신 부분
		try {
			// refToken 검증
			if (!refToken.equals("") &&!checkToken(null, refToken)) {

				Cookie cookie = new Cookie("AccToken", "");
				cookie.setPath("/");
				cookie.setHttpOnly(true);
				response.addCookie(cookie);

				cookie = new Cookie("RefToken", "");
				cookie.setPath("/");
				cookie.setHttpOnly(true);
				response.addCookie(cookie);

				responseDTO.setPurpose("Validation Failed"); // 실패 목적 설정
				responseDTO.setMessage("토큰이 유효하지 않습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);

			}else if(refToken.equals("")){
				responseDTO.setPurpose("Empty Value"); // 실패 목적 설정
				responseDTO.setMessage("토큰이 없습니다."); // 실패 메시지 설정
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
			}

			String user_id = jwtProvider.getUsernameFromToken(refToken);

			// 성공시 토큰 생성
			BlogToken blogToken = new BlogToken();

			Map<String, String> Token = jwtProvider.generateTokenSet(user_id);
			blogToken.setAccess_token(Token.get("accessToken"));
			blogToken.setRefresh_token(Token.get("refreshToken"));
			blogToken.setUser_id(user_id);

			if (userService.saveToken(blogToken)) {
				LOG.info("토큰 재발급 성공");

				Cookie cookie = new Cookie("AccToken", Token.get("accessToken"));
				cookie.setPath("/");
				cookie.setHttpOnly(true);
				response.addCookie(cookie);

				cookie = new Cookie("RefToken", Token.get("refreshToken"));
				cookie.setPath("/");
				cookie.setHttpOnly(true);
				response.addCookie(cookie);

				responseDTO.setPurpose("Success");
				responseDTO.setMessage("토큰 재발급에 성공했습니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			} else {
				LOG.info("토큰 재발급 실패");
				responseDTO.setPurpose("Error");
				responseDTO.setMessage("서버 오류발생");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
			}

		} catch (

		Exception e) {
			LOG.error("토큰 재발급 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버 오류발생");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	// 중복 아이디 확인 rest
	@PostMapping("/check-id")
	public ResponseEntity<ResponseDTO> checkUserId(@RequestBody HashMap<String, Object> form) {

//		LOG.info("아이디 중복 확인 요청시작");

		ResponseDTO responseDTO = new ResponseDTO();
		int result = 0;
		// 서버통신 부분
		try {
			String user_id = (String) form.get("user_id");

			// 각종 형식및 중복 체크
			if ((result = checkId(user_id)) != 1) {
				responseDTO.setPurpose("Check Value");
				if (result == -1) {
					responseDTO.setPurpose("Conflict");
					responseDTO.setMessage("중복된 아이디 입니다.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
				} else {
					responseDTO.setPurpose("Check Value");
					responseDTO.setMessage("8~15자 이내 영문,숫자만");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
				}
			} else {
//				LOG.info("아이디 중복 확인 성공");
				responseDTO.setPurpose("Success");
				responseDTO.setMessage("사용가능한 아이디 입니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			}

		} catch (Exception e) {
			LOG.error("아이디 중복 확인 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버조회 심각한 오류발생" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}

	}

	// 중복 닉네임 확인 rest
	@PostMapping("/check-name")
	public ResponseEntity<ResponseDTO> checkUserName(@RequestBody HashMap<String, Object> form,
			HttpServletRequest request) {

//			LOG.info("닉네임 중복 확인 요청시작");

		ResponseDTO responseDTO = new ResponseDTO();
		int result = 0;
		// 서버통신 부분
		try {
			String user_name = (String) form.get("user_name");
			String user_id = (String) form.get("user_id");

			String accToken = null;

			if (!user_id.equals("")) {

				// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
				Cookie[] cookies = request.getCookies();

				if (cookies != null) {
					for (Cookie cookie : cookies) {
						// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
						if ("AccToken".equals(cookie.getName())) {
							accToken = cookie.getValue();
							break; // 원하는 쿠키를 찾았으므로 루프를 종료합니다.
						}
					}
				}
				user_id = jwtProvider.getUsernameFromToken(accToken);
			}
			// 각종 형식및 중복 체크
			if ((result = checkName(user_name, user_id)) != 1) {
				responseDTO.setPurpose("Check Value");
				if (result == -1) {
					responseDTO.setPurpose("Conflict");
					responseDTO.setMessage("중복된 닉네임 입니다.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
				} else {
					responseDTO.setPurpose("Check Value");
					responseDTO.setMessage("2~10자 이내 영문,숫자,한글만");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
				}
			} else {
//					LOG.info("닉네임 중복 확인 성공");
				responseDTO.setPurpose("Success");
				responseDTO.setMessage("사용가능한 닉네임 입니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			}

		} catch (Exception e) {
			LOG.error("닉네임 중복 확인 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버조회 심각한 오류발생" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}

	}

	// 중복 이메일 확인 rest
	@PostMapping("/check-email")
	public ResponseEntity<ResponseDTO> checkUserEmail(@RequestBody HashMap<String, Object> form,
			HttpServletRequest request) {

//				LOG.info("닉네임 중복 확인 요청시작");

		ResponseDTO responseDTO = new ResponseDTO();
		int result = 0;
		// 서버통신 부분
		try {
			String user_email = (String) form.get("user_email");
			String user_id = (String) form.get("user_id");
			String accToken = null;

			if (!user_id.equals("")) {

				// HttpServletRequest를 통해 쿠키 배열을 가져옵니다.
				Cookie[] cookies = request.getCookies();

				if (cookies != null) {
					for (Cookie cookie : cookies) {
						// 쿠키의 이름을 확인하고 원하는 쿠키를 찾습니다.
						if ("AccToken".equals(cookie.getName())) {
							accToken = cookie.getValue();
							break; // 원하는 쿠키를 찾았으므로 루프를 종료합니다.
						}
					}
				}
				user_id = jwtProvider.getUsernameFromToken(accToken);
			}

			// 각종 형식및 중복 체크
			if ((result = checkEmail(user_email, user_id)) != 1) {
				responseDTO.setPurpose("Check Value");
				if (result == -1) {
					responseDTO.setPurpose("Conflict");
					responseDTO.setMessage("중복된 이메일 입니다.");
					return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDTO);
				} else {
					responseDTO.setPurpose("Check Value");
					responseDTO.setMessage("이메일 형식을 확인해주세요.");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
				}
			} else {
//						LOG.info("닉네임 중복 확인 성공");
				responseDTO.setPurpose("Success");
				responseDTO.setMessage("사용가능한 이메일 입니다.");
				return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
			}

		} catch (Exception e) {
			LOG.error("이메일 중복 확인 오류", e);
			responseDTO.setPurpose("Error");
			responseDTO.setMessage("서버조회 심각한 오류발생" + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}

	}

	// ===================== 기능 함수 =====================
	// 객체값들 비었는지 확인
	private int checkValue(String[] checkValue, HashMap<String, Object> form) {

		for (int i = 0; i < checkValue.length; i++) {
			if (!form.containsKey(checkValue[i]) || form.get(checkValue[i]).equals("")) {

				return i;
			}
		}
		return -1;

	}

	// 아이디 유효성 검사 등등
	// 영어 숫자만 가능
	private int checkId(String id) {

		String idPattern = "^[a-zA-Z0-9]{8,15}$";

		if (Pattern.matches(idPattern, id)) {
			if (userService.checkUserId(id)) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return 0;
		}
	}

	// 닉네임 검사 한굴,영어, 숫자만 가능
	private int checkName(String name, String user_id) {
		String namePattern = "^[a-zA-Z0-9가-힣]{2,10}$";
		if (Pattern.matches(namePattern, name)) {
			if (userService.checkUserName(name, user_id)) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return 0;
		}
	}

	// 이메일 형식및 중복 체크
	private int checkEmail(String email, String user_id) {

		if (Pattern.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", email)) {
			if (userService.checkUserEmail(email, user_id)) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return 0;
		}
	}

	// 비밀번호 형식 체크
	private int checkPassword(String userPwd) {
		// 8~15자 이내의 영문자와 숫자만 허용하는 정규표현식
		String passwordPattern = "^[a-zA-Z0-9!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/\\\\|]{8,15}$";

		if (Pattern.matches(passwordPattern, userPwd)) {
			return 1; // 패턴 일치
		} else {
			return 0; // 패턴 불일치
		}
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
