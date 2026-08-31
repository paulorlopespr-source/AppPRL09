package com.example.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import java.util.Locale
import kotlin.random.Random

/**
 * Modern Audio & Voice Coach using Android Text-To-Speech.
 * Configured with a natural, authoritative male voice and high-energy motivational coaching prompts.
 */
class TTSVoiceManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    var isEnabled: Boolean = true

    init {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val ptBrLocale = Locale("pt", "BR")
                    val result = tts?.setLanguage(ptBrLocale)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        val fallbackResult = tts?.setLanguage(Locale("pt"))
                        if (fallbackResult == TextToSpeech.LANG_MISSING_DATA || fallbackResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                            tts?.setLanguage(Locale.getDefault())
                        }
                    }

                    // Select natural masculine Brazilian Portuguese voice
                    applyMasculineNaturalVoice()

                    // Calibrated pitch and cadence for an energetic, natural male trainer tone
                    tts?.setPitch(0.90f) // Slightly deeper, natural male voice frequency
                    tts?.setSpeechRate(1.02f) // Fluid, natural human pace
                    isInitialized = true
                    Log.d("TTSVoiceManager", "Male coach TTS engine initialized successfully.")
                } else {
                    Log.w("TTSVoiceManager", "TTS initialization failed with status: $status")
                }
            }
        } catch (e: Exception) {
            Log.e("TTSVoiceManager", "Error initializing TTS", e)
        }
    }

    /**
     * Inspects available system voices and selects the highest quality male PT-BR voice.
     */
    private fun applyMasculineNaturalVoice() {
        try {
            val voices: Set<Voice>? = tts?.voices
            if (!voices.isNullOrEmpty()) {
                val ptVoices = voices.filter {
                    it.locale.language.equals("pt", ignoreCase = true)
                }

                // 1. Search for explicit male voice identifiers (Google TTS male variants: ytd, ptd, afs male)
                val explicitMaleVoice = ptVoices.firstOrNull { voice ->
                    val name = voice.name.lowercase()
                    name.contains("male") || name.contains("masc") || name.contains("homem") ||
                    name.contains("pt-br-x-ytd") || name.contains("pt-br-x-ptd") || name.contains("pt-br-x-afs")
                }

                // 2. Fallback to any non-female Brazilian Portuguese voice
                val nonFemaleVoice = ptVoices.firstOrNull { voice ->
                    val name = voice.name.lowercase()
                    !name.contains("female") && !name.contains("fem") && !name.contains("mulher") && !name.contains("pt-br-x-sfg")
                }

                val selectedVoice = explicitMaleVoice ?: nonFemaleVoice ?: ptVoices.firstOrNull()

                if (selectedVoice != null) {
                    tts?.voice = selectedVoice
                    Log.d("TTSVoiceManager", "Applied masculine voice: ${selectedVoice.name}")
                }
            }
        } catch (e: Exception) {
            Log.w("TTSVoiceManager", "Could not query available voices", e)
        }
    }

    /**
     * Speaks arbitrary text message in Portuguese with natural voice parameters
     */
    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (!isEnabled || !isInitialized || text.isBlank()) return
        try {
            tts?.speak(text, queueMode, null, "utterance_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TTSVoiceManager", "TTS speak failed", e)
        }
    }

    /**
     * Voice cue when rest timer countdown reaches final seconds
     */
    fun speakCountdown(secondsRemaining: Int) {
        if (!isEnabled || !isInitialized) return
        when (secondsRemaining) {
            5 -> {
                val phrases = listOf(
                    "Cinco segundos guerreiro! Prepara a pegada.",
                    "Cinco segundos! Respira fundo e concentra.",
                    "Atenção, cinco segundos pra próxima série!"
                )
                speak(phrases[Random.nextInt(phrases.size)])
            }
            3 -> speak("Três...")
            2 -> speak("Dois...")
            1 -> speak("Um! Vai com tudo!")
        }
    }

    /**
     * Spoken audio prompt when rest timer ends with motivating personal trainer cues
     */
    fun speakRestFinished(
        exerciseName: String? = null,
        weightKg: Double? = null,
        reps: Int? = null
    ) {
        if (!isEnabled || !isInitialized) return

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

    /**
     * Spoken voice congratulation on a new Personal Record (PR)
     */
    fun speakPersonalRecord(exerciseName: String, weightKg: Double) {
        if (!isEnabled || !isInitialized) return
        val weightStr = if (weightKg % 1.0 == 0.0) weightKg.toInt().toString() else weightKg.toString()
        val prPhrases = listOf(
            "Sensacional, guerreiro! Novo recorde pessoal destruído no $exerciseName com $weightStr quilos! Você tá imparável!",
            "Que orgulho! Novo recorde pessoal no $exerciseName com $weightStr quilos! Disciplina e força de verdade!",
            "Monstro! Novo recorde batido no $exerciseName com $weightStr quilos! O trabalho duro compensa!"
        )
        speak(prPhrases[Random.nextInt(prPhrases.size)])
    }

    /**
     * Spoken cue when starting a new workout session
     */
    fun speakStartWorkout(workoutTitle: String) {
        if (!isEnabled || !isInitialized) return
        val phrases = listOf(
            "Treino iniciado! Bora pra cima com foco total e postura perfeita hoje.",
            "Bora guerreiro! Treino de $workoutTitle valendo. Sangue nos olhos e boa execução!",
            "Hora de buscar a melhor versão. Treino iniciado, vamos com tudo!"
        )
        speak(phrases[Random.nextInt(phrases.size)])
    }

    /**
     * Spoken cue when finishing a workout session
     */
    fun speakWorkoutCompleted(durationMinutes: Int, totalWeightKg: Double) {
        if (!isEnabled || !isInitialized) return
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

    /**
     * Random motivational power booster
     */
    fun speakMotivation() {
        if (!isEnabled || !isInitialized) return
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
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (_: Exception) {}
    }
}
