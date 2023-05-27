package com.hako.web.blog.mybatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.dao.BoardDao;
import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;

@Repository
public class MybaticeBoardDao implements BoardDao {
	

	private BoardDao mapper;
	
	@Autowired
	public MybaticeBoardDao( SqlSession sqlSession) {
		mapper = sqlSession.getMapper(BoardDao.class);
	}
	
	

	@Override
	public List<Blog_Board> getList(String category, String query, int start, int end , String hidden) {

		System.out.println(hidden);
		return mapper.getList(category, query, start, end, hidden);
	}

	@Override
	public int getCount(String category, String query,String hidden) {
		
		return mapper.getCount(category, query, hidden);
	}

	@Override
	public Blog_Board get(int id) {
		// TODO Auto-generated method stub
		return mapper.get(id);
	}

	@Override
	public void ADDCount(int id) {

		mapper.ADDCount(id);
		
	}

	@Override
	public void ADDComment(int id) {
		mapper.ADDComment(id);
	}

	@Override
	public void SUBComment(int id) {
		mapper.SUBComment(id);
	}

	@Override
	public List<Blog_Board> getCommend() {
		// TODO Auto-generated method stub
		return mapper.getCommend();
	}

	@Override
	public List<Blog_Board> getCommendNext(String category, int num, int count) {
		// TODO Auto-generated method stub
		return mapper.getCommendNext(category, num, count);
	}

	@Override
	public List<Blog_Board> getCommendPrev(String category, int num, int count) {
		// TODO Auto-generated method stub
		return mapper.getCommendPrev(category, num, count);
		
	}

	@Override
	public int insert(Blog_Board board) {
		// TODO Auto-generated method stub
		return mapper.insert(board);
	}

	@Override
	public int update(Blog_Board board) {
		// TODO Auto-generated method stub
		return mapper.update(board);
	}

	@Override
	public int del(int id) {
		// TODO Auto-generated method stub
		return mapper.del(id);
	}



	@Override
	public List<Blog_Category> getCategory() {
		// TODO Auto-generated method stub
		return mapper.getCategory();
	}



	@Override
	public Blog_Board getLast() {
		// TODO Auto-generated method stub
		return mapper.getLast();
	}

}
