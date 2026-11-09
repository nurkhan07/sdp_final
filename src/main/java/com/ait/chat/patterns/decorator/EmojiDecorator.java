package com.ait.chat.patterns.decorator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmojiDecorator extends MessageDecorator {
    private static final Map<String, String> EMOJI = new LinkedHashMap<>();
    static {
        EMOJI.put(":)", "😊");
        EMOJI.put(":(", "😞");
        EMOJI.put(":D", "😃");
        EMOJI.put("<3", "❤️");
        EMOJI.put(":fire:", "🔥");
        EMOJI.put(":ok:", "👍");
    }

    public EmojiDecorator(MessageView wrapped) {
        super(wrapped);
    }

    @Override
    public String render() {
        String text = super.render();
        for (Map.Entry<String, String> e : EMOJI.entrySet()) {
            text = text.replace(e.getKey(), e.getValue());
        }
        return text;
    }

    @Override
    public List<String> tags() {
        List<String> tags = super.tags();
        tags.add("emoji");
        return tags;
    }
}
