package com.ait.chat.patterns.facade;

import com.ait.chat.model.ChatRoom;
import com.ait.chat.model.Message;
import com.ait.chat.model.MessageType;
import com.ait.chat.patterns.adapter.IncomingMessage;
import com.ait.chat.patterns.adapter.RestPayload;
import com.ait.chat.patterns.adapter.RestProtocolAdapter;
import com.ait.chat.patterns.adapter.XmppProtocolAdapter;
import com.ait.chat.patterns.decorator.EmojiDecorator;
import com.ait.chat.patterns.decorator.EncryptionDecorator;
import com.ait.chat.patterns.decorator.LoggingDecorator;
import com.ait.chat.patterns.decorator.MessageView;
import com.ait.chat.patterns.decorator.PlainMessageView;
import com.ait.chat.patterns.factory.MessageCreatorProvider;
import com.ait.chat.patterns.observer.ChatEvent;
import com.ait.chat.patterns.observer.ChatEventPublisher;
import com.ait.chat.patterns.observer.ConsoleLogObserver;
import com.ait.chat.patterns.observer.LiveUpdateObserver;
import com.ait.chat.patterns.observer.UnreadCounterObserver;
import com.ait.chat.patterns.strategy.MessageHistory;
import com.ait.chat.repository.ChatRoomRepository;
import com.ait.chat.repository.MessageRepository;
import com.ait.chat.web.dto.RoomView;
import com.ait.chat.web.dto.SendMessageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatFacade {
    private final ChatRoomRepository rooms;
    private final MessageRepository messages;

    private final MessageCreatorProvider creators = new MessageCreatorProvider();
    private final RestProtocolAdapter restAdapter = new RestProtocolAdapter();
    private final XmppProtocolAdapter xmppAdapter = new XmppProtocolAdapter();
    private final ChatEventPublisher publisher = new ChatEventPublisher();
    private final UnreadCounterObserver unreadCounter = new UnreadCounterObserver();
    private final LiveUpdateObserver liveUpdates = new LiveUpdateObserver();

    public ChatFacade(ChatRoomRepository rooms, MessageRepository messages) {
        this.rooms = rooms;
        this.messages = messages;
        publisher.subscribe(new ConsoleLogObserver());
        publisher.subscribe(unreadCounter);
        publisher.subscribe(liveUpdates);
    }

    public ChatRoom createRoom(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Room name is required");
        }
        String id = name.trim().toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", "-").replaceAll("(^-|-$)", "");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Room name must contain letters or digits");
        }
        if (rooms.exists(id)) {
            throw new IllegalArgumentException("Room '" + id + "' already exists");
        }
        ChatRoom room = rooms.save(new ChatRoom(id, name.trim(), description == null ? "" : description.trim()));
        publisher.publish(ChatEvent.roomCreated(id, room.getName()));
        sendSystem(id, "Room #" + room.getName() + " was created");
        return room;
    }

    public List<RoomView> listRooms() {
        return rooms.findAll().stream()
                .map(r -> new RoomView(r.getId(), r.getName(), r.getDescription(),
                        unreadCounter.unreadFor(r.getId()), messages.findByRoom(r.getId()).size()))
                .collect(Collectors.toList());
    }

    public void markRead(String roomId) {
        requireRoom(roomId);
        publisher.publish(ChatEvent.roomRead(roomId));
    }

    public Message send(String roomId, SendMessageRequest req) {
        requireRoom(roomId);
        Message message = creators.forType(req.type()).create(roomId, req.author(), req.content());

        MessageView view = new PlainMessageView(message);
        if (req.emoji()) {
            view = new EmojiDecorator(view);
        }
        view = new LoggingDecorator(view, message.getAuthor());
        if (req.encrypt() && message.getType() == MessageType.TEXT) {
            view = new EncryptionDecorator(view);
            message.setEncrypted(true);
        }
        return store(message, view);
    }

    public Message sendSystem(String roomId, String text) {
        Message message = creators.forType(MessageType.SYSTEM).create(roomId, null, text);
        return store(message, new PlainMessageView(message));
    }

    public Message receiveRest(RestPayload payload) {
        return receive(restAdapter.adapt(payload));
    }

    public Message receiveXmpp(String xml) {
        return receive(xmppAdapter.adapt(xml));
    }

    private Message receive(IncomingMessage incoming) {
        String roomId = incoming.roomId();
        if (!rooms.exists(roomId)) {
            throw new IllegalArgumentException("Room '" + roomId + "' does not exist");
        }
        Message message = creators.forType(MessageType.TEXT).create(roomId, incoming.author(), incoming.content());
        message.setProtocol(incoming.protocol());
        message.getTags().add(incoming.protocol().toLowerCase());
        return store(message, new LoggingDecorator(new PlainMessageView(message), message.getAuthor()));
    }

    private Message store(Message message, MessageView view) {
        message.setContent(view.render());
        List<String> tags = view.tags();
        message.getTags().clear();
        message.getTags().addAll(tags);
        messages.save(message);
        publisher.publish(ChatEvent.messageSent(message));
        return message;
    }

    public List<Message> history(String roomId, String sort) {
        requireRoom(roomId);
        MessageHistory history = new MessageHistory();
        history.setStrategy(sort);
        return history.arrange(messages.findByRoom(roomId));
    }

    public String decrypt(long messageId) {
        Message m = messages.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message " + messageId + " not found"));
        if (!m.isEncrypted()) {
            return m.getContent();
        }
        return EncryptionDecorator.decrypt(m.getContent());
    }

    public SseEmitter subscribeLive() {
        return liveUpdates.connect();
    }

    public Map<String, Object> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<Message> all = messages.findAll();
        stats.put("rooms", rooms.findAll().size());
        stats.put("messages", all.size());
        stats.put("encrypted", all.stream().filter(Message::isEncrypted).count());
        stats.put("byProtocol", all.stream().collect(Collectors.groupingBy(Message::getProtocol, Collectors.counting())));
        stats.put("byType", all.stream().collect(Collectors.groupingBy(m -> m.getType().name(), Collectors.counting())));
        stats.put("observers", publisher.observerCount());
        stats.put("liveClients", liveUpdates.connectedClients());
        stats.put("sortStrategies", new MessageHistory().availableKeys());
        return stats;
    }

    private void requireRoom(String roomId) {
        if (!rooms.exists(roomId)) {
            throw new IllegalArgumentException("Room '" + roomId + "' does not exist");
        }
    }
}
