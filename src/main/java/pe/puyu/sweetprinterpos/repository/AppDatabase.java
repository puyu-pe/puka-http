package pe.puyu.sweetprinterpos.repository;

import ch.qos.logback.classic.Logger;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.repository.model.Ticket;
import pe.puyu.sweetprinterpos.util.AppUtil;

import java.util.Optional;


public class AppDatabase {
	private JdbcPooledConnectionSource connectionSource;
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("AppDatabase"));

	public AppDatabase() {
		try {
			var url = String.format("jdbc:h2:file:%s/pos", AppUtil.getDatabaseDirectory());
			connectionSource = new JdbcPooledConnectionSource(url);
			connectionSource.setLoginTimeoutSecs(15);
			createTables();
			logger.info("Start connection DB success  :)");
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
				connectionSource = null;
				logger.info("close connection success DB :)");
			}
		} catch (Exception e) {
			logger.error("Exception at close connection DB: {}", e.getMessage());
		}
	}

	public void clearTables() {
		try {
			TableUtils.clearTable(connectionSource, Ticket.class);
		} catch (Exception e) {
			logger.error("Exception at clear tables: {}", e.getLocalizedMessage(), e);
		}
	}

	private void createTables() {
		try {
			TableUtils.createTableIfNotExists(connectionSource, Ticket.class);
		} catch (Exception e) {
			logger.error("Exception at create tables: {}", e.getLocalizedMessage(), e);
		}
	}
}
