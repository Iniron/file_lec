package pds.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import pds.dao.PdsItemDao;

public class IncreaseDownloadCountService {
	//싱글톤
	private static IncreaseDownloadCountService instance = new IncreaseDownloadCountService();
	public static IncreaseDownloadCountService getInstance() {
		return instance;
	}
	private IncreaseDownloadCountService() {}

	//id에 해당하는 item의 다운로드 수 를 증가
	public boolean increaseCount(int id) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			int updatedCount = PdsItemDao.getInstance().increaseCount(conn, id);
			return updatedCount == 0 ? false : true;
		} catch (SQLException e) {
			throw new RuntimeException("DB 처리 에러 발생: " + e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn);
		}

	}
}
