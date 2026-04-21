package com.maxxleon.samsungremote.ui

import app.cash.turbine.test
import com.maxxleon.samsungremote.net.KeyCode
import com.maxxleon.samsungremote.net.SamsungLegacyClient
import com.maxxleon.samsungremote.net.Transport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteControllerTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    private class StubTransport(var failOnConnect: Boolean = false) : Transport {
        val written = ByteArrayOutputStream()
        override fun connect(host: String, port: Int, timeoutMs: Int) {
            if (failOnConnect) throw java.net.ConnectException("refused")
        }
        override fun write(bytes: ByteArray) { written.write(bytes) }
        override fun read(maxBytes: Int) = ByteArray(0)
        override fun close() {}
    }

    private fun makeClient(transport: Transport) = SamsungLegacyClient(
        transportFactory = { transport },
        phoneIp = "192.168.1.55",
        phoneMac = "AA:BB:CC:DD:EE:FF",
        remoteName = "Remote",
        ioDispatcher = dispatcher
    )

    @Test fun `connect transitions Idle - Connecting - Connected on success`() = runTest {
        val controller = RemoteController(makeClient(StubTransport()))
        controller.state.test {
            assertEquals(ConnectionState.Idle, awaitItem())
            controller.connect("192.168.1.10")
            assertEquals(ConnectionState.Connecting, awaitItem())
            assertEquals(ConnectionState.Connected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun `connect transitions to Error on socket failure`() = runTest {
        val controller = RemoteController(makeClient(StubTransport(failOnConnect = true)))
        controller.state.test {
            assertEquals(ConnectionState.Idle, awaitItem())
            controller.connect("192.168.1.10")
            assertEquals(ConnectionState.Connecting, awaitItem())
            val errorState = awaitItem()
            assertTrue(errorState is ConnectionState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test fun `sendKey does nothing when not connected`() = runTest {
        val transport = StubTransport()
        val controller = RemoteController(makeClient(transport))
        controller.sendKey(KeyCode.MUTE)
        assertEquals(0, transport.written.size())
    }

    @Test fun `sendKey writes bytes when connected`() = runTest {
        val transport = StubTransport()
        val controller = RemoteController(makeClient(transport))
        controller.connect("192.168.1.10")
        dispatcher.scheduler.advanceUntilIdle()
        val beforeSize = transport.written.size()
        controller.sendKey(KeyCode.MUTE)
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(transport.written.size() > beforeSize)
    }
}
