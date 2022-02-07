<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>회원 가입창</title>
</head>
<body>
	<h1>회원 가입</h1>
	<c:url value="/user/addUser.do" var="addUserUrl" />
	<form:form name="frmMember" action="${addUserUrl}" method="POST">

		<p>
			<label for="username">아이디</label> <input type="text" name="username" />
		</p>
		<p>
			<label for="password">비밀번호</label> <input type="password" name="password" />
		</p>
		<button type="submit">가입하기</button>
	</form:form>

</body>
</html>