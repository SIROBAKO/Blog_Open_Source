package com.hako.web.blog.dao;

import java.util.List;

import com.hako.web.blog.entity.Blog_Count;

public interface CountDao {

    // 카운트 항목을 초기화합니다.
    int resetCount();

    // 지정된 카운트 항목에 값을 추가합니다.
    int addCount(String name, int plus);

    // 모든 카운트 정보 리스트를 가져옵니다.
    List<Blog_Count> getList();

    // 오늘 방문자 수를 가져옵니다.
    int countVisiter();

    // 지정된 카운트 항목의 카운트 값을 가져옵니다.
    Blog_Count get(String name);
}
