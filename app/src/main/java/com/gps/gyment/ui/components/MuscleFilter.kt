package com.gps.gyment.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gps.gyment.data.enums.Muscle
import com.gps.gyment.ui.theme.GymentTheme

@Composable
fun MuscleFilter(selectedMuscle: Muscle?, onMuscleSelected: (Muscle?) -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Muscle.entries.forEach { muscle ->
            MuscleChip(label = muscle.displayName, selected = muscle == selectedMuscle) {
                onMuscleSelected(if (muscle == selectedMuscle) null else muscle)
            }
        }
    }
}


@Composable
fun MuscleChip(
    label: String,
    selected: Boolean = false,
    onChipSelected: () -> Unit
) {
    FilterChip(
        onClick = { onChipSelected() },
        label = { Text(text = label.uppercase()) },
        selected = selected,
        modifier = Modifier.padding(end = 12.dp)
    )
}
