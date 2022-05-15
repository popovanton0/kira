package com.popovanton0.kira.annotations

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class Kira(
    val customization: Customization = Customization(enabled = false, supplierImpls = false),
    val targetModule: String = "",
)

public annotation class Customization(val enabled: Boolean, val supplierImpls: Boolean)

