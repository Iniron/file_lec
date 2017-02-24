<%@page import="pds.service.AddPdsItemService"%>
<%@page import="pds.model.PdsItem"%>
<%@page import="pds.file.FileSaveHelper"%>
<%@page import="java.util.Iterator"%>
<%@page import="pds.model.AddRequest"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	//multipart/form-data 조건 확인
	boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	if(!isMultipart){
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return;
	}
	//fileItem의 Factory설정
	DiskFileItemFactory factory = new DiskFileItemFactory();
	
	//업로드 요청 처리하는 ServletFileUpload생성
	ServletFileUpload upload = new ServletFileUpload(factory);
	upload.setHeaderEncoding("utf-8");	//한글처리
	
	//업로드 요청 파싱 -> FileItem 목록 추출
	List<FileItem> items = upload.parseRequest(request);
	
	AddRequest addRequest = new AddRequest();
	Iterator<FileItem> iter = items.iterator();
	while(iter.hasNext()){
		FileItem item = iter.next();
		if(item.isFormField()){
			String name = item.getFieldName();	//input의 name을 저장
			if(name.equals("description")){	//description인경우
				System.out.println("upload nonfile");
				String value = item.getString("utf-8");
				addRequest.setDescription(value);
			}
		}else{
			String name = item.getFieldName();
			if(name.equals("file")){		//file인 경우
				System.out.println("upload file");
				//c:\java\pds에 파일을 저장 후 절대경로를 리턴값으로 저장
				String realPath = FileSaveHelper.save("c:\\Java\\pds", item.getInputStream());
				addRequest.setFileName(item.getName());
				addRequest.setFileSize(item.getSize());
				addRequest.setRealPath(realPath);
			}
		}
	}
	
	//데이터 베이스에 파일정보 저장
	PdsItem pdsItem = AddPdsItemService.getInstance().add(addRequest);
%>
<!DOCTYPE>
<html>
<head>
<title>업로드 성공</title>
</head>
<body>
<%=pdsItem.getFileName() %>파일을 업로드 했습니다.
<br/>
<a href="list.jsp">목록보기</a>
</body>
</html>