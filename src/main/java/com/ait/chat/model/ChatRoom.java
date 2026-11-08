package com.ait.chat.model;

import java.time.Instant;

public class ChatRoom {
    private final String id;
    private final String name;
    private final String description;
    private final Instant createdAt = Instant.now();

    public ChatRoom(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
}
