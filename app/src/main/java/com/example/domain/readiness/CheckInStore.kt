package com.example.domain.readiness

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Persistência local do check-in sem alterar o schema Room v9. */
class CheckInStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("readiness_checkins", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val type = Types.newParameterizedType(List::class.java, DailyCheckIn::class.java)
    private val adapter = moshi.adapter<List<DailyCheckIn>>(type)

    fun save(checkIn: DailyCheckIn) {
        val day = dayOf(checkIn)
        val updated = history().filterNot { dayOf(it) == day } + checkIn
        prefs.edit().putString(KEY, adapter.toJson(updated.sortedByDescending { it.timestampMillis }.take(MAX_HISTORY))).apply()
    }

    fun history(): List<DailyCheckIn> = try {
        adapter.fromJson(prefs.getString(KEY, "[]") ?: "[]").orEmpty().sortedByDescending { it.timestampMillis }
    } catch (_: Exception) { emptyList() }

    fun today(today: LocalDate = LocalDate.now()): DailyCheckIn? = history().firstOrNull { dayOf(it) == today }

    private fun dayOf(item: DailyCheckIn): LocalDate = Instant.ofEpochMilli(item.timestampMillis).atZone(ZoneId.systemDefault()).toLocalDate()

    private companion object { const val KEY = "history_json"; const val MAX_HISTORY = 180 }
}
