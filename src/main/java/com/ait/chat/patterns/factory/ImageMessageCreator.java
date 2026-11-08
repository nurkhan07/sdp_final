package com.ait.chat.patterns.factory;

import com.ait.chat.model.Message;
import com.ait.chat.model.MessageType;

/** FACTORY METHOD - Concrete creator for image messages (content is an image URL). */
public class ImageMessageCreator extends MessageCreator {

    @Override
    protected Message createMessage(String roomId, String author, String content) {
        if (!content.startsWith("http://") && !content.startsWith("https://")) {
            throw new IllegalArgumentException("Image message must be an http(s) URL");
        }
        return new Message(roomId, safeAuthor(author), content, MessageType.IMAGE);
    }
}
