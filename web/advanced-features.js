// 🚀 Advanced Features for FlyMusic AI
class AdvancedFeatures {
    constructor() {
        this.initializeFeatures();
    }

    initializeFeatures() {
        this.setupVoiceSearch();
        this.setupOfflineMode();
        this.setupSocialSharing();
        this.setupMusicVisualization();
        this.setupAIRecommendations();
        this.setupLyrics();
        this.setupCrossfade();
        this.setupEqualizer();
    }

    // 🎤 Voice Search Feature
    setupVoiceSearch() {
        if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            this.recognition = new SpeechRecognition();
            
            this.recognition.continuous = false;
            this.recognition.interimResults = false;
            this.recognition.lang = 'en-US';

            this.addVoiceSearchButton();
        }
    }

    addVoiceSearchButton() {
        const searchContainer = document.querySelector('.search-container');
        if (searchContainer) {
            const voiceBtn = document.createElement('button');
            voiceBtn.className = 'voice-search-btn';
            voiceBtn.innerHTML = '<i class="fas fa-microphone"></i>';
            voiceBtn.title = 'Voice Search';
            voiceBtn.onclick = () => this.startVoiceSearch();
            searchContainer.appendChild(voiceBtn);
        }
    }

    startVoiceSearch() {
        const voiceBtn = document.querySelector('.voice-search-btn');
        const searchInput = document.getElementById('searchInput');
        
        voiceBtn.classList.add('listening');
        voiceBtn.innerHTML = '<i class="fas fa-microphone-slash"></i>';
        
        this.recognition.start();
        
        this.recognition.onresult = (event) => {
            const transcript = event.results[0][0].transcript;
            searchInput.value = transcript;
            if (window.app) {
                window.app.performSearch(transcript);
            }
            this.stopVoiceSearch();
        };

        this.recognition.onerror = () => {
            this.stopVoiceSearch();
            this.showToast('Voice search failed. Please try again.', 'error');
        };
    }

    stopVoiceSearch() {
        const voiceBtn = document.querySelector('.voice-search-btn');
        voiceBtn.classList.remove('listening');
        voiceBtn.innerHTML = '<i class="fas fa-microphone"></i>';
        this.recognition.stop();
    }

    // 📱 Offline Mode with Service Worker
    setupOfflineMode() {
        if ('serviceWorker' in navigator) {
            this.registerServiceWorker();
        }
    }

    async registerServiceWorker() {
        try {
            const registration = await navigator.serviceWorker.register('/sw.js');
            console.log('Service Worker registered:', registration);
            
            // Listen for updates
            registration.addEventListener('updatefound', () => {
                this.showToast('New version available! Refresh to update.', 'info');
            });
        } catch (error) {
            console.error('Service Worker registration failed:', error);
        }
    }

    // 🔗 Social Sharing
    setupSocialSharing() {
        this.addSocialShareButtons();
    }

    addSocialShareButtons() {
        // Add share buttons to music cards
        const style = document.createElement('style');
        style.textContent = `
            .social-share-popup {
                position: fixed;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                background: white;
                padding: 30px;
                border-radius: 20px;
                box-shadow: 0 20px 40px rgba(0,0,0,0.2);
                z-index: 10000;
                text-align: center;
            }
            .social-share-buttons {
                display: flex;
                gap: 15px;
                margin-top: 20px;
            }
            .social-btn {
                width: 50px;
                height: 50px;
                border-radius: 50%;
                border: none;
                color: white;
                font-size: 1.2rem;
                cursor: pointer;
                transition: transform 0.2s ease;
            }
            .social-btn:hover {
                transform: scale(1.1);
            }
            .social-btn.facebook { background: #1877f2; }
            .social-btn.twitter { background: #1da1f2; }
            .social-btn.whatsapp { background: #25d366; }
            .social-btn.telegram { background: #0088cc; }
        `;
        document.head.appendChild(style);
    }

    showSocialShare(song) {
        const popup = document.createElement('div');
        popup.className = 'social-share-popup';
        popup.innerHTML = `
            <h3>Share "${song.title}"</h3>
            <p>by ${song.artist}</p>
            <div class="social-share-buttons">
                <button class="social-btn facebook" onclick="advancedFeatures.shareToFacebook('${song.title}', '${song.artist}')">
                    <i class="fab fa-facebook-f"></i>
                </button>
                <button class="social-btn twitter" onclick="advancedFeatures.shareToTwitter('${song.title}', '${song.artist}')">
                    <i class="fab fa-twitter"></i>
                </button>
                <button class="social-btn whatsapp" onclick="advancedFeatures.shareToWhatsApp('${song.title}', '${song.artist}')">
                    <i class="fab fa-whatsapp"></i>
                </button>
                <button class="social-btn telegram" onclick="advancedFeatures.shareToTelegram('${song.title}', '${song.artist}')">
                    <i class="fab fa-telegram"></i>
                </button>
            </div>
            <button onclick="this.parentElement.remove()" style="margin-top: 20px; padding: 10px 20px; border: none; border-radius: 10px; background: #f0f0f0; cursor: pointer;">Close</button>
        `;
        document.body.appendChild(popup);
    }

    shareToFacebook(title, artist) {
        const url = encodeURIComponent(window.location.href);
        const text = encodeURIComponent(`🎵 Listening to "${title}" by ${artist} on FlyMusic AI!`);
        window.open(`https://www.facebook.com/sharer/sharer.php?u=${url}&quote=${text}`, '_blank');
    }

    shareToTwitter(title, artist) {
        const url = encodeURIComponent(window.location.href);
        const text = encodeURIComponent(`🎵 Listening to "${title}" by ${artist} on FlyMusic AI! #FlyMusicAI #Music`);
        window.open(`https://twitter.com/intent/tweet?text=${text}&url=${url}`, '_blank');
    }

    shareToWhatsApp(title, artist) {
        const text = encodeURIComponent(`🎵 Check out "${title}" by ${artist} on FlyMusic AI! ${window.location.href}`);
        window.open(`https://wa.me/?text=${text}`, '_blank');
    }

    shareToTelegram(title, artist) {
        const text = encodeURIComponent(`🎵 Listening to "${title}" by ${artist} on FlyMusic AI!`);
        const url = encodeURIComponent(window.location.href);
        window.open(`https://t.me/share/url?url=${url}&text=${text}`, '_blank');
    }

    // 🎨 Music Visualization
    setupMusicVisualization() {
        this.createVisualizerCanvas();
    }

    createVisualizerCanvas() {
        const playerContainer = document.getElementById('musicPlayer');
        if (playerContainer) {
            const canvas = document.createElement('canvas');
            canvas.id = 'musicVisualizer';
            canvas.width = 300;
            canvas.height = 100;
            canvas.style.cssText = `
                position: absolute;
                top: -110px;
                left: 50%;
                transform: translateX(-50%);
                background: rgba(102, 126, 234, 0.1);
                border-radius: 10px;
                display: none;
            `;
            playerContainer.appendChild(canvas);
            
            this.setupAudioContext(canvas);
        }
    }

    setupAudioContext(canvas) {
        try {
            this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
            this.analyser = this.audioContext.createAnalyser();
            this.analyser.fftSize = 256;
            
            const ctx = canvas.getContext('2d');
            this.animateVisualizer(ctx, canvas);
        } catch (error) {
            console.log('Audio visualization not supported');
        }
    }

    animateVisualizer(ctx, canvas) {
        const bufferLength = this.analyser.frequencyBinCount;
        const dataArray = new Uint8Array(bufferLength);
        
        const draw = () => {
            requestAnimationFrame(draw);
            
            this.analyser.getByteFrequencyData(dataArray);
            
            ctx.fillStyle = 'rgba(102, 126, 234, 0.1)';
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            
            const barWidth = (canvas.width / bufferLength) * 2.5;
            let barHeight;
            let x = 0;
            
            for (let i = 0; i < bufferLength; i++) {
                barHeight = (dataArray[i] / 255) * canvas.height;
                
                const r = barHeight + 25 * (i / bufferLength);
                const g = 250 * (i / bufferLength);
                const b = 50;
                
                ctx.fillStyle = `rgb(${r},${g},${b})`;
                ctx.fillRect(x, canvas.height - barHeight, barWidth, barHeight);
                
                x += barWidth + 1;
            }
        };
        
        draw();
    }

    // 🤖 AI Music Recommendations
    setupAIRecommendations() {
        this.userPreferences = this.loadUserPreferences();
        this.listeningHistory = [];
    }

    loadUserPreferences() {
        return JSON.parse(localStorage.getItem('musicPreferences')) || {
            genres: [],
            artists: [],
            languages: [],
            mood: 'happy'
        };
    }

    saveUserPreferences() {
        localStorage.setItem('musicPreferences', JSON.stringify(this.userPreferences));
    }

    trackUserInteraction(song, action) {
        this.listeningHistory.push({
            song,
            action, // 'play', 'like', 'skip', 'share'
            timestamp: Date.now()
        });
        
        // Update preferences based on interaction
        if (action === 'like' || action === 'play') {
            this.updatePreferences(song);
        }
        
        // Keep only last 100 interactions
        if (this.listeningHistory.length > 100) {
            this.listeningHistory = this.listeningHistory.slice(-100);
        }
    }

    updatePreferences(song) {
        // Add genre preference
        if (song.genre && !this.userPreferences.genres.includes(song.genre)) {
            this.userPreferences.genres.push(song.genre);
        }
        
        // Add artist preference
        if (song.artist && !this.userPreferences.artists.includes(song.artist)) {
            this.userPreferences.artists.push(song.artist);
        }
        
        // Add language preference
        if (song.language && !this.userPreferences.languages.includes(song.language)) {
            this.userPreferences.languages.push(song.language);
        }
        
        this.saveUserPreferences();
    }

    getAIRecommendations(songs) {
        // Simple AI recommendation algorithm
        return songs.filter(song => {
            let score = 0;
            
            // Genre match
            if (this.userPreferences.genres.includes(song.genre)) score += 3;
            
            // Artist match
            if (this.userPreferences.artists.includes(song.artist)) score += 2;
            
            // Language match
            if (this.userPreferences.languages.includes(song.language)) score += 1;
            
            // Recent listening patterns
            const recentLikes = this.listeningHistory
                .filter(h => h.action === 'like' && Date.now() - h.timestamp < 7 * 24 * 60 * 60 * 1000)
                .map(h => h.song);
            
            if (recentLikes.some(liked => liked.genre === song.genre)) score += 2;
            
            return score >= 2;
        }).sort((a, b) => Math.random() - 0.5); // Shuffle recommended songs
    }

    // 📝 Lyrics Integration
    setupLyrics() {
        this.addLyricsButton();
    }

    addLyricsButton() {
        const playerControls = document.querySelector('.player-controls');
        if (playerControls) {
            const lyricsBtn = document.createElement('button');
            lyricsBtn.className = 'control-btn lyrics-btn';
            lyricsBtn.innerHTML = '<i class="fas fa-align-left"></i>';
            lyricsBtn.title = 'Show Lyrics';
            lyricsBtn.onclick = () => this.showLyrics();
            playerControls.appendChild(lyricsBtn);
        }
    }

    async showLyrics() {
        const currentSong = window.musicPlayer?.getCurrentSong();
        if (!currentSong) {
            this.showToast('No song is currently playing', 'info');
            return;
        }

        // Try to fetch lyrics (mock implementation)
        const lyrics = await this.fetchLyrics(currentSong.title, currentSong.artist);
        this.displayLyrics(currentSong, lyrics);
    }

    async fetchLyrics(title, artist) {
        // Mock lyrics - in real implementation, use lyrics API
        return `🎵 ${title} - ${artist} 🎵

[Verse 1]
Sample lyrics for demonstration
This would be the actual song lyrics
Fetched from a lyrics API service

[Chorus]
Beautiful music playing
In FlyMusic AI today
Enjoy your favorite songs
In the most amazing way

[Verse 2]
More lyrics would appear here
With proper formatting
And synchronized timing
For the best experience

[Bridge]
Music brings us together
Across the world we sing
FlyMusic AI makes it better
Joy is what music brings`;
    }

    displayLyrics(song, lyrics) {
        const lyricsModal = document.createElement('div');
        lyricsModal.className = 'lyrics-modal';
        lyricsModal.innerHTML = `
            <div class="lyrics-content">
                <div class="lyrics-header">
                    <h3>${song.title}</h3>
                    <p>by ${song.artist}</p>
                    <button onclick="this.closest('.lyrics-modal').remove()" class="close-btn">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="lyrics-text">
                    <pre>${lyrics}</pre>
                </div>
            </div>
        `;
        
        // Add styles
        lyricsModal.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.8);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 10000;
        `;
        
        document.body.appendChild(lyricsModal);
    }

    // 🎚️ Audio Equalizer
    setupEqualizer() {
        this.addEqualizerButton();
    }

    addEqualizerButton() {
        const playerVolume = document.querySelector('.player-volume');
        if (playerVolume) {
            const eqBtn = document.createElement('button');
            eqBtn.className = 'control-btn eq-btn';
            eqBtn.innerHTML = '<i class="fas fa-sliders-h"></i>';
            eqBtn.title = 'Equalizer';
            eqBtn.onclick = () => this.showEqualizer();
            playerVolume.appendChild(eqBtn);
        }
    }

    showEqualizer() {
        const eqModal = document.createElement('div');
        eqModal.className = 'equalizer-modal';
        eqModal.innerHTML = `
            <div class="equalizer-content">
                <h3>Audio Equalizer</h3>
                <div class="eq-sliders">
                    <div class="eq-band">
                        <label>60Hz</label>
                        <input type="range" min="-20" max="20" value="0" class="eq-slider" data-freq="60">
                        <span>0dB</span>
                    </div>
                    <div class="eq-band">
                        <label>170Hz</label>
                        <input type="range" min="-20" max="20" value="0" class="eq-slider" data-freq="170">
                        <span>0dB</span>
                    </div>
                    <div class="eq-band">
                        <label>350Hz</label>
                        <input type="range" min="-20" max="20" value="0" class="eq-slider" data-freq="350">
                        <span>0dB</span>
                    </div>
                    <div class="eq-band">
                        <label>1kHz</label>
                        <input type="range" min="-20" max="20" value="0" class="eq-slider" data-freq="1000">
                        <span>0dB</span>
                    </div>
                    <div class="eq-band">
                        <label>3.5kHz</label>
                        <input type="range" min="-20" max="20" value="0" class="eq-slider" data-freq="3500">
                        <span>0dB</span>
                    </div>
                    <div class="eq-band">
                        <label>10kHz</label>
                        <input type="range" min="-20" max="20" value="0" class="eq-slider" data-freq="10000">
                        <span>0dB</span>
                    </div>
                </div>
                <div class="eq-presets">
                    <button onclick="advancedFeatures.applyEQPreset('flat')">Flat</button>
                    <button onclick="advancedFeatures.applyEQPreset('rock')">Rock</button>
                    <button onclick="advancedFeatures.applyEQPreset('pop')">Pop</button>
                    <button onclick="advancedFeatures.applyEQPreset('jazz')">Jazz</button>
                    <button onclick="advancedFeatures.applyEQPreset('classical')">Classical</button>
                </div>
                <button onclick="this.closest('.equalizer-modal').remove()" class="close-eq-btn">Close</button>
            </div>
        `;
        
        document.body.appendChild(eqModal);
        this.setupEQStyles();
        this.setupEQSliderEvents();
    }

    applyEQPreset(preset) {
        const presets = {
            flat: [0, 0, 0, 0, 0, 0],
            rock: [5, 3, -2, 2, 4, 3],
            pop: [2, 4, 2, 0, -2, 2],
            jazz: [4, 2, -2, 2, 4, 5],
            classical: [5, 3, -2, 4, 4, 2]
        };
        
        const values = presets[preset] || presets.flat;
        const sliders = document.querySelectorAll('.eq-slider');
        
        sliders.forEach((slider, index) => {
            slider.value = values[index];
            const dbDisplay = slider.nextElementSibling;
            dbDisplay.textContent = `${values[index]}dB`;
            
            // Apply visual feedback for preset values
            const value = values[index];
            dbDisplay.style.background = value > 0 ? 
                'rgba(76, 175, 80, 0.2)' : 
                value < 0 ? 
                'rgba(244, 67, 54, 0.2)' : 
                'rgba(102, 126, 234, 0.1)';
            
            dbDisplay.style.color = value > 0 ? 
                '#4caf50' : 
                value < 0 ? 
                '#f44336' : 
                '#555';
            
            // Add preset application animation
            dbDisplay.style.transform = 'scale(1.2)';
            setTimeout(() => {
                dbDisplay.style.transform = 'scale(1)';
            }, 200);
        });
    }

    setupEQSliderEvents() {
        const sliders = document.querySelectorAll('.eq-slider');
        sliders.forEach(slider => {
            slider.addEventListener('input', (e) => {
                const value = e.target.value;
                const dbDisplay = e.target.nextElementSibling;
                dbDisplay.textContent = `${value}dB`;
                
                // Add visual feedback
                dbDisplay.style.background = value > 0 ? 
                    'rgba(76, 175, 80, 0.2)' : 
                    value < 0 ? 
                    'rgba(244, 67, 54, 0.2)' : 
                    'rgba(102, 126, 234, 0.1)';
                
                dbDisplay.style.color = value > 0 ? 
                    '#4caf50' : 
                    value < 0 ? 
                    '#f44336' : 
                    '#555';
                
                // Add scale animation
                dbDisplay.style.transform = 'scale(1.1)';
                setTimeout(() => {
                    dbDisplay.style.transform = 'scale(1)';
                }, 150);
            });
        });
    }

    setupEQStyles() {
        const style = document.createElement('style');
        style.textContent = `
            .equalizer-modal {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.85);
                backdrop-filter: blur(10px);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10000;
                animation: fadeIn 0.3s ease-out;
            }
            
            @keyframes fadeIn {
                from { opacity: 0; }
                to { opacity: 1; }
            }
            
            @keyframes slideUp {
                from { 
                    opacity: 0;
                    transform: translateY(30px) scale(0.95);
                }
                to { 
                    opacity: 1;
                    transform: translateY(0) scale(1);
                }
            }
            
            .equalizer-content {
                background: linear-gradient(145deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.85));
                backdrop-filter: blur(20px);
                border: 1px solid rgba(255, 255, 255, 0.2);
                padding: 40px 30px;
                border-radius: 25px;
                text-align: center;
                max-width: 600px;
                width: 90%;
                box-shadow: 
                    0 25px 50px rgba(0, 0, 0, 0.15),
                    0 0 0 1px rgba(255, 255, 255, 0.1),
                    inset 0 1px 0 rgba(255, 255, 255, 0.6);
                animation: slideUp 0.4s ease-out;
                position: relative;
                overflow: hidden;
            }
            
            .equalizer-content::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 1px;
                background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.5), transparent);
            }
            
            .equalizer-content h3 {
                margin: 0 0 30px 0;
                font-size: 1.8rem;
                font-weight: 700;
                background: linear-gradient(135deg, #667eea, #764ba2);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
                text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                position: relative;
            }
            
            .equalizer-content h3::after {
                content: '';
                position: absolute;
                bottom: -10px;
                left: 50%;
                transform: translateX(-50%);
                width: 60px;
                height: 3px;
                background: linear-gradient(90deg, #667eea, #764ba2);
                border-radius: 2px;
            }
            
            .eq-sliders {
                display: flex;
                gap: 25px;
                margin: 40px 0;
                justify-content: center;
                align-items: flex-end;
                padding: 20px;
                background: rgba(102, 126, 234, 0.05);
                border-radius: 20px;
                border: 1px solid rgba(102, 126, 234, 0.1);
            }
            
            .eq-band {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 15px;
                position: relative;
            }
            
            .eq-band label {
                font-size: 0.9rem;
                font-weight: 600;
                color: #667eea;
                margin-bottom: 5px;
                text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
            }
            
            .eq-slider {
                writing-mode: bt-lr;
                -webkit-appearance: slider-vertical;
                width: 8px;
                height: 180px;
                background: linear-gradient(to top, 
                    rgba(102, 126, 234, 0.2) 0%,
                    rgba(102, 126, 234, 0.1) 50%,
                    rgba(102, 126, 234, 0.2) 100%);
                border-radius: 10px;
                outline: none;
                cursor: pointer;
                transition: all 0.3s ease;
                position: relative;
                border: 2px solid rgba(102, 126, 234, 0.3);
            }
            
            .eq-slider::-webkit-slider-thumb {
                -webkit-appearance: none;
                appearance: none;
                width: 20px;
                height: 20px;
                border-radius: 50%;
                background: linear-gradient(145deg, #667eea, #764ba2);
                cursor: pointer;
                box-shadow: 
                    0 4px 8px rgba(102, 126, 234, 0.3),
                    0 0 0 3px rgba(255, 255, 255, 0.8),
                    0 0 0 5px rgba(102, 126, 234, 0.2);
                transition: all 0.2s ease;
            }
            
            .eq-slider::-webkit-slider-thumb:hover {
                transform: scale(1.2);
                box-shadow: 
                    0 6px 12px rgba(102, 126, 234, 0.4),
                    0 0 0 3px rgba(255, 255, 255, 0.9),
                    0 0 0 6px rgba(102, 126, 234, 0.3);
            }
            
            .eq-slider::-webkit-slider-thumb:active {
                transform: scale(1.1);
            }
            
            .eq-slider:hover {
                background: linear-gradient(to top, 
                    rgba(102, 126, 234, 0.3) 0%,
                    rgba(102, 126, 234, 0.15) 50%,
                    rgba(102, 126, 234, 0.3) 100%);
                border-color: rgba(102, 126, 234, 0.5);
            }
            
            .eq-band span {
                font-size: 0.8rem;
                font-weight: 600;
                color: #555;
                background: rgba(102, 126, 234, 0.1);
                padding: 4px 8px;
                border-radius: 12px;
                min-width: 35px;
                text-align: center;
                border: 1px solid rgba(102, 126, 234, 0.2);
                transition: all 0.2s ease;
            }
            
            .eq-presets {
                display: flex;
                gap: 12px;
                margin: 30px 0;
                flex-wrap: wrap;
                justify-content: center;
            }
            
            .eq-presets button {
                padding: 12px 20px;
                border: 2px solid transparent;
                background: linear-gradient(145deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
                color: #667eea;
                border-radius: 25px;
                cursor: pointer;
                font-weight: 600;
                font-size: 0.9rem;
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
                backdrop-filter: blur(10px);
                border: 1px solid rgba(102, 126, 234, 0.2);
            }
            
            .eq-presets button::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
                transition: left 0.5s ease;
            }
            
            .eq-presets button:hover::before {
                left: 100%;
            }
            
            .eq-presets button:hover {
                background: linear-gradient(145deg, #667eea, #764ba2);
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
                border-color: rgba(255, 255, 255, 0.3);
            }
            
            .eq-presets button:active {
                transform: translateY(0);
                box-shadow: 0 4px 10px rgba(102, 126, 234, 0.2);
            }
            
            .close-eq-btn {
                padding: 12px 30px;
                border: none;
                background: linear-gradient(145deg, #ff6b6b, #ee5a52);
                color: white;
                border-radius: 25px;
                cursor: pointer;
                font-weight: 600;
                font-size: 1rem;
                transition: all 0.3s ease;
                margin-top: 20px;
                box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
                position: relative;
                overflow: hidden;
            }
            
            .close-eq-btn::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
                transition: left 0.5s ease;
            }
            
            .close-eq-btn:hover::before {
                left: 100%;
            }
            
            .close-eq-btn:hover {
                background: linear-gradient(145deg, #ff5252, #e53935);
                transform: translateY(-2px);
                box-shadow: 0 8px 25px rgba(255, 107, 107, 0.4);
            }
            
            .close-eq-btn:active {
                transform: translateY(0);
                box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
            }
            
            /* Responsive Design */
            @media (max-width: 768px) {
                .equalizer-content {
                    padding: 30px 20px;
                    margin: 20px;
                    max-width: none;
                    width: calc(100% - 40px);
                }
                
                .eq-sliders {
                    gap: 15px;
                    padding: 15px;
                    flex-wrap: nowrap;
                    overflow-x: auto;
                }
                
                .eq-slider {
                    height: 120px;
                }
                
                .eq-presets {
                    gap: 8px;
                }
                
                .eq-presets button {
                    padding: 10px 16px;
                    font-size: 0.8rem;
                }
                
                .equalizer-content h3 {
                    font-size: 1.5rem;
                    margin-bottom: 20px;
                }
            }
            
            @media (max-width: 480px) {
                .eq-sliders {
                    gap: 10px;
                }
                
                .eq-band label {
                    font-size: 0.8rem;
                }
                
                .eq-slider {
                    height: 100px;
                }
                
                .eq-presets button {
                    padding: 8px 12px;
                    font-size: 0.75rem;
                }
            }
        `;
        document.head.appendChild(style);
    }

    // 🔄 Crossfade Feature
    setupCrossfade() {
        this.crossfadeDuration = 3000; // 3 seconds
        this.addCrossfadeControl();
    }

    addCrossfadeControl() {
        const playerControls = document.querySelector('.player-controls');
        if (playerControls) {
            const crossfadeBtn = document.createElement('button');
            crossfadeBtn.className = 'control-btn crossfade-btn';
            crossfadeBtn.innerHTML = '<i class="fas fa-exchange-alt"></i>';
            crossfadeBtn.title = 'Crossfade: OFF';
            crossfadeBtn.onclick = () => this.toggleCrossfade(crossfadeBtn);
            playerControls.appendChild(crossfadeBtn);
        }
    }

    toggleCrossfade(button) {
        this.crossfadeEnabled = !this.crossfadeEnabled;
        button.style.color = this.crossfadeEnabled ? '#667eea' : '#666';
        button.title = `Crossfade: ${this.crossfadeEnabled ? 'ON' : 'OFF'}`;
        
        this.showToast(`Crossfade ${this.crossfadeEnabled ? 'enabled' : 'disabled'}`, 'info');
    }

    // 🎯 Utility Methods
    showToast(message, type = 'info') {
        if (window.authManager) {
            window.authManager.showToast(message, type);
        }
    }
}

// Initialize advanced features
window.advancedFeatures = new AdvancedFeatures();

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AdvancedFeatures;
}
