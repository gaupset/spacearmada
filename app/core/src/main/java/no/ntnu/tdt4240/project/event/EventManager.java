package no.ntnu.tdt4240.project.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    List<EventListener> listeners;

    public EventManager() {
        listeners = new ArrayList<>();
    }

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }

    public void alert(Event event) {
        for (EventListener listener : listeners) {
            listener.receive(event);
        }
    }
}
