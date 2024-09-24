package com.gps.gyment.data.models

import com.google.firebase.Timestamp

data class Exercise(
    var id: String = "",
    val name: String = "",
    val repetitions: String = "",
    val sets: String = "",
    val muscleGroup: String = "",
    val createdAt: String = "",
    val done: Boolean = false,
    val doneAt: String = ""
)