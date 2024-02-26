<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="ko-KR">
<head>


<meta charset="UTF-8" />


<meta name="viewport" content="width=device-width, initial-scale=1.0 ">


<!-- 홈페이지 정보 -->

<tag:if test="${ empty board }">
	<title>HAKO's Dev Diaries</title>
	<meta property="og:title" content="HAKO blog">

	<meta name='description' content="HAKO's Dev Diaries에 오신걸 환영합니다." />
	<meta property="og:description"
		content="HAKO's Dev Diaries에 오신걸 환영합니다.">
	<meta property='og:image'
		content='https://sirobako.co.kr/image/logo/HAK-logo.png' />
</tag:if>
<tag:if test="${ not empty board}">
	<title>${board.title }</title>
	<tag:if test="${board.description == ''}">
		<meta name='description' content='${board.title }' />
		<meta property="og:description" content="${board.title }">
	</tag:if>
	<tag:if test="${board.description != ''}">
		<meta name='description' content='${board.description }' />
		<meta property="og:description" content="${board.description }">
	</tag:if>


	<meta property="og:title" content="${board.title }">
	<meta property='og:image'
		content='https://sirobako.co.kr/upload/image/fileupload/${board.num}/thumbnail/thumbnail.PNG' />

</tag:if>

<meta name='author' content='HAKO' />

<meta property='og:type' content='website' />
<meta property='og:url' content='https://sirobako.co.kr' />




<!-- 선호 링크 -->
<link rel="canonical" href="https://sirobako.co.kr/${url}">



<!-- 탭창아이콘 -->

<link rel='shortcut icon' href='/image/logo/HAKO-short-logo.png' />
<link rel='icon' href='/image/logo/HAKO-short-logo.png'
	type='image/x-icon' />

<!-- 검색엔진 -->

<meta name="robots" content="index,follow">


<!-- css 연동 -->
<link rel="stylesheet" href="/Css/blog-layout.css" />
<link rel="stylesheet" href="/Css/blog-user.css" />
<link rel="stylesheet" href="/Css/blog-detail.css" />
<link rel="stylesheet" href="/Css/blog-comment.css" />
<link rel="stylesheet" href="/Css/fontello-embedded.css" />

<!-- js 연동 -->
<script type="text/javascript" src="/JS/blog-main.js"></script>
<script type="text/javascript" src="/JS/blog-user.js"></script>
<script type="text/javascript" src="/JS/blog-comment.js"></script>

<!-- Google tag (gtag.js) -->
<script async
	src="https://www.googletagmanager.com/gtag/js?id=G-KLE7PB4D75"></script>
<script>
	window.dataLayer = window.dataLayer || [];
	function gtag() {
		dataLayer.push(arguments);
	}
	gtag('js', new Date());

	gtag('config', 'G-KLE7PB4D75');
</script>


<script async
	src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
<script>
	(adsbygoogle = window.adsbygoogle || []).push({
		google_ad_client : "ca-pub-6908918465539147",
		enable_page_level_ads : true,
		overlays : {
			bottom : true
		}
	});
</script>

</head>
<body>
	<div id="scrollIndicator"></div>
	<tiles:insertAttribute name="header" />

	<main>
		<tiles:insertAttribute name="nav" />
		<tiles:insertAttribute name="content" />
	</main>

	<tiles:insertAttribute name="footer" />

</body>
</html>
