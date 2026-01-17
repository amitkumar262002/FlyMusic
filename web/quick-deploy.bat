@echo off
echo ========================================
echo 🚀 FlyMusic AI - Quick API Setup & Deploy
echo ========================================
echo.

echo 🎵 Step-by-Step YouTube API Setup
echo.

:MENU
echo Choose your action:
echo.
echo 1. 🔑 Get YouTube API Key (Opens Google Cloud Console)
echo 2. 🧪 Test API Key (Opens API Test Page)
echo 3. 🚀 Deploy to Firebase
echo 4. 🌐 Deploy to Netlify
echo 5. 📱 Start Local Server
echo 6. ❌ Exit
echo.
set /p choice="Enter your choice (1-6): "

if "%choice%"=="1" goto GET_API_KEY
if "%choice%"=="2" goto TEST_API
if "%choice%"=="3" goto DEPLOY_FIREBASE
if "%choice%"=="4" goto DEPLOY_NETLIFY
if "%choice%"=="5" goto LOCAL_SERVER
if "%choice%"=="6" goto EXIT
goto MENU

:GET_API_KEY
echo.
echo 🔑 Opening Google Cloud Console...
echo.
echo Follow these steps:
echo 1. Create project "FlyMusic AI"
echo 2. Enable "YouTube Data API v3"
echo 3. Create API Key
echo 4. Add restrictions (localhost:3000)
echo 5. Copy the API key
echo.
start https://console.cloud.google.com/
echo.
echo ✅ Google Cloud Console opened in browser
echo After getting your API key, choose option 2 to test it
echo.
pause
goto MENU

:TEST_API
echo.
echo 🧪 Opening API Test Page...
echo.
start api-test.html
echo.
echo ✅ API Test page opened in browser
echo.
echo Instructions:
echo 1. Paste your YouTube API key
echo 2. Click "Test API Key"
echo 3. Try searching for "Arijit Singh"
echo 4. Verify results appear
echo.
pause
goto MENU

:DEPLOY_FIREBASE
echo.
echo 🔥 Deploying to Firebase...
echo.
echo Step 1: Installing Firebase CLI...
call npm install -g firebase-tools
echo.
echo Step 2: Login to Firebase...
call firebase login
echo.
echo Step 3: Initialize Firebase...
call firebase init hosting
echo.
echo Step 4: Deploy to Firebase...
call firebase deploy
echo.
echo ✅ Firebase deployment completed!
echo Your app is live at: https://fly-music-2fef0.web.app
echo.
pause
goto MENU

:DEPLOY_NETLIFY
echo.
echo 🌐 Deploying to Netlify...
echo.
echo Step 1: Installing Netlify CLI...
call npm install -g netlify-cli
echo.
echo Step 2: Deploy to Netlify...
call netlify deploy --prod --dir=.
echo.
echo ✅ Netlify deployment completed!
echo Check your Netlify dashboard for the live URL
echo.
pause
goto MENU

:LOCAL_SERVER
echo.
echo 📱 Starting Local Development Server...
echo.
echo Choose server type:
echo 1. Python HTTP Server (Recommended)
echo 2. Live Server (Node.js)
echo 3. PHP Server
echo.
set /p server="Enter choice (1-3): "

if "%server%"=="1" (
    echo Starting Python server on http://localhost:3000
    echo.
    echo ✅ Server starting... Open http://localhost:3000 in browser
    echo Press Ctrl+C to stop server
    echo.
    python -m http.server 3000
) else if "%server%"=="2" (
    echo Installing Live Server...
    call npm install -g live-server
    echo Starting Live Server on http://localhost:8080
    call live-server --port=8080
) else if "%server%"=="3" (
    echo Starting PHP server on http://localhost:8000
    php -S localhost:8000
) else (
    echo Invalid choice, starting Python server...
    python -m http.server 3000
)

pause
goto MENU

:EXIT
echo.
echo 🎵 FlyMusic AI Setup Complete!
echo.
echo 📋 Quick Testing Commands:
echo.
echo Open browser console and run:
echo   app.testYouTubeAPI()           // Test API connection
echo   app.setupYouTubeAPI('your_key') // Set API key
echo   app.searchMusicTest('Arijit Singh') // Search music
echo.
echo 🎯 Your FlyMusic AI Features:
echo ✅ Professional rotating logo
echo ✅ YouTube API integration
echo ✅ Dark theme design
echo ✅ Instagram-like music feed
echo ✅ Mobile responsive
echo ✅ PWA support
echo ✅ Voice search
echo ✅ AI recommendations
echo.
echo 🚀 Ready to launch India's #1 music app!
echo.
pause
exit
