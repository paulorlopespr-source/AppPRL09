# Fase 6 — Status de integração

- 6.1 Unificação da IA: `CoachPRL09Service` criado como fachada para perguntas e treino adaptado. Nutrição continua isolada no serviço legado para evitar regressão.
- 6.2 Contexto: `CoachContextBuilder` conectado a perfil, WorkoutSession, CardioSession, medidas e Readiness.
- 6.3 Perguntas: `CoachPRL09ViewModel` + `CoachPRL09Screen` implementados com contexto determinístico de 7/28 dias.
- 6.4 Treino adaptado: entrada de tempo, equipamentos e foco conectada ao gerador existente através do Coach PRL09.

Pendências para fechamento do gate:
1. inserir `CoachPRL09Screen` na navegação principal;
2. conectar PRs reais ao campo `recentPersonalRecords`;
3. substituir `toString()` do plano gerado por apresentação estruturada;
4. executar CI `assembleDebug` + `testDebugUnitTest` e corrigir erros.
