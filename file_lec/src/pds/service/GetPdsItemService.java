package pds.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import pds.dao.PdsItemDao;
import pds.model.PdsItem;

public class GetPdsItemService {
	//싱글톤
	private static GetPdsItemService instance = new GetPdsItemService();
	public static GetPdsItemService getInstance() {
		return instance;
	}
	private GetPdsItemService() {}

	//id로 해당 Item객체를 추출
	public PdsItem getPdsItem(int id) throws PdsItemNotFoundException {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();						//BD연결
			PdsItem pdsItem = PdsItemDao.getInstance().selectById(conn, id);//id를통해 해당 item추출
			if (pdsItem == null) {
				throw new PdsItemNotFoundException("존재하지 않습니다:" + id);
			}
			return pdsItem;
		} catch (SQLException e) {
			throw new RuntimeException("DB 처리 에러 발생: " + e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}
