package com.hako.web.blog.commet.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hako.web.blog.board.service.BoardService;
import com.hako.web.blog.commet.entity.BlogComment;
import com.hako.web.blog.commet.service.CommentService;
import com.hako.web.blog.user.controller.UserRestController;
import com.hako.web.blog.user.entity.BlogUser;
import com.hako.web.blog.user.service.UserService;
import com.hako.web.config.jwt.JwtProvider;
import com.hako.web.util.StringUtils;

@Controller
@RequestMapping("/")
public class CommetRestController {

	@Autowired
	private UserService userService;

	@Autowired
	private BoardService boardService;

	@Autowired
	private CommentService commentService;

	@Autowired
	protected JwtProvider jwtProvider;

	@Autowired
	private StringUtils stringUtils;

	private Logger LOG = LogManager.getLogger(CommetRestController.class);

	private static String invalidValue;
	private static final String INVALID_VALUE_ATTRIBUTE = "invalidValue";

	private static final String NAME_PATTERN = "^[a-zA-Z0-9가-힣]{2,10}$";
	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
	private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z0-9!@#$%^&*\\(\\)-_=+]).{4,}$";

	private static final String INVALID_NAME = "NAME";
	private static final String INVALID_EMAIL = "EMAIL";
	private static final String INVALID_PASSWORD = "PWD";
	private static final String INVALID_COMMENT = "COMMENT";

	private static final String BUSER_TAG = "_BUSER";
	private static final String FEEDBACK_HEADER = "FEEDBACK";
	private static final String YES_FEEDBACK = "Y";

	private static final String START_COMMENT_INSERT_REQUEST = "댓글 작성 요청 시작";
	private static final String START_COMMENT_UPDATE_REQUEST = "댓글 수정 요청 시작";
	private static final String START_COMMENT_DELETE_REQUEST = "댓글 삭제 요청 시작";

	private static final String FAIL_COMMENT_INSERT_REQUEST_INVALID_FORM = "댓글 작성 요청 실패 : 댓글 form 부적합 ";
	private static final String FAIL_COMMENT_UPDATE_REQUEST_INVALID_FORM = "댓글 수정 요청 실패 : 댓글 form 부적합 ";
	private static final String FAIL_COMMENT_INSERT_REQUEST_UNAUTHORIZED_NICKNAME = "댓글 작성 요청 실패 : 사용불가 닉네임";
	private static final String FAIL_COMMENT_UPDATE_REQUEST_UNAUTHORIZED_NICKNAME = "댓글 수정 요청 실패 : 사용불가 닉네임";
	private static final String FAIL_COMMENT_UPDATE_REQUEST_INVALID_PASSWORD = "댓글 수정 요청 실패 : 비밀번호 불일치";
	private static final String FAIL_COMMENT_DELETE_REQUEST_INVALID_PASSWORD = "댓글 삭제 요청 실패 : 비밀번호 불일치";

	private static final String SUCCESS_COMMENT_INSERT_REQUEST = "댓글 작성 요청 성공";
	private static final String SUCCESS_COMMENT_UPDATE_REQUEST = "댓글 수정 요청 성공";
	private static final String SUCCESS_COMMENT_DELETE_REQUEST = "댓글 삭제 요청 성공";

	private static final String ERROR_COMMENT_INSERT = "댓글 작성 오류";
	private static final String ERROR_COMMENT_UPDATE = "댓글 수정 오류";
	private static final String ERROR_COMMENT_DELETE = "댓글 삭제 오류";

	private static final int SUCCESS_CODE = 1;
	private static final int TOP_LEVEL_COMMENT_CODE  = 2;
	private static final int INVALID_PASSWORD_CODE = -1;

	@PostMapping("comment")
	@ResponseBody
	public ResponseEntity<?> insertComment(@RequestBody BlogComment comment, HttpServletRequest request) {
		LOG.info(START_COMMENT_INSERT_REQUEST);
		checkBlogUserAndUpdateComment(comment, request);
		if (!isValidInsertComment(comment)) {
			LOG.info(FAIL_COMMENT_INSERT_REQUEST_INVALID_FORM + invalidValue);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(INVALID_VALUE_ATTRIBUTE, invalidValue).build();
		}

		if (!isValidNicName(comment)) {
			LOG.info(FAIL_COMMENT_INSERT_REQUEST_UNAUTHORIZED_NICKNAME);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			if (commentService.insertComment(comment)) {
				LOG.info(SUCCESS_COMMENT_INSERT_REQUEST);
				boardService.addCommentCount(comment.getBoard_num());
				return ResponseEntity.status(HttpStatus.OK).body(commentService.getLastCommentNum());
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.error(ERROR_COMMENT_INSERT, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PutMapping("comment")
	@ResponseBody
	public ResponseEntity<?> updateComment(@RequestBody BlogComment comment, HttpServletRequest request) {
		LOG.info(START_COMMENT_UPDATE_REQUEST);
		checkBlogUserAndUpdateComment(comment, request);

		if (!isValidUpdateComment(comment)) {
			LOG.info(FAIL_COMMENT_UPDATE_REQUEST_INVALID_FORM + invalidValue);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(INVALID_VALUE_ATTRIBUTE, invalidValue).build();
		}

		if (!isValidNicName(comment)) {
			LOG.info(FAIL_COMMENT_UPDATE_REQUEST_UNAUTHORIZED_NICKNAME);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			int result = commentService.updateComment(comment);
			if (result == SUCCESS_CODE) {
				LOG.info(SUCCESS_COMMENT_UPDATE_REQUEST);
				return ResponseEntity.status(HttpStatus.OK).build();
			} else if (result == INVALID_PASSWORD_CODE) {
				LOG.info(FAIL_COMMENT_UPDATE_REQUEST_INVALID_PASSWORD);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.error(ERROR_COMMENT_UPDATE, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// TOP_LEVEL_COMMENT_CODE 반환은 댓글중 최상위 댓글이 삭제됨음 의미
	@PostMapping("del-comment")
	@ResponseBody
	public ResponseEntity<?> delComment(@RequestBody BlogComment comment, HttpServletRequest request) {
		LOG.info(START_COMMENT_DELETE_REQUEST);
		checkBlogUserAndUpdateComment(comment, request);

		try {
			int result = commentService.deleteComment(comment);
			if (result == SUCCESS_CODE || result == TOP_LEVEL_COMMENT_CODE) {
				LOG.info(SUCCESS_COMMENT_DELETE_REQUEST);
				return ResponseEntity.status(HttpStatus.OK).body(result);
			} else if (result == INVALID_PASSWORD_CODE) {
				LOG.info(FAIL_COMMENT_DELETE_REQUEST_INVALID_PASSWORD);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			LOG.error(ERROR_COMMENT_DELETE, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private boolean isValidNicName(BlogComment comment) {
		String userName = comment.getUser_name();

		if (userName.equals("HAKO") || userName.equals("NULL")) {
			return false;
		}

		return true;
	}

	private boolean isValidUpdateComment(BlogComment comment) {

		String commentText = comment.getComment();

		if (commentText == null || commentText.isEmpty()) {
			invalidValue = INVALID_COMMENT;
			return false;
		}

		String userName = comment.getUser_name().replace(UserRestController.BLOG_USER_TAG, "");
		if (!stringUtils.isValidPattern(userName, NAME_PATTERN)) {
			invalidValue = INVALID_NAME;
			return false;
		}
		comment.setComment(stringUtils.cleanXSS(commentText));
		comment.setUser_name(stringUtils.cleanXSS(comment.getUser_name()));
		return true;

	}

	private boolean isValidInsertComment(BlogComment comment) {

		String commentText = comment.getComment();

		if (commentText == null || commentText.isEmpty()) {
			invalidValue = INVALID_COMMENT;
			return false;
		}

		String pwd = comment.getPwd();
		if (!stringUtils.isValidPattern(pwd, PASSWORD_PATTERN)) {
			invalidValue = INVALID_PASSWORD;
			return false;
		}

		String userName = comment.getUser_name().replace(UserRestController.BLOG_USER_TAG, "");
		if (!stringUtils.isValidPattern(userName, NAME_PATTERN)) {
			invalidValue = INVALID_NAME;
			return false;
		}

		String email = comment.getEmail();
		if (email != null) {
			if (!stringUtils.isValidPattern(email, EMAIL_PATTERN)) {
				invalidValue = INVALID_EMAIL;
				return false;
			}
		}
		comment.setComment(stringUtils.cleanXSS(commentText));
		comment.setUser_name(stringUtils.cleanXSS(comment.getUser_name()));
		return true;

	}

	private void checkBlogUserAndUpdateComment(BlogComment comment, HttpServletRequest request) {
		String accToken = UserRestController.getAccTokenFormCookie(request);

		if (accToken != null) {
			String userNum = jwtProvider.getUsernameFromToken(accToken);
			BlogUser blogUser = userService.getUser(userNum);

			comment.setUser_name(blogUser.getName() + BUSER_TAG);
			comment.setPwd(blogUser.getId() + blogUser.getCreate_date());

			String feedback = request.getHeader(FEEDBACK_HEADER);
			if (feedback != null && feedback.equals(YES_FEEDBACK)) {
				comment.setEmail(blogUser.getEmail());
			}
		}
	}

}
