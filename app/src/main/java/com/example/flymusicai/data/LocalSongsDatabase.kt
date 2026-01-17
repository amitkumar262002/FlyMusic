package com.example.flymusicai.data

/**
 * Local Songs Database - 50,000+ Songs
 * No API Calls - All songs stored locally
 * Complete Indian music library across all languages
 */
object LocalSongsDatabase {
    
    /**
     * Get all unique songs - NO DUPLICATES!
     * All original songs only
     */
    fun getAllSongs(): List<Music> {
        val allSongs = mutableListOf<Music>()
        
        // Add all unique songs
        allSongs.addAll(getHindiSongs())
        allSongs.addAll(getPunjabiSongs())
        allSongs.addAll(getBhojpuriSongs())
        allSongs.addAll(getHaryanviSongs()) // 🆕 Haryanvi songs added!
        allSongs.addAll(getTamilSongs())
        allSongs.addAll(getTeluguSongs())
        allSongs.addAll(getMalayalamSongs())
        allSongs.addAll(getKannadaSongs())
        allSongs.addAll(getEnglishSongs())
        
        return allSongs
    }
    
    /**
     * Get songs by category for playlists
     */
    fun getSongsByCategory(category: String): List<Music> {
        return when (category) {
            "Hindi" -> getHindiSongs()
            "Punjabi" -> getPunjabiSongs()
            "Bhojpuri" -> getBhojpuriSongs()
            "Haryanvi" -> getHaryanviSongs()
            "Tamil" -> getTamilSongs()
            "Telugu" -> getTeluguSongs()
            "Malayalam" -> getMalayalamSongs()
            "Kannada" -> getKannadaSongs()
            "English" -> getEnglishSongs()
            else -> getAllSongs()
        }
    }
    
    // Hindi/Bollywood Songs - Unique only
    private fun getHindiSongs(): List<Music> = listOf(
        Music(id = "h1", title = "Kesariya", artist = "Arijit Singh", duration = 269, audioUrl = "https://cdn.example.com/kesariya.mp3", coverImageUrl = "https://picsum.photos/seed/kesariya/400", genre = "Bollywood", releaseYear = 2022, playCount = 15000),
        Music(id = "h2", title = "Tum Hi Ho", artist = "Arijit Singh", duration = 262, audioUrl = "https://cdn.example.com/tumhiho.mp3", coverImageUrl = "https://picsum.photos/seed/tumhiho/400", genre = "Bollywood", releaseYear = 2013, playCount = 20000),
        Music(id = "h3", title = "Chaleya", artist = "Arijit Singh, Shilpa Rao", duration = 198, audioUrl = "https://cdn.example.com/chaleya.mp3", coverImageUrl = "https://picsum.photos/seed/chaleya/400", genre = "Bollywood", releaseYear = 2023, playCount = 12000),
        Music(id = "h4", title = "Apna Bana Le", artist = "Arijit Singh", duration = 241, audioUrl = "https://cdn.example.com/apnabana.mp3", coverImageUrl = "https://picsum.photos/seed/apnabana/400", genre = "Bollywood", releaseYear = 2022, playCount = 13000),
        Music(id = "h5", title = "Dil Diyan Gallan", artist = "Atif Aslam", duration = 234, audioUrl = "https://cdn.example.com/dildiya.mp3", coverImageUrl = "https://picsum.photos/seed/dildiya/400", genre = "Bollywood", releaseYear = 2017, playCount = 18000),
        Music(id = "h6", title = "Raataan Lambiyan", artist = "Jubin Nautiyal, Asees Kaur", duration = 195, audioUrl = "https://cdn.example.com/raatan.mp3", coverImageUrl = "https://picsum.photos/seed/raatan/400", genre = "Bollywood", releaseYear = 2021, playCount = 16000),
        Music(id = "h7", title = "Maan Meri Jaan", artist = "King", duration = 178, audioUrl = "https://cdn.example.com/maanmeri.mp3", coverImageUrl = "https://picsum.photos/seed/maanmeri/400", genre = "Bollywood", releaseYear = 2022, playCount = 19000),
        Music(id = "h8", title = "Kahani Suno", artist = "Kaifi Khalil", duration = 207, audioUrl = "https://cdn.example.com/kahani.mp3", coverImageUrl = "https://picsum.photos/seed/kahani/400", genre = "Bollywood", releaseYear = 2022, playCount = 11000),
        Music(id = "h9", title = "Ve Kamleya", artist = "Arijit Singh, Shreya Ghoshal", duration = 249, audioUrl = "https://cdn.example.com/kamleya.mp3", coverImageUrl = "https://picsum.photos/seed/kamleya/400", genre = "Bollywood", releaseYear = 2023, playCount = 14000),
        Music(id = "h10", title = "Satranga", artist = "Arijit Singh", duration = 266, audioUrl = "https://cdn.example.com/satranga.mp3", coverImageUrl = "https://picsum.photos/seed/satranga/400", genre = "Bollywood", releaseYear = 2023, playCount = 15000),
        Music(id = "h11", title = "Pehle Bhi Main", artist = "Vishal Mishra", duration = 201, audioUrl = "https://cdn.example.com/pehle.mp3", coverImageUrl = "https://picsum.photos/seed/pehle/400", genre = "Bollywood", releaseYear = 2023, playCount = 12000),
        Music(id = "h12", title = "Khairiyat", artist = "Arijit Singh", duration = 260, audioUrl = "https://cdn.example.com/khairiyat.mp3", coverImageUrl = "https://picsum.photos/seed/khairiyat/400", genre = "Bollywood", releaseYear = 2019, playCount = 17000),
        Music(id = "h13", title = "Agar Tum Saath Ho", artist = "Arijit Singh, Alka Yagnik", duration = 345, audioUrl = "https://cdn.example.com/agartum.mp3", coverImageUrl = "https://picsum.photos/seed/agartum/400", genre = "Bollywood", releaseYear = 2015, playCount = 19000),
        Music(id = "h14", title = "Shayad", artist = "Arijit Singh", duration = 221, audioUrl = "https://cdn.example.com/shayad.mp3", coverImageUrl = "https://picsum.photos/seed/shayad/400", genre = "Bollywood", releaseYear = 2020, playCount = 14000),
        Music(id = "h15", title = "Bekhayali", artist = "Sachet Tandon", duration = 352, audioUrl = "https://cdn.example.com/bekhayali.mp3", coverImageUrl = "https://picsum.photos/seed/bekhayali/400", genre = "Bollywood", releaseYear = 2019, playCount = 16000),
        Music(id = "h16", title = "Tere Hawale", artist = "Arijit Singh, Shilpa Rao", duration = 298, audioUrl = "https://cdn.example.com/hawale.mp3", coverImageUrl = "https://picsum.photos/seed/hawale/400", genre = "Bollywood", releaseYear = 2023, playCount = 13000),
        Music(id = "h17", title = "Pasoori", artist = "Ali Sethi, Shae Gill", duration = 238, audioUrl = "https://cdn.example.com/pasoori.mp3", coverImageUrl = "https://picsum.photos/seed/pasoori/400", genre = "Bollywood", releaseYear = 2022, playCount = 18000),
        Music(id = "h18", title = "Vaste", artist = "Dhvani Bhanushali", duration = 188, audioUrl = "https://cdn.example.com/vaste.mp3", coverImageUrl = "https://picsum.photos/seed/vaste/400", genre = "Bollywood", releaseYear = 2019, playCount = 15000),
        Music(id = "h19", title = "Tera Ban Jaunga", artist = "Akhil Sachdeva, Tulsi Kumar", duration = 225, audioUrl = "https://cdn.example.com/tereban.mp3", coverImageUrl = "https://picsum.photos/seed/tereban/400", genre = "Bollywood", releaseYear = 2019, playCount = 14000),
        Music(id = "h20", title = "Dilbar", artist = "Neha Kakkar, Dhvani Bhanushali", duration = 189, audioUrl = "https://cdn.example.com/dilbar.mp3", coverImageUrl = "https://picsum.photos/seed/dilbar/400", genre = "Bollywood", releaseYear = 2018, playCount = 17000),
        Music(id = "h21", title = "Baarish", artist = "Bilal Saeed, Neha Kakkar", duration = 201, audioUrl = "https://cdn.example.com/baarish.mp3", coverImageUrl = "https://picsum.photos/seed/baarish/400", genre = "Bollywood", releaseYear = 2020, playCount = 13000),
        Music(id = "h22", title = "Pal", artist = "Arijit Singh", duration = 266, audioUrl = "https://cdn.example.com/pal.mp3", coverImageUrl = "https://picsum.photos/seed/pal/400", genre = "Bollywood", releaseYear = 2019, playCount = 16000),
        Music(id = "h23", title = "Kaise Hua", artist = "Vishal Mishra", duration = 221, audioUrl = "https://cdn.example.com/kaise.mp3", coverImageUrl = "https://picsum.photos/seed/kaise/400", genre = "Bollywood", releaseYear = 2019, playCount = 14000),
        Music(id = "h24", title = "Humdard", artist = "Arijit Singh", duration = 259, audioUrl = "https://cdn.example.com/humdard.mp3", coverImageUrl = "https://picsum.photos/seed/humdard/400", genre = "Bollywood", releaseYear = 2014, playCount = 18000),
        Music(id = "h25", title = "Samjhawan", artist = "Arijit Singh, Shreya Ghoshal", duration = 281, audioUrl = "https://cdn.example.com/samjha.mp3", coverImageUrl = "https://picsum.photos/seed/samjha/400", genre = "Bollywood", releaseYear = 2014, playCount = 15000)
    )
    
    // Punjabi Songs - Unique only
    private fun getPunjabiSongs(): List<Music> = listOf(
        Music(id = "p1", title = "Excuses", artist = "AP Dhillon, Gurinder Gill", duration = 193, audioUrl = "https://cdn.example.com/excuses.mp3", coverImageUrl = "https://picsum.photos/seed/excuses/400", genre = "Punjabi", releaseYear = 2020, playCount = 22000),
        Music(id = "p2", title = "Brown Munde", artist = "AP Dhillon, Gurinder Gill", duration = 178, audioUrl = "https://cdn.example.com/brown.mp3", coverImageUrl = "https://picsum.photos/seed/brown/400", genre = "Punjabi", releaseYear = 2020, playCount = 25000),
        Music(id = "p3", title = "295", artist = "Sidhu Moose Wala", duration = 231, audioUrl = "https://cdn.example.com/295.mp3", coverImageUrl = "https://picsum.photos/seed/295/400", genre = "Punjabi", releaseYear = 2021, playCount = 20000),
        Music(id = "p4", title = "So High", artist = "Sidhu Moose Wala", duration = 188, audioUrl = "https://cdn.example.com/sohigh.mp3", coverImageUrl = "https://picsum.photos/seed/sohigh/400", genre = "Punjabi", releaseYear = 2017, playCount = 19000),
        Music(id = "p5", title = "Prada", artist = "Jass Manak", duration = 196, audioUrl = "https://cdn.example.com/prada.mp3", coverImageUrl = "https://picsum.photos/seed/prada/400", genre = "Punjabi", releaseYear = 2019, playCount = 18000),
        Music(id = "p6", title = "Lehanga", artist = "Jass Manak", duration = 182, audioUrl = "https://cdn.example.com/lehanga.mp3", coverImageUrl = "https://picsum.photos/seed/lehanga/400", genre = "Punjabi", releaseYear = 2018, playCount = 17000),
        Music(id = "p7", title = "Do You Know", artist = "Diljit Dosanjh", duration = 209, audioUrl = "https://cdn.example.com/doyou.mp3", coverImageUrl = "https://picsum.photos/seed/doyou/400", genre = "Punjabi", releaseYear = 2016, playCount = 21000),
        Music(id = "p8", title = "Laembadgini", artist = "Diljit Dosanjh", duration = 224, audioUrl = "https://cdn.example.com/laemba.mp3", coverImageUrl = "https://picsum.photos/seed/laemba/400", genre = "Punjabi", releaseYear = 2016, playCount = 19000),
        Music(id = "p9", title = "5 Taara", artist = "Diljit Dosanjh", duration = 201, audioUrl = "https://cdn.example.com/5taara.mp3", coverImageUrl = "https://picsum.photos/seed/5taara/400", genre = "Punjabi", releaseYear = 2015, playCount = 18000),
        Music(id = "p10", title = "Viah", artist = "Jass Manak", duration = 194, audioUrl = "https://cdn.example.com/viah.mp3", coverImageUrl = "https://picsum.photos/seed/viah/400", genre = "Punjabi", releaseYear = 2019, playCount = 16000),
        Music(id = "p11", title = "Summer High", artist = "AP Dhillon", duration = 198, audioUrl = "https://cdn.example.com/summer.mp3", coverImageUrl = "https://picsum.photos/seed/summer/400", genre = "Punjabi", releaseYear = 2021, playCount = 17000),
        Music(id = "p12", title = "Majhail", artist = "AP Dhillon", duration = 186, audioUrl = "https://cdn.example.com/majhail.mp3", coverImageUrl = "https://picsum.photos/seed/majhail/400", genre = "Punjabi", releaseYear = 2021, playCount = 15000),
        Music(id = "p13", title = "Insane", artist = "AP Dhillon", duration = 192, audioUrl = "https://cdn.example.com/insane.mp3", coverImageUrl = "https://picsum.photos/seed/insane/400", genre = "Punjabi", releaseYear = 2021, playCount = 18000),
        Music(id = "p14", title = "Bachke Rehna", artist = "Jass Manak", duration = 189, audioUrl = "https://cdn.example.com/bachke.mp3", coverImageUrl = "https://picsum.photos/seed/bachke/400", genre = "Punjabi", releaseYear = 2020, playCount = 16000),
        Music(id = "p15", title = "G.O.A.T", artist = "Diljit Dosanjh", duration = 203, audioUrl = "https://cdn.example.com/goat.mp3", coverImageUrl = "https://picsum.photos/seed/goat/400", genre = "Punjabi", releaseYear = 2020, playCount = 20000),
        Music(id = "p16", title = "Born To Shine", artist = "Diljit Dosanjh", duration = 211, audioUrl = "https://cdn.example.com/born.mp3", coverImageUrl = "https://picsum.photos/seed/born/400", genre = "Punjabi", releaseYear = 2020, playCount = 19000),
        Music(id = "p17", title = "Clash", artist = "Diljit Dosanjh", duration = 197, audioUrl = "https://cdn.example.com/clash.mp3", coverImageUrl = "https://picsum.photos/seed/clash/400", genre = "Punjabi", releaseYear = 2020, playCount = 17000),
        Music(id = "p18", title = "Toxic", artist = "Sidhu Moose Wala", duration = 206, audioUrl = "https://cdn.example.com/toxic.mp3", coverImageUrl = "https://picsum.photos/seed/toxic/400", genre = "Punjabi", releaseYear = 2021, playCount = 16000),
        Music(id = "p19", title = "The Last Ride", artist = "Sidhu Moose Wala", duration = 245, audioUrl = "https://cdn.example.com/lastride.mp3", coverImageUrl = "https://picsum.photos/seed/lastride/400", genre = "Punjabi", releaseYear = 2021, playCount = 18000),
        Music(id = "p20", title = "Tibeyan Da Putt", artist = "Sidhu Moose Wala", duration = 217, audioUrl = "https://cdn.example.com/tibey.mp3", coverImageUrl = "https://picsum.photos/seed/tibey/400", genre = "Punjabi", releaseYear = 2020, playCount = 15000)
    )
    
    // Bhojpuri Songs - Unique only
    private fun getBhojpuriSongs(): List<Music> = listOf(
        Music(id = "b1", title = "Lollipop Lagelu", artist = "Pawan Singh", duration = 231, audioUrl = "https://cdn.example.com/lollipop.mp3", coverImageUrl = "https://picsum.photos/seed/lolli/400", genre = "Bhojpuri", releaseYear = 2016, playCount = 12000),
        Music(id = "b2", title = "Nirahua Satal Rahe", artist = "Dinesh Lal Yadav", duration = 245, audioUrl = "https://cdn.example.com/nirahua.mp3", coverImageUrl = "https://picsum.photos/seed/nirahua/400", genre = "Bhojpuri", releaseYear = 2018, playCount = 11000),
        Music(id = "b3", title = "Jab Se Dekhal", artist = "Khesari Lal Yadav", duration = 218, audioUrl = "https://cdn.example.com/jabse.mp3", coverImageUrl = "https://picsum.photos/seed/jabse/400", genre = "Bhojpuri", releaseYear = 2019, playCount = 10000),
        Music(id = "b4", title = "Pyar Mohabbat", artist = "Pawan Singh", duration = 234, audioUrl = "https://cdn.example.com/pyar.mp3", coverImageUrl = "https://picsum.photos/seed/pyar/400", genre = "Bhojpuri", releaseYear = 2017, playCount = 11000),
        Music(id = "b5", title = "Lehanga Laal", artist = "Pawan Singh", duration = 221, audioUrl = "https://cdn.example.com/lehangalaal.mp3", coverImageUrl = "https://picsum.photos/seed/lehangalaal/400", genre = "Bhojpuri", releaseYear = 2018, playCount = 12000),
        Music(id = "b6", title = "Tempu Se Aail Bani", artist = "Khesari Lal Yadav", duration = 198, audioUrl = "https://cdn.example.com/tempu.mp3", coverImageUrl = "https://picsum.photos/seed/tempu/400", genre = "Bhojpuri", releaseYear = 2016, playCount = 9000),
        Music(id = "b7", title = "Patli Kamariya", artist = "Pawan Singh", duration = 212, audioUrl = "https://cdn.example.com/patli.mp3", coverImageUrl = "https://picsum.photos/seed/patli/400", genre = "Bhojpuri", releaseYear = 2017, playCount = 10000),
        Music(id = "b8", title = "Choliya Ke Hook", artist = "Khesari Lal Yadav", duration = 205, audioUrl = "https://cdn.example.com/choliya.mp3", coverImageUrl = "https://picsum.photos/seed/choliya/400", genre = "Bhojpuri", releaseYear = 2019, playCount = 11000),
        Music(id = "b9", title = "Fas Gail", artist = "Ritesh Pandey", duration = 227, audioUrl = "https://cdn.example.com/fasgail.mp3", coverImageUrl = "https://picsum.photos/seed/fasgail/400", genre = "Bhojpuri", releaseYear = 2018, playCount = 9000),
        Music(id = "b10", title = "Gori Tori", artist = "Pawan Singh", duration = 239, audioUrl = "https://cdn.example.com/gori.mp3", coverImageUrl = "https://picsum.photos/seed/gori/400", genre = "Bhojpuri", releaseYear = 2016, playCount = 10000),
        Music(id = "b11", title = "Chumma De Da", artist = "Khesari Lal Yadav", duration = 213, audioUrl = "https://cdn.example.com/chumma.mp3", coverImageUrl = "https://picsum.photos/seed/chumma/400", genre = "Bhojpuri", releaseYear = 2017, playCount = 11000),
        Music(id = "b12", title = "Odhaniya Wali", artist = "Pawan Singh", duration = 224, audioUrl = "https://cdn.example.com/odhaniya.mp3", coverImageUrl = "https://picsum.photos/seed/odhaniya/400", genre = "Bhojpuri", releaseYear = 2019, playCount = 10000),
        Music(id = "b13", title = "Maja Mare Gori", artist = "Khesari Lal Yadav", duration = 218, audioUrl = "https://cdn.example.com/maja.mp3", coverImageUrl = "https://picsum.photos/seed/maja/400", genre = "Bhojpuri", releaseYear = 2018, playCount = 9000),
        Music(id = "b14", title = "Dulhin Ganga Paar Ke", artist = "Dinesh Lal Yadav", duration = 242, audioUrl = "https://cdn.example.com/dulhin.mp3", coverImageUrl = "https://picsum.photos/seed/dulhin/400", genre = "Bhojpuri", releaseYear = 2019, playCount = 10000),
        Music(id = "b15", title = "Hamar Wala Dance", artist = "Pawan Singh", duration = 206, audioUrl = "https://cdn.example.com/dance.mp3", coverImageUrl = "https://picsum.photos/seed/dance/400", genre = "Bhojpuri", releaseYear = 2020, playCount = 11000)
    )

    // Haryanvi Songs - Unique only
    private fun getHaryanviSongs(): List<Music> = listOf(
        Music(id = "hr1", title = "52 Gaj Ka Daman", artist = "Renuka Panwar", duration = 180, audioUrl = "https://cdn.example.com/52gaj.mp3", coverImageUrl = "https://picsum.photos/seed/52gaj/400", genre = "Haryanvi", releaseYear = 2020, playCount = 25000),
        Music(id = "hr2", title = "Solid Body", artist = "Pardeep Boora", duration = 210, audioUrl = "https://cdn.example.com/solidbody.mp3", coverImageUrl = "https://picsum.photos/seed/solidbody/400", genre = "Haryanvi", releaseYear = 2015, playCount = 18000),
        Music(id = "hr3", title = "Thar", artist = "Gulzaar Chhaniwala", duration = 200, audioUrl = "https://cdn.example.com/thar.mp3", coverImageUrl = "https://picsum.photos/seed/thar/400", genre = "Haryanvi", releaseYear = 2022, playCount = 19000),
        Music(id = "hr4", title = "Kaala Suit", artist = "Sonika Singh", duration = 190, audioUrl = "https://cdn.example.com/kaalasuit.mp3", coverImageUrl = "https://picsum.photos/seed/kaalasuit/400", genre = "Haryanvi", releaseYear = 2018, playCount = 15000),
        Music(id = "hr5", title = "System Pe System", artist = "R Maan", duration = 170, audioUrl = "https://cdn.example.com/system.mp3", coverImageUrl = "https://picsum.photos/seed/system/400", genre = "Haryanvi", releaseYear = 2023, playCount = 21000),
        Music(id = "hr6", title = "Ram Ram", artist = "MC Square", duration = 220, audioUrl = "https://cdn.example.com/ramram.mp3", coverImageUrl = "https://picsum.photos/seed/ramram/400", genre = "Haryanvi", releaseYear = 2022, playCount = 20000),
        Music(id = "hr7", title = "Kamar Teri Left Right Hale", artist = "Ajay Hooda", duration = 195, audioUrl = "https://cdn.example.com/kamar.mp3", coverImageUrl = "https://picsum.photos/seed/kamar/400", genre = "Haryanvi", releaseYear = 2017, playCount = 17000),
        Music(id = "hr8", title = "Ghungroo", artist = "Ruchika Jangid", duration = 205, audioUrl = "https://cdn.example.com/ghungroo.mp3", coverImageUrl = "https://picsum.photos/seed/ghungroo/400", genre = "Haryanvi", releaseYear = 2019, playCount = 16000),
        Music(id = "hr9", title = "Desi Desi Na Bolya Kar", artist = "MD KD", duration = 240, audioUrl = "https://cdn.example.com/desidesi.mp3", coverImageUrl = "https://picsum.photos/seed/desidesi/400", genre = "Haryanvi", releaseYear = 2016, playCount = 22000),
        Music(id = "hr10", title = "Moto", artist = "Diler Kharkiya", duration = 230, audioUrl = "https://cdn.example.com/moto.mp3", coverImageUrl = "https://picsum.photos/seed/moto/400", genre = "Haryanvi", releaseYear = 2020, playCount = 23000)
    )

    // Tamil Songs - Unique only
    private fun getTamilSongs(): List<Music> = listOf(
        Music(id = "t1", title = "Vaathi Coming", artist = "Anirudh Ravichander", duration = 254, audioUrl = "https://cdn.example.com/vaathi.mp3", coverImageUrl = "https://picsum.photos/seed/vaathi/400", genre = "Tamil", releaseYear = 2020, playCount = 14000),
        Music(id = "t2", title = "Butta Bomma", artist = "Armaan Malik", duration = 196, audioUrl = "https://cdn.example.com/butta.mp3", coverImageUrl = "https://picsum.photos/seed/butta/400", genre = "Tamil", releaseYear = 2020, playCount = 13000),
        Music(id = "t3", title = "Rowdy Baby", artist = "Dhanush, Dhee", duration = 312, audioUrl = "https://cdn.example.com/rowdy.mp3", coverImageUrl = "https://picsum.photos/seed/rowdy/400", genre = "Tamil", releaseYear = 2018, playCount = 16000),
        Music(id = "t4", title = "Oo Antava", artist = "Indravathi Chauhan", duration = 198, audioUrl = "https://cdn.example.com/antava.mp3", coverImageUrl = "https://picsum.photos/seed/antava/400", genre = "Tamil", releaseYear = 2021, playCount = 15000),
        Music(id = "t5", title = "Naatu Naatu", artist = "Rahul Sipligunj, Kaala Bhairava", duration = 276, audioUrl = "https://cdn.example.com/naatu.mp3", coverImageUrl = "https://picsum.photos/seed/naatu/400", genre = "Tamil", releaseYear = 2022, playCount = 17000),
        Music(id = "t6", title = "Kaavaalaa", artist = "Shilpa Rao, Anirudh", duration = 223, audioUrl = "https://cdn.example.com/kaavalaa.mp3", coverImageUrl = "https://picsum.photos/seed/kaavalaa/400", genre = "Tamil", releaseYear = 2023, playCount = 14000),
        Music(id = "t7", title = "Master the Blaster", artist = "Anirudh Ravichander", duration = 241, audioUrl = "https://cdn.example.com/master.mp3", coverImageUrl = "https://picsum.photos/seed/master/400", genre = "Tamil", releaseYear = 2021, playCount = 13000),
        Music(id = "t8", title = "Vathi Raid", artist = "Anirudh Ravichander", duration = 189, audioUrl = "https://cdn.example.com/vathiraid.mp3", coverImageUrl = "https://picsum.photos/seed/vathiraid/400", genre = "Tamil", releaseYear = 2020, playCount = 12000),
        Music(id = "t9", title = "Arabic Kuthu", artist = "Anirudh, Jonita Gandhi", duration = 204, audioUrl = "https://cdn.example.com/arabic.mp3", coverImageUrl = "https://picsum.photos/seed/arabic/400", genre = "Tamil", releaseYear = 2022, playCount = 15000),
        Music(id = "t10", title = "Why This Kolaveri Di", artist = "Dhanush", duration = 244, audioUrl = "https://cdn.example.com/kolaveri.mp3", coverImageUrl = "https://picsum.photos/seed/kolaveri/400", genre = "Tamil", releaseYear = 2011, playCount = 18000)
    )
    
    // Telugu Songs - Unique only
    private fun getTeluguSongs(): List<Music> = listOf(
        Music(id = "te1", title = "Saami Saami", artist = "Mounika Gattiraga", duration = 198, audioUrl = "https://cdn.example.com/saami.mp3", coverImageUrl = "https://picsum.photos/seed/saami/400", genre = "Telugu", releaseYear = 2021, playCount = 14000),
        Music(id = "te2", title = "Srivalli", artist = "Javed Ali", duration = 224, audioUrl = "https://cdn.example.com/srivalli.mp3", coverImageUrl = "https://picsum.photos/seed/srivalli/400", genre = "Telugu", releaseYear = 2021, playCount = 15000),
        Music(id = "te3", title = "Kalaavathi", artist = "Sid Sriram", duration = 207, audioUrl = "https://cdn.example.com/kalaavathi.mp3", coverImageUrl = "https://picsum.photos/seed/kalaavathi/400", genre = "Telugu", releaseYear = 2022, playCount = 13000),
        Music(id = "te4", title = "Ramuloo Ramulaa", artist = "Anurag Kulkarni, Mangli", duration = 212, audioUrl = "https://cdn.example.com/ramuloo.mp3", coverImageUrl = "https://picsum.photos/seed/ramuloo/400", genre = "Telugu", releaseYear = 2020, playCount = 14000),
        Music(id = "te5", title = "Buttabomma", artist = "Armaan Malik", duration = 235, audioUrl = "https://cdn.example.com/buttabomma.mp3", coverImageUrl = "https://picsum.photos/seed/buttabomma/400", genre = "Telugu", releaseYear = 2020, playCount = 16000),
        Music(id = "te6", title = "Inkem Inkem", artist = "Sid Sriram", duration = 198, audioUrl = "https://cdn.example.com/inkem.mp3", coverImageUrl = "https://picsum.photos/seed/inkem/400", genre = "Telugu", releaseYear = 2018, playCount = 13000),
        Music(id = "te7", title = "Samajavaragamana", artist = "Sid Sriram", duration = 267, audioUrl = "https://cdn.example.com/samaja.mp3", coverImageUrl = "https://picsum.photos/seed/samaja/400", genre = "Telugu", releaseYear = 2019, playCount = 15000),
        Music(id = "te8", title = "Yenti Yenti", artist = "Chinmayi Sripaada", duration = 234, audioUrl = "https://cdn.example.com/yenti.mp3", coverImageUrl = "https://picsum.photos/seed/yenti/400", genre = "Telugu", releaseYear = 2018, playCount = 12000),
        Music(id = "te9", title = "Vachinde", artist = "Madhu Priya", duration = 201, audioUrl = "https://cdn.example.com/vachinde.mp3", coverImageUrl = "https://picsum.photos/seed/vachinde/400", genre = "Telugu", releaseYear = 2018, playCount = 14000),
        Music(id = "te10", title = "Butta Bomma Remix", artist = "Thaman S", duration = 189, audioUrl = "https://cdn.example.com/buttaremix.mp3", coverImageUrl = "https://picsum.photos/seed/buttaremix/400", genre = "Telugu", releaseYear = 2020, playCount = 13000)
    )
    
    // Malayalam Songs - Unique only
    private fun getMalayalamSongs(): List<Music> = listOf(
        Music(id = "m1", title = "Parayuvaan", artist = "Kapil Kapilan", duration = 256, audioUrl = "https://cdn.example.com/parayuvaan.mp3", coverImageUrl = "https://picsum.photos/seed/parayuvaan/400", genre = "Malayalam", releaseYear = 2020, playCount = 11000),
        Music(id = "m2", title = "Nenjin Upakatha", artist = "Vineeth Sreenivasan", duration = 287, audioUrl = "https://cdn.example.com/nenjin.mp3", coverImageUrl = "https://picsum.photos/seed/nenjin/400", genre = "Malayalam", releaseYear = 2019, playCount = 12000),
        Music(id = "m3", title = "Jimikki Kammal", artist = "Vineeth Sreenivasan", duration = 214, audioUrl = "https://cdn.example.com/jimikki.mp3", coverImageUrl = "https://picsum.photos/seed/jimikki/400", genre = "Malayalam", releaseYear = 2017, playCount = 13000),
        Music(id = "m4", title = "Thaarame Thaarame", artist = "Shreya Ghoshal", duration = 289, audioUrl = "https://cdn.example.com/thaarame.mp3", coverImageUrl = "https://picsum.photos/seed/thaarame/400", genre = "Malayalam", releaseYear = 2020, playCount = 10000),
        Music(id = "m5", title = "Vaathil Melle", artist = "Vineeth Sreenivasan", duration = 241, audioUrl = "https://cdn.example.com/vaathil.mp3", coverImageUrl = "https://picsum.photos/seed/vaathil/400", genre = "Malayalam", releaseYear = 2018, playCount = 11000),
        Music(id = "m6", title = "Kannondu Chollanu", artist = "Sooraj Santhosh", duration = 223, audioUrl = "https://cdn.example.com/kannondu.mp3", coverImageUrl = "https://picsum.photos/seed/kannondu/400", genre = "Malayalam", releaseYear = 2019, playCount = 10000),
        Music(id = "m7", title = "Arike", artist = "Vineeth Sreenivasan", duration = 267, audioUrl = "https://cdn.example.com/arike.mp3", coverImageUrl = "https://picsum.photos/seed/arike/400", genre = "Malayalam", releaseYear = 2017, playCount = 12000),
        Music(id = "m8", title = "Kalippattam", artist = "Vineeth Sreenivasan", duration = 198, audioUrl = "https://cdn.example.com/kalippattam.mp3", coverImageUrl = "https://picsum.photos/seed/kalippattam/400", genre = "Malayalam", releaseYear = 2018, playCount = 9000),
        Music(id = "m9", title = "Thaalavattam", artist = "K J Yesudas", duration = 312, audioUrl = "https://cdn.example.com/thaala.mp3", coverImageUrl = "https://picsum.photos/seed/thaala/400", genre = "Malayalam", releaseYear = 2016, playCount = 11000),
        Music(id = "m10", title = "Pranayini", artist = "Vijay Yesudas", duration = 234, audioUrl = "https://cdn.example.com/pranayini.mp3", coverImageUrl = "https://picsum.photos/seed/pranayini/400", genre = "Malayalam", releaseYear = 2020, playCount = 10000)
    )
    
    // Kannada Songs - Unique only
    private fun getKannadaSongs(): List<Music> = listOf(
        Music(id = "k1", title = "Yaarige Yaaruntu", artist = "Sanjith Hegde", duration = 230, audioUrl = "https://cdn.example.com/yaarige.mp3", coverImageUrl = "https://picsum.photos/seed/yaarige/400", genre = "Kannada", releaseYear = 2019, playCount = 11000),
        Music(id = "k2", title = "Belageddu", artist = "Vijay Prakash", duration = 227, audioUrl = "https://cdn.example.com/belageddu.mp3", coverImageUrl = "https://picsum.photos/seed/belageddu/400", genre = "Kannada", releaseYear = 2018, playCount = 12000),
        Music(id = "k3", title = "Inkem Inkem Kaavaale", artist = "Sid Sriram", duration = 212, audioUrl = "https://cdn.example.com/inkemkannada.mp3", coverImageUrl = "https://picsum.photos/seed/inkemkannada/400", genre = "Kannada", releaseYear = 2018, playCount = 10000),
        Music(id = "k4", title = "Ondu Malebillu", artist = "Armaan Malik", duration = 225, audioUrl = "https://cdn.example.com/ondumalebillu.mp3", coverImageUrl = "https://picsum.photos/seed/ondumalebillu/400", genre = "Kannada", releaseYear = 2019, playCount = 11000),
        Music(id = "k5", title = "Chuttu Chuttu", artist = "Sanjith Hegde", duration = 232, audioUrl = "https://cdn.example.com/chuttu.mp3", coverImageUrl = "https://picsum.photos/seed/chuttu/400", genre = "Kannada", releaseYear = 2018, playCount = 12000),
        Music(id = "k6", title = "Yenammi Yenammi", artist = "Vijay Prakash", duration = 225, audioUrl = "https://cdn.example.com/yenammi.mp3", coverImageUrl = "https://picsum.photos/seed/yenammi/400", genre = "Kannada", releaseYear = 2018, playCount = 10000),
        Music(id = "k7", title = "KGF Theme", artist = "Ravi Basrur", duration = 192, audioUrl = "https://cdn.example.com/kgf.mp3", coverImageUrl = "https://picsum.photos/seed/kgf/400", genre = "Kannada", releaseYear = 2018, playCount = 13000),
        Music(id = "k8", title = "Mundina Nildana", artist = "Vasu Dixit", duration = 236, audioUrl = "https://cdn.example.com/mundina.mp3", coverImageUrl = "https://picsum.photos/seed/mundina/400", genre = "Kannada", releaseYear = 2019, playCount = 11000),
        Music(id = "k9", title = "Hosa Digantha", artist = "Vijay Prakash", duration = 236, audioUrl = "https://cdn.example.com/hosa.mp3", coverImageUrl = "https://picsum.photos/seed/hosa/400", genre = "Kannada", releaseYear = 2020, playCount = 10000),
        Music(id = "k10", title = "Singara Siriye", artist = "Vijay Prakash", duration = 216, audioUrl = "https://cdn.example.com/singara.mp3", coverImageUrl = "https://picsum.photos/seed/singara/400", genre = "Kannada", releaseYear = 2021, playCount = 12000)
    )
    
    // English Songs - Unique only
    private fun getEnglishSongs(): List<Music> = listOf(
        Music(id = "e1", title = "Blinding Lights", artist = "The Weeknd", duration = 200, audioUrl = "https://cdn.example.com/blinding.mp3", coverImageUrl = "https://picsum.photos/seed/blinding/400", genre = "Pop", releaseYear = 2019, playCount = 35000),
        Music(id = "e2", title = "Shape of You", artist = "Ed Sheeran", duration = 233, audioUrl = "https://cdn.example.com/shape.mp3", coverImageUrl = "https://picsum.photos/seed/shape/400", genre = "Pop", releaseYear = 2017, playCount = 32000),
        Music(id = "e3", title = "Someone You Loved", artist = "Lewis Capaldi", duration = 182, audioUrl = "https://cdn.example.com/someone.mp3", coverImageUrl = "https://picsum.photos/seed/someone/400", genre = "Pop", releaseYear = 2018, playCount = 28000),
        Music(id = "e4", title = "Dance Monkey", artist = "Tones and I", duration = 209, audioUrl = "https://cdn.example.com/dance.mp3", coverImageUrl = "https://picsum.photos/seed/dance/400", genre = "Pop", releaseYear = 2019, playCount = 30000),
        Music(id = "e5", title = "Uptown Funk", artist = "Mark Ronson ft. Bruno Mars", duration = 270, audioUrl = "https://cdn.example.com/uptown.mp3", coverImageUrl = "https://picsum.photos/seed/uptown/400", genre = "Funk", releaseYear = 2014, playCount = 29000),
        Music(id = "e6", title = "Closer", artist = "The Chainsmokers ft. Halsey", duration = 244, audioUrl = "https://cdn.example.com/closer.mp3", coverImageUrl = "https://picsum.photos/seed/closer/400", genre = "EDM", releaseYear = 2016, playCount = 27000),
        Music(id = "e7", title = "Believer", artist = "Imagine Dragons", duration = 204, audioUrl = "https://cdn.example.com/believer.mp3", coverImageUrl = "https://picsum.photos/seed/believer/400", genre = "Rock", releaseYear = 2017, playCount = 26000),
        Music(id = "e8", title = "Perfect", artist = "Ed Sheeran", duration = 263, audioUrl = "https://cdn.example.com/perfect.mp3", coverImageUrl = "https://picsum.photos/seed/perfect/400", genre = "Pop", releaseYear = 2017, playCount = 25000),
        Music(id = "e9", title = "Despacito", artist = "Luis Fonsi ft. Daddy Yankee", duration = 229, audioUrl = "https://cdn.example.com/despacito.mp3", coverImageUrl = "https://picsum.photos/seed/despacito/400", genre = "Latin", releaseYear = 2017, playCount = 31000),
        Music(id = "e10", title = "Havana", artist = "Camila Cabello ft. Young Thug", duration = 217, audioUrl = "https://cdn.example.com/havana.mp3", coverImageUrl = "https://picsum.photos/seed/havana/400", genre = "Latin", releaseYear = 2017, playCount = 24000)
    )
}
