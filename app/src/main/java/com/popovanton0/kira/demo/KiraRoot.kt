package com.popovanton0.kira.demo

import com.popovanton0.kira.annotations.ExperimentalKiraApi
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@OptIn(ExperimentalKiraApi::class)
@KiraRoot(
    externalFunctionsToProcess = [Kira(
        name = "androidx.compose.material.Checkbox",
        useDefaultValueForParams = ["modifier", "interactionSource", "colors", ]
    )]
)
object KiraRoot
