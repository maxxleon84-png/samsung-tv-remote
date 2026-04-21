package com.maxxleon.samsungremote.net

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Base64 as JavaBase64

object SamsungPacketEncoder {

    private const val APP_STRING = "iphone..iapp.samsung"

    fun authPacket(ip: String, mac: String, remoteName: String): ByteArray {
        val payload = buildPayload {
            putByte(0x64); putByte(0x00)
            putB64String(ip)
            putB64String(mac)
            putB64String(remoteName)
        }
        return wrapPacket(payload)
    }

    fun keyPacket(key: KeyCode): ByteArray {
        val payload = buildPayload {
            putByte(0x00); putByte(0x00); putByte(0x00)
            putB64String(key.samsungName)
        }
        return wrapPacket(payload)
    }

    private fun wrapPacket(payload: ByteArray): ByteArray {
        val appBytes = APP_STRING.toByteArray(Charsets.US_ASCII)
        val buf = ByteBuffer.allocate(1 + 2 + appBytes.size + 2 + payload.size)
            .order(ByteOrder.LITTLE_ENDIAN)
        buf.put(0x00.toByte())
        buf.putShort(appBytes.size.toShort())
        buf.put(appBytes)
        buf.putShort(payload.size.toShort())
        buf.put(payload)
        return buf.array()
    }

    private class PayloadBuilder {
        private val out = java.io.ByteArrayOutputStream()
        fun putByte(b: Int) { out.write(b) }
        fun putB64String(s: String) {
            val b64 = JavaBase64.getEncoder().encode(s.toByteArray(Charsets.UTF_8))
            out.write(b64.size and 0xFF)
            out.write((b64.size shr 8) and 0xFF)
            out.write(b64)
        }
        fun build(): ByteArray = out.toByteArray()
    }

    private inline fun buildPayload(block: PayloadBuilder.() -> Unit): ByteArray =
        PayloadBuilder().apply(block).build()
}
