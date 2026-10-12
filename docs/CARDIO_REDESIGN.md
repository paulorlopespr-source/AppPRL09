# Cardio FITPR09 — implementação e auditoria preliminar

## Escopo entregue em código

- Biblioteca de categorias com grandes imagens e acesso às modalidades.
- Corrida na esteira e três modalidades de boxe adicionadas ao enum, mantendo nomes antigos para compatibilidade de registros Room.
- Navegação de Treinos para o mesmo destino Cardio.
- Configuração de intervalos, dois presets e modelos persistidos no Room.
- Cálculo de fases pelo tempo ativo do serviço; notificação e tela mostram esforço/descanso e ciclo.
- Sinal sonoro/vibração na mudança de fase, executados no serviço existente.
- Wake lock parcial com timeout limitado ao restante do protocolo, liberado ao pausar, finalizar e destruir o serviço. Exige teste de bateria em aparelho.
- GPS bloqueado no serviço para modalidades internas; mapa oculto sem rastreamento.
- Checkpoints e histórico guardam configuração do protocolo.

## Arquivos

Novos componentes: `CardioVisualLibrary.kt` e `CardioIntervalBuilder.kt` em `ui/components`; motor `domain/cardio/CardioIntervalPlan.kt`; codec `data/local/CardioIntervalCodec.kt`; entidade `data/model/CardioIntervalTemplate.kt`; teste `CardioIntervalPlanTest.kt`.

Alterações: `MainActivity.kt`, `CardioScreen.kt`, `WorkoutTemplatesScreen.kt`, `ActiveCardioTrackerModal.kt`, `OutdoorActivityHistoryCard.kt`, `FitnessViewModel.kt`, `OutdoorLocationService.kt`, `HealthConnectManager.kt`, `CardioSession.kt`, `OutdoorSessionModels.kt`, `ActiveCardioSessionRecord.kt`, `OutdoorCardioPersistence.kt`, `FitnessRepository.kt`, `FitnessDao.kt`, `AppDatabase.kt` e `AndroidManifest.xml`.

Permissão nova: `WAKE_LOCK`. As permissões de localização, reconhecimento de atividade, notificação e serviços `health`/`location` já existiam. Não foi adicionada localização irrestrita em segundo plano.

Mapas: preservado o Google Maps SDK existente; não foi provisionada uma chave de produção.

Assets novos em `app/src/main/res/drawable-nodpi/`:

- `cardio_cycling_male.png`, `cardio_cycling_female.png`
- `cardio_boxing_male.png`, `cardio_boxing_female.png`
- `cardio_hiit_male.png`, `cardio_hiit_female.png`

Geração pela ferramenta de imagens, com prompt por asset: atleta masculino/feminino em bicicleta de estrada, boxe sombra com luvas ou exercício de corrida com joelhos elevados; corpo estilizado em 3D grafite, roupa escura, iluminação muscular e de contorno roxa, corpo e equipamento completos, fundo transparente, sem texto ou marca. PNGs otimizados para dimensão máxima de 640 px. Os atletas de corrida anteriores continuam em uso.

## Validação e limites

Os cinco testes do motor verificam 22 minutos sem descanso final, transições quando a atualização chega atrasada, aquecimento/desaquecimento, um ciclo, descanso zero e rejeição de configuração inválida. Foram compilados com o compilador Kotlin embutido no Gradle e executados com JUnit 4, fora do build Android.

Não afirmar que o módulo está pronto para produção: a compilação Android local não chegou à etapa de Kotlin/Compose/Room. A tentativa falhou ao provisionar a toolchain JDK 25 pelo Foojay. É preciso executar a CI e revisar seus resultados, além de exportar o esquema 17 do Room. O checkout não possui SDK/emulador nem celular conectado.

O SQL da migração 16 → 17 também foi executado em SQLite sobre o esquema 16 exportado, com sessão concluída e checkpoint ativo de exemplo. As linhas anteriores foram preservadas e a verificação de integridade passou. A validação de esquema do Room no Android ainda está pendente.

A finalização, plano, histórico e mecanismos existentes de recompensas foram preservados. Ausência de duplicação de XP e sessões exige teste de integração; não foi atribuída uma nova regra de XP nesta alteração.

Ainda pendentes: assets individuais de cada modalidade, comparação com imagem de referência original, UI do cronômetro circular definitiva, validar fidelidade em dispositivos pequenos e fontes ampliadas, atualização instantânea após retorno, sensores físicos, configuração externa de Maps e auditoria completa dos compartilhamentos/SDKs.

## Roteiro no desktop

1. Abrir a branch `feat/cardio-visual-library`, verificar o estado remoto e preservar mudanças locais existentes antes de atualizar.
2. Configurar JDK/SDK e assinatura debug, executar `gradle testDebugUnitTest assembleDebug`, corrigir os erros que aparecerem e adicionar o esquema Room 17 exportado.
3. Instalar sobre a versão anterior com banco preenchido; testar migração, modelos salvos, histórico e finalização repetida.
4. No celular: iniciar 2:00/2:00 × 6, atender chamada, abrir notificações, pressionar Home, bloquear tela; confirmar fases, notificações, 22:00 finais e cinco descansos.
5. Testar pausa/retomada, morte de processo, reinicialização e permissões revogadas. Não presumir continuidade após force-stop.
6. Verificar GPS externo, ausência de localização interna e ausência de passos em bicicleta/boxe. Testar percursos sem preencher lacunas fictícias.
7. Repetir com plano de treino e verificar uma sessão, uma conclusão do plano e uma recompensa por evento.

## Auditoria preliminar para Play Store

O código configura `applicationId com.aistudio.musculacao.vktrwq`, `targetSdk 36`, `versionCode 6`, `versionName 1.5`. Esses valores foram preservados. Antes de lançar, conferir o maior versionCode já usado no Play Console e elevar o próximo release se necessário.

Pendências concretas para publicação:

- Build completo, testes Android e AAB assinado com a chave de upload correta.
- Declarações Play Console dos serviços health/location, descrição do uso e vídeo mostrando como o usuário inicia a atividade. [Fonte oficial](https://support.google.com/googleplay/android-developer/answer/13392821).
- Conferir o [requisito vigente de target API](https://developer.android.com/google/play/requirements/target-sdk) no momento do envio.
- Revisar política de privacidade, formulário Segurança dos dados e declaração de app de saúde com base nos dados realmente transmitidos. Não presumir “somente local”: o app existente inclui IA, Health Connect e SDKs externos.
- Revisar se GPS/rota ou perfil entram em avaliação IA, sincronização, logs, backups ou cards compartilhados; assegurar escolhas claras e exclusão dos dados.
- Restringir a chave Maps e revisar notificações e permissão de reconhecimento para o serviço health em Android 14+.
- Distribuir primeiro em teste interno, verificar instalação/atualização e analisar o relatório de pré-lançamento.

Nenhum acesso ao Play Console, envio de AAB, promoção de versão ou publicação em produção foi realizado nesta etapa. A auditoria é preliminar, com os bloqueios acima explicitados.
