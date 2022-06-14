package com.popovanton0.kira.demo

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(
    externalFunctionsToProcess = [Kira(
        name = "androidx.compose.material.Checkbox",
        useDefaultValueForParams = ["modifier", "interactionSource", "colors", ]
    )]
)
object KiraRoot
