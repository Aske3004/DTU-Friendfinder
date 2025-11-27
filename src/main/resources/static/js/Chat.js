// static/js/Chat.js

let currentUserId = null;
let activeChatId = null;
let stompClient = null;
let subscription = null;

// ----- HELPER FUNCTIONS -----

function getRootElement() {
    return document.getElementById('chatRoot');
}

function initUserId() {
    const root = getRootElement();
    if (!root) {
        console.error('chatRoot element not found!');
        return;
    }
    currentUserId = Number(root.dataset.userId);
    console.log('Initialized user ID:', currentUserId);
}

// ----- LOAD CHATS (LEFT SIDEBAR) -----

function loadChats() {
    if (!currentUserId) {
        console.error('No user ID set');
        return;
    }

    console.log('Loading chats for user:', currentUserId);

    fetch('/api/chats?userId=' + currentUserId)
        .then(res => {
            if (!res.ok) {
                throw new Error('HTTP ' + res.status);
            }
            return res.json();
        })
        .then(chats => {
            console.log('Loaded chats:', chats);
            const list = document.getElementById('chatList');
            if (!list) return;
            list.innerHTML = '';

            if (!chats || chats.length === 0) {
                list.innerHTML = '<li class="no-chats">No chats available</li>';
                return;
            }

            chats.forEach(chat => {
                const li = document.createElement('li');
                li.className = 'chat-list-item';
                // FIXED: Use chatId, not id
                li.dataset.chatId = chat.chatId;

                const btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'chat-list-button';

                const nameSpan = document.createElement('span');
                nameSpan.className = 'chat-list-name';
                // FIXED: Use chatName, not name
                nameSpan.textContent = chat.chatName || 'Chat';

                const previewSmall = document.createElement('small');
                previewSmall.className = 'chat-list-preview';
                // FIXED: Check lastMessage structure from ChatDTO
                previewSmall.textContent = chat.lastMessage?.content || 'No messages yet';

                btn.appendChild(nameSpan);
                btn.appendChild(previewSmall);
                // FIXED: Use chatId
                btn.addEventListener('click', () => selectChat(chat.chatId));

                li.appendChild(btn);
                list.appendChild(li);
            });
        })
        .catch(err => console.error('Failed to load chats:', err));
}

// ----- SELECT CHAT -----

function selectChat(chatId) {
    if (activeChatId === chatId) return;

    console.log('Selecting chat:', chatId);
    activeChatId = chatId;

    // Mark active in sidebar
    document.querySelectorAll('#chatList li').forEach(li => {
        li.classList.toggle('active', String(li.dataset.chatId) === String(chatId));
    });

    // Fetch chat info
    fetch('/api/chats/' + chatId)
        .then(res => {
            if (!res.ok) {
                throw new Error('HTTP ' + res.status);
            }
            return res.json();
        })
        .then(chat => {
            if (!chat) return;
            console.log('Chat details:', chat);

            const titleEl = document.getElementById('chatTitle');
            const partEl = document.getElementById('chatParticipants');

            if (titleEl) {
                // FIXED: Use chatName
                titleEl.textContent = chat.chatName || 'Chat';
            }

            if (partEl) {
                // FIXED: Use participantUsernames or participantCount from ChatDTO
                if (chat.participantUsernames && chat.participantUsernames.length > 0) {
                    partEl.textContent = chat.participantUsernames.join(', ');
                } else if (chat.participantCount) {
                    partEl.textContent = chat.participantCount + ' participants';
                } else {
                    partEl.textContent = '';
                }
            }
        })
        .catch(err => console.error('Failed to load chat details:', err));

    // Fetch messages
    fetch(`/api/chats/${chatId}/messages?userId=${currentUserId}`)
        .then(res => {
            if (!res.ok) {
                throw new Error('HTTP ' + res.status);
            }
            return res.json();
        })
        .then(messages => {
            console.log('Loaded messages:', messages);
            const container = document.getElementById('chatMessages');
            if (!container) return;

            container.innerHTML = '';

            if (!messages || messages.length === 0) {
                container.innerHTML = '<div class="no-messages">No messages yet</div>';
                return;
            }

            messages.forEach(appendMessage);
            container.scrollTop = container.scrollHeight;
        })
        .catch(err => console.error('Failed to load messages:', err));

    // Subscribe to WebSocket for this chat
    if (stompClient && stompClient.connected) {
        subscribeToChat(chatId);
    }
}

// ----- WEBSOCKET -----

function connectSocket() {
    console.log('Connecting to WebSocket...');

    // FIXED: Use /ws endpoint from WebSocketConfig
    const socket = new SockJS('/ws-chat');
    stompClient = Stomp.over(socket);

    // Disable debug output (optional)
    stompClient.debug = null;

    stompClient.connect({}, () => {
        console.log('✓ Connected to WebSocket');
        if (activeChatId) {
            subscribeToChat(activeChatId);
        }
    }, (error) => {
        console.error('✗ WebSocket connection error:', error);
        // Retry after 5 seconds
        setTimeout(connectSocket, 5000);
    });
}

function subscribeToChat(chatId) {
    if (!stompClient || !stompClient.connected) {
        console.error('STOMP client not connected');
        return;
    }

    // Unsubscribe from previous chat
    if (subscription) {
        console.log('Unsubscribing from previous chat');
        subscription.unsubscribe();
    }

    console.log('Subscribing to /topic/chat/' + chatId);

    subscription = stompClient.subscribe('/topic/chat/' + chatId, (message) => {
        console.log('Received WebSocket message:', message.body);
        const msg = JSON.parse(message.body);
        appendMessage(msg);
    });
}

// ----- DISPLAY A MESSAGE -----

function appendMessage(msg) {
    const container = document.getElementById('chatMessages');
    if (!container) return;

    // FIXED: Check if it's a system message
    if (msg.type === 'JOIN' || msg.type === 'LEAVE' || msg.type === 'SYSTEM') {
        const systemDiv = document.createElement('div');
        systemDiv.className = 'system-message';
        systemDiv.textContent = msg.content;
        container.appendChild(systemDiv);
        container.scrollTop = container.scrollHeight;
        return;
    }

    const isOwn = msg.senderId === currentUserId;

    const row = document.createElement('div');
    row.className = 'message-row ' + (isOwn ? 'own' : 'other');

    const bubble = document.createElement('div');
    bubble.className = 'message-bubble';

    const meta = document.createElement('div');
    meta.className = 'message-meta';

    const senderSpan = document.createElement('span');
    senderSpan.className = 'message-sender';
    // FIXED: Use senderUsername from MessageDTO
    senderSpan.textContent = msg.senderUsername || 'Unknown';

    const timeSpan = document.createElement('span');
    timeSpan.className = 'message-time';
    // FIXED: Use timestamp from MessageDTO
    if (msg.timestamp) {
        const dt = new Date(msg.timestamp);
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

// ----- SEND MESSAGE -----

function setupForm() {
    const form = document.getElementById('chatForm');
    const input = document.getElementById('chatMessageInput');
    if (!form || !input) {
        console.error('Form or input not found');
        return;
    }

    form.addEventListener('submit', (event) => {
        event.preventDefault();

        if (!activeChatId) {
            alert('Please select a chat first');
            return;
        }

        if (!stompClient || !stompClient.connected) {
            alert('Not connected to chat server');
            return;
        }

        const text = input.value.trim();
        if (!text) return;

        // FIXED: Match MessageDTO structure
        const payload = {
            chatId: activeChatId,
            senderId: currentUserId,
            senderUsername: 'User', // You might want to store this
            content: text,
            type: 'CHAT'  // FIXED: Use 'CHAT' not 'TEXT'
        };

        console.log('Sending message:', payload);

        try {
            stompClient.send('/app/chat.send', {}, JSON.stringify(payload));
            input.value = '';
            input.focus();
        } catch (error) {
            console.error('Error sending message:', error);
            alert('Failed to send message');
        }
    });

    // Also support Enter key
    input.addEventListener('keypress', (event) => {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            form.dispatchEvent(new Event('submit'));
        }
    });
}

// ----- INITIALIZATION -----

document.addEventListener('DOMContentLoaded', () => {
    console.log('Initializing chat...');
    initUserId();

    if (!currentUserId) {
        console.error('Failed to initialize user ID');
        alert('Authentication error. Please log in.');
        return;
    }

    loadChats();
    setupForm();
    connectSocket();
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect();
    }
});