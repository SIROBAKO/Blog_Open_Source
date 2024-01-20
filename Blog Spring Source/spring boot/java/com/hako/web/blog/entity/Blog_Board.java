package com.hako.web.blog.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Blog_Board {
	private int num;
	private String title;
	private String description;
	private String category;
	private String contents;
	private Date date;
	private String hidden;

	
	
}
