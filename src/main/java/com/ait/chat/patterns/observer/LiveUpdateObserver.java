package com.ait.chat.patterns.observer;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/** OBSERVER - pushes every event to connected browsers through Server-Sent Events. */
public class LiveUpdateObserver implements ChatObserver {
    private final List<SseEmitter> clients = new CopyOnWriteArrayList<>();

    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(0L);
        clients.add(emitter);
        emitter.onCompletion(() -> clients.remove(emitter));
        emitter.onTimeout(() -> clients.remove(emitter));
        emitter.onError(e -> clients.remove(emitter));
        return emitter;
    }

    @Override
    public void onEvent(ChatEvent event) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", event.type().name());
        data.put("roomId", event.roomId());
        data.put("info", event.info());
        if (event.message() != null) {
            data.put("author", event.message().getAuthor());
            data.put("protocol", event.message().getProtocol());
            data.put("messageId", event.message().getId());
        }
        for (SseEmitter client : clients) {
            try {
                client.send(SseEmitter.event().name("chat").data(data));
            } catch (IOException | IllegalStateException e) {
                clients.remove(client);
            }
        }
    }

    public int connectedClients() {
        return clients.size();
    }
}
