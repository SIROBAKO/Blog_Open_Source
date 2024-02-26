package com.hako.web.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	@Column(name = "USER_ID")
    String user_id;
	
	@Column(name = "ACCESS_TOKEN", nullable = false)
    String access_token;
	
	@Column(name = "REFRESH_TOKEN", nullable = false)
    String refresh_token;
	
}
