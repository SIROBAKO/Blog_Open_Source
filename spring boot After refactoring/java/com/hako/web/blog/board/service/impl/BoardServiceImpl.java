package com.hako.web.blog.board.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hako.web.blog.board.dao.BoardDao;
import com.hako.web.blog.board.dao.CategoryDao;
import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.board.service.BoardService;

@Service
@Transactional
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardDao boardDao;

	@Autowired
	private CategoryDao categoryDao;

	private static final String DEFAULT_VALUE = "";
	private static final int DEFAULT_PAGE_VALUE = 1;
	private static final int LIST_LENGTH = 6;

	private static final int SUCCECSS = 1;
	private static final int FAILE = 0;

	private static final String HIDDEN_BOARD_VALUE = "Y";
	private static final String VISIBLE_BOARD_VALUE = "N";

	// =================== 게시물 관련 메서드 ===================

	// 게시물 목록 구하기
	@Override
	public List<BlogBoard> getBoardList() {
		return getBoardList(DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_PAGE_VALUE);
	}

	@Override
	public List<BlogBoard> getBoardList(int page) {
		return getBoardList(DEFAULT_VALUE, DEFAULT_VALUE, page);
	}

	@Override
	public List<BlogBoard> getBoardList(String category, String query, int page) {
		int start = DEFAULT_PAGE_VALUE + (page - DEFAULT_PAGE_VALUE) * LIST_LENGTH;
		int end = page * LIST_LENGTH;
		return boardDao.getBoardList(category, query, start, end, VISIBLE_BOARD_VALUE);
	}

	@Override
	public List<BlogBoard> getHiddenBoardList() {
		return getBoardList(DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_PAGE_VALUE);
	}

	@Override
	public List<BlogBoard> getHiddenBoardList(int page) {
		return getBoardList(DEFAULT_VALUE, DEFAULT_VALUE, page);
	}

	@Override
	public List<BlogBoard> getHiddenBoardList(String category, String query, int page) {
		int start = DEFAULT_PAGE_VALUE + (page - DEFAULT_PAGE_VALUE) * LIST_LENGTH;
		int end = page * LIST_LENGTH;
		return boardDao.getBoardList(category, query, start, end, HIDDEN_BOARD_VALUE);
	}

	// 게시물 수 구하기
	@Override
	public int getBoardCount() {
		return getBoardCount(DEFAULT_VALUE, DEFAULT_VALUE);
	}

	@Override
	public int getBoardCount(String category, String query) {
		return boardDao.getBoardCount(category, query, VISIBLE_BOARD_VALUE);
	}

	@Override
	public int getHiddenBoardCount() {
		return getHiddenBoardCount(DEFAULT_VALUE, DEFAULT_VALUE);
	}

	@Override
	public int getHiddenBoardCount(String category, String query) {
		return boardDao.getBoardCount(category, query, HIDDEN_BOARD_VALUE);
	}

	// 게시물 반환
	public BlogBoard getBoard(int boardNum) {
		return boardDao.getBoard(boardNum);
	}

	// 최근 게시물 반환
	@Override
	public int getNewBoardNum() {
		return boardDao.getNewBoardNum();
	}

	// 게시글 작성
	@Override
	public int insertBoard(BlogBoard board) {
		if (categoryDao.updateCategory() != FAILE && boardDao.insertBoard(board) == SUCCECSS) {

			return SUCCECSS;
		} else {
			return FAILE;
		}
	}

	// 게시글 수정
	@Override
	public int updateBoard(BlogBoard board) {

		if (categoryDao.updateCategory() != FAILE && boardDao.updateBoard(board) == SUCCECSS) {
			return SUCCECSS;
		} else {
			return FAILE;
		}
	}

	// 게시글 삭제
	@Override
	public int deleteBoard(int boardNum) {
		if (categoryDao.updateCategory() != FAILE && boardDao.deleteBoard(boardNum) == SUCCECSS) {
			return SUCCECSS;
		} else {
			return FAILE;
		}
	}

	// 조회수 상승
	@Override
	public void addViewCount(int boardNum) {
		boardDao.addViewCount(boardNum);
	}

	// 댓글 수 상승
	@Override
	public void addCommentCount(int boardNum) {
		boardDao.addCommentCount(boardNum);
	}

	// 댓글 수 감소
	@Override
	public void subCommentCount(int boardNum) {
		boardDao.subCommentCount(boardNum);
	}

	// 추천 게시물 반환
	@Override
	public List<BlogBoard> getCommendBoardList() {
		return boardDao.getCommendBoardList();
	}

	// 다음 게시물 반환
	public List<BlogBoard> getCommendNextBoardList(String category, int boardNum, int count) {
		return boardDao.getCommendNextBoardList(category, boardNum, count);
	}

	// 이전 게시물 반환
	public List<BlogBoard> getCommendPrevBoardList(String category, int boardNum, int count) {
		return boardDao.getCommendPrevBoardList(category, boardNum, count);
	}

}
