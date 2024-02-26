package com.hako.web.blog.board.dao;

import java.util.List;

import com.hako.web.blog.board.entity.BlogCount;

public interface CountDao {

    // 카운트 항목을 초기화합니다.
    int resetTodayCount();

    // 지정된 카운트 항목에 값을 추가합니다.
    int addCountInfo(String name, int plus);

    // 모든 카운트 정보 리스트를 가져옵니다.
    List<BlogCount> getCountInfoList();

    // 지정된 카운트 항목의 카운트 값을 가져옵니다.
    BlogCount getCountInfo(String name);
}
