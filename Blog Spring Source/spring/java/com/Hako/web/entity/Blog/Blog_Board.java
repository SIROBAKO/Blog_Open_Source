package com.Hako.web.entity.Blog;

import java.util.Date;

public class Blog_Board {
	int num;
	String title;
	String category;
	String contents;
	Date date;
	
	
	public Blog_Board() {
		// TODO Auto-generated constructor stub
	}


	public Blog_Board(int num, String title, String category, String contents, Date date) {
		
		this.num = num;
		this.title = title;
		this.category = category;
		this.contents = contents;
		this.date = date;
	}


	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public String getContents() {
		return contents;
	}


	public void setContents(String contents) {
		this.contents = contents;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
