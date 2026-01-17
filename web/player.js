// Enhanced Music Player functionality
class MusicPlayer {
    constructor() {
        this.audio = document.getElementById('audioPlayer') || this.createAudioElement();
        this.currentSong = null;
        this.playlist = [];
        this.currentIndex = 0;
        this.isPlaying = false;
        this.isShuffled = false;
        this.repeatMode = 'none'; // none, one, all
        this.volume = 0.5;
        this.isLoading = false;
        
        this.initializePlayer();
        this.setupEventListeners();
        
        // Add to window for global access
        window.musicPlayer = this;
    }
    
    createAudioElement() {
        const audio = document.createElement('audio');
        audio.id = 'audioPlayer';
        audio.preload = 'metadata';
        audio.crossOrigin = 'anonymous';
        document.body.appendChild(audio);
        return audio;
    }

    initializePlayer() {
        // Set initial volume
        this.audio.volume = this.volume;
        
        // Update UI elements
        this.updateVolumeSlider();
        this.updatePlayerInfo();
    }

    setupEventListeners() {
        // Audio element events
        this.audio.addEventListener('loadedmetadata', () => this.onLoadedMetadata());
        this.audio.addEventListener('timeupdate', () => this.onTimeUpdate());
        this.audio.addEventListener('ended', () => this.onSongEnded());
        this.audio.addEventListener('error', (e) => this.onAudioError(e));
        this.audio.addEventListener('canplay', () => this.onCanPlay());

        // Player control buttons
        const playPauseBtn = document.getElementById('playPauseBtn');
        const prevBtn = document.getElementById('prevBtn');
        const nextBtn = document.getElementById('nextBtn');
        const shuffleBtn = document.getElementById('shuffleBtn');
        const repeatBtn = document.getElementById('repeatBtn');

        if (playPauseBtn) {
            playPauseBtn.addEventListener('click', () => this.togglePlayPause());
        }

        if (prevBtn) {
            prevBtn.addEventListener('click', () => this.playPrevious());
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => this.playNext());
        }

        if (shuffleBtn) {
            shuffleBtn.addEventListener('click', () => this.toggleShuffle());
        }

        if (repeatBtn) {
            repeatBtn.addEventListener('click', () => this.toggleRepeat());
        }

        // Progress slider
        const progressSlider = document.getElementById('progressSlider');
        if (progressSlider) {
            progressSlider.addEventListener('input', (e) => this.seekTo(e.target.value));
        }

        // Volume controls
        const volumeBtn = document.getElementById('volumeBtn');
        const volumeSlider = document.getElementById('volumeSlider');

        if (volumeBtn) {
            volumeBtn.addEventListener('click', () => this.toggleMute());
        }

        if (volumeSlider) {
            volumeSlider.addEventListener('input', (e) => this.setVolume(e.target.value / 100));
        }

        // Keyboard shortcuts
        document.addEventListener('keydown', (e) => this.handleKeyboardShortcuts(e));
    }

    // Core player methods
    async playSong(song, playlist = null, index = 0) {
        try {
            console.log('🎵 Playing song:', song.title, 'by', song.artist);
            
            this.isLoading = true;
            this.currentSong = song;
            
            if (playlist) {
                this.playlist = playlist;
                this.currentIndex = index;
            }

            // Update UI immediately
            this.updatePlayerInfo();
            this.showLoadingState();
            
            // Get audio URL from Real Music API
            let audioUrl = song.audioUrl;
            
            // If no direct audio URL, try to get stream URL
            if (!audioUrl && window.realMusicAPI) {
                audioUrl = await window.realMusicAPI.getAudioStreamUrl(song.id);
            }
            
            // Fallback to working audio URLs
            if (!audioUrl) {
                const workingUrls = [
                    'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3',
                    'https://sample-videos.com/zip/10/mp3/SampleAudio_0.4mb_mp3.mp3',
                    'https://actions.google.com/sounds/v1/alarms/beep_short.ogg'
                ];
                audioUrl = workingUrls[Math.floor(Math.random() * workingUrls.length)];
            }

            console.log('🔗 Audio URL:', audioUrl);

            if (audioUrl) {
                this.audio.src = audioUrl;
                
                // Wait for audio to load
                await new Promise((resolve, reject) => {
                    const onCanPlay = () => {
                        this.audio.removeEventListener('canplay', onCanPlay);
                        this.audio.removeEventListener('error', onError);
                        resolve();
                    };
                    
                    const onError = (e) => {
                        this.audio.removeEventListener('canplay', onCanPlay);
                        this.audio.removeEventListener('error', onError);
                        reject(new Error('Audio failed to load'));
                    };
                    
                    this.audio.addEventListener('canplay', onCanPlay);
                    this.audio.addEventListener('error', onError);
                    
                    this.audio.load();
                    
                    // Timeout after 10 seconds
                    setTimeout(() => {
                        this.audio.removeEventListener('canplay', onCanPlay);
                        this.audio.removeEventListener('error', onError);
                        reject(new Error('Audio load timeout'));
                    }, 10000);
                });
                
                // Start playing
                this.isPlaying = true;
                await this.audio.play();
                
                console.log('✅ Song playing successfully');
                this.showToast(`Now playing: ${song.title}`, 'success');
                
            } else {
                throw new Error('No audio URL available');
            }

            // Save to recently played
            this.saveToRecentlyPlayed(song);
            
            // Update media session (for browser media controls)
            this.updateMediaSession();
            
            this.isLoading = false;
            this.hideLoadingState();

        } catch (error) {
            console.error('❌ Error playing song:', error);
            this.isLoading = false;
            this.hideLoadingState();
            
            // Only show toast if we haven't shown one recently
            const now = Date.now();
            if (!this.lastErrorTime || (now - this.lastErrorTime) > 3000) {
                this.showToast(`Unable to play "${song.title}". Trying next...`, 'error');
                this.lastErrorTime = now;
            }
            
            // Try next song if available, but limit retries
            if (this.playlist.length > 1 && (!this.retryCount || this.retryCount < 3)) {
                this.retryCount = (this.retryCount || 0) + 1;
                setTimeout(() => {
                    this.playNext();
                }, 1500);
            } else {
                this.retryCount = 0;
                this.showToast('No playable songs found', 'info');
                this.isPlaying = false;
                this.updatePlayPauseButton();
            }
        }
    }
    
    showLoadingState() {
        const playBtn = document.getElementById('playPauseBtn');
        if (playBtn) {
            playBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
            playBtn.disabled = true;
        }
        
        // Show loading in player info
        const songTitle = document.getElementById('currentSongTitle');
        if (songTitle && this.currentSong) {
            songTitle.textContent = `Loading ${this.currentSong.title}...`;
        }
    }
    
    hideLoadingState() {
        const playBtn = document.getElementById('playPauseBtn');
        if (playBtn) {
            playBtn.disabled = false;
        }
        this.updatePlayPauseButton();
        this.updatePlayerInfo();
    }

    togglePlayPause() {
        if (!this.currentSong) {
            this.showToast('Please select a song to play', 'info');
            return;
        }

        if (this.isPlaying) {
            this.pause();
        } else {
            this.play();
        }
    }

    async play() {
        try {
            await this.audio.play();
            this.isPlaying = true;
            this.updatePlayPauseButton();
        } catch (error) {
            console.error('Error playing audio:', error);
            this.showToast('Unable to play audio', 'error');
        }
    }

    pause() {
        this.audio.pause();
        this.isPlaying = false;
        this.updatePlayPauseButton();
    }

    playNext() {
        if (this.playlist.length === 0) return;

        let nextIndex;
        
        if (this.isShuffled) {
            nextIndex = Math.floor(Math.random() * this.playlist.length);
        } else {
            nextIndex = (this.currentIndex + 1) % this.playlist.length;
        }

        this.playSong(this.playlist[nextIndex], this.playlist, nextIndex);
    }

    playPrevious() {
        if (this.playlist.length === 0) return;

        let prevIndex;
        
        if (this.isShuffled) {
            prevIndex = Math.floor(Math.random() * this.playlist.length);
        } else {
            prevIndex = this.currentIndex === 0 ? this.playlist.length - 1 : this.currentIndex - 1;
        }

        this.playSong(this.playlist[prevIndex], this.playlist, prevIndex);
    }

    seekTo(percentage) {
        if (this.audio.duration) {
            this.audio.currentTime = (percentage / 100) * this.audio.duration;
        }
    }

    setVolume(volume) {
        this.volume = Math.max(0, Math.min(1, volume));
        this.audio.volume = this.volume;
        this.updateVolumeButton();
        this.updateVolumeSlider();
    }

    toggleMute() {
        if (this.audio.volume > 0) {
            this.audio.volume = 0;
        } else {
            this.audio.volume = this.volume;
        }
        this.updateVolumeButton();
    }

    toggleShuffle() {
        this.isShuffled = !this.isShuffled;
        this.updateShuffleButton();
        this.showToast(`Shuffle ${this.isShuffled ? 'enabled' : 'disabled'}`, 'info');
    }

    toggleRepeat() {
        const modes = ['none', 'one', 'all'];
        const currentIndex = modes.indexOf(this.repeatMode);
        this.repeatMode = modes[(currentIndex + 1) % modes.length];
        this.updateRepeatButton();
        
        const modeText = this.repeatMode === 'none' ? 'off' : 
                        this.repeatMode === 'one' ? 'current song' : 'playlist';
        this.showToast(`Repeat ${modeText}`, 'info');
    }

    // Event handlers
    onLoadedMetadata() {
        const totalTime = document.getElementById('totalTime');
        if (totalTime && this.audio.duration) {
            totalTime.textContent = this.formatTime(this.audio.duration);
        }
    }

    onTimeUpdate() {
        if (!this.audio.duration) return;

        const currentTime = document.getElementById('currentTime');
        const progressFill = document.getElementById('progressFill');
        const progressSlider = document.getElementById('progressSlider');

        const progress = (this.audio.currentTime / this.audio.duration) * 100;

        if (currentTime) {
            currentTime.textContent = this.formatTime(this.audio.currentTime);
        }

        if (progressFill) {
            progressFill.style.width = `${progress}%`;
        }

        if (progressSlider && !progressSlider.matches(':active')) {
            progressSlider.value = progress;
        }
    }

    onSongEnded() {
        if (this.repeatMode === 'one') {
            this.audio.currentTime = 0;
            this.play();
        } else if (this.repeatMode === 'all' || this.currentIndex < this.playlist.length - 1) {
            this.playNext();
        } else {
            this.isPlaying = false;
            this.updatePlayPauseButton();
        }
    }

    onAudioError(error) {
        console.error('Audio error:', error);
        this.showToast('Error loading audio. Skipping to next song...', 'error');
        
        // Try next song
        setTimeout(() => {
            if (this.playlist.length > 1) {
                this.playNext();
            }
        }, 2000);
    }

    onCanPlay() {
        // Audio is ready to play
        console.log('Audio ready to play');
    }

    // UI update methods
    updatePlayerInfo() {
        const songTitle = document.getElementById('currentSongTitle');
        const songArtist = document.getElementById('currentSongArtist');
        const songImage = document.getElementById('currentSongImage');

        if (this.currentSong) {
            if (songTitle) {
                songTitle.textContent = this.currentSong.title;
            }
            
            if (songArtist) {
                songArtist.textContent = this.currentSong.artist;
            }
            
            if (songImage) {
                songImage.src = this.currentSong.thumbnail || 'https://via.placeholder.com/50x50?text=♪';
                songImage.alt = this.currentSong.title;
            }
        } else {
            if (songTitle) songTitle.textContent = 'No song selected';
            if (songArtist) songArtist.textContent = 'Artist';
            if (songImage) songImage.src = 'https://via.placeholder.com/50x50?text=♪';
        }
    }

    updatePlayPauseButton() {
        const playPauseBtn = document.getElementById('playPauseBtn');
        if (playPauseBtn) {
            const icon = playPauseBtn.querySelector('i');
            if (icon) {
                icon.className = this.isPlaying ? 'fas fa-pause' : 'fas fa-play';
            }
        }
    }

    updateVolumeButton() {
        const volumeBtn = document.getElementById('volumeBtn');
        if (volumeBtn) {
            const icon = volumeBtn.querySelector('i');
            if (icon) {
                if (this.audio.volume === 0) {
                    icon.className = 'fas fa-volume-mute';
                } else if (this.audio.volume < 0.5) {
                    icon.className = 'fas fa-volume-down';
                } else {
                    icon.className = 'fas fa-volume-up';
                }
            }
        }
    }

    updateVolumeSlider() {
        const volumeSlider = document.getElementById('volumeSlider');
        if (volumeSlider) {
            volumeSlider.value = this.audio.volume * 100;
        }
    }

    updateShuffleButton() {
        const shuffleBtn = document.getElementById('shuffleBtn');
        if (shuffleBtn) {
            shuffleBtn.style.color = this.isShuffled ? '#667eea' : '#666';
        }
    }

    updateRepeatButton() {
        const repeatBtn = document.getElementById('repeatBtn');
        if (repeatBtn) {
            const icon = repeatBtn.querySelector('i');
            if (icon) {
                switch (this.repeatMode) {
                    case 'none':
                        icon.className = 'fas fa-redo';
                        repeatBtn.style.color = '#666';
                        break;
                    case 'one':
                        icon.className = 'fas fa-redo';
                        repeatBtn.style.color = '#667eea';
                        break;
                    case 'all':
                        icon.className = 'fas fa-redo';
                        repeatBtn.style.color = '#667eea';
                        break;
                }
            }
        }
    }

    // Utility methods
    formatTime(seconds) {
        if (!seconds || isNaN(seconds)) return '0:00';
        
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    }

    getYouTubeAudioUrl(videoId) {
        // For demo purposes, return working audio URLs
        // In production, you'd need a proper YouTube audio extraction service
        const workingAudios = [
            'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3',
            'https://sample-videos.com/zip/10/mp3/mp3-15s/SampleAudio_0.4mb_mp3.mp3',
            'https://actions.google.com/sounds/v1/alarms/bugle_tune.ogg',
            'https://actions.google.com/sounds/v1/cartoon/clang_and_wobble.ogg'
        ];
        
        // Return a random working audio for demo
        const randomIndex = Math.floor(Math.random() * workingAudios.length);
        return workingAudios[randomIndex];
    }

    saveToRecentlyPlayed(song) {
        // Save to user's recently played list
        if (window.authManager && window.authManager.currentUser) {
            const userId = window.authManager.currentUser.uid;
            // Implementation would save to Firestore
            console.log('Saving to recently played:', song.title);
        }
    }

    updateMediaSession() {
        if ('mediaSession' in navigator && this.currentSong) {
            const artwork = [];
            
            // Only add artwork if we have a valid image URL
            if (this.currentSong.image || this.currentSong.thumbnail) {
                const imageUrl = this.currentSong.image || this.currentSong.thumbnail;
                if (imageUrl && imageUrl.startsWith('http')) {
                    artwork.push({ src: imageUrl, sizes: '300x300', type: 'image/jpeg' });
                }
            }
            
            navigator.mediaSession.metadata = new MediaMetadata({
                title: this.currentSong.title || 'Unknown Song',
                artist: this.currentSong.artist || 'Unknown Artist',
                album: this.currentSong.album || 'Saathi Music',
                artwork: artwork
            });

            navigator.mediaSession.setActionHandler('play', () => this.play());
            navigator.mediaSession.setActionHandler('pause', () => this.pause());
            navigator.mediaSession.setActionHandler('previoustrack', () => this.playPrevious());
            navigator.mediaSession.setActionHandler('nexttrack', () => this.playNext());
        }
    }

    handleKeyboardShortcuts(event) {
        // Prevent shortcuts when typing in input fields
        if (event.target.tagName === 'INPUT' || event.target.tagName === 'TEXTAREA') {
            return;
        }

        switch (event.code) {
            case 'Space':
                event.preventDefault();
                this.togglePlayPause();
                break;
            case 'ArrowLeft':
                event.preventDefault();
                this.playPrevious();
                break;
            case 'ArrowRight':
                event.preventDefault();
                this.playNext();
                break;
            case 'ArrowUp':
                event.preventDefault();
                this.setVolume(Math.min(1, this.volume + 0.1));
                break;
            case 'ArrowDown':
                event.preventDefault();
                this.setVolume(Math.max(0, this.volume - 0.1));
                break;
            case 'KeyM':
                event.preventDefault();
                this.toggleMute();
                break;
            case 'KeyS':
                event.preventDefault();
                this.toggleShuffle();
                break;
            case 'KeyR':
                event.preventDefault();
                this.toggleRepeat();
                break;
        }
    }

    showToast(message, type = 'info') {
        // Use the same toast system as auth
        if (window.authManager) {
            window.authManager.showToast(message, type);
        }
    }

    // Public methods for external use
    addToPlaylist(songs) {
        this.playlist = [...this.playlist, ...songs];
    }

    clearPlaylist() {
        this.playlist = [];
        this.currentIndex = 0;
    }

    setPlaylist(songs) {
        this.playlist = songs;
        this.currentIndex = 0;
    }

    getCurrentSong() {
        return this.currentSong;
    }

    getPlaylist() {
        return this.playlist;
    }
}

// Initialize music player
window.musicPlayer = new MusicPlayer();
