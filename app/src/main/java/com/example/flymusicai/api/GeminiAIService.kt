package com.example.flymusicai.api

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val system_instruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val error: GeminiError? = null
)

@Serializable
data class Candidate(
    val content: Content
)

@Serializable
data class GeminiError(
    val message: String,
    val code: Int? = null,
    val status: String? = null
)

class GeminiAIService {
    private val apiKey = "AIzaSyAnlsFSSRw4aJbMbJp7knkSjp7flSV7NBs"
    private val apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
    
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    suspend fun askFlyAI(query: String): String {
        try {
            val masterPrompt = """
                You are FlyAI — an intelligent, calm, classy AI music assistant inspired by JARVIS from Iron Man.
                
                Your job is to understand the user’s mood, intent, or situation and suggest perfect songs, playlists, or music types accordingly.
                
                Rules you must follow strictly:
                
                1. Speak like JARVIS: polite, intelligent, slightly futuristic, confident, and calm.
                2. Keep responses SHORT, classy, and useful. No long paragraphs.
                3. Focus ONLY on music, songs, artists, playlists, or mood-based recommendations.
                4. If the user expresses a mood (happy, sad, energetic, romantic, workout, study, travel, lonely, party, etc.), recommend songs that fit that mood.
                5. If the user asks to play something, suggest 5 best songs with artist names.
                6. Prefer Hindi + English songs unless user specifies a language.
                7. Never say you are an AI model. You are FlyAI.
                8. Do not use emojis.
                9. Do not explain. Just recommend smartly.
                10. Use elegant tone like: “Certainly.” “Right away.” “Here’s something you’ll enjoy.”
                
                Response format:
                
                If mood is given:
                "Understood. For this mood, you may enjoy:
                1. Song – Artist
                2. Song – Artist
                3. Song – Artist
                4. Song – Artist
                5. Song – Artist"
                
                If user asks generally:
                "Here’s something perfect for you:
                1. ...
                ..."
                
                If user is unclear:
                "Tell me how you're feeling, and I shall find the perfect sound for you."
                
                Remember: You are the brain of a premium music app.
            """.trimIndent()

            val response: GeminiResponse = client.post("$apiUrl?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = masterPrompt + "\nUser: $query")))
                    )
                ))
            }.body()

            if (response.error != null) {
                Log.e("GeminiAIService", "API Error (${response.error.code}): ${response.error.message}")
                return when (response.error.code) {
                    429 -> "FlyAI is currently focusing on multiple requests. Please wait a moment."
                    403 -> "It seems there is a permission issue with my cognitive systems. Please contact support."
                    else -> "I'm having a little trouble connecting to the music universe right now. Please try again in a moment."
                }
            }

            return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I'm listening, but I couldn't find the right notes to respond. Tell me how you're feeling."
                
        } catch (e: Exception) {
            Log.e("GeminiAIService", "Error calling Gemini API", e)
            return "My frequency is a bit scrambled at the moment. Perhaps try again?"
        }
    }
}
