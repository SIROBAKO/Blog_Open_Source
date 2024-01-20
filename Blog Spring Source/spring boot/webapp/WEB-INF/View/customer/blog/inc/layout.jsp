<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko-KR">

<head>

<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- 홈페이지 정보 -->

<tag:if test="${ empty category }">
	<title>HAKO's Dev Diaries</title>
	<meta property="og:title" content="HAKO's Dev Diaries">

	<meta name='description' content="HAKO's Dev Diaries에 오신걸 환영합니다." />
	<meta property="og:description"
		content="HAKO's Dev Diaries에 오신걸 환영합니다.">
	<meta property='og:image'
		content='https://sirobako.co.kr/image/logo/HAK-logo.png' />
</tag:if>
<tag:if test="${ not empty category}">
	<title>HAKO ${category}</title>
	<meta property="og:title" content="HAKO blog 카테고리 : ${category}">

	<meta name='description' content='${category}에관한 게시글이 모여있습니다.' />
	<meta property="og:description" content="${category}에관한 게시글이 모여있습니다.">
	<meta property='og:image'
		content='https://sirobako.co.kr/image/logo/HAK-logo.png' />
</tag:if>

<meta name='author' content='HAKO' />

<meta property='og:type' content='website' />
<meta property='og:url' content='https://sirobako.co.kr' />




<!-- 선호 링크 -->
<link rel="canonical" href="https://sirobako.co.kr/${url}">


<!-- 탭창아이콘 -->


<link rel='shortcut icon' href='/image/logo/HAKO-short-logo.png' />
<link rel='icon' href='/image/logo/HAKO-short-logo.png' type='image/x-icon' />
<!-- 검색엔진 -->
<meta name="robots" content="index,follow">


<!-- css 연동 -->
<link rel="stylesheet" href="/Css/blog-layout.css" />
<link rel="stylesheet" href="/Css/blog-user.css" />
<link rel="stylesheet" href="/Css/fontello-embedded.css" />

<!-- js 연동 -->
<script type="text/javascript" src="/JS/blog-main.js"></script>
<script type="text/javascript" src="/JS/blog-user.js"></script>


</head>
<body>
	<div id="scrollIndicator"></div>
	<tiles:insertAttribute name="header" />

	<main>
		<tiles:insertAttribute name="nav" />
		<tiles:insertAttribute name="content" />
		<tiles:insertAttribute name="aside" />
	</main>

	<tiles:insertAttribute name="footer" />
</body>
</html>
