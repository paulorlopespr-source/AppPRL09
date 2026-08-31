package com.example.data.local

import com.example.data.model.CardioRoutinePlan
import com.example.data.model.CardioType
import com.example.data.model.Equipment
import com.example.data.model.Exercise
import com.example.data.model.ExercisePerformanceTarget
import com.example.data.model.ExerciseSetEntry
import com.example.data.model.FitnessGoal
import com.example.data.model.IntensityLevel
import com.example.data.model.MuscleGroup
import com.example.data.model.PresetGoalRecommendation
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
            // Peitoral
            Exercise(
                id = 1,
                name = "Supino reto com barra",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.BARRA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 90,
                instructions = "Deite no banco, pés firmes no chão, pegada um pouco mais larga que os ombros. Desça a barra controladamente até o peitoral médio e empurre firmando a escápula.",
                executionTips = "Escápulas retraídas, pés firmes no chão e descida controlada da barra até a linha do peito."
            ),
            Exercise(
                id = 2,
                name = "Supino inclinado com halteres",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Banco regulado a 30-45 graus. Eleve os halteres em trajetória convergente controlando o alongamento do peitoral superior.",
                executionTips = "Mantenha o banco inclinado em 30 a 45 graus e evite bater os halteres no topo."
            ),
            Exercise(
                id = 3,
                name = "Crucifixo na máquina (Peck Deck)",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Ajuste o assento para que as manoplas fiquem na altura do peitoral. Feche os braços contraindo o peitoral.",
                executionTips = "Mantenha cotovelos levemente flexionados e sinta o alongamento das fibras peitorais."
            ),
            Exercise(
                id = 4,
                name = "Crossover no cabo médio/baixo",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.POLIA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Com o tronco levemente inclinado à frente, puxe os cabos aproximando as mãos à frente do peito.",
                executionTips = "Foque no pico de contração máxima ao cruzar as mãos à frente."
            ),
            Exercise(
                id = 5,
                name = "Supino inclinado articulado com anilhas",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.MAQUINA,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 75,
                instructions = "Ajuste a altura do banco. Empurre as alavancas estufando o peito e controle a descida.",
                executionTips = "Explore a amplitude máxima com contração sustentada no ápice do movimento."
            ),
            Exercise(
                id = 6,
                name = "Paralelas com sobrecarga (Dips)",
                muscleGroup = MuscleGroup.PEITO,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 75,
                instructions = "Incline o tronco levemente para a frente e desça flexionando os cotovelos até 90 graus.",
                executionTips = "Tronco inclinado para a frente para transferir a ênfase das fibras para o peitoral."
            ),

            // Ombros & Deltóides
            Exercise(
                id = 7,
                name = "Desenvolvimento com halteres sentado",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 75,
                instructions = "Sentado com encosto ereto, empurre os halteres acima da cabeça sem bater no topo.",
                executionTips = "Coluna totalmente apoiada no encosto, suba os halteres sem hiperestender a lombar."
            ),
            Exercise(
                id = 8,
                name = "Elevação lateral com halteres",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Em pé, eleve os braços lateralmente até a altura dos ombros.",
                executionTips = "Eleve até a linha dos ombros, cotovelos ligeiramente à frente do corpo."
            ),
            Exercise(
                id = 9,
                name = "Desenvolvimento militar com barra em pé",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 90,
                instructions = "Em pé, barra apoiada na clavícula, empurre verticalmente até travar acima da cabeça.",
                executionTips = "Glúteos e abdômen ativos para proteger a coluna lombar."
            ),
            Exercise(
                id = 10,
                name = "Elevação lateral na polia média",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.POLIA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Cabo ajustado na altura da coxa. Eleve lateralmente até a linha do ombro.",
                executionTips = "Tensão mecânica contínua do início ao fim do movimento."
            ),
            Exercise(
                id = 11,
                name = "Desenvolvimento Arnold com halteres",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.HALTERES,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 75,
                instructions = "Inicie com as palmas viradas para você e rotacione para fora enquanto empurra para cima.",
                executionTips = "Rotação fluida dos pulsos sincronizada com a elevação dos pesos."
            ),
            Exercise(
                id = 12,
                name = "Elevação lateral no cabo atrás do corpo",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.POLIA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 45,
                instructions = "Cabo passando por trás das costas. Eleve o braço lateralmente em plano escapular.",
                executionTips = "Tensão no ângulo de maior desvantagem mecânica do deltóide."
            ),
            Exercise(
                id = 13,
                name = "Crucifixo inverso na máquina",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Sente virado para o encosto e abra os braços para trás acionando o deltóide posterior.",
                executionTips = "Foco na aproximação das escápulas ao final do movimento."
            ),
            Exercise(
                id = 14,
                name = "Face Pull na polia alta com corda",
                muscleGroup = MuscleGroup.OMBROS,
                equipment = Equipment.POLIA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Polia alta com corda. Puxe em direção ao rosto abrindo os cotovelos para trás.",
                executionTips = "Puxe a corda na altura dos olhos e rotacione os ombros para trás externamente."
            ),

            // Tríceps
            Exercise(
                id = 15,
                name = "Tríceps no pulley com corda",
                muscleGroup = MuscleGroup.TRICEPS,
                equipment = Equipment.POLIA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "De frente para a polia alta, empurre a corda para baixo abrindo as pontas no final.",
                executionTips = "Cotovelos fixos ao lado do tronco, abra a corda no final da extensão."
            ),
            Exercise(
                id = 16,
                name = "Tríceps banco",
                muscleGroup = MuscleGroup.TRICEPS,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Mãos apoiadas na borda do banco atrás de você, desça flexionando os cotovelos até 90 graus.",
                executionTips = "Mantenha o tronco próximo ao banco para não sobrecarregar a articulação do ombro."
            ),
            Exercise(
                id = 17,
                name = "Tríceps testa com barra W",
                muscleGroup = MuscleGroup.TRICEPS,
                equipment = Equipment.BARRA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 75,
                instructions = "Deitado no banco, desça a barra W em direção à testa flexionando apenas os cotovelos.",
                executionTips = "Cotovelos estáveis apontados para o teto, sem abri-los lateralmente."
            ),
            Exercise(
                id = 18,
                name = "Tríceps francês unilateral com halter",
                muscleGroup = MuscleGroup.TRICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Braço estendido acima da cabeça, flexione o cotovelo descendo o halter atrás da nuca.",
                executionTips = "Alongue a porção posterior do braço sem forçar os ombros."
            ),
            Exercise(
                id = 19,
                name = "Tríceps testa conjugado com supino fechado",
                muscleGroup = MuscleGroup.TRICEPS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Faça a série de tríceps testa e, ao atingir a fadiga, continue imediatamente com supino pegada fechada.",
                executionTips = "Ao finalizar as repetições do testa, execute repetições do supino fechado imediatamente."
            ),
            Exercise(
                id = 20,
                name = "Tríceps coice na polia baixa unilateral",
                muscleGroup = MuscleGroup.TRICEPS,
                equipment = Equipment.POLIA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 45,
                instructions = "Tronco inclinado, estenda o braço para trás na polia baixa mantendo o cotovelo alto.",
                executionTips = "Pausa isométrica de 2 segundos na extensão completa do braço."
            ),

            // Costas & Dorsais
            Exercise(
                id = 21,
                name = "Puxada frontal na polia aberta",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.POLIA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 75,
                instructions = "Pegada aberta pronada. Puxe a barra em direção à clavícula inclinando o tronco levemente para trás.",
                executionTips = "Puxe a barra em direção à parte superior do peito, acionando as escápulas."
            ),
            Exercise(
                id = 22,
                name = "Remada baixa no cabo (triângulo)",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.POLIA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 75,
                instructions = "Sente ereto com os pés na plataforma. Puxe o triângulo até o abdômen estufando o peito.",
                executionTips = "Mantenha a coluna ereta e evite balançar o tronco durante a tração."
            ),
            Exercise(
                id = 23,
                name = "Remada unilateral com halter (serrote)",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Apoie joelho e mão no banco. Puxe o halter em direção ao quadril com o cotovelo colado.",
                executionTips = "Apoie joelho e mão no banco mantendo as costas paralelas ao chão."
            ),
            Exercise(
                id = 24,
                name = "Barra fixa (Pull-up) ou Graviton",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 90,
                instructions = "Pendure-se na barra e puxe o corpo para cima até o queixo ultrapassar a linha da barra.",
                executionTips = "Peito apontado para a barra, evite balanços e impulsos nas pernas."
            ),
            Exercise(
                id = 25,
                name = "Remada curvada com barra (pegada pronada)",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 90,
                instructions = "Tronco inclinado a 45 graus, coluna neutra. Puxe a barra em direção ao umbigo.",
                executionTips = "Tronco inclinado a 45 graus, puxando a barra em direção ao umbigo."
            ),
            Exercise(
                id = 26,
                name = "Puxador articulado pegada neutra",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Sente na máquina e puxe as manoplas verticalmente em pegada neutra.",
                executionTips = "Trabalhe amplitude máxima, sentindo a dorsal abrir e contrair."
            ),
            Exercise(
                id = 27,
                name = "Levantamento terra convencional (Deadlift)",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 6,
                defaultRestSeconds = 90,
                instructions = "Pés na largura dos quadris, barra colada nas canelas. Levante o peso estendendo quadris e joelhos simultaneamente.",
                executionTips = "Barra colada nas canelas, ativação de grande dorsal e extensão simultânea de quadril e joelhos."
            ),
            Exercise(
                id = 28,
                name = "Remada cavalinho na barra T com pegada neutra",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 75,
                instructions = "Posicione-se sobre a barra T, coluna reta, e puxe o peso até o abdômen.",
                executionTips = "Trabalho denso de costas com contração escapular total no topo."
            ),
            Exercise(
                id = 29,
                name = "Puxada alta unilateral no cabo",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.POLIA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Ajoelhado ou sentado, puxe o cabo unilateralmente focando no grande dorsal.",
                executionTips = "Puxe trazendo o cotovelo em direção ao bolso da calça para focar na porção ilíaca."
            ),
            Exercise(
                id = 30,
                name = "Encolhimento com barra pela frente",
                muscleGroup = MuscleGroup.COSTAS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Em pé, segure a barra à frente e eleve os ombros verticalmente em direção às orelhas.",
                executionTips = "Movimento estritamente vertical, nunca gire os ombros sob carga."
            ),

            // Bíceps
            Exercise(
                id = 31,
                name = "Rosca direta com barra W",
                muscleGroup = MuscleGroup.BICEPS,
                equipment = Equipment.BARRA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Em pé, segure a barra W na largura dos ombros. Flexione os cotovelos sem balançar o tronco.",
                executionTips = "Cotovelos colados ao lado do corpo, sem balançar o tronco para gerar impulso."
            ),
            Exercise(
                id = 32,
                name = "Rosca martelo com halteres",
                muscleGroup = MuscleGroup.BICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Segure os halteres com pegada neutra. Flexione os antebraços mantendo as palmas para dentro.",
                executionTips = "Palmas das mãos voltadas para dentro durante toda a execução."
            ),
            Exercise(
                id = 33,
                name = "Rosca alternada com halteres no banco inclinado",
                muscleGroup = MuscleGroup.BICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 75,
                instructions = "Deite no banco inclinado a 45 graus e faça a rosca alternada supinando os punhos.",
                executionTips = "Excelente alongamento da cabeça longa do bíceps no ponto inicial."
            ),
            Exercise(
                id = 34,
                name = "Rosca Scott na máquina",
                muscleGroup = MuscleGroup.BICEPS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Apoie os braços no suporte inclinado e flexione os cotovelos isolando o bíceps.",
                executionTips = "Isole o movimento mantendo o peito e axilas colados no apoio."
            ),
            Exercise(
                id = 35,
                name = "Rosca spider no banco inclinado",
                muscleGroup = MuscleGroup.BICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Deite de bruços no banco inclinado e realize a flexão de cotovelos com braços verticais.",
                executionTips = "Braços verticais perpendiculares ao chão, eliminando auxílio dos ombros."
            ),
            Exercise(
                id = 36,
                name = "Rosca inclinada 45 graus com halteres",
                muscleGroup = MuscleGroup.BICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "No banco a 45 graus, faça a rosca mantendo o braço recuado para máximo alongamento.",
                executionTips = "Supinação completa da mão ao longo de toda a subida."
            ),

            // Pernas & Quadríceps
            Exercise(
                id = 37,
                name = "Leg Press 45 graus",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 90,
                instructions = "Pés na largura dos ombros no meio da plataforma. Desça até 90 graus sem descolar a lombar.",
                executionTips = "Pés na largura dos ombros, não estenda os joelhos até o travamento total."
            ),
            Exercise(
                id = 38,
                name = "Agachamento taça (Goblet Squat)",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 75,
                instructions = "Segure um halter rente ao peito. Agache com os cotovelos passando por dentro dos joelhos.",
                executionTips = "Segure o halter rente ao peito e mantenha os joelhos alinhados com a ponta dos pés."
            ),
            Exercise(
                id = 39,
                name = "Cadeira extensora",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Ajuste o rolo nos tornozelos. Estenda os joelhos até a contração total dos quadríceps.",
                executionTips = "Faça uma pausa de 1 segundo no topo em contração máxima."
            ),
            Exercise(
                id = 40,
                name = "Agachamento livre com barra",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 90,
                instructions = "Barra apoiada no trapézio. Desça flexionando joelhos e quadril até quebrar a paralela.",
                executionTips = "Trave o core com respiração diafragmática e desça até quebrar o paralelo com segurança."
            ),
            Exercise(
                id = 41,
                name = "Afundo com halteres (passada)",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Dê passos à frente descendo o joelho traseiro em direção ao chão a 90 graus.",
                executionTips = "Passos firmes mantendo o tronco ereto e joelho de trás descendo em 90 graus."
            ),
            Exercise(
                id = 42,
                name = "Agachamento livre profundo (Back Squat)",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.BARRA,
                defaultSets = 5,
                defaultReps = 6,
                defaultRestSeconds = 90,
                instructions = "Barra bem firme no trapézio, agachamento profundo mantendo a coluna neutra.",
                executionTips = "Posição de pés personalizada, mantendo o tronco o mais verticalizado possível."
            ),
            Exercise(
                id = 43,
                name = "Agachamento búlgaro com halteres",
                muscleGroup = MuscleGroup.QUADRICEPS,
                equipment = Equipment.HALTERES,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 60,
                instructions = "Um pé apoiado no banco atrás. Desça controladamente até quase tocar o joelho no chão.",
                executionTips = "Maior carga na perna da frente, controle a descida para evitar impacto no joelho traseiro."
            ),

            // Posterior & Glúteos
            Exercise(
                id = 44,
                name = "Mesa flexora",
                muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Deitado de bruços, flexione os joelhos trazendo os calcanhares em direção aos glúteos.",
                executionTips = "Mantenha o quadril bem apoiado no estofado durante a flexão dos joelhos."
            ),
            Exercise(
                id = 45,
                name = "RDL (Stiff com halteres)",
                muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS,
                equipment = Equipment.HALTERES,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 75,
                instructions = "Pés paralelos, incline o tronco empurrando o quadril para trás sentindo o posterior alongar.",
                executionTips = "Empurre o quadril para trás mantendo os joelhos semiflexionados e coluna neutra."
            ),
            Exercise(
                id = 46,
                name = "Cadeira flexora",
                muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Sentado na máquina, flexione os joelhos para baixo com amplitude completa.",
                executionTips = "Controle a volta do peso sem deixar as placas tocarem o suporte."
            ),
            Exercise(
                id = 47,
                name = "Elevação pélvica com barra (Hip Thrust)",
                muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS,
                equipment = Equipment.BARRA,
                defaultSets = 4,
                defaultReps = 8,
                defaultRestSeconds = 75,
                instructions = "Costas no banco, barra sobre a bacia com almofada. Eleve os quadris contraindo os glúteos.",
                executionTips = "Queixo no peito, contraia os glúteos intensamente no ponto máximo por 2 segundos."
            ),
            Exercise(
                id = 48,
                name = "Mesa flexora com drop-set na última série",
                muscleGroup = MuscleGroup.POSTERIOR_GLUTEOS,
                equipment = Equipment.MAQUINA,
                defaultSets = 4,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Execute as séries normais e na última reduza a carga em 30% após a falha.",
                executionTips = "Na última série, reduza 30% da carga após a falha e continue até nova exaustão."
            ),

            // Panturrilhas
            Exercise(
                id = 49,
                name = "Elevação de panturrilhas em pé na máquina",
                muscleGroup = MuscleGroup.PANTURRILHA,
                equipment = Equipment.MAQUINA,
                defaultSets = 3,
                defaultReps = 15,
                defaultRestSeconds = 60,
                instructions = "Na ponta dos pés, desça alongando os calcanhares e suba até a contração máxima.",
                executionTips = "Amplitude completa, alongando bem no ponto mais baixo."
            ),
            Exercise(
                id = 50,
                name = "Gêmeos sentado (panturrilha sóleo)",
                muscleGroup = MuscleGroup.PANTURRILHA,
                equipment = Equipment.MAQUINA,
                defaultSets = 4,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Sentado na máquina, apoie as coxas sob as almofadas e faça a flexão plantar completa.",
                executionTips = "Segure 2 segundos no ponto mais alto e alongue totalmente na descida."
            ),
            Exercise(
                id = 51,
                name = "Gêmeos no Leg Press com pausa",
                muscleGroup = MuscleGroup.PANTURRILHA,
                equipment = Equipment.MAQUINA,
                defaultSets = 4,
                defaultReps = 15,
                defaultRestSeconds = 45,
                instructions = "Apoie a ponta dos pés na borda inferior da plataforma e flexione os tornozelos com pausa.",
                executionTips = "Pausa de 2 segundos no ponto de maior alongamento para eliminar energia elástica do tendão."
            ),

            // Abdômen & Core
            Exercise(
                id = 52,
                name = "Abdominal supra no solo",
                muscleGroup = MuscleGroup.ABDOMEN,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 3,
                defaultReps = 15,
                defaultRestSeconds = 45,
                instructions = "Deitado de costas, flexione o tronco expirando e elevando as escápulas.",
                executionTips = "Eleve apenas as escápulas do chão expirando todo o ar."
            ),
            Exercise(
                id = 53,
                name = "Prancha isométrica frontal",
                muscleGroup = MuscleGroup.ABDOMEN,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 3,
                defaultReps = 30, // segundos
                defaultRestSeconds = 45,
                instructions = "Apoiado nos antebraços e ponta dos pés, mantenha a coluna alinhada e abdômen travado.",
                executionTips = "Mantenha alinhamento de cabeça, coluna e glúteos com abdômen contraído."
            ),
            Exercise(
                id = 54,
                name = "Elevação de pernas na barra fixa ou paralela",
                muscleGroup = MuscleGroup.ABDOMEN,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 3,
                defaultReps = 12,
                defaultRestSeconds = 60,
                instructions = "Suspenso na barra ou paralelas, eleve os joelhos enrolando a bacia em direção ao peito.",
                executionTips = "Enrole o quadril em direção ao tronco, não faça apenas flexão de pernas."
            ),
            Exercise(
                id = 55,
                name = "Abdominal na roda (Ab Wheel)",
                muscleGroup = MuscleGroup.ABDOMEN,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 3,
                defaultReps = 10,
                defaultRestSeconds = 60,
                instructions = "Ajoelhado, role a roda para a frente estendendo o tronco e retorne contraindo o abdômen.",
                executionTips = "Retorne usando a força do abdômen, sem puxar com os quadris."
            ),
            Exercise(
                id = 56,
                name = "Dragon Flag no banco plano",
                muscleGroup = MuscleGroup.ABDOMEN,
                equipment = Equipment.PESO_CORPO,
                defaultSets = 3,
                defaultReps = 6,
                defaultRestSeconds = 60,
                instructions = "Segure o banco atrás da cabeça e eleve todo o corpo apoiando apenas nas escápulas.",
                executionTips = "Mantenha o corpo como uma prancha reta durante a descida excêntrica."
            ),
            Exercise(
                id = 57,
                name = "Abdominal no cabo com corda ajoelhado",
                muscleGroup = MuscleGroup.ABDOMEN,
                equipment = Equipment.POLIA,
                defaultSets = 4,
                defaultReps = 12,
                defaultRestSeconds = 45,
                instructions = "Ajoelhado de frente para a polia, segure a corda junto à cabeça e flexione o tronco para baixo.",
                executionTips = "Flexione a coluna vertebral aproximando as costelas da bacia com carga progressiva."
            )
        )
    }

    fun getDefaultWorkoutTemplates(exercises: List<Exercise>): List<WorkoutTemplate> {
        val exByName = exercises.associateBy { it.name.trim().lowercase() }
        val exById = exercises.associateBy { it.id }

        fun makePlan(
            id: Long,
            name: String,
            group: MuscleGroup,
            setsCount: Int,
            reps: Int,
            weight: Double,
            restSec: Int,
            tip: String
        ): WorkoutExercisePlan {
            val ex = exById[id] ?: exByName[name.trim().lowercase()]
            val realName = ex?.name ?: name
            val realGroup = ex?.muscleGroup?.displayName ?: group.displayName
            val realTip = ex?.executionTips ?: tip

            val setList = (1..setsCount).map {
                ExerciseSetEntry(
                    setNumber = it,
                    weightKg = weight,
                    reps = reps,
                    isCompleted = false,
                    restSeconds = restSec
                )
            }

            return WorkoutExercisePlan(
                exerciseId = id,
                exerciseName = realName,
                muscleGroup = realGroup,
                sets = setList,
                targetRestSeconds = restSec,
                notes = realTip
            )
        }

        // ==========================================
        // 1. INICIANTE - DIVISÃO ABC (3x/semana)
        // ==========================================
        val inicianteA = listOf(
            makePlan(1, "Supino reto com barra", MuscleGroup.PEITO, 3, 10, 40.0, 90, "Escápulas retraídas, pés firmes no chão"),
            makePlan(2, "Supino inclinado com halteres", MuscleGroup.PEITO, 3, 10, 16.0, 60, "Mantenha o banco inclinado em 30 a 45 graus"),
            makePlan(3, "Crucifixo na máquina (Peck Deck)", MuscleGroup.PEITO, 3, 12, 35.0, 60, "Mantenha cotovelos levemente flexionados"),
            makePlan(7, "Desenvolvimento com halteres sentado", MuscleGroup.OMBROS, 3, 10, 12.0, 75, "Coluna totalmente apoiada no encosto"),
            makePlan(8, "Elevação lateral com halteres", MuscleGroup.OMBROS, 3, 12, 8.0, 60, "Eleve até a linha dos ombros"),
            makePlan(15, "Tríceps no pulley com corda", MuscleGroup.TRICEPS, 3, 12, 20.0, 60, "Cotovelos fixos ao lado do tronco"),
            makePlan(16, "Tríceps banco", MuscleGroup.TRICEPS, 3, 10, 0.0, 60, "Mantenha o tronco próximo ao banco")
        )

        val inicianteB = listOf(
            makePlan(21, "Puxada frontal na polia aberta", MuscleGroup.COSTAS, 3, 10, 40.0, 75, "Puxe a barra em direção à parte superior do peito"),
            makePlan(22, "Remada baixa no cabo (triângulo)", MuscleGroup.COSTAS, 3, 10, 35.0, 75, "Mantenha a coluna ereta e evite balançar"),
            makePlan(23, "Remada unilateral com halter (serrote)", MuscleGroup.COSTAS, 3, 10, 14.0, 60, "Apoie joelho e mão no banco"),
            makePlan(13, "Crucifixo inverso na máquina", MuscleGroup.OMBROS, 3, 12, 25.0, 60, "Foco na aproximação das escápulas"),
            makePlan(31, "Rosca direta com barra W", MuscleGroup.BICEPS, 3, 10, 18.0, 60, "Cotovelos colados ao lado do corpo"),
            makePlan(32, "Rosca martelo com halteres", MuscleGroup.BICEPS, 3, 10, 10.0, 60, "Palmas das mãos voltadas para dentro")
        )

        val inicianteC = listOf(
            makePlan(37, "Leg Press 45 graus", MuscleGroup.QUADRICEPS, 3, 10, 100.0, 90, "Pés na largura dos ombros"),
            makePlan(38, "Agachamento taça (Goblet Squat)", MuscleGroup.QUADRICEPS, 3, 10, 16.0, 75, "Segure o halter rente ao peito"),
            makePlan(39, "Cadeira extensora", MuscleGroup.QUADRICEPS, 3, 12, 40.0, 60, "Faça uma pausa de 1s no topo"),
            makePlan(44, "Mesa flexora", MuscleGroup.POSTERIOR_GLUTEOS, 3, 12, 30.0, 60, "Mantenha o quadril bem apoiado"),
            makePlan(49, "Elevação de panturrilhas em pé na máquina", MuscleGroup.PANTURRILHA, 3, 15, 50.0, 60, "Amplitude completa"),
            makePlan(52, "Abdominal supra no solo", MuscleGroup.ABDOMEN, 3, 15, 0.0, 45, "Eleve apenas as escápulas expirando"),
            makePlan(53, "Prancha isométrica frontal", MuscleGroup.ABDOMEN, 3, 30, 0.0, 45, "Alinhamento e abdômen contraído")
        )

        // ==========================================
        // 2. INTERMEDIÁRIO - DIVISÃO ABC (4x/semana)
        // ==========================================
        val intermediarioA = listOf(
            makePlan(1, "Supino reto com barra", MuscleGroup.PEITO, 4, 8, 50.0, 90, "Ponte torácica controlada, pegada firme"),
            makePlan(2, "Supino inclinado com halteres", MuscleGroup.PEITO, 4, 10, 20.0, 75, "Controle a fase excêntrica em 2 a 3 segundos"),
            makePlan(4, "Crossover no cabo médio/baixo", MuscleGroup.PEITO, 3, 10, 15.0, 60, "Foque no pico de contração máxima"),
            makePlan(9, "Desenvolvimento militar com barra em pé", MuscleGroup.OMBROS, 4, 8, 30.0, 90, "Glúteos e abdômen ativos"),
            makePlan(10, "Elevação lateral na polia média", MuscleGroup.OMBROS, 3, 10, 8.0, 60, "Tensão mecânica contínua"),
            makePlan(17, "Tríceps testa com barra W", MuscleGroup.TRICEPS, 3, 10, 22.0, 75, "Cotovelos estáveis apontados para o teto"),
            makePlan(18, "Tríceps francês unilateral com halter", MuscleGroup.TRICEPS, 3, 10, 10.0, 60, "Alongue a porção posterior do braço")
        )

        val intermediarioB = listOf(
            makePlan(24, "Barra fixa (Pull-up) ou Graviton", MuscleGroup.COSTAS, 4, 8, 0.0, 90, "Peito apontado para a barra"),
            makePlan(25, "Remada curvada com barra (pegada pronada)", MuscleGroup.COSTAS, 4, 8, 40.0, 90, "Tronco inclinado a 45 graus"),
            makePlan(26, "Puxador articulado pegada neutra", MuscleGroup.COSTAS, 3, 10, 45.0, 60, "Trabalhe amplitude máxima"),
            makePlan(14, "Face Pull na polia alta com corda", MuscleGroup.OMBROS, 3, 12, 20.0, 60, "Puxe na altura dos olhos e rotacione"),
            makePlan(33, "Rosca alternada com halteres no banco inclinado", MuscleGroup.BICEPS, 3, 10, 12.0, 75, "Excelente alongamento da cabeça longa"),
            makePlan(34, "Rosca Scott na máquina", MuscleGroup.BICEPS, 3, 10, 25.0, 60, "Isole o movimento colando as axilas")
        )

        val intermediarioC = listOf(
            makePlan(40, "Agachamento livre com barra", MuscleGroup.QUADRICEPS, 4, 8, 60.0, 90, "Trave o core e desça até quebrar o paralelo"),
            makePlan(45, "RDL (Stiff com halteres)", MuscleGroup.POSTERIOR_GLUTEOS, 4, 8, 22.0, 75, "Empurre o quadril para trás"),
            makePlan(41, "Afundo com halteres (passada)", MuscleGroup.QUADRICEPS, 3, 10, 14.0, 60, "Passos firmes mantendo o tronco ereto"),
            makePlan(46, "Cadeira flexora", MuscleGroup.POSTERIOR_GLUTEOS, 3, 10, 40.0, 60, "Controle a volta do peso"),
            makePlan(50, "Gêmeos sentado (panturrilha sóleo)", MuscleGroup.PANTURRILHA, 4, 12, 45.0, 60, "Segure 2s no ponto mais alto"),
            makePlan(54, "Elevação de pernas na barra fixa ou paralela", MuscleGroup.ABDOMEN, 3, 12, 0.0, 60, "Enrole o quadril em direção ao tronco"),
            makePlan(55, "Abdominal na roda (Ab Wheel)", MuscleGroup.ABDOMEN, 3, 10, 0.0, 60, "Retorne usando a força do abdômen")
        )

        // ==========================================
        // 3. AVANÇADO - DIVISÃO ABC (5-6x/semana)
        // ==========================================
        val avancadoA = listOf(
            makePlan(1, "Supino reto com barra", MuscleGroup.PEITO, 5, 6, 70.0, 90, "Leg drive sincronizado com fase concêntrica explosiva"),
            makePlan(5, "Supino inclinado articulado com anilhas", MuscleGroup.PEITO, 4, 8, 35.0, 75, "Amplitude máxima com contração sustentada"),
            makePlan(6, "Paralelas com sobrecarga (Dips)", MuscleGroup.PEITO, 4, 8, 10.0, 75, "Tronco inclinado para frente"),
            makePlan(11, "Desenvolvimento Arnold com halteres", MuscleGroup.OMBROS, 4, 8, 20.0, 75, "Rotação fluida dos pulsos"),
            makePlan(12, "Elevação lateral no cabo atrás do corpo", MuscleGroup.OMBROS, 4, 10, 10.0, 45, "Tensão no ângulo de maior desvantagem"),
            makePlan(19, "Tríceps testa conjugado com supino fechado", MuscleGroup.TRICEPS, 4, 10, 26.0, 60, "Ao fadigar o testa, siga no supino fechado"),
            makePlan(20, "Tríceps coice na polia baixa unilateral", MuscleGroup.TRICEPS, 3, 12, 12.0, 45, "Pausa isométrica de 2 segundos")
        )

        val avancadoB = listOf(
            makePlan(27, "Levantamento terra convencional (Deadlift)", MuscleGroup.COSTAS, 4, 6, 100.0, 90, "Barra colada nas canelas e extensão simultânea"),
            makePlan(28, "Remada cavalinho na barra T com pegada neutra", MuscleGroup.COSTAS, 4, 8, 50.0, 75, "Trabalho denso de costas com contração escapular"),
            makePlan(29, "Puxada alta unilateral no cabo", MuscleGroup.COSTAS, 4, 10, 25.0, 60, "Traga o cotovelo em direção ao bolso"),
            makePlan(30, "Encolhimento com barra pela frente", MuscleGroup.COSTAS, 4, 10, 60.0, 60, "Movimento estritamente vertical"),
            makePlan(35, "Rosca spider no banco inclinado", MuscleGroup.BICEPS, 4, 10, 14.0, 60, "Braços verticais perpendiculares ao chão"),
            makePlan(36, "Rosca inclinada 45 graus com halteres", MuscleGroup.BICEPS, 4, 10, 14.0, 60, "Supinação completa da mão ao subir")
        )

        val avancadoC = listOf(
            makePlan(42, "Agachamento livre profundo (Back Squat)", MuscleGroup.QUADRICEPS, 5, 6, 80.0, 90, "Tronco verticalizado e profundidade máxima"),
            makePlan(47, "Elevação pélvica com barra (Hip Thrust)", MuscleGroup.POSTERIOR_GLUTEOS, 4, 8, 80.0, 75, "Queixo no peito e contração de 2s"),
            makePlan(43, "Agachamento búlgaro com halteres", MuscleGroup.QUADRICEPS, 4, 8, 18.0, 60, "Maior carga na perna da frente"),
            makePlan(48, "Mesa flexora com drop-set na última série", MuscleGroup.POSTERIOR_GLUTEOS, 4, 10, 45.0, 60, "Reduza 30% da carga após a falha"),
            makePlan(51, "Gêmeos no Leg Press com pausa", MuscleGroup.PANTURRILHA, 4, 15, 80.0, 45, "Pausa de 2s no maior alongamento"),
            makePlan(56, "Dragon Flag no banco plano", MuscleGroup.ABDOMEN, 3, 6, 0.0, 60, "Corpo como prancha na descida excêntrica"),
            makePlan(57, "Abdominal no cabo com corda ajoelhado", MuscleGroup.ABDOMEN, 4, 12, 35.0, 45, "Flexione a coluna vertebral com carga")
        )

        // Outros templates clássicos
        val planPush = listOf(
            makePlan(1, "Supino reto com barra", MuscleGroup.PEITO, 4, 8, 45.0, 90, "Retração escapular"),
            makePlan(2, "Supino inclinado com halteres", MuscleGroup.PEITO, 3, 10, 22.0, 60, "Controle excêntrico"),
            makePlan(3, "Crucifixo na máquina (Peck Deck)", MuscleGroup.PEITO, 3, 12, 40.0, 60, "Isolamento"),
            makePlan(7, "Desenvolvimento com halteres sentado", MuscleGroup.OMBROS, 4, 10, 18.0, 60, "Press firme"),
            makePlan(8, "Elevação lateral com halteres", MuscleGroup.OMBROS, 4, 15, 8.0, 45, "Cadência"),
            makePlan(15, "Tríceps no pulley com corda", MuscleGroup.TRICEPS, 4, 12, 25.0, 45, "Extensão total")
        )

        val planGluteos = listOf(
            makePlan(47, "Elevação pélvica com barra (Hip Thrust)", MuscleGroup.POSTERIOR_GLUTEOS, 4, 10, 60.0, 90, "Foco nos glúteos"),
            makePlan(43, "Agachamento búlgaro com halteres", MuscleGroup.QUADRICEPS, 3, 10, 12.0, 60, "Passada profunda"),
            makePlan(37, "Leg Press 45 graus", MuscleGroup.QUADRICEPS, 4, 12, 100.0, 75, "Pés altos na plataforma"),
            makePlan(45, "RDL (Stiff com halteres)", MuscleGroup.POSTERIOR_GLUTEOS, 4, 10, 20.0, 60, "Alongamento de posteriores"),
            makePlan(49, "Elevação de panturrilhas em pé na máquina", MuscleGroup.PANTURRILHA, 4, 15, 50.0, 45, "Pausa no topo")
        )

        val planFullBody = listOf(
            makePlan(40, "Agachamento livre com barra", MuscleGroup.QUADRICEPS, 3, 10, 40.0, 90, "Agache até paralelo"),
            makePlan(1, "Supino reto com barra", MuscleGroup.PEITO, 3, 10, 35.0, 90, "Peito estufado"),
            makePlan(21, "Puxada frontal na polia aberta", MuscleGroup.COSTAS, 3, 10, 40.0, 60, "Dorsal aberta"),
            makePlan(7, "Desenvolvimento com halteres sentado", MuscleGroup.OMBROS, 3, 10, 14.0, 60, "Estabilidade"),
            makePlan(31, "Rosca direta com barra W", MuscleGroup.BICEPS, 3, 10, 18.0, 60, "Bíceps"),
            makePlan(15, "Tríceps no pulley com corda", MuscleGroup.TRICEPS, 3, 12, 20.0, 45, "Tríceps"),
            makePlan(53, "Prancha isométrica frontal", MuscleGroup.ABDOMEN, 3, 45, 0.0, 45, "Core")
        )

        return listOf(
            // 1. Iniciante ABC
            WorkoutTemplate(
                id = 1,
                title = "Iniciante - Treino A: Peito, Ombros e Tríceps",
                subtitle = "Divisão ABC (3x/sem) • 3 séries • 10-15 reps",
                category = WorkoutCategory.INICIANTE,
                defaultRestSeconds = 60,
                executionDurationMinutes = 45,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(inicianteA),
                description = "Aquecimento: 5-10 min mobilidade articular e esteira leve. Foco na adaptação neural e padrão biomecânico correto. Alongamento final estático."
            ),
            WorkoutTemplate(
                id = 2,
                title = "Iniciante - Treino B: Costas e Bíceps",
                subtitle = "Divisão ABC (3x/sem) • 3 séries • 10-12 reps",
                category = WorkoutCategory.INICIANTE,
                defaultRestSeconds = 60,
                executionDurationMinutes = 45,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(inicianteB),
                description = "Aquecimento: 5-10 min de aquecimento geral. Desenvolvimento de puxada e fortalecimento de dorsais e braquiais. Alongamento final suave."
            ),
            WorkoutTemplate(
                id = 3,
                title = "Iniciante - Treino C: Pernas, Glúteos e Abdômen",
                subtitle = "Divisão ABC (3x/sem) • 3 séries • 10-20 reps",
                category = WorkoutCategory.INICIANTE,
                defaultRestSeconds = 60,
                executionDurationMinutes = 50,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(inicianteC),
                description = "Aquecimento: 5-10 min bike/esteira e mobilidade de tornozelo/quadril. Fortalecimento de membros inferiores e estabilização de core."
            ),

            // 2. Intermediário ABC
            WorkoutTemplate(
                id = 4,
                title = "Intermediário - Treino A: Peito, Ombros e Tríceps",
                subtitle = "Divisão ABC (4x/sem) • 3-4 séries • 8-12 reps",
                category = WorkoutCategory.INTERMEDIARIO,
                defaultRestSeconds = 75,
                executionDurationMinutes = 55,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(intermediarioA),
                description = "Aquecimento: 8 min com rotação articular e séries de ativação. Sobrecarga progressiva no supino e desenvolvimento militar."
            ),
            WorkoutTemplate(
                id = 5,
                title = "Intermediário - Treino B: Costas e Bíceps",
                subtitle = "Divisão ABC (4x/sem) • 3-4 séries • 6-15 reps",
                category = WorkoutCategory.INTERMEDIARIO,
                defaultRestSeconds = 75,
                executionDurationMinutes = 55,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(intermediarioB),
                description = "Aquecimento: 8 min dinâmico. Puxadas verticais e horizontais pesadas com remada curvada e barra fixa para densidade muscular."
            ),
            WorkoutTemplate(
                id = 6,
                title = "Intermediário - Treino C: Pernas, Glúteos e Abdômen",
                subtitle = "Divisão ABC (4x/sem) • 3-4 séries • 8-15 reps",
                category = WorkoutCategory.INTERMEDIARIO,
                defaultRestSeconds = 75,
                executionDurationMinutes = 60,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(intermediarioC),
                description = "Aquecimento: Mobilidade articular e ativação de glúteos. Agachamento livre com barra, RDL, passada e trabalho de panturrilha/core."
            ),

            // 3. Avançado ABC
            WorkoutTemplate(
                id = 7,
                title = "Avançado - Treino A: Peito, Ombros e Tríceps",
                subtitle = "Divisão ABC (5-6x/sem) • 4-5 séries • 6-15 reps",
                category = WorkoutCategory.AVANCADO,
                defaultRestSeconds = 75,
                executionDurationMinutes = 65,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(avancadoA),
                description = "Aquecimento: 10 min liberação miofascial e séries de rampa. Alta intensidade mecânica, paralelas com sobrecarga e técnicas avançadas de tríceps."
            ),
            WorkoutTemplate(
                id = 8,
                title = "Avançado - Treino B: Costas e Bíceps",
                subtitle = "Divisão ABC (5-6x/sem) • 4 séries • 6-12 reps",
                category = WorkoutCategory.AVANCADO,
                defaultRestSeconds = 75,
                executionDurationMinutes = 65,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(avancadoB),
                description = "Aquecimento: 10 min dinâmico. Levantamento terra convencional, remada cavalinho pesada e isolamento cirúrgico de bíceps spider e inclinado."
            ),
            WorkoutTemplate(
                id = 9,
                title = "Avançado - Treino C: Pernas, Glúteos e Abdômen",
                subtitle = "Divisão ABC (5-6x/sem) • 4-5 séries • 6-20 reps",
                category = WorkoutCategory.AVANCADO,
                defaultRestSeconds = 75,
                executionDurationMinutes = 70,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(avancadoC),
                description = "Aquecimento: Aquecimento específico e séries progressivas. Back Squat profundo, elevação pélvica pesada, drop-sets e Dragon Flag."
            ),

            // Templates adicionais de apoio
            WorkoutTemplate(
                id = 10,
                title = "Push Day (Empurrar Intenso)",
                subtitle = "Divisão Push/Pull/Legs",
                category = WorkoutCategory.FORCA,
                defaultRestSeconds = 90,
                executionDurationMinutes = 50,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(planPush),
                description = "Sobrecarga progressiva no Supino e Desenvolvimento para ganho de força máxima."
            ),
            WorkoutTemplate(
                id = 11,
                title = "Foco Inferiores & Glúteos",
                subtitle = "Hipertrofia Glúteos & Posteriores",
                category = WorkoutCategory.FEMININO,
                defaultRestSeconds = 60,
                executionDurationMinutes = 55,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(planGluteos),
                description = "Protocolo de alta tensão mecânica com foco prioritário em glúteos e cadeia posterior."
            ),
            WorkoutTemplate(
                id = 12,
                title = "Full Body Condicionamento",
                subtitle = "Corpo Inteiro 3x na Semana",
                category = WorkoutCategory.FULLBODY,
                defaultRestSeconds = 60,
                executionDurationMinutes = 45,
                isPreset = true,
                exercisesJson = jsonAdapter.toJson(planFullBody),
                description = "Treino eficiente trabalhando todos os grandes grupos musculares na mesma sessão."
            )
        )
    }

    // ==========================================
    // ROTINAS DE CARDIO (HIIT & LISS)
    // ==========================================
    fun getDefaultCardioRoutines(): List<CardioRoutinePlan> {
        return listOf(
            CardioRoutinePlan(
                id = "hiit_iniciante",
                tipo = "HIIT",
                nivel = "Iniciante",
                duracaoMinutos = 18,
                estrutura = "20s esforço / 40s descanso",
                rodadas = 8,
                frequenciaSemanal = 2,
                exercicios = listOf(
                    "Polichinelo",
                    "Elevação de joelhos no lugar",
                    "Agachamento com peso corporal",
                    "Passada alternada sem carga"
                ),
                observacaoSeguranca = "Mantenha o esforço em torno de 80% da sua capacidade. Hidrate-se e interrompa caso sinta tontura.",
                cardioType = CardioType.CORRIDA,
                intensity = IntensityLevel.MODERADA
            ),
            CardioRoutinePlan(
                id = "hiit_intermediario",
                tipo = "HIIT",
                nivel = "Intermediário",
                duracaoMinutos = 22,
                estrutura = "30s esforço / 30s descanso",
                rodadas = 10,
                frequenciaSemanal = 3,
                exercicios = listOf(
                    "Burpee adaptado",
                    "Mountain climber",
                    "Corrida no lugar com joelhos altos",
                    "Agachamento com salto (Jump Squat)"
                ),
                observacaoSeguranca = "Aqueça previamente por 3 minutos e monitore a frequência cardíaca para respeitar o tempo de recuperação.",
                cardioType = CardioType.CORRIDA,
                intensity = IntensityLevel.INTENSA
            ),
            CardioRoutinePlan(
                id = "hiit_avancado",
                tipo = "HIIT",
                nivel = "Avançado",
                duracaoMinutos = 28,
                estrutura = "40s esforço / 20s descanso",
                rodadas = 14,
                frequenciaSemanal = 3,
                exercicios = listOf(
                    "Burpee completo com flexão",
                    "Mountain climber veloz",
                    "Agachamento com salto pliométrico",
                    "Sprints na esteira ou corda naval",
                    "Tuck Jumps (saltos com joelhos no peito)"
                ),
                observacaoSeguranca = "Treino de alta demanda cardiovascular (>90% FCmáx). Indicado apenas para praticantes adaptados.",
                cardioType = CardioType.CORRIDA,
                intensity = IntensityLevel.INTENSA
            ),
            CardioRoutinePlan(
                id = "liss_iniciante",
                tipo = "LISS",
                nivel = "Iniciante",
                duracaoMinutos = 30,
                estrutura = "Ritmo contínuo constante (60-65% FCmáx)",
                rodadas = 1,
                frequenciaSemanal = 2,
                exercicios = listOf(
                    "Caminhada rápida em esteira plana",
                    "Bicicleta ergométrica com carga leve"
                ),
                observacaoSeguranca = "Mantenha uma intensidade que permita conversar sem perder o fôlego excessivamente.",
                cardioType = CardioType.CAMINHADA_ESTEIRA,
                intensity = IntensityLevel.LEVE
            ),
            CardioRoutinePlan(
                id = "liss_intermediario",
                tipo = "LISS",
                nivel = "Intermediário",
                duracaoMinutos = 40,
                estrutura = "Ritmo contínuo constante (65-70% FCmáx)",
                rodadas = 1,
                frequenciaSemanal = 3,
                exercicios = listOf(
                    "Caminhada inclinada na esteira (inclinação 4% a 8%)",
                    "Bicicleta ergométrica com resistência moderada",
                    "Elíptico"
                ),
                observacaoSeguranca = "Mantenha postura alinhada e evite apoiar todo o peso do corpo nos braços do aparelho.",
                cardioType = CardioType.CAMINHADA_ESTEIRA,
                intensity = IntensityLevel.MODERADA
            ),
            CardioRoutinePlan(
                id = "liss_avancado",
                tipo = "LISS",
                nivel = "Avançado",
                duracaoMinutos = 45,
                estrutura = "Ritmo contínuo constante (70-75% FCmáx)",
                rodadas = 1,
                frequenciaSemanal = 3,
                exercicios = listOf(
                    "Simulador de escada (Stairmaster)",
                    "Trote contínuo na esteira ou ao ar livre",
                    "Remo ergômetro com cadência estável"
                ),
                observacaoSeguranca = "Monitore a hidratação e faça reposição adequada de eletrólitos para evitar cãibras em sessões longas.",
                cardioType = CardioType.CORRIDA,
                intensity = IntensityLevel.MODERADA
            )
        )
    }

    // ==========================================
    // SISTEMA DE METAS DE TREINO E PERFORMANCE
    // ==========================================
    fun getPresetGoalRecommendations(): List<PresetGoalRecommendation> {
        return listOf(
            PresetGoalRecommendation(
                id = "meta_hip_1",
                objetivo = "Hipertrofia",
                titulo = "Progressão de carga no supino reto",
                descricao = "Progredir a carga de trabalho de forma gradual mantendo cadência controlada e padrão biomecânico correto.",
                metrica = "kg levantados para 8 repetições",
                valorInicialEsperado = "Peso corporal x 0.7",
                valorAlvo = "Peso corporal x 1.0 (ex: 80 kg)",
                prazoSemanas = 12,
                frequenciaAvaliacao = "quinzenal",
                targetWeightKg = 80.0,
                targetReps = 8,
                exerciseName = "Supino reto com barra"
            ),
            PresetGoalRecommendation(
                id = "meta_hip_2",
                objetivo = "Hipertrofia",
                titulo = "Ganho de massa magra nos membros inferiores",
                descricao = "Aumentar a circunferência de coxa e volume muscular através de sobrecarga progressiva em agachamento e leg press.",
                metrica = "centímetros de circunferência de coxa",
                valorInicialEsperado = "Medida basal individual",
                valorAlvo = "+2 a +4 cm de circunferência",
                prazoSemanas = 24,
                frequenciaAvaliacao = "mensal"
            ),
            PresetGoalRecommendation(
                id = "meta_gord_1",
                objetivo = "Perda de Gordura",
                titulo = "Redução do percentual de gordura corporal (%BF)",
                descricao = "Combinar rotina de musculação, cardio LISS/HIIT e déficit calórico moderado para preservação de massa magra.",
                metrica = "% de gordura corporal",
                valorInicialEsperado = "22% - 28%",
                valorAlvo = "14% - 18%",
                prazoSemanas = 24,
                frequenciaAvaliacao = "mensal"
            ),
            PresetGoalRecommendation(
                id = "meta_gord_2",
                objetivo = "Perda de Gordura",
                titulo = "Redução de circunferência abdominal",
                descricao = "Diminuir o acúmulo de gordura visceral através de consistência de treino e balanço calórico negativo.",
                metrica = "centímetros de cintura (linha do umbigo)",
                valorInicialEsperado = "Medida basal",
                valorAlvo = "-4 a -8 cm",
                prazoSemanas = 12,
                frequenciaAvaliacao = "quinzenal"
            ),
            PresetGoalRecommendation(
                id = "meta_cond_1",
                objetivo = "Condicionamento",
                titulo = "Resistência cardiovascular no HIIT",
                descricao = "Completar a sessão completa de HIIT avançado (28 min) respeitando rigorosamente os tempos de descanso sem pausas extras.",
                metrica = "rodadas completadas na intensidade estipulada",
                valorInicialEsperado = "6 rodadas",
                valorAlvo = "14 rodadas completas",
                prazoSemanas = 8,
                frequenciaAvaliacao = "semanal"
            ),
            PresetGoalRecommendation(
                id = "meta_cond_2",
                objetivo = "Condicionamento",
                titulo = "Consistência de frequência semanal",
                descricao = "Cumprir 100% dos treinos programados na semana sem faltas não justificadas.",
                metrica = "dias de treino realizados por semana",
                valorInicialEsperado = "2 dias/semana",
                valorAlvo = "4 a 5 dias/semana",
                prazoSemanas = 4,
                frequenciaAvaliacao = "semanal"
            ),
            PresetGoalRecommendation(
                id = "meta_forc_1",
                objetivo = "Força",
                titulo = "Agachamento livre com o próprio peso corporal",
                descricao = "Atingir a marca de 1 repetição máxima (1RM) ou série de 5 repetições com carga equivalente ao peso corporal.",
                metrica = "kg na barra no agachamento",
                valorInicialEsperado = "40 kg",
                valorAlvo = "Peso corporal (ex: 80 kg)",
                prazoSemanas = 12,
                frequenciaAvaliacao = "quinzenal",
                targetWeightKg = 80.0,
                targetReps = 6,
                exerciseName = "Agachamento livre com barra"
            ),
            PresetGoalRecommendation(
                id = "meta_forc_2",
                objetivo = "Força",
                titulo = "Execução de repetições estritas de barra fixa",
                descricao = "Progredir da barra fixa assistida (elástico/graviton) para repetições livres e completas.",
                metrica = "repetições completas sem impulso",
                valorInicialEsperado = "0 repetições livres",
                valorAlvo = "8 repetições livres completas",
                prazoSemanas = 12,
                frequenciaAvaliacao = "quinzenal",
                targetWeightKg = 0.0,
                targetReps = 8,
                exerciseName = "Barra fixa (Pull-up) ou Graviton"
            )
        )
    }

    fun getDefaultExerciseTargets(): List<ExercisePerformanceTarget> {
        val today = java.time.LocalDate.now().toEpochDay()
        return listOf(
            ExercisePerformanceTarget(
                id = 1,
                exerciseId = 1,
                exerciseName = "Supino reto com barra",
                targetWeightKg = 80.0,
                targetReps = 8,
                currentWeightKg = 60.0,
                currentReps = 10,
                targetDateEpochDay = today + (12 * 7),
                isAchieved = false,
                notes = "Meta de 80kg (1x peso corporal) com cadência controlada.",
                createdAtEpochDay = today
            ),
            ExercisePerformanceTarget(
                id = 2,
                exerciseId = 40,
                exerciseName = "Agachamento livre com barra",
                targetWeightKg = 100.0,
                targetReps = 6,
                currentWeightKg = 70.0,
                currentReps = 8,
                targetDateEpochDay = today + (12 * 7),
                isAchieved = false,
                notes = "Agachamento livre quebrando a paralela com 100kg.",
                createdAtEpochDay = today
            ),
            ExercisePerformanceTarget(
                id = 3,
                exerciseId = 27,
                exerciseName = "Levantamento terra convencional (Deadlift)",
                targetWeightKg = 130.0,
                targetReps = 6,
                currentWeightKg = 100.0,
                currentReps = 6,
                targetDateEpochDay = today + (12 * 7),
                isAchieved = false,
                notes = "Fortalecimento de eretores e pegada pronada.",
                createdAtEpochDay = today
            ),
            ExercisePerformanceTarget(
                id = 4,
                exerciseId = 24,
                exerciseName = "Barra fixa (Pull-up) ou Graviton",
                targetWeightKg = 0.0,
                targetReps = 8,
                currentWeightKg = 0.0,
                currentReps = 4,
                targetDateEpochDay = today + (12 * 7),
                isAchieved = false,
                notes = "8 repetições estritas livres sem impulso nas pernas.",
                createdAtEpochDay = today
            )
        )
    }

    fun getDefaultWorkoutSessions(): List<com.example.data.model.WorkoutSession> {
        val today = java.time.LocalDate.now().toEpochDay()
        val now = System.currentTimeMillis()

        // Session 1 (2 days ago): Treino A
        val session1Plans = listOf(
            WorkoutExercisePlan(
                exerciseId = 1,
                exerciseName = "Supino reto com barra",
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
                exerciseName = "Supino inclinado com halteres",
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
                exerciseId = 15,
                exerciseName = "Tríceps no pulley com corda",
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

        // Session 2 (5 days ago): Treino B
        val session2Plans = listOf(
            WorkoutExercisePlan(
                exerciseId = 21,
                exerciseName = "Puxada frontal na polia aberta",
                muscleGroup = MuscleGroup.COSTAS.displayName,
                sets = listOf(
                    ExerciseSetEntry(1, 45.0, 10, true, 60),
                    ExerciseSetEntry(2, 45.0, 10, true, 60),
                    ExerciseSetEntry(3, 45.0, 10, true, 60)
                ),
                targetRestSeconds = 60,
                notes = "Depressão escapular firme."
            ),
            WorkoutExercisePlan(
                exerciseId = 25,
                exerciseName = "Remada curvada com barra (pegada pronada)",
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
                exerciseId = 31,
                exerciseName = "Rosca direta com barra W",
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

        return listOf(
            com.example.data.model.WorkoutSession(
                id = 1,
                templateId = 1,
                title = "Iniciante - Treino A: Peito, Ombros e Tríceps",
                dateEpochDay = today - 2,
                startTimeMillis = now - (2 * 86400000L) - 3600000L,
                endTimeMillis = now - (2 * 86400000L),
                durationSeconds = 2700, // 45 min
                location = "Academia Smart Fit",
                status = com.example.data.model.SessionStatus.COMPLETED,
                totalWeightLiftedKg = 3450.0,
                estimatedCalories = 320,
                perceivedExertion = 7,
                notes = "Treino produtivo. 10 reps em todas as séries do Supino Reto com 60kg!",
                exercisesDoneJson = jsonAdapter.toJson(session1Plans),
                aiCaloricEvaluation = "Boa intensidade mecânica com sobrecarga progressiva recomendada no supino reto."
            ),
            com.example.data.model.WorkoutSession(
                id = 2,
                templateId = 2,
                title = "Iniciante - Treino B: Costas e Bíceps",
                dateEpochDay = today - 5,
                startTimeMillis = now - (5 * 86400000L) - 3000000L,
                endTimeMillis = now - (5 * 86400000L),
                durationSeconds = 2700, // 45 min
                location = "Academia Smart Fit",
                status = com.example.data.model.SessionStatus.COMPLETED,
                totalWeightLiftedKg = 3150.0,
                estimatedCalories = 310,
                perceivedExertion = 7,
                notes = "Puxada frontal fechada com 45kg.",
                exercisesDoneJson = jsonAdapter.toJson(session2Plans),
                aiCaloricEvaluation = "Ótimo estímulo para dorsais e bíceps."
            )
        )
    }

    fun getDefaultMedals(): List<com.example.data.model.UserMedal> {
        val today = java.time.LocalDate.now().toEpochDay()
        return listOf(
            // ==========================================
            // 1. METAS & MEDALHAS SEMANAIS (Weekly Goals)
            // ==========================================
            com.example.data.model.UserMedal(
                id = "weekly_freq_bronze",
                title = "Início Ativo Semanal 🥉",
                description = "Completar pelo menos 3 treinos na mesma semana.",
                category = "CONSISTENCIA",
                period = "SEMANAL",
                iconEmoji = "🔥",
                isUnlocked = true,
                unlockedDateEpochDay = today,
                progressCurrent = 3,
                progressMax = 3,
                rarity = "Bronze",
                xpReward = 100
            ),
            com.example.data.model.UserMedal(
                id = "weekly_freq_prata",
                title = "Constância de Ferro 🥈",
                description = "Completar 4 ou mais treinos na semana mantendo o ritmo.",
                category = "CONSISTENCIA",
                period = "SEMANAL",
                iconEmoji = "⚡",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 3,
                progressMax = 4,
                rarity = "Prata",
                xpReward = 250
            ),
            com.example.data.model.UserMedal(
                id = "weekly_freq_ouro",
                title = "Guerreiro Implacável 🥇",
                description = "Completar 5 dias de treino na semana com alta dedicação.",
                category = "CONSISTENCIA",
                period = "SEMANAL",
                iconEmoji = "🛡️",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 3,
                progressMax = 5,
                rarity = "Ouro",
                xpReward = 500
            ),
            com.example.data.model.UserMedal(
                id = "weekly_master_superacao",
                title = "Master da Superação Semanal 👑",
                description = "Completar 6 treinos + 60 min de cardio na mesma semana.",
                category = "CONSISTENCIA",
                period = "SEMANAL",
                iconEmoji = "👑",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 3,
                progressMax = 6,
                rarity = "Master da Superação",
                xpReward = 1000
            ),
            com.example.data.model.UserMedal(
                id = "weekly_volume_bronze",
                title = "Tonelagem Inicial 🥉",
                description = "Levantar 5.000 kg em volume de carga acumulado na semana.",
                category = "VOLUME",
                period = "SEMANAL",
                iconEmoji = "🏋️",
                isUnlocked = true,
                unlockedDateEpochDay = today - 1,
                progressCurrent = 6800,
                progressMax = 5000,
                rarity = "Bronze",
                xpReward = 100
            ),
            com.example.data.model.UserMedal(
                id = "weekly_volume_prata",
                title = "Carga Expressiva 🥈",
                description = "Acumular 15.000 kg em volume total levantado na semana.",
                category = "VOLUME",
                period = "SEMANAL",
                iconEmoji = "💪",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 6800,
                progressMax = 15000,
                rarity = "Prata",
                xpReward = 250
            ),
            com.example.data.model.UserMedal(
                id = "weekly_volume_ouro",
                title = "Titã da Semana 🥇",
                description = "Alcançar 30.000 kg em volume total de carga na semana.",
                category = "VOLUME",
                period = "SEMANAL",
                iconEmoji = "⚔️",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 6800,
                progressMax = 30000,
                rarity = "Ouro",
                xpReward = 500
            ),
            com.example.data.model.UserMedal(
                id = "weekly_volume_master",
                title = "Leviatã dos Pesos Semanal 👑",
                description = "Ultrapassar 50.000 kg levantados em 7 dias com força sobre-humana.",
                category = "VOLUME",
                period = "SEMANAL",
                iconEmoji = "🌋",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 6800,
                progressMax = 50000,
                rarity = "Master da Superação",
                xpReward = 1000
            ),

            // ==========================================
            // 2. METAS & MEDALHAS MENSAIS (Monthly Goals)
            // ==========================================
            com.example.data.model.UserMedal(
                id = "monthly_freq_bronze",
                title = "Compromisso Mensal 🥉",
                description = "Completar 12 treinos registrados dentro do mês.",
                category = "CONSISTENCIA",
                period = "MENSAL",
                iconEmoji = "🗓️",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 7,
                progressMax = 12,
                rarity = "Bronze",
                xpReward = 150
            ),
            com.example.data.model.UserMedal(
                id = "monthly_freq_prata",
                title = "Dedicação de Aço 🥈",
                description = "Completar 16 ou mais treinos no mês corrente.",
                category = "CONSISTENCIA",
                period = "MENSAL",
                iconEmoji = "⚙️",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 7,
                progressMax = 16,
                rarity = "Prata",
                xpReward = 300
            ),
            com.example.data.model.UserMedal(
                id = "monthly_freq_ouro",
                title = "Elite Mensal 🥇",
                description = "Atingir a marca de 20 treinos completos no mês.",
                category = "CONSISTENCIA",
                period = "MENSAL",
                iconEmoji = "🏆",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 7,
                progressMax = 20,
                rarity = "Ouro",
                xpReward = 600
            ),
            com.example.data.model.UserMedal(
                id = "monthly_freq_master",
                title = "Master da Disciplina Mensal 👑",
                description = "Concluir 24+ treinos no mês sem quebrar a rotina.",
                category = "CONSISTENCIA",
                period = "MENSAL",
                iconEmoji = "💎",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 7,
                progressMax = 24,
                rarity = "Master da Superação",
                xpReward = 1200
            ),
            com.example.data.model.UserMedal(
                id = "monthly_pr_bronze",
                title = "Quebra de Limite 🥉",
                description = "Bater pelo menos 1 novo Recorde Pessoal (PR) no mês.",
                category = "FORCA",
                period = "MENSAL",
                iconEmoji = "🚀",
                isUnlocked = true,
                unlockedDateEpochDay = today - 2,
                progressCurrent = 1,
                progressMax = 1,
                rarity = "Bronze",
                xpReward = 150
            ),
            com.example.data.model.UserMedal(
                id = "monthly_pr_prata",
                title = "Evolução Contínua 🥈",
                description = "Bater 3 novos Recordes Pessoais (PR) de carga no mês.",
                category = "FORCA",
                period = "MENSAL",
                iconEmoji = "📈",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 1,
                progressMax = 3,
                rarity = "Prata",
                xpReward = 300
            ),
            com.example.data.model.UserMedal(
                id = "monthly_pr_ouro",
                title = "Destruidor de Platôs 🥇",
                description = "Superar 5 recordes pessoais (PR) no mesmo mês.",
                category = "FORCA",
                period = "MENSAL",
                iconEmoji = "💥",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 1,
                progressMax = 5,
                rarity = "Ouro",
                xpReward = 600
            ),
            com.example.data.model.UserMedal(
                id = "monthly_pr_master",
                title = "Master da Superação Mensal 👑",
                description = "Bater 8 ou mais PRs e levantar mais de 100.000 kg no mês.",
                category = "FORCA",
                period = "MENSAL",
                iconEmoji = "🌟",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 1,
                progressMax = 8,
                rarity = "Master da Superação",
                xpReward = 1200
            ),

            // ==========================================
            // 3. METAS & MEDALHAS ANUAIS (Annual Goals)
            // ==========================================
            com.example.data.model.UserMedal(
                id = "annual_workouts_bronze",
                title = "Fundação de Ferro 🥉",
                description = "Completar 50 treinos registrados no ano.",
                category = "CONSISTENCIA",
                period = "ANUAL",
                iconEmoji = "🧱",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 24,
                progressMax = 50,
                rarity = "Bronze",
                xpReward = 300
            ),
            com.example.data.model.UserMedal(
                id = "annual_workouts_prata",
                title = "Hábito Inabalável 🥈",
                description = "Completar 100 treinos registrados durante o ano.",
                category = "CONSISTENCIA",
                period = "ANUAL",
                iconEmoji = "🎯",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 24,
                progressMax = 100,
                rarity = "Prata",
                xpReward = 600
            ),
            com.example.data.model.UserMedal(
                id = "annual_workouts_ouro",
                title = "Estilo de Vida Fit 🥇",
                description = "Atingir a incrível marca de 180 treinos no ano.",
                category = "CONSISTENCIA",
                period = "ANUAL",
                iconEmoji = "🏅",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 24,
                progressMax = 180,
                rarity = "Ouro",
                xpReward = 1200
            ),
            com.example.data.model.UserMedal(
                id = "annual_workouts_master",
                title = "Master da Superação Anual: Imortal do Ferro 👑",
                description = "Alcançar 250+ treinos e transformar o corpo e a mente para sempre.",
                category = "CONSISTENCIA",
                period = "ANUAL",
                iconEmoji = "🦅",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 24,
                progressMax = 250,
                rarity = "Master da Superação",
                xpReward = 2500
            ),
            com.example.data.model.UserMedal(
                id = "annual_tonnage_bronze",
                title = "Marco dos 100 Mil Kg 🥉",
                description = "Acumular 100.000 kg em volume de carga durante o ano.",
                category = "VOLUME",
                period = "ANUAL",
                iconEmoji = "🪨",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 38000,
                progressMax = 100000,
                rarity = "Bronze",
                xpReward = 300
            ),
            com.example.data.model.UserMedal(
                id = "annual_tonnage_prata",
                title = "Meio Milhão de Kg 🥈",
                description = "Acumular 500.000 kg em volume de peso levantado no ano.",
                category = "VOLUME",
                period = "ANUAL",
                iconEmoji = "🏔️",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 38000,
                progressMax = 500000,
                rarity = "Prata",
                xpReward = 700
            ),
            com.example.data.model.UserMedal(
                id = "annual_tonnage_ouro",
                title = "Milionário do Ferro 🥇",
                description = "Atingir 1.000.000 kg (1 Milhão de Kg / 1.000 toneladas) no ano.",
                category = "VOLUME",
                period = "ANUAL",
                iconEmoji = "🌟",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 38000,
                progressMax = 1000000,
                rarity = "Ouro",
                xpReward = 1500
            ),
            com.example.data.model.UserMedal(
                id = "annual_tonnage_master",
                title = "Master da Superação: Atlas Eterno 👑",
                description = "Superar 2.000.000 kg (2 Milhões de Kg) levantados com honra suprema.",
                category = "VOLUME",
                period = "ANUAL",
                iconEmoji = "🌌",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 38000,
                progressMax = 2000000,
                rarity = "Master da Superação",
                xpReward = 3000
            ),

            // ==========================================
            // 4. MARCOS ESPECIAIS & SUPERAÇÃO (Special)
            // ==========================================
            com.example.data.model.UserMedal(
                id = "first_workout",
                title = "Primeiro Passo 🚀",
                description = "Completou seu 1º treino de musculação no FitPr09.",
                category = "CONSISTENCIA",
                period = "ESPECIAL",
                iconEmoji = "🥇",
                isUnlocked = true,
                unlockedDateEpochDay = today - 5,
                progressCurrent = 1,
                progressMax = 1,
                rarity = "Bronze",
                xpReward = 100
            ),
            com.example.data.model.UserMedal(
                id = "club_100kg",
                title = "Clube dos 100kg ⚡",
                description = "Levantou 100kg ou mais em uma série de exercício.",
                category = "FORCA",
                period = "ESPECIAL",
                iconEmoji = "🏋️‍♂️",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 70,
                progressMax = 100,
                rarity = "Ouro",
                xpReward = 500
            ),
            com.example.data.model.UserMedal(
                id = "ai_architect",
                title = "Arquiteto com IA 🤖",
                description = "Gerou uma rotina de treino personalizada com a Inteligência Artificial Gemini.",
                category = "ESPECIAL",
                period = "ESPECIAL",
                iconEmoji = "🤖",
                isUnlocked = true,
                unlockedDateEpochDay = today,
                progressCurrent = 1,
                progressMax = 1,
                rarity = "Prata",
                xpReward = 250
            ),
            com.example.data.model.UserMedal(
                id = "smart_nutrition",
                title = "Mestre dos Macros 🥗",
                description = "Analisou sua primeira refeição com a Inteligência Artificial Gemini.",
                category = "NUTRICAO",
                period = "ESPECIAL",
                iconEmoji = "🥗",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 0,
                progressMax = 1,
                rarity = "Bronze",
                xpReward = 100
            ),
            com.example.data.model.UserMedal(
                id = "super_master_supremo",
                title = "Ouro Master da Superação Suprema 👑",
                description = "Desbloquear mais de 10 medalhas de ouro e se consagrar lenda viva do fitness.",
                category = "ESPECIAL",
                period = "ESPECIAL",
                iconEmoji = "👑",
                isUnlocked = false,
                unlockedDateEpochDay = null,
                progressCurrent = 3,
                progressMax = 10,
                rarity = "Master da Superação",
                xpReward = 5000
            )
        )
    }

    fun getDefaultMealLogs(): List<com.example.data.model.MealLog> {
        val today = java.time.LocalDate.now().toEpochDay()
        return listOf(
            com.example.data.model.MealLog(
                id = 1,
                dateEpochDay = today,
                mealType = "Café da Manhã",
                description = "3 ovos mexidos, 2 fatias de pão integral 100%, 1 banana e café preto sem açúcar.",
                estimatedCalories = 460,
                proteinGrams = 26.0,
                carbsGrams = 42.0,
                fatsGrams = 18.0,
                fiberGrams = 6.0,
                aiInsight = "Excelente aporte proteico matinal e carboidratos de baixo índice glicêmico para sustentação anabólica.",
                healthRating = "Excelente"
            ),
            com.example.data.model.MealLog(
                id = 2,
                dateEpochDay = today,
                mealType = "Almoço Pós-Treino",
                description = "150g de peito de frango grelhado, 180g de arroz branco, 1 concha de feijão carioca e salada verde.",
                estimatedCalories = 620,
                proteinGrams = 48.0,
                carbsGrams = 72.0,
                fatsGrams = 9.0,
                fiberGrams = 7.5,
                aiInsight = "Janela de absorção pós-treino perfeita. Combinação ideal de leucina para síntese proteica miofibrilar.",
                healthRating = "Excelente"
            )
        )
    }
}
