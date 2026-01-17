// Clean & Fresh Saathi Music App - Debug Free

class SaathiMusicApp {
    constructor() {
        this.currentSong = null;
        this.isPlaying = false;
        this.currentPlaylist = [];
        this.currentIndex = 0;
        this.audioPlayer = null;
        this.initialized = false;
        
        this.init();
    }
    
    init() {
        try {
            console.log('🎵 Initializing Saathi Music...');
            
            // Wait for DOM to be ready
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', () => this.setup());
            } else {
                this.setup();
            }
        } catch (error) {
            console.error('❌ App initialization error:', error);
        }
    }
    
    setup() {
        try {
            // Initialize audio player
            this.audioPlayer = document.getElementById('audioPlayer');
            if (!this.audioPlayer) {
                this.audioPlayer = document.createElement('audio');
                this.audioPlayer.id = 'audioPlayer';
                this.audioPlayer.preload = 'metadata';
                document.body.appendChild(this.audioPlayer);
            }
            
            // Setup event listeners
            this.setupEventListeners();
            
            // Initialize navigation
            this.initializeNavigation();
            
            // Load initial content
            this.loadHomeContent();
            
            // Show app container and hide splash
            this.showApp();
            
            this.initialized = true;
            console.log('✅ Saathi Music initialized successfully');
            
        } catch (error) {
            console.error('❌ Setup error:', error);
        }
    }
    
    setupEventListeners() {
        try {
            // Navigation
            const navItems = document.querySelectorAll('.nav-item');
            navItems.forEach(item => {
                item.addEventListener('click', (e) => {
                    e.preventDefault();
                    const section = item.dataset.section;
                    if (section) {
                        this.showSection(section);
                    }
                });
            });
            
            // Search
            const searchInput = document.getElementById('searchInput');
            if (searchInput) {
                searchInput.addEventListener('input', (e) => {
                    this.handleSearch(e.target.value);
                });
            }
            
            // Player controls
            this.setupPlayerControls();
            
            // Mobile menu
            const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
            if (mobileMenuBtn) {
                mobileMenuBtn.addEventListener('click', () => {
                    this.toggleMobileMenu();
                });
            }
            
        } catch (error) {
            console.error('❌ Event listener setup error:', error);
        }
    }
    
    setupPlayerControls() {
        try {
            const playPauseBtn = document.getElementById('playPauseBtn');
            const prevBtn = document.getElementById('prevBtn');
            const nextBtn = document.getElementById('nextBtn');
            
            if (playPauseBtn) {
                playPauseBtn.addEventListener('click', () => this.togglePlayPause());
            }
            
            if (prevBtn) {
                prevBtn.addEventListener('click', () => this.previousSong());
            }
            
            if (nextBtn) {
                nextBtn.addEventListener('click', () => this.nextSong());
            }
            
            // Audio player events
            if (this.audioPlayer) {
                this.audioPlayer.addEventListener('loadedmetadata', () => {
                    this.updatePlayerUI();
                });
                
                this.audioPlayer.addEventListener('timeupdate', () => {
                    this.updateProgress();
                });
                
                this.audioPlayer.addEventListener('ended', () => {
                    this.nextSong();
                });
                
                this.audioPlayer.addEventListener('error', (e) => {
                    console.warn('⚠️ Audio error:', e);
                    this.showToast('Audio playback error', 'error');
                });
            }
            
        } catch (error) {
            console.error('❌ Player controls setup error:', error);
        }
    }
    
    initializeNavigation() {
        try {
            // Set home as active by default
            this.showSection('home');
        } catch (error) {
            console.error('❌ Navigation initialization error:', error);
        }
    }
    
    showSection(sectionName) {
        try {
            // Hide all sections
            const sections = document.querySelectorAll('.content-section');
            sections.forEach(section => {
                section.classList.remove('active');
            });
            
            // Show target section
            const targetSection = document.getElementById(`${sectionName}-section`);
            if (targetSection) {
                targetSection.classList.add('active');
            }
            
            // Update navigation
            this.updateNavigation(sectionName);
            
            // Load section content
            this.loadSectionContent(sectionName);
            
            console.log(`📍 Navigated to: ${sectionName}`);
            
        } catch (error) {
            console.error('❌ Section navigation error:', error);
        }
    }
    
    updateNavigation(activeSection) {
        try {
            const navItems = document.querySelectorAll('.nav-item');
            navItems.forEach(item => {
                item.classList.remove('active');
                if (item.dataset.section === activeSection) {
                    item.classList.add('active');
                }
            });
        } catch (error) {
            console.error('❌ Navigation update error:', error);
        }
    }
    
    loadSectionContent(section) {
        try {
            switch(section) {
                case 'home':
                    this.loadHomeContent();
                    break;
                case 'search':
                    this.loadSearchContent();
                    break;
                case 'library':
                    this.loadLibraryContent();
                    break;
                case 'playlists':
                    this.loadPlaylistsContent();
                    break;
                case 'favorites':
                    this.loadFavoritesContent();
                    break;
                case 'recent':
                    this.loadRecentContent();
                    break;
            }
        } catch (error) {
            console.error('❌ Content loading error:', error);
        }
    }
    
    loadHomeContent() {
        try {
            this.loadMusicCategories();
            this.loadTrendingSongs();
            this.loadPopularArtists();
        } catch (error) {
            console.error('❌ Home content loading error:', error);
        }
    }
    
    loadMusicCategories() {
        try {
            const container = document.getElementById('categoriesContainer');
            if (!container) return;
            
            const categories = [
                { id: 'bollywood', name: 'Bollywood', icon: '🎬', color: '#e74c3c' },
                { id: 'punjabi', name: 'Punjabi', icon: '🎵', color: '#f39c12' },
                { id: 'romantic', name: 'Romantic', icon: '💕', color: '#e91e63' },
                { id: 'party', name: 'Party', icon: '🎉', color: '#9c27b0' },
                { id: 'devotional', name: 'Devotional', icon: '🙏', color: '#ff9800' },
                { id: 'international', name: 'International', icon: '🌍', color: '#2196f3' }
            ];
            
            container.innerHTML = categories.map(category => `
                <div class="category-card" onclick="app.loadCategoryMusic('${category.id}')">
                    <div class="category-icon" style="font-size: 2rem; margin-bottom: 10px;">${category.icon}</div>
                    <h3>${category.name}</h3>
                </div>
            `).join('');
            
        } catch (error) {
            console.error('❌ Categories loading error:', error);
        }
    }
    
    loadTrendingSongs() {
        try {
            const container = document.getElementById('trendingGrid');
            if (!container) return;
            
            const trendingSongs = [
                { 
                    id: 'trend_1', 
                    title: 'Tere Ishk Mein', 
                    artist: 'A.R. Rahman, Arijit Singh', 
                    duration: '4:32',
                    thumbnail: this.createPlaceholderImage('Tere Ishk Mein', '#1DB954'),
                    audioUrl: 'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3'
                },
                { 
                    id: 'trend_2', 
                    title: 'Deewaniyat', 
                    artist: 'Vishal Mishra', 
                    duration: '3:45',
                    thumbnail: this.createPlaceholderImage('Deewaniyat', '#e74c3c'),
                    audioUrl: 'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3'
                },
                { 
                    id: 'trend_3', 
                    title: 'Nafrat', 
                    artist: 'Darshan Raval', 
                    duration: '3:52',
                    thumbnail: this.createPlaceholderImage('Nafrat', '#9c27b0'),
                    audioUrl: 'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3'
                }
            ];
            
            container.innerHTML = trendingSongs.map((song, index) => `
                <div class="trending-card" onclick="app.playSong(${JSON.stringify(song).replace(/"/g, '&quot;')}, ${index})">
                    <img src="${song.thumbnail}" alt="${song.title}" style="width: 100%; height: 200px; object-fit: cover; border-radius: 8px; margin-bottom: 12px;">
                    <h4 style="margin-bottom: 4px; font-size: 1rem;">${song.title}</h4>
                    <p style="color: #b3b3b3; font-size: 0.9rem; margin-bottom: 8px;">${song.artist}</p>
                    <span style="color: #1DB954; font-size: 0.8rem;">${song.duration}</span>
                    <div class="play-btn-overlay" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: #1DB954; color: white; border: none; border-radius: 50%; width: 50px; height: 50px; display: none; align-items: center; justify-content: center; cursor: pointer;">
                        <i class="fas fa-play"></i>
                    </div>
                </div>
            `).join('');
            
        } catch (error) {
            console.error('❌ Trending songs loading error:', error);
        }
    }
    
    loadPopularArtists() {
        try {
            const container = document.getElementById('artistsGrid');
            if (!container) return;
            
            const artists = [
                { id: 'artist_1', name: 'Arijit Singh', followers: '45.2M' },
                { id: 'artist_2', name: 'A.R. Rahman', followers: '12.8M' },
                { id: 'artist_3', name: 'Shreya Ghoshal', followers: '8.5M' },
                { id: 'artist_4', name: 'Darshan Raval', followers: '6.2M' }
            ];
            
            container.innerHTML = artists.map(artist => `
                <div class="artist-card" onclick="app.loadArtistDetails('${artist.id}')">
                    <img src="${this.createPlaceholderImage(artist.name, '#1DB954')}" alt="${artist.name}" style="width: 100%; height: 180px; object-fit: cover; border-radius: 50%; margin-bottom: 12px;">
                    <h4 style="text-align: center; margin-bottom: 4px;">${artist.name}</h4>
                    <p style="text-align: center; color: #b3b3b3; font-size: 0.9rem;">${artist.followers} followers</p>
                </div>
            `).join('');
            
        } catch (error) {
            console.error('❌ Artists loading error:', error);
        }
    }
    
    loadSearchContent() {
        try {
            const searchInput = document.getElementById('searchInput');
            if (searchInput) {
                searchInput.focus();
            }
        } catch (error) {
            console.error('❌ Search content loading error:', error);
        }
    }
    
    loadLibraryContent() {
        try {
            const container = document.getElementById('libraryContent');
            if (!container) return;
            
            container.innerHTML = `
                <div class="library-placeholder">
                    <i class="fas fa-music"></i>
                    <h3>Your Music Library</h3>
                    <p>Your saved songs and albums will appear here</p>
                    <button class="primary-btn" onclick="app.showSection('search')">
                        <i class="fas fa-search"></i> Start Exploring
                    </button>
                </div>
            `;
        } catch (error) {
            console.error('❌ Library content loading error:', error);
        }
    }
    
    loadPlaylistsContent() {
        try {
            const container = document.getElementById('playlistsContent');
            if (!container) return;
            
            container.innerHTML = `
                <div class="playlists-placeholder">
                    <i class="fas fa-list"></i>
                    <h3>No Playlists Yet</h3>
                    <p>Create your first playlist to organize your favorite songs</p>
                    <button class="primary-btn" onclick="app.createPlaylist()">
                        <i class="fas fa-plus"></i> Create Playlist
                    </button>
                </div>
            `;
        } catch (error) {
            console.error('❌ Playlists content loading error:', error);
        }
    }
    
    loadFavoritesContent() {
        try {
            const container = document.getElementById('favoritesContent');
            if (!container) return;
            
            container.innerHTML = `
                <div class="favorites-placeholder">
                    <i class="fas fa-heart"></i>
                    <h3>No Favorites Yet</h3>
                    <p>Songs you like will appear here</p>
                    <button class="primary-btn" onclick="app.showSection('search')">
                        <i class="fas fa-search"></i> Discover Music
                    </button>
                </div>
            `;
        } catch (error) {
            console.error('❌ Favorites content loading error:', error);
        }
    }
    
    loadRecentContent() {
        try {
            const container = document.getElementById('recentContent');
            if (!container) return;
            
            container.innerHTML = `
                <div class="recent-placeholder">
                    <i class="fas fa-clock"></i>
                    <h3>No Recent Activity</h3>
                    <p>Songs you play will appear here</p>
                    <button class="primary-btn" onclick="app.showSection('search')">
                        <i class="fas fa-play"></i> Start Listening
                    </button>
                </div>
            `;
        } catch (error) {
            console.error('❌ Recent content loading error:', error);
        }
    }
    
    playSong(song, index = 0) {
        try {
            console.log('🎵 Playing song:', song.title);
            
            this.currentSong = song;
            this.currentIndex = index;
            
            if (this.audioPlayer && song.audioUrl) {
                this.audioPlayer.src = song.audioUrl;
                this.audioPlayer.load();
                
                const playPromise = this.audioPlayer.play();
                if (playPromise !== undefined) {
                    playPromise.then(() => {
                        this.isPlaying = true;
                        this.updatePlayerUI();
                        this.showToast(`Now playing: ${song.title}`, 'success');
                    }).catch(error => {
                        console.warn('⚠️ Playback error:', error);
                        this.showToast('Playback error - trying alternative', 'warning');
                    });
                }
            }
            
        } catch (error) {
            console.error('❌ Play song error:', error);
            this.showToast('Unable to play song', 'error');
        }
    }
    
    togglePlayPause() {
        try {
            if (!this.audioPlayer) return;
            
            if (this.isPlaying) {
                this.audioPlayer.pause();
                this.isPlaying = false;
            } else {
                const playPromise = this.audioPlayer.play();
                if (playPromise !== undefined) {
                    playPromise.then(() => {
                        this.isPlaying = true;
                    }).catch(error => {
                        console.warn('⚠️ Play error:', error);
                    });
                }
            }
            
            this.updatePlayerUI();
            
        } catch (error) {
            console.error('❌ Toggle play/pause error:', error);
        }
    }
    
    nextSong() {
        try {
            if (this.currentPlaylist.length > 0) {
                this.currentIndex = (this.currentIndex + 1) % this.currentPlaylist.length;
                this.playSong(this.currentPlaylist[this.currentIndex], this.currentIndex);
            }
        } catch (error) {
            console.error('❌ Next song error:', error);
        }
    }
    
    previousSong() {
        try {
            if (this.currentPlaylist.length > 0) {
                this.currentIndex = this.currentIndex > 0 ? this.currentIndex - 1 : this.currentPlaylist.length - 1;
                this.playSong(this.currentPlaylist[this.currentIndex], this.currentIndex);
            }
        } catch (error) {
            console.error('❌ Previous song error:', error);
        }
    }
    
    updatePlayerUI() {
        try {
            const titleElement = document.getElementById('currentSongTitle');
            const artistElement = document.getElementById('currentSongArtist');
            const imageElement = document.getElementById('currentSongImage');
            const playPauseBtn = document.getElementById('playPauseBtn');
            
            if (this.currentSong) {
                if (titleElement) titleElement.textContent = this.currentSong.title || 'Unknown';
                if (artistElement) artistElement.textContent = this.currentSong.artist || 'Unknown Artist';
                if (imageElement) imageElement.src = this.currentSong.thumbnail || this.createPlaceholderImage('♪', '#1DB954', 50, 50);
            }
            
            if (playPauseBtn) {
                const icon = playPauseBtn.querySelector('i');
                if (icon) {
                    icon.className = this.isPlaying ? 'fas fa-pause' : 'fas fa-play';
                }
            }
            
        } catch (error) {
            console.error('❌ Player UI update error:', error);
        }
    }
    
    updateProgress() {
        try {
            if (!this.audioPlayer) return;
            
            const currentTime = this.audioPlayer.currentTime;
            const duration = this.audioPlayer.duration;
            
            if (isNaN(duration)) return;
            
            const progress = (currentTime / duration) * 100;
            
            const progressFill = document.getElementById('progressFill');
            const currentTimeElement = document.getElementById('currentTime');
            const totalTimeElement = document.getElementById('totalTime');
            
            if (progressFill) {
                progressFill.style.width = `${progress}%`;
            }
            
            if (currentTimeElement) {
                currentTimeElement.textContent = this.formatTime(currentTime);
            }
            
            if (totalTimeElement) {
                totalTimeElement.textContent = this.formatTime(duration);
            }
            
        } catch (error) {
            console.error('❌ Progress update error:', error);
        }
    }
    
    formatTime(seconds) {
        try {
            if (isNaN(seconds)) return '0:00';
            
            const minutes = Math.floor(seconds / 60);
            const secs = Math.floor(seconds % 60);
            return `${minutes}:${secs.toString().padStart(2, '0')}`;
        } catch (error) {
            return '0:00';
        }
    }
    
    handleSearch(query) {
        try {
            if (query.length < 2) return;
            
            console.log('🔍 Searching for:', query);
            // Implement search functionality here
            
        } catch (error) {
            console.error('❌ Search error:', error);
        }
    }
    
    toggleMobileMenu() {
        try {
            const sidebar = document.querySelector('.sidebar');
            if (sidebar) {
                sidebar.classList.toggle('open');
            }
        } catch (error) {
            console.error('❌ Mobile menu toggle error:', error);
        }
    }
    
    showApp() {
        try {
            const splashScreen = document.getElementById('splash-screen');
            const authContainer = document.getElementById('auth-container');
            const appContainer = document.getElementById('app-container');
            
            if (splashScreen) {
                splashScreen.style.display = 'none';
            }
            
            if (authContainer) {
                authContainer.style.display = 'none';
            }
            
            if (appContainer) {
                appContainer.classList.remove('hidden');
                appContainer.style.display = 'flex';
            }
            
        } catch (error) {
            console.error('❌ Show app error:', error);
        }
    }
    
    showToast(message, type = 'info') {
        try {
            // Create toast element
            const toast = document.createElement('div');
            toast.className = `toast toast-${type}`;
            toast.innerHTML = `
                <div class="toast-content">
                    <i class="fas fa-${this.getToastIcon(type)}"></i>
                    <span>${message}</span>
                </div>
            `;
            
            // Style the toast
            toast.style.cssText = `
                position: fixed;
                top: 80px;
                right: 20px;
                background: var(--surface-color);
                border: 1px solid var(--border-color);
                border-radius: var(--radius-lg);
                padding: var(--spacing-md);
                color: var(--text-primary);
                z-index: 10001;
                animation: slideIn 0.3s ease;
                max-width: 300px;
                box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
            `;
            
            // Add to page
            document.body.appendChild(toast);
            
            // Auto remove after 3 seconds
            setTimeout(() => {
                if (toast.parentElement) {
                    toast.style.animation = 'slideOut 0.3s ease';
                    setTimeout(() => {
                        if (toast.parentElement) {
                            toast.remove();
                        }
                    }, 300);
                }
            }, 3000);
            
        } catch (error) {
            console.error('❌ Toast error:', error);
        }
    }
    
    getToastIcon(type) {
        const icons = {
            'success': 'check-circle',
            'error': 'exclamation-circle',
            'warning': 'exclamation-triangle',
            'info': 'info-circle'
        };
        return icons[type] || 'info-circle';
    }
    
    // Create local placeholder image using canvas
    createPlaceholderImage(text, color = '#1DB954', width = 300, height = 300) {
        try {
            const canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;
            const ctx = canvas.getContext('2d');
            
            // Background
            ctx.fillStyle = color;
            ctx.fillRect(0, 0, width, height);
            
            // Text
            ctx.fillStyle = '#ffffff';
            ctx.font = 'bold 24px Poppins, Arial, sans-serif';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            
            // Wrap text if too long
            const maxWidth = width - 40;
            const words = text.split(' ');
            let line = '';
            const lines = [];
            
            for (let n = 0; n < words.length; n++) {
                const testLine = line + words[n] + ' ';
                const metrics = ctx.measureText(testLine);
                const testWidth = metrics.width;
                if (testWidth > maxWidth && n > 0) {
                    lines.push(line);
                    line = words[n] + ' ';
                } else {
                    line = testLine;
                }
            }
            lines.push(line);
            
            // Draw lines
            const lineHeight = 30;
            const startY = height / 2 - (lines.length - 1) * lineHeight / 2;
            
            lines.forEach((line, index) => {
                ctx.fillText(line.trim(), width / 2, startY + index * lineHeight);
            });
            
            return canvas.toDataURL();
        } catch (error) {
            console.error('❌ Placeholder image creation error:', error);
            // Fallback to a simple data URL
            return `data:image/svg+xml;base64,${btoa(`
                <svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}">
                    <rect width="100%" height="100%" fill="${color}"/>
                    <text x="50%" y="50%" font-family="Arial" font-size="24" fill="white" text-anchor="middle" dy="0.3em">${text}</text>
                </svg>
            `)}`;
        }
    }
    
    // Public methods for global access
    loadCategoryMusic(categoryId) {
        try {
            console.log('📂 Loading category:', categoryId);
            this.showToast(`Loading ${categoryId} music...`, 'info');
        } catch (error) {
            console.error('❌ Category loading error:', error);
        }
    }
    
    loadArtistDetails(artistId) {
        try {
            console.log('👤 Loading artist:', artistId);
            this.showToast('Loading artist details...', 'info');
        } catch (error) {
            console.error('❌ Artist loading error:', error);
        }
    }
    
    createPlaylist() {
        try {
            const playlistName = prompt('Enter playlist name:');
            if (playlistName && playlistName.trim()) {
                this.showToast(`Playlist "${playlistName}" created!`, 'success');
            }
        } catch (error) {
            console.error('❌ Create playlist error:', error);
        }
    }
}

// Initialize app
let app;

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    
    .trending-card:hover .play-btn-overlay {
        display: flex !important;
    }
`;
document.head.appendChild(style);

// Start the app
document.addEventListener('DOMContentLoaded', () => {
    app = new SaathiMusicApp();
});

// Global functions for HTML onclick handlers
window.app = null;

// Set global app reference after initialization
setTimeout(() => {
    if (typeof app !== 'undefined') {
        window.app = app;
    }
}, 1000);
