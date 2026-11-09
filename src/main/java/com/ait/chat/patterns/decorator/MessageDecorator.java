package com.ait.chat.patterns.decorator;

import java.util.List;

public abstract class MessageDecorator implements MessageView {
    protected final MessageView wrapped;

    protected MessageDecorator(MessageView wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String render() {
        return wrapped.render();
    }

    @Override
    public List<String> tags() {
        return wrapped.tags();
    }
}
