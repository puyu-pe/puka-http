package pe.puyu.pukahttp.repository;

import ch.qos.logback.classic.Logger;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.LoggerFactory;
import pe.puyu.pukahttp.repository.model.Ticket;
import pe.puyu.pukahttp.util.AppUtil;
import pe.puyu.pukahttp.util.UUIDGenerator;

import java.util.*;
import java.util.function.Consumer;

public class TicketRepository {
	private Dao<Ticket, Long> ticketDao;
	private final HashMap<String, Consumer<Integer>> observers = new LinkedHashMap<>();
	private final Logger logger = (Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("TicketRepository"));

	public TicketRepository(JdbcPooledConnectionSource connectionSource) {
		try {
			ticketDao = DaoManager.createDao(connectionSource, Ticket.class);
		} catch (Exception e) {
			logger.error("Exception at creat ticketDao: {}", e.getLocalizedMessage(), e);
		}
	}

	public int countAll() {
		try {
			return (int) ticketDao.countOf();
		} catch (Exception e) {
			logger.error("Exception at countOf ticketDao: {}", e.getLocalizedMessage(), e);
			return 0;
		}
	}

	public void insert(String jsonTicket) {
		try {
			Ticket ticket = new Ticket();
			ticket.setData(jsonTicket);
			ticketDao.create(ticket);
			notifyObservers();
		} catch (Exception e) {
			logger.error("Exception on save ticket: {}", e.getMessage(), e);
		}
	}

	public void deleteAll() {
		try {
			TableUtils.clearTable(ticketDao.getConnectionSource(), Ticket.class);
			notifyObservers();
		} catch (Exception e) {
			logger.error("Exception on release tickets: {}", e.getMessage());
		}
	}

	public int deleteWithDatesBefore(Date dateTime) {
		try {
			DeleteBuilder<Ticket, Long> deleteBuilder = ticketDao.deleteBuilder();
			deleteBuilder.where().le("created_at", dateTime);
			int rowsDeleted = deleteBuilder.delete();
			notifyObservers();
			return rowsDeleted;
		} catch (Exception e) {
			logger.error(
				"Exception on delete tickets before date {}: {}",
				dateTime,
				e.getMessage()
			);
			return 0;
		}
	}

	public List<Ticket> getAll() {
		try {
			return ticketDao.queryForAll();
		} catch (Exception e) {
			logger.error("Exception on getAll tickets: {}", e.getMessage());
			return new LinkedList<>();
		}
	}

	public void addObserver(String idObserver, Consumer<Integer> consumer) {
		observers.put(idObserver, consumer);
	}

	public void removeObserver(String idObserver) {
		observers.remove(idObserver);
	}

	private void notifyObservers() {
		observers.forEach((id, observer) -> {
			try{
				observer.accept(countAll());
			}catch (Exception e){
				logger.error("Exception on notifyObservers: {}", e.getMessage());
			}
		});
	}
}
