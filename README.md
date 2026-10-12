# FITPR09 — Jornada do Atleta

Aplicativo Android de treinos, histórico e Jornada RPG. O projeto existente usa Kotlin, Jetpack Compose, Room e um serviço em primeiro plano para cardio. As novas funcionalidades devem preservar dados, treinos, planos e conquistas existentes.

## Modo Cardio

A biblioteca usa cartões ilustrados em duas colunas, com quatro categorias principais:

| Categoria | Modalidades |
| --- | --- |
| Corrida e caminhada | Caminhada livre, caminhada na esteira, corrida livre, corrida na esteira, trilha |
| Bicicleta | Indoor e ao ar livre |
| Boxe | Saco, sombra e intervalado |
| HIIT e Tabata | Construtor personalizado e modelos reutilizáveis |

Elíptico e futebol permanecem disponíveis no acesso “Outras atividades”. Os protocolos existentes ficam em uma seção expansível. O cartão **TREINO CARDIO** em **Meus Treinos** abre a mesma biblioteca da aba Cardio.

### Intervalos

O construtor configura nome, atividade do esforço, esforço, descanso, ciclos, aquecimento e desaquecimento. Os tempos de configuração são informados em segundos. Os modelos são salvos no Room.

Configuração inicial: **120 segundos de esforço, 120 segundos de descanso, 6 ciclos = 22 minutos**. O descanso ocorre apenas entre ciclos: 6 esforços e 5 descansos. O preset Tabata também segue essa regra, sem descanso após o último esforço.

A duração é `aquecimento + ciclos × esforço + (ciclos − 1) × descanso + desaquecimento`. A fase é calculada a partir do tempo ativo monotônico do serviço, sem depender dos ticks da interface. A tela e a notificação mostram fase, tempo restante e ciclo. Ao completar os intervalos, a sessão fica pausada e recuperável; o usuário deve finalizar para salvar o resumo. Pausar não equivale ao descanso de um protocolo: a pausa suspende o tempo total, enquanto o descanso entre ciclos faz parte do treino.

### GPS e métricas

GPS opcional para caminhada livre, corrida livre, trilha e bicicleta externa. HIIT inicia sem GPS e permite habilitar rastreamento para uma atividade externa. Esteira, bicicleta indoor, boxe e elíptico não habilitam GPS, inclusive no serviço. O mapa aparece somente quando o rastreamento está habilitado.

Passos são coletados somente nas modalidades de caminhada/corrida/trilha compatíveis. Bicicleta e boxe não recebem passos de pedaladas ou golpes. Distância interna não é produzida a partir de GPS. Calorias continuam sendo estimativas; dados de frequência cardíaca dependem de fonte real.

## Assets

Seis novos PNGs RGBA de bicicleta, boxe e HIIT, com variantes masculina/feminina, estão em `app/src/main/res/drawable-nodpi/`. Os runners existentes foram preservados. A seleção visual usa o sexo informado na avaliação inicial do perfil, sem alterar o cadastro.

Os cartões reutilizam a ilustração da categoria entre modalidades. Ilustrações específicas de esteira, caminhada, trilha, bicicleta ergométrica e saco de boxe ainda não foram geradas nesta etapa. A referência original de cinco telas não acompanha este checkout; a comparação visual exata continua pendente.

## Persistência

Room versão **17**, com migração aditiva **16 → 17**:

- `intervalConfigJson` em sessões concluídas e checkpoints ativos.
- Nova tabela `cardio_interval_templates` para modelos.
- Preservação dos identificadores UUID e do fluxo de finalização existente.

Não desinstale o app nem limpe seus dados para atualizar. Não use migrações destrutivas. O esquema JSON de versão 17 deve ser exportado e conferido pelo KSP na primeira compilação Android bem-sucedida.

## Compilação e validação

Pré-requisitos: Android SDK compatível com `compileSdk 36.1`, Gradle **9.3.1** e JDK **25** conforme `gradle/gradle-daemon-jvm.properties`. O repositório não contém scripts `gradlew`; use a instalação do Gradle ou a CI existente.

```bash
gradle testDebugUnitTest assembleDebug
gradle connectedDebugAndroidTest
```

A assinatura debug atual requer um `debug.keystore` na raiz. Para release, configure `keystore.properties` ou as variáveis `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`; nunca versionar credenciais. A chave Maps deve ser configurada conforme `.env.example` e restrita ao aplicativo e certificado apropriados.

```bash
gradle bundleRelease
```

Esse comando é uma instrução de build, não uma confirmação de que existe um AAB publicado.

### Estado da validação desta implementação

- Testes JVM do motor de intervalos e da matemática de tempo/passos executados e aprovados isoladamente.
- SQL da migração 16 → 17 aplicado em SQLite sobre o esquema 16 com registros de exemplo; preservação dos dados e integridade aprovadas. Essa verificação não substitui o MigrationTestHelper Android.
- Transparência dos seis novos assets verificada.
- `git diff --check` aprovado.
- Compilação Android completa tentada, bloqueada antes de compilar o código pelo download automático do JDK 25 no ambiente local.
- Migração Room completa, teste de UI, chamadas, bloqueio de tela, GPS, consumo de bateria e recompensas ainda exigem validação Android.

Consulte [o relatório de implementação e publicação](docs/CARDIO_REDESIGN.md) para pendências e roteiro de continuidade no desktop.
