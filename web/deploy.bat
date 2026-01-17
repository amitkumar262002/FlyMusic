@echo off
echo ========================================
echo FlyMusic AI - Deployment Script
echo ========================================
echo.

echo Checking if Firebase CLI is installed...
firebase --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Firebase CLI not found. Installing...
    npm install -g firebase-tools
)

echo.
echo Logging into Firebase...
firebase login

echo.
echo Initializing Firebase project (if not already done)...
firebase init

echo.
echo Building the project...
echo No build step required for static files.

echo.
echo Deploying to Firebase Hosting...
firebase deploy

echo.
echo ========================================
echo Deployment completed!
echo Your FlyMusic AI app is now live!
echo ========================================
echo.
echo To view your app, run: firebase open hosting:site
echo.
pause
