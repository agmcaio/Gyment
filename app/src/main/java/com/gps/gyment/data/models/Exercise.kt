package com.gps.gyment.data.models

data class Exercise(
    var id: String = "",                  // Identificador único para o exercício
    val name: String = "",            // Nome do exercício
    val repetitions: String = "",     // Número de repetições como String
    val sets: String = "",            // Número de séries como String
    val muscleGroup: String = "",     // Grupo muscular relacionado ao exercício
    val createdAt: Long = 0L,         // Timestamp de criação
    val done: Boolean = false,         // Indica se o exercício foi concluído
    val doneAt: Long = 0L,         // Timestamp de conclusão
)