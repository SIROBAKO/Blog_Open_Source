package com.hako.web.blog.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hako.web.blog.board.service.CountService;

@Component
public class BlogScheduler {
	@Autowired
	CountService countService;

	@Scheduled(cron = "30 59 23 * * *")
	public void resetCount() {
		countService.updateCountInfo();
	}

}