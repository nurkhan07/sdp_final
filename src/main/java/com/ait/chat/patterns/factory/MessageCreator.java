package com.ait.chat.patterns.factory;

import com.ait.chat.model.Message;

/**
 * FACTORY METHOD - Creator.
 * create() validates input and delegates object creation
 * to the subclass-specific factory method createMessage().
 */
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

    /** The factory method. */
    protected abstract Message createMessage(String roomId, String author, String content);

    protected String safeAuthor(String author) {
        return (author == null || author.isBlank()) ? "Anonymous" : author.trim();
    }
}
