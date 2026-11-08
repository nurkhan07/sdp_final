package com.ait.chat.patterns.observer;

/** OBSERVER - Observer interface. */
public interface ChatObserver {
    void onEvent(ChatEvent event);
}
