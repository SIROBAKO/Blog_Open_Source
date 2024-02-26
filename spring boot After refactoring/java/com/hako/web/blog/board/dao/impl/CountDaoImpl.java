package com.hako.web.blog.board.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.board.dao.CountDao;
import com.hako.web.blog.board.entity.BlogCount;

@Repository
public class CountDaoImpl implements CountDao {

	private CountDao mapper;

	private Logger LOG = LogManager.getLogger(CountDaoImpl.class);

	private static final String RESET_TODAY_COUNT = "조회수 리셋";
	private static final String ADD_COUNT_INFO = "조회수 더하기";
	private static final String GET_COUNT_INFO = "조회수 정보 가져오기";
	private static final String GET_COUNT_INFO_LIST = "조회수 정보 목록 가져오기";

	private static final String FAIL_RESET_TODAY_COUNT = "조회수 리셋 실패";
	private static final String FAIL_ADD_COUNT_INFO = "조회수 더하기 실패";
	private static final String FAIL_GET_COUNT_INFO = "조회수 정보 가져오기 실패";
	private static final String FAIL_GET_COUNT_INFO_LIST = "조회수 정보 목록 가져오기 실패";

	@Autowired
	public CountDaoImpl(SqlSession sqlSession) {
		// SqlSession을 통해 CountDao 인터페이스의 MyBatis 매퍼를 가져옵니다.
		mapper = sqlSession.getMapper(CountDao.class);
	}

	// 오늘의 방문자 수와 총 방문자 수를 0으로 초기화합니다.
	public int resetTodayCount() {
		try {
			LOG.info(RESET_TODAY_COUNT);
			return mapper.resetTodayCount();

		} catch (Exception e) {
			LOG.error(FAIL_RESET_TODAY_COUNT, e);
			return 0;
		}
	}

	// 지정된 이름에 해당하는 카운트에 값을 더합니다.
	public int addCountInfo(String name, int plus) {
		try {
			LOG.info(ADD_COUNT_INFO);
			return mapper.addCountInfo(name, plus);
		} catch (Exception e) {
			LOG.error(FAIL_ADD_COUNT_INFO, e);
			return 0;
		}
	}

	// 데이터베이스로부터 모든 카운트 정보 리스트를 가져옵니다.
	public List<BlogCount> getCountInfoList() {
		try {
			LOG.info(GET_COUNT_INFO_LIST);
			return mapper.getCountInfoList();
		} catch (Exception e) {
			LOG.error(FAIL_GET_COUNT_INFO_LIST, e);
			return null;
		}
	}

	// 지정된 이름에 해당하는 카운트 정보를 가져옵니다.
	public BlogCount getCountInfo(String name) {
		try {
			LOG.info(GET_COUNT_INFO);
			return mapper.getCountInfo(name);
		} catch (Exception e) {
			LOG.error(FAIL_GET_COUNT_INFO, e);
			return null;
		}
	}
}
