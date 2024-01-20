package com.hako.web.user.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.user.dao.UserDao;
import com.hako.web.user.entity.BlogToken;
import com.hako.web.user.entity.BlogUser;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private EntityManager entityManager;

	private Logger LOG = LogManager.getLogger(UserDaoImpl.class);

	// ====================== 유저 관련 ==========================
	// 유저 정보 저장
	@Override
	public boolean insertUser(BlogUser user) {
		try {
			LOG.info("유저 정보 저장");
			entityManager.merge(user); // 이미 영속화된 경우 merge 수행
			return true;
		} catch (Exception e) {
			LOG.debug("유저 정보 저장 실패", e);
			return false;
		}
	}

	// 유저 정보 업데이트
	@Override
	public boolean updateUser(BlogUser user) {

		try {
			LOG.info("유저 정보 업데이트");
			BlogUser existingUser = entityManager.find(BlogUser.class, user.getId());
			if (existingUser != null) {
				// 기존 유저 정보 업데이트
				if (user.getName() != null) {
					existingUser.setName(user.getName());
				}
				
				existingUser.setEmail(user.getEmail());

				if (user.getPwd() != null) {
					existingUser.setPwd(user.getPwd());
				}
				entityManager.merge(existingUser);
				return true;
			} else {
				// 해당 ID에 해당하는 유저가 존재하지 않음

				return false;
			}
		} catch (Exception e) {
			LOG.debug("유저 정보 업데이트 실패", e);
			return false;
		}
	}

	// 유저 정보 반환
	@Override
	public BlogUser getUserInfo(String user_id) {
		try {
			LOG.info(user_id + "의 유저 정보 반환");
			return entityManager.find(BlogUser.class, user_id);
		} catch (Exception e) {
			LOG.debug(user_id + "의유저 정보 반환 실패", e);
			return null;
		}
	}

	// 유저 정보 삭제
	@Override
	public boolean deleteUser(String user_id) {
		try {
			LOG.info(user_id + "의 유저 정보 삭제");
			BlogUser user = entityManager.find(BlogUser.class, user_id);
			if (user != null) {
				entityManager.remove(user);
				return true; // 삭제 성공
			} else {
				return false; // 해당 유저 정보가 없음
			}
		} catch (Exception e) {
			LOG.debug(user_id + "의유저 정보 삭제 실패", e);
			return false;
		}
	}

	// 이메일로 유저 찾기
	@Override
	public BlogUser findUserInfo(String user_email) {
		try {
			LOG.info("이메일로 유저정보 찾기");
			String query = "SELECT u FROM BlogUser u WHERE u.email = :user_email";

			// 쿼리 실행 등록
			Query q = entityManager.createQuery(query).setParameter("user_email", user_email);

			BlogUser blogUser = (BlogUser) q.getSingleResult();
			return blogUser; // 중복안되는 경우 true 반환
		} catch (Exception e) {
			LOG.debug("이메일로 유저정보 찾기 실패", e);
			return null;
		}
	}

	// ====================== 토큰 관련 ==========================
	// 토큰저장
	@Override
	public boolean saveToken(BlogToken blogToken) {
		String user_id = blogToken.getUser_id();
		try {
			LOG.info(user_id + "의 토큰 저장");
			// 이미 존재하는 엔티티인지 확인
			BlogToken existingToken = entityManager.find(BlogToken.class, user_id);

			if (existingToken != null) {
				// 기존에 존재하는 경우 해당 엔티티의 토큰 정보 업데이트
				existingToken.setAccess_token(blogToken.getAccess_token());
				existingToken.setRefresh_token(blogToken.getRefresh_token());
				entityManager.merge(existingToken);
			} else {
				// 새로운 경우에는 그대로 저장
				entityManager.persist(blogToken);
			}
			return true;
		} catch (Exception e) {
			LOG.debug(user_id + "의 토큰 저장 실패", e);
			return false;
		}
	}

	// id값으로 토큰 조회
	@Override
	public BlogToken getTokenInfo(String user_id) {

		try {
			LOG.info(user_id + "의 토큰 조회");
			return entityManager.find(BlogToken.class, user_id);
		} catch (Exception e) {
			LOG.debug(user_id + "의 토큰 조회 실패", e);
			return null;
		}

	}

	// 토큰 삭제
	@Override
	public boolean deleteToken(String user_id) {
		try {
			LOG.info(user_id + "의 토큰 삭제");
			BlogToken token = entityManager.find(BlogToken.class, user_id);
			if (token != null) {
				entityManager.remove(token);
				return true; // 토큰 삭제 성공
			} else {
				return false; // 해당 유저의 토큰 정보가 없음
			}
		} catch (Exception e) {
			LOG.debug(user_id + "의 토큰 삭제 실패", e);
			return false;
		}
	}

	// ====================== 중복 체크 관련 ==========================
	// id 중복 확인
	@Override
	public boolean checkUserId(String user_id) {
		try {
			LOG.info("아이디 중복확인");
			BlogUser existingUser = entityManager.find(BlogUser.class, user_id);
			return existingUser == null;
		} catch (Exception e) {
			LOG.debug("아이디 중복 확인 실패", e);
			return false;
		}
	}

	// 닉네임 중복 확인
	@Override
	public boolean checkUserName(String user_name, String user_id) {
		try {
			LOG.info("닉네임 중복확인");
			String query = "SELECT COUNT(*) FROM BlogUser u WHERE u.name = :user_name";
			if (user_id != null) {
				query += " AND u.id != :user_id";
			}

			// 쿼리 실행 등록
			Query q = entityManager.createQuery(query).setParameter("user_name", user_name);

			if (user_id != null) {
				q.setParameter("user_id", user_id);
			}

			// 결과 검색 및 중복 여부 반환
			Long count = (Long) q.getSingleResult();
			return count == 0; // 중복안되는 경우 true 반환
		} catch (Exception e) {
			LOG.debug("닉네임 중복 확인 실패", e);
			return false;
		}
	}

	// 이메일 중복확인
	@Override
	public boolean checkUserEmail(String user_email, String user_id) {
		try {
			LOG.info("이메일 중복확인");
			String query = "SELECT COUNT(*) FROM BlogUser u WHERE u.email = :user_email";
			if (user_id != null) {
				query += " AND u.id != :user_id";
			}

			// 쿼리 실행 등록
			Query q = entityManager.createQuery(query).setParameter("user_email", user_email);

			if (user_id != null) {
				q.setParameter("user_id", user_id);
			}

			// 결과 검색 및 중복 여부 반환
			Long count = (Long) q.getSingleResult();
			return count == 0; // 중복안되는 경우 true 반환

		} catch (Exception e) {
			LOG.debug("이메일 중복 확인 실패", e);
			return false;
		}
	}

}
