<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="editor-space">


	<form method="post" action="/admin/del_update"
		enctype="multipart/form-data" onsubmit="return confirm('수정 하시겠습니까?')"
		style="max-width: 800px; margin: 0 auto;">
		<h3>제목</h3>
		<input id="title" type="text" name="title" value="${board.title}" />

		<h4>
			부 제목
			<h5 id="count_description"></h5>
		</h4>
		<input type="text" name="description" oninput="countCharacters()"
			value="${board.description}" />

		<h4>썸네일</h4>
		<input type="file" name="thumbnail" />
		<h4>카테고리</h4>
		<select name="category">
			<tag:forEach var="x" items="${categorys}">
				<tag:if test="${!x.query.equals('') && !x.purpose.equals('비활성') }">
					<option value="${x.query}"
						<tag:if test="${x.query.equals(board.category)}">
						selected
					</tag:if>>${x.category}</option>
				</tag:if>
			</tag:forEach>
		</select>


		<div id="temp" style="display: none">${board.contents}</div>


		<textarea id="summernote" name="content"></textarea>

		<input type="button"  value="비공개" onclick="updateBoard('Y'); window.location.href ='/admin/list/hidden'"> 
		<input type="button"  value="업데이트" onclick="updateBoard('N'); window.location.href ='/admin/list'"> <input
			type="button" value="삭제"
			onclick="deleteBoard()">
	</form>

</div>

