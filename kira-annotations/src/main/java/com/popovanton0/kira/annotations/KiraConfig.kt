package com.popovanton0.kira.annotations

import org.intellij.lang.annotations.Language

@KiraConfig(
    KiraTypeRenderer(
        fullTypeName = "() -> Unit",
        render = """
            compound(
                dssd = sdsd,
            ) {
                
            }
        """
    )
)
public object Config

public annotation class KiraConfig(
    vararg val typeRenderer: KiraTypeRenderer
)

public annotation class KiraTypeRenderer(
    val fullTypeName: String,
    @Language("kotlin", prefix = "fun a() {", suffix = "}")
    val render: String,
)
