package pds.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jdbc.JdbcUtil;
import pds.model.PdsItem;

public class PdsItemDao {
	private static PdsItemDao instance = new PdsItemDao();
	public static PdsItemDao getInstance(){
		return instance;
	}
	
	private PdsItemDao(){}

	//Database에 입력된 item을 저장(id, name, path, size, downloadcount, description)
	public int insert(Connection conn, PdsItem item) throws SQLException{
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			pstmt = conn.prepareStatement("insert into pds_item "
					+ "(pds_item_id, filename, realpath, filesize, downloadcount, "
					+ "description) "
					+ "values (pds_item_id_seq.NEXTVAL, ?, ?, ?, 0, ?)");
			pstmt.setString(1, item.getFileName());
			pstmt.setString(2, item.getRealPath());
			pstmt.setLong(3, item.getFileSize());
			pstmt.setString(4, item.getDescription());
			int insertedCount = pstmt.executeUpdate();
			
			System.out.println("PdsItemDao insert");
			//저장된게 있다면 그때의 pds_item_id_seq값을 가져온다
			if(insertedCount>0){
				stmt = conn.createStatement();
				rs = stmt.executeQuery("select pds_item_id_seq.CURRVAL from dual");
				if(rs.next()){
					return rs.getInt(1);
				}
			}
			return -1;
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
			JdbcUtil.close(pstmt);
		}
	}
	
	//item의 전체 수 추출
	public int selectCount(Connection conn) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) from pds_item");
			rs.next();
			return rs.getInt(1);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}
	
	//id로 해당 item의 다운로드 수 증가
	public int  increaseCount(Connection conn, int id) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("update pds_item set downloadcount = downloadcount + 1 where pds_item_id = ?");
			pstmt.setInt(1, id);
			return pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
	
	//id로 해당 item삭제
	public void delete(Connection conn, int itemId) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("delete from pds_item where pds_item_id = ?");
			pstmt.setInt(1, itemId);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
	
	//firstRow와 endRow에 해당하는 범위의 item을 추출
	public List<PdsItem> select(Connection conn, int firstRow, int endRow) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select * from ( "
					+ "    select rownum rnum, pds_item_id, filename, realpath, filesize, downloadcount, description from ( "
					+ "        select * from pds_item m order by m.pds_item_id desc "
					+ "    ) where rownum <= ? "
					+ ") where rnum >= ?");
			
			pstmt.setInt(1, endRow);
			pstmt.setInt(2, firstRow);
			rs = pstmt.executeQuery();
			//item이 하나도 없다면 빈 객체를 리턴
			if (!rs.next()) {
				return Collections.emptyList();
			}
			List<PdsItem> itemList = new ArrayList<PdsItem>();
			do {
				PdsItem article = makeItemFromResultSet(rs);
				itemList.add(article);
			} while (rs.next());
			return itemList;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	//id로 해당 item정보 추출
	public PdsItem selectById(Connection conn, int itemId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select * from pds_item "	+ "where pds_item_id = ?");
			pstmt.setInt(1, itemId);
			rs = pstmt.executeQuery();
			//해당 item이 없다면 null을 리턴
			if (!rs.next()) {
				return null;
			}
			PdsItem item = makeItemFromResultSet(rs);
			return item;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	//select 쿼리의 결과를 item객체에 담아 리턴
	private PdsItem makeItemFromResultSet(ResultSet rs) throws SQLException {
		PdsItem item = new PdsItem();
		item.setId(rs.getInt("pds_item_id"));
		item.setFileName(rs.getString("filename"));
		item.setRealPath(rs.getString("realpath"));
		item.setFileSize(rs.getLong("filesize"));
		item.setDownloadCount(rs.getInt("downloadcount"));
		item.setDescription(rs.getString("description"));
		return item;
	}
}