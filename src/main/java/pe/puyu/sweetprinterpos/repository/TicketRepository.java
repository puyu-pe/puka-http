package pe.puyu.sweetprinterpos.repository;

import ch.qos.logback.classic.Logger;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.LoggerFactory;
import pe.puyu.sweetprinterpos.repository.model.Ticket;
import pe.puyu.sweetprinterpos.util.AppUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class TicketRepository {
	private Dao<Ticket, Long> ticketDao;
	private final List<Consumer<Integer>> observers = new LinkedList<>();
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
			return (int)ticketDao.countOf();
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

	public List<Ticket> getAll() {
		try {
			return ticketDao.queryForAll();
		} catch (Exception e) {
			logger.error("Exception on getAll tickets: {}", e.getMessage());
			return new LinkedList<>();
		}
	}

	public void addObserver(Consumer<Integer> consumer) {
		observers.add(consumer);
	}

	public void removeObserver(Consumer<Integer> consumer) {
		observers.remove(consumer);
	}

	private void notifyObservers() {
		observers.forEach(observer -> observer.accept(countAll()));
	}
}
