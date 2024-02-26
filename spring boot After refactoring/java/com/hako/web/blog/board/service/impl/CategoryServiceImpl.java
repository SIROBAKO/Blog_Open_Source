package com.hako.web.blog.board.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hako.web.blog.board.dao.CategoryDao;
import com.hako.web.blog.board.entity.BlogCategory;
import com.hako.web.blog.board.service.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryDao categoryDao;

	
	// 카테고리 목록 반환
	@Override
	public List<BlogCategory> getCategoryList() {
		return categoryDao.getCategoryList();
	}

}
