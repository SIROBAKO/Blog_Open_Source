<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko-KR">
<head>
<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>

<!-- include libraries(jQuery, bootstrap) -->
<tag:if test="${empty list}">
	 <link
		href="/Css/summernote/bootstrap.min.css"
		rel="stylesheet" />  
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>

	<!-- include summernote css/js -->
	<link href="/Css/summernote/summernote.css" rel="stylesheet" />
	<script src="/JS/summernote/summernote-ko-KR.js"></script>
	<script src="/JS/summernote/summernote.js"></script>


</tag:if>


<meta charset="UTF-8" />


<meta name="viewport"
	content="initial-scale=1.0,user-scalable=no,maximum-scale=1,width=device-width" />

<!-- 홈페이지 정보 -->

<tag:if test="${ empty board }">
	<title>HAKO's Dev Diaries</title>
	<meta property="og:title" content="HAKO blog">

	<meta name='description' content='블로그 페이지' />
	<meta property="og:description" content="블로그 페이지">
	<meta property='og:image'
		content='https://sirobako.co.kr/image/logo/HAK-logo.png' />
</tag:if>
<tag:if test="${ not empty board}">
	<title>${board.title }</title>
	<meta property="og:title" content="${board.title }">

	<meta name='description' content='${board.category }' />
	<meta property="og:description" content="${board.category }">
	<meta property='og:image'
		content='https://sirobako.co.kr/upload_image/image/fileupload/${board.num}/thumbnail/thumbnail.PNG' />

</tag:if>

<meta name='author' content='HAKO' />

<meta property='og:type' content='website' />
<meta property='og:url' content='https://sirobako.co.kr' />




<!-- 선호 링크 -->
<link rel="canonical" href="https://www.sirobako.co.kr/${url}">



<!-- 탭창아이콘 -->

<link rel='shortcut icon' href='/image/logo/HAK-logo.png' />
<link rel='icon' href='/image/logo/HAK-logo.png' type='image/x-icon' />

<!-- 검색엔진 -->

<meta name="robots" content="noindex,nofollow">


<!-- css 연동 -->
<link rel="stylesheet" href="/Css/blog-layout.css" />
<link rel="stylesheet" href="/Css/fontello-embedded.css" />

<!-- js 연동 -->
<script type="text/javascript" src="/JS/blog-main.js"></script>



</head>
<body class>
	<tiles:insertAttribute name="header" />

	<main>
		<tiles:insertAttribute name="nav" />
		<tiles:insertAttribute name="content" />
	</main>

	<tiles:insertAttribute name="footer" />
</body>
</html>
