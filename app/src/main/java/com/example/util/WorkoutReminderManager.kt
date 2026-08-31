package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.data.model.ReminderTone
import com.example.data.model.WorkoutReminderSettings
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Calendar

object WorkoutReminderManager {

    const val CHANNEL_ID = "workout_daily_reminders_channel"
    const val CHANNEL_NAME = "Lembretes Diários de Treino"
    const val NOTIFICATION_ID = 2026
    const val PREFS_NAME = "workout_reminders_prefs"
    const val KEY_SETTINGS = "reminder_settings_json"
    const val ACTION_WORKOUT_REMINDER = "com.example.ACTION_WORKOUT_REMINDER"

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(WorkoutReminderSettings::class.java)

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações para alertar sobre horários de treino e metas diárias"
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun loadSettings(context: Context): WorkoutReminderSettings {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SETTINGS, null)
        return if (!json.isNullOrBlank()) {
            try {
                adapter.fromJson(json) ?: WorkoutReminderSettings()
            } catch (_: Exception) {
                WorkoutReminderSettings()
            }
        } else {
            WorkoutReminderSettings()
        }
    }

    fun saveSettings(context: Context, settings: WorkoutReminderSettings) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = adapter.toJson(settings)
        prefs.edit().putString(KEY_SETTINGS, json).apply()

        createNotificationChannel(context)

        if (settings.isEnabled) {
            scheduleNextAlarm(context, settings)
        } else {
            cancelAlarm(context)
        }
    }

    fun scheduleNextAlarm(context: Context, settings: WorkoutReminderSettings) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, WorkoutReminderReceiver::class.java).apply {
            action = ACTION_WORKOUT_REMINDER
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 1001, intent, flags)

        // Find the next active day and trigger time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, settings.hour)
            set(Calendar.MINUTE, settings.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val now = System.currentTimeMillis()
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Advance to a day of week enabled by the user (Calendar.DAY_OF_WEEK: 1=Sun, 2=Mon... 7=Sat)
        // Convert to ISO 1=Mon .. 7=Sun
        var attempts = 0
        while (attempts < 7) {
            val calDow = calendar.get(Calendar.DAY_OF_WEEK)
            val isoDow = if (calDow == Calendar.SUNDAY) 7 else calDow - 1
            if (settings.daysOfWeek.contains(isoDow)) {
                break
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            attempts++
        }

        val triggerAtMillis = calendar.timeInMillis

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Fallback for devices restricting exact alarms
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, WorkoutReminderReceiver::class.java).apply {
            action = ACTION_WORKOUT_REMINDER
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 1001, intent, flags)
        alarmManager.cancel(pendingIntent)
    }

    fun showNotification(context: Context, settings: WorkoutReminderSettings, isTest: Boolean = false) {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val contentPendingIntent = PendingIntent.getActivity(context, 2001, openAppIntent, pendingIntentFlags)

        val title = if (isTest) {
            "⚡ Teste de Lembrete: FitPr09"
        } else {
            "${settings.tone.emoji} Hora do Treino!"
        }

        val body = if (settings.customMessage.isNotBlank()) {
            settings.customMessage
        } else {
            settings.tone.sampleText
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(if (isTest) 2027 else NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
