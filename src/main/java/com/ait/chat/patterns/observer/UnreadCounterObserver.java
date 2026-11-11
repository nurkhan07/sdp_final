package com.ait.chat.patterns.observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UnreadCounterObserver implements ChatObserver {
    private final Map<String, Integer> unread = new ConcurrentHashMap<>();

    @Override
    public void onEvent(ChatEvent event) {
        switch (event.type()) {
            case MESSAGE_SENT -> unread.merge(event.roomId(), 1, Integer::sum);
            case ROOM_READ -> unread.put(event.roomId(), 0);
            case ROOM_CREATED -> unread.putIfAbsent(event.roomId(), 0);
        }
    }

    public int unreadFor(String roomId) {
        return unread.getOrDefault(roomId, 0);
    }

    public Map<String, Integer> snapshot() {
        return Map.copyOf(unread);
    }
}
