package com.hako.web.blog.user.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long num;

	@Column(name = "ID", length = 30)
	private String id;

	@Column(name = "NAME", unique = true, length = 20, nullable = false)
	private String name;

	@Column(name = "PASSWORD", length = 200, nullable = false)
	private String pwd;

	@Column(name = "EMAIL", unique = true, length = 100, nullable = false)
	private String email;

	@Column(name = "CREATE_DATE", nullable = true)
	private Date create_date;

	@PrePersist
	protected void onCreate() {
		create_date = new Date(); // 현재 날짜와 시간으로 설정
	}

	public void setNum(String userNum) {
		this.num = Long.parseLong(userNum);
	}

	public void setNum(long userNum) {
		this.num = userNum;
	}

	public boolean isValidLoginForm() {

		try {
			return this.id != null && this.pwd != null;
		} catch (Exception e) {
			return false;
		}

	}

	public String getNumToString() {
		// TODO Auto-generated method stub
		return this.num + "";
	}

	public BlogUser deepCopy() {
		BlogUser copiedUser = new BlogUser();
		copiedUser.setNum(this.num);
		copiedUser.setId(this.id);
		copiedUser.setName(this.name);
		copiedUser.setPwd(this.pwd);
		copiedUser.setEmail(this.email);
		copiedUser.setCreate_date(new Date(this.create_date.getTime())); // 날짜 객체는 복사하여 새로 생성

		return copiedUser;
	}

}
