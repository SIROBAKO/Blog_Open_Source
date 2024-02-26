package com.hako.web.user.entity;

import java.util.Date;

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
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "BLOG_USER")
public class BlogUser {

	@Id
	@Column(name = "ID", length = 30)
	String id;

	@Column(name = "NAME", unique = true, length = 20, nullable = false)
	String name;

	@Column(name = "PASSWORD", length = 200, nullable = false)
	String pwd;

	@Column(name = "EMAIL", unique = true, length = 100, nullable = false)
	String email;

	@Column(name = "CREATE_DATE", nullable = false)
	Date create_date;
}
