<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="Main-Content">

	<form class="user-form" id="find-id">

		<h1>
			HAKO<span>blog</span>
		</h1>
		<select id="find-handler" onchange="findHandler()"><option
				value="id" >아이디 찾기</option>
			<option value="pwd">비밀번호 재설정</option></select>

		<p id="result_area"></p>
		<label for="user_id">아이디</label> <input type="text" id="user_id"
			placeholder="ID" /> <label for="user_email">이메일</label> <input
			type="text" id="user_email_form" placeholder="E-MAIL" /> <label
			for="user_pwd">비밀번호<span id ="pwd_message"></span></label><input type="password" id="user_pwd"
			placeholder="PASSWORD" /> <label for="user_pwd_re_enter">비밀번호
			재입력</label><input type="password" id="user_pwd_re_enter"
			placeholder="PASSWORD RE ENTER" /> <input type="button"
			value="아이디 찾기" id="find_button" onclick="find_id()"><input
			type="button" value="비밀번호 재설정" id="reset_button" onclick="reset_pwd()">

	</form>


</div>
