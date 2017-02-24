<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.File"%>
<%@page import="pds.model.PdsItem"%>
<%@page import="jdbc.connection.ConnectionProvider"%>
<%@page import="java.sql.Connection"%>
<%@page import="pds.dao.PdsItemDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String[] deleteParam = request.getParameterValues("delete");
	Connection conn = ConnectionProvider.getConnection();
	
	for(int i=0; i<deleteParam.length; i++){
		int itemId = Integer.parseInt(deleteParam[i]);
		PdsItem pdsItem = PdsItemDao.getInstance().selectById(conn, itemId);
		File file = new File(pdsItem.getRealPath());
		if(file.delete()){
		//DB에서 item삭제
		PdsItemDao.getInstance().delete(conn, itemId);
		%><%=pdsItem.getFileName() %>파일을 삭제 했습니다.<br><%
		}else
		%>파일 삭제에 실패 했습니다.<br><%			
	}
%>

<!DOCTYPE>
<html>
<head>
<title>삭제 성공</title>
</head>
<body>
<a href="list.jsp">목록보기</a>
</body>
</html>