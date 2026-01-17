// Sample data for testing and demo purposes
const sampleMusicData = {
  trending: [
    {
      id: 'trending_1',
      title: 'Kesariya',
      artist: 'Arijit Singh',
      album: 'Brahmastra',
      thumbnail: 'https://c.saavncdn.com/191/Brahmastra-Hindi-2022-20220825141240-500x500.jpg',
      duration: '4:28',
      source: 'jiosaavn',
      year: '2022',
      language: 'Hindi',
      genre: 'Bollywood'
    },
    {
      id: 'trending_2',
      title: 'As It Was',
      artist: 'Harry Styles',
      album: "Harry's House",
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273b46f74097655d7f353caab14',
      duration: '2:47',
      source: 'youtube',
      year: '2022',
      language: 'English',
      genre: 'Pop'
    },
    {
      id: 'trending_3',
      title: 'Oo Antava',
      artist: 'Indravathi Chauhan',
      album: 'Pushpa',
      thumbnail: 'https://c.saavncdn.com/165/Pushpa-The-Rise-Telugu-2021-20211216014247-500x500.jpg',
      duration: '3:15',
      source: 'jiosaavn',
      year: '2021',
      language: 'Telugu',
      genre: 'Tollywood'
    },
    {
      id: 'trending_4',
      title: 'Heat Waves',
      artist: 'Glass Animals',
      album: 'Dreamland',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273b0dd6a5cd1dec96c4119c262',
      duration: '3:58',
      source: 'youtube',
      year: '2020',
      language: 'English',
      genre: 'Indie Pop'
    },
    {
      id: 'trending_5',
      title: 'Raataan Lambiyan',
      artist: 'Tanishk Bagchi, Jubin Nautiyal, Asees Kaur',
      album: 'Shershaah',
      thumbnail: 'https://c.saavncdn.com/191/Shershaah-Original-Motion-Picture-Soundtrack--Hindi-2021-20210815181610-500x500.jpg',
      duration: '3:28',
      source: 'jiosaavn',
      year: '2021',
      language: 'Hindi',
      genre: 'Bollywood'
    },
    {
      id: 'trending_6',
      title: 'Maan Meri Jaan',
      artist: 'King',
      album: 'Champagne Talk',
      thumbnail: 'https://c.saavncdn.com/734/Champagne-Talk-Hindi-2022-20221008011951-500x500.jpg',
      duration: '2:37',
      source: 'jiosaavn',
      year: '2022',
      language: 'Hindi',
      genre: 'Hip Hop'
    },
    {
      id: 'trending_7',
      title: 'Anti-Hero',
      artist: 'Taylor Swift',
      album: 'Midnights',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273bb54dde68cd23e2a268ae0f5',
      duration: '3:20',
      source: 'youtube',
      year: '2022',
      language: 'English',
      genre: 'Pop'
    },
    {
      id: 'trending_8',
      title: 'Unholy',
      artist: 'Sam Smith ft. Kim Petras',
      album: 'Unholy',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273cd222052a2594be29a6616b5',
      duration: '2:36',
      source: 'youtube',
      year: '2022',
      language: 'English',
      genre: 'Pop'
    }
  ],

  bollywood: [
    {
      id: 'bollywood_1',
      title: 'Tum Hi Ho',
      artist: 'Arijit Singh',
      album: 'Aashiqui 2',
      thumbnail: 'https://c.saavncdn.com/430/Aashiqui-2-Hindi-2013-500x500.jpg',
      duration: '4:22',
      source: 'jiosaavn',
      year: '2013',
      language: 'Hindi',
      genre: 'Bollywood'
    },
    {
      id: 'bollywood_2',
      title: 'Pal Pal Dil Ke Paas',
      artist: 'Kishore Kumar',
      album: 'Blackmail',
      thumbnail: 'https://c.saavncdn.com/430/Blackmail-Hindi-1973-500x500.jpg',
      duration: '4:15',
      source: 'jiosaavn',
      year: '1973',
      language: 'Hindi',
      genre: 'Classic Bollywood'
    },
    {
      id: 'bollywood_3',
      title: 'Channa Mereya',
      artist: 'Arijit Singh',
      album: 'Ae Dil Hai Mushkil',
      thumbnail: 'https://c.saavncdn.com/243/Ae-Dil-Hai-Mushkil-Hindi-2016-500x500.jpg',
      duration: '4:49',
      source: 'jiosaavn',
      year: '2016',
      language: 'Hindi',
      genre: 'Bollywood'
    },
    {
      id: 'bollywood_4',
      title: 'Jeene Laga Hoon',
      artist: 'Atif Aslam, Shreya Ghoshal',
      album: 'Ramaiya Vastavaiya',
      thumbnail: 'https://c.saavncdn.com/430/Ramaiya-Vastavaiya-Hindi-2013-500x500.jpg',
      duration: '4:32',
      source: 'jiosaavn',
      year: '2013',
      language: 'Hindi',
      genre: 'Bollywood'
    },
    {
      id: 'bollywood_5',
      title: 'Malang Sajna',
      artist: 'Sachet Tandon, Parampara Thakur',
      album: 'Malang',
      thumbnail: 'https://c.saavncdn.com/430/Malang-Hindi-2020-20200207041155-500x500.jpg',
      duration: '3:45',
      source: 'jiosaavn',
      year: '2020',
      language: 'Hindi',
      genre: 'Bollywood'
    },
    {
      id: 'bollywood_6',
      title: 'Apna Bana Le',
      artist: 'Arijit Singh',
      album: 'Bhediya',
      thumbnail: 'https://c.saavncdn.com/191/Bhediya-Hindi-2022-20221123151830-500x500.jpg',
      duration: '3:54',
      source: 'jiosaavn',
      year: '2022',
      language: 'Hindi',
      genre: 'Bollywood'
    },
    {
      id: 'bollywood_7',
      title: 'Deva Deva',
      artist: 'Arijit Singh, Jonita Gandhi',
      album: 'Brahmastra',
      thumbnail: 'https://c.saavncdn.com/191/Brahmastra-Hindi-2022-20220825141240-500x500.jpg',
      duration: '4:32',
      source: 'jiosaavn',
      year: '2022',
      language: 'Hindi',
      genre: 'Bollywood'
    },
    {
      id: 'bollywood_8',
      title: 'Phir Na Aisi Raat Aayegi',
      artist: 'Lata Mangeshkar, Mukesh',
      album: 'Lal Patthar',
      thumbnail: 'https://c.saavncdn.com/430/Lal-Patthar-Hindi-1971-500x500.jpg',
      duration: '4:18',
      source: 'jiosaavn',
      year: '1971',
      language: 'Hindi',
      genre: 'Classic Bollywood'
    }
  ],

  international: [
    {
      id: 'international_1',
      title: 'Shape of You',
      artist: 'Ed Sheeran',
      album: '÷ (Divide)',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273ba5db46f4b838ef6027e6f96',
      duration: '3:53',
      source: 'youtube',
      year: '2017',
      language: 'English',
      genre: 'Pop'
    },
    {
      id: 'international_2',
      title: 'Blinding Lights',
      artist: 'The Weeknd',
      album: 'After Hours',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b2738863bc11d2aa12b54f5aeb36',
      duration: '3:20',
      source: 'youtube',
      year: '2019',
      language: 'English',
      genre: 'Synthpop'
    },
    {
      id: 'international_3',
      title: 'Levitating',
      artist: 'Dua Lipa',
      album: 'Future Nostalgia',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273f056627f16bd7400de0b5052',
      duration: '3:23',
      source: 'youtube',
      year: '2020',
      language: 'English',
      genre: 'Disco Pop'
    },
    {
      id: 'international_4',
      title: 'Stay',
      artist: 'The Kid LAROI, Justin Bieber',
      album: 'Stay',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273e2e352d89826aef6dbd5ff8f',
      duration: '2:21',
      source: 'youtube',
      year: '2021',
      language: 'English',
      genre: 'Pop'
    },
    {
      id: 'international_5',
      title: 'Good 4 U',
      artist: 'Olivia Rodrigo',
      album: 'SOUR',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273a91c10fe9472d9bd89802e5a',
      duration: '2:58',
      source: 'youtube',
      year: '2021',
      language: 'English',
      genre: 'Pop Rock'
    },
    {
      id: 'international_6',
      title: 'Flowers',
      artist: 'Miley Cyrus',
      album: 'Endless Summer Vacation',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273f4d5cc8e2c48f7b610160b4b',
      duration: '3:20',
      source: 'youtube',
      year: '2023',
      language: 'English',
      genre: 'Pop'
    },
    {
      id: 'international_7',
      title: 'Calm Down',
      artist: 'Rema, Selena Gomez',
      album: 'Rave & Roses',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273e20e5c366b497518353497b0',
      duration: '3:59',
      source: 'youtube',
      year: '2022',
      language: 'English',
      genre: 'Afrobeats'
    },
    {
      id: 'international_8',
      title: 'Bad Habit',
      artist: 'Steve Lacy',
      album: 'Gemini Rights',
      thumbnail: 'https://i.scdn.co/image/ab67616d0000b273b85259a971157e9f2728457a',
      duration: '3:51',
      source: 'youtube',
      year: '2022',
      language: 'English',
      genre: 'R&B'
    }
  ],

  searchSuggestions: [
    'Arijit Singh songs',
    'Bollywood hits 2024',
    'English pop songs',
    'AR Rahman music',
    'Trending Hindi songs',
    'International hits',
    'Romantic songs',
    'Party songs',
    'Classical music',
    'Regional music'
  ]
};

// Export for use in other files
if (typeof window !== 'undefined') {
  window.sampleMusicData = sampleMusicData;
}

// For Node.js environments
if (typeof module !== 'undefined' && module.exports) {
  module.exports = sampleMusicData;
}
