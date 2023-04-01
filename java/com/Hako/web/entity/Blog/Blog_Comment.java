package com.Hako.web.entity.Blog;

import java.util.Date;

public class Blog_Comment {

	int id;
	int ref;
	int ref_comment;
	String comment;
	Date create_date;
	String user_name;
	String pwd;

	
	public Blog_Comment() {
		// TODO Auto-generated constructor stub
	}


	public Blog_Comment(int id, int ref, int ref_comment, String comment, Date cerate_date, String user_name,
			String pwd) {
	
		this.id = id;
		this.ref = ref;
		this.ref_comment = ref_comment;
		this.comment = comment;
		this.create_date = cerate_date;
		this.user_name = user_name;
		this.pwd = pwd;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getRef() {
		return ref;
	}


	public void setRef(int ref) {
		this.ref = ref;
	}


	public int getRef_comment() {
		return ref_comment;
	}


	public void setRef_comment(int ref_comment) {
		this.ref_comment = ref_comment;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public Date getCreate_date() {
		return create_date;
	}


	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}


	public String getUser_name() {
		return user_name;
	}


	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}


	public String getPwd() {
		return pwd;
	}


	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
