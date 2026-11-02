package com.asta.cherrymessages

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.asta.cherrymessages.ui.components.smsLength
import com.asta.cherrymessages.ui.components.MAX_SMS_PARTS
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmsLengthTest {
    @Test fun ordinaryTextCrossesSingleSmsBoundary() {
        assertEquals(0, smsLength("").characters)
        assertEquals(160, smsLength("").remaining)
        assertEquals(1, smsLength("a".repeat(160)).parts)
        assertEquals(0, smsLength("a".repeat(160)).remaining)
        assertEquals(2, smsLength("a".repeat(161)).parts)
    }

    @Test fun unicodeHasSmallerCapacity() {
        assertEquals(1, smsLength("漢".repeat(70)).parts)
        assertEquals(0, smsLength("漢".repeat(70)).remaining)
        assertEquals(2, smsLength("漢".repeat(71)).parts)
    }

    @Test fun extensionCharactersAndEmojiUseExtraEncodingSpace() {
        assertEquals(1, smsLength("^".repeat(80)).parts)
        assertEquals(2, smsLength("^".repeat(81)).parts)
        assertEquals(35, smsLength("😀".repeat(35)).characters)
        assertEquals(0, smsLength("😀".repeat(35)).remaining)
        assertEquals(2, smsLength("😀".repeat(36)).parts)
    }

    @Test fun multipartLimitUsesEncodingRatherThanRawCharacters() {
        assertEquals(MAX_SMS_PARTS, smsLength("a".repeat(1530)).parts)
        assertTrue(smsLength("a".repeat(1531)).parts > MAX_SMS_PARTS)
        assertEquals(MAX_SMS_PARTS, smsLength("漢".repeat(670)).parts)
        assertTrue(smsLength("漢".repeat(671)).parts > MAX_SMS_PARTS)
    }
}
