<%@page import="pds.service.GetPdsItemService"%>
<%@page import="pds.model.PdsItem"%>
<%@page import="pds.service.PdsItemNotFoundException"%>
<%@page import="pds.service.IncreaseDownloadCountService"%>
<%@page import="pds.file.FileDownloadHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	//다운받을 파일의 id을 parameter로 넘겨 받음
	int id = Integer.parseInt(request.getParameter("id"));
	try {
		//id에 해당하는 PdsItem객체 추출
		PdsItem item = GetPdsItemService.getInstance().getPdsItem(id);

		// 응답 헤더 다운로드로 설정
		response.reset();
		
		String fileName = new String(item.getFileName().getBytes("utf-8"), 
				"iso-8859-1");
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", 
				"attachment; filename=\"" + fileName+"\"");
		//attachment는 보여주지말고 무조건 다운로드 받게한다.
		//fileName에 문자열을 지정해주면 그 문자열로만 다운로드 받는다.
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setContentLength((int)item.getFileSize());
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");

		//item의 절대경로에서 파일을 로드해 response의 ouputStream으로 파일을 출력
		FileDownloadHelper.copy(item.getRealPath(), response.getOutputStream());
		
		//outputStream객체 종료
		response.getOutputStream().close();
		
		//다운로드 수 증가
		IncreaseDownloadCountService.getInstance().increaseCount(id);
	} catch (PdsItemNotFoundException ex) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}
%>