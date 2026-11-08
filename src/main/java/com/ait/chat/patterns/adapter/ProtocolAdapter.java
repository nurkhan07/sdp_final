package com.ait.chat.patterns.adapter;

/** ADAPTER - Target interface: turns a foreign protocol payload into an IncomingMessage. */
public interface ProtocolAdapter<T> {
    String protocolName();
    IncomingMessage adapt(T externalPayload);
}
