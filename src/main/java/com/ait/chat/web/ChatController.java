package com.ait.chat.web;

import com.ait.chat.model.ChatRoom;
import com.ait.chat.model.Message;
import com.ait.chat.patterns.adapter.RestPayload;
import com.ait.chat.patterns.facade.ChatFacade;
import com.ait.chat.web.dto.CreateRoomRequest;
import com.ait.chat.web.dto.RoomView;
import com.ait.chat.web.dto.SendMessageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatFacade chat;

    public ChatController(ChatFacade chat) {
        this.chat = chat;
    }

    @GetMapping("/rooms")
    public List<RoomView> rooms() {
        return chat.listRooms();
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatRoom createRoom(@RequestBody CreateRoomRequest req) {
        return chat.createRoom(req.name(), req.description());
    }

    @GetMapping("/rooms/{roomId}/messages")
    public List<Message> history(@PathVariable String roomId,
                                 @RequestParam(defaultValue = "oldest") String sort) {
        return chat.history(roomId, sort);
    }

    @PostMapping("/rooms/{roomId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public Message send(@PathVariable String roomId, @RequestBody SendMessageRequest req) {
        return chat.send(roomId, req);
    }

    @PostMapping("/rooms/{roomId}/read")
    public void markRead(@PathVariable String roomId) {
        chat.markRead(roomId);
    }

    @GetMapping("/messages/{id}/decrypt")
    public Map<String, String> decrypt(@PathVariable long id) {
        return Map.of("content", chat.decrypt(id));
    }

    @PostMapping("/external/rest")
    @ResponseStatus(HttpStatus.CREATED)
    public Message external(@RequestBody RestPayload payload) {
        return chat.receiveRest(payload);
    }

    @PostMapping(value = "/external/xmpp", consumes = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Message xmpp(@RequestBody String stanza) {
        return chat.receiveXmpp(stanza);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return chat.stats();
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events() {
        return chat.subscribeLive();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
