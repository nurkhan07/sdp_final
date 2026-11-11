package com.ait.chat.patterns.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleLogObserver implements ChatObserver {
    private static final Logger log = LoggerFactory.getLogger(ConsoleLogObserver.class);

    @Override
    public void onEvent(ChatEvent event) {
        if (event.message() != null) {
            log.info("[EVENT] {} in #{} by {} via {}", event.type(), event.roomId(),
                    event.message().getAuthor(), event.message().getProtocol());
        } else {
            log.info("[EVENT] {} #{}", event.type(), event.roomId());
        }
    }
}
