package com.hako.web.blog.user.service;

import com.hako.web.blog.user.entity.BlogToken;
import com.hako.web.blog.user.entity.BlogUser;

public interface UserService {

	// 회원가입
	boolean insertUser(BlogUser user);

	// 회원탈퇴
	boolean deleteUser(String user_id);

	// 회원정보수정
	boolean updateUser(BlogUser user);

	// 회원정보 반환
	BlogUser getUser(String userNum);

	// 중복 id 체크
	boolean isDuplicatedId(String userId);

	// 중복 닉네임 체크
	boolean isDuplicatedName(String user_name, String userNum);

	// 중복 이메일 체크
	boolean isDuplicatedEmail(String user_email, String userNum);

	// 로그인
	boolean loginUser(BlogUser user);

	BlogUser findUser(String user_email);

	String getUserNumFromId(String id);

	BlogUser getUserFromId(String id);

	

}
