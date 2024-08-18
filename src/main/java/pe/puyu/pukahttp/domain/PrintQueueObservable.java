package pe.puyu.pukahttp.domain;


import java.util.HashMap;
import java.util.function.Consumer;

public abstract class PrintQueueObservable {

    private final HashMap<String, Consumer<Long>> observers = new HashMap<>();

    public void addObserver(String key, Consumer<Long> observer) {
        observers.put(key, observer);
    }

    public void removeObserver(String key) {
        observers.remove(key);
    }

    protected void notifyQueueSize() {
        observers.values().forEach(observer -> observer.accept(getQueueSize()));
    }

    public abstract long getQueueSize();

}
