package com.masterdog.app.ui.shared.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class SnackbarType { SUCCESS, ERROR, INFO }

@Composable
fun Snackbar(
    snackbarData: SnackbarData,
    type: SnackbarType = SnackbarType.INFO
) {
    val containerColor = when (type) {
        SnackbarType.SUCCESS -> Color(0xFF2E7D32)
        SnackbarType.ERROR   -> MaterialTheme.colorScheme.error
        SnackbarType.INFO    -> MaterialTheme.colorScheme.inverseSurface
    }

    Snackbar(
        snackbarData = snackbarData,
        containerColor = containerColor,
        contentColor = Color.White,
        actionColor = Color.White.copy(alpha = 0.8f)
    )
}
