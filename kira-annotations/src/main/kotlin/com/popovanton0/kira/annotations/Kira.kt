package com.popovanton0.kira.annotations

/**
 * @param name needs to be specified if the functions name contains unicode characters that require
 * escaping using backticks OR if there is another function overload with the same name
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
public annotation class Kira(
    val name: String = "",
    //val customization: Customization = Customization(false, false)
)

public annotation class Customization(
    val enabled: Boolean = false,
    val supplierImpls: Boolean = false
)

