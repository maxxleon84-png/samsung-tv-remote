package com.maxxleon.samsungremote.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    currentIp: String?,
    onChangeIp: () -> Unit,
    onReconnect: () -> Unit,
    onBack: () -> Unit
) {
    val uri = LocalUriHandler.current
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TopAppBar(
            title = { Text("Настройки") },
            navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
        )
        Text("IP телевизора: ${currentIp ?: "—"}", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onChangeIp, modifier = Modifier.fillMaxWidth()) { Text("Сменить IP") }
        Button(onClick = onReconnect, modifier = Modifier.fillMaxWidth()) { Text("Переподключиться") }
        Spacer(Modifier.height(24.dp))
        Text("О приложении", style = MaterialTheme.typography.titleMedium)
        Text("Samsung Remote v1.0.0 — мой пульт на Kotlin за вечер.")
        TextButton(onClick = { uri.openUri("https://t.me/my_way_in_wibecoding") }) {
            Text("Канал @my_way_in_wibecoding")
        }
    }
}
