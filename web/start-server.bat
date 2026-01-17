@echo off
echo Starting Saathi Music Web Server...
echo.
echo Choose an option:
echo 1. Python HTTP Server (Port 3000)
echo 2. Python HTTP Server (Port 8080)
echo 3. Open in default browser directly
echo.
set /p choice="Enter your choice (1-3): "

if "%choice%"=="1" (
    echo Starting Python server on port 3000...
    python -m http.server 3000
) else if "%choice%"=="2" (
    echo Starting Python server on port 8080...
    python -m http.server 8080
) else if "%choice%"=="3" (
    echo Opening website directly in browser...
    start index.html
) else (
    echo Invalid choice. Starting default server on port 3000...
    python -m http.server 3000
)

pause
