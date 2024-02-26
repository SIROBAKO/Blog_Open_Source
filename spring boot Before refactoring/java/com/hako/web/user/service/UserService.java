package com.hako.web.user.service;

import com.hako.web.user.entity.BlogToken;
import com.hako.web.user.entity.BlogUser;


public interface UserService {

	// 회원가입
	boolean insertUser(BlogUser user);

	// 회원탈퇴
	boolean deleteUser(String user_id);

	// 회원정보수정
	boolean updateUser(BlogUser user);

	// 회원정보 반환
	BlogUser getUser(String user_id);

	// 중복 id 체크
	boolean checkUserId(String user_id);

	// 중복 닉네임 체크
	boolean checkUserName(String user_name, String user_id);

	// 중복 이메일 체크
	boolean checkUserEmail(String user_email, String user_id);



	
	// 로그인 
	int loginUser(String user_id, String user_pwd);

	// 토큰 저장
	boolean saveToken(BlogToken blogToken);

	// 토큰 유효성 검사
	boolean checkToken(String accToken,String refToken, String user_id);

	// 토큰삭제
	boolean deleteToken(String user_id);

	BlogUser findUser(String user_email);

}
