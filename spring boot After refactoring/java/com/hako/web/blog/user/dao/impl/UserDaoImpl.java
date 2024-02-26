package com.hako.web.blog.user.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.user.dao.UserDao;
import com.hako.web.blog.user.entity.BlogToken;
import com.hako.web.blog.user.entity.BlogUser;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private EntityManager entityManager;

	private Logger LOG = LogManager.getLogger(UserDaoImpl.class);

	private static final String USER_INFO_SAVING = "유저 정보 저장";
	private static final String USER_INFO_SAVE_FAIL = "유저 정보 저장 실패";
	private static final String USER_INFO_UPDATE = "유저 정보 업데이트";
	private static final String USER_INFO_UPDATE_FAIL = "유저 정보 업데이트 실패";
	private static final String USER_INFO_FETCH = "유저 정보 반환";
	private static final String USER_INFO_FETCH_FAIL = "유저 정보 반환 실패";
	private static final String USER_INFO_FETCH_BY_ID = "ID를 통한 유저 정보 반환";
	private static final String USER_INFO_FETCH_BY_ID_FAIL = "ID를 통한 유저 정보 반환 실패";
	private static final String USER_INFO_DELETE = "유저 정보 삭제";
	private static final String USER_INFO_DELETE_FAIL = "유저 정보 삭제 실패";
	private static final String USER_EMAIL_FETCH = "이메일로 유저 정보 찾기";
	private static final String USER_EMAIL_FETCH_FAIL = "이메일로 유저 정보 찾기 실패";

	// 중복 체크 관련 로그 메시지
	private static final String ID_DUPLICATE_CHECK = "아이디 중복확인";
	private static final String ID_DUPLICATE_CHECK_FAIL = "아이디 중복확인 실패";
	private static final String NAME_DUPLICATE_CHECK = "닉네임 중복확인";
	private static final String NAME_DUPLICATE_CHECK_FAIL = "닉네임 중복확인 실패";
	private static final String EMAIL_DUPLICATE_CHECK = "이메일 중복확인";
	private static final String EMAIL_DUPLICATE_CHECK_FAIL = "이메일 중복확인 실패";

	// ====================== 유저 관련 ==========================
	// 유저 정보 저장
	@Override
	public boolean insertUser(BlogUser user) {
		try {
			LOG.info(USER_INFO_SAVING);
			entityManager.merge(user); // 이미 영속화된 경우 merge 수행
			return true;
		} catch (Exception e) {
			LOG.error(USER_INFO_SAVE_FAIL, e);
			return false;
		}
	}

	// 유저 정보 업데이트
	@Override
	public boolean updateUser(BlogUser user) {

		try {
			LOG.info(USER_INFO_UPDATE);
			BlogUser existingUser = entityManager.find(BlogUser.class, user.getNum());

			if (existingUser != null) {
				// 기존 유저 정보 업데이트
				if (user.getName() != null) {
					existingUser.setName(user.getName());
				}

				if (user.getEmail() != null) {
					existingUser.setEmail(user.getEmail());
				}
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
			LOG.error(USER_INFO_UPDATE_FAIL, e);
			return false;
		}
	}

	// 유저 정보 반환
	@Override
	public BlogUser getUserInfo(String userNum) {
		LOG.info(USER_INFO_FETCH);
		try {
			long userNumTemp = Long.parseLong(userNum);
			return entityManager.find(BlogUser.class, userNumTemp);
		} catch (Exception e) {
			LOG.error(USER_INFO_FETCH_FAIL, e);
			return null;
		}
	}

	@Override
	public BlogUser getUserInfoFromId(String id) {
		LOG.info(USER_INFO_FETCH_BY_ID);
		try {
			String selectQuery = "SELECT u FROM BlogUser u WHERE u.id = :id";
			TypedQuery<BlogUser> query = entityManager.createQuery(selectQuery, BlogUser.class);
			query.setParameter("id", id);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null; // 해당 ID에 해당하는 사용자를 찾을 수 없을 때
		} catch (Exception e) {
			LOG.error(USER_INFO_FETCH_BY_ID_FAIL, e);
			return null;
		}
	}

	// 유저 정보 삭제
	@Override
	public boolean deleteUser(String userNum) {
		try {
			LOG.info(USER_INFO_DELETE);
			long userNumTemp = Long.parseLong(userNum);
			BlogUser user = entityManager.find(BlogUser.class, userNumTemp);
			if (user != null) {
				entityManager.remove(user);
				return true; // 삭제 성공
			} else {
				return false; // 해당 유저 정보가 없음
			}
		} catch (Exception e) {
			LOG.error(USER_INFO_DELETE_FAIL, e);
			return false;
		}
	}

	// 이메일로 유저 찾기
	@Override
	public BlogUser findUserInfo(String user_email) {
		try {
			LOG.info(USER_EMAIL_FETCH);
			String query = "SELECT u FROM BlogUser u WHERE u.email = :user_email";

			// 쿼리 실행 등록
			Query q = entityManager.createQuery(query).setParameter("user_email", user_email);

			BlogUser blogUser = (BlogUser) q.getSingleResult();
			return blogUser; // 중복안되는 경우 true 반환
		} catch (Exception e) {
			LOG.error(USER_EMAIL_FETCH_FAIL, e);
			return null;
		}
	}

	// id 중복 확인
	@Override
	public boolean isDuplicatedId(String userId) {
		try {
			LOG.info(ID_DUPLICATE_CHECK);

			String query = "SELECT COUNT(*) FROM BlogUser u WHERE u.id = :user_id";

			// 쿼리 실행 등록
			Query q = entityManager.createQuery(query).setParameter("user_id", userId);

			// 결과 검색 및 중복 여부 반환
			Long count = (Long) q.getSingleResult();
			return count == 0; // 중복안되는 경우 true 반환

		} catch (Exception e) {
			LOG.error(ID_DUPLICATE_CHECK_FAIL, e);
			return false;
		}
	}

	// 닉네임 중복 확인
	@Override
	public boolean isDuplicatedName(String user_name, String userNum) {
		try {
			LOG.info(NAME_DUPLICATE_CHECK);

			String query = "SELECT COUNT(*) FROM BlogUser u WHERE u.name = :user_name";
			if (userNum != null) {
				query += " AND u.num != :user_num";
			}

			// 쿼리 실행 등록
			Query q = entityManager.createQuery(query).setParameter("user_name", user_name);

			if (userNum != null) {
				long userNumTemp = Long.parseLong(userNum);
				q.setParameter("user_num", userNumTemp);
			}

			// 결과 검색 및 중복 여부 반환
			Long count = (Long) q.getSingleResult();
			return count == 0; // 중복안되는 경우 true 반환
		} catch (Exception e) {
			LOG.error(NAME_DUPLICATE_CHECK_FAIL, e);
			return false;
		}
	}

	// 이메일 중복확인
	@Override
	public boolean isDuplicatedEmail(String user_email, String userNum) {
		try {
			LOG.info(EMAIL_DUPLICATE_CHECK);
			String query = "SELECT COUNT(*) FROM BlogUser u WHERE u.email = :user_email";
			if (userNum != null) {
				query += " AND u.num != :user_num";
			}

			// 쿼리 실행 등록
			Query q = entityManager.createQuery(query).setParameter("user_email", user_email);

			if (userNum != null) {
				long userNumTemp = Long.parseLong(userNum);
				q.setParameter("user_num", userNumTemp);
			}

			// 결과 검색 및 중복 여부 반환
			Long count = (Long) q.getSingleResult();
			return count == 0; // 중복안되는 경우 true 반환

		} catch (Exception e) {
			LOG.error(EMAIL_DUPLICATE_CHECK_FAIL, e);
			return false;
		}
	}
}
