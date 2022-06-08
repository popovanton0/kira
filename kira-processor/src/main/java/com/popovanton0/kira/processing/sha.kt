package com.popovanton0.kira.processing

import java.security.MessageDigest

internal fun sha256(str: String): String {
    return MessageDigest.getInstance("SHA-256").digest(str.toByteArray()).toHex()
}

private fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }