package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CardioRoutinePlan(
    val id: String,
    val tipo: String, // "HIIT" or "LISS"
    val nivel: String, // "Iniciante", "Intermediário", "Avançado"
    val duracaoMinutos: Int,
    val estrutura: String,
    val rodadas: Int,
    val frequenciaSemanal: Int,
    val exercicios: List<String>,
    val observacaoSeguranca: String,
    val cardioType: CardioType,
    val intensity: IntensityLevel
)

@JsonClass(generateAdapter = true)
data class PresetGoalRecommendation(
    val id: String,
    val objetivo: String, // "Hipertrofia", "Perda de Gordura", "Condicionamento", "Força"
    val titulo: String,
    val descricao: String,
    val metrica: String,
    val valorInicialEsperado: String,
    val valorAlvo: String,
    val prazoSemanas: Int,
    val frequenciaAvaliacao: String,
    val targetWeightKg: Double? = null,
    val targetReps: Int? = null,
    val exerciseName: String? = null
)
