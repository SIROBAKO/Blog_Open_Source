package com.hako.web.blog.board.dao;

import java.util.List;

import com.hako.web.blog.board.entity.BlogCategory;

public interface CategoryDao {

  
    // 모든 게시글 카테고리를 가져옵니다.
    List<BlogCategory> getCategoryList();
    
    // 카테고리별 게시글 수 업뎃
    int updateCategory();
}
