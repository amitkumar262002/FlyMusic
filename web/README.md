# FlyMusic AI 🎵

Your Ultimate Music Experience with AI-powered recommendations, Firebase Authentication, YouTube API, and JioSaavn Integration.

## Features ✨

### 🔐 Authentication
- **Firebase Authentication** with email/password and Google Sign-In
- Secure user management and profile handling
- Password reset functionality
- User preferences and settings

### 🎵 Music Integration
- **YouTube API** integration for international music
- **JioSaavn API** integration for Indian/Bollywood music
- Advanced search across multiple platforms
- AI-powered music recommendations

### 🎧 Music Player
- Full-featured music player with play/pause, next/previous
- Progress bar with seek functionality
- Volume control and mute
- Shuffle and repeat modes
- Keyboard shortcuts support
- Media session integration (browser controls)

### 📱 Modern UI/UX
- Responsive design for all devices
- Beautiful gradient themes
- Smooth animations and transitions
- Toast notifications
- Loading states and error handling

### 🎼 Music Management
- Create and manage playlists
- Favorites system
- Recently played tracks
- Music library organization
- Search suggestions

## Setup Instructions 🚀

### Prerequisites
- Node.js (v14 or higher)
- Firebase account
- YouTube Data API key
- Modern web browser

### 1. Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing one
3. Enable Authentication with Email/Password and Google providers
4. Enable Firestore Database
5. Copy your Firebase configuration (already configured in the project)

### 2. YouTube API Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable YouTube Data API v3
4. Create credentials (API Key)
5. Replace `YOUR_YOUTUBE_API_KEY` in `music-api.js` with your API key

### 3. Installation
```bash
# Navigate to the web directory
cd web

# Install dependencies (optional, for development server)
npm install

# Start development server
npm start
```

### 4. Direct Usage
You can also run the app directly by opening `index.html` in a web browser, but some features may require a local server due to CORS policies.

## File Structure 📁

```
web/
├── index.html          # Main HTML file
├── styles.css          # CSS styles and responsive design
├── firebase-config.js  # Firebase configuration
├── auth.js            # Authentication logic
├── music-api.js       # YouTube and JioSaavn API integration
├── player.js          # Music player functionality
├── app.js             # Main application logic
├── package.json       # Dependencies and scripts
└── README.md          # This file
```

## API Configuration 🔧

### YouTube API
- **Endpoint**: `https://www.googleapis.com/youtube/v3`
- **Required**: API Key from Google Cloud Console
- **Features**: Search videos, get video details, trending music

### JioSaavn API
- **Endpoint**: `https://saavn.me/api` (Unofficial API)
- **Features**: Search Indian music, trending Bollywood songs, albums

## Usage Guide 📖

### Authentication
1. **Sign Up**: Create account with email/password or Google
2. **Sign In**: Login with existing credentials
3. **Password Reset**: Use forgot password feature

### Music Discovery
1. **Search**: Use the search bar to find songs across platforms
2. **Browse**: Explore trending, Bollywood, and international sections
3. **Play**: Click on any song to start playing

### Music Player
- **Space**: Play/Pause
- **Arrow Left/Right**: Previous/Next track
- **Arrow Up/Down**: Volume control
- **M**: Mute/Unmute
- **S**: Toggle Shuffle
- **R**: Toggle Repeat

### Playlists
1. Click "Create Playlist" in sidebar
2. Add songs to playlists using the + button
3. Manage playlists in the Playlists section

## Deployment 🌐

### Firebase Hosting
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firebase in project
firebase init hosting

# Deploy to Firebase
firebase deploy
```

### Netlify
1. Connect your GitHub repository to Netlify
2. Set build command: `echo "Static site"`
3. Set publish directory: `web`
4. Deploy automatically on push

### GitHub Pages
1. Push code to GitHub repository
2. Go to repository Settings > Pages
3. Select source branch (main/master)
4. Set folder to `web` if needed

## Environment Variables 🔐

Create a `.env` file (not included in repo) for sensitive data:
```
YOUTUBE_API_KEY=your_youtube_api_key_here
FIREBASE_API_KEY=your_firebase_api_key_here
```

## Browser Support 🌐

- Chrome 80+
- Firefox 75+
- Safari 13+
- Edge 80+

## Contributing 🤝

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## Security Notes 🔒

- API keys should be restricted by domain in production
- Firebase security rules should be properly configured
- User data is stored securely in Firestore
- HTTPS is required for production deployment

## Troubleshooting 🔧

### Common Issues
1. **CORS Errors**: Use a local server instead of opening HTML directly
2. **API Limits**: YouTube API has daily quotas, implement caching
3. **Audio Playback**: Some browsers require user interaction before playing audio
4. **Firebase Auth**: Ensure domain is added to authorized domains

### Debug Mode
Open browser developer tools and check console for detailed error messages.

## License 📄

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments 🙏

- Firebase for authentication and database
- YouTube API for music content
- JioSaavn for Indian music content
- Font Awesome for icons
- Google Fonts for typography

## Support 💬

For support and questions:
- Create an issue on GitHub
- Email: support@flymusic-ai.com
- Documentation: [Wiki](https://github.com/your-username/flymusic-ai/wiki)

---

**Made with ❤️ by FlyMusic AI Team**
