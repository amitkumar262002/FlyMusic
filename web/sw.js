// 📱 Service Worker for FlyMusic AI - Offline Support
const CACHE_NAME = 'flymusic-ai-v1.0.0';
const STATIC_CACHE = 'flymusic-static-v1';
const DYNAMIC_CACHE = 'flymusic-dynamic-v1';

// Files to cache for offline use
const STATIC_FILES = [
    '/',
    '/index.html',
    '/styles.css',
    '/firebase-config.js',
    '/auth.js',
    '/music-api.js',
    '/player.js',
    '/app.js',
    '/sample-data.js',
    '/advanced-features.js',
    'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css',
    'https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap'
];

// Install event - cache static files
self.addEventListener('install', (event) => {
    console.log('🚀 Service Worker installing...');
    
    event.waitUntil(
        caches.open(STATIC_CACHE)
            .then((cache) => {
                console.log('📦 Caching static files');
                return cache.addAll(STATIC_FILES);
            })
            .catch((error) => {
                console.error('❌ Cache installation failed:', error);
            })
    );
    
    // Force activation of new service worker
    self.skipWaiting();
});

// Activate event - clean up old caches
self.addEventListener('activate', (event) => {
    console.log('✅ Service Worker activated');
    
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames.map((cacheName) => {
                    if (cacheName !== STATIC_CACHE && cacheName !== DYNAMIC_CACHE) {
                        console.log('🗑️ Deleting old cache:', cacheName);
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
    
    // Take control of all pages
    self.clients.claim();
});

// Fetch event - serve cached files or fetch from network
self.addEventListener('fetch', (event) => {
    const { request } = event;
    const url = new URL(request.url);
    
    // Skip non-GET requests
    if (request.method !== 'GET') {
        return;
    }
    
    // Handle different types of requests
    if (url.origin === location.origin) {
        // Same origin - use cache first strategy
        event.respondWith(cacheFirst(request));
    } else if (url.hostname.includes('googleapis.com')) {
        // API requests - network first with cache fallback
        event.respondWith(networkFirst(request));
    } else if (url.hostname.includes('saavn') || url.hostname.includes('spotify') || url.hostname.includes('youtube')) {
        // Music service requests - network first
        event.respondWith(networkFirst(request));
    } else {
        // External resources - cache first
        event.respondWith(cacheFirst(request));
    }
});

// Cache first strategy - for static assets
async function cacheFirst(request) {
    try {
        const cachedResponse = await caches.match(request);
        if (cachedResponse) {
            return cachedResponse;
        }
        
        const networkResponse = await fetch(request);
        
        // Cache successful responses
        if (networkResponse.status === 200) {
            const cache = await caches.open(DYNAMIC_CACHE);
            cache.put(request, networkResponse.clone());
        }
        
        return networkResponse;
    } catch (error) {
        // Silently handle cache errors for demo
        
        // Return offline fallback for HTML requests
        if (request.destination === 'document') {
            return caches.match('/index.html');
        }
        
        // Return placeholder for images
        if (request.destination === 'image') {
            return new Response(
                '<svg xmlns="http://www.w3.org/2000/svg" width="300" height="300" viewBox="0 0 300 300"><rect width="300" height="300" fill="#667eea"/><text x="150" y="150" text-anchor="middle" dy=".3em" fill="white" font-size="24">♪</text></svg>',
                { headers: { 'Content-Type': 'image/svg+xml' } }
            );
        }
        
        throw error;
    }
}

// Network first strategy - for API requests
async function networkFirst(request) {
    try {
        const networkResponse = await fetch(request);
        
        // Cache successful API responses for short time
        if (networkResponse.status === 200) {
            const cache = await caches.open(DYNAMIC_CACHE);
            cache.put(request, networkResponse.clone());
        }
        
        return networkResponse;
    } catch (error) {
        console.log('🌐 Network failed, trying cache:', request.url);
        
        const cachedResponse = await caches.match(request);
        if (cachedResponse) {
            return cachedResponse;
        }
        
        // Return offline message for API failures
        if (request.url.includes('api') || request.url.includes('googleapis')) {
            return new Response(
                JSON.stringify({
                    error: 'Offline mode',
                    message: 'You are currently offline. Some features may not work.',
                    offline: true
                }),
                {
                    headers: { 'Content-Type': 'application/json' },
                    status: 503
                }
            );
        }
        
        throw error;
    }
}

// Background sync for offline actions
self.addEventListener('sync', (event) => {
    console.log('🔄 Background sync triggered:', event.tag);
    
    if (event.tag === 'sync-music-data') {
        event.waitUntil(syncMusicData());
    } else if (event.tag === 'sync-user-preferences') {
        event.waitUntil(syncUserPreferences());
    }
});

// Sync music data when back online
async function syncMusicData() {
    try {
        console.log('🎵 Syncing music data...');
        
        // Get pending sync data from IndexedDB
        const pendingData = await getPendingSync('music-data');
        
        for (const data of pendingData) {
            try {
                await fetch('/api/sync-music', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });
                
                // Remove from pending sync
                await removePendingSync('music-data', data.id);
            } catch (error) {
                console.error('❌ Failed to sync music data:', error);
            }
        }
        
        console.log('✅ Music data sync completed');
    } catch (error) {
        console.error('❌ Music data sync failed:', error);
    }
}

// Sync user preferences when back online
async function syncUserPreferences() {
    try {
        console.log('👤 Syncing user preferences...');
        
        const pendingPrefs = await getPendingSync('user-preferences');
        
        for (const pref of pendingPrefs) {
            try {
                await fetch('/api/sync-preferences', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(pref)
                });
                
                await removePendingSync('user-preferences', pref.id);
            } catch (error) {
                console.error('❌ Failed to sync preferences:', error);
            }
        }
        
        console.log('✅ User preferences sync completed');
    } catch (error) {
        console.error('❌ User preferences sync failed:', error);
    }
}

// Push notification handling
self.addEventListener('push', (event) => {
    console.log('📱 Push notification received');
    
    const options = {
        body: 'New music recommendations available!',
        icon: '/icon-192.png',
        badge: '/badge-72.png',
        vibrate: [200, 100, 200],
        data: {
            url: '/',
            action: 'open-app'
        },
        actions: [
            {
                action: 'open',
                title: 'Open FlyMusic AI',
                icon: '/icon-32.png'
            },
            {
                action: 'dismiss',
                title: 'Dismiss',
                icon: '/close-32.png'
            }
        ]
    };
    
    if (event.data) {
        const data = event.data.json();
        options.body = data.body || options.body;
        options.data = { ...options.data, ...data };
    }
    
    event.waitUntil(
        self.registration.showNotification('FlyMusic AI', options)
    );
});

// Notification click handling
self.addEventListener('notificationclick', (event) => {
    console.log('🔔 Notification clicked:', event.action);
    
    event.notification.close();
    
    if (event.action === 'open' || !event.action) {
        event.waitUntil(
            clients.matchAll({ type: 'window' }).then((clientList) => {
                // Focus existing window if available
                for (const client of clientList) {
                    if (client.url === '/' && 'focus' in client) {
                        return client.focus();
                    }
                }
                
                // Open new window
                if (clients.openWindow) {
                    return clients.openWindow('/');
                }
            })
        );
    }
});

// Message handling from main thread
self.addEventListener('message', (event) => {
    console.log('💬 Message received:', event.data);
    
    if (event.data && event.data.type === 'SKIP_WAITING') {
        self.skipWaiting();
    } else if (event.data && event.data.type === 'CACHE_MUSIC') {
        // Cache music for offline playback
        event.waitUntil(cacheMusicFile(event.data.url));
    }
});

// Cache music file for offline playback
async function cacheMusicFile(url) {
    try {
        const cache = await caches.open(DYNAMIC_CACHE);
        await cache.add(url);
        console.log('🎵 Music file cached:', url);
    } catch (error) {
        console.error('❌ Failed to cache music file:', error);
    }
}

// IndexedDB helpers for offline sync
async function getPendingSync(store) {
    // Mock implementation - replace with actual IndexedDB code
    return [];
}

async function removePendingSync(store, id) {
    // Mock implementation - replace with actual IndexedDB code
    console.log(`Removing ${id} from ${store}`);
}

// Periodic background sync for music updates
self.addEventListener('periodicsync', (event) => {
    if (event.tag === 'update-music-library') {
        event.waitUntil(updateMusicLibrary());
    }
});

async function updateMusicLibrary() {
    try {
        console.log('🔄 Updating music library in background...');
        
        // Fetch latest music data
        const response = await fetch('/api/music/trending');
        const musicData = await response.json();
        
        // Cache updated data
        const cache = await caches.open(DYNAMIC_CACHE);
        await cache.put('/api/music/trending', new Response(JSON.stringify(musicData)));
        
        console.log('✅ Music library updated');
    } catch (error) {
        console.error('❌ Failed to update music library:', error);
    }
}

console.log('🎵 FlyMusic AI Service Worker loaded successfully!');
