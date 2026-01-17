// Content Manager - Music, Podcasts, Audiobooks
class ContentManager {
    constructor() {
        this.currentPage = 'music';
        this.content = {
            music: null,
            podcasts: this.getPodcastContent(),
            audiobooks: this.getAudiobookContent()
        };
        
        this.isLoading = false;
        this.cache = new Map();
        
        this.init();
        window.contentManager = this;
        console.log('🎵 Content Manager initialized');
    }
    
    async init() {
        this.createNavigationTabs();
        await this.loadMusicContent();
        this.showPage('music');
    }
    
    async loadMusicContent() {
        if (this.isLoading) return;
        
        this.isLoading = true;
        console.log('🔄 Loading real music content...');
        
        try {
            // Wait for Real Music API to be available
            await this.waitForRealMusicAPI();
            
            // Load different categories
            const [trending, newReleases, artists] = await Promise.all([
                window.realMusicAPI.getTrendingSongs(50),
                window.realMusicAPI.getNewReleases(30),
                window.realMusicAPI.getPopularArtists(20)
            ]);
            
            this.content.music = {
                trending: trending,
                newReleases: newReleases,
                artists: artists,
                genres: this.getGenreData()
            };
            
            console.log(`✅ Loaded ${trending.length} trending songs, ${newReleases.length} new releases, ${artists.length} artists`);
            
        } catch (error) {
            console.error('❌ Error loading music content:', error);
            this.content.music = this.getFallbackMusicContent();
        }
        
        this.isLoading = false;
    }
    
    async waitForRealMusicAPI() {
        return new Promise((resolve) => {
            const checkAPI = () => {
                if (window.realMusicAPI) {
                    resolve();
                } else {
                    setTimeout(checkAPI, 100);
                }
            };
            checkAPI();
        });
    }
    
    getFallbackMusicContent() {
        return {
            trending: [
                {
                    id: 'fallback_1',
                    title: 'Sample Song 1',
                    artist: 'Demo Artist',
                    album: 'Demo Album',
                    thumbnail: 'https://via.placeholder.com/300x300/667eea/white?text=Music',
                    duration: '3:30',
                    audioUrl: 'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3',
                    source: 'demo',
                    year: '2023',
                    language: 'English',
                    genre: 'Pop'
                }
            ],
            newReleases: [],
            artists: [],
            genres: this.getGenreData()
        };
    }
    
    getGenreData() {
        return [
            { name: 'Bollywood', count: '1000+ songs', image: '🎬' },
            { name: 'Punjabi', count: '500+ songs', image: '🎵' },
            { name: 'Pop', count: '800+ songs', image: '🎤' },
            { name: 'Rock', count: '600+ songs', image: '🎸' },
            { name: 'Hip Hop', count: '400+ songs', image: '🎧' },
            { name: 'Classical', count: '300+ songs', image: '🎼' }
        ];
    }
    
    createNavigationTabs() {
        const sidebar = document.querySelector('.sidebar');
        if (!sidebar) return;
        
        // Add content navigation after existing nav
        const contentNav = document.createElement('div');
        contentNav.className = 'content-navigation';
        contentNav.innerHTML = `
            <div class="nav-section">
                <h3>Browse</h3>
                <div class="nav-items">
                    <div class="nav-item active" data-page="music">
                        <i class="fas fa-music"></i>
                        <span>Music</span>
                    </div>
                    <div class="nav-item" data-page="podcasts">
                        <i class="fas fa-podcast"></i>
                        <span>Podcasts</span>
                    </div>
                    <div class="nav-item" data-page="audiobooks">
                        <i class="fas fa-book-open"></i>
                        <span>Audiobooks</span>
                    </div>
                </div>
            </div>
        `;
        
        sidebar.appendChild(contentNav);
        
        // Add click handlers
        contentNav.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', () => {
                const page = item.dataset.page;
                this.showPage(page);
                
                // Update active state
                contentNav.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
                item.classList.add('active');
            });
        });
    }
    
    showPage(pageType) {
        this.currentPage = pageType;
        const contentArea = document.querySelector('.content-area');
        if (!contentArea) return;
        
        // Clear existing content
        contentArea.innerHTML = '';
        
        // Create page content
        const pageContent = this.createPageContent(pageType);
        contentArea.appendChild(pageContent);
        
        // Update header title
        this.updateHeaderTitle(pageType);
    }
    
    updateHeaderTitle(pageType) {
        const brandName = document.querySelector('.brand-name');
        if (brandName) {
            const titles = {
                music: 'Saathi Music',
                podcasts: 'Saathi Podcasts',
                audiobooks: 'Saathi Audiobooks'
            };
            brandName.textContent = titles[pageType] || 'Saathi Music';
        }
    }
    
    createPageContent(pageType) {
        const container = document.createElement('div');
        container.className = `${pageType}-page`;
        
        switch(pageType) {
            case 'music':
                container.innerHTML = this.createMusicPage();
                break;
            case 'podcasts':
                container.innerHTML = this.createPodcastsPage();
                break;
            case 'audiobooks':
                container.innerHTML = this.createAudiobooksPage();
                break;
        }
        
        return container;
    }
    
    createMusicPage() {
        return `
            <div class="page-header">
                <h1>Music For Every Movement</h1>
                <p>Discover trending songs, albums, and artists from around the world</p>
            </div>
            
            <div class="trending-section">
                <div class="section-header">
                    <h2>Trending Now</h2>
                    <button class="show-all-btn">Show all</button>
                </div>
                <div class="content-grid" id="music-trending">
                    ${this.renderMusicCards(this.content.music.trending)}
                </div>
            </div>
            
            <div class="genres-section">
                <div class="section-header">
                    <h2>Browse by Genre</h2>
                </div>
                <div class="genres-grid">
                    ${this.renderGenreCards(this.content.music.genres)}
                </div>
            </div>
            
            <div class="new-releases-section">
                <div class="section-header">
                    <h2>New Releases</h2>
                    <button class="show-all-btn">Show all</button>
                </div>
                <div class="content-grid">
                    ${this.renderMusicCards(this.content.music.newReleases)}
                </div>
            </div>
        `;
    }
    
    createPodcastsPage() {
        return `
            <div class="page-header">
                <h1>Podcasts For Every Interest</h1>
                <p>Listen to the latest episodes from your favorite shows</p>
            </div>
            
            <div class="featured-section">
                <div class="section-header">
                    <h2>Featured Podcasts</h2>
                </div>
                <div class="content-grid">
                    ${this.renderPodcastCards(this.content.podcasts.featured)}
                </div>
            </div>
            
            <div class="categories-section">
                <div class="section-header">
                    <h2>Browse Categories</h2>
                </div>
                <div class="categories-grid">
                    ${this.renderCategoryCards(this.content.podcasts.categories)}
                </div>
            </div>
        `;
    }
    
    createAudiobooksPage() {
        return `
            <div class="page-header">
                <h1>Audiobooks For Every Reader</h1>
                <p>Immerse yourself in captivating stories and knowledge</p>
            </div>
            
            <div class="bestsellers-section">
                <div class="section-header">
                    <h2>Bestsellers</h2>
                </div>
                <div class="content-grid">
                    ${this.renderAudiobookCards(this.content.audiobooks.bestsellers)}
                </div>
            </div>
            
            <div class="genres-section">
                <div class="section-header">
                    <h2>Browse by Genre</h2>
                </div>
                <div class="genres-grid">
                    ${this.renderGenreCards(this.content.audiobooks.genres)}
                </div>
            </div>
        `;
    }
    
    getMusicContent() {
        return {
            trending: [
                { id: 'm1', title: 'Kesariya', artist: 'Arijit Singh', album: 'Brahmastra', image: 'https://via.placeholder.com/300x300/667eea/ffffff?text=Kesariya', duration: '4:28' },
                { id: 'm2', title: 'Tum Hi Ho', artist: 'Arijit Singh', album: 'Aashiqui 2', image: 'https://via.placeholder.com/300x300/e74c3c/ffffff?text=Tum+Hi+Ho', duration: '4:22' },
                { id: 'm3', title: 'Raataan Lambiyan', artist: 'Tanishk Bagchi', album: 'Shershaah', image: 'https://via.placeholder.com/300x300/f39c12/ffffff?text=Raataan', duration: '3:15' },
                { id: 'm4', title: 'Manike', artist: 'Yohani', album: 'Single', image: 'https://via.placeholder.com/300x300/9b59b6/ffffff?text=Manike', duration: '3:42' },
                { id: 'm5', title: 'Dil Bechara', artist: 'A.R. Rahman', album: 'Dil Bechara', image: 'https://via.placeholder.com/300x300/2ecc71/ffffff?text=Dil+Bechara', duration: '4:05' },
                { id: 'm6', title: 'Ghungroo', artist: 'Arijit Singh', album: 'War', image: 'https://via.placeholder.com/300x300/e67e22/ffffff?text=Ghungroo', duration: '5:02' }
            ],
            genres: [
                { id: 'bollywood', name: 'Bollywood', image: 'https://via.placeholder.com/200x200/e74c3c/ffffff?text=Bollywood', count: '1000+ songs' },
                { id: 'punjabi', name: 'Punjabi', image: 'https://via.placeholder.com/200x200/f39c12/ffffff?text=Punjabi', count: '500+ songs' },
                { id: 'pop', name: 'Pop', image: 'https://via.placeholder.com/200x200/9b59b6/ffffff?text=Pop', count: '800+ songs' },
                { id: 'rock', name: 'Rock', image: 'https://via.placeholder.com/200x200/2ecc71/ffffff?text=Rock', count: '600+ songs' }
            ],
            newReleases: [
                { id: 'm7', title: 'Apna Bana Le', artist: 'Arijit Singh', album: 'Bhediya', image: 'https://via.placeholder.com/300x300/3498db/ffffff?text=Apna+Bana', duration: '4:18' },
                { id: 'm8', title: 'Kahani Suno', artist: 'Kaifi Khalil', album: 'Single', image: 'https://via.placeholder.com/300x300/1abc9c/ffffff?text=Kahani+Suno', duration: '3:55' }
            ]
        };
    }
    
    getPodcastContent() {
        return {
            featured: [
                { id: 'p1', title: 'The Ranveer Show', host: 'Ranveer Allahbadia', episodes: '200+', image: 'https://via.placeholder.com/300x300/667eea/ffffff?text=Ranveer+Show' },
                { id: 'p2', title: 'Dostcast', host: 'Vinamre Kasanaa', episodes: '150+', image: 'https://via.placeholder.com/300x300/e74c3c/ffffff?text=Dostcast' },
                { id: 'p3', title: 'Cyrus Says', host: 'Cyrus Broacha', episodes: '300+', image: 'https://via.placeholder.com/300x300/f39c12/ffffff?text=Cyrus+Says' }
            ],
            categories: [
                { id: 'comedy', name: 'Comedy', image: 'https://via.placeholder.com/200x200/9b59b6/ffffff?text=Comedy', count: '50+ shows' },
                { id: 'business', name: 'Business', image: 'https://via.placeholder.com/200x200/2ecc71/ffffff?text=Business', count: '30+ shows' },
                { id: 'tech', name: 'Technology', image: 'https://via.placeholder.com/200x200/e67e22/ffffff?text=Tech', count: '40+ shows' }
            ]
        };
    }
    
    getAudiobookContent() {
        return {
            bestsellers: [
                { id: 'a1', title: 'Atomic Habits', author: 'James Clear', duration: '5h 35m', image: 'https://via.placeholder.com/300x300/667eea/ffffff?text=Atomic+Habits' },
                { id: 'a2', title: 'The Alchemist', author: 'Paulo Coelho', duration: '4h 10m', image: 'https://via.placeholder.com/300x300/e74c3c/ffffff?text=Alchemist' },
                { id: 'a3', title: 'Sapiens', author: 'Yuval Noah Harari', duration: '15h 17m', image: 'https://via.placeholder.com/300x300/f39c12/ffffff?text=Sapiens' }
            ],
            genres: [
                { id: 'fiction', name: 'Fiction', image: 'https://via.placeholder.com/200x200/9b59b6/ffffff?text=Fiction', count: '200+ books' },
                { id: 'business', name: 'Business', image: 'https://via.placeholder.com/200x200/2ecc71/ffffff?text=Business', count: '150+ books' },
                { id: 'selfhelp', name: 'Self Help', image: 'https://via.placeholder.com/200x200/e67e22/ffffff?text=Self+Help', count: '100+ books' }
            ]
        };
    }
    
    renderMusicCards(items) {
        return items.map(item => `
            <div class="content-card music-card" onclick="playContent('${item.id}', 'music')">
                <div class="card-image">
                    <img src="${item.image}" alt="${item.title}">
                    <div class="play-overlay">
                        <i class="fas fa-play"></i>
                    </div>
                </div>
                <div class="card-info">
                    <h4>${item.title}</h4>
                    <p>${item.artist}</p>
                    <span class="duration">${item.duration}</span>
                </div>
            </div>
        `).join('');
    }
    
    renderPodcastCards(items) {
        return items.map(item => `
            <div class="content-card podcast-card" onclick="playContent('${item.id}', 'podcast')">
                <div class="card-image">
                    <img src="${item.image}" alt="${item.title}">
                    <div class="play-overlay">
                        <i class="fas fa-play"></i>
                    </div>
                </div>
                <div class="card-info">
                    <h4>${item.title}</h4>
                    <p>${item.host}</p>
                    <span class="episodes">${item.episodes} episodes</span>
                </div>
            </div>
        `).join('');
    }
    
    renderAudiobookCards(items) {
        return items.map(item => `
            <div class="content-card audiobook-card" onclick="playContent('${item.id}', 'audiobook')">
                <div class="card-image">
                    <img src="${item.image}" alt="${item.title}">
                    <div class="play-overlay">
                        <i class="fas fa-play"></i>
                    </div>
                </div>
                <div class="card-info">
                    <h4>${item.title}</h4>
                    <p>${item.author}</p>
                    <span class="duration">${item.duration}</span>
                </div>
            </div>
        `).join('');
    }
    
    renderGenreCards(items) {
        return items.map(item => `
            <div class="genre-card" onclick="browseGenre('${item.id}')">
                <img src="${item.image}" alt="${item.name}">
                <div class="genre-info">
                    <h4>${item.name}</h4>
                    <p>${item.count}</p>
                </div>
            </div>
        `).join('');
    }
    
    renderCategoryCards(items) {
        return items.map(item => `
            <div class="category-card" onclick="browseCategory('${item.id}')">
                <img src="${item.image}" alt="${item.name}">
                <div class="category-info">
                    <h4>${item.name}</h4>
                    <p>${item.count}</p>
                </div>
            </div>
        `).join('');
    }
}

// Global functions
window.playContent = (id, type) => {
    if (window.app) {
        window.app.showToast(`Playing ${type}: ${id}`, 'success');
    }
};

window.browseGenre = (genreId) => {
    if (window.app) {
        window.app.showToast(`Browsing ${genreId} genre`, 'info');
    }
};

window.browseCategory = (categoryId) => {
    if (window.app) {
        window.app.showToast(`Browsing ${categoryId} category`, 'info');
    }
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ContentManager();
});
