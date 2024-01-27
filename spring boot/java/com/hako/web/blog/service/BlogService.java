package com.hako.web.blog.service;

import java.util.List;

import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;
import com.hako.web.blog.entity.Blog_Comment;
import com.hako.web.blog.entity.Blog_Count;

public interface BlogService {

	// =================== 게시물 관련 메서드 ===================

	// 게시물 목록 반환
	List<Blog_Board> getBoardList(String hidden);

	List<Blog_Board> getBoardList(int page, String hidden);

	List<Blog_Board> getBoardList(String category, String query, int page, String hidden);

	// 게시물 개수 반환
	int getBoardCount(String hidden);

	int getBoardCount(String category, String query, String hidden);

	// 게시물 반환
	Blog_Board getBoard(int id);

	// 마지막 게시물 반환
	Blog_Board getLastBoard();

	// 게시물 등록
	int insertBoard(Blog_Board board);

	// 게시물 수정
	int updateBoard(Blog_Board board);

	// 게시물 삭제
	int delBoard(int id);

	// =================== 추천 게시물 관련 메서드 ===================

	// 추천게시물(조회수 기반) 반환
	List<Blog_Board> getCommendBoard();

	// 다음 게시물 반환
	List<Blog_Board> getCommendNextBoard(String category, int num, int count);

	// 이전 게시물 반환
	List<Blog_Board> getCommendPrevBoard(String category, int num, int count);

	// =================== 조회수 및 댓글 수 관련 메서드 ===================

	// 게시글 조회수 상승
	void addCount(int id);

	// 게시글 댓글 수 상승
	void addComment(int id);

	// 게시글 댓글 수 감소
	void subComment(int id);

	// =================== 댓글 관련 메서드 ===================

	// 댓글 작성
	int insertComment(Blog_Comment comment);

	// 댓글 수정
	int updateComment(Blog_Comment comment);

	// 댓글 삭제
	int delComment(Blog_Comment comment);

	// 댓글 반환
	List<Blog_Comment> getCommentList(int num);

	// 댓글 수정
	void updateCommentName(String old_user_name, String new_user_name);

	// 댓글 수정
	void updateCommentEmail(String old_user_email, String new_user_email);

	// =================== 카테고리, 카운트 등 관련 메서드 ===================

	// 카테고리 목록 반환
	List<Blog_Category> getCategory();

	// 카운트 더하기 (오늘 조회수, 총 조회수, 오늘 방문자, 총 방문자)
	int addTotalCount(String name, int plus);

	// 00시에 업데이트 후 초기화
	void updateCount();

	// 각종 카운트 조회
	List<Blog_Count> getCount();

	// 특정 카운트 정보 반환
	Blog_Count get(String name);

	// 댓글에 대한 피드백 정보 반환
	List<String> feedBackComment(int ref_comment);

}
