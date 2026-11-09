package com.ait.chat.repository;

import com.ait.chat.model.Message;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MessageRepository {
    private final List<Message> messages = new CopyOnWriteArrayList<>();
    private final AtomicLong sequence = new AtomicLong();

    public Message save(Message message) {
        message.setId(sequence.incrementAndGet());
        messages.add(message);
        return message;
    }

    public List<Message> findByRoom(String roomId) {
        return messages.stream().filter(m -> m.getRoomId().equals(roomId)).collect(Collectors.toList());
    }

    public Optional<Message> findById(long id) {
        return messages.stream().filter(m -> m.getId() == id).findFirst();
    }

    public long count() {
        return messages.size();
    }

    public List<Message> findAll() {
        return List.copyOf(messages);
    }
}
