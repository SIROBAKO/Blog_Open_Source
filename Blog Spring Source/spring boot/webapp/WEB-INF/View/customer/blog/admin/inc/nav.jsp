<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- menu -->
<nav>
	<div id="fiexd">

		<h4 id="Language-Toggle">카테고리</h4>
		<ul id="Language-menu">
			<li><a href="/admin/edit">글작성</a></li>
			<li><a href="/admin/list/hidden">임시저장</a></li>
			<tag:forEach var="x" items="${categorys}">
				<li><a href="/admin/list/${x.query}"> ${x.category}</a></li>
			</tag:forEach>
			
		</ul>
	
	</div>
</nav>