package com.ait.chat.web.dto;

public record RoomView(String id, String name, String description, int unread, int messages) {
}
