package com.hako.web.blog.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Blog_Board {
	private int num;
	private String title;
	private String category;
	private String contents;
	private Date date;
	private String hidden;

	
	
}
