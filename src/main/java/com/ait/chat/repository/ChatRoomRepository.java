package com.ait.chat.repository;

import com.ait.chat.model.ChatRoom;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ChatRoomRepository {
    private final Map<String, ChatRoom> rooms = new ConcurrentHashMap<>();

    public ChatRoom save(ChatRoom room) {
        rooms.put(room.getId(), room);
        return room;
    }

    public Optional<ChatRoom> findById(String id) {
        return Optional.ofNullable(id == null ? null : rooms.get(id));
    }

    public boolean exists(String id) {
        return id != null && rooms.containsKey(id);
    }

    public List<ChatRoom> findAll() {
        List<ChatRoom> list = new ArrayList<>(rooms.values());
        list.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        return list;
    }
}
