<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<!-- header -->
<header id="header">
	<div class="Header-Icon">
		<a href="/index"> <img id="logo" src="/image/logo/HAK-logo.png"
			alt="HAKO의 개인블로그 LOGO" />
		</a>
	</div>
	<div id="user_name"></div>
	<div id="user_login">
		<a href="/blog-login">로그인</a>
	</div>
	<div id="user_logout" onclick="user_logout()">로그아웃</div>
	<div class="Header-search">
		<form method="GET" action="/list">
			<input id="Search_Submit_mobile" type="submit" /> <label
				for="Search_Submit_mobile"> <i class="icon-search"></i>
			</label> <input type="text" name="title" placeholder="검색..." /> <input
				id="Search_Reset_mobile" type="reset" /> <label
				for="Search_Reset_mobile"> <i class="icon-cancel"></i>
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
	<div class="Mobile-Count-List">

		<tag:set var="totalViews"
			value="${countList[0].count + countList[2].count}" />
		<tag:set var="todayViews" value="${countList[2].count}" />
		<tag:set var="totalVisitors"
			value="${countList[1].count + countList[3].count}" />
		<tag:set var="todayVisitors" value="${countList[3].count}" />


		<fmt:formatNumber value="${totalViews}" pattern="#,##0"
			var="formattedTotalViews" />
		<fmt:formatNumber value="${todayViews}" pattern="#,##0"
			var="formattedTodayViews" />
		<fmt:formatNumber value="${totalVisitors}" pattern="#,##0"
			var="formattedTotalVisitors" />
		<fmt:formatNumber value="${todayVisitors}" pattern="#,##0"
			var="formattedTodayVisitors" />

		<p>
			<b>총 방문자 수 : </b>${formattedTotalVisitors}</p>
		<p>
			<b>오늘의 방문자 수 : </b>${formattedTodayVisitors}</p>
		<p>
			<b>총 조회수 : </b> ${formattedTotalViews}
		</p>
		<p>
			<b>오늘의 조회수 : </b>${formattedTodayViews}</p>
	</div>

	<div class="Header-search">
		<form method="GET" action="/list">
			<input id="Search_Submit_desktop" type="submit" /> <label
				for="Search_Submit_desktop"> <i class="icon-search"></i>
			</label> <input type="text" name="title" placeholder="검색..." /> <input
				id="Search_Reset_desktop" type="reset" /> <label
				for="Search_Reset_desktop"> <i class="icon-cancel"></i>
			</label>
		</form>
	</div>
	<div id="user_header">

		<div id="user_name_header">이름</div>
		<div id="user_login_header">
			<a href="/blog-login">로그인</a>
		</div>
		<div id="user_logout_header" onclick="user_logout()">로그아웃</div>

	</div>

	<div class="Header-Mode-Console" onclick="DarkModeChange()">
		<label for="mobile-Mode-Console">Dark</label> <i
			id="mobile-Mode-Console" class="icon-moon"></i>
	</div>


	<h4 id="Language-Toggle">카테고리</h4>
	<ul id="Language-menu">
		<tag:forEach var="x" items="${categorys}">
			<tag:if test="${x.purpose == '기본'}">
				<li><a href="/list/${x.query}"> ${x.category}<span>(${x.count})</span></a></li>
			</tag:if>
		</tag:forEach>
	</ul>
	<h4>프로그래밍 언어</h4>
	<ul>
		<tag:forEach var="x" items="${categorys}">
			<tag:if test="${x.purpose == '언어'}">
				<li><a href="/list/${x.query}"> ${x.category}<span>(${x.count})</span></a></li>
			</tag:if>
		</tag:forEach>
	</ul>
	<h4>개발 도구</h4>
	<ul>
		<tag:forEach var="x" items="${categorys}">
			<tag:if test="${x.purpose == '도구'}">
				<li><a href="/list/${x.query}"> ${x.category}<span>(${x.count})</span></a></li>
			</tag:if>
		</tag:forEach>
	</ul>

	<h4 id="Project-Toggle">프로젝트</h4>
	<ul id="Project-menu">
		<tag:forEach var="x" items="${categorys}">
			<tag:if test="${x.purpose == '프로젝트'}">
				<li><a href="/list/${x.query}"> ${x.category}<span>(${x.count})</span></a></li>
			</tag:if>
		</tag:forEach>
	</ul>

</div>
<header id="header-mobile">
	<div class="Header-menu" onclick="openNav()">
		<i class="icon-menu-1"></i>
	</div>

	<div class="Header-Icon">
		<a href="/index"> <img id="mobile-logo"
			src="/image/logo/HAK-logo.png" alt="HAKO의 개인블로그 LOGO" />
		</a>
	</div>
</header>