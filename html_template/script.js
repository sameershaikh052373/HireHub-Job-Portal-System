// SocialVibe - Comprehensive Social Media Platform

// Global variables
let posts = [];
let confessions = [];
let currentMood = '😊';
let currentChat = 'sarah';
let isDarkTheme = false;
let typingTimeout;

// Sample data
const samplePosts = [
    {
        author: 'Priya Sharma',
        mood: '😊',
        content: 'Just had the most amazing brunch with friends! Life is good ✨',
        time: '2 hours ago',
        likes: 24,
        comments: 8,
        reactions: { '❤️': 12, '😂': 5, '👍': 7 }
    },
    {
        author: 'Arjun Patel',
        mood: '🚀',
        content: 'Finally finished my weekend project! Nothing beats that feeling of accomplishment 💪',
        time: '4 hours ago',
        likes: 18,
        comments: 12,
        reactions: { '🔥': 8, '👍': 10 }
    },
    {
        author: 'Kavya Singh',
        mood: '❤️',
        content: 'Movie night with the squad was incredible! We laughed until our stomachs hurt 🍿🎬',
        time: '6 hours ago',
        likes: 31,
        comments: 15,
        reactions: { '😂': 15, '❤️': 16 }
    }
];

const chatMessages = {
    sarah: [
        { author: 'Priya Sharma', content: 'Hey! How was your day?', time: '10:30 AM', own: false },
        { author: 'You', content: 'It was great! Just finished work. How about you?', time: '10:32 AM', own: true },
        { author: 'Priya Sharma', content: 'Same here! Want to grab coffee later? ☕', time: '10:35 AM', own: false }
    ],
    group: [
        { author: 'Arjun', content: 'Anyone up for movies tonight?', time: '9:15 AM', own: false },
        { author: 'Kavya', content: 'I\'m in! What are we watching?', time: '9:20 AM', own: false },
        { author: 'You', content: 'Count me in too! 🍿', time: '9:25 AM', own: true }
    ]
};

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    loadPosts();
    loadChatMessages();
    loadTrendingPosts();
    loadPeopleSuggestions();
    loadConfessions();
});

function initializeApp() {
    // Navigation
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const target = link.getAttribute('href').substring(1);
            showSection(target);
            
            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
        });
    });

    // Search functionality
    document.getElementById('searchInput').addEventListener('input', handleSearch);
}

function showSection(sectionId) {
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => section.classList.remove('active'));
    document.getElementById(sectionId).classList.add('active');
}

// Theme toggle
function toggleTheme() {
    isDarkTheme = !isDarkTheme;
    document.body.setAttribute('data-theme', isDarkTheme ? 'dark' : 'light');
    document.getElementById('themeIcon').className = isDarkTheme ? 'fas fa-sun' : 'fas fa-moon';
}

// Mood selection
function selectMood(mood) {
    currentMood = mood;
    document.querySelectorAll('.mood-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelector(`[data-mood="${mood}"]`).classList.add('active');
}

// Posts functionality
function createPost() {
    const input = document.getElementById('postInput');
    const content = input.value.trim();
    
    if (content) {
        const newPost = {
            author: 'You',
            mood: currentMood,
            content: content,
            time: 'Just now',
            likes: 0,
            comments: 0,
            reactions: {}
        };
        
        posts.unshift(newPost);
        renderPosts();
        input.value = '';
        
        // Update notification badge
        updateNotificationBadge();
    }
}

function loadPosts() {
    posts = [...samplePosts];
    renderPosts();
}

function renderPosts() {
    const feed = document.getElementById('postsFeed');
    feed.innerHTML = '';
    
    posts.forEach((post, index) => {
        const postElement = document.createElement('div');
        postElement.className = 'post';
        
        const reactionsHtml = Object.entries(post.reactions || {})
            .map(([emoji, count]) => `<span class="reaction">${emoji} ${count}</span>`)
            .join('');
        
        postElement.innerHTML = `
            <div class="post-header">
                <img src="https://via.placeholder.com/40" alt="Profile" class="profile-pic">
                <div>
                    <div class="post-author">${post.author} ${post.mood}</div>
                    <div class="post-time">${post.time}</div>
                </div>
            </div>
            <div class="post-content">${post.content}</div>
            <div class="post-reactions">${reactionsHtml}</div>
            <div class="post-actions">
                <button class="action-btn" onclick="likePost(${index})">
                    <i class="fas fa-heart"></i> ${post.likes}
                </button>
                <button class="action-btn" onclick="commentPost(${index})">
                    <i class="fas fa-comment"></i> ${post.comments}
                </button>
                <button class="action-btn" onclick="sharePost(${index})">
                    <i class="fas fa-share"></i> Share
                </button>
                <button class="action-btn" onclick="reactToPost(${index})">
                    <i class="fas fa-smile"></i> React
                </button>
            </div>
        `;
        feed.appendChild(postElement);
    });
}

function likePost(index) {
    posts[index].likes++;
    renderPosts();
}

function commentPost(index) {
    posts[index].comments++;
    renderPosts();
}

function sharePost(index) {
    alert('Post shared!');
}

function reactToPost(index) {
    const reactions = ['❤️', '😂', '👍', '🔥', '💯'];
    const reaction = reactions[Math.floor(Math.random() * reactions.length)];
    
    if (!posts[index].reactions[reaction]) {
        posts[index].reactions[reaction] = 0;
    }
    posts[index].reactions[reaction]++;
    renderPosts();
}

// Stories functionality
function addStory() {
    showSection('stories');
    document.querySelector('[href="#stories"]').classList.add('active');
    document.querySelectorAll('.nav-link').forEach(l => {
        if (l.getAttribute('href') !== '#stories') l.classList.remove('active');
    });
}

function applyFilter(filter) {
    const canvas = document.getElementById('storyCanvas');
    canvas.className = `story-canvas ${filter}`;
}

function shareStory() {
    const text = document.getElementById('storyText').value;
    if (text) {
        alert('Story shared! It will disappear in 24 hours.');
        document.getElementById('storyText').value = '';
    }
}

// Chat functionality
function selectChat(chatId) {
    currentChat = chatId;
    
    document.querySelectorAll('.chat-item').forEach(item => item.classList.remove('active'));
    event.target.closest('.chat-item').classList.add('active');
    
    const titles = {
        sarah: 'Priya Sharma',
        group: 'Weekend Squad'
    };
    
    const status = {
        sarah: 'Online',
        group: '3 members online'
    };
    
    document.getElementById('chatTitle').textContent = titles[chatId];
    document.getElementById('chatStatus').textContent = status[chatId];
    
    loadChatMessages();
}

function loadChatMessages() {
    const messagesContainer = document.getElementById('chatMessages');
    messagesContainer.innerHTML = '';
    
    const messages = chatMessages[currentChat] || [];
    
    messages.forEach(message => {
        const messageElement = document.createElement('div');
        messageElement.className = `message ${message.own ? 'own' : ''}`;
        messageElement.innerHTML = `
            <img src="https://via.placeholder.com/40" alt="Profile" class="profile-pic">
            <div class="message-content">
                ${!message.own ? `<div class="message-author">${message.author}</div>` : ''}
                <div>${message.content}</div>
                <div class="message-time">${message.time}</div>
            </div>
        `;
        messagesContainer.appendChild(messageElement);
    });
    
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

function sendMessage() {
    const input = document.getElementById('messageInput');
    const content = input.value.trim();
    
    if (content) {
        const newMessage = {
            author: 'You',
            content: content,
            time: new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}),
            own: true
        };
        
        if (!chatMessages[currentChat]) {
            chatMessages[currentChat] = [];
        }
        
        chatMessages[currentChat].push(newMessage);
        loadChatMessages();
        input.value = '';
        
        // Simulate response
        setTimeout(() => simulateResponse(), 1000 + Math.random() * 2000);
    }
}

function simulateResponse() {
    const responses = [
        'That sounds awesome! 😊',
        'I totally agree!',
        'Thanks for sharing!',
        'Haha, that\'s funny! 😂',
        'Can\'t wait!',
        'You\'re the best! ❤️'
    ];
    
    const authors = ['Priya Sharma', 'Arjun', 'Kavya'];
    
    const response = {
        author: authors[Math.floor(Math.random() * authors.length)],
        content: responses[Math.floor(Math.random() * responses.length)],
        time: new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}),
        own: false
    };
    
    chatMessages[currentChat].push(response);
    loadChatMessages();
}

function handleMessageKeyPress(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
}

function showTyping() {
    const indicator = document.getElementById('typingIndicator');
    indicator.style.display = 'block';
    
    clearTimeout(typingTimeout);
    typingTimeout = setTimeout(() => {
        indicator.style.display = 'none';
    }, 2000);
}

function newChat() {
    alert('New chat feature coming soon!');
}

// Emoji picker
function toggleEmojiPicker() {
    const picker = document.getElementById('emojiPicker');
    picker.style.display = picker.style.display === 'none' ? 'block' : 'none';
}

function addEmoji(emoji) {
    const input = document.getElementById('messageInput');
    input.value += emoji;
    toggleEmojiPicker();
}

// Discover functionality
function showDiscoverTab(tabName) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.discover-tab').forEach(tab => tab.classList.remove('active'));
    
    event.target.classList.add('active');
    document.getElementById(tabName).classList.add('active');
}

function loadTrendingPosts() {
    const trending = document.getElementById('trendingPosts');
    const trendingData = [
        { content: 'Weekend vibes are hitting different! 🌟', likes: 156, author: 'TrendUser1' },
        { content: 'Coffee and good friends = perfect morning ☕', likes: 89, author: 'TrendUser2' },
        { content: 'New music discovery has me dancing! 🎵', likes: 234, author: 'TrendUser3' }
    ];
    
    trending.innerHTML = trendingData.map(post => `
        <div class="post">
            <div class="post-header">
                <img src="https://via.placeholder.com/40" alt="Profile" class="profile-pic">
                <div class="post-author">${post.author}</div>
            </div>
            <div class="post-content">${post.content}</div>
            <div class="post-actions">
                <button class="action-btn"><i class="fas fa-heart"></i> ${post.likes}</button>
            </div>
        </div>
    `).join('');
}

function loadPeopleSuggestions() {
    const people = document.getElementById('peopleSuggestions');
    const suggestions = [
        { name: 'Ananya Reddy', bio: 'Photography enthusiast', mutual: 5 },
        { name: 'Ishaan Mehta', bio: 'Travel blogger', mutual: 12 },
        { name: 'Riya Agarwal', bio: 'Music producer', mutual: 8 }
    ];
    
    people.innerHTML = suggestions.map(person => `
        <div class="person-suggestion">
            <img src="https://via.placeholder.com/60" alt="Profile" class="profile-pic">
            <div class="person-info">
                <h4>${person.name}</h4>
                <p>${person.bio}</p>
                <small>${person.mutual} mutual friends</small>
            </div>
            <button class="follow-btn">Follow</button>
        </div>
    `).join('');
}

function postConfession() {
    const input = document.getElementById('confessionInput');
    const content = input.value.trim();
    
    if (content) {
        const confession = {
            content: content,
            time: 'Just now',
            likes: 0
        };
        
        confessions.unshift(confession);
        loadConfessions();
        input.value = '';
    }
}

function loadConfessions() {
    const feed = document.getElementById('confessionsFeed');
    const sampleConfessions = [
        { content: 'I still sleep with a stuffed animal and I\'m 25... 🧸', time: '1 hour ago', likes: 23 },
        { content: 'I pretend to be busy at work but I\'m actually planning my next vacation', time: '3 hours ago', likes: 45 },
        { content: 'I talk to my plants and I think they understand me 🌱', time: '5 hours ago', likes: 67 }
    ];
    
    const allConfessions = [...confessions, ...sampleConfessions];
    
    feed.innerHTML = allConfessions.map((confession, index) => `
        <div class="post">
            <div class="post-header">
                <img src="https://via.placeholder.com/40" alt="Anonymous" class="profile-pic">
                <div class="post-author">Anonymous</div>
                <div class="post-time">${confession.time}</div>
            </div>
            <div class="post-content">${confession.content}</div>
            <div class="post-actions">
                <button class="action-btn" onclick="likeConfession(${index})">
                    <i class="fas fa-heart"></i> ${confession.likes}
                </button>
            </div>
        </div>
    `).join('');
}

function likeConfession(index) {
    if (index < confessions.length) {
        confessions[index].likes++;
    }
    loadConfessions();
}

// Profile functionality
function updateProfile() {
    const name = document.getElementById('editName').value;
    const status = document.getElementById('editStatus').value;
    const bio = document.getElementById('editBio').value;
    
    if (name) document.getElementById('profileName').textContent = name;
    if (status) document.getElementById('profileStatus').textContent = status;
    if (bio) document.getElementById('profileBio').textContent = bio;
    
    document.getElementById('editName').value = '';
    document.getElementById('editStatus').value = '';
    document.getElementById('editBio').value = '';
    
    alert('Profile updated successfully!');
}

// Utility functions
function addImage() {
    alert('Image upload feature coming soon!');
}

function addGif() {
    alert('GIF picker coming soon!');
}

function handleSearch() {
    const query = document.getElementById('searchInput').value;
    if (query.length > 2) {
        console.log('Searching for:', query);
        // Implement search functionality
    }
}

function updateNotificationBadge() {
    const badge = document.getElementById('notificationBadge');
    let count = parseInt(badge.textContent) + 1;
    badge.textContent = count;
}

// Handle Enter key for post input
document.addEventListener('keypress', function(event) {
    if (event.target.id === 'postInput' && event.key === 'Enter') {
        createPost();
    }
});

console.log('SocialVibe - Social Media Platform loaded successfully!');