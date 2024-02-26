package com.hako.web.blog.board.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.board.dao.CategoryDao;
import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.board.entity.BlogCategory;



@Repository
public class CategoryDaoImpl implements CategoryDao {

	private CategoryDao mapper;

	private Logger LOG = LogManager.getLogger(CategoryDaoImpl.class);

	
	private static final String GET_CATEGORY_LIST = "카테고리 목록 가져오기";
	private static final String UPDATE_CATEGORY = "카테고리 업데이트";

	private static final String FAIL_GET_CATEGORY_LIST = "카테고리 목록 가져오기 실패";
	private static final String FAIL_UPDATE_CATEGORY = "카테고리 업데이트 실패";
	
	@Autowired
	public CategoryDaoImpl(SqlSession sqlSession) {
		// SqlSession을 통해 BoardDao 인터페이스의 MyBatis 매퍼를 가져옵니다.
		mapper = sqlSession.getMapper(CategoryDao.class);
	}

	@Override
	public List<BlogCategory> getCategoryList() {
		try {
			LOG.info(GET_CATEGORY_LIST);
			return mapper.getCategoryList();
		} catch (Exception e) {
			LOG.error(FAIL_GET_CATEGORY_LIST,e);
			return null;
		}
	}

	@Override
	public int updateCategory() {
		try {
			LOG.info(UPDATE_CATEGORY);
			return mapper.updateCategory();
		} catch (Exception e) {
			LOG.error(FAIL_UPDATE_CATEGORY,e);
			return 0;
		}
	}


}
