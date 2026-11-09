package com.ait.chat.patterns.decorator;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class EncryptionDecorator extends MessageDecorator {
    public static final int SHIFT = 3;

    public EncryptionDecorator(MessageView wrapped) {
        super(wrapped);
    }

    @Override
    public String render() {
        return encrypt(super.render());
    }

    @Override
    public List<String> tags() {
        List<String> tags = super.tags();
        tags.add("encrypted");
        return tags;
    }

    public static String encrypt(String plain) {
        StringBuilder sb = new StringBuilder();
        plain.codePoints().forEach(cp -> sb.appendCodePoint(cp + SHIFT));
        return Base64.getEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(String cipher) {
        String shifted = new String(Base64.getDecoder().decode(cipher), StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        shifted.codePoints().forEach(cp -> sb.appendCodePoint(cp - SHIFT));
        return sb.toString();
    }
}
