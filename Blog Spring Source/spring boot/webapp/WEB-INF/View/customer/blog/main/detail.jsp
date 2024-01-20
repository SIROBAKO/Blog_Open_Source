<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>



<div id="summernote" class="detail-space">


	<div class="info_post">
		<h1 class="daum-wm-title">${board.title}</h1>
		<span>${board.category} º <span class="daum-wm-datetime"><fmt:formatDate
					value="${board.date}" pattern="yyyy-MM-dd HH:mm:ss" /></span></span>
	</div>
	<div id="detail">

		<div id="thumbnail">
			<img id="thumbnail" style="width: 100%; height: auto;"
				src="/upload_image/image/fileupload/${board.num}/thumbnail/thumbnail.PNG"
				onerror="this.src='/image/logo/black.png'"
				alt="${board.title} thumbnail" />
		</div>
		<!-- 최 상단 -->
		<!-- 에드센스 -->
		<div style="overflow: hidden; width: 100%; height: 125px;">
			<ins class="adsbygoogle"
				style="display: block; width: 100%; height: 110px; margin-top: 15px;"
				data-ad-client="ca-pub-6908918465539147" data-ad-slot="4813326796"></ins>
			<script>(adsbygoogle = window.adsbygoogle || []).push({});</script>
		</div>
		<br /> ${board.contents}
	</div>

	<!-- 에드센스 -->
	<div style="overflow: hidden; width: 100%; height: 125px;">
		<ins class="adsbygoogle"
			style="display: inline-block; width: 100%; height: 110px; margin-top: 15px;"
			data-ad-client="ca-pub-6908918465539147" data-ad-slot="6835950942"></ins>
		<script>(adsbygoogle = window.adsbygoogle || []).push({});</script>
	</div>
	<div class="list-space">
		<h4>
			${board.category} 카테고리의 다른 글 <a href="/list/${board.category}"><i
				class="icon-menu"></i></a>
		</h4>
		<ul>
			<tag:forEach var="x" items="${nextBoard}">
				<li><a href="/detail/${x.num}"> ${x.title} </a><i
					class="icon-right-big"></i></li>
			</tag:forEach>


			<li><a href="/detail/${board.num}"> ${board.title} </a> <i
				class="icon-right-big"></i></li>

			<tag:forEach var="x" items="${prevBoard}">
				<li><a href="/detail/${x.num}"> ${x.title} </a><i
					class="icon-right-big"></i></li>
			</tag:forEach>

		</ul>
	</div>

	<p style="width: 100%; text-align: right; margin-top: 20px;">
		<fn onclick="shareLink()"
			style="background-color: var(--background-color-code);
    border-radius: 10px;
    padding: 8px;
    font-size: 0.8rem;">
		<i class="icon-share"></i> 공유하기</fn>
	</p>



	<div class="comment-space">
		<div class="comment-input">
			<textarea id="comment"> </textarea>
			<p>
				이름 : <input type="text" id="user-name" /> 암호 : <input type="password"
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
							<b id="user-name${ x.num }">${ x.user_name.replaceAll("HAKO_DEV_USER","") }</b> <span> <fmt:formatDate
									value="${  x.create_date }" pattern="yyyy-MM-dd HH:mm:ss" />
							</span>
						</p>
						<p id="comment${x.num}">${x.comment }</p>
						<p id="edit${x.num}">
							<tag:if test="${ x.user_name !='NULL' }">
								<fn onclick="ReComment(${board.num},${x.num},${x.num},0)">
								대댓글 </fn>
								<fn onclick="ReComment(${board.num},${x.num},${x.num},1)">수정</fn>
								<fn onclick="DelComment(${x.num}, ${x.ref })">삭제</fn>
							</tag:if>
						</p>
					</li>

					<tag:forEach var="y" items="${recomment}" varStatus="status">
						<tag:if test="${ y.ref_comment == x.num }">
							<li id="comment_area${y.num}" class="recomment"><i
								class="icon-terminal"></i>
								<p>
									<b id="user-name${y.num}">${y.user_name.replaceAll("HAKO_DEV_USER","") }</b> <span><fmt:formatDate
											value="${y.create_date}" pattern="yyyy-MM-dd HH:mm:ss" /></span>
								</p>
								<p id="comment${y.num}">${y.comment }</p>
								<p id="edit${y.num}">
									<tag:if test="${ y.user_name !='NULL' }">
										<fn onclick="ReComment(${board.num},${x.num},${y.num},1)">수정</fn>
										<fn onclick="DelComment(${y.num}, ${y.ref })">삭제</fn>
									</tag:if>
								</p></li>

						</tag:if>
					</tag:forEach>
				</tag:if>
			</tag:forEach>

		</ul>
	</div>

</div>


