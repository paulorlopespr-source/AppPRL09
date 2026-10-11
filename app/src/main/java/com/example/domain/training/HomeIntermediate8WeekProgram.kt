package com.example.domain.training

import com.example.data.model.Equipment
import com.example.data.model.CardioSession
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.MuscleGroup
import com.example.data.model.SessionStatus
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTemplate
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.Normalizer
import kotlin.math.roundToInt

/** Official, flexible home program. It reuses the existing exercise rows and workout history. */
object HomeIntermediate8WeekProgram {
    const val CODE = "home_intermediate_8w_v1"
    const val TITLE_PREFIX = "Casa · Intermediário"
    const val CARDIO_MARKER_PREFIX = "[$CODE:"
    const val STRENGTH_SESSIONS_PER_WEEK = 3

    private val orderedSessions = listOf("A", "CARDIO_A", "B", "CARDIO_B", "C", "CARDIO_C")

    data class CompletionEvent(val timestampMillis: Long, val sessionCode: String)
    data class Progress(
        val weekNumber: Int,
        val nextSessionCode: String?,
        val isComplete: Boolean,
        val completedInWeek: Int,
        val recoveryRecommended: Boolean = false
    )

    fun progress(events: List<CompletionEvent>): Progress {
        var week = 1
        var nextIndex = 0
        var lastAcceptedCode: String? = null
        events.sortedBy { it.timestampMillis }.forEach { event ->
            if (week > 8 || event.sessionCode != orderedSessions[nextIndex]) return@forEach
            lastAcceptedCode = event.sessionCode
            nextIndex++
            if (nextIndex == orderedSessions.size) {
                if (week == 8) return Progress(weekNumber = 8, nextSessionCode = null, isComplete = true, completedInWeek = 6, recoveryRecommended = true)
                week++
                nextIndex = 0
            }
        }
        return Progress(
            weekNumber = week,
            nextSessionCode = orderedSessions.getOrNull(nextIndex),
            isComplete = false,
            completedInWeek = nextIndex,
            recoveryRecommended = lastAcceptedCode == "CARDIO_C"
        )
    }

    fun progress(workouts: List<WorkoutSession>, cardios: List<CardioSession>, templates: List<WorkoutTemplate>): Progress {
        val codeByTemplateId = templates.mapNotNull { template ->
            strengthCode(template)?.let { template.id to it }
        }.toMap()
        val events = buildList {
            workouts.filter { it.status == SessionStatus.COMPLETED }.forEach { session ->
                codeByTemplateId[session.templateId]?.let { add(CompletionEvent(session.startTimeMillis, it)) }
            }
            cardios.forEach { session ->
                cardioCodeFromNotes(session.notes)?.takeIf { session.durationMinutes >= minimumCardioMinutes(it) }
                    ?.let { add(CompletionEvent(session.timestampMillis, it)) }
            }
        }
        return progress(events)
    }

    fun isStrengthTemplate(template: WorkoutTemplate): Boolean = strengthCode(template) != null
    fun isCardioTemplate(template: WorkoutTemplate): Boolean = cardioCode(template) != null
    fun isRestTemplate(template: WorkoutTemplate): Boolean = template.title.startsWith("$TITLE_PREFIX Descanso")
    fun isProgramTemplate(template: WorkoutTemplate): Boolean = isStrengthTemplate(template) || isCardioTemplate(template) || isRestTemplate(template)

    fun strengthCode(template: WorkoutTemplate): String? = listOf("A", "B", "C").firstOrNull {
        template.title.startsWith("$TITLE_PREFIX $it ·")
    }

    fun cardioCode(template: WorkoutTemplate): String? = listOf("A", "B", "C").firstOrNull {
        template.title.startsWith("$TITLE_PREFIX Cardio $it ·")
    }?.let { "CARDIO_$it" }

    fun cardioMarker(sessionCode: String, plannedMinutes: Int? = null): String =
        "$CARDIO_MARKER_PREFIX$sessionCode${plannedMinutes?.let { ":$it" }.orEmpty()}]"

    fun cardioCodeFromNotes(notes: String): String? = listOf("CARDIO_A", "CARDIO_B", "CARDIO_C").firstOrNull {
        notes.contains("$CARDIO_MARKER_PREFIX$it")
    }

    private fun minimumCardioMinutes(sessionCode: String): Int = when (sessionCode) {
        "CARDIO_A" -> 35
        "CARDIO_B" -> 28
        else -> 30
    }

    fun cardioDurationOptions(sessionCode: String): List<Int> = when (sessionCode) {
        "CARDIO_A" -> listOf(35, 40, 45)
        "CARDIO_B" -> listOf(28, 32, 37)
        else -> listOf(30, 40, 50)
    }

    fun cardioDefaultIntensity(sessionCode: String) = when (sessionCode) {
        "CARDIO_B" -> com.example.data.model.IntensityLevel.INTENSA
        "CARDIO_C" -> com.example.data.model.IntensityLevel.LEVE
        else -> com.example.data.model.IntensityLevel.MODERADA
    }

    fun displayName(sessionCode: String): String = when (sessionCode) {
        "A" -> "Força A · Peito, ombros e tríceps"
        "B" -> "Força B · Pernas, costas e bíceps"
        "C" -> "Força C · Peito, ombros e full body"
        "CARDIO_A" -> "Cardio A · Base aeróbica"
        "CARDIO_B" -> "Cardio B · Intervalado"
        "CARDIO_C" -> "Cardio C · Recuperativo"
        else -> "Descanso programado"
    }

    enum class Phase(val title: String, val coaching: String) {
        CALIBRATION("Calibração", "Priorize técnica e amplitude; mantenha 2–3 repetições em reserva."),
        PROGRESSION("Progressão", "Busque mais repetições dentro da faixa antes de pensar em aumentar a carga."),
        DELOAD("Deload", "Semana de recuperação: reduza aproximadamente 30% das séries e não force a falha."),
        INTENSIFICATION("Intensificação", "Retome o volume. Considere pequena carga extra somente ao atingir o topo da faixa com técnica e RIR adequados."),
        CONSOLIDATION("Consolidação", "Compare as séries registradas e procure uma progressão mensurável, sem exigir aumento de peso.")
    }

    data class Week(val number: Int, val phase: Phase)

    fun weekForCompletedStrengthSessions(completedCount: Int): Week {
        val week = (completedCount.coerceAtLeast(0) / STRENGTH_SESSIONS_PER_WEEK + 1).coerceIn(1, 8)
        val phase = when (week) {
            1, 2 -> Phase.CALIBRATION
            3, 4 -> Phase.PROGRESSION
            5 -> Phase.DELOAD
            6, 7 -> Phase.INTENSIFICATION
            else -> Phase.CONSOLIDATION
        }
        return Week(week, phase)
    }

    fun applyWeek(plan: WorkoutExercisePlan, week: Int): WorkoutExercisePlan {
        val clampedWeek = week.coerceIn(1, 8)
        val phasedSets = if (clampedWeek == 5) {
            val reducedCount = (plan.sets.size * 0.7).roundToInt().coerceAtLeast(1)
            plan.sets.take(reducedCount).mapIndexed { index, set -> set.copy(setNumber = index + 1, isCompleted = false) }
        } else plan.sets.map { it.copy(isCompleted = false) }
        val phase = weekForCompletedStrengthSessions((clampedWeek - 1) * STRENGTH_SESSIONS_PER_WEEK).phase
        val phaseNote = "Semana $clampedWeek/8 · ${phase.title}. ${phase.coaching}"
        return plan.copy(sets = phasedSets, notes = listOf(plan.notes, phaseNote).filter { it.isNotBlank() }.joinToString("\n"))
    }

    fun templates(exercises: List<Exercise>): List<WorkoutTemplate> {
        val catalog = exercises.associateBy { normalize(it.name) }
        val sessions = listOf(
            SessionSpec(
                code = "A",
                focus = "Peito + Ombros + Tríceps",
                minutes = 60,
                moves = listOf(
                    Move(listOf("Supino reto com barra"), MuscleGroup.PEITO, 4, 6, 8, 150, 2, 3),
                    Move(listOf("Desenvolvimento militar com barra em pé", "Desenvolvimento militar com barra"), MuscleGroup.OMBROS, 3, 6, 10, 120, 2, 3),
                    Move(listOf("Supino reto com halteres"), MuscleGroup.PEITO, 3, 8, 12, 90, 1, 3),
                    Move(listOf("Elevação lateral com halteres"), MuscleGroup.OMBROS, 3, 12, 15, 75, 1, 3),
                    Move(listOf("Tríceps testa com barra W", "Tríceps testa com barra EZ"), MuscleGroup.TRICEPS, 3, 8, 12, 90, 1, 3),
                    Move(listOf("Tríceps coice com halter", "Tríceps francês com halter"), MuscleGroup.TRICEPS, 2, 12, 20, 60, 1, 3, "A biblioteca ainda não contém um exercício ilustrado com elástico; esta é a alternativa cadastrada com halter.")
                )
            ),
            SessionSpec(
                code = "B",
                focus = "Pernas + Costas + Bíceps",
                minutes = 70,
                moves = listOf(
                    Move(listOf("Agachamento livre com barra"), MuscleGroup.QUADRICEPS, 4, 6, 10, 150, 2, 3, "Sem rack com travas, substitua por agachamento búlgaro com halteres; não faça o agachamento livre sem suporte seguro."),
                    Move(listOf("Levantamento terra romeno"), MuscleGroup.POSTERIOR_GLUTEOS, 3, 8, 10, 120, 2, 3),
                    Move(listOf("Remada curvada com barra (pegada pronada)", "Remada curvada com barra"), MuscleGroup.COSTAS, 4, 6, 10, 120, 2, 3),
                    Move(listOf("Afundo com halteres (passada)", "Avanço com halteres"), MuscleGroup.QUADRICEPS, 3, 8, 12, 90, 1, 3, "Repetições por perna."),
                    Move(listOf("Remada unilateral com halter (serrote)", "Remada unilateral com halter"), MuscleGroup.COSTAS, 3, 8, 12, 90, 1, 3),
                    Move(listOf("Rosca direta com barra W", "Rosca direta com barra"), MuscleGroup.BICEPS, 3, 8, 12, 90, 1, 3),
                    Move(listOf("Panturrilha com halteres", "Panturrilha em degrau"), MuscleGroup.PANTURRILHA, 3, 12, 20, 60, 1, 3)
                )
            ),
            SessionSpec(
                code = "C",
                focus = "Peito + Ombros + Full Body",
                minutes = 70,
                moves = listOf(
                    Move(listOf("Supino reto com barra", "Supino fechado com barra"), MuscleGroup.PEITO, 3, 8, 12, 120, 1, 3, "Supino reto com pegada moderadamente fechada."),
                    Move(listOf("Desenvolvimento sentado com halteres", "Desenvolvimento com halteres sentado"), MuscleGroup.OMBROS, 3, 8, 12, 105, 1, 3),
                    Move(listOf("Agachamento búlgaro com halteres", "Agachamento búlgaro"), MuscleGroup.QUADRICEPS, 3, 8, 12, 90, 1, 3, "Repetições por perna."),
                    Move(listOf("Remada unilateral com halter (serrote)", "Remada unilateral com halter"), MuscleGroup.COSTAS, 3, 10, 15, 90, 1, 3),
                    Move(listOf("Crucifixo com halteres"), MuscleGroup.PEITO, 2, 12, 15, 75, 1, 3, "Alternativa ilustrada ao crucifixo com elástico, ainda não disponível na biblioteca."),
                    Move(listOf("Elevação lateral com halteres"), MuscleGroup.OMBROS, 3, 12, 20, 60, 1, 3),
                    Move(listOf("Rosca martelo com halteres", "Rosca martelo"), MuscleGroup.BICEPS, 2, 10, 15, 75, 1, 3),
                    Move(listOf("Tríceps francês com halter", "Tríceps coice com halter"), MuscleGroup.TRICEPS, 2, 12, 20, 75, 1, 3, "Alternativa ilustrada ao tríceps com elástico, ainda não disponível na biblioteca.")
                )
            )
        )

        val adapter = Moshi.Builder().add(KotlinJsonAdapterFactory())
            .build().adapter<List<WorkoutExercisePlan>>(Types.newParameterizedType(List::class.java, WorkoutExercisePlan::class.java))
        val strengthTemplates = sessions.map { session ->
            val plans = session.moves.map { move ->
                val exercise = move.names.asSequence().mapNotNull { catalog[normalize(it)] }.firstOrNull()
                    ?: exercises.firstOrNull { it.muscleGroup == move.group && it.equipment in move.allowedEquipment }
                    ?: error("Exercício não encontrado na biblioteca: ${move.names.first()}")
                WorkoutExercisePlan(
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    muscleGroup = exercise.muscleGroup.displayName,
                    sets = (1..move.sets).map { setNumber ->
                        ExerciseSetEntry(setNumber = setNumber, weightKg = 0.0, reps = move.repMin, restSeconds = move.restSeconds)
                    },
                    targetRestSeconds = move.restSeconds,
                    notes = listOfNotNull(move.note, "Faixa alvo: ${move.repMin}–${move.repMax} reps; RIR ${move.rirMin}–${move.rirMax}.").joinToString(" "),
                    targetRepMin = move.repMin,
                    targetRepMax = move.repMax,
                    targetRirMin = move.rirMin,
                    targetRirMax = move.rirMax
                )
            }
            WorkoutTemplate(
                title = "$TITLE_PREFIX ${session.code} · ${session.focus}",
                subtitle = "Plano em casa · força ${session.code} de 3 · 8 semanas",
                category = WorkoutCategory.INTERMEDIARIO,
                defaultRestSeconds = 90,
                executionDurationMinutes = session.minutes,
                isPreset = true,
                exercisesJson = adapter.toJson(plans),
                description = programDescription(session.code) + "\n\nSessão ${session.code}: ${session.focus}. Cargas iniciais vazias de propósito; escolha uma carga segura para alcançar a faixa com RIR 1–3."
            )
        }
        return strengthTemplates + cardioTemplates() + restTemplate()
    }

    private fun cardioTemplates(): List<WorkoutTemplate> = listOf(
        cardioTemplate(
            code = "A",
            focus = "Base aeróbica",
            minutes = 40,
            durationRange = "35–45 min",
            description = "Cardio A — Base aeróbica: 35–45 minutos em intensidade moderada e contínua. Escolha caminhada, corrida, bicicleta ou outra modalidade confortável; registre duração, intensidade e, quando houver, distância/ritmo."
        ),
        cardioTemplate(
            code = "B",
            focus = "Intervalado",
            minutes = 32,
            durationRange = "aprox. 28–37 min",
            description = "Cardio B — Intervalado: aqueça por 5–8 min; faça 6–8 ciclos de 1 min forte + 2 min leve; finalize com 5 min leves. Duração total aproximada de 28–37 min. Escolha caminhada, corrida, bicicleta ou outra modalidade e registre duração/intensidade."
        ),
        cardioTemplate(
            code = "C",
            focus = "Recuperativo",
            minutes = 40,
            durationRange = "30–50 min",
            description = "Cardio C — Recuperativo: 30–50 min em intensidade leve a moderada. Escolha caminhada, corrida leve, bicicleta ou outra modalidade que permita manter esforço confortável. Registre duração, intensidade e, quando houver, distância/ritmo."
        )
    )

    private fun cardioTemplate(code: String, focus: String, minutes: Int, durationRange: String, description: String) = WorkoutTemplate(
        title = "$TITLE_PREFIX Cardio $code · $focus",
        subtitle = "Plano em casa · cardio $code · $durationRange",
        category = WorkoutCategory.INTERMEDIARIO,
        defaultRestSeconds = 0,
        executionDurationMinutes = minutes,
        isPreset = true,
        exercisesJson = "[]",
        description = "$CODE · Intermediário · ciclo de 8 semanas.\n\n$description\n\nSequência flexível: Força A → Cardio A → Força B → Cardio B → Força C → Cardio C → descanso programado. Não há dias fixos; se atrasar, retome a sessão pendente. O descanso não conta como falta."
    )

    private fun restTemplate() = WorkoutTemplate(
        title = "$TITLE_PREFIX Descanso programado",
        subtitle = "Plano em casa · recuperação · não é falta",
        category = WorkoutCategory.INTERMEDIARIO,
        defaultRestSeconds = 0,
        executionDurationMinutes = 0,
        isPreset = true,
        exercisesJson = "[]",
        description = "$CODE · Descanso planejado entre os blocos de treino. Use o dia para recuperação e não registre como falta. A sequência é flexível, sem dias da semana fixos; quando estiver pronto, continue na próxima sessão pendente."
    )

    private fun programDescription(code: String) = """$CODE · Intermediário · hipertrofia, força e condicionamento · ciclo de 8 semanas.

Sequência flexível, sem dias da semana fixos: Força A → Cardio A → Força B → Cardio B → Força C → Cardio C → descanso. Se atrasar, retome a próxima sessão pendente; descanso planejado não é falta.

Cardio A — base aeróbica: 35–45 min moderados. Cardio B — 5–8 min aquecimento, 6–8 ciclos de 1 min forte + 2 min leve e 5 min de volta à calma. Cardio C — 30–50 min leves/moderados. Abra a sessão Cardio A/B/C na biblioteca do plano, escolha a modalidade e duração, e o app registra duração, intensidade e distância/ritmo quando disponível.

Progressão: semanas 1–2 calibração; 3–4 aumentar repetições dentro da faixa; semana 5 deload (cerca de 30% menos séries); semanas 6–7 retomar volume e só considerar aumento pequeno de carga ao atingir o topo da faixa em todas as séries com boa técnica e RIR adequado; semana 8 consolidação. Aumentar peso nunca é obrigatório. O histórico preserva séries, repetições, carga e RIR para comparação.

Equipamentos previstos: barra e anilhas, halteres ajustáveis, banco/suporte e elásticos. Informe os equipamentos ao escolher Em Casa; se não houver rack com trava, troque o agachamento livre com barra pela alternativa segura com halteres. Exercícios com elástico sem ilustração na biblioteca usam alternativas equivalentes já cadastradas.""".trimIndent()

    private data class SessionSpec(val code: String, val focus: String, val minutes: Int, val moves: List<Move>)
    private data class Move(
        val names: List<String>, val group: MuscleGroup, val sets: Int,
        val repMin: Int, val repMax: Int, val restSeconds: Int,
        val rirMin: Int, val rirMax: Int, val note: String = "",
        val allowedEquipment: Set<Equipment> = setOf(Equipment.BARRA, Equipment.HALTERES, Equipment.PESO_CORPO, Equipment.ANILHAS, Equipment.SUPORTE_BARRA)
    )

    private fun normalize(value: String): String = Normalizer.normalize(value.lowercase(), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .replace("[^a-z0-9]+".toRegex(), " ").trim()
}
