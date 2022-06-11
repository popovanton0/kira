package com.popovanton0.kira.suppliers.base

public data class NamedValue<out T>
@Deprecated("Use infix function withName()", ReplaceWith("value withName displayName"))
constructor(val value: T, val displayName: String) {
    @Suppress("DEPRECATION")
    public constructor(value: T) : this(value, value.toString())

    public companion object {
        public infix fun <T> T.withName(displayName: String): NamedValue<T> =
            @Suppress("DEPRECATION") NamedValue(this, displayName)
    }
}
