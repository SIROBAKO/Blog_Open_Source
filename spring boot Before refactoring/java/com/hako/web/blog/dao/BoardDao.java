package com.hako.web.blog.dao;

import java.util.List;

import com.hako.web.blog.entity.Blog_Board;
import com.hako.web.blog.entity.Blog_Category;

public interface BoardDao {

    // 게시글 목록을 반환합니다.
    List<Blog_Board> getList(String category, String query, int start, int end, String hidden);

    // 게시글 개수를 반환합니다.
    int getCount(String category, String query, String hidden);

    // 지정된 ID에 해당하는 게시글을 반환합니다.
    Blog_Board get(int id);

    // 지정된 게시글의 조회수를 증가시킵니다.
    void ADDCount(int id);

    // 지정된 게시글의 댓글 수를 증가시킵니다.
    void ADDComment(int id);

    // 지정된 게시글의 댓글 수를 감소시킵니다.
    void SUBComment(int id);

    // 조회수 기준으로 추천글 목록을 반환합니다.
    List<Blog_Board> getCommend();

    // 지정된 게시글 이후의 추천글 목록을 반환합니다.
    List<Blog_Board> getCommendNext(String category, int num, int count);

    // 지정된 게시글 이전의 추천글 목록을 반환합니다.
    List<Blog_Board> getCommendPrev(String category, int num, int count);

    // 게시글을 등록합니다.
    int insert(Blog_Board board);

    // 게시글을 수정합니다.
    int update(Blog_Board board);

    // 게시글을 삭제합니다.
    int del(int id);

    // 모든 게시글 카테고리를 가져옵니다.
    List<Blog_Category> getCategory();

    // 가장 최근에 등록된 게시글을 반환합니다.
    Blog_Board getLast();
    
    // 카테고리별 게시글 수 업뎃
    int updateCategory();
}
