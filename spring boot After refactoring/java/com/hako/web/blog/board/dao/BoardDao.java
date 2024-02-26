package com.hako.web.blog.board.dao;

import java.util.List;

import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.board.entity.BlogCategory;

public interface BoardDao {

	// 게시글 목록을 반환합니다.
	List<BlogBoard> getBoardList(String category, String query, int start, int end, String hidden);

	// 조회수 기준으로 추천글 목록을 반환합니다.
	List<BlogBoard> getCommendBoardList();

	// 지정된 게시글 이후의 추천글 목록을 반환합니다.
	List<BlogBoard> getCommendNextBoardList(String category, int boardNum, int count);

	// 지정된 게시글 이전의 추천글 목록을 반환합니다.
	List<BlogBoard> getCommendPrevBoardList(String category, int boardNum, int count);

	// 지정된 ID에 해당하는 게시글을 반환합니다.
	BlogBoard getBoard(int boardNum);

	// 게시글 개수를 반환합니다.
	int getBoardCount(String category, String query, String hidden);

	// 지정된 게시글의 조회수를 증가시킵니다.
	void addViewCount(int boardNum);

	// 지정된 게시글의 댓글 수를 증가시킵니다.
	void addCommentCount(int boardNum);

	// 지정된 게시글의 댓글 수를 감소시킵니다.
	void subCommentCount(int boardNum);

	// 게시글을 등록합니다.
	int insertBoard(BlogBoard board);

	// 게시글을 수정합니다.
	int updateBoard(BlogBoard board);

	// 게시글을 삭제합니다.
	int deleteBoard(int boardNum);

	// 가장 최근에 등록된 게시글을 반환합니다.
	int getNewBoardNum();

}
