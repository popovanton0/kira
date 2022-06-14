package com.popovanton0.kira.annotations

/**
 * @param externalFunctionsToProcess functions from external libraries, which cannot be annotated
 * with [Kira]. Here, [Kira.name] MUST be __qualified__ name of the function (__with__ package name)
 */
@OptIn(ExperimentalKiraApi::class)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
public annotation class KiraRoot(
    val generateRegistry: Boolean = true,
    val externalFunctionsToProcess: Array<Kira> = [],
    val typeRenderers: Array<KiraTypeRenderer> = [],
)
