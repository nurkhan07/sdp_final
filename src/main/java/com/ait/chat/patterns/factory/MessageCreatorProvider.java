package com.ait.chat.patterns.factory;

import com.ait.chat.model.MessageType;

import java.util.EnumMap;
import java.util.Map;

public class MessageCreatorProvider {
    private final Map<MessageType, MessageCreator> creators = new EnumMap<>(MessageType.class);

    public MessageCreatorProvider() {
        creators.put(MessageType.TEXT, new TextMessageCreator());
        creators.put(MessageType.IMAGE, new ImageMessageCreator());
        creators.put(MessageType.SYSTEM, new SystemMessageCreator());
    }

    public MessageCreator forType(MessageType type) {
        return creators.get(type == null ? MessageType.TEXT : type);
    }
}
