package external_functions_collide_with_declared_ones

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(
    generateRegistry = false, externalFunctionsToProcess = [
        Kira(name = "external_functions_collide_with_declared_ones.ExampleFunction2")
    ]
)
object RootModule

@Kira(name = "ExampleFunction2",useDefaultValueForParams = ["param2"])
fun ExampleFunction1(param1: Boolean, param2: String = "def val") = Unit

// in another module
fun ExampleFunction2(param1: Boolean) = Unit
