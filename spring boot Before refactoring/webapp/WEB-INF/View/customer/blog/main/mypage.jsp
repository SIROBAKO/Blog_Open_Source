<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="Main-Content">
	<form class="user-form">
		<h1>
			HAKO<span>blog</span>
		</h1>
		
		<label for="user_name_form">닉네임<span id ="name_message"></span></label> <input type="text"
			id="user_name_form" placeholder="NAME" /> <label for="user_email">이메일<span id ="email_message"></span></label>
		<input type="text" id="user_email_form" placeholder="E-MAIL" /><br /> <input
			type="button" value="회원정보수정" onclick="user_update()" /><input
			type="button" value="회원탈퇴" onclick="user_delete()" />
	</form>
</div>
