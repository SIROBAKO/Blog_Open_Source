<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- side_contant -->
<aside>
	<div id="fiexd">
		<h3>추천 게시물</h3>
		<ul>
			<tag:forEach var="x" items="${commend}" varStatus="status">
				<li>
					<p>${status.count}</p> <a href="/detail?num=${x.num}">
						<h4>${x.title}</h4>
				</a>
				</li>
			</tag:forEach>
		</ul>
	</div>
</aside>