<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- main_list -->
<div class="Main-Content">

	<ul id="index-list"class="Content-List">
		<tag:forEach var="x" items="${list}">
			<li ><a href="/detail/${x.num}"> <img
					src="/upload/image/fileupload/${x.num}/thumbnail/thumbnail.PNG"
					onerror="this.src='/image/logo/black.png'" alt="${x.title} ½æ³×ÀÏ" />
					<h3>${x.title}</h3>
			</a>
				<p>${x.category}</p></li>
		</tag:forEach>
	
	</ul>
	
	<input type="button" class="Load-More" value="LOAD MORE" onclick="AddList()"/>
</div>