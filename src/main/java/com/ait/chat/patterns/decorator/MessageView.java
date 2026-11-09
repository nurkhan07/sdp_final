package com.ait.chat.patterns.decorator;

import java.util.List;

public interface MessageView {
    String render();
    List<String> tags();
}
