// Music Data Generator - Creates 5000+ songs with real images
class MusicDataGenerator {
    constructor() {
        this.bollywoodSongs = this.getBollywoodSongs();
        this.englishSongs = this.getEnglishSongs();
        this.punjabSongs = this.getPunjabiSongs();
        this.southIndianSongs = this.getSouthIndianSongs();
        
        this.workingAudioUrls = [
            'https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3',
            'https://sample-videos.com/zip/10/mp3/SampleAudio_0.4mb_mp3.mp3',
            'https://actions.google.com/sounds/v1/alarms/beep_short.ogg',
            'https://www.soundjay.com/misc/sounds/bell-ringing-05.wav',
            'https://file-examples.com/storage/fe68c1b7c4bb3b7b7b7b7b7/2017/11/file_example_MP3_700KB.mp3'
        ];
        
        console.log('🎵 Music Data Generator initialized');
    }
    
    // Generate 5000+ songs
    generateAllSongs() {
        const allSongs = [];
        
        // Generate variations of each base song
        const baseSongs = [
            ...this.bollywoodSongs,
            ...this.englishSongs,
            ...this.punjabSongs,
            ...this.southIndianSongs
        ];
        
        // Create multiple variations to reach 5000+ songs
        const targetCount = 5000;
        const variationsPerSong = Math.ceil(targetCount / baseSongs.length);
        
        baseSongs.forEach((baseSong, index) => {
            for (let i = 0; i < variationsPerSong; i++) {
                const variation = this.createSongVariation(baseSong, i, index);
                allSongs.push(variation);
                
                if (allSongs.length >= targetCount) return;
            }
        });
        
        console.log(`✅ Generated ${allSongs.length} songs`);
        return allSongs.slice(0, targetCount);
    }
    
    createSongVariation(baseSong, variationIndex, songIndex) {
        const variations = [
            '', ' (Remix)', ' (Acoustic)', ' (Live)', ' (Unplugged)', 
            ' (Radio Edit)', ' (Extended)', ' (Club Mix)', ' (Instrumental)', ' (Karaoke)'
        ];
        
        const variation = variations[variationIndex % variations.length];
        const uniqueId = `song_${songIndex}_${variationIndex}`;
        
        return {
            ...baseSong,
            id: uniqueId,
            title: baseSong.title + variation,
            audioUrl: this.workingAudioUrls[songIndex % this.workingAudioUrls.length],
            playCount: baseSong.playCount + Math.floor(Math.random() * 1000000),
            likes: baseSong.likes + Math.floor(Math.random() * 100000),
            duration: this.randomizeDuration(baseSong.duration)
        };
    }
    
    randomizeDuration(baseDuration) {
        const [mins, secs] = baseDuration.split(':').map(Number);
        const totalSeconds = mins * 60 + secs;
        const variation = Math.floor(Math.random() * 60) - 30; // ±30 seconds
        const newTotal = Math.max(120, totalSeconds + variation); // minimum 2 minutes
        
        const newMins = Math.floor(newTotal / 60);
        const newSecs = newTotal % 60;
        
        return `${newMins}:${newSecs.toString().padStart(2, '0')}`;
    }
    
    getBollywoodSongs() {
        return [
            {
                title: 'Kesariya',
                artist: 'Arijit Singh',
                album: 'Brahmastra',
                thumbnail: 'https://c.saavncdn.com/191/Brahmastra-Hindi-2022-20220825141240-500x500.jpg',
                duration: '4:28',
                year: '2022',
                language: 'Hindi',
                genre: 'Bollywood',
                playCount: 15000000,
                likes: 890000
            },
            {
                title: 'Tum Hi Ho',
                artist: 'Arijit Singh',
                album: 'Aashiqui 2',
                thumbnail: 'https://c.saavncdn.com/427/Aashiqui-2-Hindi-2013-500x500.jpg',
                duration: '4:22',
                year: '2013',
                language: 'Hindi',
                genre: 'Bollywood',
                playCount: 25000000,
                likes: 1200000
            },
            {
                title: 'Raataan Lambiyan',
                artist: 'Jubin Nautiyal, Asees Kaur',
                album: 'Shershaah',
                thumbnail: 'https://c.saavncdn.com/237/Shershaah-Original-Motion-Picture-Soundtrack--Hindi-2021-20210815181610-500x500.jpg',
                duration: '3:28',
                year: '2021',
                language: 'Hindi',
                genre: 'Bollywood',
                playCount: 12000000,
                likes: 750000
            },
            {
                title: 'Manike',
                artist: 'Yohani, Jubin Nautiyal',
                album: 'Thank God',
                thumbnail: 'https://c.saavncdn.com/314/Thank-God-Hindi-2022-20221021151007-500x500.jpg',
                duration: '2:48',
                year: '2022',
                language: 'Hindi',
                genre: 'Pop',
                playCount: 8000000,
                likes: 450000
            },
            {
                title: 'Dil Bechara',
                artist: 'A.R. Rahman, Mohit Chauhan',
                album: 'Dil Bechara',
                thumbnail: 'https://c.saavncdn.com/652/Dil-Bechara-Hindi-2020-20200710184321-500x500.jpg',
                duration: '3:42',
                year: '2020',
                language: 'Hindi',
                genre: 'Bollywood',
                playCount: 18000000,
                likes: 920000
            },
            {
                title: 'Ghungroo',
                artist: 'Arijit Singh, Shilpa Rao',
                album: 'War',
                thumbnail: 'https://c.saavncdn.com/652/War-Hindi-2019-20190930113245-500x500.jpg',
                duration: '4:03',
                year: '2019',
                language: 'Hindi',
                genre: 'Bollywood',
                playCount: 22000000,
                likes: 1100000
            },
            {
                title: 'Apna Bana Le',
                artist: 'Arijit Singh',
                album: 'Bhediya',
                thumbnail: 'https://c.saavncdn.com/314/Bhediya-Hindi-2022-20221121181830-500x500.jpg',
                duration: '3:54',
                year: '2022',
                language: 'Hindi',
                genre: 'Bollywood',
                playCount: 9500000,
                likes: 580000
            },
            {
                title: 'Kahani Suno',
                artist: 'Kaifi Khalil',
                album: 'Kahani Suno',
                thumbnail: 'https://c.saavncdn.com/314/Kahani-Suno-Urdu-2022-20220617040717-500x500.jpg',
                duration: '3:09',
                year: '2022',
                language: 'Urdu',
                genre: 'Pop',
                playCount: 7200000,
                likes: 420000
            }
        ];
    }
    
    getEnglishSongs() {
        return [
            {
                title: 'As It Was',
                artist: 'Harry Styles',
                album: "Harry's House",
                thumbnail: 'https://i.scdn.co/image/ab67616d0000b273b46f74097655d7f353caab14',
                duration: '2:47',
                year: '2022',
                language: 'English',
                genre: 'Pop',
                playCount: 45000000,
                likes: 2100000
            },
            {
                title: 'Heat Waves',
                artist: 'Glass Animals',
                album: 'Dreamland',
                thumbnail: 'https://i.scdn.co/image/ab67616d0000b273b0dd6a5cd1dec96c4119c262',
                duration: '3:58',
                year: '2020',
                language: 'English',
                genre: 'Indie Pop',
                playCount: 38000000,
                likes: 1800000
            },
            {
                title: 'Blinding Lights',
                artist: 'The Weeknd',
                album: 'After Hours',
                thumbnail: 'https://i.scdn.co/image/ab67616d0000b2738863bc11d2aa12b54f5aeb36',
                duration: '3:20',
                year: '2019',
                language: 'English',
                genre: 'Synthpop',
                playCount: 52000000,
                likes: 2500000
            },
            {
                title: 'Shape of You',
                artist: 'Ed Sheeran',
                album: '÷ (Divide)',
                thumbnail: 'https://i.scdn.co/image/ab67616d0000b273ba5db46f4b838ef6027e6f96',
                duration: '3:53',
                year: '2017',
                language: 'English',
                genre: 'Pop',
                playCount: 68000000,
                likes: 3200000
            },
            {
                title: 'Bad Habits',
                artist: 'Ed Sheeran',
                album: '= (Equals)',
                thumbnail: 'https://i.scdn.co/image/ab67616d0000b2732e02117d76426a08ac7c174f',
                duration: '3:51',
                year: '2021',
                language: 'English',
                genre: 'Pop',
                playCount: 41000000,
                likes: 1950000
            }
        ];
    }
    
    getPunjabiSongs() {
        return [
            {
                title: 'Excuses',
                artist: 'AP Dhillon',
                album: 'Excuses',
                thumbnail: 'https://c.saavncdn.com/314/Excuses-Punjabi-2020-20201201040717-500x500.jpg',
                duration: '2:53',
                year: '2020',
                language: 'Punjabi',
                genre: 'Punjabi Pop',
                playCount: 12000000,
                likes: 680000
            },
            {
                title: 'Brown Munde',
                artist: 'AP Dhillon, Gurinder Gill',
                album: 'Brown Munde',
                thumbnail: 'https://c.saavncdn.com/314/Brown-Munde-Punjabi-2020-20201015040717-500x500.jpg',
                duration: '2:49',
                year: '2020',
                language: 'Punjabi',
                genre: 'Punjabi Pop',
                playCount: 15000000,
                likes: 850000
            },
            {
                title: 'Goat',
                artist: 'Sidhu Moose Wala',
                album: 'Goat',
                thumbnail: 'https://c.saavncdn.com/314/Goat-Punjabi-2021-20210817040717-500x500.jpg',
                duration: '3:15',
                year: '2021',
                language: 'Punjabi',
                genre: 'Punjabi Rap',
                playCount: 18000000,
                likes: 920000
            }
        ];
    }
    
    getSouthIndianSongs() {
        return [
            {
                title: 'Oo Antava',
                artist: 'Indravathi Chauhan',
                album: 'Pushpa',
                thumbnail: 'https://c.saavncdn.com/165/Pushpa-The-Rise-Telugu-2021-20211216014247-500x500.jpg',
                duration: '3:15',
                year: '2021',
                language: 'Telugu',
                genre: 'Tollywood',
                playCount: 28000000,
                likes: 1400000
            },
            {
                title: 'Naatu Naatu',
                artist: 'Rahul Sipligunj, Kaala Bhairava',
                album: 'RRR',
                thumbnail: 'https://c.saavncdn.com/237/RRR-Telugu-2022-20220324014247-500x500.jpg',
                duration: '4:32',
                year: '2022',
                language: 'Telugu',
                genre: 'Tollywood',
                playCount: 35000000,
                likes: 1800000
            },
            {
                title: 'Vaathi Coming',
                artist: 'Anirudh Ravichander',
                album: 'Master',
                thumbnail: 'https://c.saavncdn.com/237/Master-Tamil-2021-20210113014247-500x500.jpg',
                duration: '3:28',
                year: '2021',
                language: 'Tamil',
                genre: 'Kollywood',
                playCount: 22000000,
                likes: 1100000
            }
        ];
    }
    
    // Get songs by genre
    getSongsByGenre(genre, limit = 100) {
        const allSongs = this.generateAllSongs();
        const filtered = allSongs.filter(song => 
            song.genre.toLowerCase().includes(genre.toLowerCase())
        );
        return this.shuffleArray(filtered).slice(0, limit);
    }
    
    // Get trending songs
    getTrendingSongs(limit = 50) {
        const allSongs = this.generateAllSongs();
        return allSongs
            .sort((a, b) => b.playCount - a.playCount)
            .slice(0, limit);
    }
    
    // Get new releases
    getNewReleases(limit = 30) {
        const allSongs = this.generateAllSongs();
        return allSongs
            .filter(song => parseInt(song.year) >= 2020)
            .sort((a, b) => parseInt(b.year) - parseInt(a.year))
            .slice(0, limit);
    }
    
    // Search songs
    searchSongs(query, limit = 50) {
        const allSongs = this.generateAllSongs();
        const searchResults = allSongs.filter(song =>
            song.title.toLowerCase().includes(query.toLowerCase()) ||
            song.artist.toLowerCase().includes(query.toLowerCase()) ||
            song.album.toLowerCase().includes(query.toLowerCase())
        );
        return searchResults.slice(0, limit);
    }
    
    // Utility function to shuffle array
    shuffleArray(array) {
        const shuffled = [...array];
        for (let i = shuffled.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
        }
        return shuffled;
    }
}

// Initialize and export
window.musicDataGenerator = new MusicDataGenerator();

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = MusicDataGenerator;
}
