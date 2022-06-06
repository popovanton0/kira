@file:OptIn(ExperimentalKiraApi::class)

package com.popovanton0.kira.annotations

import org.intellij.lang.annotations.Language

@OptIn(ExperimentalKiraApi::class)
@KiraRoot(
    typeRenderers = [
         KiraTypeRenderer(
            fullTypeName = "() -> Unit",
            render = """
            compound(
                dssd = sdsd,
            ) {
                
            }
        """
        )
    ]
)
private object Config

@ExperimentalKiraApi
@Target(allowedTargets = emptyArray())
public annotation class KiraTypeRenderer(
    val fullTypeName: String,
    @Language("kotlin", prefix = "fun a() {", suffix = "}")
    val render: String,
)
