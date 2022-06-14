package external_functions

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(
    generateRegistry = false, externalFunctionsToProcess = [
        Kira(name = "external_functions.ExampleFunction1", useDefaultValueForParams = ["param2"]),
        Kira(name = "external_functions.ExampleFunction2")
    ]
)
object RootModule

fun ExampleFunction1(param1: Boolean, param2: String = "def val") = Unit
fun ExampleFunction2(param1: Boolean) = Unit
