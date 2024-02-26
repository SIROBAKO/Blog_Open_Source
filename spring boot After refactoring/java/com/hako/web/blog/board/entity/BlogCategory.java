package com.hako.web.blog.board.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BlogCategory {
	private int num;
	private String category;
	private String query;
	private String purpose;
	private int count;
}
