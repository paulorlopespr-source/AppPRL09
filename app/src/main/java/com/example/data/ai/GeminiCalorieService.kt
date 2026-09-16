package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.AICoachMessage
import com.example.data.model.AICoachSender
import com.example.data.model.AIWorkoutPlanResult
import com.example.data.model.CardioSession
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.IntensityLevel
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.FitnessGoal
import com.example.data.model.MealAnalysisResult
import com.example.data.model.MealLog
import com.example.data.model.UserProfile
import com.example.data.model.VolumeNutritionEvaluationResult
import com.example.data.model.ExerciseExecutionGuideResult
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
import org.json.JSONObject
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

    /**
     * Process a meal description (e.g. "3 ovos mexidos, 2 fatias de pão integral e 1 xícara de café com leite")
     * and calculate estimated calories, proteins, carbohydrates, fats, and dietary advice with Gemini API.
     */
    suspend fun analyzeMealDescription(
        mealText: String,
        mealType: String,
        userProfile: UserProfile
    ): MealAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val goalStr = userProfile.goal.label

        val prompt = """
            Você é um nutricionista esportivo de alto rendimento.
            Analise a seguinte refeição informada pelo usuário e forneça uma estimativa detalhada e precisa de calorias e macronutrientes:
            
            Informações do Usuário:
            - Peso: ${userProfile.currentWeightKg}kg | Altura: ${userProfile.heightCm}cm | Meta: $goalStr
            - Tipo de Refeição: $mealType
            - Descrição da Refeição: "$mealText"
            
            Retorne EXCLUSIVAMENTE um objeto JSON válido (sem tags markdown, sem crases ```json) com a seguinte estrutura:
            {
                "title": "Nome resumido da refeição",
                "estimatedCalories": 450,
                "proteinGrams": 32.5,
                "carbsGrams": 40.0,
                "fatsGrams": 14.0,
                "fiberGrams": 5.0,
                "healthRating": "Excelente",
                "summary": "Resumo nutricional de 2 a 3 frases sobre o impacto dessa refeição na meta de $goalStr.",
                "suggestions": [
                    "Dica 1 para otimizar os macros",
                    "Dica 2 sobre timing ou hidratação"
                ]
            }
        """.trimIndent()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
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
                    val rawText = geminiRes?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!rawText.isNullOrBlank()) {
                        // Extract JSON substring
                        val cleanJson = rawText.substringAfter("{").substringBeforeLast("}")
                        val parsed = JSONObject("{$cleanJson}")
                        val suggestionsList = mutableListOf<String>()
                        val sugArray = parsed.optJSONArray("suggestions")
                        if (sugArray != null) {
                            for (i in 0 until sugArray.length()) {
                                suggestionsList.add(sugArray.getString(i))
                            }
                        }
                        return@withContext MealAnalysisResult(
                            title = parsed.optString("title", mealType),
                            estimatedCalories = parsed.optInt("estimatedCalories", 400),
                            proteinGrams = parsed.optDouble("proteinGrams", 25.0),
                            carbsGrams = parsed.optDouble("carbsGrams", 35.0),
                            fatsGrams = parsed.optDouble("fatsGrams", 12.0),
                            fiberGrams = parsed.optDouble("fiberGrams", 4.0),
                            healthRating = parsed.optString("healthRating", "Equilibrado"),
                            summary = parsed.optString("summary", "Refeição nutritiva para suporte de energia e recuperação muscular."),
                            suggestions = if (suggestionsList.isNotEmpty()) suggestionsList else listOf("Beba 400ml de água para auxiliar a digestão.")
                        )
                    }
                }
            } catch (_: Exception) {
                // Fallback to offline rule-based calculation
            }
        }
        fallbackMealCalculation(mealText, mealType, userProfile)
    }

    private fun fallbackMealCalculation(
        mealText: String,
        mealType: String,
        userProfile: UserProfile
    ): MealAnalysisResult {
        val lower = mealText.lowercase()
        var cal = 350
        var p = 20.0
        var c = 35.0
        var f = 10.0
        var fiber = 3.0

        // Heuristic detection of common foods
        if (lower.contains("ovo") || lower.contains("ovos")) {
            val count = if (lower.contains("3") || lower.contains("três")) 3 else if (lower.contains("4") || lower.contains("quatro")) 4 else 2
            cal += count * 80
            p += count * 6.5
            f += count * 5.0
        }
        if (lower.contains("frango") || lower.contains("peito de frango")) {
            cal += 180
            p += 32.0
            f += 3.5
        }
        if (lower.contains("carne") || lower.contains("patinho") || lower.contains("bife")) {
            cal += 230
            p += 30.0
            f += 10.0
        }
        if (lower.contains("arroz")) {
            cal += 130
            c += 28.0
            p += 2.5
        }
        if (lower.contains("feijão") || lower.contains("feijao")) {
            cal += 100
            c += 18.0
            p += 6.0
            fiber += 5.0
        }
        if (lower.contains("whey") || lower.contains("proteina") || lower.contains("shake")) {
            cal += 130
            p += 24.0
            c += 3.0
            f += 1.5
        }
        if (lower.contains("aveia")) {
            cal += 120
            c += 20.0
            p += 4.5
            fiber += 3.0
        }
        if (lower.contains("banana") || lower.contains("fruta") || lower.contains("maçã") || lower.contains("maca")) {
            cal += 90
            c += 23.0
            fiber += 2.5
        }
        if (lower.contains("pão") || lower.contains("pao") || lower.contains("torrada") || lower.contains("tapioca")) {
            cal += 140
            c += 26.0
            p += 4.0
        }
        if (lower.contains("queijo") || lower.contains("requeijao") || lower.contains("manteiga")) {
            cal += 110
            p += 7.0
            f += 9.0
        }
        if (lower.contains("salada") || lower.contains("legumes") || lower.contains("vegetais")) {
            cal += 35
            fiber += 4.0
            c += 6.0
        }

        val rating = if (p >= 25.0) "Excelente" else "Equilibrado"
        val summary = "Refeição estimada em aproximadamente $cal kcal com ${p.toInt()}g de proteínas e ${c.toInt()}g de carboidratos, adequada para a meta de ${userProfile.goal.label}."
        val suggestions = listOf(
            "Mantenha uma distribuição uniforme de proteínas ao longo do dia (~25g a 40g por refeição).",
            "Acompanhe a ingestão hídrica para melhor síntese proteica e absorção de micronutrientes."
        )

        return MealAnalysisResult(
            title = mealType,
            estimatedCalories = cal,
            proteinGrams = p,
            carbsGrams = c,
            fatsGrams = f,
            fiberGrams = fiber,
            healthRating = rating,
            summary = summary,
            suggestions = suggestions
        )
    }

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
            Você é o Coach de IA do aplicativo FitPr09.
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

    /**
     * Generate custom workout routine tailored to user request, equipment and fitness goal using Gemini API
     */
    suspend fun generateAIWorkoutRoutine(
        prompt: String,
        userProfile: UserProfile
    ): AIWorkoutPlanResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val goalStr = userProfile.goal.label

        val systemPrompt = """
            Você é um mestre em Fisiologia do Exercício, Biomecânica e Preparação Física de Alto Rendimento.
            Crie uma ficha de treino personalizada e altamente eficiente com base na solicitação do atleta.

            Informações do Atleta:
            - Peso: ${userProfile.currentWeightKg} kg | Meta: $goalStr | Local habitual: ${userProfile.defaultGymLocation}
            - Solicitação do Treino: "$prompt"

            Retorne EXCLUSIVAMENTE um objeto JSON válido (sem tags markdown, sem crases ```json) com a seguinte estrutura:
            {
                "title": "Nome do Treino (ex: Costas & Bíceps Hipertrofia Intensa)",
                "subtitle": "Subtítulo curto (ex: Foco em dorsal e pico de contração)",
                "category": "HIPERTROFIA",
                "durationMinutes": 45,
                "description": "Descrição fisiológica de 2 frases sobre os estímulos propostos.",
                "aiBiomechanicalTips": [
                    "Dica 1 sobre cadência e ativação escapular",
                    "Dica 2 sobre respiração e tempo sob tensão"
                ],
                "exercises": [
                    {
                        "exerciseId": 1,
                        "exerciseName": "Puxada Frontal na Polia",
                        "muscleGroup": "Costas",
                        "setsCount": 4,
                        "reps": 10,
                        "weightKg": 50.0,
                        "targetRestSeconds": 60,
                        "notes": "Puxe com os cotovelos apontando para o chão e pause 1s na contração máxima."
                    }
                ]
            }
        """.trimIndent()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiReq = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
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
                    val rawText = geminiRes?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!rawText.isNullOrBlank()) {
                        val cleanJson = rawText.substringAfter("{").substringBeforeLast("}")
                        val parsed = JSONObject("{$cleanJson}")

                        val title = parsed.optString("title", "Treino Gerado por IA")
                        val subtitle = parsed.optString("subtitle", "Personalizado para sua meta")
                        val categoryStr = parsed.optString("category", "HIPERTROFIA")
                        val category = try {
                            WorkoutCategory.valueOf(categoryStr)
                        } catch (_: Exception) {
                            WorkoutCategory.PERSONALIZADO
                        }
                        val duration = parsed.optInt("durationMinutes", 45)
                        val description = parsed.optString("description", "Treino montado pela inteligência artificial com base no seu objetivo.")

                        val tipsList = mutableListOf<String>()
                        val tipsArr = parsed.optJSONArray("aiBiomechanicalTips")
                        if (tipsArr != null) {
                            for (i in 0 until tipsArr.length()) {
                                tipsList.add(tipsArr.getString(i))
                            }
                        }

                        val exercisesList = mutableListOf<WorkoutExercisePlan>()
                        val exercisesArr = parsed.optJSONArray("exercises")
                        if (exercisesArr != null) {
                            for (i in 0 until exercisesArr.length()) {
                                val exObj = exercisesArr.getJSONObject(i)
                                val exName = exObj.optString("exerciseName", "Exercício ${i + 1}")
                                val mGroup = exObj.optString("muscleGroup", "Geral")
                                val sCount = exObj.optInt("setsCount", 4).coerceIn(2, 6)
                                val reps = exObj.optInt("reps", 10).coerceIn(4, 25)
                                val weight = exObj.optDouble("weightKg", 20.0)
                                val rest = exObj.optInt("targetRestSeconds", 60)
                                val notes = exObj.optString("notes", "")

                                val sets = (1..sCount).map { sNum ->
                                    ExerciseSetEntry(
                                        setNumber = sNum,
                                        weightKg = weight,
                                        reps = reps,
                                        isCompleted = false,
                                        restSeconds = rest
                                    )
                                }

                                exercisesList.add(
                                    WorkoutExercisePlan(
                                        exerciseId = (i + 100).toLong(),
                                        exerciseName = exName,
                                        muscleGroup = mGroup,
                                        sets = sets,
                                        targetRestSeconds = rest,
                                        notes = notes
                                    )
                                )
                            }
                        }

                        if (exercisesList.isNotEmpty()) {
                            return@withContext AIWorkoutPlanResult(
                                title = title,
                                subtitle = subtitle,
                                category = category,
                                durationMinutes = duration,
                                description = description,
                                exercises = exercisesList,
                                aiBiomechanicalTips = if (tipsList.isNotEmpty()) tipsList else listOf("Controle a fase excêntrica por 2 segundos.")
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                // Fallback to offline rule-based workout builder
            }
        }

        fallbackAIWorkoutGeneration(prompt, userProfile)
    }

    /**
     * Interactive AI Coach Assistant conversation
     */
    suspend fun askAICoach(
        userQuery: String,
        userProfile: UserProfile,
        contextSummary: String
    ): AICoachMessage = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val goalStr = userProfile.goal.label

        val prompt = """
            Você é o Coach IA do FitPr09, um especialista de elite em musculação, biomecânica, nutrição esportiva e hipertrofia.
            Responda de forma altamente prática, precisa e motivadora à dúvida do atleta:

            Perfil do Atleta:
            - Nome: ${userProfile.name} | Peso: ${userProfile.currentWeightKg}kg | Altura: ${userProfile.heightCm}cm | Meta: $goalStr
            - Contexto atual: $contextSummary
            - Pergunta do Atleta: "$userQuery"

            Retorne EXCLUSIVAMENTE um objeto JSON válido (sem tags markdown, sem crases ```json) com a seguinte estrutura:
            {
                "answer": "Texto completo e detalhado da resposta em Português do Brasil com explicações claras.",
                "keyPoints": [
                    "Ponto chave 1 prático para aplicar hoje",
                    "Ponto chave 2 sobre segurança ou execução"
                ],
                "suggestedFollowUps": [
                    "Pergunta sugerida de continuação 1",
                    "Pergunta sugerida de continuação 2"
                ]
            }
        """.trimIndent()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
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
                    val rawText = geminiRes?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!rawText.isNullOrBlank()) {
                        val cleanJson = rawText.substringAfter("{").substringBeforeLast("}")
                        val parsed = JSONObject("{$cleanJson}")

                        val answer = parsed.optString("answer", "")
                        val keyPoints = mutableListOf<String>()
                        val kpArr = parsed.optJSONArray("keyPoints")
                        if (kpArr != null) {
                            for (i in 0 until kpArr.length()) {
                                keyPoints.add(kpArr.getString(i))
                            }
                        }
                        val followUps = mutableListOf<String>()
                        val fuArr = parsed.optJSONArray("suggestedFollowUps")
                        if (fuArr != null) {
                            for (i in 0 until fuArr.length()) {
                                followUps.add(fuArr.getString(i))
                            }
                        }

                        if (answer.isNotBlank()) {
                            return@withContext AICoachMessage(
                                sender = AICoachSender.COACH,
                                text = answer,
                                keyPoints = keyPoints,
                                suggestedFollowUps = followUps
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                // Fallback to heuristic coach answer
            }
        }

        fallbackAICoachAnswer(userQuery, userProfile)
    }

    private fun fallbackAIWorkoutGeneration(prompt: String, userProfile: UserProfile): AIWorkoutPlanResult {
        val lower = prompt.lowercase()
        return when {
            lower.contains("costa") || lower.contains("bicep") || lower.contains("bíceps") || lower.contains("puxar") -> {
                AIWorkoutPlanResult(
                    title = "Costas & Bíceps Hipertrofia IA",
                    subtitle = "Foco em largura dorsal e densidade de bíceps",
                    category = WorkoutCategory.HIPERTROFIA,
                    durationMinutes = 45,
                    description = "Sequência otimizada com ênfase em sobrecarga progressiva e contração de pico para dorsais e flexores do cotovelo.",
                    aiBiomechanicalTips = listOf(
                        "Mantenha a escápula em depressão antes de iniciar a tração.",
                        "Concentre a força nos cotovelos para diminuir o recrutamento excessivo do antebraço."
                    ),
                    exercises = listOf(
                        createExercisePlan("Puxada Frontal na Polia", "Costas", 4, 10, 45.0, 60, "Tronco estável e 1s de isometria embaixo."),
                        createExercisePlan("Remada Curvada com Barra", "Costas", 4, 8, 50.0, 75, "Mantenha a coluna neutra e puxe na direção do umbigo."),
                        createExercisePlan("Remada Baixa no Triângulo", "Costas", 3, 12, 40.0, 60, "Alongue a dorsal na fase excêntrica."),
                        createExercisePlan("Rosca Direta com Barra W", "Bíceps", 3, 10, 20.0, 60, "Cotovelos fixos junto ao tronco."),
                        createExercisePlan("Rosca Martelo com Halteres", "Bíceps", 3, 12, 12.0, 45, "Foco no braquial e braquiorradial.")
                    )
                )
            }
            lower.contains("perna") || lower.contains("quadriceps") || lower.contains("gluteo") || lower.contains("glúteo") -> {
                AIWorkoutPlanResult(
                    title = "Inferiores & Quadríceps Turbo IA",
                    subtitle = "Foco em cadeia anterior e glúteos",
                    category = WorkoutCategory.HIPERTROFIA,
                    durationMinutes = 50,
                    description = "Combinação de movimentos compostos de alta demanda biomecânica e exercícios de isolamento com tempo sob tensão elevado.",
                    aiBiomechanicalTips = listOf(
                        "Desça controlando a fase excêntrica em 3 segundos no agachamento.",
                        "Mantenha os joelhos alinhados com a ponta dos pés durante toda a trajetória."
                    ),
                    exercises = listOf(
                        createExercisePlan("Agachamento Livre com Barra", "Quadríceps (Pernas)", 4, 8, 60.0, 90, "Base firme e descida abaixo de 90 graus."),
                        createExercisePlan("Leg Press 45°", "Quadríceps (Pernas)", 4, 10, 140.0, 75, "Pés na largura dos ombros sem travar os joelhos no topo."),
                        createExercisePlan("Cadeira Extensora", "Quadríceps (Pernas)", 3, 12, 45.0, 60, "Pico de contração de 1s no topo."),
                        createExercisePlan("Mesa Flexora", "Posterior & Glúteos", 4, 10, 35.0, 60, "Controle a volta sem deixar o peso bater."),
                        createExercisePlan("Panturrilha em Pé na Máquina", "Panturrilhas", 4, 15, 50.0, 45, "Amplitude máxima na flexão plantar.")
                    )
                )
            }
            lower.contains("ombro") || lower.contains("deltoide") || lower.contains("deltóide") -> {
                AIWorkoutPlanResult(
                    title = "Ombros & Abdômen 3D IA",
                    subtitle = "Construção de deltoides densos e core blindado",
                    category = WorkoutCategory.HIPERTROFIA,
                    durationMinutes = 40,
                    description = "Foco em todas as 3 cabeças do deltoide (lateral, anterior e posterior) com estímulos mecânicos variados.",
                    aiBiomechanicalTips = listOf(
                        "Na elevação lateral, eleve os halteres em plano escapular (~30 graus à frente do tronco).",
                        "Evite elevar os trapézios na fase concêntrica."
                    ),
                    exercises = listOf(
                        createExercisePlan("Desenvolvimento com Halteres", "Ombros", 4, 8, 18.0, 75, "Cotovelos a 75 graus do tronco."),
                        createExercisePlan("Elevação Lateral com Halteres", "Ombros", 4, 12, 10.0, 60, "Movimento suave sem impulso corporal."),
                        createExercisePlan("Crucifixo Invertido na Máquina", "Ombros", 3, 12, 35.0, 60, "Ativação do deltoide posterior."),
                        createExercisePlan("Abdominal Crunch na Polia", "Abdômen", 4, 15, 30.0, 45, "Curvatura da coluna torácica sem puxar o pescoço.")
                    )
                )
            }
            lower.contains("casa") || lower.contains("sem peso") || lower.contains("viagem") || lower.contains("calistenia") -> {
                AIWorkoutPlanResult(
                    title = "Full Body Funcional Sem Peso IA",
                    subtitle = "Treino de alta intensidade com peso do corpo",
                    category = WorkoutCategory.FULLBODY,
                    durationMinutes = 35,
                    description = "Treino completo de calistenia metabólica para estimular grandes grupos musculares sem necessidade de equipamentos.",
                    aiBiomechanicalTips = listOf(
                        "Mantenha o core sempre contraído em prancha.",
                        "Use cadência lenta para compensar a ausência de sobrecarga externa."
                    ),
                    exercises = listOf(
                        createExercisePlan("Flexão de Braços no Solo", "Peito", 4, 15, 0.0, 45, "Descida controlada até quase tocar o peito no chão."),
                        createExercisePlan("Agachamento Livre Bodyweight", "Quadríceps (Pernas)", 4, 20, 0.0, 45, "Agache profundo e suba com explosão."),
                        createExercisePlan("Afundo Alternado", "Quadríceps (Pernas)", 3, 12, 0.0, 45, "Passe largo e tronco vertical."),
                        createExercisePlan("Prancha Isométrica", "Abdômen", 3, 45, 0.0, 45, "Alinhamento perfeito de ombros, quadril e calcanhares."),
                        createExercisePlan("Polichinelo Metabólico", "Condicionamento", 3, 30, 0.0, 30, "Ritmo contínuo e respiração ritmada.")
                    )
                )
            }
            else -> {
                // Default: Peito, Ombros e Tríceps (Push Day)
                AIWorkoutPlanResult(
                    title = "Peito, Ombros & Tríceps Hipertrofia IA",
                    subtitle = "Push Day de alta eficiência biomecânica",
                    category = WorkoutCategory.HIPERTROFIA,
                    durationMinutes = 45,
                    description = "Foco na sobrecarga mecânica dos músculos de empurrar com amplitude máxima e ativação das fibras do peitoral maior.",
                    aiBiomechanicalTips = listOf(
                        "Mantenha as escápulas aduzidas e fixadas no banco durante o supino.",
                        "Evite estender totalmente o cotovelo no topo para manter a tensão constante no músculo-alvo."
                    ),
                    exercises = listOf(
                        createExercisePlan("Supino Reto com Barra", "Peito", 4, 8, 60.0, 90, "Toque suave no peito e subida explosiva."),
                        createExercisePlan("Supino Inclinado com Halteres", "Peito", 4, 10, 22.0, 75, "Banco a 30 graus para foco no feixe clavicular."),
                        createExercisePlan("Crucifixo no Crossover", "Peito", 3, 12, 15.0, 60, "Abra os braços sentindo o alongamento do peito."),
                        createExercisePlan("Desenvolvimento Militar", "Ombros", 3, 10, 30.0, 60, "Barra passando rente ao rosto."),
                        createExercisePlan("Tríceps na Polia com Corda", "Tríceps", 4, 12, 25.0, 45, "Abra a corda no final da extensão.")
                    )
                )
            }
        }
    }

    private fun createExercisePlan(
        name: String,
        group: String,
        setsCount: Int,
        reps: Int,
        weight: Double,
        rest: Int,
        notes: String
    ): WorkoutExercisePlan {
        val sets = (1..setsCount).map {
            ExerciseSetEntry(
                setNumber = it,
                weightKg = weight,
                reps = reps,
                isCompleted = false,
                restSeconds = rest
            )
        }
        return WorkoutExercisePlan(
            exerciseId = (100..999).random().toLong(),
            exerciseName = name,
            muscleGroup = group,
            sets = sets,
            targetRestSeconds = rest,
            notes = notes
        )
    }

    private fun fallbackAICoachAnswer(query: String, userProfile: UserProfile): AICoachMessage {
        val lower = query.lowercase()
        return when {
            lower.contains("supino") || lower.contains("peito") || lower.contains("peso") || lower.contains("platô") || lower.contains("plato") -> {
                AICoachMessage(
                    sender = AICoachSender.COACH,
                    text = "Para quebrar o platô de carga no supino ou exercícios básicos, a estratégia padrão de ouro é a periodização ondulatória: reduza a carga em 10% por 1 semana (deload regenerativo), aumente o descanso entre séries para 2 a 3 minutos nas séries pesadas e foque na estabilidade das escápulas e na contração dos glúteos contra o banco.",
                    keyPoints = listOf(
                        "Aumente o tempo de descanso entre séries pesadas para 2.5 a 3 minutos.",
                        "Faça 1 série de aquecimento específico com 50% e 70% da carga de trabalho antes da série principal.",
                        "Trabalhe o tríceps e deltoide anterior com sobrecarga para fortalecer o bloqueio final do movimento."
                    ),
                    suggestedFollowUps = listOf(
                        "Qual a melhor cadência para hipertrofia?",
                        "Devo treinar até a falha em todas as séries?",
                        "Como aquecer corretamente os manguitos rotadores?"
                    )
                )
            }
            lower.contains("pre-treino") || lower.contains("pré-treino") || lower.contains("comer") || lower.contains("dieta") || lower.contains("alimenta") -> {
                AICoachMessage(
                    sender = AICoachSender.COACH,
                    text = "Para sua meta de ${userProfile.goal.label} (peso atual: ${userProfile.currentWeightKg}kg), a refeição pré-treino ideal deve conter entre 30g e 50g de carboidratos de média absorção (ex: aveia, banana ou pão integral) associados a 20g a 25g de proteína magra cerca de 60 a 90 minutos antes da sessão, acompanhada de 400ml de água para garantir hidratação celular.",
                    keyPoints = listOf(
                        "Evite refeições ricas em gorduras logo antes de treinar, pois retardam o esvaziamento gástrico.",
                        "Se treinar logo ao acordar, 1 banana com mel e 1 dose de whey protein já fornecem glicogênio rápido.",
                        "Consuma 500ml de água durante o treino para manter o volume plasmático e a força muscular."
                    ),
                    suggestedFollowUps = listOf(
                        "Quanto de creatina devo tomar por dia?",
                        "Qual a quantidade ideal de proteína por kg de peso corporal?",
                        "O que consumir no pós-treino imediato?"
                    )
                )
            }
            lower.contains("dor") || lower.contains("lesao") || lower.contains("lesão") || lower.contains("ombro") || lower.contains("joelho") -> {
                AICoachMessage(
                    sender = AICoachSender.COACH,
                    text = "Dores articulares costumam decorrer de falta de aquecimento, restrição de mobilidade ou trajetória mecânica inadequada. Nunca treine com dor aguda penetrante. Se sentir desconforto no ombro, substitua o supino reto com barra por supino com halteres com pegada neutra e realize rotação externa de ombro no cabo antes de iniciar.",
                    keyPoints = listOf(
                        "Substitua barras retas por halteres para permitir uma trajetória articular mais anatômica e livre.",
                        "Reduza a amplitude temporariamente para a zona em que não haja atrito ou dor.",
                        "Consulte um fisioterapeuta caso a dor persista por mais de 48 horas em repouso."
                    ),
                    suggestedFollowUps = listOf(
                        "Exercícios de mobilidade para ombros e tornozelos",
                        "Como fortalecer o manguito rotador?",
                        "Qual a diferença entre dor muscular tardia e lesão?"
                    )
                )
            }
            else -> {
                AICoachMessage(
                    sender = AICoachSender.COACH,
                    text = "Excelente pergunta! Para atingir sua meta de ${userProfile.goal.label}, os pilares fundamentais são: sobrecarga progressiva nos treinos (adicionando repetições ou peso a cada 1-2 semanas), consistência com seus ${userProfile.weeklyGoalDays} dias semanais planejados e sono de qualidade (7 a 8 horas) para recuperação do sistema nervoso central e síntese proteica.",
                    keyPoints = listOf(
                        "Anote suas cargas em cada treino para garantir que você está progredindo.",
                        "Mantenha 1 a 2 repetições na reserva (RIR 1-2) na maioria das séries para evitar fadiga excessiva.",
                        "Beba pelo menos 35ml de água por kg de peso corporal ao longo do dia."
                    ),
                    suggestedFollowUps = listOf(
                        "Como montar uma divisão de treino eficiente?",
                        "Cardio antes ou depois da musculação?",
                        "Como calcular meu gasto calórico total diário?"
                    )
                )
            }
        }
    }

    /**
     * Evaluates the athlete's nutrition and recommends dietary adjustments
     * based on the exact training volume history (sets, reps, kg lifted, cardio sessions).
     */
    suspend fun evaluateNutritionFromVolumeHistory(
        userProfile: UserProfile,
        workoutSessions: List<WorkoutSession>,
        cardioSessions: List<CardioSession>
    ): VolumeNutritionEvaluationResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val completedSessions = workoutSessions.filter { it.status == com.example.data.model.SessionStatus.COMPLETED }
        val totalVolumeKg = completedSessions.sumOf { it.totalWeightLiftedKg }
        val totalWorkoutMinutes = completedSessions.sumOf { it.durationSeconds / 60 }
        val totalWorkoutsCount = completedSessions.size
        val avgVolumePerSession = if (totalWorkoutsCount > 0) totalVolumeKg / totalWorkoutsCount else 0.0
        val totalCardioMinutes = cardioSessions.sumOf { it.durationMinutes }
        val totalCardioCalories = cardioSessions.sumOf { it.caloriesBurned }

        val weight = userProfile.currentWeightKg
        val height = userProfile.heightCm
        val goalStr = userProfile.goal.label

        val prompt = """
            Você é um nutricionista esportivo de elite e fisiologista do exercício.
            Avalie o histórico de volume de treino do atleta e gere uma prescrição nutricional e ajustes dietéticos precisos:

            DADOS BIOMÉTRICOS DO ATLETA:
            - Peso Atual: ${weight} kg | Altura: ${height} cm
            - Objetivo Principal: $goalStr
            - Frequência Planejada: ${userProfile.weeklyGoalDays} dias/semana

            HISTÓRICO REAL DE TREINO REGISTRADO:
            - Sessões Concluídas no Histórico: $totalWorkoutsCount treinos de musculação
            - Tonelagem / Volume Total Acumulado: ${String.format(java.util.Locale.US, "%.0f", totalVolumeKg)} kg
            - Média de Volume por Treino: ${String.format(java.util.Locale.US, "%.0f", avgVolumePerSession)} kg/treino
            - Tempo Total de Musculação: $totalWorkoutMinutes minutos
            - Cardio Registrado: $totalCardioMinutes minutos ($totalCardioCalories kcal gastas)

            Retorne EXCLUSIVAMENTE um objeto JSON válido (sem crases ```json, sem markdown) com a seguinte estrutura:
            {
                "trainingVolumeSummary": "Resumo do volume (ex: 48.500 kg acumulados em 6 treinos)",
                "recommendedDailyCalories": 2750,
                "calorieAdjustmentReason": "Explicação detalhada do superávit ou déficit adequado a este volume de treino",
                "proteinGrams": 165.0,
                "proteinPerKg": 2.1,
                "carbsGrams": 340.0,
                "carbsPerKg": 4.3,
                "fatsGrams": 72.0,
                "fatsPerKg": 0.9,
                "preWorkoutNutrition": "O que comer 60-90min antes do treino para sustentar esse volume",
                "postWorkoutNutrition": "O que consumir logo após o treino para máxima síntese proteica e reposição de glicogênio",
                "hydrationLitres": 3.6,
                "dietAdjustments": [
                    "Ajuste específico 1 baseado no volume de carga",
                    "Ajuste específico 2 para dias pesados vs dias de descanso",
                    "Ajuste específico 3 sobre ingestão de eletrólitos/micronutrientes",
                    "Ajuste específico 4 de suplementação recomendada (creatina, etc.)"
                ],
                "volumeInsight": "Análise fisiológica conectando o volume acumulado de carga à necessidade energética e hipertrofia."
            }
        """.trimIndent()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
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
                    val rawText = geminiRes?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!rawText.isNullOrBlank()) {
                        val cleanJson = rawText.substringAfter("{").substringBeforeLast("}")
                        val parsed = JSONObject("{$cleanJson}")
                        val adjustments = mutableListOf<String>()
                        val adjArray = parsed.optJSONArray("dietAdjustments")
                        if (adjArray != null) {
                            for (i in 0 until adjArray.length()) {
                                adjustments.add(adjArray.getString(i))
                            }
                        }
                        return@withContext VolumeNutritionEvaluationResult(
                            title = "Avaliação Nutricional Baseada no Volume",
                            trainingVolumeSummary = parsed.optString(
                                "trainingVolumeSummary",
                                "${String.format(java.util.Locale.US, "%.0f", totalVolumeKg)} kg acumulados"
                            ),
                            recommendedDailyCalories = parsed.optInt("recommendedDailyCalories", 2700),
                            calorieAdjustmentReason = parsed.optString(
                                "calorieAdjustmentReason",
                                "Ajuste calórico calculado para suportar a demanda energética do seu volume de treino."
                            ),
                            proteinGrams = parsed.optDouble("proteinGrams", weight * 2.0),
                            proteinPerKg = parsed.optDouble("proteinPerKg", 2.0),
                            carbsGrams = parsed.optDouble("carbsGrams", weight * 4.0),
                            carbsPerKg = parsed.optDouble("carbsPerKg", 4.0),
                            fatsGrams = parsed.optDouble("fatsGrams", weight * 0.9),
                            fatsPerKg = parsed.optDouble("fatsPerKg", 0.9),
                            preWorkoutNutrition = parsed.optString("preWorkoutNutrition", "Carboidratos complexos e proteína magra 1h antes."),
                            postWorkoutNutrition = parsed.optString("postWorkoutNutrition", "Whey protein e carboidrato de rápida absorção."),
                            hydrationLitres = parsed.optDouble("hydrationLitres", (weight * 0.045).coerceAtLeast(3.0)),
                            dietAdjustments = if (adjustments.isNotEmpty()) adjustments else defaultDietAdjustments(userProfile, totalVolumeKg),
                            volumeInsight = parsed.optString(
                                "volumeInsight",
                                "O volume de carga exige reposição consistente de glicogênio para manter o ganho de força progressivo."
                            ),
                            isFromGeminiAI = true
                        )
                    }
                }
            } catch (_: Exception) {
                // Fallback to scientific formula
            }
        }
        fallbackVolumeNutritionCalculation(userProfile, totalVolumeKg, totalWorkoutsCount, totalCardioMinutes)
    }

    private fun fallbackVolumeNutritionCalculation(
        userProfile: UserProfile,
        totalVolumeKg: Double,
        workoutsCount: Int,
        cardioMinutes: Int
    ): VolumeNutritionEvaluationResult {
        val weight = userProfile.currentWeightKg
        val height = userProfile.heightCm
        val isHypertrophy = userProfile.goal == FitnessGoal.GANHO_PESO_HIPERTROFIA
        val isFatLoss = userProfile.goal == FitnessGoal.PERDA_PESO_EMAGRECIMENTO

        // Mifflin-St Jeor TDEE formula
        val bmr = 10 * weight + 6.25 * height - 5 * 28 + 5
        val activityMultiplier = when {
            userProfile.weeklyGoalDays >= 5 -> 1.55
            userProfile.weeklyGoalDays >= 3 -> 1.4
            else -> 1.25
        }
        val tdee = bmr * activityMultiplier
        val volumeExtraKcal = ((totalVolumeKg / 1000.0) * 15.0).coerceIn(100.0, 500.0)

        val targetCalories = when {
            isHypertrophy -> (tdee + volumeExtraKcal + 250).toInt()
            isFatLoss -> (tdee + (volumeExtraKcal * 0.5) - 350).toInt().coerceAtLeast(1600)
            else -> (tdee + volumeExtraKcal).toInt()
        }

        val proteinPerKg = if (isFatLoss) 2.2 else 2.0
        val proteinGrams = (weight * proteinPerKg).coerceAtLeast(100.0)
        val fatPerKg = 0.9
        val fatsGrams = (weight * fatPerKg).coerceAtLeast(50.0)
        val remainingKcal = targetCalories - (proteinGrams * 4 + fatsGrams * 9)
        val carbsGrams = (remainingKcal / 4.0).coerceAtLeast(150.0)
        val carbsPerKg = carbsGrams / weight

        val volumeFormatted = if (totalVolumeKg > 0) {
            "${String.format(java.util.Locale.US, "%,.0f", totalVolumeKg)} kg acumulados em $workoutsCount treinos"
        } else {
            "Volume inicial registrado"
        }

        return VolumeNutritionEvaluationResult(
            title = "Avaliação Nutricional Baseada no Volume",
            trainingVolumeSummary = volumeFormatted,
            recommendedDailyCalories = targetCalories,
            calorieAdjustmentReason = if (isHypertrophy) {
                "Superávit calórico de ~250-300 kcal/dia alinhado ao seu volume de ${String.format(java.util.Locale.US, "%.0f", totalVolumeKg)}kg para suportar hipertrofia miofibrilar."
            } else if (isFatLoss) {
                "Déficit moderado preservando proteína alta para oxidação de gordura sem perda de massa magra sob volume constante."
            } else {
                "Calorias de manutenção ajustadas ao gasto energético da sua sobrecarga de treino."
            },
            proteinGrams = String.format(java.util.Locale.US, "%.1f", proteinGrams).toDouble(),
            proteinPerKg = String.format(java.util.Locale.US, "%.1f", proteinPerKg).toDouble(),
            carbsGrams = String.format(java.util.Locale.US, "%.1f", carbsGrams).toDouble(),
            carbsPerKg = String.format(java.util.Locale.US, "%.1f", carbsPerKg).toDouble(),
            fatsGrams = String.format(java.util.Locale.US, "%.1f", fatsGrams).toDouble(),
            fatsPerKg = String.format(java.util.Locale.US, "%.1f", fatPerKg).toDouble(),
            preWorkoutNutrition = "Refeição 1h30 antes com ${String.format(java.util.Locale.US, "%.0f", weight * 0.8)}g de carboidratos complexos (aveia, arroz ou batata) e 25-30g de proteína magra.",
            postWorkoutNutrition = "Até 45min após: 30g de Whey Protein + 40-50g de carboidratos rápidos para reposição imediata do glicogênio muscular.",
            hydrationLitres = String.format(java.util.Locale.US, "%.1f", (weight * 0.045).coerceAtLeast(3.0)).toDouble(),
            dietAdjustments = defaultDietAdjustments(userProfile, totalVolumeKg),
            volumeInsight = "Com a progressão de volume de treino registrada, a demanda por substrato energético (carboidratos) aumenta proporcionalmente para evitar catabolismo e manter a intensidade das séries.",
            isFromGeminiAI = false
        )
    }

    private fun defaultDietAdjustments(userProfile: UserProfile, volumeKg: Double): List<String> {
        val list = mutableListOf<String>()
        list.add("Aumente 30g a 50g de carboidratos nos dias de treino de grandes grupos musculares (Pernas e Costas).")
        list.add("Distribua sua ingestão de proteínas em 4 a 5 refeições com pelo menos 25-30g cada para manter a síntese proteica ativa.")
        list.add("Mantenha suplementação diária de 5g de Creatina Monohidratada para otimizar a ressíntese de ATP e sustentar cargas altas.")
        list.add("Beba 500ml de água com eletrólitos durante treinos com duração superior a 50 minutos.")
        return list
    }

    /**
     * Generates a detailed biomechanical execution guide for an exercise with Gemini AI.
     */
    suspend fun generateExerciseExecutionGuide(
        exerciseName: String,
        muscleGroup: String,
        equipment: String
    ): ExerciseExecutionGuideResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val prompt = """
            Você é um cinesiologista e treinador de musculação de elite.
            Gere uma descrição minuciosa, científica e didática da execução correta do seguinte exercício:

            - Exercício: $exerciseName
            - Grupo Muscular Alvo: $muscleGroup
            - Equipamento: $equipment

            Retorne EXCLUSIVAMENTE um objeto JSON válido (sem crases ```json, sem markdown) com a seguinte estrutura:
            {
                "exerciseName": "$exerciseName",
                "targetMuscle": "Músculo primário e sinergistas",
                "equipment": "$equipment",
                "setupInstructions": [
                    "Passo 1 do posicionamento inicial e pegada",
                    "Passo 2 do alinhamento do tronco e estabilidade dos pés",
                    "Passo 3 da ativação de escápulas e abdômen"
                ],
                "executionSteps": [
                    "Fase Excêntrica (descida/alongamento): velocidade, trajeto e controle",
                    "Ponto de Inversão / Alongamento: ângulo articular seguro",
                    "Fase Concêntrica (subida/contração): aceleração intencional sem trancos",
                    "Pico de Contração: squeeze do músculo alvo no topo"
                ],
                "biomechanicsAndBreathing": "Explicação clara da respiração (inspire na descida, expire no esforço) e dos ângulos articulares.",
                "commonMistakes": [
                    "Erro comum 1 (e risco de lesão)",
                    "Erro comum 2 (perda de tensão no músculo alvo)",
                    "Erro comum 3 (uso excessivo de impulso/inércia)"
                ],
                "mindMuscleConnectionTip": "Dica de ouro de conexão mente-músculo para sentir o músculo queimar ao máximo."
            }
        """.trimIndent()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
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
                    val rawText = geminiRes?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!rawText.isNullOrBlank()) {
                        val cleanJson = rawText.substringAfter("{").substringBeforeLast("}")
                        val parsed = JSONObject("{$cleanJson}")

                        val setupList = mutableListOf<String>()
                        val setupArr = parsed.optJSONArray("setupInstructions")
                        if (setupArr != null) {
                            for (i in 0 until setupArr.length()) setupList.add(setupArr.getString(i))
                        }

                        val execList = mutableListOf<String>()
                        val execArr = parsed.optJSONArray("executionSteps")
                        if (execArr != null) {
                            for (i in 0 until execArr.length()) execList.add(execArr.getString(i))
                        }

                        val mistakesList = mutableListOf<String>()
                        val mistakesArr = parsed.optJSONArray("commonMistakes")
                        if (mistakesArr != null) {
                            for (i in 0 until mistakesArr.length()) mistakesList.add(mistakesArr.getString(i))
                        }

                        return@withContext ExerciseExecutionGuideResult(
                            exerciseName = parsed.optString("exerciseName", exerciseName),
                            targetMuscle = parsed.optString("targetMuscle", muscleGroup),
                            equipment = parsed.optString("equipment", equipment),
                            setupInstructions = if (setupList.isNotEmpty()) setupList else fallbackSetup(exerciseName),
                            executionSteps = if (execList.isNotEmpty()) execList else fallbackExecution(exerciseName),
                            biomechanicsAndBreathing = parsed.optString(
                                "biomechanicsAndBreathing",
                                "Inspire na fase excêntrica (descida) controlada em 2-3 segundos; expire na fase concêntrica (subida) mantendo as escápulas estáveis."
                            ),
                            commonMistakes = if (mistakesList.isNotEmpty()) mistakesList else fallbackMistakes(exerciseName),
                            mindMuscleConnectionTip = parsed.optString(
                                "mindMuscleConnectionTip",
                                "Concentre-se em puxar ou empurrar a partir do cotovelo, eliminando a tensão desnecessária nos punhos e trapézio."
                            ),
                            isFromGeminiAI = true
                        )
                    }
                }
            } catch (_: Exception) {
                // Fallback to local biomechanical database
            }
        }
        fallbackExerciseGuide(exerciseName, muscleGroup, equipment)
    }

    private fun fallbackExerciseGuide(
        exerciseName: String,
        muscleGroup: String,
        equipment: String
    ): ExerciseExecutionGuideResult {
        return ExerciseExecutionGuideResult(
            exerciseName = exerciseName,
            targetMuscle = muscleGroup.ifBlank { "Músculo Primário & Estabilizadores" },
            equipment = equipment.ifBlank { "Livre / Máquina" },
            setupInstructions = fallbackSetup(exerciseName),
            executionSteps = fallbackExecution(exerciseName),
            biomechanicsAndBreathing = "Mantenha o core ativado, coluna em posição neutra e respire ritmicamente: inspire no alongamento (fase excêntrica) e expire na força (fase concêntrica).",
            commonMistakes = fallbackMistakes(exerciseName),
            mindMuscleConnectionTip = "Não pense em apenas mover o peso do ponto A ao B. Sinta o músculo alongar com controle máximo e aperte com força no topo do movimento.",
            isFromGeminiAI = false
        )
    }

    private fun fallbackSetup(exerciseName: String): List<String> {
        val lower = exerciseName.lowercase()
        return when {
            lower.contains("supino") -> listOf(
                "Deite-se no banco com os olhos diretamente sob a barra ou alinhado aos halteres.",
                "Retraia e deprima as escápulas ('guarde as escápulas nos bolsos de trás').",
                "Apoie os pés firmemente no chão mantendo leve arco lombar fisiológico."
            )
            lower.contains("agachamento") -> listOf(
                "Pés na largura dos ombros com as pontas levemente apontadas para fora (15° a 30°).",
                "Apoie a barra firme no trapézio ou deltoide posterior com pegada firme.",
                "Encha o abdômen de ar (manobra de Valsalva) criando pressão intra-abdominal protetora."
            )
            lower.contains("terra") -> listOf(
                "Barra a 2cm das canelas, pés na largura do quadril.",
                "Segure a barra logo por fora das pernas, empurrando o quadril para trás.",
                "Peito aberto, coluna neutra e dorsal contraída travando a barra contra o corpo."
            )
            lower.contains("puxada") || lower.contains("remada") -> listOf(
                "Ajuste os apoios das pernas firmemente para evitar elevação do corpo.",
                "Segure com pegada firme e posicione o tronco levemente inclinado para trás (10° a 15°).",
                "Inicie deprimindo as escápulas antes de flexionar os cotovelos."
            )
            else -> listOf(
                "Ajuste o equipamento ou banco na altura compatível com sua estatura.",
                "Mantenha o abdômen contraído e postura ereta sem hiperextensão articular.",
                "Segure o peso com pegada firme e alinhamento neutro dos punhos."
            )
        }
    }

    private fun fallbackExecution(exerciseName: String): List<String> {
        return listOf(
            "Fase Excêntrica: Desça o peso de forma cadenciada (2 a 3 segundos), resistindo à gravidade.",
            "Ponto de Alongamento: Pause por uma fração de segundo no ponto de máximo alongamento sob tensão.",
            "Fase Concêntrica: Empurre ou puxe com aceleração controlada e sem trancos articulares.",
            "Pico de Contração: Contraia o músculo alvo com firmeza por 1 segundo no topo da repetição."
        )
    }

    private fun fallbackMistakes(exerciseName: String): List<String> {
        return listOf(
            "Usar inércia ou balanço do corpo para mover a carga em vez da contração muscular.",
            "Encurtar a amplitude de movimento (amplitude parcial) para usar cargas excessivas.",
            "Perder a estabilização articular no final da série (ex: ombros projetados para frente)."
        )
    }
}

