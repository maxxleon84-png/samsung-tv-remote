package com.maxxleon.samsungremote.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IpValidatorTest {

    @Test fun `accepts typical home subnets`() {
        assertTrue(IpValidator.isValid("192.168.1.10"))
        assertTrue(IpValidator.isValid("10.0.0.1"))
        assertTrue(IpValidator.isValid("172.16.5.20"))
    }

    @Test fun `accepts boundary octets`() {
        assertTrue(IpValidator.isValid("0.0.0.0"))
        assertTrue(IpValidator.isValid("255.255.255.255"))
    }

    @Test fun `rejects out of range octet`() {
        assertFalse(IpValidator.isValid("192.168.1.256"))
        assertFalse(IpValidator.isValid("999.1.1.1"))
    }

    @Test fun `rejects wrong segment count`() {
        assertFalse(IpValidator.isValid("192.168.1"))
        assertFalse(IpValidator.isValid("192.168.1.1.1"))
    }

    @Test fun `rejects non-numeric`() {
        assertFalse(IpValidator.isValid("abc.def.ghi.jkl"))
        assertFalse(IpValidator.isValid(""))
        assertFalse(IpValidator.isValid("   "))
    }

    @Test fun `rejects leading zeros`() {
        // "01" is ambiguous; reject for safety
        assertFalse(IpValidator.isValid("192.168.001.1"))
    }
}
