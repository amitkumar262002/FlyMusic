// Main Application Logic
class FlyMusicApp {
    constructor() {
        this.currentSection = 'home';
        this.searchTimeout = null;
        this.isLoading = false;
        
        this.initializeApp();
    }

    async initializeApp() {
        // Show loading screen
        this.showLoadingScreen();
        
        // Wait for Firebase to initialize
        await this.waitForFirebase();
        
        // Initialize components
        this.setupEventListeners();
        this.setupNavigation();
        this.setupSearch();
        
        // Load initial content
        await this.loadInitialContent();
        
        // Hide loading screen
        this.hideLoadingScreen();
    }

    async waitForFirebase() {
        return new Promise((resolve) => {
            const checkFirebase = () => {
                if (window.firebaseServices && window.authManager) {
                    resolve();
                } else {
                    setTimeout(checkFirebase, 100);
                }
            };
            checkFirebase();
        });
    }

    showLoadingScreen() {
        const splashScreen = document.getElementById('splash-screen');
        if (splashScreen) {
            splashScreen.classList.remove('hidden');
            // Start progress animation
            this.animateProgress();
        }
    }

    hideLoadingScreen() {
        const splashScreen = document.getElementById('splash-screen');
        if (splashScreen) {
            setTimeout(() => {
                splashScreen.style.opacity = '0';
                splashScreen.style.transform = 'scale(1.1)';
                splashScreen.style.transition = 'all 0.8s ease-out';
                
                setTimeout(() => {
                    splashScreen.classList.add('hidden');
                }, 800);
            }, 3000); // Show for 3 seconds for branding
        }
    }

    animateProgress() {
        const progressFill = document.querySelector('.progress-fill-splash');
        const loadingText = document.querySelector('.loading-text');
        
        if (progressFill && loadingText) {
            const messages = [
                'Initializing Saathi Music...',
                'Loading music libraries...',
                'Connecting to services...',
                'Preparing your musical journey...'
            ];
            
            let messageIndex = 0;
            const messageInterval = setInterval(() => {
                if (messageIndex < messages.length) {
                    loadingText.textContent = messages[messageIndex];
                    messageIndex++;
                } else {
                    clearInterval(messageInterval);
                }
            }, 750);
        }
    }

    setupEventListeners() {
        // Window events
        window.addEventListener('resize', () => this.handleResize());
        
        // Profile and settings
        window.showProfile = () => this.showProfile();
        window.showSettings = () => this.showSettings();
        window.createPlaylist = () => this.createPlaylist();
    }

    setupNavigation() {
        const navItems = document.querySelectorAll('.nav-item');
        navItems.forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const section = item.dataset.section;
                if (section) {
                    this.navigateToSection(section);
                }
            });
        });
    }

    setupSearch() {
        const searchInput = document.getElementById('searchInput');
        const searchSuggestions = document.getElementById('searchSuggestions');

        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                const query = e.target.value.trim();
                
                // Clear previous timeout
                if (this.searchTimeout) {
                    clearTimeout(this.searchTimeout);
                }

                if (query.length > 2) {
                    // Debounce search
                    this.searchTimeout = setTimeout(() => {
                        this.performSearch(query);
                        this.showSearchSuggestions(query);
                    }, 300);
                } else {
                    this.hideSearchSuggestions();
                }
            });

            searchInput.addEventListener('keydown', (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    const query = searchInput.value.trim();
                    if (query) {
                        this.performSearch(query);
                        this.navigateToSection('search');
                    }
                }
            });

            // Hide suggestions when clicking outside
            document.addEventListener('click', (e) => {
                if (!searchInput.contains(e.target) && !searchSuggestions.contains(e.target)) {
                    this.hideSearchSuggestions();
                }
            });
        }
    }

    async loadInitialContent() {
        try {
            // Load content for home section
            await this.loadHomeContent();
            // Load Instagram-like feed
            await this.loadMusicFeed();
            // Setup view toggle
            this.setupViewToggle();
        } catch (error) {
            console.error('Error loading initial content:', error);
        }
    }

    async loadHomeContent() {
        try {
            // Wait for multi-platform API to be ready
            if (window.multiPlatformAPI) {
                await this.loadMultiPlatformContent();
            } else {
                // Fallback to sample data
                const musicData = window.sampleMusicData || [];
                this.createMusicFeed(musicData);
                this.loadTrendingMusic();
            }
            
            const bollywoodMusic = await window.musicAPI.getBollywoodHits();
            this.renderMusicGrid('bollywoodMusic', bollywoodMusic);

            // Load international music
            const internationalMusic = await window.musicAPI.getInternationalMusic();
            this.renderMusicGrid('internationalMusic', internationalMusic);

        } catch (error) {
            console.error('Error loading home content:', error);
            this.showToast('Error loading music content', 'error');
        }
    }

    async loadMusicFeed() {
        try {
            // Combine all music for feed
            const trendingMusic = await window.musicAPI.getTrendingMusic();
            const bollywoodMusic = await window.musicAPI.getBollywoodHits();
            const internationalMusic = await window.musicAPI.getInternationalMusic();
            
            const allMusic = [...trendingMusic, ...bollywoodMusic, ...internationalMusic];
            
            // Shuffle for variety
            const shuffledMusic = this.shuffleArray(allMusic);
            
            this.renderMusicFeed(shuffledMusic.slice(0, 20));
        } catch (error) {
            console.error('Error loading music feed:', error);
        }
    }

    renderMusicFeed(songs) {
        const feedContainer = document.getElementById('musicFeedContainer');
        if (!feedContainer || !songs.length) return;

        feedContainer.innerHTML = '';

        songs.forEach((song, index) => {
            const feedCard = this.createFeedCard(song, index);
            feedContainer.appendChild(feedCard);
        });
    }

    createFeedCard(song, index) {
        const card = document.createElement('div');
        card.className = 'feed-card';
        
        const artistInitial = song.artist ? song.artist.charAt(0).toUpperCase() : 'M';
        const sourceLabel = song.source === 'youtube' ? 'YouTube Music' : 'JioSaavn';
        
        card.innerHTML = `
            <div class="feed-header">
                <div class="feed-avatar">${artistInitial}</div>
                <div class="feed-info">
                    <h4>${song.artist || 'Unknown Artist'}</h4>
                    <p>${sourceLabel} • ${this.getTimeAgo()}</p>
                </div>
            </div>
            
            <div class="feed-image-container">
                <img src="${song.thumbnail || 'https://via.placeholder.com/500x400?text=♪'}" 
                     alt="${song.title}" 
                     class="feed-image"
                     onerror="this.src='https://via.placeholder.com/500x400?text=♪'">
                
                <div class="feed-overlay">
                    <div class="feed-song-info">
                        <h3>${this.truncateText(song.title, 40)}</h3>
                        <p>${song.album || song.artist}</p>
                    </div>
                </div>
                
                <button class="feed-play-btn" onclick="app.playFeedSong(${JSON.stringify(song).replace(/"/g, '&quot;')}, ${index})">
                    <i class="fas fa-play"></i>
                </button>
            </div>
            
            <div class="feed-actions">
                <div class="feed-actions-left">
                    <button class="feed-action-btn" onclick="app.toggleLike('${song.id}')" title="Like">
                        <i class="fas fa-heart"></i>
                    </button>
                    <button class="feed-action-btn" onclick="app.shareSong('${song.id}')" title="Share">
                        <i class="fas fa-share"></i>
                    </button>
                    <button class="feed-action-btn" onclick="app.addToPlaylist('${song.id}')" title="Add to Playlist">
                        <i class="fas fa-plus"></i>
                    </button>
                </div>
                <div class="feed-duration">${song.duration || '3:45'}</div>
            </div>
        `;

        return card;
    }

    setupViewToggle() {
        const feedViewBtn = document.getElementById('feedViewBtn');
        const gridViewBtn = document.getElementById('gridViewBtn');
        const musicFeed = document.querySelector('.music-feed');
        const traditionalView = document.querySelector('.traditional-view');

        if (feedViewBtn && gridViewBtn) {
            feedViewBtn.addEventListener('click', () => {
                feedViewBtn.classList.add('active');
                gridViewBtn.classList.remove('active');
                musicFeed.classList.remove('hidden');
                traditionalView.classList.add('hidden');
            });

            gridViewBtn.addEventListener('click', () => {
                gridViewBtn.classList.add('active');
                feedViewBtn.classList.remove('active');
                musicFeed.classList.add('hidden');
                traditionalView.classList.remove('hidden');
            });
        }
    }

    playFeedSong(song, index) {
        if (window.musicPlayer) {
            // Create playlist from current feed
            const feedCards = document.querySelectorAll('.feed-card');
            const feedPlaylist = Array.from(feedCards).map((card, i) => {
                // Extract song data from card (simplified)
                return song; // In real implementation, extract from data attributes
            });
            
            window.musicPlayer.playSong(song, feedPlaylist, index);
        }
    }

    toggleLike(songId) {
        const likeBtn = event.target.closest('.feed-action-btn');
        const icon = likeBtn.querySelector('i');
        
        if (likeBtn.classList.contains('liked')) {
            likeBtn.classList.remove('liked');
            icon.className = 'fas fa-heart';
            this.showToast('Removed from favorites', 'info');
        } else {
            likeBtn.classList.add('liked');
            icon.className = 'fas fa-heart';
            this.showToast('Added to favorites!', 'success');
        }
    }

    shuffleArray(array) {
        const shuffled = [...array];
        for (let i = shuffled.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
        }
        return shuffled;
    }

    getTimeAgo() {
        const timeOptions = ['2m ago', '5m ago', '1h ago', '3h ago', '1d ago', '2d ago'];
        return timeOptions[Math.floor(Math.random() * timeOptions.length)];
    }

    renderMusicGrid(containerId, songs) {
        const container = document.getElementById(containerId);
        if (!container || !songs.length) return;

        container.innerHTML = '';

        songs.forEach((song, index) => {
            const musicCard = this.createMusicCard(song, index);
            container.appendChild(musicCard);
        });
    }

    createMusicCard(song, index) {
        const card = document.createElement('div');
        card.className = 'music-card';
        card.innerHTML = `
            <div style="position: relative;">
                <img src="${song.thumbnail || 'https://via.placeholder.com/200x200?text=♪'}" 
                     alt="${song.title}" 
                     onerror="this.src='https://via.placeholder.com/200x200?text=♪'">
                <button class="play-btn-overlay" onclick="app.playSong(${JSON.stringify(song).replace(/"/g, '&quot;')}, ${index})">
                    <i class="fas fa-play"></i>
                </button>
            </div>
            <h3>${this.truncateText(song.title, 30)}</h3>
            <p>${this.truncateText(song.artist, 25)}</p>
            <div class="card-actions">
                <button onclick="app.addToFavorites('${song.id}')" title="Add to Favorites">
                    <i class="fas fa-heart"></i>
                </button>
                <button onclick="app.addToPlaylist('${song.id}')" title="Add to Playlist">
                    <i class="fas fa-plus"></i>
                </button>
                <button onclick="app.shareSong('${song.id}')" title="Share">
                    <i class="fas fa-share"></i>
                </button>
            </div>
        `;

        return card;
    }

    async performSearch(query) {
        try {
            this.setLoading(true);
            
            let results = [];
            
            // Use multi-platform API if available
            if (window.multiPlatformAPI) {
                const multiResults = await window.multiPlatformAPI.searchAllPlatforms(query);
                results = multiResults.tracks || [];
                
                if (results.length > 0) {
                    this.showToast(`Found ${results.length} songs across ${multiResults.platforms.join(', ')}`, 'success');
                }
            } else {
                // Fallback to original API
                results = await window.musicAPI.searchMusic(query);
            }
            
            this.renderSearchResults(results);
            
        } catch (error) {
            console.error('Search error:', error);
            this.showToast('Search failed. Please try again.', 'error');
        } finally {
            this.setLoading(false);
        }
    }

    renderSearchResults(results) {
        const searchResults = document.getElementById('searchResults');
        if (!searchResults) return;

        if (results.length === 0) {
            searchResults.innerHTML = `
                <div class="search-placeholder">
                    <i class="fas fa-search"></i>
                    <h3>No results found</h3>
                    <p>Try searching with different keywords</p>
                </div>
            `;
            return;
        }

        searchResults.innerHTML = `
            <div class="search-results-header">
                <h2>Search Results (${results.length})</h2>
                <button onclick="app.playAllSearchResults()" class="play-all-btn">
                    <i class="fas fa-play"></i> Play All
                </button>
            </div>
            <div class="music-grid" id="searchResultsGrid"></div>
        `;

        this.renderMusicGrid('searchResultsGrid', results);
        this.currentSearchResults = results;
    }

    showSearchSuggestions(query) {
        const suggestions = document.getElementById('searchSuggestions');
        if (!suggestions) return;

        // Sample suggestions - in a real app, these would come from an API
        const sampleSuggestions = [
            `${query} songs`,
            `${query} artist`,
            `${query} album`,
            `Best of ${query}`,
            `${query} hits`
        ];

        suggestions.innerHTML = '';
        sampleSuggestions.forEach(suggestion => {
            const item = document.createElement('div');
            item.className = 'suggestion-item';
            item.textContent = suggestion;
            item.addEventListener('click', () => {
                document.getElementById('searchInput').value = suggestion;
                this.performSearch(suggestion);
                this.navigateToSection('search');
                this.hideSearchSuggestions();
            });
            suggestions.appendChild(item);
        });

        suggestions.style.display = 'block';
    }

    hideSearchSuggestions() {
        const suggestions = document.getElementById('searchSuggestions');
        if (suggestions) {
            suggestions.style.display = 'none';
        }
    }

    navigateToSection(sectionName) {
        // Update navigation
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });

        const activeNavItem = document.querySelector(`[data-section="${sectionName}"]`);
        if (activeNavItem) {
            activeNavItem.classList.add('active');
        }

        // Show/hide sections
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });

        const targetSection = document.getElementById(`${sectionName}-section`);
        if (targetSection) {
            targetSection.classList.add('active');
        }

        this.currentSection = sectionName;

        // Load section-specific content
        this.loadSectionContent(sectionName);
    }

    async loadSectionContent(sectionName) {
        switch (sectionName) {
            case 'library':
                await this.loadLibraryContent();
                break;
            case 'playlists':
                await this.loadPlaylistsContent();
                break;
            case 'favorites':
                await this.loadFavoritesContent();
                break;
            case 'recent':
                await this.loadRecentContent();
                break;
        }
    }

    async loadLibraryContent() {
        const libraryContent = document.getElementById('libraryContent');
        if (libraryContent) {
            libraryContent.innerHTML = `
                <div class="library-placeholder">
                    <i class="fas fa-music"></i>
                    <h3>Your Music Library</h3>
                    <p>Your saved songs and albums will appear here</p>
                </div>
            `;
        }
    }

    async loadPlaylistsContent() {
        // Implementation for loading playlists
        console.log('Loading playlists content');
    }

    async loadFavoritesContent() {
        // Implementation for loading favorites
        console.log('Loading favorites content');
    }

    async loadRecentContent() {
        // Implementation for loading recent content
        console.log('Loading recent content');
    }

    // Music player integration
    playSong(song, index = 0) {
        if (window.musicPlayer) {
            // If we have search results, use them as playlist
            const playlist = this.currentSearchResults || [song];
            window.musicPlayer.playSong(song, playlist, index);
        }
    }

    playAllSearchResults() {
        if (this.currentSearchResults && this.currentSearchResults.length > 0) {
            window.musicPlayer.setPlaylist(this.currentSearchResults);
            window.musicPlayer.playSong(this.currentSearchResults[0], this.currentSearchResults, 0);
        }
    }

    addToFavorites(songId) {
        // Implementation for adding to favorites
        this.showToast('Added to favorites!', 'success');
    }

    addToPlaylist(songId) {
        // Implementation for adding to playlist
        this.showToast('Added to playlist!', 'success');
    }

    shareSong(songId) {
        // Implementation for sharing
        if (navigator.share) {
            navigator.share({
                title: 'Check out this song on FlyMusic AI',
                url: window.location.href
            });
        } else {
            // Fallback - copy to clipboard
            navigator.clipboard.writeText(window.location.href);
            this.showToast('Link copied to clipboard!', 'success');
        }
    }

    createPlaylist() {
        const playlistName = prompt('Enter playlist name:');
        if (playlistName && playlistName.trim()) {
            // Implementation for creating playlist
            this.showToast(`Playlist "${playlistName}" created!`, 'success');
        }
    }

    showProfile() {
        // Create and show profile modal
        const profileModal = this.createProfileModal();
        document.body.appendChild(profileModal);
        setTimeout(() => profileModal.classList.add('show'), 10);
    }

    showSettings() {
        // Create and show settings modal
        const settingsModal = this.createSettingsModal();
        document.body.appendChild(settingsModal);
        setTimeout(() => settingsModal.classList.add('show'), 10);
    }

    // Utility methods
    truncateText(text, maxLength) {
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    }

    setLoading(isLoading) {
        this.isLoading = isLoading;
        // Update UI to show loading state
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.disabled = isLoading;
            if (isLoading) {
                searchInput.placeholder = 'Searching...';
            } else {
                searchInput.placeholder = 'Search for songs, artists, albums...';
            }
        }
    }

    handleResize() {
        // Handle responsive behavior
        const sidebar = document.querySelector('.sidebar');
        const isMobile = window.innerWidth <= 768;
        
        if (isMobile && sidebar) {
            sidebar.classList.remove('open');
        }
        
        // Adjust feed layout for mobile
        this.adjustFeedLayout();
    }

    adjustFeedLayout() {
        const feedContainer = document.getElementById('musicFeedContainer');
        const isMobile = window.innerWidth <= 480;
        
        if (feedContainer) {
            if (isMobile) {
                feedContainer.style.maxWidth = '100%';
            } else {
                feedContainer.style.maxWidth = '500px';
            }
        }
    }

    // Add mobile menu toggle
    toggleMobileMenu() {
        const sidebar = document.querySelector('.sidebar');
        if (sidebar) {
            sidebar.classList.toggle('open');
        }
    }

    // Add infinite scroll for feed
    setupInfiniteScroll() {
        const feedContainer = document.getElementById('musicFeedContainer');
        if (!feedContainer) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.loadMoreFeedContent();
                }
            });
        }, {
            rootMargin: '100px'
        });

        // Observe the last feed card
        const lastCard = feedContainer.lastElementChild;
        if (lastCard) {
            observer.observe(lastCard);
        }
    }

    async loadMoreFeedContent() {
        // Load more content for infinite scroll
        try {
            const moreMusic = await window.musicAPI.searchMusic('popular music', ['youtube', 'jiosaavn']);
            const shuffledMore = this.shuffleArray(moreMusic).slice(0, 10);
            
            const feedContainer = document.getElementById('musicFeedContainer');
            shuffledMore.forEach((song, index) => {
                const feedCard = this.createFeedCard(song, index);
                feedContainer.appendChild(feedCard);
            });
        } catch (error) {
            console.error('Error loading more content:', error);
        }
    }

    showToast(message, type = 'info') {
        if (window.authManager) {
            window.authManager.showToast(message, type);
        }
    }
    
    // API Testing Functions
    async testYouTubeAPI() {
        if (!window.musicAPI) {
            this.showToast('Music API not initialized', 'error');
            return;
        }
        
        this.showToast('Testing YouTube API...', 'info');
        
        const result = await window.musicAPI.testApiConnection();
        
        if (result.success) {
            this.showToast('✅ YouTube API is working!', 'success');
            console.log('API Test Result:', result);
        } else {
            this.showToast(`❌ API Error: ${result.message}`, 'error');
            console.error('API Test Failed:', result);
        }
        
        return result;
    }
    
    async setupYouTubeAPI(apiKey) {
        if (!apiKey) {
            this.showToast('Please provide a valid API key', 'error');
            return false;
        }
        
        if (window.musicAPI) {
            window.musicAPI.setApiKey(apiKey);
            const testResult = await this.testYouTubeAPI();
            return testResult.success;
        }
        
        return false;
    }
    
    async searchMusicTest(query = 'Arijit Singh') {
        if (!window.musicAPI) {
            this.showToast('Music API not initialized', 'error');
            return [];
        }
        
        this.showToast(`Searching for "${query}"...`, 'info');
        
        try {
            const results = await window.musicAPI.searchMusic(query);
            
            if (results.length > 0) {
                this.showToast(`✅ Found ${results.length} songs!`, 'success');
                console.log('Search Results:', results);
                
                // Display first few results in console
                results.slice(0, 3).forEach((song, index) => {
                    console.log(`${index + 1}. ${song.title} - ${song.artist}`);
                });
            } else {
                this.showToast('No results found', 'info');
            }
            
            return results;
        } catch (error) {
            this.showToast(`Search failed: ${error.message}`, 'error');
            console.error('Search Error:', error);
            return [];
        }
    }

    createProfileModal() {
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content profile-modal">
                <div class="modal-header">
                    <h2><i class="fas fa-user"></i> User Profile</h2>
                    <button class="modal-close" onclick="this.closest('.modal-overlay').remove()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="profile-info">
                        <div class="profile-avatar">
                            <img src="https://via.placeholder.com/100/667eea/ffffff?text=User" alt="Profile">
                            <button class="change-avatar-btn"><i class="fas fa-camera"></i></button>
                        </div>
                        <div class="profile-details">
                            <div class="form-group">
                                <label>Name</label>
                                <input type="text" value="Music Lover" readonly>
                            </div>
                            <div class="form-group">
                                <label>Email</label>
                                <input type="email" value="user@saathimusic.com" readonly>
                            </div>
                            <div class="form-group">
                                <label>Member Since</label>
                                <input type="text" value="January 2024" readonly>
                            </div>
                        </div>
                    </div>
                    <div class="profile-stats">
                        <div class="stat-item">
                            <i class="fas fa-music"></i>
                            <span class="stat-number">1,234</span>
                            <span class="stat-label">Songs Played</span>
                        </div>
                        <div class="stat-item">
                            <i class="fas fa-heart"></i>
                            <span class="stat-number">89</span>
                            <span class="stat-label">Favorites</span>
                        </div>
                        <div class="stat-item">
                            <i class="fas fa-list"></i>
                            <span class="stat-number">12</span>
                            <span class="stat-label">Playlists</span>
                        </div>
                    </div>
                </div>
            </div>
        `;
        return modal;
    }

    createSettingsModal() {
        const modal = document.createElement('div');
        modal.className = 'modal-overlay';
        modal.innerHTML = `
            <div class="modal-content settings-modal">
                <div class="modal-header">
                    <h2><i class="fas fa-cog"></i> Settings</h2>
                    <button class="modal-close" onclick="this.closest('.modal-overlay').remove()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="settings-section">
                        <h3>Audio Settings</h3>
                        <div class="setting-item">
                            <label>Audio Quality</label>
                            <select>
                                <option>High (320kbps)</option>
                                <option selected>Medium (128kbps)</option>
                                <option>Low (64kbps)</option>
                            </select>
                        </div>
                        <div class="setting-item">
                            <label>Crossfade</label>
                            <input type="range" min="0" max="10" value="3">
                        </div>
                    </div>
                    <div class="settings-section">
                        <h3>Appearance</h3>
                        <div class="setting-item">
                            <label>Theme</label>
                            <select>
                                <option selected>Dark Theme</option>
                                <option>Light Theme</option>
                                <option>Auto</option>
                            </select>
                        </div>
                    </div>
                    <div class="settings-section">
                        <h3>Notifications</h3>
                        <div class="setting-item">
                            <label>Push Notifications</label>
                            <input type="checkbox" checked>
                        </div>
                        <div class="setting-item">
                            <label>Email Updates</label>
                            <input type="checkbox">
                        </div>
                    </div>
                </div>
            </div>
        `;
        return modal;
    }

    async loadMultiPlatformContent() {
        try {
            // Load categories
            const categories = await window.multiPlatformAPI.getMusicCategories();
            this.renderCategories(categories);
            
            // Load trending songs
            const trendingSongs = await window.multiPlatformAPI.getTrendingSongs();
            this.renderTrendingSongs(trendingSongs);
            
            // Load popular artists
            const popularArtists = await window.multiPlatformAPI.getPopularArtists();
            this.renderPopularArtists(popularArtists);
            
        } catch (error) {
            console.error('Error loading multi-platform content:', error);
            this.showToast('Error loading music content', 'error');
        }
    }

    renderCategories(categories) {
        const container = document.getElementById('categoriesContainer');
        if (!container) return;
        
        container.innerHTML = categories.map(category => `
            <div class="category-card" onclick="loadCategoryMusic('${category.id}')">
                <img src="${category.image}" alt="${category.name}">
                <h4>${category.name}</h4>
            </div>
        `).join('');
    }

    renderTrendingSongs(songs) {
        const container = document.getElementById('trendingGrid');
        if (!container) return;
        
        container.innerHTML = songs.slice(0, 6).map(song => `
            <div class="trending-card" onclick="playSong(${JSON.stringify(song).replace(/"/g, '&quot;')}, 0)">
                <div class="trending-card-header">
                    <img src="${song.image}" alt="${song.title}">
                    <div class="trending-card-info">
                        <h4>${song.title}</h4>
                        <p>${song.artist}</p>
                    </div>
                </div>
                <div class="trending-card-footer">
                    <div class="platform-indicator">
                        <i class="${window.multiPlatformAPI.getPlatformIcon(song.platform)}"></i>
                        <span>${song.platform}</span>
                    </div>
                    <span class="duration">${song.duration}</span>
                </div>
                <button class="play-btn-overlay">
                    <i class="fas fa-play"></i>
                </button>
            </div>
        `).join('');
    }

    renderPopularArtists(artists) {
        const container = document.getElementById('artistsGrid');
        if (!container) return;
        
        container.innerHTML = artists.map(artist => `
            <div class="artist-card" onclick="loadArtistDetails('${artist.id}')">
                <img src="${artist.image}" alt="${artist.name}">
                <h4>${artist.name}</h4>
                <div class="artist-followers">${artist.followers} followers</div>
                <div class="artist-platform ${artist.platform}">
                    <i class="${window.multiPlatformAPI.getPlatformIcon(artist.platform)}"></i>
                    <span>${artist.platform}</span>
                </div>
            </div>
        `).join('');
    }
}

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.app = new FlyMusicApp();
});

// Global functions for HTML onclick handlers
window.playSong = (song, index) => {
    if (window.app) {
        window.app.playSong(song, index);
    }
};

window.addToFavorites = (songId) => {
    if (window.app) {
        window.app.addToFavorites(songId);
    }
};

window.addToPlaylist = (songId) => {
    if (window.app) {
        window.app.addToPlaylist(songId);
    }
};

window.shareSong = (songId) => {
    if (window.app) {
        window.app.shareSong(songId);
    }
};
