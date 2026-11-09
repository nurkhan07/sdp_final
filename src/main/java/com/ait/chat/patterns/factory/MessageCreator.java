package com.ait.chat.patterns.factory;

import com.ait.chat.model.Message;

public abstract class MessageCreator {
    public final Message create(String roomId, String author, String content) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room is required");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content is empty");
        }
        Message message = createMessage(roomId, author, content.trim());
        message.getTags().add(message.getType().name().toLowerCase());
        return message;
    }

    protected abstract Message createMessage(String roomId, String author, String content);

    protected String safeAuthor(String author) {
        return (author == null || author.isBlank()) ? "Anonymous" : author.trim();
    }
}
