package com.ait.chat.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Message {
    private Long id;
    private final String roomId;
    private final String author;
    private String content;
    private final MessageType type;
    private String protocol = "WEB";
    private boolean encrypted;
    private final List<String> tags = new ArrayList<>();
    private final Instant createdAt = Instant.now();

    public Message(String roomId, String author, String content, MessageType type) {
        this.roomId = roomId;
        this.author = author;
        this.content = content;
        this.type = type;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoomId() { return roomId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public MessageType getType() { return type; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
    public List<String> getTags() { return tags; }
    public Instant getCreatedAt() { return createdAt; }
}
