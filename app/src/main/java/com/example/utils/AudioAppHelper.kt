package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Utility to integrate and launch audio/music streaming apps (Spotify, YouTube Music, Deezer, Default Media Player)
 * directly during workouts.
 */
object AudioAppHelper {

    enum class AudioApp(val displayName: String, val packageName: String, val playStoreUrl: String, val iconEmoji: String) {
        SPOTIFY("Spotify", "com.spotify.music", "https://play.google.com/store/apps/details?id=com.spotify.music", "🟢"),
        YOUTUBE_MUSIC("YouTube Music", "com.google.android.apps.youtube.music", "https://play.google.com/store/apps/details?id=com.google.android.apps.youtube.music", "🔴"),
        DEEZER("Deezer", "deezer.android.app", "https://play.google.com/store/apps/details?id=deezer.android.app", "🟣"),
        DEFAULT_MEDIA("Player de Música Padrão", "", "", "🎵")
    }

    fun openAudioApp(context: Context, app: AudioApp) {
        try {
            if (app == AudioApp.DEFAULT_MEDIA) {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_MUSIC)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                    return
                }
            } else {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    return
                } else {
                    // Fallback to web / Play Store
                    val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse(app.playStoreUrl)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(marketIntent)
                    return
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Não foi possível abrir ${app.displayName}", Toast.LENGTH_SHORT).show()
        }
    }
}
