package com.example.data.local

import com.example.data.model.Equipment
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.FitnessGoal
import com.example.data.model.MuscleGroup
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutCategory
import com.example.data.model.WorkoutExercisePlan
import com.example.data.model.WorkoutTemplate
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object DefaultFitnessData {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val planListType = Types.newParameterizedType(List::class.java, WorkoutExercisePlan::class.java)
    private val jsonAdapter = moshi.adapter<List<WorkoutExercisePlan>>(planListType)

    fun getDefaultUserProfile(): UserProfile {
        return UserProfile(
            id = 1,
            name = "Atleta Fit",
            age = 26,
            heightCm = 175.0,
            startingWeightKg = 76.0,
            currentWeightKg = 78.0,
            targetWeightKg = 82.0,
            goal = FitnessGoal.GANHO_PESO_HIPERTROFIA,
            defaultGymLocation = "Academia Smart Fit",
            weeklyGoalDays = 5,
            defaultRestSeconds = 60,
            aiCaloricAdvice = "Para ganho de massa magra, mantenha um superávit calórico de 300-500 kcal diárias com 1.8g a 2.2g de proteína por kg de peso corporal."
        )
    }

    fun getDefaultExercises(): List<Exercise> {
        return listOf(
            // Peito
            Exercise(
                id = 1,
                name = "Supino Reto com Barra",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 90,
                instructions = "Deite no banco, pés firmes no chão, pegada um pouco mais larga que os ombros. Desça a barra controladamente até o peitoral médio e empurre firmando a escápula.",
                executionTips = "Mantenha as escápulas aduzidas e os cotovelos a ~75 graus do tronco."
            ),
            Exercise(
                id = 2,
                name = "Supino Inclinado com Halteres",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.HALTERES,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Banco regulado a 30-45 graus. Eleve os halteres em trajetória convergente controlando o alongamento do peitoral superior.",
                executionTips = "Não deixe os halteres baterem no topo e controle a fase excêntrica."
            ),
            Exercise(
                id = 3,
                name = "Crossover na Polia Média",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.POLIA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Com o tronco levemente inclinado à frente, puxe os cabos aproximando as mãos à frente do peito, apertando o peitoral por 1 segundo.",
                executionTips = "Mantenha uma leve flexão nos cotovelos durante todo o movimento."
            ),
            Exercise(
                id = 4,
                name = "Crucifixo Reto com Halteres",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Abra os braços controladamente sentindo o peitoral alongar e volte contraindo sem esticar totalmente os cotovelos.",
                executionTips = "Foque na sensação de abraçar uma árvore no topo."
            ),
            Exercise(
                id = 5,
                name = "Voador / Peck Deck",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Ajuste o assento para que os braços fiquem na altura do peitoral. Feche as hastes contraindo o peito.",
                executionTips = "Mantenha o peito estufado e não projete os ombros à frente."
            ),

            // Costas
            Exercise(
                id = 6,
                name = "Puxada Alta Frontal",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.POLIA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Pegada pronada aberta. Puxe a barra em direção à clavícula, inclinando o tronco levemente para trás e deprimindo as escápulas.",
                executionTips = "Puxe com os cotovelos apontando para o chão, e não com os antebraços."
            ),
            Exercise(
                id = 7,
                name = "Remada Curvada com Barra",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 90,
                instructions = "Tronco inclinado a 45 graus, coluna neutra, joelhos semiflexionados. Puxe a barra em direção ao umbigo.",
                executionTips = "Não arredonde a coluna lombar; mantenha o abdômen contraído."
            ),
            Exercise(
                id = 8,
                name = "Remada Baixa no Triângulo",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.POLIA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Sente ereto com os pés apoiados. Puxe o triângulo até o abdômen estufando o peito e aproximando as escápulas.",
                executionTips = "Evite balançar excessivamente o tronco para trás."
            ),
            Exercise(
                id = 9,
                name = "Levantamento Terra (Deadlift)",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.BARRA,
                defaultSets = 3,
                defaultReps = 6,
                defaultRestSeconds = 120,
                instructions = "Pés na largura dos quadris, barra colada nas canelas. Levante o peso estendendo quadris e joelhos simultaneamente.",
                executionTips = "Mantenha a barra colada ao corpo durante todo o trajeto."
            ),
            Exercise(
                id = 10,
                name = "Pulldown na Polia com Corda",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.POLIA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Braços estendidos, puxe a corda para baixo em direção às coxas abrindo as pontas no final.",
                executionTips = "Excelente para isolamento e ativação da grande dorsal."
            ),

            // Quadríceps & Pernas
            Exercise(
                id = 11,
                name = "Agachamento Livre com Barra",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 90,
                instructions = "Barra apoiada no trapézio, pés na largura dos ombros. Agache flexionando joelhos e quadril até que as coxas fiquem paralelas ao solo.",
                executionTips = "Mantenha o peso nos calcanhares e os joelhos alinhados com as pontas dos pés."
            ),
            Exercise(
                id = 12,
                name = "Leg Press 45°",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.MAQUINA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 75,
                instructions = "Pés no centro da plataforma na largura dos ombros. Destrave e desça a plataforma até 90 graus sem descolar a lombar.",
                executionTips = "Nunca estenda os joelhos completamente (bloqueio articular) no topo."
            ),
            Exercise(
                id = 13,
                name = "Cadeira Extensora",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Apoie os tornozelos sob o rolo. Estenda as pernas contraindo os quadríceps no topo por 1 segundo.",
                executionTips = "Controle a volta do peso sem deixar as placas baterem."
            ),
            Exercise(
                id = 14,
                name = "Agachamento Búlgaro com Halteres",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Um pé apoiado atrás em um banco e o outro à frente. Desça flexionando o joelho dianteiro até quase tocar o joelho traseiro no chão.",
                executionTips = "Mantenha o tronco firme e o pé da frente bem plantado."
            ),

            // Posterior & Glúteos
            Exercise(
                id = 15,
                name = "Elevação Pélvica com Barra / Máquina",
                muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 75,
                instructions = "Costas apoiadas no banco, barra sobre os quadris com almofada. Eleve o quadril até alinhar com o tronco contraindo forte os glúteos.",
                executionTips = "Mantenha o queixo no peito e faça uma pausa de 2 segundos no topo da contração."
            ),
            Exercise(
                id = 16,
                name = "Stiff com Halteres ou Barra",
                muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS,
                equipment = Equipment.HALTERES,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 75,
                instructions = "Pés alinhados, joelhos levemente destravados. Incline o quadril para trás descendo os halteres rente às pernas até sentir posterior alongar.",
                executionTips = "Coluna reta durante todo o percurso; o movimento nasce no quadril."
            ),
            Exercise(
                id = 17,
                name = "Mesa Flexora",
                muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Deitado de bruços, posicione o rolo acima dos calcanhares. Flexione as pernas aproximando os calcanhares dos glúteos.",
                executionTips = "Não deixe o quadril subir durante a flexão."
            ),
            Exercise(
                id = 18,
                name = "Cadeira Abdutora",
                muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 15,
                defaultRestSeconds = 45,
                instructions = "Sente na máquina e abra as pernas contra a resistência, focando na contração do glúteo médio.",
                executionTips = "Experimente inclinar o tronco um pouco à frente para maior ativação do glúteo."
            ),

            // Ombros
            Exercise(
                id = 19,
                name = "Desenvolvimento com Halteres",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.HALTERES,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Sentado com as costas apoiadas, segure os halteres na altura das orelhas. Empurre para cima até quase estender os braços.",
                executionTips = "Cotovelos ligeiramente à frente do plano do corpo no plano escapular."
            ),
            Exercise(
                id = 20,
                name = "Elevação Lateral com Halteres / Cabo",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.HALTERES,
                defaultSets = 4,
                defaultReps = 12,
                defaultRestSeconds = 45,
                instructions = "Em pé, eleve os braços lateralmente até a altura dos ombros com os cotovelos levemente flexionados.",
                executionTips = "Pense em empurrar as paredes com os cotovelos, sem encolher o pescoço."
            ),
            Exercise(
                id = 21,
                name = "Crucifixo Invertido no Peck Deck",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 45,
                instructions = "Sente virado para a máquina. Abra os braços para trás ativando o deltoide posterior.",
                executionTips = "Mantenha os ombros relaxados e baixos."
            ),

            // Bíceps
            Exercise(
                id = 22,
                name = "Rosca Direta com Barra W",
                muscleGroup = MuscleGroup.BICEPS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Em pé, segure a barra W na largura dos ombros. Flexione os cotovelos subindo a barra sem balançar o tronco.",
                executionTips = "Mantenha os cotovelos colados ao lado do corpo."
            ),
            Exercise(
                id = 23,
                name = "Rosca Martelo com Halteres",
                muscleGroup = MuscleGroup.BICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Segure os halteres com pegada neutra (palmas voltadas uma para a outra). Flexione os antebraços.",
                executionTips = "Excelente para braquial e antebraço."
            ),
            Exercise(
                id = 24,
                name = "Rosca Scott na Máquina / Banco",
                muscleGroup = MuscleGroup.BICEPS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Apoie os braços no banco inclinado. Puxe a barra ou manopla isolando o bíceps no pico de contração.",
                executionTips = "Não desça além do ponto seguro para não sobrecarregar o tendão distal."
            ),

            // Tríceps
            Exercise(
                id = 25,
                name = "Tríceps Corda na Polia",
                muscleGroup = MuscleGroup.TRICEPS,
                equipment = Equipment.POLIA,
                defaultSets = 4,
                defaultReps = 12,
                defaultRestSeconds = 45,
                instructions = "Em pé de frente para a polia alta, puxe a corda para baixo abrindo as pontas no final da extensão.",
                executionTips = "Mantenha os cotovelos fixos nas laterais das costelas."
            ),
            Exercise(
                id = 26,
                name = "Tríceps Testa com Barra W",
                muscleGroup = MuscleGroup.TRICEPS,
                equipment = Equipment.BARRA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Deitado no banco, estenda os braços para cima. Flexione apenas os cotovelos descendo a barra em direção à testa.",
                executionTips = "Mantenha os cotovelos apontados para o teto sem abri-los excessivamente."
            ),
            Exercise(
                id = 27,
                name = "Tríceps Francês com Halter",
                muscleGroup = MuscleGroup.TRICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Sentado ou em pé, segure um halter com as duas mãos acima da cabeça. Flexione os cotovelos descendo o peso atrás da nuca.",
                executionTips = "Excelente para alongamento e ativação da cabeça longa do tríceps."
            ),

            // Abdômen & Panturrilha
            Exercise(
                id = 28,
                name = "Prancha Abdominal Isométrica",
                muscleGroup = MuscleGroup.ABDOMEN,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 3,
                defaultReps = 45, // seconds
                defaultRestSeconds = 45,
                instructions = "Apoie os antebraços e pontas dos pés no chão, mantendo o corpo reto como uma prancha.",
                executionTips = "Contraia glúteos e abdômen; não deixe o quadril cair."
            ),
            Exercise(
                id = 29,
                name = "Abdominal Infra na Paralela / Suspenso",
                muscleGroup = MuscleGroup.ABDOMEN,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 3,
                defaultReps = 15,
                defaultRestSeconds = 45,
                instructions = "Apoiado nas paralelas, eleve os joelhos em direção ao peito flexionando o quadril e a coluna.",
                executionTips = "Evite usar o balanço do corpo; faça o movimento de forma lenta."
            ),
            Exercise(
                id = 30,
                name = "Panturrilha em Pé na Máquina / Degrau",
                muscleGroup = MuscleGroup.PANTURRILHA,
                equipment = Equipment.MAQUINA,
                defaultSets = 4,
                defaultReps = 15,
                defaultRestSeconds = 45,
                instructions = "Apoie a ponta dos pés na borda. Desça alongando bem os calcanhares e suba na ponta máxima dos pés.",
                executionTips = "Faça uma pausa de 1 segundo na ponta dos pés para maior tensão."
            )
        )
    }

    fun getDefaultWorkoutTemplates(exercises: List<Exercise>): List<WorkoutTemplate> {
        val exMap = exercises.associateBy { it.id }

        fun makePlan(exId: Long, sets: Int = 4, reps: Int = 10, weight: Double = 20.0, rest: Int = 60): WorkoutExercisePlan {
            val ex = exMap[exId] ?: Exercise(
                id = exId,
                name = "Exercício $exId",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.HALTERES
            )
            val setList = (1..sets).map {
                ExerciseSetEntry(
                    setNumber = it,
                    weightKg = weight,
                    reps = reps,
                    isCompleted = false,
                    restSeconds = rest
                )
            }
            return WorkoutExercisePlan(
                exerciseId = exId,
                exerciseName = ex.name,
                muscleGroup = ex.muscleGroup.displayName,
                sets = setList,
                targetRestSeconds = rest,
                notes = ex.executionTips
            )
        }

        // Treino ABC - A: Peito, Tríceps & Ombro
        val planA = listOf(
            makePlan(1, sets = 4, reps = 10, weight = 40.0, rest = 90), // Supino Reto
            makePlan(2, sets = 4, reps = 10, weight = 20.0, rest = 60), // Supino Inclinado
            makePlan(3, sets = 3, reps = 12, weight = 15.0, rest = 60), // Crossover
            makePlan(19, sets = 3, reps = 10, weight = 16.0, rest = 60), // Desenv. Ombros
            makePlan(20, sets = 4, reps = 12, weight = 10.0, rest = 45), // Elevação Lateral
            makePlan(25, sets = 4, reps = 12, weight = 25.0, rest = 45), // Tríceps Corda
            makePlan(26, sets = 3, reps = 10, weight = 18.0, rest = 60)  // Tríceps Testa
        )

        // Treino ABC - B: Costas, Bíceps & Abdômen
        val planB = listOf(
            makePlan(6, sets = 4, reps = 10, weight = 45.0, rest = 60), // Puxada Alta
            makePlan(7, sets = 4, reps = 8, weight = 35.0, rest = 75),  // Remada Curvada
            makePlan(8, sets = 3, reps = 10, weight = 40.0, rest = 60), // Remada Baixa
            makePlan(21, sets = 3, reps = 12, weight = 30.0, rest = 45), // Crucifixo Invertido
            makePlan(22, sets = 4, reps = 10, weight = 20.0, rest = 60), // Rosca Direta
            makePlan(23, sets = 3, reps = 10, weight = 14.0, rest = 60), // Rosca Martelo
            makePlan(28, sets = 3, reps = 45, weight = 0.0, rest = 45)  // Prancha
        )

        // Treino ABC - C: Pernas Completas & Panturrilha
        val planC = listOf(
            makePlan(11, sets = 4, reps = 8, weight = 50.0, rest = 90), // Agachamento
            makePlan(12, sets = 4, reps = 10, weight = 120.0, rest = 75), // Leg Press
            makePlan(13, sets = 3, reps = 12, weight = 45.0, rest = 60), // Cadeira Extensora
            makePlan(16, sets = 4, reps = 10, weight = 24.0, rest = 75), // Stiff
            makePlan(17, sets = 3, reps = 12, weight = 35.0, rest = 60), // Mesa Flexora
            makePlan(30, sets = 4, reps = 15, weight = 60.0, rest = 45)  // Panturrilha
        )

        // Treino Push (Empurrar)
        val planPush = listOf(
            makePlan(1, sets = 4, reps = 8, weight = 45.0, rest = 90),
            makePlan(2, sets = 3, reps = 10, weight = 22.0, rest = 60),
            makePlan(5, sets = 3, reps = 12, weight = 40.0, rest = 60),
            makePlan(19, sets = 4, reps = 10, weight = 18.0, rest = 60),
            makePlan(20, sets = 4, reps = 15, weight = 8.0, rest = 45),
            makePlan(25, sets = 4, reps = 12, weight = 25.0, rest = 45)
        )

        // Treino Foco Glúteos & Pernas Feminino
        val planGluteos = listOf(
            makePlan(15, sets = 4, reps = 10, weight = 60.0, rest = 90), // Elevação Pélvica
            makePlan(14, sets = 3, reps = 10, weight = 12.0, rest = 60), // Búlgaro
            makePlan(12, sets = 4, reps = 12, weight = 100.0, rest = 75), // Leg Press
            makePlan(16, sets = 4, reps = 10, weight = 20.0, rest = 60), // Stiff
            makePlan(18, sets = 4, reps = 15, weight = 45.0, rest = 45), // Abdutora
            makePlan(30, sets = 4, reps = 15, weight = 50.0, rest = 45)  // Panturrilha
        )

        // Treino Full Body
        val planFullBody = listOf(
            makePlan(11, sets = 3, reps = 10, weight = 40.0, rest = 90), // Agachamento
            makePlan(1, sets = 3, reps = 10, weight = 35.0, rest = 90),  // Supino Reto
            makePlan(6, sets = 3, reps = 10, weight = 40.0, rest = 60),  // Puxada Alta
            makePlan(19, sets = 3, reps = 10, weight = 14.0, rest = 60), // Desenvolvimento
            makePlan(22, sets = 3, reps = 10, weight = 18.0, rest = 60), // Rosca Direta
            makePlan(25, sets = 3, reps = 12, weight = 20.0, rest = 45), // Tríceps Corda
            makePlan(28, sets = 3, reps = 45, weight = 0.0, rest = 45)   // Prancha
        )

        return listOf(
            WorkoutTemplate(
                id = 1,
                title = "Treino A: Peito, Tríceps & Ombros",
                subtitle = "Divisão ABC - Foco em Empurrar & Superior",
                category = WorkoutCategory.HIPERTROFIA,
                defaultRestSeconds = 60,
                executionDurationMinutes = 55,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(planA),
                description = "Treino completo de peito, ombros anteriores/laterais e tríceps para máxima hipertrofia e definição."
            ),
            WorkoutTemplate(
                id = 2,
                title = "Treino B: Costas, Bíceps & Abdômen",
                subtitle = "Divisão ABC - Foco em Puxar & Core",
                category = WorkoutCategory.HIPERTROFIA,
                defaultRestSeconds = 60,
                executionDurationMinutes = 50,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(planB),
                description = "Desenvolvimento de dorsal em V, densidade de costas, bíceps e estabilização de core."
            ),
            WorkoutTemplate(
                id = 3,
                title = "Treino C: Pernas Completas & Panturrilha",
                subtitle = "Divisão ABC - Quadríceps, Posterior & Panturrilha",
                category = WorkoutCategory.HIPERTROFIA,
                defaultRestSeconds = 75,
                executionDurationMinutes = 60,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(planC),
                description = "Treino completo de membros inferiores com foco em força, volume muscular e simetria."
            ),
            WorkoutTemplate(
                id = 4,
                title = "Push Day (Empurrar Intenso)",
                subtitle = "Divisão Push/Pull/Legs",
                category = WorkoutCategory.FORCA,
                defaultRestSeconds = 90,
                executionDurationMinutes = 50,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(planPush),
                description = "Sobrecarga progressiva no Supino e Desenvolvimento militar para força bruta."
            ),
            WorkoutTemplate(
                id = 5,
                title = "Foco Inferiores & Glúteos",
                subtitle = "Hipertrofia Glúteos, Posteriores & Quadríceps",
                category = WorkoutCategory.FEMININO,
                defaultRestSeconds = 60,
                executionDurationMinutes = 55,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(planGluteos),
                description = "Protocolo de alta tensão mecânica com foco prioritário em glúteos e cadeia posterior."
            ),
            WorkoutTemplate(
                id = 6,
                title = "Full Body Condicionamento",
                subtitle = "Corpo Inteiro 3x na Semana",
                category = WorkoutCategory.FULLBODY,
                defaultRestSeconds = 60,
                executionDurationMinutes = 45,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(planFullBody),
                description = "Treino eficiente que trabalha todos os grandes grupos musculares na mesma sessão."
            )
        )
    }

    fun getDefaultExerciseTargets(): List<com.example.data.model.ExercisePerformanceTarget> {
        val today = java.time.LocalDate.now().toEpochDay()
        return listOf(
            com.example.data.model.ExercisePerformanceTarget(
                id = 1,
                exerciseId = 1,
                exerciseName = "Supino Reto com Barra",
                targetWeightKg = 100.0,
                targetReps = 8,
                currentWeightKg = 80.0,
                currentReps = 8,
                targetDateEpochDay = today + 60,
                isAchieved = false,
                notes = "Meta dos 100kg (3 dígitos) com técnica perfeita e amplitude completa.",
                createdAtEpochDay = today
            ),
            com.example.data.model.ExercisePerformanceTarget(
                id = 2,
                exerciseId = 9,
                exerciseName = "Agachamento Livre com Barra",
                targetWeightKg = 140.0,
                targetReps = 6,
                currentWeightKg = 110.0,
                currentReps = 6,
                targetDateEpochDay = today + 90,
                isAchieved = false,
                notes = "Foco em profundidade paralela e força nos quadríceps.",
                createdAtEpochDay = today
            ),
            com.example.data.model.ExercisePerformanceTarget(
                id = 3,
                exerciseId = 10,
                exerciseName = "Levantamento Terra (Deadlift)",
                targetWeightKg = 160.0,
                targetReps = 5,
                currentWeightKg = 130.0,
                currentReps = 5,
                targetDateEpochDay = today + 75,
                isAchieved = false,
                notes = "Fortalecimento de lombar, eretores e pegada pronada.",
                createdAtEpochDay = today
            ),
            com.example.data.model.ExercisePerformanceTarget(
                id = 4,
                exerciseId = 4,
                exerciseName = "Desenvolvimento com Halteres",
                targetWeightKg = 32.0,
                targetReps = 10,
                currentWeightKg = 26.0,
                currentReps = 10,
                targetDateEpochDay = today + 45,
                isAchieved = false,
                notes = "Ombros densos e controle escapular.",
                createdAtEpochDay = today
            )
        )
    }

    fun getDefaultWorkoutSessions(): List<com.example.data.model.WorkoutSession> {
        val today = java.time.LocalDate.now().toEpochDay()
        val now = System.currentTimeMillis()

        // Session 1 (2 days ago): Treino A - Peito, Tríceps & Ombros
        val session1Plans = listOf(
            WorkoutExercisePlan(
                exerciseId = 1,
                exerciseName = "Supino Reto com Barra",
                muscleGroup = MuscleGroup.PEITO.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 60.0, 10, true, 90),
                    ExerciseSetEntry(2, 60.0, 10, true, 90),
                    ExerciseSetEntry(3, 60.0, 10, true, 90)
                ),
                targetRestSeconds = 90,
                notes = "Execução perfeita, cadência 2-0-1 controlada."
            ),
            WorkoutExercisePlan(
                exerciseId = 2,
                exerciseName = "Supino Inclinado com Halteres",
                muscleGroup = MuscleGroup.PEITO.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 22.0, 10, true, 60),
                    ExerciseSetEntry(2, 22.0, 10, true, 60),
                    ExerciseSetEntry(3, 22.0, 9, true, 60)
                ),
                targetRestSeconds = 60,
                notes = "Última repetição próxima da falha concêntrica."
            ),
            WorkoutExercisePlan(
                exerciseId = 3,
                exerciseName = "Crossover na Polia Média",
                muscleGroup = MuscleGroup.PEITO.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 15.0, 12, true, 60),
                    ExerciseSetEntry(2, 15.0, 12, true, 60),
                    ExerciseSetEntry(3, 15.0, 12, true, 60)
                ),
                targetRestSeconds = 60,
                notes = "Pico de contração de 1s."
            ),
            WorkoutExercisePlan(
                exerciseId = 25,
                exerciseName = "Tríceps na Polia com Corda",
                muscleGroup = MuscleGroup.TRICEPS.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 25.0, 12, true, 45),
                    ExerciseSetEntry(2, 25.0, 12, true, 45),
                    ExerciseSetEntry(3, 25.0, 12, true, 45)
                ),
                targetRestSeconds = 45,
                notes = "Abertura máxima no final."
            )
        )

        // Session 2 (5 days ago): Treino B - Costas, Bíceps & Abdômen
        val session2Plans = listOf(
            WorkoutExercisePlan(
                exerciseId = 6,
                exerciseName = "Puxada Alta Frontal",
                muscleGroup = MuscleGroup.COSTAS.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 45.0, 10, true, 60),
                    ExerciseSetEntry(2, 45.0, 10, true, 60),
                    ExerciseSetEntry(3, 45.0, 10, true, 60),
                    ExerciseSetEntry(4, 45.0, 10, true, 60)
                ),
                targetRestSeconds = 60,
                notes = "Depressão escapular firme."
            ),
            WorkoutExercisePlan(
                exerciseId = 7,
                exerciseName = "Remada Curvada com Barra",
                muscleGroup = MuscleGroup.COSTAS.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 35.0, 8, true, 75),
                    ExerciseSetEntry(2, 35.0, 8, true, 75),
                    ExerciseSetEntry(3, 35.0, 8, true, 75)
                ),
                targetRestSeconds = 75,
                notes = "Tronco travado a 45 graus."
            ),
            WorkoutExercisePlan(
                exerciseId = 22,
                exerciseName = "Rosca Direta com Barra W",
                muscleGroup = MuscleGroup.BICEPS.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 20.0, 10, true, 60),
                    ExerciseSetEntry(2, 20.0, 10, true, 60),
                    ExerciseSetEntry(3, 20.0, 9, true, 60)
                ),
                targetRestSeconds = 60,
                notes = "Sem roubo com a lombar."
            )
        )

        // Session 3 (7 days ago): Treino A anterior
        val session3Plans = listOf(
            WorkoutExercisePlan(
                exerciseId = 1,
                exerciseName = "Supino Reto com Barra",
                muscleGroup = MuscleGroup.PEITO.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 60.0, 10, true, 90),
                    ExerciseSetEntry(2, 60.0, 9, true, 90),
                    ExerciseSetEntry(3, 60.0, 8, true, 90)
                ),
                targetRestSeconds = 90,
                notes = "Boa estabilidade."
            ),
            WorkoutExercisePlan(
                exerciseId = 2,
                exerciseName = "Supino Inclinado com Halteres",
                muscleGroup = MuscleGroup.PEITO.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 20.0, 10, true, 60),
                    ExerciseSetEntry(2, 20.0, 10, true, 60),
                    ExerciseSetEntry(3, 20.0, 10, true, 60)
                ),
                targetRestSeconds = 60,
                notes = "Transição para 22kg no próximo treino."
            )
        )

        return listOf(
            com.example.data.model.WorkoutSession(
                id = 1,
                templateId = 1,
                title = "Treino A: Peito, Tríceps & Ombros",
                dateEpochDay = today - 2,
                startTimeMillis = now - (2 * 86400000L) - 3600000L,
                endTimeMillis = now - (2 * 86400000L),
                durationSeconds = 3120, // 52 min
                location = "Academia Smart Fit",
                status = com.example.data.model.SessionStatus.COMPLETED,
                totalWeightLiftedKg = 3840.0,
                estimatedCalories = 380,
                perceivedExertion = 8,
                notes = "Treino muito produtivo. Bati 10 reps em todas as séries do Supino Reto com 60kg!",
                exercisesDoneJson = jsonAdapter.toJson(session1Plans),
                aiCaloricEvaluation = "Excelente intensidade mecânica. Sobrecarga progressiva recomendada no supino reto."
            ),
            com.example.data.model.WorkoutSession(
                id = 2,
                templateId = 2,
                title = "Treino B: Costas, Bíceps & Abdômen",
                dateEpochDay = today - 5,
                startTimeMillis = now - (5 * 86400000L) - 3000000L,
                endTimeMillis = now - (5 * 86400000L),
                durationSeconds = 2880, // 48 min
                location = "Academia Smart Fit",
                status = com.example.data.model.SessionStatus.COMPLETED,
                totalWeightLiftedKg = 3420.0,
                estimatedCalories = 340,
                perceivedExertion = 7,
                notes = "Costas ativadas com sucesso. Puxada alta fechada com 45kg em 4x10.",
                exercisesDoneJson = jsonAdapter.toJson(session2Plans),
                aiCaloricEvaluation = "Ótimo estímulo para dorsais e bíceps."
            ),
            com.example.data.model.WorkoutSession(
                id = 3,
                templateId = 1,
                title = "Treino A: Peito, Tríceps & Ombros",
                dateEpochDay = today - 7,
                startTimeMillis = now - (7 * 86400000L) - 3300000L,
                endTimeMillis = now - (7 * 86400000L),
                durationSeconds = 3000, // 50 min
                location = "Academia Smart Fit",
                status = com.example.data.model.SessionStatus.COMPLETED,
                totalWeightLiftedKg = 3520.0,
                estimatedCalories = 350,
                perceivedExertion = 8,
                notes = "Primeiro teste com 60kg no supino reto (10, 9, 8 reps).",
                exercisesDoneJson = jsonAdapter.toJson(session3Plans),
                aiCaloricEvaluation = "Treino sólido de hipertrofia."
            )
        )
    }
}
