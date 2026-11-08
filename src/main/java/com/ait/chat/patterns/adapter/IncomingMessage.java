package com.ait.chat.patterns.adapter;

/** ADAPTER - Target format the chat core understands. */
public record IncomingMessage(String roomId, String author, String content, String protocol) {
}
