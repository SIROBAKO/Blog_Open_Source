package com.hako.web.blog.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.dao.CountDao;
import com.hako.web.blog.entity.Blog_Count;

@Repository
public class CountDaoImpl implements CountDao {

	private CountDao mapper;

	@Autowired
	public CountDaoImpl(SqlSession sqlSession) {
		// SqlSession을 통해 CountDao 인터페이스의 MyBatis 매퍼를 가져옵니다.
		mapper = sqlSession.getMapper(CountDao.class);
	}

	// 오늘의 방문자 수와 총 방문자 수를 0으로 초기화합니다.
	public int resetCount() {
		return mapper.resetCount();
	}

	// 지정된 이름에 해당하는 카운트에 값을 더합니다.
	public int addCount(String name, int plus) {
		return mapper.addCount(name, plus);
	}

	// 데이터베이스로부터 모든 카운트 정보 리스트를 가져옵니다.
	public List<Blog_Count> getList() {
		return mapper.getList();
	}

	// 오늘의 방문자 수를 카운트합니다.
	public int countVisiter() {
		return mapper.countVisiter();
	}

	// 지정된 이름에 해당하는 카운트 정보를 가져옵니다.
	public Blog_Count get(String name) {
		return mapper.get(name);
	}
}
