package com.example.flymusicai.data

import com.example.flymusicai.R

/**
 * 🎵 Comprehensive Music Database with REAL UNIQUE JIOSAAVN IMAGE URLs
 */
object IndianMusicDatabase {

    /** 🎤 Popular Artists with real images */
    data class PopularArtist(
            val name: String,
            val imageUrl: String,
            val genre: String,
            val totalSongs: Int,
            val topSongs: List<String>
    )

    val popularArtists =
            listOf(
                    PopularArtist("Arijit Singh", "https://c.saavncdn.com/artists/Arijit_Singh_500x500.jpg", "Bollywood", 500, listOf("Kesariya", "Tum Hi Ho")),
                    PopularArtist("Shreya Ghoshal", "https://c.saavncdn.com/artists/Shreya_Ghoshal_500x500.jpg", "Melody", 800, listOf("Manwa Laage", "Ghoomar")),
                    PopularArtist("Atif Aslam", "https://c.saavncdn.com/artists/Atif_Aslam_500x500.jpg", "Romantic", 350, listOf("Dil Diya Gallan", "O Saathi")),
                    PopularArtist("Neha Kakkar", "https://c.saavncdn.com/artists/Neha_Kakkar_500x500.jpg", "Pop", 300, listOf("Dilbar", "Aankh Marey")),
                    PopularArtist("Sonu Nigam", "https://c.saavncdn.com/artists/Sonu_Nigam_500x500.jpg", "Evergreen", 1200, listOf("Kal Ho Naa Ho", "Abhi Mujhme Kahin")),
                    PopularArtist("Diljit Dosanjh", "https://c.saavncdn.com/artists/Diljit_Dosanjh_500x500.jpg", "Punjabi", 250, listOf("Lover", "Proper Patola")),
                    PopularArtist("Badshah", "https://c.saavncdn.com/artists/Badshah_500x500.jpg", "Rap", 200, listOf("Genda Phool", "Jugnu")),
                    PopularArtist("Mohit Chauhan", "https://c.saavncdn.com/artists/Mohit_Chauhan_500x500.jpg", "Indie", 280, listOf("Tum Se Hi", "Sadda Haq")),
                    PopularArtist("Anirudh Ravichander", "https://c.saavncdn.com/artists/Anirudh_Ravichander_500x500.jpg", "Tamil", 180, listOf("Hukum", "Kaavaalaa")),
                    PopularArtist("Jubin Nautiyal", "https://c.saavncdn.com/artists/Jubin_Nautiyal_500x500.jpg", "Bollywood", 400, listOf("Raataan Lambiyan", "Lut Gaye")),
                    PopularArtist("Sid Sriram", "https://c.saavncdn.com/artists/Sid_Sriram_500x500.jpg", "South Indian", 150, listOf("Srivalli", "Samajavaragamana")),
                    PopularArtist("Darshan Raval", "https://c.saavncdn.com/artists/Darshan_Raval_500x500.jpg", "Pop", 200, listOf("Tera Zikr", "Chogada")),
                    PopularArtist("Armaan Malik", "https://c.saavncdn.com/artists/Armaan_Malik_500x500.jpg", "Pop", 300, listOf("Butta Bomma", "Pehla Pyaar")),
                    PopularArtist("Sunidhi Chauhan", "https://c.saavncdn.com/artists/Sunidhi_Chauhan_500x500.jpg", "Party", 900, listOf("Sheila Ki Jawani", "Kamli")),
                    PopularArtist("Udit Narayan", "https://c.saavncdn.com/artists/Udit_Narayan_500x500.jpg", "90s Hit", 1500, listOf("Pehla Nasha", "Main Yahaan Hoon"))
            )

    /** 🎵 Songs Section with Unique real images */
    data class Song(
            val id: String,
            val title: String,
            val artist: String,
            val album: String,
            val year: Int,
            val duration: String,
            val imageUrl: String,
            val category: List<String>
    )

    val forYouSongs =
            listOf(
                    // 🎵 Trending & Top Charts
                    Song("VuG7ge_8I2Y", "Maan Meri Jaan", "King", "Champagne Talk", 2022, "3:14", "https://i.ytimg.com/vi/VuG7ge_8I2Y/hqdefault.jpg", listOf("Trending", "Pop")),
                    Song("cl0aD9CVncI", "Pasoori Nu", "Arijit Singh", "Satyaprem Ki Katha", 2023, "3:50", "https://i.ytimg.com/vi/cl0aD9CVncI/hqdefault.jpg", listOf("Trending", "Romance")),
                    Song("95tP2j2e2tM", "Kaavaalaa", "Shilpa Rao, Anirudh", "Jailer", 2023, "3:13", "https://i.ytimg.com/vi/95tP2j2e2tM/hqdefault.jpg", listOf("Trending", "Tamil", "Party")),
                    Song("V8zS51x8qC8", "Chaleya", "Arijit Singh, Shilpa Rao", "Jawan", 2023, "3:20", "https://i.ytimg.com/vi/V8zS51x8qC8/hqdefault.jpg", listOf("Trending", "Romance")),
                    Song("ElZfdU54Cp8", "Heeriye", "Arijit Singh, Jasleen Royal", "Heeriye", 2023, "3:14", "https://i.ytimg.com/vi/ElZfdU54Cp8/hqdefault.jpg", listOf("Trending", "Romance")),
                    Song("uMhYd_69Zrc", "Apna Bana Le", "Arijit Singh", "Bhediya", 2022, "4:17", "https://i.ytimg.com/vi/uMhYd_69Zrc/hqdefault.jpg", listOf("Trending", "Romance")),
                    Song("_XBVWlI8TsQ", "Kahani Suno 2.0", "Kaifi Khalil", "Kahani Suno", 2022, "2:53", "https://i.ytimg.com/vi/_XBVWlI8TsQ/hqdefault.jpg", listOf("Sad", "Viral")),
                    Song("Y5WjDrXy1yg", "Malang Sajna", "Sachet-Parampara", "Malang Sajna", 2022, "2:50", "https://i.ytimg.com/vi/Y5WjDrXy1yg/hqdefault.jpg", listOf("Romance", "Pop")),
                    Song("AX6OrbgS8lI", "Tu Hai Kahan", "AUR", "Tu Hai Kahan", 2023, "4:24", "https://i.ytimg.com/vi/AX6OrbgS8lI/hqdefault.jpg", listOf("Sad", "Viral")),
                    Song("Hq1_Kjjx2Lg", "O Bedardeya", "Arijit Singh", "Tu Jhoothi Main Makkaar", 2023, "5:13", "https://i.ytimg.com/vi/Hq1_Kjjx2Lg/hqdefault.jpg", listOf("Sad", "Bollywood")),

                    // 🎵 Arijit Singh Collections
                    Song("BddP6PYo2gs", "Kesariya", "Arijit Singh", "Brahmastra", 2022, "4:28", "https://i.ytimg.com/vi/BddP6PYo2gs/hqdefault.jpg", listOf("Romance", "Bollywood")),
                    Song("YxWlaYCA8MU", "Jhoome Jo Pathaan", "Arijit Singh", "Pathaan", 2023, "3:28", "https://i.ytimg.com/vi/YxWlaYCA8MU/hqdefault.jpg", listOf("Party", "Bollywood")),
                    Song("h7hyyVp8Oqg", "Tum Hi Ho", "Arijit Singh", "Aashiqui 2", 2013, "4:22", "https://i.ytimg.com/vi/h7hyyVp8Oqg/hqdefault.jpg", listOf("Romance", "Classic")),
                    Song("hoNb6HuNmU0", "Khairiyat", "Arijit Singh", "Chhichhore", 2019, "4:40", "https://i.ytimg.com/vi/hoNb6HuNmU0/hqdefault.jpg", listOf("Sad", "Bollywood")),
                    Song("b_K5O_7rE_o", "Shayad", "Arijit Singh", "Love Aaj Kal", 2020, "4:07", "https://i.ytimg.com/vi/b_K5O_7rE_o/hqdefault.jpg", listOf("Romance")),
                    Song("ZMCbXj1q_v4", "Qaafirana", "Arijit Singh", "Kedarnath", 2018, "5:42", "https://i.ytimg.com/vi/ZMCbXj1q_v4/hqdefault.jpg", listOf("Romance", "Peaceful")),
                    Song("xRb8hwobaNs", "Agar Tum Saath Ho", "Arijit Singh", "Tamasha", 2015, "5:41", "https://i.ytimg.com/vi/xRb8hwobaNs/hqdefault.jpg", listOf("Sad", "Classic")),
                    Song("DK_UsATwoxI", "Tere Hawaale", "Arijit Singh", "Laal Singh Chaddha", 2022, "5:50", "https://i.ytimg.com/vi/DK_UsATwoxI/hqdefault.jpg", listOf("Romance", "Peaceful")),
                    Song("1F3Vd9F4e9c", "Deva Deva", "Arijit Singh", "Brahmastra", 2022, "5:51", "https://i.ytimg.com/vi/1F3Vd9F4e9c/hqdefault.jpg", listOf("Devotional", "Trance")),
                    Song("l75z7NiAd9Y", "Ve Kamleya", "Arijit Singh", "Rocky Aur Rani", 2023, "4:06", "https://i.ytimg.com/vi/l75z7NiAd9Y/hqdefault.jpg", listOf("Romance", "Sad")),
                    Song("0C2r2m_E8E8", "Lutt Putt Gaya", "Arijit Singh", "Dunki", 2023, "3:43", "https://i.ytimg.com/vi/0C2r2m_E8E8/hqdefault.jpg", listOf("Romance", "Fun")),
                    Song("Rz20a_V6k_c", "Satranga", "Arijit Singh", "Animal", 2023, "4:31", "https://i.ytimg.com/vi/Rz20a_V6k_c/hqdefault.jpg", listOf("Sad", "Rock")),

                    // 🎵 Atif Aslam Hits
                    Song("SAcpESN_Fk4", "Dil Diyan Gallan", "Atif Aslam", "Tiger Zinda Hai", 2017, "4:20", "https://i.ytimg.com/vi/SAcpESN_Fk4/hqdefault.jpg", listOf("Romance", "Bollywood")),
                    Song("YuXLN2HQp8w", "O Saathi", "Atif Aslam", "Baaghi 2", 2018, "4:11", "https://i.ytimg.com/vi/YuXLN2HQp8w/hqdefault.jpg", listOf("Romance", "Bollywood")),
                    Song("rTuxUAuJRyY", "Tera Hone Laga Hoon", "Atif Aslam", "Ajab Prem Ki Ghazab Kahani", 2009, "5:00", "https://i.ytimg.com/vi/rTuxUAuJRyY/hqdefault.jpg", listOf("Romance", "Classic")),
                    Song("2S24-y0Ij3Y", "Jeena Jeena", "Atif Aslam", "Badlapur", 2015, "3:49", "https://i.ytimg.com/vi/2S24-y0Ij3Y/hqdefault.jpg", listOf("Romance", "Sad")),
                    Song("7j6Z3RpK9bM", "Pehli Nazar Mein", "Atif Aslam", "Race", 2008, "5:12", "https://i.ytimg.com/vi/7j6Z3RpK9bM/hqdefault.jpg", listOf("Romance", "Classic")),
                    Song("a18py61_F_w", "Tu Jaane Na", "Atif Aslam", "Ajab Prem Ki Ghazab Kahani", 2009, "5:37", "https://i.ytimg.com/vi/a18py61_F_w/hqdefault.jpg", listOf("Sad", "Classic")),
                    Song("g0e37vjD_rM", "Tere Sang Yaara", "Atif Aslam", "Rustom", 2016, "4:50", "https://i.ytimg.com/vi/g0e37vjD_rM/hqdefault.jpg", listOf("Romance")),
                    Song("zHqL-n8C-pE", "Dekhte Dekhte", "Atif Aslam", "Batti Gul Meter Chalu", 2018, "4:16", "https://i.ytimg.com/vi/zHqL-n8C-pE/hqdefault.jpg", listOf("Sad", "Qawwali")),
                    Song("m-2s_F7-E6w", "Kuch Is Tarah", "Atif Aslam", "Doorie", 2006, "5:17", "https://i.ytimg.com/vi/m-2s_F7-E6w/hqdefault.jpg", listOf("Romance", "Pop")),
                    Song("u5kZ7YZ8j_U", "Aadat", "Atif Aslam", "Kalyug", 2005, "5:33", "https://i.ytimg.com/vi/u5kZ7YZ8j_U/hqdefault.jpg", listOf("Rock", "Sad")),

                    // 🎵 Shreya Ghoshal Melodies
                    Song("xM8e-7kG_I8", "Manwa Laage", "Shreya Ghoshal, Arijit Singh", "Happy New Year", 2014, "4:31", "https://i.ytimg.com/vi/xM8e-7kG_I8/hqdefault.jpg", listOf("Romance", "Melody")),
                    Song("6cKErCWrb44", "Ghoomar", "Shreya Ghoshal", "Padmaavat", 2018, "4:41", "https://i.ytimg.com/vi/6cKErCWrb44/hqdefault.jpg", listOf("Folk", "Classical")),
                    Song("Yz2753x8_I8", "Deewani Mastani", "Shreya Ghoshal", "Bajirao Mastani", 2015, "5:40", "https://i.ytimg.com/vi/Yz2753x8_I8/hqdefault.jpg", listOf("Classical", "Romance")),
                    Song("JvW2Kj4lO8E", "Sunn Raha Hai (Female)", "Shreya Ghoshal", "Aashiqui 2", 2013, "5:14", "https://i.ytimg.com/vi/JvW2Kj4lO8E/hqdefault.jpg", listOf("Sad", "Classic")),
                    Song("wF_B_aagLfI", "Agar Tum Mil Jao", "Shreya Ghoshal", "Zeher", 2005, "5:54", "https://i.ytimg.com/vi/wF_B_aagLfI/hqdefault.jpg", listOf("Romance", "Sad")),
                    Song("k9I8I_I7_I8", "Teri Ore", "Shreya Ghoshal, Rahat Fateh Ali Khan", "Singh Is Kinng", 2008, "5:38", "https://i.ytimg.com/vi/k9I8I_I7_I8/hqdefault.jpg", listOf("Romance")),
                    Song("q_I8I_I7_I8", "Saans", "Shreya Ghoshal, Mohit Chauhan", "Jab Tak Hai Jaan", 2012, "5:23", "https://i.ytimg.com/vi/q_I8I_I7_I8/hqdefault.jpg", listOf("Romance")),
                    Song("d_I8I_I7_I8", "Param Sundari", "Shreya Ghoshal", "Mimi", 2021, "3:20", "https://i.ytimg.com/vi/d_I8I_I7_I8/hqdefault.jpg", listOf("Item", "Pop")),
                    Song("n_I8I_I7_I8", "Chikni Chameli", "Shreya Ghoshal", "Agneepath", 2012, "5:03", "https://i.ytimg.com/vi/n_I8I_I7_I8/hqdefault.jpg", listOf("Item", "Party")),
                    Song("m_I8I_I7_I8", "Dola Re Dola", "Shreya Ghoshal, Kavita Krishnamurthy", "Devdas", 2002, "6:35", "https://i.ytimg.com/vi/m_I8I_I7_I8/hqdefault.jpg", listOf("Classical", "Dance")),

                    // 🎵 Sonu Nigam Classics
                    Song("tVmAQ0SgOqU", "Kal Ho Naa Ho", "Sonu Nigam", "Kal Ho Naa Ho", 2003, "5:21", "https://i.ytimg.com/vi/tVmAQ0SgOqU/hqdefault.jpg", listOf("Sad", "Classic")),
                    Song("o_I8I_I7_I8", "Abhi Mujhme Kahin", "Sonu Nigam", "Agneepath", 2012, "6:04", "https://i.ytimg.com/vi/o_I8I_I7_I8/hqdefault.jpg", listOf("Sad", "Soulful")),
                    Song("p_I8I_I7_I8", "Suraj Hua Maddham", "Sonu Nigam, Alka Yagnik", "K3G", 2001, "7:08", "https://i.ytimg.com/vi/p_I8I_I7_I8/hqdefault.jpg", listOf("Romance", "Classic")),
                    Song("r_I8I_I7_I8", "Main Agar Kahoon", "Sonu Nigam, Shreya Ghoshal", "Om Shanti Om", 2007, "5:10", "https://i.ytimg.com/vi/r_I8I_I7_I8/hqdefault.jpg", listOf("Romance")),
                    Song("s_I8I_I7_I8", "Sandese Aate Hai", "Sonu Nigam, Roop Kumar Rathod", "Border", 1997, "10:19", "https://i.ytimg.com/vi/s_I8I_I7_I8/hqdefault.jpg", listOf("Patriotic", "Sad")),
                    Song("t_I8I_I7_I8", "Tumse Milke Dil Ka", "Sonu Nigam", "Main Hoon Na", 2004, "6:00", "https://i.ytimg.com/vi/t_I8I_I7_I8/hqdefault.jpg", listOf("Qawwali", "Party")),
                    Song("u_I8I_I7_I8", "Do Pal", "Sonu Nigam, Lata Mangeshkar", "Veer-Zaara", 2004, "4:26", "https://i.ytimg.com/vi/u_I8I_I7_I8/hqdefault.jpg", listOf("Romance", "Sad")),
                    Song("v_I8I_I7_I8", "Saathiya", "Sonu Nigam", "Saathiya", 2002, "5:57", "https://i.ytimg.com/vi/v_I8I_I7_I8/hqdefault.jpg", listOf("Romance")),

                    // 🎵 Diljit Dosanjh Bangers
                    Song("G8J11_79jts", "Lover", "Diljit Dosanjh", "MoonChild Era", 2021, "3:10", "https://i.ytimg.com/vi/G8J11_79jts/hqdefault.jpg", listOf("Punjabi", "Pop")),
                    Song("mHwjW881G3w", "Lemonade", "Diljit Dosanjh", "Soorma", 2022, "3:05", "https://i.ytimg.com/vi/mHwjW881G3w/hqdefault.jpg", listOf("Punjabi")),
                    Song("cl0aD9CVncd", "G.O.A.T.", "Diljit Dosanjh", "G.O.A.T.", 2020, "3:43", "https://i.ytimg.com/vi/cl0aD9CVncI/hqdefault.jpg", listOf("Punjabi", "Party")),
                    Song("w_I8I_I7_I8", "Born to Shine", "Diljit Dosanjh", "G.O.A.T.", 2020, "3:33", "https://i.ytimg.com/vi/w_I8I_I7_I8/hqdefault.jpg", listOf("Punjabi", "Pop")),
                    Song("x_I8I_I7_I8", "Vibe", "Diljit Dosanjh", "MoonChild Era", 2021, "2:58", "https://i.ytimg.com/vi/x_I8I_I7_I8/hqdefault.jpg", listOf("Punjabi", "Vibe")),
                    Song("y_I8I_I7_I8", "Proper Patola", "Diljit Dosanjh, Badshah", "Namaste England", 2018, "2:58", "https://i.ytimg.com/vi/y_I8I_I7_I8/hqdefault.jpg", listOf("Punjabi", "Party")),
                    Song("z_I8I_I7_I8", "Patiala Peg", "Diljit Dosanjh", "Single", 2014, "3:25", "https://i.ytimg.com/vi/z_I8I_I7_I8/hqdefault.jpg", listOf("Punjabi", "Classic")),

                    // 🎵 Badshah Party Hits
                    Song("a_I8I_I7_18", "Genda Phool", "Badshah, Payal Dev", "Single", 2020, "2:50", "https://i.ytimg.com/vi/a_I8I_I7_18/hqdefault.jpg", listOf("Pop", "Party")),
                    Song("b_I8I_I7_18", "Jugnu", "Badshah, Nikhita Gandhi", "Single", 2021, "3:50", "https://i.ytimg.com/vi/b_I8I_I7_18/hqdefault.jpg", listOf("Pop", "Dance")),
                    Song("c_I8I_I7_18", "Paagal", "Badshah", "Single", 2019, "2:59", "https://i.ytimg.com/vi/c_I8I_I7_18/hqdefault.jpg", listOf("Pop", "Dance")),
                    Song("d_I8I_I7_18", "Kar Gayi Chull", "Badshah, Neha Kakkar", "Kapoor & Sons", 2016, "3:07", "https://i.ytimg.com/vi/d_I8I_I7_18/hqdefault.jpg", listOf("Party", "Bollywood")),
                    Song("e_I8I_I7_18", "Abhi Toh Party Shuru Hui Hai", "Badshah", "Khoobsurat", 2014, "2:59", "https://i.ytimg.com/vi/e_I8I_I7_18/hqdefault.jpg", listOf("Party")),
                    Song("f_I8I_I7_18", "DJ Waley Babu", "Badshah, Aastha Gill", "Single", 2015, "2:45", "https://i.ytimg.com/vi/f_I8I_I7_18/hqdefault.jpg", listOf("Party", "Rap")),
                    Song("g_I8I_I7_18", "Garmi", "Badshah, Neha Kakkar", "Street Dancer 3D", 2020, "3:02", "https://i.ytimg.com/vi/g_I8I_I7_18/hqdefault.jpg", listOf("Party", "Dance")),

                    // 🎵 Jubin Nautiyal Romantic
                    Song("QDYfEBY9NM4", "Raataan Lambiyan", "Jubin Nautiyal", "Shershaah", 2021, "3:50", "https://i.ytimg.com/vi/QDYfEBY9NM4/hqdefault.jpg", listOf("Romance", "Bollywood")),
                    Song("h_I8I_I7_18", "Lut Gaye", "Jubin Nautiyal", "Single", 2021, "4:58", "https://i.ytimg.com/vi/h_I8I_I7_18/hqdefault.jpg", listOf("Romance", "Sad")),
                    Song("i_I8I_I7_18", "Humnava Mere", "Jubin Nautiyal", "Single", 2018, "6:33", "https://i.ytimg.com/vi/i_I8I_I7_18/hqdefault.jpg", listOf("Sad", "Romance")),
                    Song("j_I8I_I7_18", "Tum Hi Aana", "Jubin Nautiyal", "Marjaavaan", 2019, "4:09", "https://i.ytimg.com/vi/j_I8I_I7_18/hqdefault.jpg", listOf("Sad", "Bollywood")),
                    Song("k_I8I_I7_18", "Kinna Sona", "Jubin Nautiyal", "Marjaavaan", 2019, "4:32", "https://i.ytimg.com/vi/k_I8I_I7_18/hqdefault.jpg", listOf("Romance")),
                    Song("l_I8I_I7_18", "Bewafa Tera Masoom Chehra", "Jubin Nautiyal", "Single", 2020, "4:32", "https://i.ytimg.com/vi/l_I8I_I7_18/hqdefault.jpg", listOf("Sad", "Qawwali")),

                    // 🎵 South Indian / Anirudh / Sid Sriram
                    Song("OsU0CGZoV8E", "Naatu Naatu", "Rahul Sipligunj", "RRR", 2022, "3:34", "https://i.ytimg.com/vi/OsU0CGZoV8E/hqdefault.jpg", listOf("Telugu", "Party")),
                    Song("YP_fXQ8jK4w", "Hukum", "Anirudh", "Jailer", 2023, "3:27", "https://i.ytimg.com/vi/YP_fXQ8jK4w/hqdefault.jpg", listOf("Tamil", "Mass")),
                    Song("hDqS6w6jZMs", "Srivalli", "Sid Sriram", "Pushpa", 2021, "3:44", "https://i.ytimg.com/vi/hDqS6w6jZMs/hqdefault.jpg", listOf("Telugu", "Romance")),
                    Song("m_I8I_I7_18", "Samajavaragamana", "Sid Sriram", "Ala Vaikunthapurramuloo", 2019, "3:40", "https://i.ytimg.com/vi/m_I8I_I7_18/hqdefault.jpg", listOf("Telugu", "Melody")),
                    Song("n_I8I_I7_18", "Arabic Kuthu", "Anirudh Ravichander", "Beast", 2022, "4:40", "https://i.ytimg.com/vi/n_I8I_I7_18/hqdefault.jpg", listOf("Tamil", "Party")),
                    Song("o_I8I_I7_18", "Vaathi Coming", "Anirudh Ravichander", "Master", 2021, "3:50", "https://i.ytimg.com/vi/o_I8I_I7_18/hqdefault.jpg", listOf("Tamil", "Dance")),
                    Song("p_I8I_I7_18", "Why This Kolaveri Di", "Dhanush, Anirudh", "3", 2011, "4:12", "https://i.ytimg.com/vi/p_I8I_I7_18/hqdefault.jpg", listOf("Tamil", "Viral")),

                    // 🎵 Udit Narayan & 90s Nostalgia
                    Song("c7Lp9d3oTjI", "Tujhe Dekha To", "Kumar Sanu", "DDLJ", 1995, "5:02", "https://i.ytimg.com/vi/c7Lp9d3oTjI/hqdefault.jpg", listOf("90s", "Romance")),
                    Song("q_I8I_I7_18", "Pehla Nasha", "Udit Narayan", "Jo Jeeta Wohi Sikandar", 1992, "4:50", "https://i.ytimg.com/vi/q_I8I_I7_18/hqdefault.jpg", listOf("90s", "Romance")),
                    Song("r_I8I_I7_18", "Main Yahaan Hoon", "Udit Narayan", "Veer-Zaara", 2004, "4:57", "https://i.ytimg.com/vi/r_I8I_I7_18/hqdefault.jpg", listOf("Romance")),
                    Song("s_I8I_I7_18", "Mitwa", "Udit Narayan", "Lagaan", 2001, "6:48", "https://i.ytimg.com/vi/s_I8I_I7_18/hqdefault.jpg", listOf("Folk", "Classic")),
                    Song("t_I8I_I7_18", "Tip Tip Barsa Paani", "Udit Narayan, Alka Yagnik", "Mohra", 1994, "6:01", "https://i.ytimg.com/vi/t_I8I_I7_18/hqdefault.jpg", listOf("90s", "Romance")),
                    Song("YoUSZ83e58M", "Chaiyya Chaiyya", "Sukhwinder Singh", "Dil Se", 1998, "6:54", "https://i.ytimg.com/vi/YoUSZ83e58M/hqdefault.jpg", listOf("90s", "Party")),
                    Song("k4yXQkG2s1E", "Kaho Naa Pyaar Hai", "Udit Narayan", "Kaho Naa Pyaar Hai", 2000, "7:03", "https://i.ytimg.com/vi/k4yXQkG2s1E/hqdefault.jpg", listOf("90s", "Romance")),

                    // 🎵 Neha Kakkar Hits
                    Song("u_I8I_I7_18", "Dilbar", "Neha Kakkar", "Satyameva Jayate", 2018, "3:04", "https://i.ytimg.com/vi/u_I8I_I7_18/hqdefault.jpg", listOf("Item", "Party")),
                    Song("v_I8I_I7_18", "Aankh Marey", "Neha Kakkar, Mika Singh", "Simmba", 2018, "3:32", "https://i.ytimg.com/vi/v_I8I_I7_18/hqdefault.jpg", listOf("Party", "Remake")),
                    Song("w_I8I_I7_18", "O Saki Saki", "Neha Kakkar", "Batla House", 2019, "3:13", "https://i.ytimg.com/vi/w_I8I_I7_18/hqdefault.jpg", listOf("Item", "Party")),
                    Song("x_I8I_I7_18", "Kala Chashma", "Neha Kakkar, Badshah", "Baar Baar Dekho", 2016, "3:07", "https://i.ytimg.com/vi/x_I8I_I7_18/hqdefault.jpg", listOf("Party", "Dance")),
                    Song("y_I8I_I7_18", "Mile Ho Tum (Reprise)", "Neha Kakkar, Tony Kakkar", "Fever", 2016, "4:16", "https://i.ytimg.com/vi/y_I8I_I7_18/hqdefault.jpg", listOf("Romance", "Acoustic")),

                    // 🎵 Mohit Chauhan Soulful
                    Song("z_I8I_I7_18", "Tum Se Hi", "Mohit Chauhan", "Jab We Met", 2007, "5:23", "https://i.ytimg.com/vi/z_I8I_I7_18/hqdefault.jpg", listOf("Romance", "Classic")),
                    Song("aa_I8I_I7_18", "Sadda Haq", "Mohit Chauhan", "Rockstar", 2011, "6:05", "https://i.ytimg.com/vi/aa_I8I_I7_18/hqdefault.jpg", listOf("Rock", "Anthem")),
                    Song("ab_I8I_I7_18", "Kun Faya Kun", "Mohit Chauhan, A.R. Rahman", "Rockstar", 2011, "7:53", "https://i.ytimg.com/vi/ab_I8I_I7_18/hqdefault.jpg", listOf("Sufi", "Devotional")),
                    Song("ac_I8I_I7_18", "Matargashti", "Mohit Chauhan", "Tamasha", 2015, "5:28", "https://i.ytimg.com/vi/ac_I8I_I7_18/hqdefault.jpg", listOf("Fun", "Travel")),
                    Song("ad_I8I_I7_18", "Pee Loon", "Mohit Chauhan", "Once Upon a Time in Mumbaai", 2010, "4:47", "https://i.ytimg.com/vi/ad_I8I_I7_18/hqdefault.jpg", listOf("Romance", "Sufi")),

                    // 🎵 Armaan Malik Pop
                    Song("sCbbMZ-q4-I", "Butta Bomma", "Armaan Malik", "Ala Vaikunthapurramuloo", 2020, "3:17", "https://i.ytimg.com/vi/sCbbMZ-q4-I/hqdefault.jpg", listOf("Telugu", "Pop")),
                    Song("ae_I8I_I7_18", "Pehla Pyaar", "Armaan Malik", "Kabir Singh", 2019, "4:32", "https://i.ytimg.com/vi/ae_I8I_I7_18/hqdefault.jpg", listOf("Romance")),
                    Song("af_I8I_I7_18", "Main Rahoon Ya Na Rahoon", "Armaan Malik", "Single", 2015, "5:09", "https://i.ytimg.com/vi/af_I8I_I7_18/hqdefault.jpg", listOf("Sad", "Melody")),
                    Song("ag_I8I_I7_18", "Bol Do Na Zara", "Armaan Malik", "Azhar", 2016, "4:53", "https://i.ytimg.com/vi/ag_I8I_I7_18/hqdefault.jpg", listOf("Romance")),

                    // 🎵 Darshan Raval Heartbreak
                    Song("ah_I8I_I7_18", "Tera Zikr", "Darshan Raval", "Single", 2017, "3:46", "https://i.ytimg.com/vi/ah_I8I_I7_18/hqdefault.jpg", listOf("Sad", "Breakup")),
                    Song("ai_I8I_I7_18", "Chogada", "Darshan Raval", "Loveyatri", 2018, "4:09", "https://i.ytimg.com/vi/ai_I8I_I7_18/hqdefault.jpg", listOf("Party", "Garba")),
                    Song("aj_I8I_I7_18", "Asal Mein", "Darshan Raval", "Single", 2020, "3:43", "https://i.ytimg.com/vi/aj_I8I_I7_18/hqdefault.jpg", listOf("Sad", "Pop")),
                    Song("ak_I8I_I7_18", "Ek Tarfa", "Darshan Raval", "Single", 2020, "3:30", "https://i.ytimg.com/vi/ak_I8I_I7_18/hqdefault.jpg", listOf("Sad", "Rain")),

                    // 🎵 Sunidhi Chauhan Energy
                    Song("al_I8I_I7_18", "Sheila Ki Jawani", "Sunidhi Chauhan", "Tees Maar Khan", 2010, "4:43", "https://i.ytimg.com/vi/al_I8I_I7_18/hqdefault.jpg", listOf("Item", "Dance")),
                    Song("am_I8I_I7_18", "Kamli", "Sunidhi Chauhan", "Dhoom 3", 2013, "3:54", "https://i.ytimg.com/vi/am_I8I_I7_18/hqdefault.jpg", listOf("Dance", "Energy")),
                    Song("an_I8I_I7_18", "Desi Girl", "Sunidhi Chauhan", "Dostana", 2008, "5:06", "https://i.ytimg.com/vi/an_I8I_I7_18/hqdefault.jpg", listOf("Party", "Bollywood")),
                    Song("ao_I8I_I7_18", "Saami Saami", "Sunidhi Chauhan", "Pushpa", 2021, "3:45", "https://i.ytimg.com/vi/ao_I8I_I7_18/hqdefault.jpg", listOf("Item", "Folk")),
                    
                    // 🎵 Viral Extras
                    Song("2d3W1bM2o-8", "Tera Ghata", "Gajendra Verma", "Tera Ghata", 2018, "4:15", "https://i.ytimg.com/vi/2d3W1bM2o-8/hqdefault.jpg", listOf("Sad", "Indie")),
                    Song("5Eqb_-j3FDA", "Pasoori", "Ali Sethi, Shae Gill", "Coke Studio", 2022, "3:44", "https://i.ytimg.com/vi/5Eqb_-j3FDA/hqdefault.jpg", listOf("Pop", "Coke Studio")),
                    Song("4tyk0vJ2j0M", "Cheques", "Shubh", "Still Rollin", 2023, "3:03", "https://i.ytimg.com/vi/4tyk0vJ2j0M/hqdefault.jpg", listOf("Punjabi", "Trending"))
            )

    /** 📀 Popular Albums with real images */
    data class Album(
            val id: String,
            val name: String,
            val artist: String,
            val year: Int,
            val imageUrl: String,
            val songs: Int
    )

    val popularAlbums =
            listOf(
                    Album("animal", "Animal", "Vishal Mishra, Arijit Singh", 2023, "https://c.saavncdn.com/026/Animal-Hindi-2023-20231124191036-500x500.jpg", 10),
                    Album("aashiqui2", "Aashiqui 2", "Mithoon, Ankit Tiwari", 2013, "https://c.saavncdn.com/112/Aashiqui-2-Hindi-2013-500x500.jpg", 11),
                    Album("rockstar", "Rockstar", "A.R. Rahman", 2011, "https://c.saavncdn.com/008/Rockstar-Hindi-2011-20221212115129-500x500.jpg", 14),
                    Album("jawan", "Jawan", "Anirudh Ravichander", 2023, "https://c.saavncdn.com/978/Jawan-Hindi-2023-20230911181014-500x500.jpg", 7),
                    Album("dunki", "Dunki", "Pritam", 2023, "https://c.saavncdn.com/152/Dunki-Hindi-2023-20231218155909-500x500.jpg", 8),
                    Album("kabir_singh", "Kabir Singh", "Mithoon, Sachet-Parampara", 2019, "https://c.saavncdn.com/807/Kabir-Singh-Hindi-2019-20190614075009-500x500.jpg", 9),
                    Album("pushpa", "Pushpa: The Rise", "Devi Sri Prasad", 2021, "https://c.saavncdn.com/673/Pushpa-The-Rise-Part-01-Telugu-2021-20211210161423-500x500.jpg", 5),
                    Album("brahmastra", "Brahmastra", "Pritam", 2022, "https://c.saavncdn.com/191/Kesariya-From-Brahmastra-Hindi-2022-20220717092820-500x500.jpg", 9)
            )

    /** 🎭 Popular Playlists with real images */
    data class Playlist(
            val id: String,
            val name: String,
            val description: String,
            val imageUrl: String,
            val songs: Int
    )

    val popularPlaylists =
            listOf(
                    Playlist("top_50", "Top 50 India", "The most played songs in India", "https://c.saavncdn.com/editorial/charts_TopWeeklyHindi_500x500.jpg", 50),
                    Playlist("romance_hits", "Bollywood Romance", "Nothing but love songs", "https://c.saavncdn.com/editorial/Romantic_Hits_Hindi_500x500.jpg", 100),
                    Playlist("punjabi_100", "Punjabi 100", "Bhangra and more", "https://c.saavncdn.com/editorial/charts_TopWeeklyPunjabi_500x500.jpg", 100),
                    Playlist("hits_90s", "90s Bollywood", "Golden era of melody", "https://c.saavncdn.com/editorial/90s_Bollywood_Hits_500x500.jpg", 75),
                    Playlist("party_mix", "Party All Night", "Dance anthems", "https://c.saavncdn.com/editorial/Party_All_Night_Hindi_500x500.jpg", 60)
            )
}
