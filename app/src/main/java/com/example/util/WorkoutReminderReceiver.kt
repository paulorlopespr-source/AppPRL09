package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class WorkoutReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val settings = WorkoutReminderManager.loadSettings(context)
        if (!settings.isEnabled) return

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule alarm on phone reboot
            WorkoutReminderManager.scheduleNextAlarm(context, settings)
            return
        }

        // Check if current day of week is enabled
        val calendar = Calendar.getInstance()
        val calDow = calendar.get(Calendar.DAY_OF_WEEK)
        val isoDow = if (calDow == Calendar.SUNDAY) 7 else calDow - 1

        if (settings.daysOfWeek.contains(isoDow)) {
            WorkoutReminderManager.showNotification(context, settings, isTest = false)
        }

        // Reschedule for next occurrence
        WorkoutReminderManager.scheduleNextAlarm(context, settings)
    }
}
