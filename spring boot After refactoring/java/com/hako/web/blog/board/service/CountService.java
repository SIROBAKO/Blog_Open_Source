package com.hako.web.blog.board.service;

import java.util.List;

import com.hako.web.blog.board.entity.BlogCategory;
import com.hako.web.blog.board.entity.BlogCount;

public interface CountService {

	
	// 카운트 더하기 (오늘 조회수, 총 조회수, 오늘 방문자, 총 방문자)
	int addTotalCount(String name);

	int addTotalCount(String name, int plus);

	// 00시에 업데이트 후 초기화
	void updateCountInfo();

	// 각종 카운트 조회
	List<BlogCount> getCountInfoList();

	// 특정 카운트 정보 반환
	BlogCount getCountInfo(String name);

}
