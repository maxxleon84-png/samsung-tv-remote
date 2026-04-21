package com.maxxleon.samsungremote.net

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class SamsungPacketEncoderTest {

    @Test fun `auth packet total length matches formula`() {
        val bytes = SamsungPacketEncoder.authPacket(
            ip = "192.168.1.10",
            mac = "AA:BB:CC:DD:EE:FF",
            remoteName = "Remote"
        )
        assertEquals(81, bytes.size)
    }

    @Test fun `auth packet starts with 0x00 header`() {
        val bytes = SamsungPacketEncoder.authPacket(
            ip = "192.168.1.10",
            mac = "AA:BB:CC:DD:EE:FF",
            remoteName = "Remote"
        )
        assertEquals(0x00.toByte(), bytes[0])
    }

    @Test fun `auth packet contains app string at expected offset`() {
        val bytes = SamsungPacketEncoder.authPacket(
            ip = "192.168.1.10",
            mac = "AA:BB:CC:DD:EE:FF",
            remoteName = "Remote"
        )
        // после 1 байта заголовка + 2 байта длины
        val appString = String(bytes, 3, 20, Charsets.US_ASCII)
        assertEquals("iphone..iapp.samsung", appString)
    }

    @Test fun `auth packet matches full reference bytes`() {
        val bytes = SamsungPacketEncoder.authPacket(
            ip = "192.168.1.10",
            mac = "AA:BB:CC:DD:EE:FF",
            remoteName = "Remote"
        )
        val expected = byteArrayOf(
            0x00,
            // len16LE(20) + "iphone..iapp.samsung"
            0x14, 0x00,
            0x69, 0x70, 0x68, 0x6F, 0x6E, 0x65, 0x2E, 0x2E,
            0x69, 0x61, 0x70, 0x70, 0x2E, 0x73, 0x61, 0x6D,
            0x73, 0x75, 0x6E, 0x67,
            // len16LE(56) payload
            0x38, 0x00,
            0x64, 0x00,
            // len16LE(16) + b64("192.168.1.10") = "MTkyLjE2OC4xLjEw"
            0x10, 0x00,
            0x4D, 0x54, 0x6B, 0x79, 0x4C, 0x6A, 0x45, 0x32,
            0x4F, 0x43, 0x34, 0x78, 0x4C, 0x6A, 0x45, 0x77,
            // len16LE(24) + b64("AA:BB:CC:DD:EE:FF") = "QUE6QkI6Q0M6REQ6RUU6RkY="
            0x18, 0x00,
            0x51, 0x55, 0x45, 0x36, 0x51, 0x6B, 0x49, 0x36,
            0x51, 0x30, 0x30, 0x36, 0x52, 0x45, 0x51, 0x36,
            0x52, 0x55, 0x55, 0x36, 0x52, 0x6B, 0x59, 0x3D,
            // len16LE(8) + b64("Remote") = "UmVtb3Rl"
            0x08, 0x00,
            0x55, 0x6D, 0x56, 0x74, 0x62, 0x33, 0x52, 0x6C
        )
        assertArrayEquals(expected, bytes)
    }
}
