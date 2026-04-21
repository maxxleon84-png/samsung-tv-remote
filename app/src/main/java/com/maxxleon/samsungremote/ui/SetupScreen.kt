package com.maxxleon.samsungremote.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maxxleon.samsungremote.data.IpValidator
import com.maxxleon.samsungremote.ui.theme.SamsungRemoteTheme

@Composable
fun SetupScreen(
    initialIp: String = "",
    state: ConnectionState,
    onConnectClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    var ip by remember { mutableStateOf(initialIp) }
    val isValid = IpValidator.isValid(ip)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Text("Подключим твой Samsung TV", style = MaterialTheme.typography.titleLarge)
        Text(
            "На ТВ → Меню → Сеть → Состояние сети → IP-адрес",
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = ip,
            onValueChange = { ip = it.trim() },
            label = { Text("IP-адрес телевизора") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { onConnectClick(ip) },
            enabled = isValid && state !is ConnectionState.Connecting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Подключить")
        }
        when (state) {
            ConnectionState.Connecting -> {
                CircularProgressIndicator()
                Text("Подтверди подключение на телевизоре")
            }
            is ConnectionState.Error -> {
                Text("Ошибка: ${state.reason}")
                OutlinedButton(onClick = onRetry) { Text("Повторить") }
            }
            else -> Unit
        }
    }
}

@Preview @Composable
private fun SetupScreenPreview() {
    SamsungRemoteTheme {
        SetupScreen(state = ConnectionState.Idle, onConnectClick = {}, onRetry = {})
    }
}
