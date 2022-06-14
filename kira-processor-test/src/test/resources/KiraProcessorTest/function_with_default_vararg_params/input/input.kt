package function_with_default_vararg_params

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira(useDefaultValueForParams = ["param2"])
fun ExampleFunction(
    param1: Boolean,
    vararg param2: String,
) = Unit
