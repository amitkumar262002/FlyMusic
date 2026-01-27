package com.example.flymusicai.api.innertube

import kotlinx.serialization.Serializable

@Serializable
data class InnerTubeContext(val client: Client) {
        @Serializable
        data class Client(
                val clientName: String,
                val clientVersion: String,
                val gl: String = "IN",
                val hl: String = "hi",
                val visitorData: String? = null,
                val userAgent: String? = null
        )
}

@Serializable
data class SearchBody(
        val context: InnerTubeContext,
        val query: String? = null,
        val params: String? = null
)

@Serializable
data class BrowseBody(
        val context: InnerTubeContext,
        val browseId: String? = null,
        val params: String? = null
)

@Serializable
data class PlayerBody(
        val context: InnerTubeContext,
        val videoId: String,
        val playlistId: String? = null,
        val playbackContext: PlaybackContext? = null
)

@Serializable data class PlaybackContext(val contentPlaybackContext: ContentPlaybackContext)

@Serializable data class ContentPlaybackContext(val signatureTimestamp: Int? = null)

object YouTubeClients {
        val WEB_REMIX =
                YouTubeClientConfig(
                        clientName = "WEB_REMIX",
                        clientVersion = "1.20220606.03.00",
                        apiKey = "AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30",
                        userAgent =
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36"
                )

        val IOS =
                YouTubeClientConfig(
                        clientName = "IOS",
                        clientVersion = "19.30.2",
                        apiKey = "AIzaSyB-63vPrdThhKuerbB2N_l7Kwwcxj6yUAc",
                        userAgent =
                                "com.google.ios.youtube/19.30.2 (iPhone16,2; U; CPU iOS 17_5_1 like Mac OS X;)"
                )

        val TVHTML5 =
                YouTubeClientConfig(
                        clientName = "TVHTML5",
                        clientVersion = "2.0",
                        apiKey = "AIzaSyDCU8hByM-4DrUqRUYnGn-3llEO78bcxq8",
                        userAgent =
                                "Mozilla/5.0 (PlayStation 4 5.55) AppleWebKit/601.2 (KHTML, like Gecko)"
                )

        val ANDROID =
                YouTubeClientConfig(
                        clientName = "ANDROID",
                        clientVersion = "19.30.36",
                        apiKey = "AIzaSyB-63vPrdThhKuerbB2N_l7Kwwcxj6yUAc",
                        userAgent =
                                "com.google.android.youtube/19.30.36 (Linux; U; Android 14; en_US) gzip"
                )

        val ANDROID_MUSIC =
                YouTubeClientConfig(
                        clientName = "ANDROID_MUSIC",
                        clientVersion = "7.03.52",
                        apiKey = "AIzaSyB-63vPrdThhKuerbB2N_l7Kwwcxj6yUAc",
                        userAgent =
                                "com.google.android.apps.youtube.music/7.03.52 (Linux; U; Android 14; en_US) gzip"
                )

        val WEB =
                YouTubeClientConfig(
                        clientName = "WEB",
                        clientVersion = "2.20230301.09.00",
                        apiKey = "AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30",
                        userAgent =
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36"
                )

        val MWEB =
                YouTubeClientConfig(
                        clientName = "MWEB",
                        clientVersion = "2.20230301.09.00",
                        apiKey = "AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30",
                        userAgent =
                                "Mozilla/5.0 (Linux; Android 12; Pixel 6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Mobile Safari/537.36"
                )

        val ANDROID_TESTSUITE =
                YouTubeClientConfig(
                        clientName = "ANDROID_TESTSUITE",
                        clientVersion = "1.9",
                        apiKey = "AIzaSyB-63vPrdThhKuerbB2N_l7Kwwcxj6yUAc",
                        userAgent =
                                "com.google.android.youtube.testsuite/1.9 (Linux; U; Android 12; en_US) gzip"
                )

        val ANDROID_VR =
                YouTubeClientConfig(
                        clientName = "ANDROID_VR",
                        clientVersion = "1.50.45",
                        apiKey = "AIzaSyB-63vPrdThhKuerbB2N_l7Kwwcxj6yUAc",
                        userAgent =
                                "com.google.android.apps.youtube.vr/1.50.45 (Linux; U; Android 12; en_US) gzip"
                )
}

data class YouTubeClientConfig(
        val clientName: String,
        val clientVersion: String,
        val apiKey: String,
        val userAgent: String
) {
        fun toInnerTubeContext() =
                InnerTubeContext(
                        client =
                                InnerTubeContext.Client(
                                        clientName = clientName,
                                        clientVersion = clientVersion,
                                        userAgent = userAgent
                                )
                )
}
