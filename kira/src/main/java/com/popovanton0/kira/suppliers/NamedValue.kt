package com.popovanton0.kira.suppliers

public data class NamedValue<out T>
@Deprecated("Use infix function withName()", ReplaceWith("value withName displayName"))
constructor(val value: T, val displayName: String) {
    @Suppress("DEPRECATION")
    public constructor(value: T) : this(value, value.toString())
}

@Suppress("DEPRECATION")
public infix fun <T> T.withName(displayName: String): NamedValue<T> = NamedValue(this, displayName)