package com.hako.web.blog.commet.entity;

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
public class BlogComment {

	private Integer num;
	private Integer board_num;
	private Integer ref_comment;
	private String comment;
	private Date create_date;
	private String user_name;
	private String pwd;
	private String email;

	
}
