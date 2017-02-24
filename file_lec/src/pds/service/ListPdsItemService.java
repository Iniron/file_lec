package pds.service;

import java.sql.Connection;
import java.util.List;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import pds.dao.PdsItemDao;
import pds.model.PdsItem;
import pds.model.PdsItemListModel;

public class ListPdsItemService {
	
	//싱글톤 패턴
	private static ListPdsItemService instance = new ListPdsItemService();
	public static ListPdsItemService getInstance(){
		return instance;
	}
	private ListPdsItemService() {}
	
	//각 페이지당 목록 수 
	public static final int COUNT_PER_PAGE = 10;
	
	public PdsItemListModel getPdsItemList(int pageNumber){
		if (pageNumber < 0) {		//페이지넘버가 0보다 작으면 Exception
			throw new IllegalArgumentException("page number < 0 : "
					+ pageNumber);
		}
		PdsItemDao pdsItemDao = PdsItemDao.getInstance();
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();		//DB연결
			int totalArticleCount = pdsItemDao.selectCount(conn);	//item의 전체 수 추출
			
			//item이 하나도 없으면 새로운 itemList를 만들어 리턴
			if(totalArticleCount == 0){
				return new PdsItemListModel();
			}
			
			//------------------------- 페이징 처리 -------------------------
			//총 페이지 수
			int totalPageCount = calculateTotalPageCount(totalArticleCount);

			int firstRow = (pageNumber - 1) * COUNT_PER_PAGE + 1;
			int endRow = firstRow + COUNT_PER_PAGE - 1;

			if (endRow > totalArticleCount) {
				endRow = totalArticleCount;
			}
			
			//페이지 범위에 해당하는 itemList를 추출
			List<PdsItem> PdsItemList = pdsItemDao.select(conn, firstRow, endRow);

			PdsItemListModel PdsItemListView = new PdsItemListModel(
					PdsItemList, pageNumber, totalPageCount, firstRow, endRow);
			return PdsItemListView;
		} catch (Exception e) {
			throw new RuntimeException("DB 에러 발생:" + e.getMessage(), e);
		} finally{
			JdbcUtil.close(conn);
		}
	}
	
	//item의 갯수를 기준으로 총 페이지의 개수를 리턴
	private int calculateTotalPageCount(int totalPdsItemCount) {
		if (totalPdsItemCount == 0) {
			return 0;
		}
		int pageCount = totalPdsItemCount / COUNT_PER_PAGE;
		if (totalPdsItemCount % COUNT_PER_PAGE > 0) {
			pageCount++;
		}
		return pageCount;
	}
}
