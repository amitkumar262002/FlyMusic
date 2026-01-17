// Spotify Web API Integration for Saathi Music
class SpotifyAPI {
    constructor() {
        // Spotify API Configuration
        this.clientId = this.getClientId();
        this.clientSecret = null; // Never expose client secret in frontend
        this.redirectUri = window.location.origin + '/callback';
        this.baseUrl = window.SPOTIFY_CONFIG?.API_ENDPOINTS?.API || 'https://api.spotify.com/v1';
        this.authUrl = window.SPOTIFY_CONFIG?.API_ENDPOINTS?.AUTH || 'https://accounts.spotify.com/authorize';
        this.tokenUrl = window.SPOTIFY_CONFIG?.API_ENDPOINTS?.TOKEN || 'https://accounts.spotify.com/api/token';
        
        // Required scopes for music app
        this.scopes = window.SPOTIFY_CONFIG?.SCOPES || [
            'user-read-private',
            'user-read-email',
            'playlist-read-private',
            'playlist-read-collaborative',
            'user-library-read',
            'user-top-read',
            'user-read-recently-played',
            'streaming',
            'user-modify-playback-state',
            'user-read-playback-state'
        ];
        
        // Token management
        this.accessToken = this.getStoredToken();
        this.refreshToken = this.getStoredRefreshToken();
        this.tokenExpiry = this.getStoredTokenExpiry();
        
        // Client credentials token (for public data)
        this.clientCredentialsToken = this.getStoredClientCredentialsToken();
        this.clientCredentialsExpiry = this.getStoredClientCredentialsExpiry();
        
        // Cache for API responses
        this.cache = new Map();
        this.cacheExpiry = 5 * 60 * 1000; // 5 minutes
        
        // Rate limiting
        this.requestQueue = [];
        this.isProcessingQueue = false;
        this.requestsPerMinute = 100;
        this.requestCount = 0;
        this.requestResetTime = Date.now() + 60000;
        
        this.init();
        window.spotifyAPI = this;
    }
    
    init() {
        console.log('🎵 Spotify API initialized');
        
        // Check if we have a valid user token
        if (this.accessToken && !this.isTokenExpired()) {
            console.log('✅ Valid Spotify user token found');
            this.testConnection();
        } else if (this.refreshToken) {
            console.log('🔄 Refreshing Spotify user token...');
            this.refreshAccessToken();
        }
        
        // Get client credentials token for public data
        if (!this.clientCredentialsToken || this.isClientCredentialsExpired()) {
            console.log('🔄 Getting client credentials token...');
            this.getClientCredentialsToken();
        } else {
            console.log('✅ Valid client credentials token found');
        }
        
        // Handle OAuth callback
        this.handleOAuthCallback();
    }
    
    // Configuration methods
    getClientId() {
        // Try to get from localStorage first, then config, then default
        return localStorage.getItem('spotify_client_id') || 
               window.SPOTIFY_CONFIG?.CLIENT_ID || 
               '2fbdff1cc8254acab1cee5ee1b3045ba'; // Your Spotify Client ID
    }
    
    setClientId(clientId) {
        this.clientId = clientId;
        localStorage.setItem('spotify_client_id', clientId);
        console.log('✅ Spotify Client ID updated');
    }
    
    // Token management
    getStoredToken() {
        return localStorage.getItem('spotify_access_token');
    }
    
    getStoredRefreshToken() {
        return localStorage.getItem('spotify_refresh_token');
    }
    
    getStoredTokenExpiry() {
        const expiry = localStorage.getItem('spotify_token_expiry');
        return expiry ? parseInt(expiry) : 0;
    }
    
    // Client credentials token management
    getStoredClientCredentialsToken() {
        return localStorage.getItem('spotify_client_credentials_token');
    }
    
    getStoredClientCredentialsExpiry() {
        const expiry = localStorage.getItem('spotify_client_credentials_expiry');
        return expiry ? parseInt(expiry) : 0;
    }
    
    isClientCredentialsExpired() {
        return Date.now() >= this.clientCredentialsExpiry;
    }
    
    storeClientCredentialsToken(accessToken, expiresIn) {
        this.clientCredentialsToken = accessToken;
        this.clientCredentialsExpiry = Date.now() + (expiresIn * 1000);
        
        localStorage.setItem('spotify_client_credentials_token', accessToken);
        localStorage.setItem('spotify_client_credentials_expiry', this.clientCredentialsExpiry.toString());
    }
    
    isTokenExpired() {
        return Date.now() >= this.tokenExpiry;
    }
    
    storeTokens(accessToken, refreshToken, expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiry = Date.now() + (expiresIn * 1000);
        
        localStorage.setItem('spotify_access_token', accessToken);
        if (refreshToken) {
            localStorage.setItem('spotify_refresh_token', refreshToken);
        }
        localStorage.setItem('spotify_token_expiry', this.tokenExpiry.toString());
    }
    
    clearTokens() {
        this.accessToken = null;
        this.refreshToken = null;
        this.tokenExpiry = 0;
        
        localStorage.removeItem('spotify_access_token');
        localStorage.removeItem('spotify_refresh_token');
        localStorage.removeItem('spotify_token_expiry');
    }
    
    clearAllTokens() {
        // Clear user tokens
        this.clearTokens();
        
        // Clear client credentials tokens
        this.clientCredentialsToken = null;
        this.clientCredentialsExpiry = 0;
        
        localStorage.removeItem('spotify_client_credentials_token');
        localStorage.removeItem('spotify_client_credentials_expiry');
    }
    
    // Authentication methods
    getAuthUrl(useImplicitFlow = false) {
        const state = this.generateRandomString(16);
        localStorage.setItem('spotify_auth_state', state);
        
        const params = new URLSearchParams({
            client_id: this.clientId,
            response_type: useImplicitFlow ? 'token' : 'code',
            redirect_uri: this.redirectUri,
            scope: this.scopes.join(' '),
            state: state,
            show_dialog: 'true'
        });
        
        return `${this.authUrl}?${params.toString()}`;
    }
    
    // Generate random string for state parameter
    generateRandomString(length) {
        const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        let text = '';
        for (let i = 0; i < length; i++) {
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }
        return text;
    }
    
    login(useImplicitFlow = false) {
        if (!this.clientId || this.clientId === 'YOUR_SPOTIFY_CLIENT_ID') {
            this.showToast('Please configure Spotify Client ID first', 'error');
            return;
        }
        
        const authUrl = this.getAuthUrl(useImplicitFlow);
        console.log(`🔄 Starting ${useImplicitFlow ? 'Implicit' : 'Authorization Code'} flow...`);
        window.location.href = authUrl;
    }
    
    // Login with implicit flow (simpler, token in URL)
    loginImplicit() {
        this.login(true);
    }
    
    logout() {
        this.clearTokens();
        this.showToast('Logged out from Spotify', 'info');
        
        // Refresh the page to update UI
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }
    
    async handleOAuthCallback() {
        // Handle Authorization Code flow (from query parameters)
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');
        const error = urlParams.get('error');
        
        if (error) {
            console.error('Spotify OAuth error:', error);
            this.showToast('Spotify authentication failed', 'error');
            return;
        }
        
        if (code) {
            console.log('🔄 Processing Authorization Code flow callback...');
            await this.exchangeCodeForToken(code);
            
            // Clean up URL
            window.history.replaceState({}, document.title, window.location.pathname);
            return;
        }
        
        // Handle Implicit Grant flow (from URL fragment)
        this.handleImplicitCallback();
    }
    
    handleImplicitCallback() {
        const hash = window.location.hash.substring(1);
        if (!hash) return;
        
        const params = new URLSearchParams(hash);
        const accessToken = params.get('access_token');
        const tokenType = params.get('token_type');
        const expiresIn = params.get('expires_in');
        const state = params.get('state');
        const error = params.get('error');
        
        if (error) {
            console.error('Spotify Implicit Grant error:', error);
            this.showToast('Spotify authentication failed', 'error');
            return;
        }
        
        // Verify state parameter
        const storedState = localStorage.getItem('spotify_auth_state');
        if (state !== storedState) {
            console.error('State mismatch in OAuth callback');
            this.showToast('Authentication state mismatch', 'error');
            return;
        }
        
        if (accessToken && tokenType === 'Bearer') {
            console.log('🔄 Processing Implicit Grant callback...');
            
            // Store the token (no refresh token in implicit flow)
            this.storeTokens(accessToken, null, parseInt(expiresIn));
            
            console.log('✅ Spotify Implicit Grant authentication successful');
            this.showToast('Connected to Spotify successfully!', 'success');
            
            // Test the connection
            this.testConnection();
            
            // Clean up URL and state
            window.history.replaceState({}, document.title, window.location.pathname);
            localStorage.removeItem('spotify_auth_state');
        }
    }
    
    async exchangeCodeForToken(code) {
        try {
            // Note: In production, this should be done on your backend server
            // to keep the client secret secure
            const response = await fetch(this.tokenUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    grant_type: 'authorization_code',
                    code: code,
                    redirect_uri: this.redirectUri,
                    client_id: this.clientId,
                    // client_secret: this.clientSecret // This should be handled server-side
                })
            });
            
            if (!response.ok) {
                throw new Error('Token exchange failed');
            }
            
            const data = await response.json();
            this.storeTokens(data.access_token, data.refresh_token, data.expires_in);
            
            console.log('✅ Spotify authentication successful');
            this.showToast('Connected to Spotify successfully!', 'success');
            
            // Test the connection
            await this.testConnection();
            
        } catch (error) {
            console.error('Error exchanging code for token:', error);
            this.showToast('Failed to connect to Spotify', 'error');
        }
    }
    
    async refreshAccessToken() {
        if (!this.refreshToken) {
            console.log('No refresh token available');
            return false;
        }
        
        try {
            // Note: This should also be done server-side in production
            const response = await fetch(this.tokenUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    grant_type: 'refresh_token',
                    refresh_token: this.refreshToken,
                    client_id: this.clientId,
                    // client_secret: this.clientSecret // Server-side only
                })
            });
            
            if (!response.ok) {
                throw new Error('Token refresh failed');
            }
            
            const data = await response.json();
            this.storeTokens(data.access_token, data.refresh_token || this.refreshToken, data.expires_in);
            
            console.log('✅ Spotify token refreshed');
            return true;
            
        } catch (error) {
            console.error('Error refreshing token:', error);
            this.clearTokens();
            return false;
        }
    }
    
    // Get client credentials token for public data access
    async getClientCredentialsToken() {
        try {
            const clientSecret = window.SPOTIFY_CONFIG?.CLIENT_SECRET || '12b3178d021848bcb151a69969903ff3';
            
            const response = await fetch(this.tokenUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Authorization': 'Basic ' + btoa(this.clientId + ':' + clientSecret)
                },
                body: new URLSearchParams({
                    grant_type: 'client_credentials'
                })
            });
            
            if (!response.ok) {
                throw new Error('Client credentials token request failed');
            }
            
            const data = await response.json();
            this.storeClientCredentialsToken(data.access_token, data.expires_in);
            
            console.log('✅ Client credentials token acquired');
            return true;
            
        } catch (error) {
            console.error('Error getting client credentials token:', error);
            return false;
        }
    }
    
    // API request methods
    async makeRequest(endpoint, options = {}) {
        const requiresUserAuth = this.requiresUserAuthentication(endpoint);
        let token = null;
        
        if (requiresUserAuth) {
            // Use user token for personal data
            if (this.isTokenExpired() && this.refreshToken) {
                await this.refreshAccessToken();
            }
            
            if (!this.accessToken) {
                throw new Error('User authentication required for this endpoint');
            }
            token = this.accessToken;
        } else {
            // Use client credentials token for public data
            if (this.isClientCredentialsExpired()) {
                await this.getClientCredentialsToken();
            }
            
            if (!this.clientCredentialsToken) {
                throw new Error('No valid Spotify access token');
            }
            token = this.clientCredentialsToken;
        }
        
        // Rate limiting
        await this.waitForRateLimit();
        
        const url = endpoint.startsWith('http') ? endpoint : `${this.baseUrl}${endpoint}`;
        
        const config = {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };
        
        const response = await fetch(url, config);
        
        if (response.status === 401) {
            // Token expired, try to refresh
            if (requiresUserAuth && await this.refreshAccessToken()) {
                // Retry the request with new user token
                config.headers['Authorization'] = `Bearer ${this.accessToken}`;
                return fetch(url, config);
            } else if (!requiresUserAuth && await this.getClientCredentialsToken()) {
                // Retry the request with new client credentials token
                config.headers['Authorization'] = `Bearer ${this.clientCredentialsToken}`;
                return fetch(url, config);
            } else {
                throw new Error('Authentication failed');
            }
        }
        
        if (!response.ok) {
            throw new Error(`Spotify API error: ${response.status} ${response.statusText}`);
        }
        
        return response.json();
    }
    
    async waitForRateLimit() {
        const now = Date.now();
        
        if (now >= this.requestResetTime) {
            this.requestCount = 0;
            this.requestResetTime = now + 60000;
        }
        
        if (this.requestCount >= this.requestsPerMinute) {
            const waitTime = this.requestResetTime - now;
            console.log(`⏳ Rate limit reached, waiting ${waitTime}ms`);
            await new Promise(resolve => setTimeout(resolve, waitTime));
            this.requestCount = 0;
            this.requestResetTime = Date.now() + 60000;
        }
        
        this.requestCount++;
    }
    
    // Music data methods
    async searchTracks(query, limit = 20) {
        try {
            const cacheKey = `search_${query}_${limit}`;
            if (this.cache.has(cacheKey)) {
                const cached = this.cache.get(cacheKey);
                if (Date.now() - cached.timestamp < this.cacheExpiry) {
                    return cached.data;
                }
            }
            
            const params = new URLSearchParams({
                q: query,
                type: 'track',
                limit: limit.toString(),
                market: 'US'
            });
            
            const data = await this.makeRequest(`/search?${params}`);
            const tracks = this.formatTracks(data.tracks.items);
            
            // Cache the results
            this.cache.set(cacheKey, {
                data: tracks,
                timestamp: Date.now()
            });
            
            return tracks;
            
        } catch (error) {
            console.error('Error searching tracks:', error);
            return [];
        }
    }
    
    async getTopTracks(limit = 20, timeRange = 'medium_term') {
        try {
            const cacheKey = `top_tracks_${timeRange}_${limit}`;
            if (this.cache.has(cacheKey)) {
                const cached = this.cache.get(cacheKey);
                if (Date.now() - cached.timestamp < this.cacheExpiry) {
                    return cached.data;
                }
            }
            
            const params = new URLSearchParams({
                limit: limit.toString(),
                time_range: timeRange
            });
            
            const data = await this.makeRequest(`/me/top/tracks?${params}`);
            const tracks = this.formatTracks(data.items);
            
            this.cache.set(cacheKey, {
                data: tracks,
                timestamp: Date.now()
            });
            
            return tracks;
            
        } catch (error) {
            console.error('Error getting top tracks:', error);
            return [];
        }
    }
    
    async getNewReleases(limit = 20) {
        try {
            const cacheKey = `new_releases_${limit}`;
            if (this.cache.has(cacheKey)) {
                const cached = this.cache.get(cacheKey);
                if (Date.now() - cached.timestamp < this.cacheExpiry) {
                    return cached.data;
                }
            }
            
            const params = new URLSearchParams({
                limit: limit.toString(),
                country: 'US'
            });
            
            const data = await this.makeRequest(`/browse/new-releases?${params}`);
            const albums = data.albums.items;
            
            // Get tracks from albums
            const tracks = [];
            for (const album of albums.slice(0, limit)) {
                const albumTracks = await this.getAlbumTracks(album.id, 1);
                if (albumTracks.length > 0) {
                    tracks.push({
                        ...albumTracks[0],
                        album: album.name,
                        thumbnail: album.images[0]?.url
                    });
                }
            }
            
            this.cache.set(cacheKey, {
                data: tracks,
                timestamp: Date.now()
            });
            
            return tracks;
            
        } catch (error) {
            console.error('Error getting new releases:', error);
            return [];
        }
    }
    
    async getAlbumTracks(albumId, limit = 10) {
        try {
            const params = new URLSearchParams({
                limit: limit.toString(),
                market: 'US'
            });
            
            const data = await this.makeRequest(`/albums/${albumId}/tracks?${params}`);
            return this.formatTracks(data.items);
            
        } catch (error) {
            console.error('Error getting album tracks:', error);
            return [];
        }
    }
    
    async getFeaturedPlaylists(limit = 10) {
        try {
            const cacheKey = `featured_playlists_${limit}`;
            if (this.cache.has(cacheKey)) {
                const cached = this.cache.get(cacheKey);
                if (Date.now() - cached.timestamp < this.cacheExpiry) {
                    return cached.data;
                }
            }
            
            const params = new URLSearchParams({
                limit: limit.toString(),
                country: 'US'
            });
            
            const data = await this.makeRequest(`/browse/featured-playlists?${params}`);
            const playlists = data.playlists.items.map(playlist => ({
                id: playlist.id,
                name: playlist.name,
                description: playlist.description,
                image: playlist.images[0]?.url,
                trackCount: playlist.tracks.total,
                owner: playlist.owner.display_name
            }));
            
            this.cache.set(cacheKey, {
                data: playlists,
                timestamp: Date.now()
            });
            
            return playlists;
            
        } catch (error) {
            console.error('Error getting featured playlists:', error);
            return [];
        }
    }
    
    // Get artist information (uses client credentials - no user auth required)
    async getArtist(artistId) {
        try {
            const cacheKey = `artist_${artistId}`;
            if (this.cache.has(cacheKey)) {
                const cached = this.cache.get(cacheKey);
                if (Date.now() - cached.timestamp < this.cacheExpiry) {
                    return cached.data;
                }
            }
            
            const data = await this.makeRequest(`/artists/${artistId}`);
            const artist = {
                id: data.id,
                name: data.name,
                genres: data.genres,
                popularity: data.popularity,
                followers: data.followers.total,
                images: data.images,
                spotifyUrl: data.external_urls.spotify,
                uri: data.uri
            };
            
            this.cache.set(cacheKey, {
                data: artist,
                timestamp: Date.now()
            });
            
            return artist;
            
        } catch (error) {
            console.error('Error getting artist:', error);
            return null;
        }
    }
    
    // Get multiple artists (uses client credentials)
    async getArtists(artistIds) {
        try {
            const params = new URLSearchParams({
                ids: artistIds.join(',')
            });
            
            const data = await this.makeRequest(`/artists?${params}`);
            return data.artists.map(artist => ({
                id: artist.id,
                name: artist.name,
                genres: artist.genres,
                popularity: artist.popularity,
                followers: artist.followers.total,
                images: artist.images,
                spotifyUrl: artist.external_urls.spotify,
                uri: artist.uri
            }));
            
        } catch (error) {
            console.error('Error getting artists:', error);
            return [];
        }
    }
    
    // Determine if endpoint requires user authentication
    requiresUserAuthentication(endpoint) {
        const userEndpoints = [
            '/me',
            '/me/top',
            '/me/tracks',
            '/me/albums',
            '/me/playlists',
            '/me/following',
            '/me/player'
        ];
        
        return userEndpoints.some(userEndpoint => endpoint.includes(userEndpoint));
    }
    
    // Enhanced connection status
    getConnectionStatus() {
        const hasUserAuth = this.accessToken && !this.isTokenExpired();
        const hasClientAuth = this.clientCredentialsToken && !this.isClientCredentialsExpired();
        
        return {
            userAuthenticated: hasUserAuth,
            publicDataAccess: hasClientAuth,
            fullyConnected: hasUserAuth,
            canSearchTracks: hasClientAuth || hasUserAuth,
            canAccessPersonalData: hasUserAuth
        };
    }
    
    // Utility methods
    formatTracks(spotifyTracks) {
        return spotifyTracks.map(track => ({
            id: `spotify_${track.id}`,
            title: track.name,
            artist: track.artists.map(artist => artist.name).join(', '),
            album: track.album?.name || 'Unknown Album',
            thumbnail: track.album?.images[0]?.url || 'https://via.placeholder.com/300x300?text=Music',
            duration: this.formatDuration(track.duration_ms),
            audioUrl: track.preview_url, // 30-second preview
            source: 'spotify',
            year: track.album?.release_date ? new Date(track.album.release_date).getFullYear().toString() : '2023',
            language: 'English', // Spotify doesn't provide language info
            genre: 'Pop', // Would need additional API call to get genres
            playCount: track.popularity * 10000, // Approximate based on popularity
            likes: track.popularity * 1000,
            spotifyId: track.id,
            spotifyUrl: track.external_urls.spotify,
            explicit: track.explicit
        }));
    }
    
    formatDuration(milliseconds) {
        const minutes = Math.floor(milliseconds / 60000);
        const seconds = Math.floor((milliseconds % 60000) / 1000);
        return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    }
    
    async testConnection() {
        try {
            // Test user connection if available
            if (this.accessToken && !this.isTokenExpired()) {
                const data = await this.makeRequest('/me');
                console.log(`✅ Connected to Spotify as: ${data.display_name}`);
                this.showToast(`Connected as ${data.display_name}`, 'success');
                return true;
            }
            
            // Test public data access with client credentials
            if (this.hasPublicAccess()) {
                // Test with a known artist (Radiohead - from your example)
                const artist = await this.getArtist('4Z8W4fKeB5YxbusRsdQVPb');
                if (artist) {
                    console.log(`✅ Spotify public data access working - Test artist: ${artist.name}`);
                    this.showToast('Spotify public data access active', 'success');
                    return true;
                }
            }
            
            throw new Error('No valid Spotify connection');
            
        } catch (error) {
            console.error('Spotify connection test failed:', error);
            this.showToast('Spotify connection test failed', 'error');
            return false;
        }
    }
    
    isConnected() {
        const status = this.getConnectionStatus();
        return status.canSearchTracks; // Can access Spotify data in some form
    }
    
    isFullyConnected() {
        return this.accessToken && !this.isTokenExpired();
    }
    
    hasPublicAccess() {
        return this.clientCredentialsToken && !this.isClientCredentialsExpired();
    }
    
    showToast(message, type = 'info') {
        if (window.showToast) {
            window.showToast(message, type);
        } else {
            console.log(`${type.toUpperCase()}: ${message}`);
        }
    }
}

// Initialize Spotify API
document.addEventListener('DOMContentLoaded', () => {
    window.spotifyAPI = new SpotifyAPI();
});

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = SpotifyAPI;
}
