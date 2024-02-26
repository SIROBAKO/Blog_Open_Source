package com.hako.web.blog.user.dao.impl;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.user.dao.TokenDao;
import com.hako.web.blog.user.entity.BlogToken;

@Repository
public class TokenDaoImpl implements TokenDao {

	@Autowired
	private EntityManager entityManager;

	private Logger LOG = LogManager.getLogger(TokenDaoImpl.class);

	// 토큰 관련 로그 메시지
	private static final  String TOKEN_SAVE = "토큰 서버 저장";
	private static final  String TOKEN_SAVE_FAIL = "토큰 서버 저장 실패";
	private static final  String TOKEN_FETCH = "토큰 조회";
	private static final  String TOKEN_FETCH_FAIL = "토큰 조회 실패";
	private static final  String TOKEN_DELETE = "토큰 삭제";
	private static final  String TOKEN_DELETE_FAIL = "토큰 삭제 실패";

	// 토큰 저장
	@Override
	public boolean saveToken(BlogToken blogToken) {
		long user_num = blogToken.getUser_num();
		try {
			LOG.info(TOKEN_SAVE);
			// 이미 존재하는 엔티티인지 확인
			BlogToken existingToken = entityManager.find(BlogToken.class, user_num);

			if (existingToken != null) {
				// 기존에 존재하는 경우 해당 엔티티의 토큰 정보 업데이트
				existingToken.setUser_num(user_num);
				existingToken.setAccess_token(blogToken.getAccess_token());
				existingToken.setRefresh_token(blogToken.getRefresh_token());
				entityManager.merge(existingToken);
			} else {
				// 새로운 경우에는 그대로 저장
				entityManager.persist(blogToken);
			}
			return true;
		} catch (Exception e) {
			LOG.error(TOKEN_SAVE_FAIL, e);
			return false;
		}
	}

	// 토큰 삭제
	@Override
	public boolean deleteToken(String userNum) {
		try {
			LOG.info(TOKEN_DELETE);
			long userNumTemp = Long.parseLong(userNum);
			BlogToken token = entityManager.find(BlogToken.class, userNumTemp);
			if (token != null) {
				entityManager.remove(token);
				return true; // 토큰 삭제 성공
			} else {
				return false; // 해당 유저의 토큰 정보가 없음
			}
		} catch (Exception e) {
			LOG.error(TOKEN_DELETE_FAIL, e);
			return false;
		}
	}

	// 토큰 조회
	@Override
	public BlogToken getTokenInfo(String userNum) {
		long userNumTemp = Long.parseLong(userNum);
		try {
			LOG.info(TOKEN_FETCH);
			return entityManager.find(BlogToken.class, userNumTemp);
		} catch (Exception e) {
			LOG.error(TOKEN_FETCH_FAIL, e);
			return null;
		}
	}

}
