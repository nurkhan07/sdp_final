package com.ait.chat.patterns.adapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmppStanza {
    private static final Pattern FROM = Pattern.compile("from=\"([^\"]*)\"");
    private static final Pattern TO = Pattern.compile("to=\"([^\"]*)\"");
    private static final Pattern BODY = Pattern.compile("<body>(.*?)</body>", Pattern.DOTALL);

    private final String from;
    private final String to;
    private final String body;

    public XmppStanza(String from, String to, String body) {
        this.from = from;
        this.to = to;
        this.body = body;
    }

    public static XmppStanza parse(String xml) {
        if (xml == null) {
            throw new IllegalArgumentException("Empty XMPP stanza");
        }
        return new XmppStanza(find(FROM, xml), find(TO, xml), find(BODY, xml));
    }

    private static String find(Pattern p, String xml) {
        Matcher m = p.matcher(xml);
        return m.find() ? m.group(1).trim() : null;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getBody() { return body; }
}
