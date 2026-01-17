// Music Visualizer - Audio Visualization Effects
class MusicVisualizer {
    constructor() {
        this.canvas = null;
        this.ctx = null;
        this.audioContext = null;
        this.analyser = null;
        this.dataArray = null;
        this.bufferLength = null;
        this.isActive = false;
        this.animationId = null;
        
        // Visualization settings
        this.visualizationType = 'bars'; // bars, wave, circle, particles
        this.colors = {
            primary: '#FFD700',
            secondary: '#FF6B6B',
            accent: '#4ECDC4',
            background: 'rgba(26, 26, 46, 0.1)'
        };
        
        this.particles = [];
        this.init();
        
        // Add to window for global access
        window.musicVisualizer = this;
        
        console.log('🎨 Music Visualizer initialized');
    }
    
    init() {
        this.createVisualizerCanvas();
        this.setupControls();
    }
    
    createVisualizerCanvas() {
        // Create visualizer container
        const visualizerContainer = document.createElement('div');
        visualizerContainer.id = 'music-visualizer';
        visualizerContainer.className = 'music-visualizer hidden';
        
        visualizerContainer.innerHTML = `
            <div class="visualizer-header">
                <h3><i class="fas fa-wave-square"></i> Music Visualizer</h3>
                <div class="visualizer-controls">
                    <button class="viz-btn" data-type="bars" title="Bar Visualizer">
                        <i class="fas fa-chart-bar"></i>
                    </button>
                    <button class="viz-btn" data-type="wave" title="Wave Visualizer">
                        <i class="fas fa-water"></i>
                    </button>
                    <button class="viz-btn" data-type="circle" title="Circle Visualizer">
                        <i class="fas fa-circle"></i>
                    </button>
                    <button class="viz-btn" data-type="particles" title="Particle Visualizer">
                        <i class="fas fa-sparkles"></i>
                    </button>
                    <button class="viz-close" onclick="toggleVisualizer()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
            <canvas id="visualizer-canvas"></canvas>
        `;
        
        document.body.appendChild(visualizerContainer);
        
        this.canvas = document.getElementById('visualizer-canvas');
        this.ctx = this.canvas.getContext('2d');
        this.resizeCanvas();
        
        // Handle window resize
        window.addEventListener('resize', () => this.resizeCanvas());
    }
    
    setupControls() {
        const vizButtons = document.querySelectorAll('.viz-btn');
        vizButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const type = e.currentTarget.dataset.type;
                this.setVisualizationType(type);
                
                // Update active button
                vizButtons.forEach(b => b.classList.remove('active'));
                e.currentTarget.classList.add('active');
            });
        });
        
        // Set default active button
        document.querySelector('.viz-btn[data-type="bars"]').classList.add('active');
    }
    
    resizeCanvas() {
        if (this.canvas) {
            this.canvas.width = window.innerWidth;
            this.canvas.height = window.innerHeight;
        }
    }
    
    async connectToAudio(audioElement) {
        try {
            if (!this.audioContext) {
                this.audioContext = new (window.AudioContext || window.webkitAudioContext)();
                this.analyser = this.audioContext.createAnalyser();
                this.analyser.fftSize = 256;
                this.bufferLength = this.analyser.frequencyBinCount;
                this.dataArray = new Uint8Array(this.bufferLength);
                
                const source = this.audioContext.createMediaElementSource(audioElement);
                source.connect(this.analyser);
                this.analyser.connect(this.audioContext.destination);
            }
            
            if (this.audioContext.state === 'suspended') {
                await this.audioContext.resume();
            }
            
            return true;
        } catch (error) {
            console.error('Error connecting to audio:', error);
            return false;
        }
    }
    
    start(audioElement) {
        if (this.isActive) return;
        
        this.connectToAudio(audioElement).then(success => {
            if (success) {
                this.isActive = true;
                this.show();
                this.animate();
            }
        });
    }
    
    stop() {
        this.isActive = false;
        this.hide();
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
            this.animationId = null;
        }
    }
    
    show() {
        const visualizer = document.getElementById('music-visualizer');
        if (visualizer) {
            visualizer.classList.remove('hidden');
            visualizer.classList.add('show');
        }
    }
    
    hide() {
        const visualizer = document.getElementById('music-visualizer');
        if (visualizer) {
            visualizer.classList.remove('show');
            visualizer.classList.add('hidden');
        }
    }
    
    setVisualizationType(type) {
        this.visualizationType = type;
        if (type === 'particles') {
            this.initParticles();
        }
    }
    
    animate() {
        if (!this.isActive) return;
        
        this.animationId = requestAnimationFrame(() => this.animate());
        
        if (this.analyser && this.dataArray) {
            this.analyser.getByteFrequencyData(this.dataArray);
            this.draw();
        }
    }
    
    draw() {
        const width = this.canvas.width;
        const height = this.canvas.height;
        
        // Clear canvas with gradient background
        const gradient = this.ctx.createLinearGradient(0, 0, 0, height);
        gradient.addColorStop(0, 'rgba(26, 26, 46, 0.1)');
        gradient.addColorStop(1, 'rgba(15, 52, 96, 0.1)');
        this.ctx.fillStyle = gradient;
        this.ctx.fillRect(0, 0, width, height);
        
        switch (this.visualizationType) {
            case 'bars':
                this.drawBars();
                break;
            case 'wave':
                this.drawWave();
                break;
            case 'circle':
                this.drawCircle();
                break;
            case 'particles':
                this.drawParticles();
                break;
        }
    }
    
    drawBars() {
        const width = this.canvas.width;
        const height = this.canvas.height;
        const barWidth = (width / this.bufferLength) * 2.5;
        let barHeight;
        let x = 0;
        
        for (let i = 0; i < this.bufferLength; i++) {
            barHeight = (this.dataArray[i] / 255) * height * 0.8;
            
            // Create gradient for each bar
            const gradient = this.ctx.createLinearGradient(0, height - barHeight, 0, height);
            gradient.addColorStop(0, this.colors.primary);
            gradient.addColorStop(0.5, this.colors.secondary);
            gradient.addColorStop(1, this.colors.accent);
            
            this.ctx.fillStyle = gradient;
            this.ctx.fillRect(x, height - barHeight, barWidth, barHeight);
            
            x += barWidth + 1;
        }
    }
    
    drawWave() {
        const width = this.canvas.width;
        const height = this.canvas.height;
        
        this.ctx.lineWidth = 3;
        this.ctx.strokeStyle = this.colors.primary;
        this.ctx.beginPath();
        
        const sliceWidth = width / this.bufferLength;
        let x = 0;
        
        for (let i = 0; i < this.bufferLength; i++) {
            const v = this.dataArray[i] / 128.0;
            const y = v * height / 2;
            
            if (i === 0) {
                this.ctx.moveTo(x, y);
            } else {
                this.ctx.lineTo(x, y);
            }
            
            x += sliceWidth;
        }
        
        this.ctx.lineTo(width, height / 2);
        this.ctx.stroke();
        
        // Add glow effect
        this.ctx.shadowColor = this.colors.primary;
        this.ctx.shadowBlur = 20;
        this.ctx.stroke();
        this.ctx.shadowBlur = 0;
    }
    
    drawCircle() {
        const width = this.canvas.width;
        const height = this.canvas.height;
        const centerX = width / 2;
        const centerY = height / 2;
        const radius = Math.min(width, height) / 4;
        
        for (let i = 0; i < this.bufferLength; i++) {
            const angle = (i / this.bufferLength) * Math.PI * 2;
            const amplitude = (this.dataArray[i] / 255) * radius;
            
            const x1 = centerX + Math.cos(angle) * radius;
            const y1 = centerY + Math.sin(angle) * radius;
            const x2 = centerX + Math.cos(angle) * (radius + amplitude);
            const y2 = centerY + Math.sin(angle) * (radius + amplitude);
            
            // Create gradient for each line
            const gradient = this.ctx.createLinearGradient(x1, y1, x2, y2);
            gradient.addColorStop(0, this.colors.primary);
            gradient.addColorStop(1, this.colors.secondary);
            
            this.ctx.strokeStyle = gradient;
            this.ctx.lineWidth = 2;
            this.ctx.beginPath();
            this.ctx.moveTo(x1, y1);
            this.ctx.lineTo(x2, y2);
            this.ctx.stroke();
        }
    }
    
    initParticles() {
        this.particles = [];
        for (let i = 0; i < 100; i++) {
            this.particles.push({
                x: Math.random() * this.canvas.width,
                y: Math.random() * this.canvas.height,
                vx: (Math.random() - 0.5) * 2,
                vy: (Math.random() - 0.5) * 2,
                size: Math.random() * 3 + 1,
                color: Math.random() > 0.5 ? this.colors.primary : this.colors.secondary
            });
        }
    }
    
    drawParticles() {
        if (this.particles.length === 0) {
            this.initParticles();
        }
        
        const avgFrequency = this.dataArray.reduce((a, b) => a + b) / this.bufferLength;
        const intensity = avgFrequency / 255;
        
        this.particles.forEach(particle => {
            // Update particle position based on audio intensity
            particle.x += particle.vx * (1 + intensity);
            particle.y += particle.vy * (1 + intensity);
            
            // Wrap around screen
            if (particle.x < 0) particle.x = this.canvas.width;
            if (particle.x > this.canvas.width) particle.x = 0;
            if (particle.y < 0) particle.y = this.canvas.height;
            if (particle.y > this.canvas.height) particle.y = 0;
            
            // Draw particle
            this.ctx.fillStyle = particle.color;
            this.ctx.globalAlpha = 0.7 + intensity * 0.3;
            this.ctx.beginPath();
            this.ctx.arc(particle.x, particle.y, particle.size * (1 + intensity), 0, Math.PI * 2);
            this.ctx.fill();
        });
        
        this.ctx.globalAlpha = 1;
    }
}

// Global functions
window.toggleVisualizer = () => {
    if (window.musicVisualizer) {
        if (window.musicVisualizer.isActive) {
            window.musicVisualizer.stop();
        } else {
            const audioElement = document.querySelector('audio');
            if (audioElement && !audioElement.paused) {
                window.musicVisualizer.start(audioElement);
            } else {
                if (window.app) {
                    window.app.showToast('Play a song first to start visualizer', 'info');
                }
            }
        }
    }
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new MusicVisualizer();
});
