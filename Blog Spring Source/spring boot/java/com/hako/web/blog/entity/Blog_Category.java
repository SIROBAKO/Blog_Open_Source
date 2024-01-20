package com.hako.web.blog.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Blog_Category {
	private int num;
	private String category;
	private String query;
	private String purpose;
	private int count;
}
