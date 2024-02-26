package com.hako.web.blog.user.service.impl;

import javax.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hako.web.blog.user.dao.TokenDao;
import com.hako.web.blog.user.dao.UserDao;
import com.hako.web.blog.user.entity.BlogUser;
import com.hako.web.blog.user.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	TokenDao tokenDao;
	
	// 회원 가입
	@Override
	public boolean insertUser(BlogUser user) {

		user.setPwd(BCrypt.hashpw(user.getPwd(), BCrypt.gensalt()));
		return userDao.insertUser(user);
	}

	// 회원 탈퇴
	@Override
	public boolean deleteUser(String user_num) {

		return tokenDao.deleteToken(user_num) && userDao.deleteUser(user_num);

	}

	// 회원 정보 수정
	@Override
	public boolean updateUser(BlogUser user) {
		user.setPwd(BCrypt.hashpw(user.getPwd(), BCrypt.gensalt()));
		return userDao.updateUser(user);
	}

	// 유저정보 반환
	@Override
	public BlogUser getUser(String userNum) {
		return userDao.getUserInfo(userNum);
	}

	@Override
	public String getUserNumFromId(String id) {
		return userDao.getUserInfoFromId(id).getNumToString();
	}

	// 유저 정보 찾기(이메일)
	@Override
	public BlogUser findUser(String user_email) {

		return userDao.findUserInfo(user_email);
	}

	// 아이디 중복 확인
	@Override
	public boolean isDuplicatedId(String userId) {

		return userDao.isDuplicatedId(userId);
	}

	// 닉네임 중복 확인
	@Override
	public boolean isDuplicatedName(String userName, String userNum) {

		return userDao.isDuplicatedName(userName, userNum);
	}

	// 이메일 중복 확인
	@Override
	public boolean isDuplicatedEmail(String userEmail, String userNum) {

		try {

			return userDao.isDuplicatedEmail(userEmail, userNum);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	// 로그인
	@Override
	public boolean loginUser(BlogUser user) {

		String tempPwd = user.getPwd();
		user = userDao.getUserInfoFromId(user.getId());
		if (user != null && BCrypt.checkpw(tempPwd, user.getPwd())) {
			// 비밀번호 같을시 성공
			return true;
		} else {
			// 비밀번호 틀림
			return false;
		}
	}

	@Override
	public BlogUser getUserFromId(String id) {

		return userDao.getUserInfoFromId(id);
	}

}
