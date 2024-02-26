<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- side_contant -->
<aside>
	<div class="fixed">
		<h3>추천 게시물</h3>
		<ul>
			<tag:forEach var="x" items="${commend}" >
				<li>
					 <a href="/detail/${x.num}"> <img
						src="/upload/image/fileupload/${x.num}/thumbnail/thumbnail.PNG"
						onerror="this.src='/image/logo/black.png'" alt="${x.title} 썸네일" />
				</a>
				</li>
			</tag:forEach>
		</ul>
	</div>
</aside>