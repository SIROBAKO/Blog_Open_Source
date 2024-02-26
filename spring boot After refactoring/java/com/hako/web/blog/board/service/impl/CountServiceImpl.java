package com.hako.web.blog.board.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hako.web.blog.board.dao.BoardDao;
import com.hako.web.blog.board.dao.CountDao;
import com.hako.web.blog.board.entity.BlogCategory;
import com.hako.web.blog.board.entity.BlogCount;
import com.hako.web.blog.board.service.CountService;

@Service
@Transactional
public class CountServiceImpl implements CountService {


	@Autowired
	private CountDao countDao;

	
	private static final int ADD_ONE = 1;

	// 카운트 더하기 (오늘 조회수, 총 조회수, 오늘 방문자, 총 방문자)
	@Override
	public int addTotalCount(String name) {
		return countDao.addCountInfo(name, ADD_ONE);
	}
	@Override
	public int addTotalCount(String name, int plus) {
		return countDao.addCountInfo(name, plus);
	}

	// 각종 카운트 조회
	@Override
	public List<BlogCount> getCountInfoList() {
		List<BlogCount> list = countDao.getCountInfoList();
		return list;
	}

	@Override
	public BlogCount getCountInfo(String name) {
		return countDao.getCountInfo(name);
	}

	// 00시에 업데이트 후 초기화
	@Override
	public void updateCountInfo() {
		countDao.addCountInfo("ALL_COUNT", countDao.getCountInfo("TODAY_COUNT").getCount());
		countDao.addCountInfo("ALL_VISITER", countDao.getCountInfo("TODAY_VISITER").getCount());
		countDao.resetTodayCount();
	}


}
