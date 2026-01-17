@echo off
echo ========================================
echo    SAATHI MUSIC - QUICK START
echo ========================================
echo.
echo Starting web server...
cd /d "%~dp0"
start "Saathi Music Server" cmd /k "python -m http.server 3000"
timeout /t 3 /nobreak >nul
echo.
echo Opening website in browser...
start http://localhost:3000
echo.
echo ========================================
echo Server running at: http://localhost:3000
echo Press Ctrl+C in the server window to stop
echo ========================================
pause
