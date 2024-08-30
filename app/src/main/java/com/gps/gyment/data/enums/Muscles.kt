package com.gps.gyment.data.enums

enum class Muscle(val displayName: String) {
    CHEST("Peito"),
    BACK("Costas"),
    BICEPS("Bíceps"),
    TRICEPS("Tríceps"),
    LEGS("Pernas"),
    SHOULDERS("Ombros"),
    ABS("Abdômen"),
    GLUTES("Glúteos");
}

fun getMuscleByName(displayName: String): Muscle? {
    return Muscle.entries.find { it.name == displayName }
}