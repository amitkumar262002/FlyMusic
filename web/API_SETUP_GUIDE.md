# API Setup Guide for FlyMusic AI 🔧

This guide will help you set up the required APIs for FlyMusic AI to work with real music data.

## 🔥 Firebase Setup (Required)

### Step 1: Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or "Add project"
3. Enter project name: `fly-music-ai` (or your preferred name)
4. Enable Google Analytics (optional)
5. Click "Create project"

### Step 2: Enable Authentication
1. In Firebase Console, go to "Authentication"
2. Click "Get started"
3. Go to "Sign-in method" tab
4. Enable "Email/Password" provider
5. Enable "Google" provider
   - Add your domain to authorized domains
   - Configure OAuth consent screen if needed

### Step 3: Enable Firestore Database
1. Go to "Firestore Database"
2. Click "Create database"
3. Choose "Start in test mode" (for development)
4. Select a location close to your users
5. Click "Done"

### Step 4: Get Firebase Configuration
1. Go to Project Settings (gear icon)
2. Scroll down to "Your apps"
3. Click "Web" icon (</>) to add web app
4. Register app with nickname: "FlyMusic AI Web"
5. Copy the configuration object
6. The configuration is already set in `firebase-config.js` with your provided values

### Step 5: Set up Hosting (Optional)
1. Go to "Hosting" in Firebase Console
2. Click "Get started"
3. Follow the setup instructions
4. Your domain will be: `your-project-id.web.app`

## 🎵 YouTube Data API v3 Setup

### Step 1: Create Google Cloud Project
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable billing (required for API usage)

### Step 2: Enable YouTube Data API
1. Go to "APIs & Services" > "Library"
2. Search for "YouTube Data API v3"
3. Click on it and press "Enable"

### Step 3: Create API Key
1. Go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "API Key"
3. Copy the API key
4. Click "Restrict Key" for security
5. Under "API restrictions", select "YouTube Data API v3"
6. Under "Website restrictions", add your domains:
   - `localhost:3000`
   - `127.0.0.1:3000`
   - Your production domain

### Step 4: Configure in Code
1. Open `music-api.js`
2. Replace `YOUR_YOUTUBE_API_KEY` with your actual API key:
```javascript
this.youtubeApiKey = 'YOUR_ACTUAL_API_KEY_HERE';
```

### API Quotas and Limits
- **Free Quota**: 10,000 units per day
- **Search**: 100 units per request
- **Video details**: 1 unit per video
- **Monitor usage** in Google Cloud Console

## 🎶 JioSaavn API Setup

### Option 1: Official API (Recommended but Limited)
JioSaavn doesn't provide a public API. The app currently uses an unofficial API endpoint.

### Option 2: Unofficial API (Current Implementation)
The app uses `https://saavn.me/api` which is an unofficial proxy.

**Note**: Unofficial APIs may have limitations:
- Rate limiting
- Potential downtime
- Terms of service concerns

### Alternative Solutions
1. **Spotify Web API**: More reliable, requires app registration
2. **Apple Music API**: Requires Apple Developer account
3. **Deezer API**: Free tier available
4. **Last.fm API**: Good for metadata and recommendations

## 🔐 Security Best Practices

### API Key Security
1. **Never commit API keys** to version control
2. Use environment variables in production
3. Implement domain restrictions
4. Monitor API usage regularly
5. Rotate keys periodically

### Firebase Security
1. Configure Firestore security rules (already provided)
2. Enable App Check for production
3. Set up proper CORS policies
4. Monitor authentication logs

## 🚀 Environment Configuration

### Development Environment
Create a `.env` file (not committed to git):
```env
YOUTUBE_API_KEY=your_youtube_api_key_here
FIREBASE_API_KEY=your_firebase_api_key_here
JIOSAAVN_API_URL=https://saavn.me/api
```

### Production Environment
Set environment variables in your hosting platform:
- **Netlify**: Site settings > Environment variables
- **Vercel**: Project settings > Environment variables
- **Firebase Hosting**: Use Firebase functions for API keys

## 📊 API Usage Monitoring

### YouTube API
1. Go to Google Cloud Console
2. Navigate to "APIs & Services" > "Quotas"
3. Monitor daily usage
4. Set up alerts for quota limits

### Firebase
1. Go to Firebase Console
2. Check "Usage" tab for each service
3. Monitor authentication, Firestore, and hosting usage

## 🔧 Troubleshooting

### Common Issues

#### YouTube API Errors
- **403 Forbidden**: Check API key restrictions
- **Quota Exceeded**: Wait for quota reset or upgrade plan
- **Invalid API Key**: Verify key is correct and enabled

#### Firebase Errors
- **Permission Denied**: Check Firestore security rules
- **Network Error**: Verify Firebase config
- **Auth Domain**: Add domain to authorized domains

#### CORS Issues
- Use a local server instead of opening HTML directly
- Configure proper CORS headers
- Check browser console for specific errors

### Debug Mode
Enable debug mode by adding to console:
```javascript
// Enable Firebase debug mode
firebase.firestore.enableNetwork();
firebase.auth().useDeviceLanguage();

// Enable verbose logging
localStorage.debug = 'firebase:*';
```

## 📈 Scaling Considerations

### For Production Use
1. **Implement caching** to reduce API calls
2. **Use CDN** for static assets
3. **Optimize images** and thumbnails
4. **Implement pagination** for large result sets
5. **Add error retry logic**
6. **Monitor performance** and costs

### Alternative Architecture
For high-traffic applications, consider:
1. **Backend API** to proxy music services
2. **Database caching** of popular content
3. **User-based recommendations** using ML
4. **Content delivery network** for media files

## 📞 Support

### Getting Help
- **Firebase**: [Firebase Support](https://firebase.google.com/support)
- **YouTube API**: [Google Cloud Support](https://cloud.google.com/support)
- **Community**: Stack Overflow with relevant tags

### Documentation Links
- [Firebase Web SDK](https://firebase.google.com/docs/web/setup)
- [YouTube Data API](https://developers.google.com/youtube/v3)
- [Firestore Security Rules](https://firebase.google.com/docs/firestore/security/get-started)

---

**⚠️ Important**: Always comply with the terms of service of all APIs you use. Respect rate limits and implement proper error handling.
