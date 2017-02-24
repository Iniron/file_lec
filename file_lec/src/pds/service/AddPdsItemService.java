package pds.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import pds.dao.PdsItemDao;
import pds.model.AddRequest;
import pds.model.PdsItem;

public class AddPdsItemService {
	private static AddPdsItemService instance = new AddPdsItemService();
	public static AddPdsItemService getInstance(){
		return instance;
	}
	
	private AddPdsItemService(){
	}
	
	public PdsItem add(AddRequest request){
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();	//커넥션 객체에서 연결
			conn.setAutoCommit(false);					//오토커밋 끄기
			
			PdsItem pdsItem = request.toPdsItem();	//PdsItem객체로 리턴
			//DB에 conn객체와 PdsItem을 인자로 넘겨 저장
			int id = PdsItemDao.getInstance().insert(conn, pdsItem);
			if(id==-1){
				JdbcUtil.rollback(conn);
				throw new RuntimeException("DB 삽입 안 됨");
			}
			//DB에 저장이 됬다면 그때의 id값을 저장
			pdsItem.setId(id);
			
			conn.commit();
			System.out.println("AddPdsItemService add");
			return pdsItem;	//pdsItem객체 리턴
			
		} catch (Exception e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally{
			if(conn != null){
				try{
					conn.setAutoCommit(true);
				}catch(SQLException e){
				}
			}
			JdbcUtil.close(conn);
		}
	}
}
