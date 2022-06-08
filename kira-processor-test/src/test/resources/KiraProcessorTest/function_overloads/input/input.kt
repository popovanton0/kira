package function_overloads

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule

@Kira fun ExampleFunction() = Unit
@Kira fun ExampleFunction(param1: String) = Unit
