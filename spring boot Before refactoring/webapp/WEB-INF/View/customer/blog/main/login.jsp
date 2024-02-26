<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="Main-Content">
	<form class="user-form">
		<h1>
			HAKO<span>blog</span>
		</h1>
		<input type="text" id="user_id" placeholder="ID" /> <input
			type="password" id="user_pwd" placeholder="PASSWORD" /> <input
			type="button" value="로그인" onclick="user_login()" />
		<div class="kakao-login">
			<img src="/image/API_img/kakao_login_large_wide.png"
				onerror="this.src='/image/logo/black.png'" alt="kakao 로그인" onclick="kakaoLogin()"/>
		</div>

		<div class="user-function">
			<a href="/blog-join">회원가입</a> <a href="/blog-find">아이디/비밀번호 찾기</a>
		</div>
		
		
	</form>


</div>