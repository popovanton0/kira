package com.popovanton0.kira.annotations

@OptIn(ExperimentalKiraApi::class)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
public annotation class KiraRoot(
    val generateRegistry: Boolean = true,
    val typeRenderers: Array<KiraTypeRenderer> = [],
)
