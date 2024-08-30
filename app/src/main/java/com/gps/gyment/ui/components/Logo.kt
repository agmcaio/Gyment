package com.gps.gyment.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gps.gyment.R
import com.gps.gyment.ui.theme.GymentTheme

@Composable
fun Logo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logo),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Logo",
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = "Gyment",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Treine sua mente e o seu corpo",
            style = MaterialTheme.typography.titleSmall
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LogoPreview() {
    GymentTheme {
        Logo()
    }
}