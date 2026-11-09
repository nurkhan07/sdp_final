package com.ait.chat.patterns.strategy;

import com.ait.chat.model.Message;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NewestFirstStrategy implements MessageSortStrategy {
    @Override
    public String key() {
        return "newest";
    }

    @Override
    public List<Message> sort(List<Message> messages) {
        return messages.stream()
                .sorted(Comparator.comparing(Message::getId).reversed())
                .collect(Collectors.toList());
    }
}
