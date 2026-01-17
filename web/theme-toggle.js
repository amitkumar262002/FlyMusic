// Theme Toggle - Dark/Light Mode Switcher
class ThemeToggle {
    constructor() {
        this.currentTheme = 'dark'; // Default theme
        this.themes = {
            dark: {
                name: 'Dark Mode',
                icon: 'fas fa-moon',
                colors: {
                    primary: '#1a1a2e',
                    secondary: '#16213e',
                    accent: '#0f3460',
                    highlight: '#533483',
                    text: '#ffffff',
                    textSecondary: 'rgba(255, 255, 255, 0.7)',
                    gold: '#FFD700'
                }
            },
            light: {
                name: 'Light Mode',
                icon: 'fas fa-sun',
                colors: {
                    primary: '#f8f9fa',
                    secondary: '#ffffff',
                    accent: '#e9ecef',
                    highlight: '#667eea',
                    text: '#333333',
                    textSecondary: 'rgba(0, 0, 0, 0.7)',
                    gold: '#f39c12'
                }
            },
            auto: {
                name: 'Auto Mode',
                icon: 'fas fa-adjust',
                colors: {} // Will be set based on system preference
            }
        };
        
        this.init();
        
        // Add to window for global access
        window.themeToggle = this;
        
        console.log('🎨 Theme Toggle initialized');
    }
    
    init() {
        this.loadSavedTheme();
        this.createThemeToggleButton();
        this.applyTheme(this.currentTheme);
        this.setupSystemThemeListener();
    }
    
    createThemeToggleButton() {
        // Add theme toggle to header
        const headerRight = document.querySelector('.header-right');
        if (headerRight) {
            const themeToggle = document.createElement('button');
            themeToggle.className = 'theme-toggle-btn';
            themeToggle.title = 'Toggle Theme';
            themeToggle.onclick = () => this.toggleTheme();
            
            themeToggle.innerHTML = `<i class="${this.themes[this.currentTheme].icon}"></i>`;
            
            // Insert before clear toasts button
            const clearBtn = headerRight.querySelector('.clear-toasts-btn');
            if (clearBtn) {
                headerRight.insertBefore(themeToggle, clearBtn);
            } else {
                headerRight.appendChild(themeToggle);
            }
        }
        
        // Add theme options to settings modal
        this.addThemeToSettings();
    }
    
    addThemeToSettings() {
        // This will be called when settings modal is created
        document.addEventListener('settingsModalCreated', () => {
            const settingsModal = document.querySelector('.settings-modal .modal-body');
            if (settingsModal) {
                const themeSection = document.createElement('div');
                themeSection.className = 'settings-section';
                themeSection.innerHTML = `
                    <h3>Theme Settings</h3>
                    <div class="setting-item">
                        <label>App Theme</label>
                        <select id="theme-selector" onchange="changeTheme(this.value)">
                            <option value="dark" ${this.currentTheme === 'dark' ? 'selected' : ''}>Dark Mode</option>
                            <option value="light" ${this.currentTheme === 'light' ? 'selected' : ''}>Light Mode</option>
                            <option value="auto" ${this.currentTheme === 'auto' ? 'selected' : ''}>Auto (System)</option>
                        </select>
                    </div>
                    <div class="setting-item">
                        <label>Theme Preview</label>
                        <div class="theme-preview" id="theme-preview">
                            <div class="preview-card">
                                <div class="preview-header"></div>
                                <div class="preview-content"></div>
                            </div>
                        </div>
                    </div>
                `;
                
                // Insert after appearance section
                const appearanceSection = settingsModal.querySelector('.settings-section:nth-child(2)');
                if (appearanceSection) {
                    appearanceSection.after(themeSection);
                } else {
                    settingsModal.appendChild(themeSection);
                }
            }
        });
    }
    
    toggleTheme() {
        const themes = Object.keys(this.themes);
        const currentIndex = themes.indexOf(this.currentTheme);
        const nextIndex = (currentIndex + 1) % themes.length;
        const nextTheme = themes[nextIndex];
        
        this.setTheme(nextTheme);
        
        // Show toast notification
        if (window.app) {
            window.app.showToast(`Switched to ${this.themes[nextTheme].name}`, 'success');
        }
    }
    
    setTheme(themeName) {
        if (!this.themes[themeName]) return;
        
        this.currentTheme = themeName;
        this.applyTheme(themeName);
        this.saveTheme(themeName);
        this.updateThemeButton();
        this.updateThemePreview();
    }
    
    applyTheme(themeName) {
        const theme = this.themes[themeName];
        let colors = theme.colors;
        
        // Handle auto theme
        if (themeName === 'auto') {
            const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
            colors = prefersDark ? this.themes.dark.colors : this.themes.light.colors;
        }
        
        // Apply CSS custom properties
        const root = document.documentElement;
        
        if (themeName === 'light' || (themeName === 'auto' && !window.matchMedia('(prefers-color-scheme: dark)').matches)) {
            this.applyLightTheme(root);
        } else {
            this.applyDarkTheme(root);
        }
        
        // Update body class
        document.body.className = document.body.className.replace(/theme-\w+/g, '');
        document.body.classList.add(`theme-${themeName}`);
        
        // Dispatch theme change event
        document.dispatchEvent(new CustomEvent('themeChanged', {
            detail: { theme: themeName, colors }
        }));
    }
    
    applyDarkTheme(root) {
        root.style.setProperty('--bg-primary', 'linear-gradient(135deg, #1a1a2e 0%, #16213e 30%, #0f3460 70%, #533483 100%)');
        root.style.setProperty('--bg-secondary', 'rgba(26, 26, 46, 0.9)');
        root.style.setProperty('--bg-glass', 'rgba(26, 26, 46, 0.8)');
        root.style.setProperty('--text-primary', '#ffffff');
        root.style.setProperty('--text-secondary', 'rgba(255, 255, 255, 0.7)');
        root.style.setProperty('--accent-color', '#FFD700');
        root.style.setProperty('--border-color', 'rgba(255, 215, 0, 0.2)');
        root.style.setProperty('--shadow-color', 'rgba(0, 0, 0, 0.3)');
    }
    
    applyLightTheme(root) {
        root.style.setProperty('--bg-primary', 'linear-gradient(135deg, #f8f9fa 0%, #e9ecef 30%, #dee2e6 70%, #ced4da 100%)');
        root.style.setProperty('--bg-secondary', 'rgba(255, 255, 255, 0.9)');
        root.style.setProperty('--bg-glass', 'rgba(255, 255, 255, 0.8)');
        root.style.setProperty('--text-primary', '#333333');
        root.style.setProperty('--text-secondary', 'rgba(0, 0, 0, 0.7)');
        root.style.setProperty('--accent-color', '#f39c12');
        root.style.setProperty('--border-color', 'rgba(243, 156, 18, 0.2)');
        root.style.setProperty('--shadow-color', 'rgba(0, 0, 0, 0.1)');
    }
    
    updateThemeButton() {
        const themeBtn = document.querySelector('.theme-toggle-btn i');
        if (themeBtn) {
            themeBtn.className = this.themes[this.currentTheme].icon;
        }
    }
    
    updateThemePreview() {
        const preview = document.getElementById('theme-preview');
        if (preview) {
            const theme = this.currentTheme === 'auto' ? 
                (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light') : 
                this.currentTheme;
            
            preview.className = `theme-preview theme-${theme}`;
        }
    }
    
    setupSystemThemeListener() {
        // Listen for system theme changes
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
        mediaQuery.addListener(() => {
            if (this.currentTheme === 'auto') {
                this.applyTheme('auto');
            }
        });
    }
    
    saveTheme(themeName) {
        localStorage.setItem('saathi-music-theme', themeName);
    }
    
    loadSavedTheme() {
        const saved = localStorage.getItem('saathi-music-theme');
        if (saved && this.themes[saved]) {
            this.currentTheme = saved;
        }
    }
    
    // Utility methods
    getCurrentTheme() {
        return this.currentTheme;
    }
    
    getThemeColors() {
        const theme = this.themes[this.currentTheme];
        if (this.currentTheme === 'auto') {
            const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
            return prefersDark ? this.themes.dark.colors : this.themes.light.colors;
        }
        return theme.colors;
    }
    
    isLightTheme() {
        if (this.currentTheme === 'light') return true;
        if (this.currentTheme === 'auto') {
            return !window.matchMedia('(prefers-color-scheme: dark)').matches;
        }
        return false;
    }
}

// Global functions
window.changeTheme = (themeName) => {
    if (window.themeToggle) {
        window.themeToggle.setTheme(themeName);
    }
};

window.toggleTheme = () => {
    if (window.themeToggle) {
        window.themeToggle.toggleTheme();
    }
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new ThemeToggle();
});
