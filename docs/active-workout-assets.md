# Assets da tela de treino ativo

A tela de treino ativo já procura imagens opcionais por nome. Coloque os arquivos finais em
`app/src/main/res/drawable-nodpi/`, com nomes minúsculos e separados por `_`.

## Grupos musculares

- `muscle_chest.webp`
- `muscle_back.webp`
- `muscle_shoulders.webp`
- `muscle_biceps.webp`
- `muscle_triceps.webp`
- `muscle_quadriceps.webp`
- `muscle_hamstrings.webp`
- `muscle_glutes.webp`
- `muscle_calves.webp`
- `muscle_core.webp`

## Exercícios já mapeados

- `exercise_incline_dumbbell_press.webp`
- `exercise_bench_press_barbell.webp`
- `exercise_barbell_squat.webp`
- `exercise_lat_pulldown.webp`

O app também tenta `exercise_<nome_do_exercicio_normalizado>.webp`. Por exemplo, `Supino
inclinado com halteres` pode usar `exercise_supino_inclinado_com_halteres.webp`.

Use uma imagem por arquivo, preferencialmente WebP/PNG transparente, sem texto, bordas ou fundo
quadriculado. Os painéis de referência enviados são guias visuais e não devem ser importados como
um único asset: cada músculo ou exercício precisa ser exportado separadamente.
