package function_overloads_with_specified_kira_name

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira
fun ExampleFunction() = Unit

@Kira(name = "ExampleFunctionWithParams")
fun ExampleFunction(param1: String) = Unit
