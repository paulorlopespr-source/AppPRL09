# Fase 7 — UX, gamificação e consolidação

## Objetivo
Transformar os recursos já entregues nas Fases 1–6 em uma experiência mais clara, motivadora e integrada, sem duplicar cálculos nem inventar métricas.

## Implementado
- Jornada PRL09 como novo hub de progresso.
- Nível e XP derivados deterministicamente de atividades registradas, dias ativos e medalhas desbloqueadas.
- Sequência atual e melhor sequência calculadas a partir dos dias reais com atividade.
- Resumo consolidado de força, cardio, tempo, distância e volume.
- Missões semanais com progresso real: consistência, cardio e volume de força.
- Atalhos para iniciar treino, abrir Coach PRL09 e abrir painel/readiness.
- Coach PRL09 finalmente acessível pela navegação principal através da Jornada.
- ViewModel dedicado para evitar adicionar nova responsabilidade ao FitnessViewModel legado.
- Testes do motor determinístico da Jornada.
- Gate de CI próprio com assembleDebug + testDebugUnitTest.

## Critério de saída
A Fase 7 só deve ser integrada à main quando o workflow `Phase 7 UX Gamification Gate` estiver totalmente verde.
