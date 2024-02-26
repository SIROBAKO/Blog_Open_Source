package com.hako.web.blog.board.service;

import java.util.List;

import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.board.entity.BlogCategory;

public interface BoardService {

	// =================== 게시물 관련 메서드 ===================

	// 게시물 목록 반환
	List<BlogBoard> getBoardList();

	List<BlogBoard> getBoardList(int page);

	List<BlogBoard> getBoardList(String category, String query, int page);

	List<BlogBoard> getHiddenBoardList();

	List<BlogBoard> getHiddenBoardList(int page);

	List<BlogBoard> getHiddenBoardList(String category, String query, int page);

	// 게시물 개수 반환
	int getBoardCount();

	int getBoardCount(String category, String query);

	int getHiddenBoardCount();

	int getHiddenBoardCount(String category, String query);

	// 게시물 반환
	BlogBoard getBoard(int boardNum);

	// 마지막 게시물 반환
	int getNewBoardNum();

	// 게시물 등록
	int insertBoard(BlogBoard board);

	// 게시물 수정
	int updateBoard(BlogBoard board);

	// 게시물 삭제
	int deleteBoard(int boardNum);

	// 추천게시물(조회수 기반) 반환
	List<BlogBoard> getCommendBoardList();

	// 다음 게시물 반환
	List<BlogBoard> getCommendNextBoardList(String category, int boardNum, int count);

	// 이전 게시물 반환
	List<BlogBoard> getCommendPrevBoardList(String category, int boardNum, int count);

	// 게시글 조회수 상승
	void addViewCount(int boardNum);

	// 게시글 댓글 수 상승
	void addCommentCount(int boardNum);

	// 게시글 댓글 수 감소
	void subCommentCount(int boardNum);

}
