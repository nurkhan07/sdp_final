package com.ait.chat.patterns.adapter;

public interface ProtocolAdapter<T> {
    String protocolName();
    IncomingMessage adapt(T externalPayload);
}
