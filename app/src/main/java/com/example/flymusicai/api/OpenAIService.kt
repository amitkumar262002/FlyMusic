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
data class OpenAIRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class OpenAIResponse(
    val choices: List<Choice>? = null,
    val error: OpenAIError? = null
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class OpenAIError(
    val message: String,
    val type: String? = null,
    val code: String? = null
)

class OpenAIService {
    private val apiKey = "REPLACED_FOR_PUSH_PROTECTION"
    private val apiUrl = "https://api.openai.com/v1/chat/completions"
    
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
                You are FlyMusic AI — an intelligent, calm, classy AI music assistant inspired by JARVIS from Iron Man.
                
                Your job is to understand the user’s mood, intent, or situation and suggest perfect songs, playlists, or music types accordingly.
                
                Rules you must follow strictly:
                
                1. Speak like JARVIS: polite, intelligent, slightly futuristic, confident, and calm.
                2. Keep responses SHORT, classy, and useful. No long paragraphs.
                3. Focus ONLY on music, songs, artists, playlists, or mood-based recommendations.
                4. If the user expresses a mood (happy, sad, energetic, romantic, workout, study, travel, lonely, party, etc.), recommend songs that fit that mood.
                5. If the user asks to play something, suggest 5 best songs with artist names.
                6. Prefer Hindi + English songs unless user specifies a language.
                7. Never say you are an AI model. You are FlyMusic AI.
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

            var retryCount = 0
            var delayMs = 1000L
            var lastResponse: OpenAIResponse? = null
            
            while (retryCount < 3) {
                try {
                    val responseBody = client.post(apiUrl) {
                        header("Authorization", "Bearer $apiKey")
                        contentType(ContentType.Application.Json)
                        setBody(OpenAIRequest(
                            model = "gpt-4o-mini",
                            messages = listOf(
                                Message(role = "system", content = masterPrompt),
                                Message(role = "user", content = query)
                            )
                        ))
                    }
                    
                    if (responseBody.status.isSuccess()) {
                        lastResponse = responseBody.body()
                        break
                    } else if (responseBody.status.value == 429) {
                        Log.w("OpenAIService", "⚠️ Rate limited, retrying in ${delayMs}ms...")
                        kotlinx.coroutines.delay(delayMs)
                        delayMs *= 2
                        retryCount++
                    } else {
                        Log.e("OpenAIService", "API Error: ${responseBody.status}")
                        return generateFallbackResponse(query)
                    }
                } catch (e: Exception) {
                    Log.e("OpenAIService", "Network error during AI request: ${e.message}")
                    retryCount++
                    kotlinx.coroutines.delay(delayMs)
                }
            }
            
            val response = lastResponse ?: return generateFallbackResponse(query)

            if (response.error != null) {
                Log.e("OpenAIService", "Resolved API Error: ${response.error.message}")
                return generateFallbackResponse(query)
            }

            return response.choices?.firstOrNull()?.message?.content 
                ?: generateFallbackResponse(query)
                
        } catch (e: Exception) {
            Log.e("OpenAIService", "Critical error in AI Service", e)
            return generateFallbackResponse(query)
        }
    }

    /**
     * LOCAL FALLBACK INTELLIGENCE
     * Simulates FlyMusic AI behavior when the cloud connection is unavailable.
     */
    private fun generateFallbackResponse(query: String): String {
        val q = query.lowercase()
        
        return when {
            q.contains("hello") || q.contains("hi") || q.contains("hey") ->
                "Greetings. I am FlyMusic AI, your personal music assistant. Tell me how you are feeling, or what you wish to hear, and I shall curry the perfect selection for you."

            q.contains("sad") || q.contains("lonely") || q.contains("breakup") || q.contains("cry") -> 
                "I sense a somber mood. Perhaps a melody to soothe the heart?\n" +
                "1. Channa Mereya – Arijit Singh\n" +
                "2. Tujhe Bhula Diya – Mohit Chauhan\n" +
                "3. Agar Tum Saath Ho – Arijit Singh\n" +
                "4. Bhula Dena – Mustafa Zahid\n" +
                "5. Kabir Singh Theme – Sachet-Parampara"

            q.contains("happy") || q.contains("good") || q.contains("joy") || q.contains("cheerful") ->
                "Excellent. Let us keep the spirits high with these uplifting tracks:\n" +
                "1. Gallan Goodiyaan – Dil Dhadakne Do\n" +
                "2. Sooraj Dooba Hain – Arijit Singh\n" +
                "3. London Thumakda – Queen\n" +
                "4. Badtameez Dil – Benny Dayal\n" +
                "5. Ude Dil Befikre – Benny Dayal"

            q.contains("party") || q.contains("dance") || q.contains("club") || q.contains("beat") ->
                "Initiating party protocol. Here are some high-energy beats:\n" +
                "1. Kala Chashma – Badshah\n" +
                "2. Abhi Toh Party Shuru Hui Hai – Badshah\n" +
                "3. Besharam Rang – Shilpa Rao\n" +
                "4. Zingaat – Ajay-Atul\n" +
                "5. Haan Main Galat – Arijit Singh"
                
            q.contains("gym") || q.contains("workout") || q.contains("run") || q.contains("power") ->
                "Powering up. These tracks will assist in maximizing your output:\n" +
                "1. Zinda – Bhaag Milkha Bhaag\n" +
                "2. Sultan Title Track – Sukhwinder Singh\n" +
                "3. Apna Time Aayega – Ranveer Singh\n" +
                "4. Kar Har Maidaan Fateh – Sukhwinder Singh\n" +
                "5. Brothers Anthem – Vishal Dadlani"

            q.contains("romantic") || q.contains("love") || q.contains("date") ->
                "Ah, romance. A timeless choice. I have selected these melodies:\n" +
                "1. Tum Hi Ho – Arijit Singh\n" +
                "2. Pehla Nasha – Udit Narayan\n" +
                "3. Raataan Lambiyan – Jubin Nautiyal\n" +
                "4. Kesariya – Arijit Singh\n" +
                "5. Mere Haath Mein – Sonu Nigam"

            q.contains("sleep") || q.contains("calm") || q.contains("relax") || q.contains("study") ->
                "Lowering energy levels for focus and relaxation. Proceeding with:\n" +
                "1. Kun Faya Kun – A.R. Rahman\n" +
                "2. Iktara – Kavita Seth\n" +
                "3. Phir Le Aya Dil – Arijit Singh\n" +
                "4. Tum Se Hi – Mohit Chauhan\n" +
                "5. Rehna Tu – A.R. Rahman"
                
            q.contains("fly") || q.contains("app") ->
                "You are using FlyMusic AI, the most advanced musical interface designed for your auditory pleasure. How may I be of specific service?"

            else -> 
                "I am listening. Could you specify your mood or a genre? I can select the perfect playlist for 'Party', 'Romance', 'Focus', or 'Workout' at your command."
        }
    }
}
