// Spotify Configuration for Saathi Music
// Note: In production, these should be environment variables

const SPOTIFY_CONFIG = {
    CLIENT_ID: '2fbdff1cc8254acab1cee5ee1b3045ba',
    // Note: Client Secret should NEVER be exposed in frontend code
    // This is for reference only - handle authentication server-side in production
    CLIENT_SECRET: '12b3178d021848bcb151a69969903ff3',
    
    REDIRECT_URIS: {
        development: [
            'http://localhost:3000/callback',
            'http://127.0.0.1:3000/callback'
        ],
        production: [
            'https://your-domain.com/callback',
            'https://your-domain.com/auth/spotify/callback'
        ]
    },
    
    SCOPES: [
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
    ],
    
    API_ENDPOINTS: {
        AUTH: 'https://accounts.spotify.com/authorize',
        TOKEN: 'https://accounts.spotify.com/api/token',
        API: 'https://api.spotify.com/v1'
    }
};

// Auto-configure Spotify API if available
if (window.spotifyAPI) {
    window.spotifyAPI.clientId = SPOTIFY_CONFIG.CLIENT_ID;
    console.log('✅ Spotify API configured with Client ID');
}

// Export for use in other files
window.SPOTIFY_CONFIG = SPOTIFY_CONFIG;

// For Node.js environments
if (typeof module !== 'undefined' && module.exports) {
    module.exports = SPOTIFY_CONFIG;
}
