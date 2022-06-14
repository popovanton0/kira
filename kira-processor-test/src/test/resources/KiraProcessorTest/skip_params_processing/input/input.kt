package skip_params_processing

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira(useDefaultValueForParams = ["param2", "param3"])
fun ExampleFunction(
    param1: String,
    param2: Boolean = true,
    param3: Int = 12,
    param4: Short,
) = Unit
