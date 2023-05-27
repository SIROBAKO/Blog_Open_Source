<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>



<div id="summernote" class="detail-space">


	<div class="info_post">
		<h1>${board.title}</h1>
		<span>${board.category} º <fmt:formatDate value="${board.date}"
				pattern="yyyy-MM-dd HH:mm:ss" /></span>
	</div>
	<div id="detail">${board.contents}</div>

	<script async
		src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-6908918465539147"
		crossorigin="anonymous"></script>
	<!-- 하단광고 2 -->
	<ins class="adsbygoogle" style="display: block"
		data-ad-client="ca-pub-6908918465539147" data-ad-slot="2403518393"
		data-ad-format="auto" data-full-width-responsive="true"></ins>
	<script>
     (adsbygoogle = window.adsbygoogle || []).push({});
	</script>

	<div class="list-space">
		<h4>
			${board.category} 카테고리의 다른 글 <a href="/list/${board.category}"><i
				class="icon-menu"></i></a>
		</h4>
		<ul>
			<tag:forEach var="x" items="${nextBoard}">
				<li><a href="/detail/${x.num}"> ${x.title} <i
						class="icon-right-big"></i>
				</a></li>
			</tag:forEach>

			<li><a href="/detail/${board.num}"> ${board.title} <i
					class="icon-right-big"></i>
			</a></li>

			<tag:forEach var="x" items="${prevBoard}">
				<li><a href="/detail/${x.num}"> ${x.title} <i
						class="icon-right-big"></i>
				</a></li>
			</tag:forEach>

		</ul>
	</div>
	<hr />
	<div class="comment-space">
		<div class="comment-input">
			<textarea id="comment"> </textarea>
			<p>
				이름 : <input type="text" id="user-name" /> 암호 : <input type="text"
					id="pwd" /> <input id="comment-submit" type="button"
					style="display: none" /> <label for="comment-submit"><i
					class="icon-paper-plane" onclick="AddComment(${board.num}, 0, 0)"></i></label>
			</p>
		</div>
		<hr />

		<ul id="comment-area">
			<tag:forEach var="x" items="${comment}">
				<tag:if test="${x.ref_comment == 0}">
					<li id="comment_area${x.num}">
						<p>
							<b id="user-name${ x.num }">${ x.user_name }</b> <span> <fmt:formatDate
									value="${  x.create_date }" pattern="yyyy-MM-dd HH:mm:ss" />
							</span>
						</p>
						<p id="comment${x.num}">${x.comment }</p>
						<p id="edit${x.num}">
							<tag:if test="${ x.user_name !='NULL' }">
								<a onclick="ReComment(${board.num},${x.num},${x.num},0)">
									대댓글 </a>
								<a onclick="ReComment(${board.num},${x.num},${x.num},1)">수정</a>
								<a onclick="DelComment(${x.num})">삭제</a>
							</tag:if>
						</p>
					</li>

					<tag:forEach var="y" items="${recomment}" varStatus="status">
						<tag:if test="${ y.ref_comment == x.num }">
							<li id="comment_area${y.num}" class="recomment"><i
								class="icon-terminal"></i>
								<p>
									<b id="user-name${y.num}">${y.user_name }</b> <span><fmt:formatDate
											value="${y.create_date}" pattern="yyyy-MM-dd HH:mm:ss" /></span>
								</p>
								<p id="comment${y.num}">${y.comment }</p>
								<p id="edit${y.num}">
									<tag:if test="${ y.user_name !='NULL' }">
										<a onclick="ReComment(${board.num},${x.num},${y.num},1)">수정</a>
										<a onclick="DelComment(${y.num})">삭제</a>
									</tag:if>
								</p></li>

						</tag:if>
					</tag:forEach>
				</tag:if>
			</tag:forEach>



		</ul>
	</div>
</div>


