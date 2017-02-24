package jdbc;

import java.sql.DriverManager;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class DBCPInit extends HttpServlet {

	@Override
	public void init() throws ServletException {
		loadJDBCDriver();
		initConnectionPool();
	}

	private void loadJDBCDriver() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("fail to load JDBC Driver", ex);
		}
	}

	private void initConnectionPool() {
		try {
			String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:XE";
			String username = "hr";
			String pw = "hr";
//커넥션팩토리 생성. 커넥션 팩토리는 새로운 커넥션을 생성할때 사용한다.			
			ConnectionFactory connFactory = 
					new DriverManagerConnectionFactory(jdbcUrl, username, pw);
//DBCP가 커넥션 풀에 커넥션을 보관할때 사용하는 PoolableConnectionFactory 생성
            //실제로 내부적으로 커넥션을 담고있고 커넥션을 관리하는데 기능을 제공한다. 
			//ex)커넥션을 close하면 종료하지 않고 커넥션 풀에 반환
			PoolableConnectionFactory poolableConnFactory = 
					new PoolableConnectionFactory(connFactory, null);
			 //커넥션이 유효한지 확인할때 사용하는 쿼리를 설정한다.
			poolableConnFactory.setValidationQuery("select 1");
			 //커넥션 풀의 설정 정보를 생성한다.
			GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
			//유효 커넥션 검사 주기
			poolConfig.setTimeBetweenEvictionRunsMillis(1000L * 60L * 5L);
			//풀에 있는 커넥션이 유효한지 검사 유무 설정
			poolConfig.setTestWhileIdle(true);
			 //커넥션 최소갯수 설정
			poolConfig.setMinIdle(4);
			  //커넥션 최대 갯수 설정
			poolConfig.setMaxTotal(50);
            //커넥션 풀 생성. 인자로는 위에서 생성한  PoolabeConnectionFactory와 GenericObjectPoolConfig를 사용한다.
			GenericObjectPool<PoolableConnection> connectionPool = 
					new GenericObjectPool<>(poolableConnFactory, poolConfig);
			//PoolabeConnectionFactory에도 커넥션 풀을 연결
			poolableConnFactory.setPool(connectionPool);
			//커넥션 풀을 제공하는 jdbc 드라이버를 등록.
			Class.forName("org.apache.commons.dbcp2.PoolingDriver");
			
			PoolingDriver driver = 
					(PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
			 //위에서 커넥션 풀 드라이버에 생성한 커넥션 풀을 등록한다. 이름은 pool이다.
											driver.registerPool("pool", connectionPool);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
	
	
}
