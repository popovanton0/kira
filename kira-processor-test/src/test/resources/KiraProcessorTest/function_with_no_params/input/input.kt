package function_with_no_params

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira
fun ExampleFunction() = Unit