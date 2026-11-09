package com.ait.chat.patterns.strategy;

import com.ait.chat.model.Message;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageHistory {
    private final Map<String, MessageSortStrategy> strategies = new LinkedHashMap<>();
    private MessageSortStrategy current;

    public MessageHistory() {
        register(new OldestFirstStrategy());
        register(new NewestFirstStrategy());
        register(new AuthorStrategy());
        current = strategies.get("oldest");
    }

    private void register(MessageSortStrategy s) {
        strategies.put(s.key(), s);
    }

    public void setStrategy(String key) {
        MessageSortStrategy s = strategies.get(key == null ? "oldest" : key.toLowerCase());
        if (s == null) {
            throw new IllegalArgumentException("Unknown sort strategy: " + key);
        }
        current = s;
    }

    public MessageSortStrategy getStrategy() {
        return current;
    }

    public List<String> availableKeys() {
        return List.copyOf(strategies.keySet());
    }

    public List<Message> arrange(List<Message> messages) {
        return current.sort(messages);
    }
}
