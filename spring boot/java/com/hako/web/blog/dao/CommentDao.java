package com.hako.web.blog.dao;

import java.util.List;

import com.hako.web.blog.entity.Blog_Comment;

public interface CommentDao {

    // 댓글 작성
    int insert(Blog_Comment comment);

    // 댓글 수정
    int update(Blog_Comment comment);

    // 댓글 삭제
    int del(int num);

    // 지정된 게시글에 해당하는 댓글 리스트를 가져옵니다.
    List<Blog_Comment> getList(int num);

    // 지정된 댓글 번호에 해당하는 댓글 정보를 가져옵니다.
    Blog_Comment get(int num);

    // 지정된 댓글의 하위 댓글 수를 반환합니다.
    int getRefCount(int ref);

    // 지정된 댓글의 하위 댓글들의 이메일 리스트를 반환합니다.
    List<String> getRefComment(int ref_comment);
    
    void updateUserName(String old_user_name,String new_user_name);
    
    void updateUserEmail(String old_user_email,String new_user_email);
    
   
}
