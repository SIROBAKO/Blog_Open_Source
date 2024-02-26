package com.hako.web.blog.user.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hako.web.blog.user.dao.TokenDao;
import com.hako.web.blog.user.entity.BlogToken;
import com.hako.web.blog.user.service.TokenService;

@Service
@Transactional
class TokenServiceImpl implements TokenService {

	@Autowired
	TokenDao tokenDao;

// 토큰 삭제 (로그아웃)
	@Override
	public boolean deleteToken(String user_id) {

		return tokenDao.deleteToken(user_id);
	}

// 서버 토큰 저장
	@Override
	public boolean saveToken(BlogToken blogToken) {
		return tokenDao.saveToken(blogToken);
	}

// 토큰 유효성 검사
	@Override
	public boolean isValidAccToken(String accToken, String userNum) {
		BlogToken blogToken = tokenDao.getTokenInfo(userNum);

		if (blogToken != null && (blogToken.getAccess_token().equals(accToken))) {
			return true;
		} else {
			return false;
		}
	}

// 토큰 유효성 검사
	@Override
	public boolean isValidRefToken(String refToken, String userNum) {

		BlogToken blogToken = tokenDao.getTokenInfo(userNum);
		if (blogToken != null && blogToken.getRefresh_token().equals(refToken)) {
			return true;
		} else {
			return false;
		}
	}

}
