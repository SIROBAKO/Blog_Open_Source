<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="UTF-8">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<!-- 홈페이지 정보 -->

<tag:if test="${empty param.num}">
	<title>HAKO blog</title>
	<meta property="og:title" content="HAKO blog">

	<meta name='description' content='블로그 페이지' />
	<meta property="og:description" content="블로그 페이지">
	<meta property='og:image' content='https://sirobako.co.kr/image/logo/HAK-logo.png' />
</tag:if>
<tag:if test="${!empty param.num}">
	<title>${board.title }</title>
	<meta property="og:title" content="${board.title }">

	<meta name='description' content='${board.category }' />
	<meta property="og:description" content="${board.category }">
	<meta property='og:image' content='https://sirobako.co.kr/upload_image/image/fileupload/${board.title}/thumbnail/thumbnail.PNG' />

</tag:if>

<meta name='author' content='HAKO' />

<meta property='og:type' content='website' />
<meta property='og:url' content='https://sirobako.co.kr' />




<!-- 선호 링크 -->
<link rel='canonical' href='https://www.sirobako.co.kr' />

<!-- 탭창아이콘 -->

<link rel='shortcut icon' href='/image/logo/HAK-logo.png' />
<link rel='icon' href='/image/logo/HAK-logo.png' type='image/x-icon' />

<!-- 검색엔진 -->
<meta name='robots' content='ALL' />

<!-- css 연동 -->
<link rel="stylesheet" href="/Css/style.css" />
<link rel="stylesheet" href="/Css/fontello-embedded.css" />

<!-- js 연동 -->
<script type="text/javascript" src="/JS/main.js"></script>


</head>
<body>
	<tiles:insertAttribute name="header" />

	<main>
		<tiles:insertAttribute name="nav" />
		<tiles:insertAttribute name="content" />
		<tiles:insertAttribute name="aside" />
	</main>

	<tiles:insertAttribute name="footer" />
</body>
</html>
