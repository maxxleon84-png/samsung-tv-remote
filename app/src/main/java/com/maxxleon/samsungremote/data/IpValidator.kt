package com.maxxleon.samsungremote.data

object IpValidator {
    fun isValid(input: String): Boolean {
        val parts = input.split(".")
        if (parts.size != 4) return false
        return parts.all { part ->
            if (part.isEmpty() || part.length > 3) return@all false
            if (part.length > 1 && part[0] == '0') return@all false
            val n = part.toIntOrNull() ?: return@all false
            n in 0..255
        }
    }
}
