package pe.puyu.pukahttp.application.notifier;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class AppNotifier {

    private final List<Consumer<String>> warningObservers;
    private final List<Consumer<String>> errorObservers;
    private final List<Consumer<String>> infoObservers;

    public AppNotifier() {
        warningObservers = new LinkedList<>();
        infoObservers = new LinkedList<>();
        errorObservers = new LinkedList<>();
    }

    public void warn(String message) {
        for (var subscriber : warningObservers) {
            subscriber.accept(message);
        }
    }

    public void error(String message) {
        for (var subscriber : errorObservers) {
            subscriber.accept(message);
        }
    }

    public void info(String message) {
        for (var subscriber : infoObservers) {
            subscriber.accept(message);
        }
    }

    public void addWarnSubscriber(Consumer<String> subscriber) {
        warningObservers.add(subscriber);
    }

    public void addErrorSubscriber(Consumer<String> subscriber) {
        errorObservers.add(subscriber);
    }

    public void addInfoSubscriber(Consumer<String> subscriber) {
        infoObservers.add(subscriber);
    }

}
