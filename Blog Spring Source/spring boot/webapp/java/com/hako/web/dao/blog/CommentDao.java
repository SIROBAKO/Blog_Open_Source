package com.hako.web.dao.blog;

import java.util.List;

import com.hako.web.entity.blog.Blog_Comment;


public interface CommentDao {

//	댓글 작성
	int insert(Blog_Comment comment) ;
	
//	댓글 수정
	int update(Blog_Comment comment);
	
//	댓글 삭제
	int del(int id);
	
//	댓글 리스트 반환
	List<Blog_Comment> getList(int num);
	
//	댓글 반환
	Blog_Comment get(int id);
	
//	하위 댓글 수 반환
	int getRefCount(int ref);
}
