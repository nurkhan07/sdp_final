package com.ait.chat.patterns.adapter;

public record IncomingMessage(String roomId, String author, String content, String protocol) {
}
