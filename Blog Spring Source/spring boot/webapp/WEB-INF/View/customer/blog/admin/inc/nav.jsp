<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- menu -->
<nav>
	<div class="fixed">
		<div class="Count-List">

			<p>
				<b>메모리 사용량 : </b>${Used_Memory}</p>
			<p>
				<b>CPU 사용량 : </b>${Used_Cpu}</p>


			<div style="text-align: center;">
				<input type="text" name="sitemap2"
					style="padding: 7px; box-sizing: border-box; width: 100%; margin: 15px 0; background-color: var(--background-color-code); border: none;" />
				<input type="button" value="사이트맵 등록" onclick="appendSitemap(2)"
					style="border: none; background-color: var(--background-color-code); width: 100%; padding: 5px;" />
				
			</div>
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
</nav>