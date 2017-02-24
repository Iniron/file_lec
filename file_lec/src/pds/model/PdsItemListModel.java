package pds.model;

import java.util.ArrayList;
import java.util.List;

public class PdsItemListModel {
	private List<PdsItem> pdsItemList;
	private int requestPage;
	private int totalPageCount;
	private int startRow;
	private int endRow;
	
	public PdsItemListModel(){
		this(new ArrayList<PdsItem>(), 0, 0, 0, 0);
	}

	public PdsItemListModel(List<PdsItem> pdsItemList, int requestPage, int totalPageCount, int startRow, int endRow) {
		this.pdsItemList = pdsItemList;
		this.requestPage = requestPage;
		this.totalPageCount = totalPageCount;
		this.startRow = startRow;
		this.endRow = endRow;
	}

	public List<PdsItem> getPdsItemList() {
		return pdsItemList;
	}
	
	public boolean isHasPdsItem(){
		return !pdsItemList.isEmpty();
	}

	public int getRequestPage() {
		return requestPage;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public int getStartRow() {
		return startRow;
	}

	public int getEndRow() {
		return endRow;
	}
}
