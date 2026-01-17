# Spotify API Setup Guide for Saathi Music

## 🎵 Overview
This guide will help you integrate Spotify Web API into your Saathi Music app to access millions of songs, playlists, and artist information.

## 📋 Prerequisites
- Spotify account (free or premium)
- Web development knowledge
- HTTPS domain (required for production)

## 🚀 Step 1: Create Spotify App

### 1.1 Go to Spotify Developer Dashboard
- Visit: https://developer.spotify.com/dashboard
- Log in with your Spotify account

### 1.2 Create New App
1. Click **"Create App"**
2. Fill in the details:
   - **App Name**: `Saathi Music`
   - **App Description**: `AI-powered music streaming platform`
   - **Website**: `https://your-domain.com` (or localhost for development)
   - **Redirect URI**: `https://your-domain.com/callback` (or `http://localhost:3000/callback`)
3. Check the boxes for:
   - ✅ Web API
   - ✅ Web Playback SDK (if you want to play music)
4. Click **"Save"**

### 1.3 Get Your Credentials
After creating the app, you'll get:
- **Client ID**: `2fbdff1cc8254acab1cee5ee1b3045ba` ✅
- **Client Secret**: `12b3178d021848bcb151a69969903ff3` ✅ (keep this secret!)

## 🔧 Step 2: Configure API Settings

### 2.1 Set Redirect URIs
In your Spotify app settings, add these redirect URIs:
```
Development:
http://localhost:3000/callback
http://127.0.0.1:3000/callback

Production:
https://your-domain.com/callback
https://your-domain.com/auth/spotify/callback
```

### 2.2 API Scopes Required
For Saathi Music, you'll need these scopes:
- `user-read-private` - Read user profile
- `user-read-email` - Read user email
- `playlist-read-private` - Read private playlists
- `playlist-read-collaborative` - Read collaborative playlists
- `user-library-read` - Read user's saved tracks
- `user-top-read` - Read user's top tracks and artists
- `user-read-recently-played` - Read recently played tracks
- `streaming` - Play music (requires Spotify Premium)
- `user-modify-playback-state` - Control playback
- `user-read-playback-state` - Read playback state

## 🛠️ Step 3: Implementation

### 3.1 Environment Variables
Create a `.env` file in your project root:
```env
SPOTIFY_CLIENT_ID=2fbdff1cc8254acab1cee5ee1b3045ba
SPOTIFY_CLIENT_SECRET=12b3178d021848bcb151a69969903ff3
SPOTIFY_REDIRECT_URI=http://localhost:3000/callback
```
✅ **Already created for you!**

### 3.2 Install Dependencies (if using Node.js backend)
```bash
npm install axios dotenv
```

## 🔐 Step 4: Authentication Flow

### 4.1 Authorization URL
```javascript
const SPOTIFY_AUTH_URL = 'https://accounts.spotify.com/authorize';
const CLIENT_ID = '2fbdff1cc8254acab1cee5ee1b3045ba'; // Your actual Client ID
const REDIRECT_URI = 'http://localhost:3000/callback';
const SCOPES = [
    'user-read-private',
    'user-read-email',
    'playlist-read-private',
    'user-library-read',
    'user-top-read',
    'streaming'
].join(' ');

const authUrl = `${SPOTIFY_AUTH_URL}?client_id=${CLIENT_ID}&response_type=code&redirect_uri=${encodeURIComponent(REDIRECT_URI)}&scope=${encodeURIComponent(SCOPES)}`;
```

### 4.2 Token Exchange
After user authorization, exchange the code for access token:
```javascript
const tokenUrl = 'https://accounts.spotify.com/api/token';
const response = await fetch(tokenUrl, {
    method: 'POST',
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': `Basic ${btoa(CLIENT_ID + ':' + CLIENT_SECRET)}`
    },
    body: new URLSearchParams({
        grant_type: 'authorization_code',
        code: authorizationCode,
        redirect_uri: REDIRECT_URI
    })
});
```

## 📊 Step 5: API Endpoints

### 5.1 Popular Endpoints for Music App
```javascript
// Get user's top tracks
GET https://api.spotify.com/v1/me/top/tracks

// Search for tracks
GET https://api.spotify.com/v1/search?q=query&type=track

// Get track details
GET https://api.spotify.com/v1/tracks/{id}

// Get artist details
GET https://api.spotify.com/v1/artists/{id}

// Get playlist tracks
GET https://api.spotify.com/v1/playlists/{playlist_id}/tracks

// Get featured playlists
GET https://api.spotify.com/v1/browse/featured-playlists

// Get new releases
GET https://api.spotify.com/v1/browse/new-releases
```

## 🎵 Step 6: Web Playback SDK (Optional)

### 6.1 Include Spotify Web Playback SDK
```html
<script src="https://sdk.scdn.co/spotify-player.js"></script>
```

### 6.2 Initialize Player
```javascript
window.onSpotifyWebPlaybackSDKReady = () => {
    const player = new Spotify.Player({
        name: 'Saathi Music Player',
        getOAuthToken: cb => { cb(access_token); },
        volume: 0.5
    });

    player.connect();
};
```

## 🔒 Step 7: Security Best Practices

### 7.1 Client-Side Security
- ✅ Never expose Client Secret in frontend code
- ✅ Use PKCE (Proof Key for Code Exchange) for public clients
- ✅ Implement token refresh logic
- ✅ Store tokens securely (httpOnly cookies recommended)

### 7.2 Rate Limiting
- Spotify allows 100 requests per minute per app
- Implement caching to reduce API calls
- Use batch requests when possible

## 🚨 Step 8: Important Limitations

### 8.1 Playback Limitations
- **Full track playback**: Requires Spotify Premium subscription
- **30-second previews**: Available for all users
- **Web Playback SDK**: Only works with Premium accounts

### 8.2 Content Restrictions
- Some tracks may not be available in all regions
- Explicit content filtering may apply
- Copyright restrictions vary by country

## 🧪 Step 9: Testing

### 9.1 Test with Spotify's Web Console
- Visit: https://developer.spotify.com/console/
- Test API endpoints with your credentials
- Verify scopes and permissions

### 9.2 Development Mode
- Your app starts in "Development Mode"
- Only 25 users can use your app
- Request quota extension for production

## 📈 Step 10: Production Deployment

### 10.1 Quota Extension Request
1. Go to your Spotify app dashboard
2. Click "Request Extension"
3. Fill out the form with:
   - App description
   - Use case explanation
   - Expected user count
   - Screenshots of your app

### 10.2 Production Checklist
- ✅ HTTPS enabled
- ✅ Proper error handling
- ✅ Token refresh implemented
- ✅ Rate limiting handled
- ✅ User privacy respected
- ✅ Terms of service compliance

## 🔧 Troubleshooting

### Common Issues:
1. **Invalid Redirect URI**: Ensure exact match in dashboard
2. **Scope Issues**: Check if requested scopes are approved
3. **CORS Errors**: Use server-side proxy for API calls
4. **Token Expiry**: Implement automatic token refresh
5. **Rate Limiting**: Add request queuing and retry logic

## 📚 Additional Resources

- [Spotify Web API Documentation](https://developer.spotify.com/documentation/web-api/)
- [Spotify Web Playback SDK](https://developer.spotify.com/documentation/web-playback-sdk/)
- [Authorization Guide](https://developer.spotify.com/documentation/general/guides/authorization/)
- [Best Practices](https://developer.spotify.com/documentation/general/guides/app-settings/)

## 💡 Next Steps

After completing this setup:
1. Implement the Spotify API integration in your app
2. Add user authentication flow
3. Create music search and discovery features
4. Implement playlist management
5. Add music playback (with Premium limitations)

---

**Note**: Remember that Spotify's terms of service require proper attribution and compliance with their brand guidelines. Always respect user privacy and implement proper error handling for the best user experience.
