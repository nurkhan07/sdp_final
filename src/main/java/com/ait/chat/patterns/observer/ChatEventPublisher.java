package com.ait.chat.patterns.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatEventPublisher {
    private final List<ChatObserver> observers = new CopyOnWriteArrayList<>();

    public void subscribe(ChatObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(ChatObserver observer) {
        observers.remove(observer);
    }

    public void publish(ChatEvent event) {
        for (ChatObserver observer : observers) {
            observer.onEvent(event);
        }
    }

    public int observerCount() {
        return observers.size();
    }
}
