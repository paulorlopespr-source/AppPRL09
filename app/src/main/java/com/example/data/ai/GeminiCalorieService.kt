package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.CardioSession
import com.example.data.model.FitnessGoal
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

class GeminiCalorieService {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun evaluateWorkoutCaloriesAndPerformance(
        workout: WorkoutSession,
        userProfile: UserProfile,
        recentCardio: CardioSession? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val weight = userProfile.currentWeightKg
        val goalStr = when (userProfile.goal) {
            FitnessGoal.GANHO_PESO_HIPERTROFIA -> "Ganho de Massa Muscular / Hipertrofia"
            FitnessGoal.PERDA_PESO_EMAGRECIMENTO -> "Perda de Peso / Emagrecimento"
            FitnessGoal.DEFINICAO_MUSCULAR -> "Definição Muscular"
            FitnessGoal.CONDICIONAMENTO_GERAL -> "Condicionamento Geral & Saúde"
        }

        val durationMinutes = (workout.durationSeconds / 60).coerceAtLeast(1)
        val volumeTotal = workout.totalWeightLiftedKg
        val rpe = workout.perceivedExertion.coerceIn(1, 10)

        val prompt = """
            Você é um especialista renomado em Fisiologia do Exercício, Bioenergética, Musculação e Nutrição Esportiva.
            Analise detalhadamente os dados do treino realizado pelo atleta e calcule uma estimativa precisa do gasto calórico com base na intensidade reportada (Escala RPE de Percepção de Esforço):

            📋 DADOS DO ATLETA:
            - Peso Corporal: ${weight} kg | Altura: ${userProfile.heightCm} cm | Idade: ${userProfile.age} anos
            - Objetivo Principal: $goalStr
            - Local do Treino: ${workout.location}

            🏋️ DADOS DA SESSÃO DE TREINO:
            - Treino: ${workout.title}
            - Duração Ativa: $durationMinutes minutos (${workout.durationSeconds}s)
            - Intensidade Reportada (RPE - Percepção de Esforço): $rpe / 10
            - Carga Total Levantada (Volume Load): ${volumeTotal.toInt()} kg
            - Exercícios e Séries Concluídas: ${workout.exercisesDoneJson}
            ${if (recentCardio != null) "- Cardio Concomitante: ${recentCardio.type.title}, ${recentCardio.durationMinutes} min, intensidade ${recentCardio.intensity.label}" else ""}

            Por favor, estruture a resposta de forma profissional, clara e motivadora em Português do Brasil com as seguintes seções:
            1. 🔥 **Gasto Calórico Estimado Total (kcal)**: 
               - Detalhe o gasto na musculação (usando equivalentes metabólicos MET ajustados para RPE $rpe/10).
               - Detalhe o Efeito Térmico Residual / Queima Pós-Treino (EPOC - Excess Post-exercise Oxygen Consumption) decorrente do volume (${volumeTotal.toInt()}kg) e da intensidade ($rpe/10).
               - Total combinado em calorias (kcal).
            2. ⚡ **Avaliação de Intensidade & Volume (RPE $rpe/10)**:
               - Análise fisiológica da relação tempo sob tensão vs sobrecarga mecânica para a meta de $goalStr.
            3. 📈 **Estratégia de Progressão & Sobrecarga**:
               - Dica prática para o próximo treino (ajuste de peso, densidade de descanso, cadência).
            4. 🥗 **Recomendação Nutricional & Recuperação**:
               - Janela anabólica/recuperação: quantidade ideal de proteínas (em gramas) e carboidratos no pós-treino para um atleta de ${weight}kg com meta de $goalStr.
            
            Use formatação elegante com títulos, marcadores e emojis.
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackWorkoutCalculation(workout, userProfile, recentCardio)
        }

        try {
            val geminiReq = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )
            val jsonBody = requestAdapter.toJson(geminiReq)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toRequestBody(mediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBodyStr = response.body?.string() ?: ""
                val geminiRes = responseAdapter.fromJson(responseBodyStr)
                val text = geminiRes?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
            fallbackWorkoutCalculation(workout, userProfile, recentCardio)
        } catch (e: Exception) {
            fallbackWorkoutCalculation(workout, userProfile, recentCardio)
        }
    }

    suspend fun evaluateCardioCalories(
        cardio: CardioSession,
        userProfile: UserProfile
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val weight = userProfile.currentWeightKg
        val goalStr = userProfile.goal.label

        val prompt = """
            Você é um especialista em cardiologia esportiva e biomecânica.
            Avalie esta sessão de cardio do atleta e calcule a estimativa de gasto calórico e benefícios:

            Atleta: ${weight}kg, Meta: $goalStr
            Tipo de Cardio: ${cardio.type.title}
            Duração: ${cardio.durationMinutes} minutos
            Intensidade: ${cardio.intensity.label}
            Distância: ${cardio.distanceKm?.let { "${it} km" } ?: "Não informada"}
            Frequência Cardíaca Média: ${cardio.avgHeartRateBpm?.let { "$it bpm" } ?: "Não monitorada"}
            Local: ${cardio.location}

            Forneça:
            1. 🔥 Gasto Calórico Total Estimado (com base no MET da atividade e peso)
            2. 🫀 Zona Cardiorrespiratória & Impacto na meta ($goalStr)
            3. 💡 Dica para a próxima sessão de cardio (cadência/ritmo).
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackCardioCalculation(cardio, userProfile)
        }

        try {
            val geminiReq = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )
            val jsonBody = requestAdapter.toJson(geminiReq)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toRequestBody(mediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBodyStr = response.body?.string() ?: ""
                val geminiRes = responseAdapter.fromJson(responseBodyStr)
                val text = geminiRes?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
            fallbackCardioCalculation(cardio, userProfile)
        } catch (e: Exception) {
            fallbackCardioCalculation(cardio, userProfile)
        }
    }

    suspend fun getPersonalizedAICoachAdvice(
        userProfile: UserProfile,
        completedWorkoutsCount: Int,
        totalVolumeKg: Double,
        totalCardioMinutes: Int
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val prompt = """
            Você é o Coach de IA do aplicativo FitTreino.
            O atleta possui as seguintes métricas:
            - Nome: ${userProfile.name}, Peso Atual: ${userProfile.currentWeightKg}kg, Meta: ${userProfile.targetWeightKg}kg (${userProfile.goal.label})
            - Histórico: $completedWorkoutsCount treinos concluídos, ${totalVolumeKg.toInt()} kg totais levantados, $totalCardioMinutes min de cardio acumulados.
            - Meta semanal: ${userProfile.weeklyGoalDays} dias de treino.

            Escreva uma análise rápida e personalizada sobre a evolução dele, sugerindo ajuste de calorias/dieta, hidratação e estratégia para atingir a meta (${userProfile.goal.label}).
            Use tom motivador, profissional e direto.
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Para sua meta de ${userProfile.goal.label}, continue mantendo regularidade nos treinos de musculação e cardio. Mantenha boa ingestão proteica diária (~2g/kg) e hidratação de ao menos 35ml por kg de peso corporal."
        }

        try {
            val geminiReq = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )
            val jsonBody = requestAdapter.toJson(geminiReq)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toRequestBody(mediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBodyStr = response.body?.string() ?: ""
                val geminiRes = responseAdapter.fromJson(responseBodyStr)
                val text = geminiRes?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
            "Excelente dedicação! Continue focado na sobrecarga progressiva e na consistência semanal para atingir ${userProfile.targetWeightKg}kg."
        } catch (e: Exception) {
            "Excelente dedicação! Continue focado na sobrecarga progressiva e na consistência semanal para atingir ${userProfile.targetWeightKg}kg."
        }
    }

    private fun fallbackWorkoutCalculation(
        workout: WorkoutSession,
        userProfile: UserProfile,
        recentCardio: CardioSession?
    ): String {
        val durationMin = (workout.durationSeconds / 60).coerceAtLeast(1)
        val weight = userProfile.currentWeightKg
        val rpe = workout.perceivedExertion.coerceIn(1, 10)

        // MET dynamic scaling based on RPE (Perceived Exertion):
        // RPE 1-3 (Light): MET 3.8 - 4.5
        // RPE 4-6 (Moderate): MET 5.0 - 6.0
        // RPE 7-8 (Heavy / Hypertrophy): MET 6.8 - 7.5
        // RPE 9-10 (Vigorous / Maximum effort): MET 8.0 - 9.0
        val baseMet = when (rpe) {
            in 1..3 -> 4.0
            in 4..6 -> 5.5
            in 7..8 -> 7.0
            else -> 8.5
        }

        val resistanceCalories = (baseMet * 3.5 * weight / 200.0) * durationMin
        // EPOC (Excess Post-exercise Oxygen Consumption): higher with higher RPE (8% to 20%)
        val epocPercentage = when {
            rpe >= 9 -> 0.20
            rpe >= 7 -> 0.15
            rpe >= 5 -> 0.10
            else -> 0.05
        }
        val epocCalories = resistanceCalories * epocPercentage

        val cardioCal = if (recentCardio != null) {
            val cMet = when (recentCardio.intensity) {
                com.example.data.model.IntensityLevel.LEVE -> recentCardio.type.metLight
                com.example.data.model.IntensityLevel.MODERADA -> recentCardio.type.metModerate
                com.example.data.model.IntensityLevel.INTENSA -> recentCardio.type.metIntense
            }
            (cMet * 3.5 * weight / 200.0) * recentCardio.durationMinutes
        } else 0.0

        val totalEstimated = (resistanceCalories + epocCalories + cardioCal).toInt()
        val proteinRecommendedGrams = (weight * 0.4).toInt().coerceIn(25, 45)

        return """
            🔥 **Gasto Calórico Estimado: ~$totalEstimated kcal**
            • Musculação Ativa (MET $baseMet | RPE $rpe/10): ~${resistanceCalories.toInt()} kcal
            • Efeito EPOC (Queima Metabólica Pós-Treino +${(epocPercentage * 100).toInt()}%): ~${epocCalories.toInt()} kcal
            ${if (recentCardio != null) "• Cardio Associado (${recentCardio.type.title}): ~${cardioCal.toInt()} kcal" else ""}

            ⚡ **Avaliação de Intensidade & Volume:**
            • Volume total movimentado: ${workout.totalWeightLiftedKg.toInt()} kg em $durationMin min de treino.
            • Nível de esforço RPE $rpe/10 compatível com estímulo ideal para ${userProfile.goal.label}.

            📈 **Dica de Sobrecarga Progressiva:**
            • Próxima sessão: tente progredir 1 a 2kg nos exercícios principais ou aumentar 1 repetição mantendo a cadência controlada.

            🥗 **Recomendação Nutricional Pós-Treino:**
            • Ingerir ~$proteinRecommendedGrams g de proteína de rápida absorção e 30-50g de carboidratos nas próximas 2 horas.
            • Hidratação recomendada: mínimo de 500-750ml de água.
        """.trimIndent()
    }

    private fun fallbackCardioCalculation(
        cardio: CardioSession,
        userProfile: UserProfile
    ): String {
        val weight = userProfile.currentWeightKg
        val met = when (cardio.intensity) {
            com.example.data.model.IntensityLevel.LEVE -> cardio.type.metLight
            com.example.data.model.IntensityLevel.MODERADA -> cardio.type.metModerate
            com.example.data.model.IntensityLevel.INTENSA -> cardio.type.metIntense
        }
        val cal = ((met * 3.5 * weight / 200.0) * cardio.durationMinutes).toInt()

        return """
            🔥 **Gasto Calórico Estimado: ~$cal kcal**
            • Modalidade: ${cardio.type.title}
            • Duração: ${cardio.durationMinutes} minutos (${cardio.intensity.label})
            • Intensidade metabólica (MET): $met

            🫀 **Impacto Fisiológico:**
            • Excelente estímulo cardiovascular para queima de gordura e aumento de VO2 máx.
            • Contribui diretamente para a meta de ${userProfile.goal.label}.
        """.trimIndent()
    }
}
