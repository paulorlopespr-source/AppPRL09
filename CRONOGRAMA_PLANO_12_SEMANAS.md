# Cronograma — Avaliação Inicial e Plano de Treino de 12 Semanas

## Objetivo

Implementar um fluxo de avaliação inicial que gere automaticamente um plano de treino de 12 semanas, distribua os treinos na Agenda e prepare a progressão para um segundo ciclo.

## Fase 1 — Modelagem e persistência

**Duração estimada:** 1–2 dias

- Criar entidade `TrainingCycle`.
- Criar entidade `InitialAssessment`.
- Criar entidade `PlannedWorkout`.
- Vincular ciclo, template, compromisso da Agenda e sessão realizada.
- Adicionar campos para nível, objetivo, disponibilidade e semana atual.
- Criar migração Room sem apagar dados existentes.
- Atualizar backup e restauração.

**Critério de aceite:** os dados do questionário e do ciclo permanecem salvos após fechar e reabrir o aplicativo.

## Fase 2 — Questionário inicial

**Duração estimada:** 1–2 dias

- Exibir o questionário no primeiro acesso ou quando não houver avaliação ativa.
- Campos obrigatórios:
  - idade;
  - sexo/perfil corporal;
  - altura;
  - peso;
  - medidas principais;
  - objetivo;
  - nível: sedentário, iniciante, intermediário ou avançado;
  - disponibilidade: 3, 4 ou 5 dias.
- Validar campos e impedir avanço com dados inválidos.
- Permitir editar a avaliação posteriormente nas configurações do perfil.

**Critério de aceite:** uma avaliação válida cria um ciclo ativo e direciona o usuário para o plano recomendado.

## Fase 3 — Gerador do plano de 12 semanas

**Duração estimada:** 2–3 dias

- Criar regras de divisão por disponibilidade:
  - 3 dias: corpo inteiro ou superior/inferior alternado;
  - 4 dias: superior/inferior;
  - 5 dias: divisão por grupos musculares com um dia complementar.
- Ajustar exercícios conforme o nível.
- Definir séries, repetições, carga inicial sugerida e descanso.
- Usar os exercícios que possuem assets na biblioteca.
- Gerar as 12 semanas com progressão gradual.
- Impedir duplicação caso o plano já tenha sido gerado.

**Critério de aceite:** o usuário recebe uma sequência coerente de treinos para todas as semanas do ciclo.

## Fase 4 — Integração com a Agenda

**Duração estimada:** 1–2 dias

- Criar compromissos automaticamente nos dias escolhidos.
- Preservar horário, data e fuso local.
- Vincular cada compromisso ao `PlannedWorkout` por ID.
- Refletir concluído, cancelado, reagendado e descanso planejado.
- Mostrar os treinos realizados com bolinha verde no calendário mensal.

**Critério de aceite:** cada treino planejado aparece na Agenda e fica ligado à sessão concluída correspondente.

## Fase 5 — Treino ativo e histórico

**Duração estimada:** 1–2 dias

- Abrir o treino correto ao iniciar um compromisso planejado.
- Persistir séries, repetições, carga, RPE, descanso, horário e conclusão.
- Registrar automaticamente a sessão no histórico.
- Mostrar o ciclo e a semana no detalhe do histórico.
- Manter retomada após fechamento do aplicativo.

**Critério de aceite:** finalizar um treino atualiza Agenda, histórico, aderência e progresso do ciclo.

## Fase 6 — Acompanhamento e aderência

**Duração estimada:** 1–2 dias

- Calcular aderência por semana e por ciclo.
- Excluir descanso planejado do cálculo de penalização.
- Diferenciar cancelado e reagendado.
- Mostrar progresso planejado versus realizado.
- Usar aderência como requisito de evolução quando aplicável.

**Critério de aceite:** a aderência nunca ultrapassa 100% e corresponde aos compromissos individuais.

## Fase 7 — Reavaliação da semana 12

**Duração estimada:** 1–2 dias

- Notificar o usuário no fim do ciclo.
- Repetir peso, medidas, nível e objetivo.
- Comparar avaliação inicial e final.
- Sugerir o segundo plano.
- Aumentar progressivamente carga, volume ou complexidade.
- Permitir iniciar o novo ciclo sem apagar o anterior.

**Critério de aceite:** o primeiro ciclo permanece consultável e o segundo começa com base nos dados coletados.

## Fase 8 — Testes e validação

**Duração estimada:** 2–3 dias

- Teste de primeiro acesso.
- Teste de validação do questionário.
- Teste dos planos de 3, 4 e 5 dias.
- Teste de persistência e migração Room.
- Teste de Agenda e vínculo por ID.
- Teste de treino concluído, cancelado e reagendado.
- Teste de retomada após fechar o app.
- Teste de backup e restauração.
- Teste de rotação e navegação.
- Validação no aparelho físico.

## Ordem recomendada de entrega

1. Modelagem e migração.
2. Questionário inicial.
3. Gerador do plano.
4. Agenda planejada.
5. Treino ativo e histórico.
6. Aderência e evolução.
7. Reavaliação e segundo ciclo.
8. Testes finais e release.

## Resultado esperado

Ao final, o usuário terá um plano personalizado de 12 semanas, compromissos automaticamente distribuídos na Agenda, histórico completo dos treinos e uma transição controlada para um segundo plano mais avançado.

## Backlog futuro — Programas prontos por nível

O fluxo atual de avaliação e ciclo não deve ser considerado a entrega dos programas completos por nível. Em uma etapa futura, criar uma área de programas em que o usuário possa escolher:

- plano de 12 semanas para iniciante;
- plano de 12 semanas para intermediário;
- plano de 12 semanas para avançado.

Ao tocar em um plano, o usuário deverá ver a rotina recomendada, a divisão semanal, os exercícios, séries, repetições, descanso e as regras de progressão daquele nível antes de iniciar. Cada programa precisa ter conteúdo e progressão próprios; não basta trocar o rótulo do nível em um plano genérico.
