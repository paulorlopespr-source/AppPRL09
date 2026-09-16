package com.example.data.local

import androidx.room.migration.Migration

/**
 * Registro central de migrations do Room.
 *
 * Regra do projeto: nunca usar fallbackToDestructiveMigration. Toda alteração de schema
 * deve incrementar a versão do AppDatabase e adicionar aqui a Migration correspondente,
 * acompanhada de teste de preservação do histórico.
 */
object DatabaseMigrations {
    /**
     * A versão atual do banco é 7 e não há mudança de schema nesta fase.
     * A próxima mudança deve declarar, por exemplo, MIGRATION_7_8 e adicioná-la em ALL.
     */
    val ALL: Array<Migration> = emptyArray()
}
