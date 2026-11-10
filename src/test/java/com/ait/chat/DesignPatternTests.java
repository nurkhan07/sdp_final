package com.ait.chat;

import com.ait.chat.model.Message;
import com.ait.chat.model.MessageType;
import com.ait.chat.patterns.adapter.IncomingMessage;
import com.ait.chat.patterns.adapter.RestPayload;
import com.ait.chat.patterns.adapter.RestProtocolAdapter;
import com.ait.chat.patterns.adapter.XmppProtocolAdapter;
import com.ait.chat.patterns.decorator.EmojiDecorator;
import com.ait.chat.patterns.decorator.EncryptionDecorator;
import com.ait.chat.patterns.decorator.MessageView;
import com.ait.chat.patterns.decorator.PlainMessageView;
import com.ait.chat.patterns.facade.ChatFacade;
import com.ait.chat.patterns.factory.MessageCreatorProvider;
import com.ait.chat.patterns.observer.ChatEvent;
import com.ait.chat.patterns.observer.ChatEventPublisher;
import com.ait.chat.patterns.observer.UnreadCounterObserver;
import com.ait.chat.patterns.strategy.MessageHistory;
import com.ait.chat.repository.ChatRoomRepository;
import com.ait.chat.repository.MessageRepository;
import com.ait.chat.web.dto.SendMessageRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DesignPatternTests {
    private final MessageCreatorProvider creators = new MessageCreatorProvider();

    private Message msg(long id, String author) {
        Message m = creators.forType(MessageType.TEXT).create("general", author, "msg " + id);
        m.setId(id);
        return m;
    }

    @Test
    void factoryMethodCreatesCorrectTypes() {
        assertEquals(MessageType.TEXT, creators.forType(MessageType.TEXT).create("r", "a", "hi").getType());
        assertEquals("System", creators.forType(MessageType.SYSTEM).create("r", "hacker", "x").getAuthor());
        assertThrows(IllegalArgumentException.class,
                () -> creators.forType(MessageType.IMAGE).create("r", "a", "not-a-url"));
    }

    @Test
    void decoratorsStackAndEncryptionIsReversible() {
        Message m = creators.forType(MessageType.TEXT).create("r", "a", "hello :)");
        MessageView view = new EncryptionDecorator(new EmojiDecorator(new PlainMessageView(m)));
        String cipher = view.render();
        assertNotEquals("hello :)", cipher);
        assertEquals("hello 😊", EncryptionDecorator.decrypt(cipher));
        assertTrue(view.tags().containsAll(List.of("emoji", "encrypted")));
    }

    @Test
    void adaptersConvertForeignProtocols() {
        IncomingMessage rest = new RestProtocolAdapter().adapt(new RestPayload("bot", "general", "hey"));
        assertEquals("general", rest.roomId());
        assertEquals("REST", rest.protocol());

        IncomingMessage xmpp = new XmppProtocolAdapter()
                .adapt("<message from=\"alice@jabber.org\" to=\"random@host\"><body>yo</body></message>");
        assertEquals("random", xmpp.roomId());
        assertEquals("alice (XMPP)", xmpp.author());
        assertEquals("yo", xmpp.content());
    }

    @Test
    void strategyChangesOrderAtRuntime() {
        List<Message> list = new ArrayList<>(List.of(msg(2, "Zed"), msg(1, "Bob"), msg(3, "Amy")));
        MessageHistory history = new MessageHistory();
        assertEquals(1L, history.arrange(list).get(0).getId());
        history.setStrategy("newest");
        assertEquals(3L, history.arrange(list).get(0).getId());
        history.setStrategy("author");
        assertEquals("Amy", history.arrange(list).get(0).getAuthor());
    }

    @Test
    void observerCountsUnreadMessages() {
        ChatEventPublisher publisher = new ChatEventPublisher();
        UnreadCounterObserver counter = new UnreadCounterObserver();
        publisher.subscribe(counter);
        publisher.publish(ChatEvent.messageSent(msg(1, "a")));
        publisher.publish(ChatEvent.messageSent(msg(2, "b")));
        assertEquals(2, counter.unreadFor("general"));
        publisher.publish(ChatEvent.roomRead("general"));
        assertEquals(0, counter.unreadFor("general"));
    }

    @Test
    void facadeRunsTheWholePipeline() {
        ChatFacade chat = new ChatFacade(new ChatRoomRepository(), new MessageRepository());
        chat.createRoom("Test Room", "demo");
        Message sent = chat.send("test-room",
                new SendMessageRequest("Nurzhau", "secret", MessageType.TEXT, true, false));
        assertTrue(sent.isEncrypted());
        assertEquals("secret", chat.decrypt(sent.getId()));
        chat.receiveXmpp("<message from=\"x@y\" to=\"test-room@h\"><body>hi</body></message>");
        assertEquals(3, chat.history("test-room", "oldest").size());
        assertEquals(3, chat.listRooms().get(0).unread());
    }
}
