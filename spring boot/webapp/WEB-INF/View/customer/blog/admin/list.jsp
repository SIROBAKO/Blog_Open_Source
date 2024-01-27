<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>



<div class="Main-Content">
	<div class="List-Category">
		<h1>
			HAKO<span>blog</span>
		</h1>
		${category}
	</div>


	<ul class="Content-List-2">
		<tag:forEach var="x" items="${list}">
		 
			<tag:if test="${!category.equals('임시저장') && !x.hidden.equals('Y')}">
				<li><a href="/admin/update?num=${x.num}"><img
						src="/upload_image/image/fileupload/${x.num}/thumbnail/thumbnail.PNG"
						onerror="this.src='/image/logo/black.png'" /> <span>
							<h3>${x.title }</h3>
							<p>${x.category}
								º
								<fmt:formatDate value="${x.date}" pattern="yyyy-MM-dd " />
							</p>
					</span></a></li>
			</tag:if>
			<tag:if test="${ category.equals('임시저장') && x.hidden.equals('Y') }">
				<li><a href="/admin/update?num=${x.num}"><img
						src="/upload_image/image/fileupload/${x.num}/thumbnail/thumbnail.PNG"
						onerror="this.src='/image/logo/black.png'" /> <span>
							<h3>${x.title }</h3>
							<p>${x.category}
								º
								<fmt:formatDate value="${x.date}" pattern="yyyy-MM-dd " />
							</p>
					</span></a></li>
			</tag:if>
		</tag:forEach>
	</ul>


	<ul class="page_controller">

		<tag:set var="page"
			value="${(empty param.page || param.page<1)?1:param.page }" />
		<tag:set var="StartNum" value="${page-(page-1)%5 }" />
		<tag:set var="LastNum"
			value="${ fn:substringBefore(Math.ceil(count/6),'.')}" />

		<tag:if test="${StartNum>1}">
			<li><a
				href="${param.category}?title=${param.title}&page=${StartNum-1}">
					<i class="icon-left-open-big"></i>
			</a></li>
		</tag:if>
		<tag:if test="${StartNum<=1}">

			<li><fn onclick="alert('이전 페이지가 없습니다.')"> <i
					class="icon-left-open-big"></i></fn></li>
		</tag:if>



		<tag:forEach var="i" begin="0" end="4">
		
			<tag:if test="${StartNum+i <= LastNum }">
				<li><a href="${param.category}?title=${param.title}&page=${StartNum+i}">
						${StartNum+i}</a></li>

			</tag:if>
		</tag:forEach>

		<tag:if test="${StartNum+5<=LastNum}">
			<li><a
				href="${param.category}?title=${param.title}&page=${StartNum+5}">
					<i class="icon-right-open-big"></i>
			</a></li>
		</tag:if>
		<tag:if test="${StartNum+5>LastNum}">
			<li><fn onclick="alert('다음 페이지가 없습니다.')"> <i
					class="icon-right-open-big"></i></fn></li>
		</tag:if>

	</ul>
</div>



