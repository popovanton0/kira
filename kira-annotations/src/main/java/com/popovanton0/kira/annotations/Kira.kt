package com.popovanton0.kira.annotations

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class Kira(
    val supplierImpls: Boolean = false,
    val targetModule: String = "",
)