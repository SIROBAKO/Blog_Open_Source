package com.hako.web.blog.board.service;

import java.util.List;

import com.hako.web.blog.board.entity.BlogCategory;

public interface CategoryService {

	
	// 카테고리 목록 반환
	List<BlogCategory> getCategoryList();

	
}
