package com.maxxleon.samsungremote.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxxleon.samsungremote.net.KeyCode
import com.maxxleon.samsungremote.ui.theme.*
import com.maxxleon.samsungremote.util.tick

@Composable
fun RemoteScreen(
    state: ConnectionState,
    onPress: (KeyCode) -> Unit,
    onSettingsClick: () -> Unit
) {
    val ctx = LocalContext.current
    val press: (KeyCode) -> Unit = { k -> tick(ctx); onPress(k) }

    Column(
        modifier = Modifier.fillMaxSize().background(BrandBg).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopBar(state, onSettingsClick)
        Spacer(Modifier.height(8.dp))
        CircleButton(icon = Icons.Default.PowerSettingsNew, accent = true) { press(KeyCode.POWER) }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            PillButton("Source") { press(KeyCode.SOURCE) }
            PillButton("Mute", icon = Icons.Default.VolumeOff) { press(KeyCode.MUTE) }
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            RockerColumn(
                topLabel = "Vol +", bottomLabel = "Vol −",
                onTop = { press(KeyCode.VOLUME_UP) }, onBottom = { press(KeyCode.VOLUME_DOWN) }
            )
            RockerColumn(
                topLabel = "Ch +", bottomLabel = "Ch −",
                onTop = { press(KeyCode.CHANNEL_UP) }, onBottom = { press(KeyCode.CHANNEL_DOWN) }
            )
        }
        DPad(
            onUp = { press(KeyCode.UP) },
            onDown = { press(KeyCode.DOWN) },
            onLeft = { press(KeyCode.LEFT) },
            onRight = { press(KeyCode.RIGHT) },
            onOk = { press(KeyCode.OK) }
        )
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            PillButton("Back") { press(KeyCode.BACK) }
            PillButton("Home") { press(KeyCode.HOME) }
            PillButton("Exit") { press(KeyCode.EXIT) }
        }
        NumberGrid(onDigit = { d -> press(digitToKey(d)) })
    }
}

@Composable
private fun TopBar(state: ConnectionState, onSettings: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        val (dotColor, label) = when (state) {
            ConnectionState.Connected -> StatusOk to "подключён"
            ConnectionState.Connecting -> StatusWarn to "подключаемся"
            is ConnectionState.Error -> StatusError to "ошибка"
            ConnectionState.Idle -> BrandTextMuted to "не подключён"
        }
        Box(Modifier.size(10.dp).clip(CircleShape).background(dotColor))
        Spacer(Modifier.width(8.dp))
        Text(label, color = BrandText)
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onSettings) {
            Icon(Icons.Default.Settings, contentDescription = "Настройки", tint = BrandText)
        }
    }
}

@Composable
private fun CircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Boolean = false,
    onClick: () -> Unit
) {
    val border = if (accent) BrandAccent else BrandBorder
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(BrandSurface)
            .border(2.dp, border, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = null, tint = BrandText)
        }
    }
}

@Composable
private fun PillButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = BrandSurface, contentColor = BrandText)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(4.dp))
        }
        Text(text)
    }
}

@Composable
private fun RockerColumn(topLabel: String, bottomLabel: String, onTop: () -> Unit, onBottom: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PillButton(topLabel, onClick = onTop)
        PillButton(bottomLabel, onClick = onBottom)
    }
}

@Composable
private fun DPad(
    onUp: () -> Unit, onDown: () -> Unit, onLeft: () -> Unit,
    onRight: () -> Unit, onOk: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PillButton("▲", onClick = onUp)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PillButton("◀", onClick = onLeft)
            CircleButton(icon = Icons.Default.Check, accent = true, onClick = onOk)
            PillButton("▶", onClick = onRight)
        }
        PillButton("▼", onClick = onDown)
    }
}

@Composable
private fun NumberGrid(onDigit: (Int) -> Unit) {
    val rows = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { digit ->
                    PillButton(digit.toString(), onClick = { onDigit(digit) })
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            PillButton("0", onClick = { onDigit(0) })
        }
    }
}

private fun digitToKey(d: Int): KeyCode = when (d) {
    0 -> KeyCode.NUM_0; 1 -> KeyCode.NUM_1; 2 -> KeyCode.NUM_2
    3 -> KeyCode.NUM_3; 4 -> KeyCode.NUM_4; 5 -> KeyCode.NUM_5
    6 -> KeyCode.NUM_6; 7 -> KeyCode.NUM_7; 8 -> KeyCode.NUM_8
    9 -> KeyCode.NUM_9
    else -> error("invalid digit $d")
}

@Preview @Composable
private fun RemoteScreenPreview() {
    SamsungRemoteTheme {
        RemoteScreen(state = ConnectionState.Connected, onPress = {}, onSettingsClick = {})
    }
}
