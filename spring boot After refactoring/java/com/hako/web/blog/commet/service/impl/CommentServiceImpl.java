package com.hako.web.blog.commet.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hako.web.blog.commet.dao.CommentDao;
import com.hako.web.blog.commet.entity.BlogComment;
import com.hako.web.blog.commet.service.CommentService;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentDao commentDao;

	@Autowired
	protected MailSender sender; // 타입으로 받을 수 있음

	@Value("${comment.salt}")
	private String COMMENT_SALT;

	private static final int SUCCESS_CODE = 1;
	private static final int FAIL_CODE = 0;
	private static final int TOP_LEVEL_COMMENT_CODE = 2;
	private static final int INVALID_PASSWORD_CODE = -1;

	private static final int NO_REFERENCES = 0;
	private static final String NULL_USER_NAME = "NULL";
	private static final String DELETED_COMMENT_MESSAGE = "삭제된 댓글입니다.";

	// 댓글 작성
	@Override
	public boolean insertComment(BlogComment comment) {

		String hashedPwd = BCrypt.hashpw(comment.getPwd() + COMMENT_SALT, BCrypt.gensalt());
		comment.setPwd(hashedPwd);

		if (commentDao.insertComment(comment) == SUCCESS_CODE) {
			sendFeedBackEmail(comment);
			return true;
		} else {
			return false;
		}
	}

	// 댓글 수정
	@Override
	public int updateComment(BlogComment comment) {
		// 비밀번호 불일치
		if (!checkPwd(comment)) {
			return -1;
		} else {
			return commentDao.updateComment(comment);
		}
	}

	public int deleteComment(BlogComment comment) {
		if (!checkPwd(comment)) {
			return INVALID_PASSWORD_CODE; // 비밀번호 불일치
		}

		BlogComment _comment = commentDao.getComment(comment.getNum());
		if (_comment == null) {
			return FAIL_CODE; // 삭제할 댓글이 없음
		}

		if (_comment.getRef_comment() == 0) {
			return deleteTopLevelComment(_comment);
		} else {
			return deleteReplyComment(_comment);
		}
	}

	private int deleteTopLevelComment(BlogComment comment) {
		int refCount = commentDao.getRefCount(comment.getNum());
		if (refCount == NO_REFERENCES) {
			return commentDao.deleteComment(comment.getNum());
		} else {
			return updateTopLevelComment(comment);
		}
	}

	private int updateTopLevelComment(BlogComment comment) {
		BlogComment update = new BlogComment();
		update.setUser_name(NULL_USER_NAME);
		update.setComment(DELETED_COMMENT_MESSAGE);
		update.setNum(comment.getNum());
		return (commentDao.updateComment(update) == SUCCESS_CODE) ? TOP_LEVEL_COMMENT_CODE : FAIL_CODE;
	}

	private int deleteReplyComment(BlogComment comment) {
		if (commentDao.deleteComment(comment.getNum()) == SUCCESS_CODE) {
			if (commentDao.getRefCount(comment.getRef_comment()) == NO_REFERENCES) {
				BlogComment refComment = commentDao.getComment(comment.getRef_comment());
				if (refComment != null && refComment.getUser_name().equals(NULL_USER_NAME)) {
					return commentDao.deleteComment(refComment.getNum());
				}
			}
			return SUCCESS_CODE;
		}
		return FAIL_CODE;
	}

	// 댓글 리스트 반환
	@Override
	public List<BlogComment> getCommentList(int num) {
		return commentDao.getCommentList(num);
	}

	// 댓글 사용자 확인
	private boolean checkPwd(BlogComment comment) {
		String _pwd = commentDao.getComment(comment.getNum()).getPwd();

		return BCrypt.checkpw(comment.getPwd() + COMMENT_SALT, _pwd);
	}

	// 댓글 수정
	public void updateCommentName(String old_user_name, String new_user_name) {

		commentDao.updateUserName(old_user_name, new_user_name);
	}

	// 댓글 수정
	public void updateCommentEmail(String old_user_email, String new_user_email) {
		commentDao.updateUserEmail(old_user_email, new_user_email);
	}

	public int getLastCommentNum() {
		// TODO Auto-generated method stub
		return commentDao.getLastCommentNum();
	}

	private void sendFeedBackEmail(BlogComment comment) {
		if (comment.getUser_name().contains("HAKO")) {
			return; // HAKO인 경우에는 메일을 보내지 않음
		}

		int refComment = comment.getRef_comment();
		String userEmail = comment.getEmail();
		String commentText = comment.getComment();
		int boardNum = comment.getBoard_num();

		sendOwnerEmailIfNormalUser(commentText, boardNum);

		sendFeedbackEmails(refComment, userEmail, commentText, boardNum);
	}

	private void sendOwnerEmailIfNormalUser(String commentText, int boardNum) {
		String address = "edwsqa987@gmail.com";
		String title = boardNum + "번글에 댓글이 달렸습니다";
		String contents = "댓글 : " + commentText + "\nhttps://sirobako.co.kr/detail/" + boardNum;

		sendEmail(address, title, contents);
	}

	private void sendFeedbackEmails(int refComment, String userEmail, String commentText, int boardNum) {
		if (refComment != 0) {
			List<String> feedbackEmails = getFeedbackEmailList(refComment);

			Set<String> sentEmails = new HashSet<>();
			for (String email : feedbackEmails) {
				if (!email.equals(userEmail) && !email.isEmpty() && !sentEmails.contains(email)) {
					String title = "대댓글이 달렸습니다";
					String contents = "댓글 : " + commentText + "\nhttps://sirobako.co.kr/detail/" + boardNum;

					sendEmail(email, title, contents);
					sentEmails.add(email);
				}
			}
		}
	}

	private void sendEmail(String adress, String title, String contents) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(adress); // 수신자 설정
		message.setSubject(title); // 메일 제목 설정
		message.setText(contents);
		sender.send(message);

	}

	private List<String> getFeedbackEmailList(int ref_comment) {
		List<String> temp = commentDao.getRefCommentEmailList(ref_comment);
		temp.add(commentDao.getComment(ref_comment).getEmail());
		return temp;
	}

}
