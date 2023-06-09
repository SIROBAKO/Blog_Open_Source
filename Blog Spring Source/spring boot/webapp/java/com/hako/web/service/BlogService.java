package com.hako.web.blog.service;


import java.util.List;

import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;
import com.hako.web.blog.entity.Blog_Comment;

public interface BlogService {

//	게시물 목록 반환
	List<Blog_Board> getBoardList(String hidden);

	List<Blog_Board> getBoardList(int page ,String hidden);

	List<Blog_Board> getBoardList(String category, String query, int page, String hidden);

//	게시물 개수 반환
	int getBoardCount(String hidden);

	int getBoardCount(String category, String query,String hidden);

//	게시물 반환
	Blog_Board getBoard(int id);

//	게시물등록
	int InsertBoard(Blog_Board board);

//	게시물수정
	int UpdateBoard(Blog_Board board);

//	게시물 삭제
	int DelBoard(int id);

//	추천게시물(조회수기반) 반환
	List<Blog_Board> getCommendBoard();
	
//   다음 게시물 반환
	 List<Blog_Board> getCommendNextBoard(String category,int num, int count);

//   이전 게시물 반환
	 List<Blog_Board> getCommendPrevBoard(String category,int num, int count);


//	게시글 조회수 상승
	void ADDCount(int id);
	
//	게시글 댓글 수 상승
	void ADDComment(int id);
	
//	게시글 댓글 수 감소
	void SUBComment(int id);

//	댓글작성
	int InsertComment(Blog_Comment comment);

//	댓글 수정
	int UpdateComment(Blog_Comment comment);

//	댓글 삭제
	int DelComment(Blog_Comment comment);
	
//	댓글 반환
	List<Blog_Comment> getCommentList(int num);

	List<Blog_Category> getCategory();

	Blog_Board getBoard();
	
	

}
