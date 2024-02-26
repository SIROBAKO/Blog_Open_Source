<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- editor -->


<div id="edit-space" class="editor-space">

	<form method="post" action="/admin/reg" enctype="multipart/form-data"
		style="max-width: 800px; margin: 0 auto;">
		<h3>제목</h3>
		<input type="text" id="title" />
		<h4>
			부 제목
			<h5 id="count_description"></h5>
		</h4>
		<input type="text" name="description" oninput="countCharacters()" />

		<h4>썸네일</h4>
		<input type="file" name="thumbnail" />
		<h4>카테고리</h4>
		<select name="category">
			<tag:forEach var="x" items="${categorys}">
				<tag:if test="${!x.query.equals('') && !x.purpose.equals('비활성') }">
					<option value="${x.query}">${x.category}</option>
				</tag:if>
			</tag:forEach>
		</select>
		<textarea id="summernote" name="content"></textarea>
		<input type="button" value="임시저장" onclick="insertBoard('Y')"> <input
			type="button"  value="저장" onclick="insertBoard('N')">
	</form>

</div>
