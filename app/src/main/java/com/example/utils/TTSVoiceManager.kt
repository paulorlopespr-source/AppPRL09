package com.example.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * High-fidelity AI Voice Coach powered by Gemini Neural Audio.
 * Uses Gemini speech synthesis (model: gemini-2.5-flash-preview-tts) with natural conversational male voices
 * (Puck, Fenrir, Charon, Orus) and automatic audio caching + offline system TTS fallback.
 */
class TTSVoiceManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var mediaPlayer: MediaPlayer? = null
    private var systemTts: TextToSpeech? = null
    private var isSystemTtsReady = false

    var isEnabled: Boolean = true
    var selectedVoiceName: String = "Puck" // Options: "Puck" (Energetic/Conversational), "Fenrir" (Deep/Powerful), "Charon" (Calm/Confident), "Orus" (Athletic)

    private val audioMemoryCache = ConcurrentHashMap<String, File>()
    private val cacheDir: File by lazy {
        File(context.cacheDir, "gemini_voice_cache").apply { if (!exists()) mkdirs() }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    init {
        initSystemTtsFallback()
    }

    private fun initSystemTtsFallback() {
        try {
            systemTts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val ptBrLocale = Locale("pt", "BR")
                    systemTts?.setLanguage(ptBrLocale)
                    applyMasculineNaturalVoice()
                    systemTts?.setPitch(0.92f)
                    systemTts?.setSpeechRate(1.0f)
                    isSystemTtsReady = true
                }
            }
        } catch (e: Exception) {
            Log.e("TTSVoiceManager", "Error initializing fallback TTS", e)
        }
    }

    private fun applyMasculineNaturalVoice() {
        try {
            val voices: Set<Voice>? = systemTts?.voices
            if (!voices.isNullOrEmpty()) {
                val ptVoices = voices.filter { it.locale.language.equals("pt", ignoreCase = true) }
                val maleVoice = ptVoices.firstOrNull { voice ->
                    val name = voice.name.lowercase()
                    name.contains("male") || name.contains("masc") || name.contains("homem") ||
                    name.contains("pt-br-x-ytd") || name.contains("pt-br-x-ptd")
                } ?: ptVoices.firstOrNull { !it.name.lowercase().contains("female") }
                if (maleVoice != null) {
                    systemTts?.voice = maleVoice
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Speaks text using Gemini Neural Audio (Natural Conversational Tone).
     */
    fun speak(text: String, voiceName: String = selectedVoiceName) {
        if (!isEnabled || text.isBlank()) return

        scope.launch {
            val playedGeminiAudio = playGeminiNeuralAudio(text, voiceName)
            if (!playedGeminiAudio) {
                // Fallback to local TTS
                withContext(Dispatchers.Main) {
                    if (isSystemTtsReady) {
                        systemTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "fallback_${System.currentTimeMillis()}")
                    }
                }
            }
        }
    }

    private suspend fun playGeminiNeuralAudio(text: String, voiceName: String): Boolean {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") return false

        try {
            val cacheKey = "${voiceName}_${text.hashCode()}"
            val cachedFile = audioMemoryCache[cacheKey] ?: File(cacheDir, "$cacheKey.wav").takeIf { it.exists() && it.length() > 100 }

            val audioFile: File = if (cachedFile != null) {
                cachedFile
            } else {
                val generatedFile = fetchGeminiAudio(text, voiceName, apiKey, cacheKey) ?: return false
                audioMemoryCache[cacheKey] = generatedFile
                generatedFile
            }

            return playAudioFile(audioFile)
        } catch (e: Exception) {
            Log.w("TTSVoiceManager", "Gemini neural audio playback failed, falling back to TTS: ${e.message}")
            return false
        }
    }

    private fun fetchGeminiAudio(text: String, voiceName: String, apiKey: String, cacheKey: String): File? {
        val prompt = "Você é um treinador de musculação de elite e amigo do atleta. Fale com naturalidade, ritmo humano, postura motivadora e firme em português do Brasil: \"$text\""

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseModalities", JSONArray().apply {
                    put("AUDIO")
                })
                put("speechConfig", JSONObject().apply {
                    put("voiceConfig", JSONObject().apply {
                        put("prebuiltVoiceConfig", JSONObject().apply {
                            put("voiceName", voiceName)
                        })
                    })
                })
            })
        }

        // Try modern TTS model first, then fallback to native audio model
        val models = listOf("gemini-2.5-flash-preview-tts", "gemini-2.5-flash-native-audio-preview-12-2025")

        for (model in models) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                val request = Request.Builder()
                    .url(url)
                    .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: continue
                    val jsonResponse = JSONObject(bodyString)
                    val candidates = jsonResponse.optJSONArray("candidates") ?: continue
                    val firstCandidate = candidates.optJSONObject(0) ?: continue
                    val content = firstCandidate.optJSONObject("content") ?: continue
                    val parts = content.optJSONArray("parts") ?: continue

                    for (i in 0 until parts.length()) {
                        val part = parts.optJSONObject(i) ?: continue
                        val inlineData = part.optJSONObject("inlineData") ?: continue
                        val base64Data = inlineData.optString("data")
                        val mimeType = inlineData.optString("mimeType", "audio/wav")

                        if (base64Data.isNotBlank()) {
                            val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
                            val finalBytes = if (mimeType.contains("pcm") || (!audioBytes.startsWithRiff() && !mimeType.contains("mp3"))) {
                                pcmToWav(audioBytes, sampleRate = 24000)
                            } else {
                                audioBytes
                            }

                            val targetFile = File(cacheDir, "$cacheKey.wav")
                            FileOutputStream(targetFile).use { it.write(finalBytes) }
                            return targetFile
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("TTSVoiceManager", "Error querying $model: ${e.message}")
            }
        }
        return null
    }

    private suspend fun playAudioFile(file: File): Boolean = withContext(Dispatchers.Main) {
        try {
            stopCurrentAudio()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .build()
                )
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                }
            }
            true
        } catch (e: Exception) {
            Log.e("TTSVoiceManager", "Failed to play audio file", e)
            false
        }
    }

    private fun stopCurrentAudio() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
            mediaPlayer = null
        } catch (_: Exception) {}
    }

    private fun ByteArray.startsWithRiff(): Boolean {
        return size > 4 && this[0] == 'R'.code.toByte() && this[1] == 'I'.code.toByte() && this[2] == 'F'.code.toByte() && this[3] == 'F'.code.toByte()
    }

    private fun pcmToWav(pcmData: ByteArray, sampleRate: Int = 24000, channels: Int = 1, bitsPerSample: Int = 16): ByteArray {
        val totalAudioLen = pcmData.size
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val header = ByteArray(44)

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * bitsPerSample / 8).toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

        return header + pcmData
    }

    fun speakCountdown(secondsRemaining: Int) {
        if (!isEnabled) return
        when (secondsRemaining) {
            5 -> {
                val phrases = listOf(
                    "Cinco segundos guerreiro, prepara a pegada!",
                    "Cinco segundos! Respira fundo e concentra.",
                    "Atenção, cinco segundos pra próxima série!"
                )
                speak(phrases[Random.nextInt(phrases.size)])
            }
            3 -> speak("Três")
            2 -> speak("Dois")
            1 -> speak("Um! Vai com tudo!")
        }
    }

    fun speakRestFinished(
        exerciseName: String? = null,
        weightKg: Double? = null,
        reps: Int? = null
    ) {
        if (!isEnabled) return

        val weightStr = if (weightKg != null && weightKg > 0.0) {
            if (weightKg % 1.0 == 0.0) "${weightKg.toInt()} quilos" else "$weightKg quilos"
        } else null

        val repsStr = if (reps != null && reps > 0) "$reps repetições" else null

        val targetInfo = when {
            exerciseName != null && weightStr != null && repsStr != null ->
                "Próxima série de $exerciseName, $weightStr com $repsStr."
            exerciseName != null && weightStr != null ->
                "Próxima série de $exerciseName com $weightStr."
            exerciseName != null ->
                "Próxima série de $exerciseName."
            else -> ""
        }

        val motivationalIntros = listOf(
            "Descanso encerrado!",
            "Bora guerreiro, tempo esgotado!",
            "Hora do show!",
            "Descanso finalizado!"
        )

        val motivationalClosers = listOf(
            "Foco total na contração, vamos buscar o resultado!",
            "Carga na barra e mente blindada. Arrebenta!",
            "Cada repetição constrói o seu objetivo. Bora pra cima!",
            "Postura impecável e intensidade máxima hoje!",
            "Vamos com tudo! Não deixa a barra vencer!"
        )

        val intro = motivationalIntros[Random.nextInt(motivationalIntros.size)]
        val closer = motivationalClosers[Random.nextInt(motivationalClosers.size)]

        val fullMessage = if (targetInfo.isNotBlank()) {
            "$intro $targetInfo $closer"
        } else {
            "$intro $closer"
        }

        speak(fullMessage)
    }

    fun speakPersonalRecord(exerciseName: String, weightKg: Double) {
        if (!isEnabled) return
        val weightStr = if (weightKg % 1.0 == 0.0) weightKg.toInt().toString() else weightKg.toString()
        val prPhrases = listOf(
            "Sensacional, guerreiro! Novo recorde pessoal destruído no $exerciseName com $weightStr quilos! Você tá imparável!",
            "Que orgulho! Novo recorde pessoal no $exerciseName com $weightStr quilos! Disciplina e força de verdade!",
            "Monstro! Novo recorde batido no $exerciseName com $weightStr quilos! O trabalho duro compensa!"
        )
        speak(prPhrases[Random.nextInt(prPhrases.size)])
    }

    fun speakStartWorkout(workoutTitle: String) {
        if (!isEnabled) return
        val phrases = listOf(
            "Treino iniciado! Bora pra cima com foco total e postura perfeita hoje.",
            "Bora guerreiro! Treino de $workoutTitle valendo. Sangue nos olhos e boa execução!",
            "Hora de buscar a melhor versão. Treino iniciado, vamos com tudo!"
        )
        speak(phrases[Random.nextInt(phrases.size)])
    }

    fun speakWorkoutCompleted(durationMinutes: Int, totalWeightKg: Double) {
        if (!isEnabled) return
        val tonnage = (totalWeightKg / 1000.0)
        val weightText = if (tonnage >= 1.0) {
            String.format(Locale("pt", "BR"), "%.1f toneladas", tonnage)
        } else {
            "${totalWeightKg.toInt()} quilos"
        }

        val phrases = listOf(
            "Missão cumprida, guerreiro! Treino finalizado com maestria e $weightText levantados. Excelente trabalho!",
            "Treino finalizado com sucesso! Mais uma vitória pra sua conta. Descanse e recupere bem!",
            "Sensacional! Treino concluído com honra e dedicação. O shape agradece, até a próxima!"
        )
        speak(phrases[Random.nextInt(phrases.size)])
    }

    fun speakMotivation() {
        if (!isEnabled) return
        val motivationalQuotes = listOf(
            "A dor é temporária, o resultado é pra sempre. Bora!",
            "Mente firme, corpo forte. Não desiste agora!",
            "Você é mais forte do que qualquer desculpa!",
            "Foco no processo. A consistência sempre vence!"
        )
        speak(motivationalQuotes[Random.nextInt(motivationalQuotes.size)])
    }

    fun shutdown() {
        try {
            stopCurrentAudio()
            systemTts?.stop()
            systemTts?.shutdown()
            systemTts = null
            isSystemTtsReady = false
        } catch (_: Exception) {}
    }
}
