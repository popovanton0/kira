package registry_generation

import androidx.compose.runtime.Composable
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot

@Kira
@Composable
fun ExampleFunction1(
    param1: String,
    param2: Boolean,
) = Unit

@Kira
@Composable
fun ExampleFunction2(
    sdf: String,
    asd: Boolean,
) = Unit

// misses
@Kira
@Composable
fun ExampleFunction3(
    param1: String,
    param2: Throwable,
) = Unit

// custom injector
@Kira
@Composable
fun <T> ExampleFunction4(
    param1: String,
    param2: Throwable,
) = Unit

@KiraRoot
object RootModule
