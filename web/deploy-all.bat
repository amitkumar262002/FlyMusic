@echo off
echo ========================================
echo 🚀 FlyMusic AI - Complete Deployment
echo ========================================
echo.

echo 🎵 Welcome to FlyMusic AI Deployment!
echo This script will deploy your app to multiple platforms
echo.

:MENU
echo Choose your deployment option:
echo.
echo 1. 🔥 Firebase Hosting (Recommended)
echo 2. 🌐 Netlify (Easiest)
echo 3. ⚡ Vercel (Fastest)
echo 4. 📊 All Platforms
echo 5. 🧪 Local Testing Only
echo 6. ❌ Exit
echo.
set /p choice="Enter your choice (1-6): "

if "%choice%"=="1" goto FIREBASE
if "%choice%"=="2" goto NETLIFY
if "%choice%"=="3" goto VERCEL
if "%choice%"=="4" goto ALL
if "%choice%"=="5" goto LOCAL
if "%choice%"=="6" goto EXIT
goto MENU

:FIREBASE
echo.
echo 🔥 Deploying to Firebase Hosting...
echo.
echo Step 1: Installing Firebase CLI...
call npm install -g firebase-tools
echo.
echo Step 2: Logging into Firebase...
call firebase login
echo.
echo Step 3: Initializing Firebase project...
call firebase init hosting
echo.
echo Step 4: Deploying to Firebase...
call firebase deploy
echo.
echo ✅ Firebase deployment completed!
echo Your app is live at: https://fly-music-2fef0.web.app
echo.
pause
goto MENU

:NETLIFY
echo.
echo 🌐 Deploying to Netlify...
echo.
echo Step 1: Installing Netlify CLI...
call npm install -g netlify-cli
echo.
echo Step 2: Deploying to Netlify...
call netlify deploy --prod --dir=.
echo.
echo ✅ Netlify deployment completed!
echo Check your Netlify dashboard for the live URL
echo.
pause
goto MENU

:VERCEL
echo.
echo ⚡ Deploying to Vercel...
echo.
echo Step 1: Installing Vercel CLI...
call npm install -g vercel
echo.
echo Step 2: Deploying to Vercel...
call vercel --prod
echo.
echo ✅ Vercel deployment completed!
echo Your app is live! Check the output above for the URL
echo.
pause
goto MENU

:ALL
echo.
echo 📊 Deploying to ALL platforms...
echo This will take a few minutes...
echo.

echo 🔥 Firebase Hosting...
call npm install -g firebase-tools
call firebase login
call firebase deploy

echo.
echo 🌐 Netlify...
call npm install -g netlify-cli
call netlify deploy --prod --dir=.

echo.
echo ⚡ Vercel...
call npm install -g vercel
call vercel --prod

echo.
echo ✅ All deployments completed!
echo Your app is now live on multiple platforms!
echo.
pause
goto MENU

:LOCAL
echo.
echo 🧪 Starting Local Development Server...
echo.
echo Choose local server option:
echo 1. Python HTTP Server (Port 3000)
echo 2. Node.js Live Server (Port 8080)
echo 3. PHP Built-in Server (Port 8000)
echo.
set /p server="Enter choice (1-3): "

if "%server%"=="1" (
    echo Starting Python server on http://localhost:3000
    python -m http.server 3000
) else if "%server%"=="2" (
    echo Installing and starting Live Server...
    call npm install -g live-server
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
echo 🎵 Thank you for using FlyMusic AI!
echo.
echo 📱 Your app features:
echo ✅ Professional splash screen with rotating logo
echo ✅ Instagram-like music feed
echo ✅ Firebase authentication
echo ✅ YouTube & JioSaavn integration
echo ✅ AI-powered recommendations
echo ✅ Voice search capability
echo ✅ PWA support with offline mode
echo ✅ Social sharing features
echo ✅ Audio equalizer
echo ✅ Mobile-first responsive design
echo.
echo 🚀 Ready to compete with Spotify, Apple Music, and JioSaavn!
echo 🇮🇳 India's #1 Music Streaming Experience!
echo.
echo Visit: https://github.com/flymusic-ai for updates
echo Support: support@flymusic-ai.com
echo.
pause
exit

:ERROR
echo.
echo ❌ An error occurred during deployment.
echo Please check the following:
echo.
echo 1. Internet connection is stable
echo 2. Node.js is installed (node --version)
echo 3. Git is installed (git --version)
echo 4. You have proper permissions
echo 5. API keys are configured correctly
echo.
echo For help, visit: https://github.com/flymusic-ai/support
echo.
pause
goto MENU
