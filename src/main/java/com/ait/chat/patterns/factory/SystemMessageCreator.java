package com.ait.chat.patterns.factory;

import com.ait.chat.model.Message;
import com.ait.chat.model.MessageType;

public class SystemMessageCreator extends MessageCreator {
    @Override
    protected Message createMessage(String roomId, String author, String content) {
        return new Message(roomId, "System", content, MessageType.SYSTEM);
    }
}
