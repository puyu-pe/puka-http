package pe.puyu.pukahttp.repository;

import ch.qos.logback.classic.Logger;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.repository.model.Ticket;
import pe.puyu.pukahttp.util.AppUtil;
import org.h2.tools.Server;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AppDatabase {
	private JdbcPooledConnectionSource connectionSource;
	private Server server;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("AppDatabase"));

	public AppDatabase() {
		try {
			server = Server.createTcpServer("-tcpAllowOthers", "-ifNotExists", "-tcpPort", "9130").start();
			var url = String.format("jdbc:h2:%s/file:%s/%s", server.getURL(), AppUtil.getDatabaseDirectory(), "pukahttp_db");
			connectionSource = new JdbcPooledConnectionSource(url);
			connectionSource.setLoginTimeoutSecs(15);
			createTables();
			logger.info("Start connection DB success  :)");
			logger.info("DB url: {}", url);
		} catch (Exception e) {
			logger.error("Exception at connection DB start!!!: {}", e.getLocalizedMessage(), e);
		}
	}

	public Optional<JdbcPooledConnectionSource> getConnection() {
		return Optional.ofNullable(connectionSource);
	}

	public void close() {
		try {
			if (connectionSource != null) {
				connectionSource.close();
				server.stop();
				logger.info("close connection success DB :)");
			}
		} catch (Exception e) {
			logger.error("Exception at close connection DB: {}", e.getMessage());
		}finally {
			connectionSource = null;
		}
	}

	private void createTables() throws Exception {
		TableUtils.createTableIfNotExists(connectionSource, Ticket.class);
	}
}
