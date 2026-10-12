package com.example.data.local

import com.example.domain.cardio.CardioIntervalPlan
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object CardioIntervalCodec {
    private val adapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(CardioIntervalPlan::class.java)
    fun encode(plan: CardioIntervalPlan): String = adapter.toJson(plan)
    fun decode(json: String?): CardioIntervalPlan? = if (json.isNullOrBlank()) null else runCatching { adapter.fromJson(json) }.getOrNull()
}
