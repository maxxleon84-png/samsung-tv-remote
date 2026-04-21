package com.maxxleon.samsungremote.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

interface Transport {
    fun connect(host: String, port: Int, timeoutMs: Int)
    fun write(bytes: ByteArray)
    fun read(maxBytes: Int): ByteArray
    fun close()
}

class SocketTransport : Transport {
    private var socket: Socket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    override fun connect(host: String, port: Int, timeoutMs: Int) {
        val s = Socket()
        s.connect(InetSocketAddress(host, port), timeoutMs)
        socket = s
        input = s.getInputStream()
        output = s.getOutputStream()
    }

    override fun write(bytes: ByteArray) {
        output?.write(bytes)
        output?.flush()
    }

    override fun read(maxBytes: Int): ByteArray {
        val buf = ByteArray(maxBytes)
        val n = input?.read(buf) ?: return ByteArray(0)
        return if (n <= 0) ByteArray(0) else buf.copyOf(n)
    }

    override fun close() {
        try { socket?.close() } catch (_: Exception) {}
        socket = null
        input = null
        output = null
    }
}

class SamsungLegacyClient(
    private val transportFactory: () -> Transport = { SocketTransport() },
    private val phoneIp: String,
    private val phoneMac: String,
    private val remoteName: String = "Remote",
    private val connectTimeoutMs: Int = 5_000
) {
    companion object { const val PORT = 55_000 }

    private var transport: Transport? = null

    suspend fun connect(tvIp: String) = withContext(Dispatchers.IO) {
        close()
        val t = transportFactory()
        t.connect(tvIp, PORT, connectTimeoutMs)
        t.write(SamsungPacketEncoder.authPacket(phoneIp, phoneMac, remoteName))
        // мы читаем ответ, но не валидируем его жёстко в v1.0 — ТВ может ответить по-разному,
        // факт успешного write + отсутствие IOException считаем признаком установки пары́.
        t.read(1024)
        transport = t
    }

    suspend fun sendKey(key: KeyCode) = withContext(Dispatchers.IO) {
        val t = transport ?: throw IllegalStateException("Not connected")
        t.write(SamsungPacketEncoder.keyPacket(key))
    }

    fun close() {
        transport?.close()
        transport = null
    }

    fun isConnected(): Boolean = transport != null
}
