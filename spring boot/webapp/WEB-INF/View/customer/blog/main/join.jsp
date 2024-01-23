<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="Main-Content">
	<form class="user-form">
		<h1>
			HAKO<span>blog</span>
		</h1>
		<label for="user_id">아이디<span id ="id_message"></span></label> <input type="text" id="user_id"
			placeholder="ID" /><br /> <label for="user_pwd">비밀번호<span id ="pwd_message"></span></label><input
			type="password" id="user_pwd" placeholder="PASSWORD" /> <label
			for="user_pwd_re_enter">비밀번호 재입력</label><input type="password"
			id="user_pwd_re_enter" placeholder="PASSWORD RE ENTER" />
		<br /> <label for="user_name_form">닉네임<span id ="name_message"></span></label> <input type="text"
			id="user_name_form" placeholder="NAME" /> <label for="user_email">이메일<span id ="email_message"></span></label>
		<input type="text" id="user_email_form" placeholder="E-MAIL" /><br /> <input
			type="button" value="회원가입" onclick="user_join()" />
	</form>
</div>
