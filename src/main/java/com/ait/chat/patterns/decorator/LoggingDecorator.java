package com.ait.chat.patterns.decorator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoggingDecorator extends MessageDecorator {
    private static final Logger log = LoggerFactory.getLogger(LoggingDecorator.class);
    private final String author;

    public LoggingDecorator(MessageView wrapped, String author) {
        super(wrapped);
        this.author = author;
    }

    @Override
    public String render() {
        String text = super.render();
        log.info("[AUDIT] message from '{}' rendered ({} chars)", author, text.length());
        return text;
    }

    @Override
    public List<String> tags() {
        List<String> tags = super.tags();
        tags.add("logged");
        return tags;
    }
}
