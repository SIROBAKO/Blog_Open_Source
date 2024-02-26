package com.hako.web.blog.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BLOG_TOKEN")
public class BlogToken {

	@Id
	@Column(name = "USER_NUM")
	long user_num;

	@Column(name = "ACCESS_TOKEN", nullable = false)
	String access_token;

	@Column(name = "REFRESH_TOKEN", nullable = false)
	String refresh_token;

	public void setUser_num(String userNum) {
		this.user_num = Long.parseLong(userNum);
	}

	public void setUser_num(long userNum) {
		this.user_num = userNum;
	}

	public BlogToken(String userNum, String access_token, String refresh_token) {
		this.user_num = Long.parseLong(userNum);
		this.access_token = access_token;
		this.refresh_token = refresh_token;
	}

}
