package com.hako.web.user.service.impl;

import javax.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hako.web.user.dao.UserDao;
import com.hako.web.user.entity.BlogToken;
import com.hako.web.user.entity.BlogUser;
import com.hako.web.user.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	// 회원 가입
	@Override
	public boolean insertUser(BlogUser user) {
		// TODO Auto-generated method stub
		return userDao.insertUser(user);
	}

	// 회원 탈퇴
	@Override
	public boolean deleteUser(String user_id) {
		// TODO Auto-generated method stub
		if (userDao.deleteToken(user_id)) {

			return userDao.deleteUser(user_id);
		} else {
			return false;
		}
	}

	// 회원 정보 수정
	@Override
	public boolean updateUser(BlogUser user) {
		// TODO Auto-generated method stub
		return userDao.updateUser(user);
	}

	// 유저정보 반환
	@Override
	public BlogUser getUser(String user_id) {
		// TODO Auto-generated method stub
		return userDao.getUserInfo(user_id);
	}
	
	// 유저 정보 찾기(이메일)
	@Override
	public BlogUser findUser(String user_email) {
		// TODO Auto-generated method stub
		return  userDao.findUserInfo(user_email);
	}

	// 아이디 중복 확인
	@Override
	public boolean checkUserId(String user_id) {
		
		return userDao.checkUserId(user_id);
	}

	// 닉네임 중복 확인
	@Override
	public boolean checkUserName(String user_name, String user_id) {
		// TODO Auto-generated method stub
		return userDao.checkUserName(user_name ,user_id);
	}

	// 이메일 중복 확인
	@Override
	public boolean checkUserEmail(String user_email, String user_id) {
		// TODO Auto-generated method stub
		return userDao.checkUserEmail(user_email,user_id);
	}

	// 토큰 삭제 (로그아웃)
	@Override
	public boolean deleteToken(String user_id) {
		// TODO Auto-generated method stub
		return userDao.deleteToken(user_id);
	}

	// 로그인
	@Override
	public int loginUser(String user_id, String user_pwd) {

		BlogUser blogUser = userDao.getUserInfo(user_id);

		if (blogUser == null) {
			// 해당 아이디 존재 안함
			return 0;
		} else if (BCrypt.checkpw(user_pwd, blogUser.getPwd())) {
			// 비밀번호 같을시 성공
			return 1;
		} else {
			// 비밀번호 틀림
			return -1;
		}
	}

	// 서버 토큰 저장
	@Override
	public boolean saveToken(BlogToken blogToken) {
		// TODO Auto-generated method stub
		return userDao.saveToken(blogToken);
	}

	// 토큰 유효성 검사
	@Override
	public boolean checkToken(String accToken, String refToken, String user_id) {
		// TODO Auto-generated method stub
		BlogToken blogToken = userDao.getTokenInfo(user_id);
		if (blogToken != null
				&& (blogToken.getAccess_token().equals(accToken) || blogToken.getRefresh_token().equals(refToken))) {
			return true;
		} else {
			return false;
		}
	}

	

}
