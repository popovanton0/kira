package com.popovanton0.kira.annotations

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
public annotation class KiraRoot(val generateRegistry: Boolean = false)
