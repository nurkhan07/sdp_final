package com.ait.chat.patterns.adapter;

public class RestPayload {
    private String sender;
    private String channel;
    private String text;

    public RestPayload() { }

    public RestPayload(String sender, String channel, String text) {
        this.sender = sender;
        this.channel = channel;
        this.text = text;
    }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
