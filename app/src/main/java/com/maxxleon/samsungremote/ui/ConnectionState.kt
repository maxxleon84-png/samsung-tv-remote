package com.maxxleon.samsungremote.ui

sealed interface ConnectionState {
    data object Idle : ConnectionState
    data object Connecting : ConnectionState
    data object Connected : ConnectionState
    data class Error(val reason: String) : ConnectionState
}
