package com.example.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Sound and haptic manager for workout timers, countdowns, PRs and completion fanfares.
 * Synthesizes high-clarity tones and uses ToneGenerator for 100% reliable offline audio on any Android device.
 */
object SoundEffectManager {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 90)
        } catch (_: Exception) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
            } catch (_: Exception) {}
        }
    }

    /**
     * Beep for countdown (5s, 4s, 3s, 2s, 1s)
     */
    fun playCountdownBeep(secondsRemaining: Int = 3) {
        try {
            val tone = if (secondsRemaining == 1) ToneGenerator.TONE_PROP_BEEP2 else ToneGenerator.TONE_PROP_BEEP
            toneGenerator?.startTone(tone, 150)
        } catch (_: Exception) {
            synthesizeSineTone(frequencyHz = 1200 + (6 - secondsRemaining) * 100, durationMs = 120)
        }
    }

    /**
     * Sound alert when rest timer is finished (Double high beep)
     */
    fun playRestTimerFinished() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 350)
        } catch (_: Exception) {
            synthesizeSineTone(frequencyHz = 1760, durationMs = 300)
        }
    }

    /**
     * Sound alert when set is completed (Short pleasant chime)
     */
    fun playSetCompleted() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
        } catch (_: Exception) {
            synthesizeSineTone(frequencyHz = 1400, durationMs = 150)
        }
    }

    /**
     * Triumphant fanfare when a Personal Record (PR) or Medal is unlocked
     */
    fun playCelebrationFanfare() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Triumphant rising arpeggio (C5 -> E5 -> G5 -> C6)
                synthesizeSineTone(523, 100)
                kotlinx.coroutines.delay(100)
                synthesizeSineTone(659, 100)
                kotlinx.coroutines.delay(100)
                synthesizeSineTone(784, 120)
                kotlinx.coroutines.delay(120)
                synthesizeSineTone(1046, 350)
            } catch (_: Exception) {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 500)
            }
        }
    }

    /**
     * Sound when workout is completed successfully
     */
    fun playWorkoutCompleted() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                synthesizeSineTone(600, 120)
                kotlinx.coroutines.delay(120)
                synthesizeSineTone(800, 150)
                kotlinx.coroutines.delay(150)
                synthesizeSineTone(1200, 400)
            } catch (_: Exception) {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 600)
            }
        }
    }

    /**
     * Synthesizes a clean sine wave tone using AudioTrack
     */
    private fun synthesizeSineTone(frequencyHz: Int, durationMs: Int) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val sample = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val angle = 2.0 * Math.PI * i / (sampleRate.toDouble() / frequencyHz)
                // Apply soft envelope attack and decay to prevent clicking
                val envelope = when {
                    i < 200 -> i / 200.0
                    i > numSamples - 300 -> (numSamples - i) / 300.0
                    else -> 1.0
                }
                sample[i] = (sin(angle) * 30000 * envelope).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setBufferSizeInBytes(sample.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sample, 0, sample.size)
            audioTrack.play()
            CoroutineScope(Dispatchers.IO).launch {
                kotlinx.coroutines.delay(durationMs.toLong() + 100)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    /**
     * Trigger tactile haptic feedback
     */
    fun vibrate(context: Context, durationMs: Long = 100) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun vibrateCelebration(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val effect = VibrationEffect.createWaveform(longArrayOf(0, 120, 80, 150, 80, 300), -1)
                vibratorManager?.defaultVibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 120, 80, 150, 80, 300), -1)
            }
        } catch (_: Exception) {}
    }
}
