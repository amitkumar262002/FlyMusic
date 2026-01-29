package com.example.flymusicai.data

/**
 * 🎤 Artist Information and Static Content
 */
data class ArtistInfo(
    val name: String,
    val imageUrl: String,
    val genre: String
)

object ArtistConstants {
    
    val TOP_SINGERS = listOf(
        ArtistInfo("Arijit Singh", "https://c.saavncdn.com/artists/Arijit_Singh_007_20230916071548_500x500.jpg", "Bollywood"),
        ArtistInfo("Shreya Ghoshal", "https://c.saavncdn.com/artists/Shreya_Ghoshal_005_20230616120521_500x500.jpg", "Bollywood"),
        ArtistInfo("Atif Aslam", "https://c.saavncdn.com/artists/Atif_Aslam_004_20230616120521_500x500.jpg", "Romantic"),
        ArtistInfo("Neha Kakkar", "https://c.saavncdn.com/artists/Neha_Kakkar_006_20230616120521_500x500.jpg", "Bollywood"),
        ArtistInfo("Jubin Nautiyal", "https://c.saavncdn.com/artists/Jubin_Nautiyal_005_20210616120521_500x500.jpg", "Bollywood"),
        ArtistInfo("Armaan Malik", "https://c.saavncdn.com/artists/Armaan_Malik_500x500.jpg", "Bollywood"),
        ArtistInfo("Sonu Nigam", "https://c.saavncdn.com/artists/Sonu_Nigam_006_20230616120521_500x500.jpg", "Bollywood"),
        ArtistInfo("Diljit Dosanjh", "https://c.saavncdn.com/artists/Diljit_Dosanjh_004_20230616120521_500x500.jpg", "Punjabi"),
        ArtistInfo("Badshah", "https://c.saavncdn.com/artists/Badshah_500x500.jpg", "Rap"),
        ArtistInfo("Mohit Chauhan", "https://c.saavncdn.com/artists/Mohit_Chauhan_500x500.jpg", "Indie"),
        ArtistInfo("Anirudh Ravichander", "https://c.saavncdn.com/artists/Anirudh_Ravichander_500x500.jpg", "Tamil"),
        ArtistInfo("Sid Sriram", "https://c.saavncdn.com/artists/Sid_Sriram_500x500.jpg", "South Indian"),
        ArtistInfo("Darshan Raval", "https://c.saavncdn.com/artists/Darshan_Raval_500x500.jpg", "Pop"),
        ArtistInfo("Sunidhi Chauhan", "https://c.saavncdn.com/artists/Sunidhi_Chauhan_500x500.jpg", "Party"),
        ArtistInfo("Udit Narayan", "https://c.saavncdn.com/artists/Udit_Narayan_500x500.jpg", "90s Hit"),
        ArtistInfo("Sidhu Moose Wala", "https://c.saavncdn.com/artists/Sidhu_Moose_Wala_500x500.jpg", "Punjabi")
    )

    val CATEGORY_IMAGES = mapOf(
        "Bollywood" to "https://c.saavncdn.com/152/Dunki-Hindi-2023-20231218155909-500x500.jpg",
        "Romantic" to "https://c.saavncdn.com/editorial/Romantic_Hits_Hindi_139364_500x500.jpg",
        "Party" to "https://c.saavncdn.com/editorial/Party_All_Night_Hindi_500x500.jpg",
        "90s Hits" to "https://c.saavncdn.com/editorial/90s_Bollywood_Hits_500x500.jpg",
        "Top Charts" to "https://c.saavncdn.com/editorial/charts_TopWeeklyHindi_139364_20231201123456_500x500.jpg"
    )

    fun getArtistImage(name: String): String {
        return TOP_SINGERS.find { it.name.equals(name, ignoreCase = true) }?.imageUrl
            ?: "https://ui-avatars.com/api/?name=${name.replace(" ", "+")}&background=random&size=512"
    }
}
