<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!-- header -->
<header id="header">
	<div class="Header-Icon">
		<a href="/index"> <img id="logo" src="/image/logo/HAK-logo.png" />
		</a>
	</div>
	<div class="Header-search">
		<form method="GET" action="/list">
			<input id="Search_Submit" type="submit" /> <label
				for="Search_Submit"> <i class="icon-search"></i>
			</label> <input type="text" name="title" placeholder="검색..." /> <input
				id="Search_Reset" type="reset" /> <label for="Search_Reset">
				<i class="icon-cancel"></i>
			</label>
		</form>
	</div>
	<div class="Header-Mode-Console" onclick="DarkModeChange()">
		<label for="Mode-Console">Dark</label> <i id="Mode-Console"
			class="icon-moon"></i>
	</div>
</header>

<!-- mobile header -->
<div id="sub_bg" onclick="closeNav()"></div>
<div id="mobile-nav" class="mobile-nav">
	<div class="mobile-nav-header">
		<a href="/index">HAKO<span>blog</span></a> <i class="icon-cancel"
			onclick="closeNav()"></i>
	</div>

	<div class="Header-search">
		<form method="GET" action="/list">
			<input id="Search_Submit" type="submit" /> <label
				for="Search_Submit"> <i class="icon-search"></i>
			</label> <input type="text" name="title" placeholder="검색..." /> <input
				id="Search_Reset" type="reset" /> <label for="Search_Reset">
				<i class="icon-cancel"></i>
			</label>
		</form>
	</div>

	<div class="Header-Mode-Console" onclick="DarkModeChange()">
		<label for="mobile-Mode-Console">Dark</label> <i
			id="mobile-Mode-Console" class="icon-moon"></i>
	</div>

	<h4 id="Language-Toggle">카테고리</h4>
	<ul id="Language-menu">
		<a href="/list"><li>전체글</li></a>
		<a href="/list?category=Blog 제작"><li>블로그 제작</li></a>
	</ul>
	<h4 id="Project-Toggle">프로젝트</h4>
	<ul id="Project-menu">
		<a href="https://brospo.co.kr">
			<li>브로스포 (쇼핑몰 사이트)</li>
		</a>
	</ul>
</div>
<header id="header-mobile">
	<div class="Header-menu" onclick="openNav()">
		<i class="icon-menu-1"></i>
	</div>

	<div class="Header-Icon">
		<a href="/index"> <img id="mobile-logo"
			src="/image/logo/HAK-logo.png" />
		</a>
	</div>
</header>