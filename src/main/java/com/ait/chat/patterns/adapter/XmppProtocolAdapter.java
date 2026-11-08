package com.ait.chat.patterns.adapter;

/** ADAPTER - XMPP stanza -> IncomingMessage ("alice@jabber.org" -> "alice", "general@host" -> "general"). */
public class XmppProtocolAdapter implements ProtocolAdapter<String> {

    @Override
    public String protocolName() {
        return "XMPP";
    }

    @Override
    public IncomingMessage adapt(String rawXml) {
        XmppStanza stanza = XmppStanza.parse(rawXml);
        if (stanza.getBody() == null || stanza.getTo() == null) {
            throw new IllegalArgumentException("XMPP stanza must have 'to' and <body>");
        }
        return new IncomingMessage(localPart(stanza.getTo()),
                localPart(stanza.getFrom()) + " (XMPP)", stanza.getBody(), protocolName());
    }

    private static String localPart(String jid) {
        if (jid == null || jid.isBlank()) {
            return "unknown";
        }
        int at = jid.indexOf('@');
        return at > 0 ? jid.substring(0, at) : jid;
    }
}
