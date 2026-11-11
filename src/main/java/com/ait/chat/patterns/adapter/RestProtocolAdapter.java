package com.ait.chat.patterns.adapter;

public class RestProtocolAdapter implements ProtocolAdapter<RestPayload> {
    @Override
    public String protocolName() {
        return "REST";
    }

    @Override
    public IncomingMessage adapt(RestPayload payload) {
        if (payload == null || payload.getText() == null) {
            throw new IllegalArgumentException("REST payload has no text");
        }
        String author = payload.getSender() == null ? "rest-bot" : payload.getSender();
        return new IncomingMessage(payload.getChannel(), author + " (REST)", payload.getText(), protocolName());
    }
}
