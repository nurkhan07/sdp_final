# Chatterbox — Chat/Messaging App (SDP Final Project, Topic 14)

Spring Boot 3 + plain HTML/CSS/JS frontend. The app uses **6 design patterns** working together.

## Team
| Student | Patterns |
|---|---|
| Nurzhau Nurkhan | Factory Method, Decorator |
| Rassulov Nursultan | Adapter, Strategy |
| Kaldybek Gabidin | Observer, Facade |

## Patterns
| Pattern | Where | What it does |
|---|---|---|
| Factory Method | `patterns/factory` | `MessageCreator.create()` validates, subclasses `Text/Image/SystemMessageCreator` build the message |
| Decorator | `patterns/decorator` | `PlainMessageView` wrapped by `EmojiDecorator`, `LoggingDecorator`, `EncryptionDecorator` |
| Adapter | `patterns/adapter` | `RestProtocolAdapter` (JSON) and `XmppProtocolAdapter` (XML stanza) → `IncomingMessage` |
| Strategy | `patterns/strategy` | `MessageHistory` switches `Oldest/Newest/Author` sort at runtime |
| Observer | `patterns/observer` | `ChatEventPublisher` notifies `ConsoleLogObserver`, `UnreadCounterObserver`, `LiveUpdateObserver` (SSE to the browser) |
| Facade | `patterns/facade` | `ChatFacade` — the only class the REST controller uses |

Flow of one message: **Controller → Facade → Factory Method → Decorators → Repository → Observers → browser (SSE)**.
External messages: **REST/XMPP payload → Adapter → Facade → same pipeline**.

## Run
Requirements: JDK 17+, Maven 3.9+ (or IntelliJ IDEA).
```
mvn spring-boot:run
```
Open http://localhost:8080 . Tests: `mvn test`.

## REST API
| Method | URL | Description |
|---|---|---|
| GET | `/api/rooms` | rooms with unread counters |
| POST | `/api/rooms` | `{ "name": "...", "description": "..." }` |
| GET | `/api/rooms/{id}/messages?sort=oldest\|newest\|author` | history (Strategy) |
| POST | `/api/rooms/{id}/messages` | `{ "author", "content", "type": "TEXT\|IMAGE\|SYSTEM", "emoji": true, "encrypt": false }` |
| POST | `/api/rooms/{id}/read` | reset unread counter |
| GET | `/api/messages/{id}/decrypt` | decrypt an encrypted message |
| POST | `/api/external/rest` | `{ "sender", "channel", "text" }` (Adapter) |
| POST | `/api/external/xmpp` | `<message from="a@x" to="general@x"><body>..</body></message>` (Adapter) |
| GET | `/api/stats` | statistics |
| GET | `/api/events` | Server-Sent Events stream (Observer) |

## Demo scenario for the defense
1. Send a text with `:)` and the 😊 toggle → EmojiDecorator; with 🔒 → EncryptionDecorator, click *decrypt*.
2. Change message type to *Image URL* / *System* → different Factory Method creators.
3. Switch *Sort* → Strategy.
4. Right panel: send REST JSON and XMPP XML → Adapter; message appears with REST/XMPP tag.
5. Open a second browser tab: messages and unread badges update live → Observer (SSE).
6. All of the above go through `ChatFacade` → Facade.
