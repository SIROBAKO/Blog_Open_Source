<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="tag" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	var msg = "<tag:out value='${msg}'/>";
	var url = "<tag:out value='${url}'/>";
	alert(msg);
	location.href = url;
</script>