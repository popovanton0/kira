package functions_with_scope_param_name

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot
object RootModule

@Kira fun ExampleFunction(scope: String) = Unit
@Kira fun ExampleFunction2(`scope$`: String) = Unit