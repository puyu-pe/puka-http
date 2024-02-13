package pe.puyu.pukahttp.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class Notifier {
	private final List<BiConsumer<String, String>> warningObservers;
	private final List<BiConsumer<String, String>> errorObservers;
	private final List<BiConsumer<String, String>> infoObservers;

	private String defaultTitle = "Notificador";

	public Notifier(Logger logger) {
		warningObservers = new LinkedList<>();
		infoObservers = new LinkedList<>();
		errorObservers = new LinkedList<>();
		warningObservers.add((t, m) -> logger.warn("{}: {}", t, m));
		infoObservers.add((t, m) -> logger.info("{}: {}", t, m));
		errorObservers.add((t, m) -> logger.error("{}: {}", t, m));
	}

	public Notifier() {
		this((Logger) LoggerFactory.getLogger(AppUtil.makeNamespaceLogs("Notifier")));
	}

	public void warn(String message) {
		warn(defaultTitle, message);
	}

	public void warn(String title, String message) {
		for (var subscriber : warningObservers) {
			subscriber.accept(title, message);
		}
	}

	public void error(String message) {
		error(defaultTitle, message);
	}

	public void error(String title, String message) {
		for (var subscriber : errorObservers) {
			subscriber.accept(title, message);
		}
	}

	public void info(String message) {
		info(defaultTitle, message);
	}

	public void info(String title, String message) {
		for (var subscriber : infoObservers) {
			subscriber.accept(title, message);
		}
	}

	public void addWarnSubscriber(BiConsumer<String, String> subscriber) {
		warningObservers.add(subscriber);
	}

	public void addErrorSubscriber(BiConsumer<String, String> subscriber) {
		errorObservers.add(subscriber);
	}

	public void addInfoSubscriber(BiConsumer<String, String> subscriber) {
		infoObservers.add(subscriber);
	}

	public void setDefaultTitle(String title) {
		this.defaultTitle = title;
	}
}
