package function_with_composable_lambda_params

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@KiraRoot(generateRegistry = false)
object RootModule


@Target(AnnotationTarget.TYPE)
public annotation class Composable

@Kira
fun ExampleFunction(
    param1: @Composable () -> Unit,
    param2: @Composable (Int, String) -> Unit,
    param3: @Composable () -> Boolean,
    param4: @Composable (Int, String) -> Boolean,
) = Unit
