package com.hako.web.mybatis.blog;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.dao.blog.CommentDao;
import com.hako.web.entity.blog.Blog_Comment;

@Repository
public class MybaticeCommentDao implements CommentDao {
	

	private CommentDao mapper;
	
	@Autowired
	public MybaticeCommentDao( SqlSession sqlSession) {
		mapper = sqlSession.getMapper(CommentDao.class);
	}

	@Override
	public int insert(Blog_Comment comment) {
		// TODO Auto-generated method stub
		return mapper.insert(comment);
	}

	@Override
	public int update(Blog_Comment comment) {
		// TODO Auto-generated method stub
		return mapper.update(comment);
	}

	@Override
	public int del(int id) {
		// TODO Auto-generated method stub
		return mapper.del(id);
	}

	@Override
	public List<Blog_Comment> getList(int num) {
		// TODO Auto-generated method stub
		return mapper.getList(num);
	}

	@Override
	public Blog_Comment get(int id) {
		// TODO Auto-generated method stub
		return mapper.get(id);
	}

	@Override
	public int getRefCount(int ref) {
		// TODO Auto-generated method stub
		return mapper.getRefCount(ref);
	}
	


}
