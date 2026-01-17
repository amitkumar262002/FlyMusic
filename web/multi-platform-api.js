// Multi-Platform Music API - Spotify, JioSaavn, YouTube
class MultiPlatformMusicAPI {
    constructor() {
        // API Configuration
        this.apis = {
            spotify: {
                clientId: 'YOUR_SPOTIFY_CLIENT_ID',
                clientSecret: 'YOUR_SPOTIFY_CLIENT_SECRET',
                accessToken: null,
                baseUrl: 'https://api.spotify.com/v1'
            },
            jiosaavn: {
                baseUrl: 'https://saavn.me/api',
                // Using public JioSaavn API endpoints
                endpoints: {
                    search: '/search/songs',
                    trending: '/modules?language=hindi',
                    albums: '/albums',
                    artists: '/artists'
                }
            },
            youtube: {
                apiKey: 'AIzaSyD-BCFeMK9XvTqX1o-v8xCujDv1I1GFnzo',
                baseUrl: 'https://www.googleapis.com/youtube/v3'
            }
        };
        
        // Cache system
        this.cache = new Map();
        this.cacheExpiry = 30 * 60 * 1000; // 30 minutes
        
        // Initialize sample data for demo
        this.initializeSampleData();
        
        // Add to window for global access
        window.multiPlatformAPI = this;
        
        console.log('🎵 Multi-Platform Music API initialized');
    }

    // Initialize with sample trending songs and artists data
    initializeSampleData() {
        this.sampleData = {
            trending: [
                {
                    id: 'trend_1',
                    title: 'Tere Ishk Mein',
                    artist: 'A.R. Rahman, Arijit Singh, Ishaal Kamil',
                    album: 'Tere Ishk Mein',
                    image: 'https://via.placeholder.com/300x300/667eea/ffffff?text=Tere+Ishk+Mein',
                    duration: '4:32',
                    platform: 'jiosaavn',
                    getWorkingAudioUrl() {
                        // Use working audio URLs for demo
                        const workingAudios = [
                            'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3',
                            'https://sample-videos.com/zip/10/mp3/mp3-15s/SampleAudio_0.4mb_mp3.mp3',
                            'https://actions.google.com/sounds/v1/alarms/bugle_tune.ogg',
                            'https://actions.google.com/sounds/v1/cartoon/clang_and_wobble.ogg'
                        ];
                        
                        // Return a random working audio for demo
                        const randomIndex = Math.floor(Math.random() * workingAudios.length);
                        return workingAudios[randomIndex];
                    }
                },
                {
                    id: 'trend_2',
                    title: 'Deewaniyat',
                    artist: 'Vishal Mishra, Kaushik Guddu, Kunaal Vermaa',
                    album: 'Ek Deewane Ki',
                    image: 'https://via.placeholder.com/300x300/e74c3c/ffffff?text=Deewaniyat',
                    duration: '3:45',
                    platform: 'spotify',
                    getWorkingAudioUrl() {
                        // Use working audio URLs for demo
                        const workingAudios = [
                            'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3',
                            'https://sample-videos.com/zip/10/mp3/mp3-15s/SampleAudio_0.4mb_mp3.mp3',
                            'https://actions.google.com/sounds/v1/alarms/bugle_tune.ogg',
                            'https://actions.google.com/sounds/v1/cartoon/clang_and_wobble.ogg'
                        ];
                        
                        // Return a random working audio for demo
                        const randomIndex = Math.floor(Math.random() * workingAudios.length);
                        return workingAudios[randomIndex];
                    }
                },
                {
                    id: 'trend_3',
                    title: 'Dum Blood',
                    artist: 'Sa Abhyankkar, Paal Dabba, beebhumiika',
                    album: 'Dude',
                    image: 'https://via.placeholder.com/300x300/f39c12/ffffff?text=Dum+Blood',
                    duration: '3:28',
                    platform: 'youtube',
                    getWorkingAudioUrl() {
                        // Use working audio URLs for demo
                        const workingAudios = [
                            'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3',
                            'https://sample-videos.com/zip/10/mp3/mp3-15s/SampleAudio_0.4mb_mp3.mp3',
                            'https://actions.google.com/sounds/v1/alarms/bugle_tune.ogg',
                            'https://actions.google.com/sounds/v1/cartoon/clang_and_wobble.ogg'
                        ];
                        
                        // Return a random working audio for demo
                        const randomIndex = Math.floor(Math.random() * workingAudios.length);
                        return workingAudios[randomIndex];
                    }
                },
                {
                    id: 'trend_4',
                    title: 'Dhurandhar - Title Track',
                    artist: 'Shashwat Sachdev, Hanumankind, Jasmine',
                    album: 'Dhurandhar',
                    image: 'https://via.placeholder.com/300x300/9b59b6/ffffff?text=Dhurandhar',
                    duration: '4:15',
                    platform: 'spotify',
                    streamUrl: 'https://actions.google.com/sounds/v1/cartoon/clang_and_wobble.ogg'
                },
                {
                    id: 'trend_5',
                    title: 'Nafrat',
                    artist: 'Darshan Raval',
                    album: 'Love You',
                    image: 'https://via.placeholder.com/300x300/e67e22/ffffff?text=Nafrat',
                    duration: '3:52',
                    platform: 'jiosaavn',
                    streamUrl: 'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3'
                },
                {
                    id: 'trend_6',
                    title: 'Boyfriend',
                    artist: 'Karan Aujla, Ikky',
                    album: 'Boyfriend',
                    image: 'https://via.placeholder.com/300x300/2ecc71/ffffff?text=Boyfriend',
                    duration: '3:33',
                    platform: 'youtube',
                    streamUrl: 'https://sample-videos.com/zip/10/mp3/mp3-15s/SampleAudio_0.4mb_mp3.mp3'
                }
            ],
            popularArtists: [
                {
                    id: 'artist_1',
                    name: 'Arijit Singh',
                    image: 'https://via.placeholder.com/300x300/3498db/ffffff?text=Arijit+Singh',
                    followers: '45.2M',
                    platform: 'spotify'
                },
                {
                    id: 'artist_2',
                    name: 'A.R. Rahman',
                    image: 'https://via.placeholder.com/300x300/e74c3c/ffffff?text=A.R.+Rahman',
                    followers: '12.8M',
                    platform: 'jiosaavn'
                },
                {
                    id: 'artist_3',
                    name: 'Shreya Ghoshal',
                    image: 'https://via.placeholder.com/300x300/9b59b6/ffffff?text=Shreya+Ghoshal',
                    followers: '8.5M',
                    platform: 'spotify'
                },
                {
                    id: 'artist_4',
                    name: 'Darshan Raval',
                    image: 'https://via.placeholder.com/300x300/f39c12/ffffff?text=Darshan+Raval',
                    followers: '6.2M',
                    platform: 'youtube'
                },
                {
                    id: 'artist_5',
                    name: 'Vishal Mishra',
                    image: 'https://via.placeholder.com/300x300/2ecc71/ffffff?text=Vishal+Mishra',
                    followers: '4.8M',
                    platform: 'jiosaavn'
                },
                {
                    id: 'artist_6',
                    name: 'Karan Aujla',
                    image: 'https://via.placeholder.com/300x300/e67e22/ffffff?text=Karan+Aujla',
                    followers: '3.9M',
                    platform: 'spotify'
                }
            ],
            categories: [
                { id: 'bollywood', name: 'Bollywood', image: 'https://via.placeholder.com/300x300/e74c3c/ffffff?text=Bollywood' },
                { id: 'punjabi', name: 'Punjabi', image: 'https://via.placeholder.com/300x300/f39c12/ffffff?text=Punjabi' },
                { id: 'romantic', name: 'Romantic', image: 'https://via.placeholder.com/300x300/e91e63/ffffff?text=Romantic' },
                { id: 'party', name: 'Party Hits', image: 'https://via.placeholder.com/300x300/9c27b0/ffffff?text=Party+Hits' },
                { id: 'devotional', name: 'Devotional', image: 'https://via.placeholder.com/300x300/ff9800/ffffff?text=Devotional' },
                { id: 'international', name: 'International', image: 'https://via.placeholder.com/300x300/2196f3/ffffff?text=International' }
            ]
        };
    }

    // Spotify API Methods
    async getSpotifyAccessToken() {
        if (this.apis.spotify.accessToken) {
            return this.apis.spotify.accessToken;
        }

        // For demo purposes, we'll use sample data
        // In production, implement proper Spotify OAuth flow
        console.log('🎵 Using Spotify sample data for demo');
        return 'demo_token';
    }

    async searchSpotify(query, limit = 20) {
        try {
            // In production, use actual Spotify API
            // For demo, return filtered sample data
            const results = this.sampleData.trending.filter(song => 
                song.platform === 'spotify' &&
                (song.title.toLowerCase().includes(query.toLowerCase()) ||
                 song.artist.toLowerCase().includes(query.toLowerCase()))
            );
            
            return {
                tracks: results.slice(0, limit),
                platform: 'spotify'
            };
        } catch (error) {
            console.error('Spotify search error:', error);
            return { tracks: [], platform: 'spotify' };
        }
    }

    // JioSaavn API Methods
    async searchJioSaavn(query, limit = 20) {
        try {
            // Using sample data for demo
            // In production, use actual JioSaavn API calls
            const results = this.sampleData.trending.filter(song => 
                song.platform === 'jiosaavn' &&
                (song.title.toLowerCase().includes(query.toLowerCase()) ||
                 song.artist.toLowerCase().includes(query.toLowerCase()))
            );
            
            return {
                tracks: results.slice(0, limit),
                platform: 'jiosaavn'
            };
        } catch (error) {
            console.error('JioSaavn search error:', error);
            return { tracks: [], platform: 'jiosaavn' };
        }
    }

    async getJioSaavnTrending() {
        // Return trending songs from JioSaavn
        return this.sampleData.trending.filter(song => song.platform === 'jiosaavn');
    }

    // YouTube API Methods
    async searchYouTube(query, limit = 20) {
        try {
            // Using sample data for demo
            // In production, use actual YouTube Data API
            const results = this.sampleData.trending.filter(song => 
                song.platform === 'youtube' &&
                (song.title.toLowerCase().includes(query.toLowerCase()) ||
                 song.artist.toLowerCase().includes(query.toLowerCase()))
            );
            
            return {
                tracks: results.slice(0, limit),
                platform: 'youtube'
            };
        } catch (error) {
            console.error('YouTube search error:', error);
            return { tracks: [], platform: 'youtube' };
        }
    }

    // Universal Search across all platforms
    async searchAllPlatforms(query, limit = 50) {
        try {
            const [spotifyResults, jiosaavnResults, youtubeResults] = await Promise.all([
                this.searchSpotify(query, Math.ceil(limit / 3)),
                this.searchJioSaavn(query, Math.ceil(limit / 3)),
                this.searchYouTube(query, Math.ceil(limit / 3))
            ]);

            const allResults = [
                ...spotifyResults.tracks,
                ...jiosaavnResults.tracks,
                ...youtubeResults.tracks
            ];

            return {
                tracks: allResults.slice(0, limit),
                platforms: ['spotify', 'jiosaavn', 'youtube'],
                total: allResults.length
            };
        } catch (error) {
            console.error('Multi-platform search error:', error);
            return { tracks: [], platforms: [], total: 0 };
        }
    }

    // Get trending songs from all platforms
    async getTrendingSongs() {
        return this.sampleData.trending;
    }

    // Get popular artists
    async getPopularArtists() {
        return this.sampleData.popularArtists;
    }

    // Get music categories
    async getMusicCategories() {
        return this.sampleData.categories;
    }

    // Get songs by category
    async getSongsByCategory(categoryId, limit = 20) {
        // Filter songs based on category
        let filteredSongs = this.sampleData.trending;
        
        switch (categoryId) {
            case 'bollywood':
                filteredSongs = this.sampleData.trending.filter(song => 
                    song.artist.includes('Rahman') || song.artist.includes('Arijit') || song.title.includes('Ishk')
                );
                break;
            case 'punjabi':
                filteredSongs = this.sampleData.trending.filter(song => 
                    song.artist.includes('Karan Aujla') || song.title.includes('Boyfriend')
                );
                break;
            case 'romantic':
                filteredSongs = this.sampleData.trending.filter(song => 
                    song.title.includes('Ishk') || song.title.includes('Deewaniyat') || song.album.includes('Love')
                );
                break;
            default:
                filteredSongs = this.sampleData.trending;
        }
        
        return filteredSongs.slice(0, limit);
    }

    // Get artist details and top tracks
    async getArtistDetails(artistId) {
        const artist = this.sampleData.popularArtists.find(a => a.id === artistId);
        if (!artist) return null;

        // Get artist's top tracks
        const topTracks = this.sampleData.trending.filter(song => 
            song.artist.toLowerCase().includes(artist.name.toLowerCase())
        );

        return {
            ...artist,
            topTracks: topTracks.slice(0, 10)
        };
    }

    // Cache management
    getCacheKey(method, params) {
        return `${method}_${JSON.stringify(params)}`;
    }

    setCache(key, data) {
        this.cache.set(key, {
            data,
            timestamp: Date.now()
        });
    }

    getCache(key) {
        const cached = this.cache.get(key);
        if (!cached) return null;
        
        if (Date.now() - cached.timestamp > this.cacheExpiry) {
            this.cache.delete(key);
            return null;
        }
        
        return cached.data;
    }

    // Utility methods
    formatDuration(seconds) {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    }

    getPlatformIcon(platform) {
        const icons = {
            spotify: 'fab fa-spotify',
            jiosaavn: 'fas fa-music',
            youtube: 'fab fa-youtube'
        };
        return icons[platform] || 'fas fa-music';
    }

    getPlatformColor(platform) {
        const colors = {
            spotify: '#1DB954',
            jiosaavn: '#FF6B35',
            youtube: '#FF0000'
        };
        return colors[platform] || '#FFD700';
    }
}

// Initialize the multi-platform API
document.addEventListener('DOMContentLoaded', () => {
    new MultiPlatformMusicAPI();
});
