package com.hako.web.blog.user.dao;

import com.hako.web.blog.user.entity.BlogToken;
import com.hako.web.blog.user.entity.BlogUser;

public interface UserDao {

	// 회원가입
	boolean insertUser(BlogUser user);

	// 유저 정보 업데이트
	boolean updateUser(BlogUser user);
	
	// 유저 정보 반환
	BlogUser getUserInfo(String userNum);

	BlogUser getUserInfoFromId(String userId);
	
	// 이메일로 아이디 찾기
	BlogUser findUserInfo(String userEmail);

	// id 중복 확인
	boolean isDuplicatedId(String userId);

	// 닉네임 중복확인
	boolean isDuplicatedName(String userName, String userNum);

	// 이메일 중복 확인
	boolean isDuplicatedEmail(String userEmail, String userNum);

    // 회원 탈퇴
	boolean deleteUser(String userNum);

	

	
}
