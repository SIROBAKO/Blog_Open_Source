package com.hako.web.blog.user.dao;

import com.hako.web.blog.user.entity.BlogToken;
import com.hako.web.blog.user.entity.BlogUser;

public interface TokenDao {

    // 토큰 저장
	boolean saveToken(BlogToken blogToken);

    // 토큰 정보 반환
	BlogToken getTokenInfo(String userNum);

    // 토큰 삭제
	boolean deleteToken(String userNum);

	

	
}
