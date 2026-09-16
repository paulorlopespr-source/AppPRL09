package com.example.domain.coach

import com.example.data.model.AICoachMessage
import com.example.data.model.AIWorkoutPlanResult
import com.example.data.model.UserProfile
import com.example.data.repository.FitnessRepository

/**
 * Fachada única para IA de treino. O modelo recebe números já calculados pelo app.
 * Nutrição permanece no serviço existente até migração separada para não quebrar funcionalidade.
 */
class CoachPRL09Service(private val repository: FitnessRepository) {

    suspend fun ask(question: String, profile: UserProfile, context: CoachPRL09Context): AICoachMessage {
        val grounded = """
            Você é o Coach PRL09. Responda em português claro e objetivo.
            REGRAS OBRIGATÓRIAS:
            1. Os números do CONTEXTO DO APP são a fonte de verdade. Não recalcule, estime ou invente métricas existentes.
            2. Se um dado não estiver no contexto, diga que ele não está disponível.
            3. Diferencie fato registrado no app de sugestão de treinamento.
            4. Explique tendências usando os períodos fornecidos; não crie sessões, PRs, pesos, distâncias ou readiness inexistentes.
            5. Recomendações de treino devem respeitar readiness e histórico, sem diagnóstico médico.

            ${context.asGroundedText()}
        """.trimIndent()
        return repository.askAICoach(question, profile, grounded)
    }

    suspend fun generateAdaptedWorkout(
        profile: UserProfile,
        context: CoachPRL09Context,
        availableMinutes: Int,
        availableEquipment: List<String>,
        focus: String?
    ): AIWorkoutPlanResult {
        require(availableMinutes in 10..240)
        val request = """
            Gere uma sessão de treino executável para HOJE.
            Tempo máximo: $availableMinutes minutos.
            Equipamentos disponíveis: ${availableEquipment.ifEmpty { listOf("peso corporal") }.joinToString()}.
            Foco solicitado: ${focus ?: "compatível com objetivo, histórico e recuperação"}.

            ${context.asGroundedText()}

            Não invente desempenho anterior. Use o readiness somente como restrição de volume/intensidade.
            Se o contexto indicar deload/recuperação, reduza volume e intensidade de forma conservadora.
        """.trimIndent()
        return repository.generateAIWorkoutRoutine(request, profile)
    }
}
