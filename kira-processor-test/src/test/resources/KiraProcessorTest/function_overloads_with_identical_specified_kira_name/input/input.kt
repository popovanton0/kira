package function_overloads_with_identical_specified_kira_name

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira(name = "ExampleFunction1")
fun ExampleFunction() = Unit

@Kira(name = "ExampleFunction1")
fun ExampleFunction(param1: String) = Unit
