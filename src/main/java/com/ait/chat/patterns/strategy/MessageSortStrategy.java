package com.ait.chat.patterns.strategy;

import com.ait.chat.model.Message;

import java.util.List;

public interface MessageSortStrategy {
    String key();
    List<Message> sort(List<Message> messages);
}
