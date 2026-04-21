package com.maxxleon.samsungremote.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "samsung_remote")

class IpStorage(private val context: Context) {

    private val TV_IP = stringPreferencesKey("tv_ip")
    private val PAIRED = booleanPreferencesKey("paired")

    val tvIp: Flow<String?> = context.dataStore.data.map { it[TV_IP] }
    val paired: Flow<Boolean> = context.dataStore.data.map { it[PAIRED] ?: false }

    suspend fun setTvIp(ip: String) {
        context.dataStore.edit { it[TV_IP] = ip }
    }

    suspend fun setPaired(paired: Boolean) {
        context.dataStore.edit { it[PAIRED] = paired }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
