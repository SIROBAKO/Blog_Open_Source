<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- header -->
<header id="header">
	<div class="Header-Icon">
		<a href="/index"> <img id="logo" src="/image/logo/HAK-logo.png"
			alt="HAKO의 개인블로그 LOGO" />
		</a>
	</div>
	<div class="Header-search">
		<form method="GET" action="/admin/list">
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


		<p>
			<b>메모리 사용량 : </b>${Used_Memory}</p>
		<p>
			<b>CPU 사용량 : </b>${Used_Cpu}</p>

		<div style="text-align: center;">
			<input type="text" name="sitemap1"
				style="padding: 7px; box-sizing: border-box; width: 100%; margin: 15px 0; background-color: var(--background-color-code); border: none;" />
			<input type="button" value="사이트맵 등록" onclick="appendSitemap(1)"
				style="border: none; background-color: var(--background-color-code); width: 100%; padding: 5px;" />

		</div>
		<script type="text/javascript">
			function appendSitemap(num) {
				// 버튼 클릭 이벤트 처리

				var sitemapId = $('input[name="sitemap' + num + '"]').val();

				if (sitemapId !== "" && isNumeric(sitemapId)) {

					// Ajax 요청
					$.ajax({
						url : '/admin/appendSitemap', // 업데이트를 처리할 서버의 URL
						method : 'POST', // 요청 방식 (GET 또는 POST)
						data : {
							board_num : sitemapId
						},
						success : function(responseData) {
							if (responseData.includes("script")) {
								alert("테스트 유저는 사용할 수 없는 기능입니다.");
							} else {
								alert(responseData);
							}
						},
					});
				} else {
					alert("값을 확인하세요");
				}
			}
			function isNumeric(value) {
				return !isNaN(parseFloat(value)) && isFinite(value);
			}
		</script>

	</div>
	<div class="Header-search">
		<form method="GET" action="/admin/list">
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
		<li><a href="/admin/edit">글작성</a></li>
		<li><a href="/admin/list/hidden">임시저장</a></li>
		<tag:forEach var="x" items="${categorys}">
			<tag:if test="${x.purpose == '기본'}">
				<li><a href="/admin/list/${x.query}"> ${x.category}</a></li>
			</tag:if>
		</tag:forEach>
	</ul>
	<h4>프로그래밍 언어</h4>
	<ul>
		<tag:forEach var="x" items="${categorys}">
			<tag:if test="${x.purpose == '언어'}">
				<li><a href="/admin/list/${x.query}"> ${x.category}</a></li>
			</tag:if>
		</tag:forEach>
	</ul>
	<h4>개발 도구</h4>
	<ul>
		<tag:forEach var="x" items="${categorys}">
			<tag:if test="${x.purpose == '도구'}">
				<li><a href="/admin/list/${x.query}"> ${x.category}</a></li>
			</tag:if>
		</tag:forEach>
	</ul>
	<h4>프로젝트</h4>
	<ul>
		<tag:forEach var="x" items="${categorys}">
			<tag:if test="${x.purpose == '프로젝트'}">
				<li><a href="/admin/list/${x.query}"> ${x.category}</a></li>
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
			src="/image/logo/HAK-logo.png" />
		</a>
	</div>
</header>