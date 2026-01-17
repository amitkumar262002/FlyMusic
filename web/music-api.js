// Music API Integration - YouTube and JioSaavn
class MusicAPIManager {
    constructor() {
        // API Keys - Add your YouTube API key here
        this.youtubeApiKey = this.getApiKey() || 'AIzaSyD-BCFeMK9XvTqX1o-v8xCujDv1I1GFnzo'; // Your actual YouTube API key
        this.jiosaavnApiKey = 'YOUR_JIOSAAVN_API_KEY';
        
        // Cache for API responses
        this.cache = new Map();
        this.cacheExpiry = 30 * 60 * 1000; // 30 minutes
        
        // Initialize with sample data
        this.initializeSampleData();
        
        this.initializeAPIs();
        
        // Add to window for testing
        window.musicAPI = this;
    }
    
    getApiKey() {
        // Try to get API key from localStorage or environment
        return localStorage.getItem('youtube_api_key') || 
               (typeof process !== 'undefined' && process.env?.YOUTUBE_API_KEY) ||
               null;
    }
    
    setApiKey(key) {
        this.youtubeApiKey = key;
        localStorage.setItem('youtube_api_key', key);
        console.log('✅ YouTube API key updated successfully!');
    }
    
    async testApiConnection() {
        if (!this.youtubeApiKey || this.youtubeApiKey === 'YOUR_YOUTUBE_API_KEY') {
            return {
                success: false,
                error: 'No API key configured',
                message: 'Please add your YouTube Data API v3 key'
            };
        }
        
        try {
            const testUrl = `https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&videoCategoryId=10&q=test&maxResults=1&key=${this.youtubeApiKey}`;
            const response = await fetch(testUrl);
            const data = await response.json();
            
            if (data.error) {
                return {
                    success: false,
                    error: data.error.code,
                    message: data.error.message
                };
            }
            
            return {
                success: true,
                message: 'YouTube API is working correctly!',
                quota: data.pageInfo?.totalResults || 'Unknown'
            };
        } catch (error) {
            return {
                success: false,
                error: 'Network Error',
                message: error.message
            };
        }
    }

    initializeSampleData() {
        // Use sample data as fallback when API quota is exceeded
        this.sampleData = window.sampleMusicData || {
            trending: [],
            bollywood: [],
            international: []
        };
    }

    async initializeAPIs() {
        // Load sample data for demo purposes
        await this.loadSampleData();
    }

    async loadSampleData() {
        // Load sample data from external file or use fallback
        if (window.sampleMusicData) {
            this.sampleData = window.sampleMusicData;
        } else {
            // Fallback data if sample-data.js is not loaded
            this.sampleData = {
                trending: [
                    {
                        id: 'sample1',
                        title: 'Kesariya',
                        artist: 'Arijit Singh',
                        album: 'Brahmastra',
                        thumbnail: 'https://via.placeholder.com/300x300/667eea/ffffff?text=Kesariya',
                        duration: '4:28',
                        source: 'jiosaavn',
                        year: '2022'
                    },
                    {
                        id: 'sample2',
                        title: 'As It Was',
                        artist: 'Harry Styles',
                        thumbnail: 'https://via.placeholder.com/300x300/f39c12/ffffff?text=As+It+Was',
                        duration: '2:47',
                        source: 'youtube',
                        year: '2022'
                    }
                ],
                bollywood: [
                    {
                        id: 'sample3',
                        title: 'Pal Pal Dil Ke Paas',
                        artist: 'Kishore Kumar',
                        thumbnail: 'https://via.placeholder.com/300x300/e74c3c/ffffff?text=Pal+Pal+Dil',
                        duration: '4:15',
                        source: 'jiosaavn'
                    },
                    {
                        id: 'sample4',
                        title: 'Tum Hi Ho',
                        artist: 'Arijit Singh',
                        album: 'Aashiqui 2',
                        thumbnail: 'https://via.placeholder.com/300x300/9b59b6/ffffff?text=Tum+Hi+Ho',
                        duration: '4:22',
                        source: 'jiosaavn'
                    }
                ],
                international: [
                    {
                        id: 'sample5',
                        title: 'Shape of You',
                        artist: 'Ed Sheeran',
                        thumbnail: 'https://via.placeholder.com/300x300/2ecc71/ffffff?text=Shape+of+You',
                        duration: '3:53',
                        source: 'youtube'
                    },
                    {
                        id: 'sample6',
                        title: 'Blinding Lights',
                        artist: 'The Weeknd',
                        thumbnail: 'https://via.placeholder.com/300x300/e67e22/ffffff?text=Blinding+Lights',
                        duration: '3:20',
                        source: 'youtube'
                    }
                ]
            };
        }
    }

    async searchYouTube(query) {
        if (!this.youtubeApiKey || this.youtubeApiKey === 'YOUR_YOUTUBE_API_KEY') {
            console.warn('YouTube API key not configured');
            throw new Error('YouTube API key not configured');
        }
        
        const cacheKey = `youtube_${query}`;
        if (this.cache.has(cacheKey)) {
            const cached = this.cache.get(cacheKey);
            if (Date.now() - cached.timestamp < this.cacheExpiry) {
                return cached.data;
            }
        }
        
        try {
            const url = `https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&videoCategoryId=10&q=${encodeURIComponent(query + ' music')}&maxResults=10&key=${this.youtubeApiKey}`;
            
            const response = await fetch(url);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            
            if (data.error) {
                console.error('YouTube API Error:', data.error);
                throw new Error(data.error.message);
            }
            
            const results = data.items.map(item => ({
                id: item.id.videoId,
                title: this.cleanTitle(item.snippet.title),
                artist: item.snippet.channelTitle,
                thumbnail: item.snippet.thumbnails.medium?.url || item.snippet.thumbnails.default?.url,
                source: 'youtube',
                url: `https://www.youtube.com/watch?v=${item.id.videoId}`,
                duration: '3:30', // Placeholder - would need additional API call
                year: new Date(item.snippet.publishedAt).getFullYear(),
                language: this.detectLanguage(item.snippet.title),
                genre: 'Music',
                album: 'YouTube'
            }));
            
            // Cache results
            this.cache.set(cacheKey, {
                data: results,
                timestamp: Date.now()
            });
            
            console.log(`Found ${results.length} YouTube results for: ${query}`);
            return results;
        } catch (error) {
            console.error('YouTube search failed:', error);
            throw error;
        }
    }
    
    cleanTitle(title) {
        // Remove common YouTube suffixes
        return title
            .replace(/\s*\(Official.*\)/gi, '')
            .replace(/\s*\[Official.*\]/gi, '')
            .replace(/\s*- Official.*/gi, '')
            .replace(/\s*\| Official.*/gi, '')
            .replace(/\s*HD$/gi, '')
            .replace(/\s*4K$/gi, '')
            .trim();
    }
    
    detectLanguage(title) {
        // Simple language detection based on title
        const hindiPattern = /[\u0900-\u097F]/;
        if (hindiPattern.test(title)) return 'Hindi';
        
        const hindiKeywords = ['bollywood', 'hindi', 'भारतीय', 'गाना', 'फिल्म'];
        const titleLower = title.toLowerCase();
        if (hindiKeywords.some(keyword => titleLower.includes(keyword))) {
            return 'Hindi';
        }
        
        return 'English';
    }

    // JioSaavn API Methods
    async searchJioSaavn(query, limit = 20) {
        try {
            const cacheKey = `jiosaavn_search_${query}_${limit}`;
            const cached = this.getFromCache(cacheKey);
            if (cached) return cached;

            const url = `${this.jioSaavnBaseUrl}/search/songs?query=${encodeURIComponent(query)}&limit=${limit}`;
            
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('JioSaavn API request failed');
            }
            
            const data = await response.json();
            const results = this.formatJioSaavnResults(data.data?.results || []);
            
            this.setCache(cacheKey, results);
            return results;
        } catch (error) {
            console.error('JioSaavn search error:', error);
            // Return sample data as fallback
            return this.getSampleJioSaavnData(query);
        }
    }

    formatJioSaavnResults(items) {
        return items.map(item => ({
            id: item.id,
            title: item.name || item.title,
            artist: item.primaryArtists || item.artists?.primary?.map(a => a.name).join(', ') || 'Unknown Artist',
            album: item.album?.name || 'Unknown Album',
            thumbnail: item.image?.[2]?.link || item.image?.[1]?.link || item.image?.[0]?.link,
            duration: this.formatDuration(item.duration),
            source: 'jiosaavn',
            url: item.perma_url || item.url,
            streamUrl: item.downloadUrl?.[4]?.link || item.downloadUrl?.[3]?.link || item.downloadUrl?.[2]?.link,
            year: item.year,
            language: item.language
        }));
    }

    // Combined search across both platforms
    async searchMusic(query, sources = ['youtube', 'jiosaavn']) {
        try {
            const results = [];
            
            // Search YouTube with real API
            if (sources.includes('youtube') && this.youtubeApiKey !== 'YOUR_YOUTUBE_API_KEY') {
                try {
                    const youtubeResults = await this.searchYouTube(query);
                    results.push(...youtubeResults);
                } catch (error) {
                    console.warn('YouTube API failed, using sample data:', error);
                    // Fallback to sample data
                    const sampleResults = this.searchSampleData(query);
                    results.push(...sampleResults);
                }
            } else {
                // Use sample data when no API key
                const sampleResults = this.searchSampleData(query);
                results.push(...sampleResults);
            }
            
            // Search JioSaavn (placeholder)
            if (sources.includes('jiosaavn')) {
                const jiosaavnResults = await this.searchJioSaavn(query);
                results.push(...jiosaavnResults);
            }
            
            return results;
        } catch (error) {
            console.error('Music search failed:', error);
            // Return sample data as ultimate fallback
            return this.searchSampleData(query);
        }
    }
    
    searchSampleData(query) {
        const allSongs = [
            ...this.sampleData.trending,
            ...this.sampleData.bollywood, 
            ...this.sampleData.international
        ];
        
        if (!query) return allSongs.slice(0, 10);
        
        const searchTerm = query.toLowerCase();
        return allSongs.filter(song => 
            song.title.toLowerCase().includes(searchTerm) ||
            song.artist.toLowerCase().includes(searchTerm) ||
            song.album.toLowerCase().includes(searchTerm)
        ).slice(0, 10);
    }

    // Get trending music
    async getTrendingMusic() {
        try {
            // Always use sample data for demo to avoid API errors
            return this.sampleData?.trending || [];
        } catch (error) {
            console.error('Error getting trending music:', error);
            return [];
        }
    }

    async getBollywoodHits() {
        try {
            // Always use sample data for demo to avoid API errors
            return this.sampleData?.bollywood || [];
        } catch (error) {
            console.error('Error getting Bollywood music:', error);
            return [];
        }
    }

    async getInternationalMusic() {
        try {
            // Always use sample data for demo to avoid API errors
            return this.sampleData?.international || [];
        } catch (error) {
            console.error('Error getting international music:', error);
            return [];
        }
    }

    getSampleYouTubeData(query) {
        return [
            {
                id: 'sample_yt_1',
                title: `${query} - Sample Song 1`,
                artist: 'Sample Artist',
                thumbnail: 'https://via.placeholder.com/300x300?text=YouTube+Sample',
                duration: '3:45',
                source: 'youtube'
            }
        ];
    }

    getSampleJioSaavnData(query) {
        return [
            {
                id: 'sample_js_1',
                title: `${query} - Sample Hindi Song`,
                artist: 'Sample Hindi Artist',
                thumbnail: 'https://via.placeholder.com/300x300?text=JioSaavn+Sample',
                duration: '4:12',
                source: 'jiosaavn'
            }
        ];
    }

    getSampleMixedData(query) {
        return [
            ...this.getSampleYouTubeData(query),
            ...this.getSampleJioSaavnData(query)
        ];
    }
}

// Initialize music API manager
window.musicAPI = new MusicAPIManager();
