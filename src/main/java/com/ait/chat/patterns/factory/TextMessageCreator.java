package com.ait.chat.patterns.factory;

import com.ait.chat.model.Message;
import com.ait.chat.model.MessageType;

public class TextMessageCreator extends MessageCreator {
    public static final int MAX_LENGTH = 1000;

    @Override
    protected Message createMessage(String roomId, String author, String content) {
        if (content.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Text message is longer than " + MAX_LENGTH + " characters");
        }
        return new Message(roomId, safeAuthor(author), content, MessageType.TEXT);
    }
}
