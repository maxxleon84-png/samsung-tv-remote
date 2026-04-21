package com.maxxleon.samsungremote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxxleon.samsungremote.net.KeyCode
import com.maxxleon.samsungremote.net.SamsungLegacyClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RemoteController(
    private val client: SamsungLegacyClient
) : ViewModel() {

    private val _state = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val state: StateFlow<ConnectionState> = _state.asStateFlow()

    fun connect(tvIp: String) {
        viewModelScope.launch {
            _state.value = ConnectionState.Connecting
            try {
                client.connect(tvIp)
                _state.value = ConnectionState.Connected
            } catch (t: Throwable) {
                _state.value = ConnectionState.Error(t.message ?: "неизвестная ошибка")
            }
        }
    }

    fun sendKey(key: KeyCode) {
        if (_state.value !is ConnectionState.Connected) return
        viewModelScope.launch {
            try {
                client.sendKey(key)
            } catch (t: Throwable) {
                _state.value = ConnectionState.Error(t.message ?: "разрыв соединения")
            }
        }
    }

    fun disconnect() {
        client.close()
        _state.value = ConnectionState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}
