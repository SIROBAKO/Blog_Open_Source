package com.hako.web.blog.user.service;

import com.hako.web.blog.user.entity.BlogToken;

public interface TokenService {

	// 토큰 저장
	boolean saveToken(BlogToken blogToken);

	// 토큰 유효성 검사
	boolean isValidAccToken(String accToken, String userNum);

	// 토큰 유효성 검사
	boolean isValidRefToken(String refToken, String userNum);

	// 토큰삭제
	boolean deleteToken(String user_id);

}
