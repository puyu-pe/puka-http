package pe.puyu.sweetprinterpos.repository;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.util.AppUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class SqlLiteConnection {
	private Connection connection;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("SqlLiteConnection"));

	public SqlLiteConnection() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:SweetPrinterPOS.db");
			logger.info("Connection success SqlLite :)");
		} catch (SQLException e) {
			logger.error("Exception at connection SqlLite start!!!: {}", e.getLocalizedMessage(), e);
		}
	}

	public Optional<Connection> getConnection() {
		return Optional.ofNullable(connection);
	}

	public void close() {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
				logger.info("close connection success SqlLite :)");
			}
		} catch (Exception e) {
			logger.error("Exception at close connection SqlLite: {}", e.getMessage());
		}
	}

}
