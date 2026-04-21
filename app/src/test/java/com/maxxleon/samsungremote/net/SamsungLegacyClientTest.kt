package com.maxxleon.samsungremote.net

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

private class FakeTransport(
    private val responseBytes: ByteArray = byteArrayOf(0x64, 0x00, 0x00, 0x00, 0x00, 0x00)
) : Transport {
    val written = ByteArrayOutputStream()
    var connected = false
    var closed = false

    override fun connect(host: String, port: Int, timeoutMs: Int) {
        connected = true
    }
    override fun write(bytes: ByteArray) { written.write(bytes) }
    override fun read(maxBytes: Int): ByteArray {
        val take = minOf(maxBytes, responseBytes.size)
        return responseBytes.copyOf(take)
    }
    override fun close() { closed = true }
}

class SamsungLegacyClientTest {

    @Test fun `connect sends auth packet to transport`() = runTest {
        val transport = FakeTransport()
        val client = SamsungLegacyClient(
            transportFactory = { transport },
            phoneIp = "192.168.1.55",
            phoneMac = "AA:BB:CC:DD:EE:FF",
            remoteName = "Remote"
        )
        client.connect("192.168.1.10")
        assertTrue(transport.connected)
        val expectedAuth = SamsungPacketEncoder.authPacket(
            "192.168.1.55", "AA:BB:CC:DD:EE:FF", "Remote"
        )
        assertArrayEqualsByteArrays(expectedAuth, transport.written.toByteArray().copyOf(expectedAuth.size))
    }

    @Test fun `sendKey writes correct key packet`() = runTest {
        val transport = FakeTransport()
        val client = SamsungLegacyClient(
            transportFactory = { transport },
            phoneIp = "192.168.1.55",
            phoneMac = "AA:BB:CC:DD:EE:FF",
            remoteName = "Remote"
        )
        client.connect("192.168.1.10")
        transport.written.reset()
        client.sendKey(KeyCode.MUTE)
        val expected = SamsungPacketEncoder.keyPacket(KeyCode.MUTE)
        assertArrayEqualsByteArrays(expected, transport.written.toByteArray())
    }

    @Test fun `close tears down transport`() = runTest {
        val transport = FakeTransport()
        val client = SamsungLegacyClient(
            transportFactory = { transport },
            phoneIp = "192.168.1.55",
            phoneMac = "AA:BB:CC:DD:EE:FF",
            remoteName = "Remote"
        )
        client.connect("192.168.1.10")
        client.close()
        assertTrue(transport.closed)
    }

    private fun assertArrayEqualsByteArrays(expected: ByteArray, actual: ByteArray) {
        assertEquals(expected.toList(), actual.toList())
    }
}
