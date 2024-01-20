package com.hako.web.user.dao;

import com.hako.web.user.entity.BlogToken;
import com.hako.web.user.entity.BlogUser;

public interface UserDao {

	// 회원가입
	boolean insertUser(BlogUser user);

	// 유저 정보 업데이트
	boolean updateUser(BlogUser user);
	
	// 유저 정보 반환
	BlogUser getUserInfo(String user_id);

	
	// 이메일로 아이디 찾기
	BlogUser findUserInfo(String user_email);

	// id 중복 확인
	boolean checkUserId(String user_id);

	// 닉네임 중복확인
	boolean checkUserName(String user_name, String user_id);

	// 이메일 중복 확인
	boolean checkUserEmail(String user_email, String user_id);
	
    // 토큰 저장
	boolean saveToken(BlogToken blogToken);

    // 토큰 정보 반환
	BlogToken getTokenInfo(String user_id);

    // 회원 탈퇴
	boolean deleteUser(String user_id);

    // 토큰 삭제
	boolean deleteToken(String user_id);







	
}
