package com.popovanton0.kira

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Returns a property delegate for a read/write property with a non-`null` value that is initialized
 * not during object construction time but at a later time. Trying to read the property before the
 * initial value has been assigned results in an exception.
 *
 * Subsequent tries to change the value of the property will be ignored, if [exceptionMsg] is
 * null. Otherwise, [IllegalAccessException] will bw thrown with [exceptionMsg].
 */
public fun <T : Any> lateinitVal(
    exceptionMsg: String = "Value of this property cannot be changed."
): ReadWriteProperty<Any?, T> = LateinitVal(exceptionMsg)

private class LateinitVal<T : Any>(val exceptionMsg: String? = null) : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException(
            "Property ${property.name} should be initialized before get."
        )
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (this.value == null) {
            this.value = value
        } else {
            if (exceptionMsg != null) throw IllegalAccessException(exceptionMsg)
        }
    }
}

