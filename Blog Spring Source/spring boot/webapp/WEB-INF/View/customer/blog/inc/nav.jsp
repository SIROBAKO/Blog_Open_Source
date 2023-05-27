<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- menu -->
<nav>
	<div id="fiexd">
		<h4 id="Language-Toggle">카테고리</h4>

		<ul id="Language-menu">
			<tag:forEach var="x" items="${categorys}">
				<li><a href="/list/${x.query}"> ${x.category} </a></li>
			</tag:forEach>
		</ul>
		<h4 id="Project-Toggle">프로젝트</h4>
		<ul id="Project-menu">
			<li><a href="https://brospo.co.kr"> 브로스포 (쇼핑몰 사이트) </a></li>

		</ul>
	</div>
</nav>