package com.maxxleon.samsungremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.maxxleon.samsungremote.data.IpStorage
import com.maxxleon.samsungremote.net.SamsungLegacyClient
import com.maxxleon.samsungremote.ui.*
import com.maxxleon.samsungremote.ui.theme.SamsungRemoteTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var ipStorage: IpStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ipStorage = IpStorage(applicationContext)

        val phoneIp = "192.168.1.55"   // v1.1: определять автоматически через ConnectivityManager
        val phoneMac = "FF:FF:FF:FF:FF:FF"  // реальный MAC недоступен на Android 6+, фейковый ОК
        val client = SamsungLegacyClient(
            phoneIp = phoneIp,
            phoneMac = phoneMac,
            remoteName = "Мой пульт"
        )

        val factory = viewModelFactory {
            initializer { RemoteController(client) }
        }

        setContent {
            SamsungRemoteTheme {
                val controller: RemoteController = viewModel(factory = factory)
                val state by controller.state.collectAsState()
                val savedIp by ipStorage.tvIp.collectAsState(initial = null)
                val paired by ipStorage.paired.collectAsState(initial = false)

                var screen by remember { mutableStateOf<Screen>(Screen.Loading) }

                LaunchedEffect(savedIp, paired) {
                    val ip = savedIp
                    screen = when {
                        ip == null || !paired -> Screen.Setup
                        else -> {
                            controller.connect(ip)
                            Screen.Remote
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val s = screen) {
                        Screen.Loading -> Unit
                        Screen.Setup -> SetupScreen(
                            initialIp = savedIp ?: "",
                            state = state,
                            onConnectClick = { ip ->
                                controller.connect(ip)
                                // при успехе сохраняем и идём в Remote
                                this@MainActivity.lifecycleScope.launch {
                                    controller.state.collect { st ->
                                        if (st is ConnectionState.Connected) {
                                            ipStorage.setTvIp(ip)
                                            ipStorage.setPaired(true)
                                            screen = Screen.Remote
                                            return@collect
                                        }
                                    }
                                }
                            },
                            onRetry = {
                                savedIp?.let { controller.connect(it) }
                            }
                        )
                        Screen.Remote -> RemoteScreen(
                            state = state,
                            onPress = controller::sendKey,
                            onSettingsClick = { screen = Screen.Settings }
                        )
                        Screen.Settings -> SettingsScreen(
                            currentIp = savedIp,
                            onChangeIp = {
                                this@MainActivity.lifecycleScope.launch {
                                    ipStorage.clear()
                                    controller.disconnect()
                                    screen = Screen.Setup
                                }
                            },
                            onReconnect = { savedIp?.let { controller.connect(it) } },
                            onBack = { screen = Screen.Remote }
                        )
                    }
                }
            }
        }
    }
}

private sealed interface Screen {
    data object Loading : Screen
    data object Setup : Screen
    data object Remote : Screen
    data object Settings : Screen
}
