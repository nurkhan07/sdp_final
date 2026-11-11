package com.ait.chat.patterns.observer;

import com.ait.chat.model.Message;

public record ChatEvent(Type type, String roomId, Message message, String info) {
    public enum Type { MESSAGE_SENT, ROOM_CREATED, ROOM_READ }

    public static ChatEvent messageSent(Message m) {
        return new ChatEvent(Type.MESSAGE_SENT, m.getRoomId(), m, null);
    }

    public static ChatEvent roomCreated(String roomId, String name) {
        return new ChatEvent(Type.ROOM_CREATED, roomId, null, name);
    }

    public static ChatEvent roomRead(String roomId) {
        return new ChatEvent(Type.ROOM_READ, roomId, null, null);
    }
}
