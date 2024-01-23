<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>




<fmt:formatNumber value="${totalViews}" pattern="#,##0"
	var="formattedTotalViews" />


<!-- menu -->
<nav>
	<div class="fixed">

		<div class="Count-List">

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
</nav>