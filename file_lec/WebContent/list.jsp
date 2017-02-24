<%@page import="pds.model.PdsItemListModel"%>
<%@page import="pds.service.ListPdsItemService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	String pageNumberString = request.getParameter("p");
	int pageNumber = 1;
	if(pageNumberString != null && pageNumberString.length() > 0){
		pageNumber = Integer.parseInt(pageNumberString);
	}
	
	ListPdsItemService listSerivce = ListPdsItemService.getInstance();
	//페이지 넘버를 인자로 보내 해당 페이지범위에 해당하는 itemList와 페이지번호, 총페이지 수 등을 추출
	PdsItemListModel itemListModel = listSerivce.getPdsItemList(pageNumber);
	//추출한 객체를 request객체에 저장
	request.setAttribute("listModel", itemListModel);
	
	//총페이지 수를 기준으로 페이징처리를 위한 계산작업
	if (itemListModel.getTotalPageCount() > 0) {
		//처음 페이지 번호
		int beginPageNumber = (itemListModel.getRequestPage() - 1) / 10 * 10 + 1;
		//마지막 페이지 번호
		int endPageNumber = beginPageNumber + 9;
		if (endPageNumber > itemListModel.getTotalPageCount()) {
			endPageNumber = itemListModel.getTotalPageCount();
		}
		//requset객체에 처음과 마지막 페이지를 저장
		request.setAttribute("beginPage", beginPageNumber);
		request.setAttribute("endPage", endPageNumber);
	}
%>
<jsp:forward page="list_view.jsp" />