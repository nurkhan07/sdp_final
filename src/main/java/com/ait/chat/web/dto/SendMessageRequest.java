package com.ait.chat.web.dto;

import com.ait.chat.model.MessageType;

public record SendMessageRequest(String author, String content, MessageType type,
                                 boolean encrypt, boolean emoji) {
}
