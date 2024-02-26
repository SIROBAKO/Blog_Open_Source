package com.hako.web.blog.commet.service;

import java.util.List;

import com.hako.web.blog.commet.entity.BlogComment;

public interface CommentService {

	// 댓글 작성
	boolean insertComment(BlogComment comment);

	// 댓글 삭제
	int deleteComment(BlogComment comment);

	// 댓글 반환
	List<BlogComment> getCommentList(int num);

	// 댓글 수정
	int updateComment(BlogComment comment);

	// 댓글 수정
	void updateCommentName(String old_user_name, String new_user_name);

	// 댓글 수정
	void updateCommentEmail(String old_user_email, String new_user_email);

	
	int getLastCommentNum();
}
