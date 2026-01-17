// Lyrics Display - Show song lyrics with sync
class LyricsDisplay {
    constructor() {
        this.currentLyrics = null;
        this.isVisible = false;
        this.syncedLyrics = [];
        this.currentLineIndex = 0;
        
        // Sample lyrics database
        this.lyricsDatabase = {
            'tere_ishk_mein': {
                title: 'Tere Ishk Mein',
                artist: 'A.R. Rahman, Arijit Singh',
                lyrics: [
                    { time: 0, text: "Tere ishk mein jo bhi doob gaya" },
                    { time: 15, text: "Woh kabhi bahar aa na saka" },
                    { time: 30, text: "Tere ishk mein jo bhi doob gaya" },
                    { time: 45, text: "Woh kabhi bahar aa na saka" },
                    { time: 60, text: "Main bhi doob gaya hoon" },
                    { time: 75, text: "Tere pyaar ke saagar mein" },
                    { time: 90, text: "Ab mujhe bachane wala" },
                    { time: 105, text: "Koi nahin hai yahan" }
                ]
            },
            'deewaniyat': {
                title: 'Deewaniyat',
                artist: 'Vishal Mishra',
                lyrics: [
                    { time: 0, text: "Yeh deewaniyat hai" },
                    { time: 12, text: "Tere ishq ki" },
                    { time: 24, text: "Main pagal ho gaya hoon" },
                    { time: 36, text: "Tere pyaar mein" },
                    { time: 48, text: "Deewaniyat hai yeh" },
                    { time: 60, text: "Mohabbat ki" },
                    { time: 72, text: "Dil mein bas tu hi tu hai" },
                    { time: 84, text: "Aur kuch bhi nahin" }
                ]
            },
            'default': {
                title: 'Sample Song',
                artist: 'Unknown Artist',
                lyrics: [
                    { time: 0, text: "🎵 Music is the universal language" },
                    { time: 10, text: "🎶 That speaks to every heart" },
                    { time: 20, text: "🎵 Let the rhythm take you away" },
                    { time: 30, text: "🎶 To a world of melody and dreams" },
                    { time: 40, text: "🎵 Where every note tells a story" },
                    { time: 50, text: "🎶 And every beat brings us together" },
                    { time: 60, text: "🎵 In the harmony of life" },
                    { time: 70, text: "🎶 Music never ends..." }
                ]
            }
        };
        
        this.init();
        
        // Add to window for global access
        window.lyricsDisplay = this;
        
        console.log('🎤 Lyrics Display initialized');
    }
    
    init() {
        this.createLyricsContainer();
        this.setupEventListeners();
    }
    
    createLyricsContainer() {
        const lyricsContainer = document.createElement('div');
        lyricsContainer.id = 'lyrics-display';
        lyricsContainer.className = 'lyrics-display hidden';
        
        lyricsContainer.innerHTML = `
            <div class="lyrics-header">
                <div class="lyrics-info">
                    <h3 id="lyrics-title">Song Title</h3>
                    <p id="lyrics-artist">Artist Name</p>
                </div>
                <div class="lyrics-controls">
                    <button class="lyrics-btn" onclick="toggleLyricsSize()" title="Toggle Size">
                        <i class="fas fa-expand-alt"></i>
                    </button>
                    <button class="lyrics-btn" onclick="toggleLyricsSync()" title="Toggle Sync">
                        <i class="fas fa-sync"></i>
                    </button>
                    <button class="lyrics-close" onclick="hideLyrics()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
            <div class="lyrics-content" id="lyrics-content">
                <div class="lyrics-text" id="lyrics-text">
                    <p>No lyrics available for this song</p>
                </div>
            </div>
            <div class="lyrics-footer">
                <div class="lyrics-progress">
                    <div class="lyrics-progress-bar" id="lyrics-progress"></div>
                </div>
                <p class="lyrics-source">Lyrics powered by Saathi Music</p>
            </div>
        `;
        
        document.body.appendChild(lyricsContainer);
    }
    
    setupEventListeners() {
        // Listen for song changes
        document.addEventListener('songChanged', (event) => {
            this.loadLyrics(event.detail.song);
        });
        
        // Listen for time updates
        document.addEventListener('timeUpdate', (event) => {
            this.updateLyricsSync(event.detail.currentTime);
        });
    }
    
    show(song) {
        const lyricsContainer = document.getElementById('lyrics-display');
        if (lyricsContainer) {
            this.loadLyrics(song);
            lyricsContainer.classList.remove('hidden');
            lyricsContainer.classList.add('show');
            this.isVisible = true;
        }
    }
    
    hide() {
        const lyricsContainer = document.getElementById('lyrics-display');
        if (lyricsContainer) {
            lyricsContainer.classList.remove('show');
            lyricsContainer.classList.add('hidden');
            this.isVisible = false;
        }
    }
    
    toggle(song) {
        if (this.isVisible) {
            this.hide();
        } else {
            this.show(song);
        }
    }
    
    loadLyrics(song) {
        if (!song) return;
        
        // Update song info
        const titleElement = document.getElementById('lyrics-title');
        const artistElement = document.getElementById('lyrics-artist');
        
        if (titleElement) titleElement.textContent = song.title || 'Unknown Song';
        if (artistElement) artistElement.textContent = song.artist || 'Unknown Artist';
        
        // Get lyrics from database
        const lyricsKey = this.getSongKey(song);
        this.currentLyrics = this.lyricsDatabase[lyricsKey] || this.lyricsDatabase['default'];
        this.syncedLyrics = this.currentLyrics.lyrics;
        this.currentLineIndex = 0;
        
        this.renderLyrics();
    }
    
    getSongKey(song) {
        // Simple key generation based on song title
        const title = song.title.toLowerCase();
        if (title.includes('tere ishk')) return 'tere_ishk_mein';
        if (title.includes('deewaniyat')) return 'deewaniyat';
        return 'default';
    }
    
    renderLyrics() {
        const lyricsText = document.getElementById('lyrics-text');
        if (!lyricsText || !this.syncedLyrics) return;
        
        lyricsText.innerHTML = this.syncedLyrics.map((line, index) => `
            <div class="lyrics-line" data-time="${line.time}" data-index="${index}">
                ${line.text}
            </div>
        `).join('');
    }
    
    updateLyricsSync(currentTime) {
        if (!this.syncedLyrics || !this.isVisible) return;
        
        // Find current line based on time
        let activeIndex = -1;
        for (let i = 0; i < this.syncedLyrics.length; i++) {
            if (currentTime >= this.syncedLyrics[i].time) {
                activeIndex = i;
            } else {
                break;
            }
        }
        
        // Update active line
        const lines = document.querySelectorAll('.lyrics-line');
        lines.forEach((line, index) => {
            line.classList.remove('active', 'past');
            if (index === activeIndex) {
                line.classList.add('active');
                // Scroll to active line
                line.scrollIntoView({ behavior: 'smooth', block: 'center' });
            } else if (index < activeIndex) {
                line.classList.add('past');
            }
        });
        
        // Update progress bar
        const progress = document.getElementById('lyrics-progress');
        if (progress && this.syncedLyrics.length > 0) {
            const lastTime = this.syncedLyrics[this.syncedLyrics.length - 1].time;
            const progressPercent = Math.min((currentTime / lastTime) * 100, 100);
            progress.style.width = `${progressPercent}%`;
        }
        
        this.currentLineIndex = activeIndex;
    }
    
    toggleSize() {
        const lyricsContainer = document.getElementById('lyrics-display');
        if (lyricsContainer) {
            lyricsContainer.classList.toggle('fullscreen');
        }
    }
    
    toggleSync() {
        const syncBtn = document.querySelector('.lyrics-btn[onclick="toggleLyricsSync()"] i');
        if (syncBtn) {
            syncBtn.classList.toggle('fa-sync');
            syncBtn.classList.toggle('fa-sync-alt');
            // Toggle sync functionality here
        }
    }
    
    // Search lyrics online (placeholder for future implementation)
    async searchLyricsOnline(song) {
        try {
            // This would integrate with lyrics APIs like Genius, Musixmatch, etc.
            console.log('Searching lyrics for:', song.title, 'by', song.artist);
            
            // For now, return sample lyrics
            return {
                title: song.title,
                artist: song.artist,
                lyrics: [
                    { time: 0, text: "🎵 Lyrics not found online" },
                    { time: 5, text: "🎶 Showing sample lyrics instead" },
                    { time: 10, text: "🎵 Enjoy the music!" }
                ]
            };
        } catch (error) {
            console.error('Error searching lyrics:', error);
            return null;
        }
    }
}

// Global functions
window.showLyrics = (song) => {
    if (window.lyricsDisplay) {
        window.lyricsDisplay.show(song);
    }
};

window.hideLyrics = () => {
    if (window.lyricsDisplay) {
        window.lyricsDisplay.hide();
    }
};

window.toggleLyrics = (song) => {
    if (window.lyricsDisplay) {
        window.lyricsDisplay.toggle(song);
    }
};

window.toggleLyricsSize = () => {
    if (window.lyricsDisplay) {
        window.lyricsDisplay.toggleSize();
    }
};

window.toggleLyricsSync = () => {
    if (window.lyricsDisplay) {
        window.lyricsDisplay.toggleSync();
    }
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new LyricsDisplay();
});
