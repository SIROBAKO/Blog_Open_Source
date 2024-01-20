package com.hako.web.blog.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Blog_Comment {

	private Integer num;
	private Integer ref;
	private Integer ref_comment;
	private String comment;
	private Date create_date;
	private String user_name;
	private String pwd;
	private String email;

}
