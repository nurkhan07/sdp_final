package com.ait.chat.patterns.strategy;

import com.ait.chat.model.Message;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorStrategy implements MessageSortStrategy {
    @Override
    public String key() {
        return "author";
    }

    @Override
    public List<Message> sort(List<Message> messages) {
        return messages.stream()
                .sorted(Comparator.comparing((Message m) -> m.getAuthor().toLowerCase())
                        .thenComparing(Message::getId))
                .collect(Collectors.toList());
    }
}
