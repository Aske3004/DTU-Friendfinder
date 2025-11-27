// static/js/chat.js

let currentUserId = null;
let activeChatId = null;
let stompClient = null;
let subscription = null;

// ----- HJÆLPEFUNKTIONER -----

function getRootElement() {
    return document.getElementById('chatRoot');
}

function initUserId() {
    const root = getRootElement();
    if (!root) return;
    currentUserId = Number(root.dataset.userId);
}

// ----- HENT CHATS TIL VENSTRE SIDE -----

function loadChats() {
    if (!currentUserId) return;

    fetch('/api/chats?userId=' + currentUserId)
        .then(res => res.ok ? res.json() : [])
        .then(chats => {
            const list = document.getElementById('chatList');
            if (!list) return;
            list.innerHTML = '';

            chats.forEach(chat => {
                const li = document.createElement('li');
                li.className = 'chat-list-item';
                li.dataset.chatId = chat.id;

                const btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'chat-list-button';

                const nameSpan = document.createElement('span');
                nameSpan.className = 'chat-list-name';
                nameSpan.textContent = chat.name || 'Chat';

                const previewSmall = document.createElement('small');
                previewSmall.className = 'chat-list-preview';
                previewSmall.textContent = chat.lastMessagePreview || '';

                btn.appendChild(nameSpan);
                btn.appendChild(previewSmall);
                btn.addEventListener('click', () => selectChat(chat.id));

                li.appendChild(btn);
                list.appendChild(li);
            });
        })
        .catch(err => console.error('Failed to load chats', err));
}

// ----- VÆLG CHAT -----

function selectChat(chatId) {
    if (activeChatId === chatId) return;
    activeChatId = chatId;

    // marker aktiv i sidebar
    document.querySelectorAll('#chatList li').forEach(li => {
        li.classList.toggle('active', String(li.dataset.chatId) === String(chatId));
    });

    // hent chat-info
    fetch('/api/chats/' + chatId)
        .then(res => res.ok ? res.json() : null)
        .then(chat => {
            if (!chat) return;
            const titleEl = document.getElementById('chatTitle');
            const partEl = document.getElementById('chatParticipants');
            if (titleEl) titleEl.textContent = chat.name || 'Chat';
            if (partEl) {
                // tilpas til dit ChatDTO-felt:
                const participants = chat.participantNames || '';
                partEl.textContent = participants;
            }
        })
        .catch(err => console.error('Failed to load chat details', err));

    // hent beskeder
    fetch(`/api/chats/${chatId}/messages?userId=${currentUserId}`)
        .then(res => res.ok ? res.json() : [])
        .then(messages => {
            const container = document.getElementById('chatMessages');
            if (!container) return;
            container.innerHTML = '';
            messages.forEach(appendMessage);
            container.scrollTop = container.scrollHeight;
        })
        .catch(err => console.error('Failed to load messages', err));

    // subscribe WS
    if (stompClient && stompClient.connected) {
        subscribeToChat(chatId);
    }
}

// ----- WEBSOCKET -----

function connectSocket() {
    const socket = new SockJS('/chat-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        console.log('Connected to WebSocket');
        if (activeChatId) {
            subscribeToChat(activeChatId);
        }
    });
}

function subscribeToChat(chatId) {
    if (!stompClient) return;
    if (subscription) {
        subscription.unsubscribe();
    }
    subscription = stompClient.subscribe('/topic/chat/' + chatId, (message) => {
        const body = JSON.parse(message.body);
        appendMessage(body);
    });
}

// ----- VIS EN BESKED -----

function appendMessage(msg) {
    const container = document.getElementById('chatMessages');
    if (!container) return;

    const isOwn = msg.senderId === currentUserId;

    const row = document.createElement('div');
    row.className = 'message-row ' + (isOwn ? 'own' : 'other');

    const bubble = document.createElement('div');
    bubble.className = 'message-bubble';

    const meta = document.createElement('div');
    meta.className = 'message-meta';

    const senderSpan = document.createElement('span');
    senderSpan.className = 'message-sender';
    senderSpan.textContent = msg.senderName || '';

    const timeSpan = document.createElement('span');
    timeSpan.className = 'message-time';
    if (msg.sentAt) {
        const dt = new Date(msg.sentAt);
        timeSpan.textContent = dt.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } else {
        timeSpan.textContent = '';
    }

    meta.appendChild(senderSpan);
    meta.appendChild(timeSpan);

    const textDiv = document.createElement('div');
    textDiv.className = 'message-text';
    textDiv.textContent = msg.content || '';

    bubble.appendChild(meta);
    bubble.appendChild(textDiv);
    row.appendChild(bubble);
    container.appendChild(row);
    container.scrollTop = container.scrollHeight;
}

// ----- SEND BESKED -----

function setupForm() {
    const form = document.getElementById('chatForm');
    const input = document.getElementById('chatMessageInput');
    if (!form || !input) return;

    form.addEventListener('submit', (event) => {
        event.preventDefault();
        if (!activeChatId || !stompClient || !stompClient.connected) return;

        const text = input.value.trim();
        if (!text) return;

        const payload = {
            chatId: activeChatId,
            senderId: currentUserId,
            content: text,
            type: "TEXT"  // matcher MessageDTO.MessageType.TEXT
        };

        stompClient.send('/app/chat.send', {}, JSON.stringify(payload));
        input.value = '';
    });
}

// ----- INIT -----

document.addEventListener('DOMContentLoaded', () => {
    initUserId();
    loadChats();
    setupForm();
    connectSocket();
});
