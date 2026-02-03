package com.example.flymusicai.api.innertube

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import com.example.flymusicai.BuildConfig

@Serializable
data class InnerTubeContext(val client: Client) {
    @Serializable
    data class Client(
        val clientName: String,
        val clientVersion: String,
        val gl: String,
        val hl: String,
        val visitorData: String? = null,
        val userAgent: String? = null,
        val clientScreen: String? = null,
        val thirdParty: ThirdParty? = null
    )
}

@Serializable
data class ThirdParty(val embedUrl: String)

@Serializable
data class SearchBody(
    val context: InnerTubeContext,
    val query: String? = null,
    val params: String? = null
)

@Serializable
data class PlayerBody(
    val context: InnerTubeContext,
    val videoId: String,
    val playlistId: String? = null,
    val playbackContext: PlaybackContext? = null
)

@Serializable
data class PlaybackContext(val contentPlaybackContext: ContentPlaybackContext)

@Serializable
data class ContentPlaybackContext(val signatureTimestamp: Int? = null)

@Serializable
data class PlayerResponse(
    val streamingData: StreamingData? = null,
    val playabilityStatus: PlayabilityStatus? = null
) {
    @Serializable
    data class StreamingData(
        val adaptiveFormats: List<Format> = emptyList(),
        val formats: List<Format> = emptyList()
    )

    @Serializable
    data class Format(
        val url: String? = null,
        val mimeType: String? = null,
        val bitrate: Int? = null,
        val signatureCipher: String? = null,
        val width: Int? = null
    )

    @Serializable
    data class PlayabilityStatus(
        val status: String,
        val reason: String? = null
    )
}

object YouTubeClients {
    val ANDROID_TESTSUITE = YouTubeClientConfig(
        clientName = "ANDROID_TESTSUITE",
        clientVersion = "1.9",
        apiKey = BuildConfig.INNERTUBE_API_KEY_TESTSUITE,
        userAgent = "com.google.android.youtube.testsuite/1.9 (Linux; U; Android 14; en_US) gzip"
    )

    val ANDROID_VR = YouTubeClientConfig(
        clientName = "ANDROID_VR",
        clientVersion = "1.64.46",
        apiKey = BuildConfig.INNERTUBE_API_KEY_TESTSUITE,
        userAgent = "com.google.android.apps.youtube.vr/1.64.46 (Linux; U; Android 14; en_US) gzip"
    )

    val TVHTML5 = YouTubeClientConfig(
        clientName = "TVHTML5",
        clientVersion = "7.20250101.08.01",
        apiKey = BuildConfig.INNERTUBE_API_KEY_TESTSUITE,
        userAgent = "Mozilla/5.0 (Web0S; Linux/SmartTV) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.5735.197 Safari/537.36"
    )

    val IOS = YouTubeClientConfig(
        clientName = "IOS",
        clientVersion = "19.29.1",
        apiKey = BuildConfig.INNERTUBE_API_KEY_IOS, 
        userAgent = "com.google.ios.youtube/19.29.1 (iPhone14,5; U; CPU iOS 17_4_1 like Mac OS X)"
    )

    val WEB_REMIX = YouTubeClientConfig(
        clientName = "WEB_REMIX",
        clientVersion = "1.20240502.01.00",
        apiKey = BuildConfig.INNERTUBE_API_KEY_WEB_REMIX,
        userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36,gzip(gfe)"
    )

    val ANDROID = YouTubeClientConfig(
        clientName = "ANDROID",
        clientVersion = "19.29.35",
        apiKey = BuildConfig.INNERTUBE_API_KEY_ANDROID,
        userAgent = "com.google.android.youtube/19.29.35 (Linux; U; Android 14; en_US) gzip"
    )
}

data class YouTubeClientConfig(
    val clientName: String,
    val clientVersion: String,
    val apiKey: String,
    val userAgent: String
) {
    fun toInnerTubeContext(videoId: String? = null, gl: String = "IN", hl: String = "hi"): InnerTubeContext {
        val thirdParty = if (clientName == "TVHTML5" && videoId != null) {
            ThirdParty(embedUrl = "https://www.youtube.com/watch?v=$videoId")
        } else null

        return InnerTubeContext(
            client = InnerTubeContext.Client(
                clientName = clientName,
                clientVersion = clientVersion,
                gl = gl,
                hl = hl,
                userAgent = userAgent,
                clientScreen = if (clientName == "TVHTML5") "WATCH" else null,
                visitorData = listOf(
                    "CgtNTE5vVjZid19zNCiS_pS2BRICGgJJTg%3D%3D",
                    "CgtGR2ZSYzdaWWU1dyiS_pS2BRICGgJJTg%3D%3D",
                    "Cgt5ZXRfUkhidVpwbyiS_pS2BRICGgJJTg%3D%3D",
                    "CgtMVnowT1ZIdXpZNCiS_pS2BRICGgJJTg%3D%3D"
                ).random(),
                thirdParty = thirdParty
            )
        )
    }
}
