<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	var msg = "<tag:out value='${msg}'/>";
	var url = "<tag:out value='${url}'/>";
	if (msg != '') {
		alert(msg);
	}
	if (url.includes("referrer")) {
		if (sessionStorage.getItem('referrer') != null) {
			window.location.href = sessionStorage.getItem('referrer');
		} else {
			window.location.href = 'index'
		}
	} else {
		location.href = url;
	}
</script>