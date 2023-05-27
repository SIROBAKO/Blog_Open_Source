package com.hako.web.blog.dao;

import java.util.List;

import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;


public interface BoardDao {

//	게시글 목록 반환
	List<Blog_Board> getList(String category, String query, int start, int end,String hidden);

//	게시글 개수 반환
	int getCount(String category, String query,String hidden);

//	게시글 반환
	Blog_Board get(int id);


//	조회수 증가
	void ADDCount(int id);

//	댓글수 증가
	void ADDComment(int id);

//	댓글수 감소
	void SUBComment(int id);

//	추천글 반환 조회수 기준
	List<Blog_Board> getCommend();

// 현재글 기준 다음글 목록 반환
	List<Blog_Board> getCommendNext(String category, int num, int count);

// 현재글 기준 이전글 목록 반환
	List<Blog_Board> getCommendPrev(String category, int num, int count);

//	게시글 등록
	int insert(Blog_Board board);

//	게시글 수정
	int update(Blog_Board board);

//	게시글 삭제
	int del(int id);

	List<Blog_Category> getCategory();


	Blog_Board getLast();
}
