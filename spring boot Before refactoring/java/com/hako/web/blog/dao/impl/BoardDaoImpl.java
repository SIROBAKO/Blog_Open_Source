package com.hako.web.blog.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.dao.BoardDao;
import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;



@Repository
public class BoardDaoImpl implements BoardDao {

	private BoardDao mapper;

	@Autowired
	public BoardDaoImpl(SqlSession sqlSession) {
		// SqlSession을 통해 BoardDao 인터페이스의 MyBatis 매퍼를 가져옵니다.
		mapper = sqlSession.getMapper(BoardDao.class);
	}

	// 모든 카테고리 정보를 가져옵니다.
	@Override
	public List<Blog_Category> getCategory() {
		return mapper.getCategory();
	}

	// 지정된 카테고리와 게시물 번호를 기준으로 이전 게시물 리스트를 가져옵니다.
	@Override
	public List<Blog_Board> getCommendPrev(String category, int num, int count) {
		return mapper.getCommendPrev(category, num, count);
	}

	// 지정된 카테고리와 게시물 번호를 기준으로 다음 게시물 리스트를 가져옵니다.
	@Override
	public List<Blog_Board> getCommendNext(String category, int num, int count) {
		return mapper.getCommendNext(category, num, count);
	}

	// 추천 게시물(조회수 기반) 리스트를 가져옵니다.
	@Override
	public List<Blog_Board> getCommend() {
		return mapper.getCommend();
	}

	// 지정된 게시물 번호의 댓글 수를 감소시킵니다.
	@Override
	public void SUBComment(int id) {
		mapper.SUBComment(id);
	}

	// 지정된 게시물 번호의 댓글 수를 증가시킵니다.
	@Override
	public void ADDComment(int id) {
		mapper.ADDComment(id);
	}

	// 지정된 게시물 번호의 조회수를 증가시킵니다.
	@Override
	public void ADDCount(int id) {
		mapper.ADDCount(id);
	}

	// 지정된 게시물 번호에 해당하는 게시물을 가져옵니다.
	@Override
	public Blog_Board get(int id) {
		return mapper.get(id);
	}

	// 지정된 카테고리, 검색어, 숨김 여부에 따라 게시물 수를 가져옵니다.
	@Override
	public int getCount(String category, String query, String hidden) {
		return mapper.getCount(category, query, hidden);
	}

	// 지정된 카테고리, 검색어, 페이지 범위, 숨김 여부에 따라 게시물 리스트를 가져옵니다.
	@Override
	public List<Blog_Board> getList(String category, String query, int start, int end, String hidden) {
		return mapper.getList(category, query, start, end, hidden);
	}

	// 새로운 게시물을 데이터베이스에 추가합니다.
	@Override
	public int insert(Blog_Board board) {
		return mapper.insert(board);
	}

	// 기존 게시물을 데이터베이스에서 수정합니다.
	@Override
	public int update(Blog_Board board) {
		return mapper.update(board);
	}

	// 지정된 게시물 번호에 해당하는 게시물을 삭제합니다.
	@Override
	public int del(int id) {
		return mapper.del(id);
	}

	// 마지막으로 등록된 게시물을 가져옵니다.
	@Override
	public Blog_Board getLast() {
		return mapper.getLast();
	}

	// 카테고리 별 게시글 개수를 업데이트 합니다.
	@Override
	public int updateCategory() {
		return mapper.updateCategory();

	}

}
