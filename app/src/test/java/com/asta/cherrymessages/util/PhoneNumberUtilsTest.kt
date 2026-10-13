package com.asta.cherrymessages.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNumberUtilsTest {

    @Test
    fun normalizePhoneNumber_formatsCorrectly() {
        assertEquals("+998334071777", normalizePhoneNumber("+998334071777"))
        assertEquals("+998334071777", normalizePhoneNumber("998334071777"))
        assertEquals("+998334071777", normalizePhoneNumber("+998 33 407-17-77"))
        assertEquals("+998334071777", normalizePhoneNumber("(998) 334-071777"))
        assertEquals("+15550123456", normalizePhoneNumber("+1 (555) 012-3456"))
        assertEquals("+15550123456", normalizePhoneNumber("15550123456"))
        assertEquals("", normalizePhoneNumber("   "))
    }
}
