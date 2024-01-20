<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	var msg = "<tag:out value='${msg}'/>";
	var url = "<tag:out value='${url}'/>";
	if (msg != '') {
		alert(msg);
	}
	if (url.includes("back")) {
		var backValue = url.split(" ")[1];
		if (backValue == null) {
			window.history.go(-1);
		} else {
			window.history.go(backValue);
		}
	} else {
		location.href = url;
	}
</script>