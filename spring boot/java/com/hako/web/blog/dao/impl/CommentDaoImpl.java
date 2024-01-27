package com.hako.web.blog.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hako.web.blog.dao.CommentDao;
import com.hako.web.blog.entity.Blog_Comment;

@Repository
public class CommentDaoImpl implements CommentDao {

	private CommentDao mapper;

	@Autowired
	public CommentDaoImpl(SqlSession sqlSession) {
		// SqlSession을 통해 CommentDao 인터페이스의 MyBatis 매퍼를 가져옵니다.
		mapper = sqlSession.getMapper(CommentDao.class);
	}

	// 댓글을 데이터베이스에 추가합니다.
	@Override
	public int insert(Blog_Comment comment) {
		return mapper.insert(comment);
	}

	// 댓글을 데이터베이스에서 수정합니다.
	@Override
	public int update(Blog_Comment comment) {
		return mapper.update(comment);
	}

	// 댓글을 데이터베이스에서 삭제합니다.
	@Override
	public int del(int num) {
		return mapper.del(num);
	}

	// 지정된 게시물에 대한 댓글 리스트를 가져옵니다.
	@Override
	public List<Blog_Comment> getList(int num) {
		return mapper.getList(num);
	}

	// 지정된 댓글 번호에 해당하는 댓글을 가져옵니다.
	@Override
	public Blog_Comment get(int num) {
		return mapper.get(num);
	}

	// 지정된 댓글의 대댓글 수를 가져옵니다.
	@Override
	public int getRefCount(int ref) {
		return mapper.getRefCount(ref);
	}

	// 지정된 댓글의 대댓글 이메일 리스트를 가져옵니다.
	@Override
	public List<String> getRefComment(int ref_comment) {
		return mapper.getRefComment(ref_comment);
	}

	@Override
	public void updateUserName(String old_user_name, String new_user_name) {
		// TODO Auto-generated method stub
		mapper.updateUserName(old_user_name, new_user_name);
		
	}

	@Override
	public void updateUserEmail(String old_user_email, String new_user_email) {
		// TODO Auto-generated method stub
		mapper.updateUserEmail(old_user_email, new_user_email);
	}

	

}
