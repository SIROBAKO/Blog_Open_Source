package com.hako.web.blog.board.entity;

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
public class BlogBoard {

	private int num;
	private String title;
	private String description;
	private String category;
	private String contents;
	private Date date;
	private String hidden;

	public BlogBoard(String num, String title, String description, String category, String contents, Date date,
			String hidden) {
		this.num = Integer.parseInt(num);
		this.title = title;
		this.description = description;
		this.category = category;
		this.contents = contents;
		this.date = date;
		this.hidden = hidden;
	}
}
