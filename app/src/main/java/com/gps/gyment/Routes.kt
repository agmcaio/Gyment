package com.gps.gyment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.vector.ImageVector

enum class Routes(val route: String, val iconData: ImageVector) {
    HOME("home", Icons.Filled.Home),
    HISTORY("historico", Icons.Filled.Refresh),
    PROFILE("profile", Icons.Filled.Person)
}
