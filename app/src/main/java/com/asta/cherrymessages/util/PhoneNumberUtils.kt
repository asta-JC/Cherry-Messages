package com.asta.cherrymessages.util

fun normalizePhoneNumber(address: String): String {
    val clean = address.trim()
        .replace(" ", "")
        .replace("-", "")
        .replace("(", "")
        .replace(")", "")
    if (clean.isBlank()) return ""
    return if (!clean.startsWith("+") && clean.all { it.isDigit() }) {
        "+$clean"
    } else {
        clean
    }
}
