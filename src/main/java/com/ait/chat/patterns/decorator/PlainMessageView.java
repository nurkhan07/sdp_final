package com.ait.chat.patterns.decorator;

import com.ait.chat.model.Message;

import java.util.ArrayList;
import java.util.List;

public class PlainMessageView implements MessageView {
    private final Message message;

    public PlainMessageView(Message message) {
        this.message = message;
    }

    @Override
    public String render() {
        return message.getContent();
    }

    @Override
    public List<String> tags() {
        return new ArrayList<>(message.getTags());
    }
}
