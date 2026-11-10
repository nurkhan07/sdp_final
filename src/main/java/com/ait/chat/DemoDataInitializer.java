package com.ait.chat;

import com.ait.chat.model.MessageType;
import com.ait.chat.patterns.adapter.RestPayload;
import com.ait.chat.patterns.facade.ChatFacade;
import com.ait.chat.web.dto.SendMessageRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {
    private final ChatFacade chat;

    public DemoDataInitializer(ChatFacade chat) {
        this.chat = chat;
    }

    @Override
    public void run(String... args) {
        chat.createRoom("General", "Everything about the course");
        chat.createRoom("SDP Team", "Nurzhau, Nursultan, Gabidin");
        chat.createRoom("Random", "Memes and off-topic");

        chat.send("general", new SendMessageRequest("Nurzhau", "Hi everyone :) Factory Method created this message", MessageType.TEXT, false, true));
        chat.send("general", new SendMessageRequest("Nursultan", "Strategy lets you sort this history :fire:", MessageType.TEXT, false, true));
        chat.send("general", new SendMessageRequest("Gabidin", "Facade + Observer keep everything in sync <3", MessageType.TEXT, false, true));
        chat.send("sdp-team", new SendMessageRequest("Gabidin", "Secret: defense is on Friday", MessageType.TEXT, true, false));
        chat.send("random", new SendMessageRequest("Nurzhau", "https://picsum.photos/seed/chatterbox/480/260", MessageType.IMAGE, false, false));
        chat.receiveRest(new RestPayload("slack-bot", "general", "Hello from a REST integration!"));
        chat.receiveXmpp("<message from=\"alice@jabber.org\" to=\"general@chatterbox\"><body>Hi from XMPP</body></message>");
    }
}
