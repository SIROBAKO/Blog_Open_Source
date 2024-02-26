package com.hako.web.blog.board.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.board.dao.BoardDao;
import com.hako.web.blog.board.entity.BlogBoard;
import com.hako.web.blog.user.controller.UserRestController;

@Repository
public class BoardDaoImpl implements BoardDao {

	private BoardDao mapper;

	private Logger LOG = LogManager.getLogger(BoardDaoImpl.class);

	private static final String GET_PREV_BOARD_LIST = "이전 게시물 리스트를 가져오기 ";
	private static final String GET_NEXT_BOARD_LIST = "다음 게시물 리스트를 가져오기";
	private static final String GET_COMMEND_BOARD_LIST = "추천 게시물 리스트를 가져오기";
	private static final String GET_BOARD = "게시물을 가져오기";
	private static final String GET_BOARD_COUNT = "게시물 수를 가져오기";
	private static final String GET_BOARD_LIST = "게시물 리스트를 가져기";
	private static final String INSERT_BOARD = "게시물을 추가";
	private static final String UPDATE_BOARD = "게시물을 수정";
	private static final String DELETE_BOARD = "게시물을 삭제";
	private static final String GET_NEW_BOARD_NUM = "최신 게시물 번호를 가져오기";
	private static final String SUB_COMMENT_COUNT = "게시물 댓글 수 감소";
	private static final String ADD_COMMENT_COUNT = "게시물 댓글 수 증가";
	private static final String ADD_VIEW_COUNT = "게시물 조회수 증가";

	private static final String FAIL_GET_PREV_BOARD_LIST = "이전 게시물 리스트를 가져오기 실패";
	private static final String FAIL_GET_NEXT_BOARD_LIST = "다음 게시물 리스트를 가져오기 실패";
	private static final String FAIL_GET_COMMEND_BOARD_LIST = "추천 게시물 리스트를 가져오기 실패";
	private static final String FAIL_GET_BOARD = "게시물을 가져오기 실패";
	private static final String FAIL_GET_BOARD_COUNT = "게시물 수를 가져오기 실패";
	private static final String FAIL_GET_BOARD_LIST = "게시물 리스트를 가져기 실패";
	private static final String FAIL_INSERT_BOARD = "게시물을 추가 실패";
	private static final String FAIL_UPDATE_BOARD = "게시물을 수정 실패";
	private static final String FAIL_DELETE_BOARD = "게시물을 삭제 실패";
	private static final String FAIL_GET_NEW_BOARD_NUM = "최신 게시물 번호를 가져오기 실패";
	private static final String FAIL_SUB_COMMENT_COUNT = "게시물 댓글 수 감소 실패";
	private static final String FAIL_ADD_COMMENT_COUNT = "게시물 댓글 수 증가 실패";
	private static final String FAIL_ADD_VIEW_COUNT = "게시물 조회수 증가 실패";

	@Autowired
	public BoardDaoImpl(SqlSession sqlSession) {

		mapper = sqlSession.getMapper(BoardDao.class);
	}

	// 지정된 카테고리와 게시물 번호를 기준으로 이전 게시물 리스트를 가져옵니다.
	@Override
	public List<BlogBoard> getCommendPrevBoardList(String category, int boardNum, int count) {
		try {
			LOG.info(GET_PREV_BOARD_LIST);
			return mapper.getCommendPrevBoardList(category, boardNum, count);
		} catch (Exception e) {
			LOG.error(FAIL_GET_PREV_BOARD_LIST, e);
			return null;
		}
	}

	// 지정된 카테고리와 게시물 번호를 기준으로 다음 게시물 리스트를 가져옵니다.
	@Override
	public List<BlogBoard> getCommendNextBoardList(String category, int boardNum, int count) {
		try {
			LOG.info(GET_NEXT_BOARD_LIST);
			return mapper.getCommendNextBoardList(category, boardNum, count);
		} catch (Exception e) {
			LOG.error(FAIL_GET_NEXT_BOARD_LIST, e);
			return null;
		}
	}

	// 추천 게시물(조회수 기반) 리스트를 가져옵니다.
	@Override
	public List<BlogBoard> getCommendBoardList() {
		try {
			LOG.info(GET_COMMEND_BOARD_LIST);
			return mapper.getCommendBoardList();
		} catch (Exception e) {
			LOG.error(FAIL_GET_COMMEND_BOARD_LIST, e);
			return null;
		}
	}

	// 지정된 게시물 번호의 댓글 수를 감소시킵니다.
	@Override
	public void subCommentCount(int boardNum) {
		try {
			LOG.info(SUB_COMMENT_COUNT);
			mapper.subCommentCount(boardNum);
		} catch (Exception e) {
			LOG.error(FAIL_SUB_COMMENT_COUNT, e);
		}
	}

	// 지정된 게시물 번호의 댓글 수를 증가시킵니다.
	@Override
	public void addCommentCount(int boardNum) {
		try {
			LOG.info(ADD_COMMENT_COUNT);
			mapper.addCommentCount(boardNum);
		} catch (Exception e) {
			LOG.error(FAIL_ADD_COMMENT_COUNT, e);
		}
	}

	// 지정된 게시물 번호의 조회수를 증가시킵니다.
	@Override
	public void addViewCount(int boardNum) {
		try {
			LOG.info(ADD_VIEW_COUNT);
			mapper.addViewCount(boardNum);
		} catch (Exception e) {
			LOG.error(FAIL_ADD_VIEW_COUNT, e);
		}
	}

	// 지정된 게시물 번호에 해당하는 게시물을 가져옵니다.
	@Override
	public BlogBoard getBoard(int boardNum) {
		try {
			LOG.info(GET_BOARD);
			return mapper.getBoard(boardNum);
		} catch (Exception e) {
			LOG.error(FAIL_GET_BOARD, e);
			return null;
		}
	}

	// 지정된 카테고리, 검색어, 숨김 여부에 따라 게시물 수를 가져옵니다.
	@Override
	public int getBoardCount(String category, String query, String hidden) {
		try {
			LOG.info(GET_BOARD_COUNT);
			return mapper.getBoardCount(category, query, hidden);
		} catch (Exception e) {
			LOG.error(FAIL_GET_BOARD_COUNT, e);
			return 0;
		}
	}

	// 지정된 카테고리, 검색어, 페이지 범위, 숨김 여부에 따라 게시물 리스트를 가져옵니다.
	@Override
	public List<BlogBoard> getBoardList(String category, String query, int start, int end, String hidden) {

		try {
			LOG.info(GET_BOARD_LIST);
			return mapper.getBoardList(category, query, start, end, hidden);
		} catch (Exception e) {
			LOG.error(FAIL_GET_BOARD_LIST, e);
			return null;
		}
	}

	// 새로운 게시물을 데이터베이스에 추가합니다.
	@Override
	public int insertBoard(BlogBoard board) {
		try {
			LOG.info(INSERT_BOARD);
			return mapper.insertBoard(board);
		} catch (Exception e) {
			LOG.error(FAIL_INSERT_BOARD, e);
			return 0;
		}
	}

	// 기존 게시물을 데이터베이스에서 수정합니다.
	@Override
	public int updateBoard(BlogBoard board) {
		try {
			LOG.info(UPDATE_BOARD);
			return mapper.updateBoard(board);
		} catch (Exception e) {
			LOG.error(FAIL_UPDATE_BOARD, e);
			return 0;
		}
	}

	// 지정된 게시물 번호에 해당하는 게시물을 삭제합니다.
	@Override
	public int deleteBoard(int boardNum) {
		try {
			LOG.info(DELETE_BOARD);
			return mapper.deleteBoard(boardNum);
		} catch (Exception e) {
			LOG.error(FAIL_DELETE_BOARD, e);
			return 0;
		}
	}

	// 마지막으로 등록된 게시물을 가져옵니다.
	@Override
	public int getNewBoardNum() {
		try {
			LOG.info(GET_NEW_BOARD_NUM);
			return mapper.getNewBoardNum();
		} catch (Exception e) {
			LOG.error(FAIL_GET_NEW_BOARD_NUM, e);
			return 0;
		}
	}

}
