const api = (path, options = {}) =>
    fetch('/api' + path, {headers: {'Content-Type': 'application/json'}, ...options})
        .then(async res => {
            if (res.status === 204 || res.headers.get('content-length') === '0') return null;
            const data = await res.json().catch(() => null);
            if (!res.ok) throw new Error((data && data.error) || res.statusText);
            return data;
        });

const $ = id => document.getElementById(id);
const state = {room: null, rooms: [], proto: 'rest'};

const SAMPLES = {
    rest: JSON.stringify({sender: 'slack-bot', channel: 'general', text: 'Deploy finished ✅'}, null, 2),
    xmpp: '<message from="bob@jabber.org" to="general@chatterbox">\n  <body>Hello from XMPP!</body>\n</message>'
};

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c => ({'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c]));
}

function showError(msg) {
    const box = $('error');
    box.textContent = msg;
    box.classList.remove('hidden');
    clearTimeout(showError.t);
    showError.t = setTimeout(() => box.classList.add('hidden'), 4000);
}

async function loadRooms() {
    state.rooms = await api('/rooms');
    const list = $('roomList');
    list.innerHTML = '';
    state.rooms.forEach(r => {
        const li = document.createElement('li');
        li.className = r.id === state.room ? 'active' : '';
        const unread = r.id === state.room ? 0 : r.unread;
        li.innerHTML = `<span># ${escapeHtml(r.name)}</span>${unread ? `<span class="badge">${unread}</span>` : ''}`;
        li.onclick = () => openRoom(r.id);
        list.appendChild(li);
    });
}

async function openRoom(id) {
    state.room = id;
    const room = state.rooms.find(r => r.id === id);
    $('roomTitle').textContent = room ? '# ' + room.name : id;
    $('roomDesc').textContent = room ? room.description : '';
    await api(`/rooms/${id}/read`, {method: 'POST'});
    await Promise.all([loadMessages(), loadRooms()]);
}

async function loadMessages() {
    if (!state.room) return;
    const sort = $('sortSelect').value;
    const msgs = await api(`/rooms/${state.room}/messages?sort=${sort}`);
    const box = $('messages');
    const me = $('username').value.trim();
    box.innerHTML = msgs.length ? '' : '<div class="empty">No messages yet — say hi!</div>';
    msgs.forEach(m => box.appendChild(renderMessage(m, me)));
    if (sort !== 'newest') box.scrollTop = box.scrollHeight; else box.scrollTop = 0;
}

function renderMessage(m, me) {
    const div = document.createElement('div');
    const isSystem = m.type === 'SYSTEM';
    div.className = 'msg' + (isSystem ? ' system' : '') + (!isSystem && me && m.author === me ? ' mine' : '');
    const time = new Date(m.createdAt).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
    let body;
    if (m.type === 'IMAGE') {
        body = `<img src="${escapeHtml(m.content)}" alt="image" loading="lazy">`;
    } else if (m.encrypted) {
        body = `<div class="body cipher" id="body-${m.id}">${escapeHtml(m.content)}</div>`;
    } else {
        body = `<div class="body">${escapeHtml(m.content)}</div>`;
    }
    const tags = m.tags.map(t => `<span class="tag ${escapeHtml(t)}">${escapeHtml(t)}</span>`).join('');
    const decryptBtn = m.encrypted ? `<button class="link-btn" data-decrypt="${m.id}">decrypt</button>` : '';
    div.innerHTML = isSystem
        ? `${escapeHtml(m.content)} · ${time}`
        : `<div class="meta"><b>${escapeHtml(m.author)}</b><span>${time}</span><span>${escapeHtml(m.protocol)}</span>${decryptBtn}</div>${body}<div class="tags">${tags}</div>`;
    return div;
}

$('messages').addEventListener('click', async e => {
    const id = e.target.dataset.decrypt;
    if (!id) return;
    try {
        const res = await api(`/messages/${id}/decrypt`);
        const body = $('body-' + id);
        body.textContent = res.content;
        body.classList.remove('cipher');
        e.target.remove();
    } catch (err) { showError(err.message); }
});

$('composer').addEventListener('submit', async e => {
    e.preventDefault();
    if (!state.room) return showError('Select a room first');
    const content = $('msgInput').value.trim();
    if (!content) return;
    try {
        await api(`/rooms/${state.room}/messages`, {
            method: 'POST',
            body: JSON.stringify({
                author: $('username').value.trim() || 'Anonymous',
                content,
                type: $('msgType').value,
                emoji: $('optEmoji').checked,
                encrypt: $('optEncrypt').checked
            })
        });
        $('msgInput').value = '';
    } catch (err) { showError(err.message); }
});

$('sortSelect').addEventListener('change', loadMessages);
$('username').addEventListener('change', () => {
    localStorage.setItem('chatterbox-user', $('username').value.trim());
    loadMessages();
});

$('roomForm').addEventListener('submit', async e => {
    e.preventDefault();
    try {
        const room = await api('/rooms', {method: 'POST', body: JSON.stringify({name: $('roomName').value, description: 'Created from the web UI'})});
        $('roomName').value = '';
        await loadRooms();
        openRoom(room.id);
    } catch (err) { showError(err.message); }
});

document.querySelectorAll('.tab').forEach(tab => tab.addEventListener('click', () => {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    tab.classList.add('active');
    state.proto = tab.dataset.proto;
    $('externalPayload').value = SAMPLES[state.proto];
}));

$('sendExternal').addEventListener('click', async () => {
    const raw = $('externalPayload').value;
    try {
        if (state.proto === 'rest') {
            await api('/external/rest', {method: 'POST', body: raw});
        } else {
            await api('/external/xmpp', {method: 'POST', headers: {'Content-Type': 'text/plain'}, body: raw});
        }
    } catch (err) { showError(err.message); }
});

async function loadStats() {
    const s = await api('/stats');
    const proto = Object.entries(s.byProtocol).map(([k, v]) => `${k}: ${v}`).join(' · ') || '—';
    $('stats').innerHTML = [
        ['Rooms', s.rooms], ['Messages', s.messages], ['Encrypted', s.encrypted],
        ['Observers', s.observers], ['Live clients', s.liveClients], ['Protocols', proto]
    ].map(([k, v]) => `<div class="stat"><div class="v">${escapeHtml(v)}</div><div class="k">${k}</div></div>`).join('');
}

function logActivity(text) {
    const li = document.createElement('li');
    li.textContent = new Date().toLocaleTimeString() + ' — ' + text;
    $('activity').prepend(li);
    while ($('activity').children.length > 30) $('activity').lastChild.remove();
}

function connectLive() {
    const es = new EventSource('/api/events');
    es.onopen = () => { $('liveDot').className = 'dot on'; loadStats(); };
    es.onerror = () => { $('liveDot').className = 'dot off'; };
    es.addEventListener('chat', async e => {
        const ev = JSON.parse(e.data);
        if (ev.type === 'MESSAGE_SENT') {
            logActivity(`${ev.author} → #${ev.roomId} (${ev.protocol})`);
            if (ev.roomId === state.room) {
                await api(`/rooms/${state.room}/read`, {method: 'POST'});
                loadMessages();
            }
        } else if (ev.type === 'ROOM_CREATED') {
            logActivity(`room #${ev.info} created`);
        }
        if (ev.type !== 'ROOM_READ') { loadRooms(); loadStats(); }
    });
}

(async function init() {
    $('username').value = localStorage.getItem('chatterbox-user') || '';
    $('externalPayload').value = SAMPLES.rest;
    await loadRooms();
    if (state.rooms.length) await openRoom(state.rooms[0].id);
    loadStats();
    connectLive();
})().catch(err => showError(err.message));
