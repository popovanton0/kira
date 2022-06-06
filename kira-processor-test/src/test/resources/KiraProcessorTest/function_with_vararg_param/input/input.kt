package function_with_vararg_param

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira
fun ExampleFunction(vararg param1: String?) = Unit