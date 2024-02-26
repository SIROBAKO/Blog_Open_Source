package com.hako.web.blog.commet.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.commet.controller.CommetRestController;
import com.hako.web.blog.commet.dao.CommentDao;
import com.hako.web.blog.commet.entity.BlogComment;

@Repository
public class CommentDaoImpl implements CommentDao {

	private CommentDao mapper;

	private Logger LOG = LogManager.getLogger(CommentDaoImpl.class);

	private static final String COMMENT_SAVE = "댓글 저장";
	private static final String COMMENT_UPDATE = "댓글 업데이트";
	private static final String COMMENT_DELETE = "댓글 삭제";
	private static final String COMMENT_LIST = "댓글 리스트 반환";
	private static final String COMMENT_RETURN = "댓글 반환";
	private static final String REF_COUNT = "대댓글 수 반환";
	private static final String REF_COMMENT_EMAIL = "피드백 이메일 목록 반환";
	private static final String USER_NAME_UPDATE = "블로그 유저 댓글 이름 업데이트";
	private static final String USER_EMAIL_UPDATE = "블로그 유저 댓글 이메일 업데이트";
	private static final String LAST_COMMENT_NUM = "최근 댓글 번호 반환";

	private static final String FAIL_COMMENT_SAVE = "댓글 저장 실패";
	private static final String FAIL_COMMENT_UPDATE = "댓글 업데이트 실패";
	private static final String FAIL_COMMENT_DELETE = "댓글 삭제 실패";
	private static final String FAIL_COMMENT_LIST = "댓글 리스트 반환 실패";
	private static final String FAIL_COMMENT_RETURN = "댓글 반환 실패";
	private static final String FAIL_REF_COUNT = "대댓글 수 반환 실패";
	private static final String FAIL_REF_COMMENT_EMAIL = "피드백 이메일 목록 실패";
	private static final String FAIL_USER_NAME_UPDATE = "블로그 유저 댓글 이름 업데이트 실패";
	private static final String FAIL_USER_EMAIL_UPDATE = "블로그 유저 댓글 이메일 업데이트 실패";
	private static final String FAIL_LAST_COMMENT_NUM = "최근 댓글 번호 반환 실패";

	@Autowired
	public CommentDaoImpl(SqlSession sqlSession) {
		// SqlSession을 통해 CommentDao 인터페이스의 MyBatis 매퍼를 가져옵니다.
		mapper = sqlSession.getMapper(CommentDao.class);
	}

	@Override
	public int insertComment(BlogComment comment) {
		try {
			LOG.info(COMMENT_SAVE);
			return mapper.insertComment(comment);
		} catch (Exception e) {
			LOG.error(FAIL_COMMENT_SAVE, e);
			return 0;
		}
	}

	@Override
	public int updateComment(BlogComment comment) {
		try {
			LOG.info(COMMENT_UPDATE);
			return mapper.updateComment(comment);
		} catch (Exception e) {
			LOG.error(FAIL_COMMENT_UPDATE, e);
			return 0;
		}
	}

	@Override
	public int deleteComment(int num) {
		try {
			LOG.info(COMMENT_DELETE);
			return mapper.deleteComment(num);
		} catch (Exception e) {
			LOG.error(FAIL_COMMENT_DELETE, e);
			return 0;
		}
	}

	@Override
	public List<BlogComment> getCommentList(int num) {
		try {
			LOG.info(COMMENT_LIST);
			return mapper.getCommentList(num);
		} catch (Exception e) {
			LOG.error(FAIL_COMMENT_LIST, e);
			return null;
		}
	}

	@Override
	public BlogComment getComment(int num) {
		try {
			LOG.info(COMMENT_RETURN);
			return mapper.getComment(num);
		} catch (Exception e) {
			LOG.error(FAIL_COMMENT_RETURN, e);
			return null;
		}
	}

	@Override
	public int getRefCount(int ref) {
		try {
			LOG.info(REF_COUNT);
			return mapper.getRefCount(ref);
		} catch (Exception e) {
			LOG.error(FAIL_REF_COUNT, e);
			return 0;
		}
	}

	@Override
	public List<String> getRefCommentEmailList(int ref_comment) {
		try {
			LOG.info(REF_COMMENT_EMAIL);
			return mapper.getRefCommentEmailList(ref_comment);
		} catch (Exception e) {
			LOG.error(FAIL_REF_COMMENT_EMAIL, e);
			return null;
		}
	}

	@Override
	public void updateUserName(String old_user_name, String new_user_name) {
		try {
			LOG.info(USER_NAME_UPDATE);
			mapper.updateUserName(old_user_name, new_user_name);
		} catch (Exception e) {
			LOG.error(FAIL_USER_NAME_UPDATE, e);
		}
	}

	@Override
	public void updateUserEmail(String old_user_email, String new_user_email) {
		try {
			LOG.info(USER_EMAIL_UPDATE);
			mapper.updateUserEmail(old_user_email, new_user_email);
		} catch (Exception e) {
			LOG.error(FAIL_USER_EMAIL_UPDATE, e);
		}
	}

	@Override
	public int getLastCommentNum() {
		try {
			LOG.info(LAST_COMMENT_NUM);
			return mapper.getLastCommentNum();
		} catch (Exception e) {
			LOG.error(LAST_COMMENT_NUM, e);
			return 0;
		}
	}

}
