# Fase 6 — Coach PRL09

## Objetivo
Centralizar a IA de treino em uma fachada única e aterrar respostas em métricas calculadas pelo aplicativo.

## Implementado
- `CoachPRL09Context`: contrato estruturado de contexto.
- `CoachContextBuilder`: calcula resumos de 7 e 28 dias a partir de WorkoutSession, CardioSession, perfil, medidas e Readiness.
- `CoachPRL09Service.ask`: perguntas ancoradas no contexto real.
- `CoachPRL09Service.generateAdaptedWorkout`: sessão condicionada por tempo, equipamentos, objetivo, histórico e readiness.
- Regra explícita: IA não recalcula nem inventa métricas determinísticas.
- Teste unitário do contexto determinístico.

## Próximas integrações antes do gate
- Criar `CoachPRL09ViewModel` combinando os Flows reais do Room + readiness.
- Migrar a tela/chat existente para usar exclusivamente `CoachPRL09Service`.
- Expor seleção de tempo/equipamentos na UI de treino adaptado.
- Conectar PRs reais do `PersonalRecordEngine` ao contexto.
- Executar `assembleDebug` e `testDebugUnitTest` no GitHub Actions e corrigir regressões.

## Gate
A IA deve explicar dados reais do app e declarar indisponibilidade quando um dado não estiver no contexto. Nenhum número determinístico deve ser produzido pelo modelo quando puder ser calculado localmente.
